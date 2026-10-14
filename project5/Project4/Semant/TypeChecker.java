package Semant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import Absyn.AddExpr;
import Absyn.AndExpr;
import Absyn.ArrayExpr;
import Absyn.ArrayType;
import Absyn.AssignStmt;
import Absyn.BlockStmt;
import Absyn.BooleanType;
import Absyn.CallExpr;
import Absyn.ClassDecl;
import Absyn.DivExpr;
import Absyn.EqualExpr;
import Absyn.FalseExpr;
import Absyn.FieldExpr;
import Absyn.Formal;
import Absyn.GreaterExpr;
import Absyn.IdentifierExpr;
import Absyn.IdentifierType;
import Absyn.IfStmt;
import Absyn.IntegerLiteral;
import Absyn.IntegerType;
import Absyn.LesserExpr;
import Absyn.MethodDecl;
import Absyn.MulExpr;
import Absyn.NegExpr;
import Absyn.NewArrayExpr;
import Absyn.NewObjectExpr;
import Absyn.NotEqExpr;
import Absyn.NotExpr;
import Absyn.NullExpr;
import Absyn.Program;
import Absyn.StringLiteral;
import Absyn.SubExpr;
import Absyn.ThisExpr;
import Absyn.ThreadDecl;
import Absyn.TrueExpr;
import Absyn.TypeVisitor;
import Absyn.VarDecl;
import Absyn.Visitable;
import Absyn.VoidDecl;
import Absyn.WhileStmt;
import Absyn.XinuCallExpr;
import Absyn.XinuCallStmt;
import Symbol.Symbol;
import Symbol.Table;
import Types.ARRAY;
import Types.BOOLEAN;
import Types.CLASS;
import Types.FIELD;
import Types.FUNCTION;
import Types.INT;
import Types.NIL;
import Types.RECORD;
import Types.STRING;
import Types.VOID;

import Absyn.ArrayLength;
import Absyn.Expr;
import Types.OBJECT;


public class TypeChecker implements TypeVisitor {
    public static final BOOLEAN BOOLEAN = new BOOLEAN();
    public static final INT INT = new INT();
    public static final NIL NIL = new NIL();
    public static final VOID VOID = new VOID();
    public static final STRING STRING = new STRING();

    public boolean checkBodies = true;
    private final Type vars = new Table();
    private Types.Type returntype = VOID;

    public int errors = 0;

    public final Table classes = new Table();

    private static final class Entry {
        final String name;
        Types.Type type;
        Entry(String name, Types.Type type) { this.name = name; this.type = type; }
    }

    private final List<String> order = new ArrayList<String>();
    private final Map<String, ClassDecl> decls = new HashMap<String, ClassDecl>();
    private final Set<String> built = new HashSet<String>();
    private final CLASS stringClass = new CLASS("String"); 

    private CLASS current;
    private List<Entry> mergedFields;
    private List<Entry> mergedMethods;

    public TypeChecker() { }

    private void error(String msg) {
        errors++;
        System.err.println("ERROR " + msg);
        System.out.flush();
    }
    private void error(String msg, Object code){
        errors++;
        java.io.PrintWriter printWriter = new java.io.PrintWriter(System.out);
        PrintVisitor printer = new PrintVisitor(printWriter);
        printer.indentCount = 10;
        printWriter.print("ERROR " + msg + ": ");
        //TODO
        ((Visitable) code).accept(printer);
        printWriter.print('\n');
        printWriter.flush();

    }

    private CLASS classNamed(String name) {
        return (CLASS) classes.get(Symbol.symbol(name));
    }

    public Types.Type visit(Program ast) {
        if (classNamed("Thread") == null) classes.put(Symbol.symbol("Thread"), new CLASS("Thread"));
        for (ClassDecl c : ast.classes) {
            if (decls.containsKey(c.name)) {
                error("duplicate class " + c.name);
                continue;
            }
            decls.put(c.name, c);
            order.add(c.name);
            classes.put(Symbol.symbol(c.name), new CLASS(c.name));
        }

        for (String name : order) {
            ClassDecl c = decls.get(name);
            if (c.parent == null) continue;
            CLASS parent = classNamed(c.parent);
            if (parent == null) error("unknown parent class " + c.parent + " for " + name);
            else classNamed(name).parent = parent;
        }

    
        for (String name : order) {
            CLASS start = classNamed(name);
            CLASS p = start.parent;
            for (int steps = 0; p != null && steps <= order.size(); steps++, p = p.parent) {
                if (p == start) {
                    error("cyclic inheritance involving " + name);
                    start.parent = null;
                    break;
                }
            }
        }

        for (String name : order) visit(decls.get(name));

        if (checkBodies) {
            for (String name : order) checkClassBodies(decls.get(name));
        }
        return VOID;
    }

    public Types.Type visit(ClassDecl ast) {
        CLASS d = classNamed(ast.name);
        if (!built.add(ast.name)) return d;
        if (d.parent != null && decls.containsKey(d.parent.name)) visit(decls.get(d.parent.name));
        if (d.parent != null) visit(decls.get(d.parent.name));

        CLASS savedClass = current;
        List<Entry> savedFields = mergedFields, savedMethods = mergedMethods;
        current = d;
        mergedFields = new ArrayList<Entry>();
        mergedMethods = new ArrayList<Entry>();

        if (d.parent != null) {
            for (FIELD f : d.parent.instance.fields) mergedFields.add(new Entry(f.name, f.type));
            for (FIELD m : d.parent.instance.methods) mergedMethods.add(new Entry(m.name, m.type));
        }

        for (VarDecl v : ast.fields) visitFields(v);
        for (MethodDecl m : ast.methods) visit(m);

        RECORD allFields = new RECORD();
        for (Entry e : mergedFields) allFields.put(e.type, e.name);
        RECORD allMethods = new RECORD();
        for (Entry e : mergedMethods) allMethods.put(e.type, e.name);
        d.instance.fields = allFields;
        d.instance.methods = allMethods;

        current = savedClass;
        mergedFields = savedFields;
        mergedMethods = savedMethods;
        return d;
    }

    public Types.Type visit(ThreadDecl ast) {
        return visit((ClassDecl) ast);
    }

    public Types.Type visitFields(VarDecl ast) {
        if (current.fields.get(ast.name) != null) {
            error("duplicate field " + ast.name + " in " + current.name);
            return null;
        }
        Types.Type t = typeOf(ast.type);
        current.fields.put(t, ast.name);
        mergedFields.add(new Entry(ast.name, t));
        return t;
    }

    public Types.Type visit(MethodDecl ast) {
        if (current.methods.get(ast.name) != null) {
            error("duplicate method " + ast.name + " in " + current.name);
            return null;
        }
        RECORD formals = new RECORD();
        for (Formal f : ast.params) {
            if (formals.get(f.name) != null) {
                error("duplicate parameter " + f.name + " in " + ast.name);
                continue;
            }
            formals.put(visit(f), f.name);
        }
        FUNCTION fn = new FUNCTION(ast.name, current.instance, formals, typeOf(ast.returnType));
        current.methods.put(fn, ast.name);


        Entry existing = null;
        for (Entry e : mergedMethods) if (e.name.equals(ast.name)) existing = e;
        if (existing != null) existing.type = fn;
        else mergedMethods.add(new Entry(ast.name, fn));
        return fn;
    }

    public Types.Type visit(VoidDecl ast) {
        return visit((MethodDecl) ast);
    }

    public Types.Type visit(VarDecl ast) {
        return typeOf(ast.type);
    }

    public Types.Type visit(Formal ast) {
        return typeOf(ast.type);
    }

    public Types.Type visit(java.util.AbstractList<Visitable> list) {
        if (list == null) return VOID;
        for (Object o : list) {
            if (o instanceof ClassDecl) visit((ClassDecl) o);
            else if (o instanceof MethodDecl) visit((MethodDecl) o);
            else if (o instanceof VarDecl) visit((VarDecl) o);
            else if (o instanceof Formal) visit((Formal) o);
        }
        return VOID;
    }


    public Types.Type visit(ArrayType ast) {
        return new ARRAY(typeOf(ast.base));
    }

    public Types.Type visit(IdentifierType ast) {
        CLASS c = classNamed(ast.id);
        if (c == null) {
            if (ast.id.equals("String")) c = stringClass;
            else {
                error("cannot resolve class " + ast.id, ast);
                c = new CLASS(ast.id);
            }
        }
        return c.instance;
    }

    public Types.Type visit(IntegerType ast) {
        return INT;
    }

    public Types.Type visit(BooleanType ast) {
        return BOOLEAN;
    }

    private Types.Type typeOf(Absyn.Type t) {
        if (t == null) return VOID;
        if (t instanceof IntegerType) return visit((IntegerType) t);
        if (t instanceof BooleanType) return visit((BooleanType) t);
        if (t instanceof ArrayType) return visit((ArrayType) t);
        if (t instanceof IdentifierType) return visit((IdentifierType) t);
        throw new IllegalArgumentException("unknown type " + t.getClass().getName());
    }

    public Types.Type visit(AddExpr ast) { return null; }
    public Types.Type visit(AndExpr ast) { return null; }
    public Types.Type visit(ArrayExpr ast) { return null; }
    public Types.Type visit(AssignStmt ast) { return null; }
    public Types.Type visit(BlockStmt ast) { return null; }
    public Types.Type visit(CallExpr ast) { return null; }
    public Types.Type visit(DivExpr ast) { return null; }
    public Types.Type visit(EqualExpr ast) { return null; }
    public Types.Type visit(FalseExpr ast) { return null; }
    public Types.Type visit(FieldExpr ast) { return null; }
    public Types.Type visit(GreaterExpr ast) { return null; }
    public Types.Type visit(IdentifierExpr ast) { return null; }
    public Types.Type visit(IfStmt ast) { return null; }
    public Types.Type visit(IntegerLiteral ast) { return null; }
    public Types.Type visit(LesserExpr ast) { return null; }
    public Types.Type visit(MulExpr ast) { return null; }
    public Types.Type visit(NegExpr ast) { return null; }
    public Types.Type visit(NewArrayExpr ast) { return null; }
    public Types.Type visit(NewObjectExpr ast) { return null; }
    public Types.Type visit(NotEqExpr ast) { return null; }
    public Types.Type visit(NotExpr ast) { return null; }
    public Types.Type visit(NullExpr ast) { return null; }
    public Types.Type visit(StringLiteral ast) { return null; }
    public Types.Type visit(SubExpr ast) { return null; }
    public Types.Type visit(ThisExpr ast) { return null; }
    public Types.Type visit(TrueExpr ast) { return null; }
    public Types.Type visit(WhileStmt ast) { return null; }
    public Types.Type visit(XinuCallExpr ast) { return null; }
    public Types.Type visit(XinuCallStmt ast) { return null; }

    private void checkBodies(ClassDecl ast){
        current = classNamed(ast.name);
        for(MethodDecl m : ast.methods) checkMethod(m);
        current = null;
    }
    private  void checkMethod(MethodDecl m){
        vars.beginScope();
        //TODO
        returnType = typeOf(m.returnType);
        for (Formal f : m.params) vars.put(Symbol.symbol(f.name), typeOf(f.type));
        for (VarDecl v : m.locals) {
            Types.Type t = typeOf(v.type);
            if(v.init != null){
                Types.Type it = exprType(v.init);
                if(!assignable(it, t))
                    error("incompatible types: " + show(t) + " required, but " + show(it) + " found", v.init);
            }
        }
        for(Object s : m.stmts) stmtType(s);
        if(m.returnType != null && m.returnVal != null){
            Types.Type rt exprType(m.returnVal);
            if(!assignsble(rt, returntype))
                error("incompatible types: " + show(returnType) + " required, but " + show(rt) + " found", m.returnVal);
            
        }
    }








    public String dump() {
        java.io.StringWriter sw = new java.io.StringWriter();
        java.io.PrintWriter pw = new java.io.PrintWriter(sw);
        PrintVisitor printer = new PrintVisitor(pw);
        boolean first = true;
        for (String n : order) {
            if (!first) pw.print("\n\n");
            first = false;
            printer.visit(classNamed(n));
        }
        pw.print('\n');
        pw.flush();
        return sw.toString();
    }
}