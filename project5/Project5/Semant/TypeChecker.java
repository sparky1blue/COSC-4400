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
import Absyn.Stmt;
import Types.OBJECT;
import java.util.IdentityHashMap;


public class TypeChecker implements TypeVisitor {
    public static final BOOLEAN BOOLEAN = new BOOLEAN();
    public static final INT INT = new INT();
    public static final NIL NIL = new NIL();
    public static final VOID VOID = new VOID();
    public static final STRING STRING = new STRING();

    public boolean checkBodies = true;
    private final Table vars = new Table();
    private Types.Type returnType = VOID;

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
    private final CLASS threadClass = new CLASS("Thread");
    private final Set<CLASS> threadClasses = new HashSet<CLASS>();
    private final Map<Object, Types.Type> typeMemo = new IdentityHashMap<Object, Types.Type>();
    public final Map<Object, Object> notes = new IdentityHashMap<Object, Object>();

    private CLASS current;
    private List<Entry> mergedFields;
    private List<Entry> mergedMethods;

    public TypeChecker() { }

    private void error(String msg) {
        errors++;
        System.err.println("ERROR " + msg);
        System.err.flush();
    }
    private void error(String msg, Object node) {
        errors++;
        java.io.PrintWriter pw = new java.io.PrintWriter(System.err);
        PrintVisitor printer = new PrintVisitor(pw);   // no notes: plain AST
        printer.indentCount = 10;
        pw.print("ERROR " + msg + ": ");
        ((Visitable) node).accept(printer);
        pw.print('\n');
        pw.flush();
    }

    private CLASS classNamed(String name) {
        return (CLASS) classes.get(Symbol.symbol(name));
    }

    public Types.Type visit(Program ast) {
        classes.put(Symbol.symbol("String"), stringClass);
        classes.put(Symbol.symbol("Thread"), threadClass);
        for (ClassDecl c : ast.classes) {
            if (decls.containsKey(c.name)) {
                error("duplicate class: " + c.name + ": line not available");
                continue;
            }
            decls.put(c.name, c);
            order.add(c.name);
            classes.put(Symbol.symbol(c.name), new CLASS(c.name));
            if (c instanceof ThreadDecl) threadClasses.add(classNamed(c.name));
        }

        for (String name : order) {
            ClassDecl c = decls.get(name);
            if (c.parent == null) continue;
            CLASS parent = classNamed(c.parent);
            if (parent == null) error("cannot resolve parent class: " + c.parent + ": line not available");
            else classNamed(name).parent = parent;
        }

        List<CLASS> cyclic = new ArrayList<CLASS>();
        for (String name : order) {
            CLASS start = classNamed(name);
            CLASS p = start.parent;
            for (int steps = 0; p != null && steps <= order.size() + 2; steps++, p = p.parent) {
                if (p == start) {
                    error("cyclic inheritance involving " + name + ": line not available");
                    cyclic.add(start);
                    break;
                }
            }
        }
        for (CLASS c : cyclic) c.parent = null;

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
        notes.put(ast, d);

        current = savedClass;
        mergedFields = savedFields;
        mergedMethods = savedMethods;
        return d;
    }

    public Types.Type visit(ThreadDecl ast) {
        return visit((ClassDecl) ast);
    }

    public Types.Type visitFields(VarDecl ast) {
        Types.Type t = typeOf(ast.type);
        if (current.fields.get(ast.name) != null) {
            error(ast.name + " is already defined in " + current.name, ast);
            return null;
        }
        current.fields.put(t, ast.name);
        mergedFields.add(new Entry(ast.name, t));
        notes.put(ast, t);
        return t;
    }

    public Types.Type visit(MethodDecl ast) {
        RECORD formals = new RECORD();
        for (Formal f : ast.params) {
            Types.Type ft = visit(f);
            if (formals.get(f.name) != null) {
                error(f.name + " is already defined in " + ast.name, f);
                continue;
            }
            formals.put(ft, f.name);
        }
        FUNCTION fn = new FUNCTION(ast.name, current.instance, formals, typeOf(ast.returnType));
        notes.put(ast, fn);
        if (current.methods.get(ast.name) != null) {
            error(ast.name + " is already defined in " + current.name + ": line not available");
            return fn;
        }
        current.methods.put(fn, ast.name);

        Entry existing = null;
        for (Entry e : mergedMethods) if (e.name.equals(ast.name)) existing = e;
        if (existing != null) {
            if (!sameSignature((FUNCTION) existing.type, fn))
                error("incompatible method override: " + ast.name + " in class " + current.name
                        + ": line not available");
            existing.type = fn;
        } else mergedMethods.add(new Entry(ast.name, fn));
        return fn;
    }

    private boolean sameSignature(FUNCTION a, FUNCTION b) {
        if (a.formals.size() != b.formals.size()) return false;
        java.util.Iterator<FIELD> i = a.formals.iterator();
        java.util.Iterator<FIELD> j = b.formals.iterator();
        while (i.hasNext() && j.hasNext()) {
            Types.Type x = i.next().type, y = j.next().type;
            if (!(assignable(x, y) && assignable(y, x))) return false;
        }
        return assignable(b.result, a.result);
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
            error("cannot resolve class " + ast.id, ast);
            return VOID;
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
        Types.Type known = typeMemo.get(t);
        if (known != null) return known;
        Types.Type r;
        if (t instanceof IntegerType) r = visit((IntegerType) t);
        else if (t instanceof BooleanType) r = visit((BooleanType) t);
        else if (t instanceof ArrayType) r = visit((ArrayType) t);
        else if (t instanceof IdentifierType) r = visit((IdentifierType) t);
        else throw new IllegalArgumentException("unknown type " + t.getClass().getName());
        typeMemo.put(t, r);
        return r;
    }

    public Types.Type visit(AddExpr ast) { return binary("+", ast.first, ast.second, INT, INT, ast); }
    public Types.Type visit(AndExpr ast) { return binary("&&", ast.first, ast.second, BOOLEAN, BOOLEAN, ast); }
    public Types.Type visit(ArrayExpr ast) {
        Types.Type a = exprType(ast.array);
        Types.Type i = exprType(ast.index);
        Types.Type result = VOID;
        if (a instanceof ARRAY) result = ((ARRAY) a).element;
        else error("incompatible types: array required, but " + show(a) + " found", ast);
        if (!assignable(i, INT))
            error("incompatible types: " + show(INT) + " required, but " + show(i) + " found", ast.index);
        return result;
    }
    public Types.Type visit(AssignStmt ast) {
         Types.Type target = exprType(ast.target);
         Types.Type value = exprType(ast.value);
         if(!(assignable(value, target)))
            error("incompatible types: " + show(target) + " required, but " + show(value) + " found", ast);
        return VOID;
    }
    public Types.Type visit(BlockStmt ast) {
        for( Object s : ast.stmts) stmtType(s);
        return VOID;
    }
    //TODO
    public Types.Type visit(CallExpr ast) {
        Types.Type o = exprType(ast.object);
        List<Types.Type> argTypes = new ArrayList<Types.Type>();
        for (Object a : ast.args) argTypes.add(exprType(a));

        if (!(o instanceof OBJECT)) {
            error("target not object, type " + show(o), ast);
            return VOID;
        }
        FIELD m = ((OBJECT) o).methods.get(ast.methString);
        if (m == null || !(m.type instanceof FUNCTION)) {
            error("cannot resolve method " + ast.methString, ast);
            return VOID;
        }
        FUNCTION fn = (FUNCTION) m.type;
        if (fn.formals.size() != argTypes.size()) {
            error("mismatch in number of arguments", ast);
            return fn.result;
        }
        int i = 0;
        java.util.Iterator<Object> it = new ArrayList<Object>(ast.args).iterator();
        for (FIELD formal : fn.formals) {
            Types.Type got = argTypes.get(i++);
            Object arg = it.next();
            if (!assignable(got, formal.type))
                error("incompatible types: " + show(formal.type) + " required, but " + show(got) + " found", arg);
        }
        notes.put(ast, Integer.valueOf(m.index));
        return fn.result;
    }
    public Types.Type visit(DivExpr ast) { return binary("/", ast.left, ast.right, INT, INT, ast); }
    public Types.Type visit(EqualExpr ast) { return equality("==", ast.left, ast.right, ast); }
    public Types.Type visit(FalseExpr ast) {return BOOLEAN; }
    public Types.Type visit(FieldExpr ast) {
        Types.Type o = exprType(ast.object);
        if (o instanceof ARRAY && ast.field.equals("length")) {
            notes.put(ast, Integer.valueOf(-1));
            return INT;
        }
        if (!(o instanceof OBJECT)) {
            error("target not object, type " + show(o), ast);
            return VOID;
        }
        FIELD f = ((OBJECT) o).fields.get(ast.field);
        if (f == null) {
            error("cannot resolve field " + ast.field, ast);
            return VOID;
        }
        notes.put(ast, Integer.valueOf(f.index));
        return f.type;
    }
    public Types.Type visit(GreaterExpr ast) { return binary(">", ast.left, ast.right, INT, BOOLEAN, ast); }
    public Types.Type visit(IdentifierExpr ast) {
        Object t = vars.get(Symbol.symbol(ast.name));
        if(t != null) return (Types.Type) t;
        FIELD f = current.instance.fields.get(ast.name);
        if(f != null) return f.type;
        error("cannot resolve symbol " + ast.name, ast);
        return VOID;
    }
    private void condition(Expr cond, Object stmt) {
        Types.Type t = exprType(cond);
        if (!assignable(t, BOOLEAN))
            error("incompatible types: " + show(BOOLEAN) + " required, but " + show(t) + " found", stmt);
    }
    public Types.Type visit(IfStmt ast) {
        condition(ast.cond, ast);
        stmtType(ast.thenStmt);
        if (ast.elseStmt != null) stmtType(ast.elseStmt);
        return VOID;
    }

    public Types.Type visit(IntegerLiteral ast) {return INT;}
    public Types.Type visit(LesserExpr ast) { return binary("<", ast.first, ast.second, INT, BOOLEAN, ast); }
    public Types.Type visit(MulExpr ast) { return binary("*", ast.first, ast.second, INT, INT, ast); }
    public Types.Type visit(NegExpr ast) {
        Types.Type t = exprType(ast.expr);
        if (!assignable(t, INT)) error("operator - cannot be applied to " + show(t), ast);
        return INT;
    }
    public Types.Type visit(NewArrayExpr ast) {
        Types.Type t = typeOf(ast.baseType);
        for (Object s : ast.sizes) {
            if (s != null) expect(INT, (Expr) s);
            t = new ARRAY(t);
        }
        return t;
    }
    public Types.Type visit(NewObjectExpr ast) {
        IdentifierType cn = (IdentifierType) ast.claName;
        CLASS c = classNamed(cn.id);
        if (c == null) {
            error("cannot resolve class " + cn.id, cn);
            return VOID;
        }
        return c.instance;
    }
    public Types.Type visit(NotEqExpr ast) { return equality("!=", ast.left, ast.right, ast); }
    public Types.Type visit(NotExpr ast) {
        Types.Type t = exprType(ast.expr);
        if (!assignable(t, BOOLEAN)) error("operator ! cannot be applied to " + show(t), ast);
        return BOOLEAN;
    }
    public Types.Type visit(NullExpr ast) { return NIL; }
    public Types.Type visit(StringLiteral ast) {return STRING; }
    public Types.Type visit(SubExpr ast) { return binary("-", ast.first, ast.second, INT, INT, ast); }
    public Types.Type visit(ThisExpr ast) { return current.instance; }
    public Types.Type visit(TrueExpr ast) { return BOOLEAN; }
    public Types.Type visit(WhileStmt ast) {
        condition(ast.cond, ast);
        stmtType(ast.body);
        return VOID;
    }
    public Types.Type visit(XinuCallExpr ast) {
        //TODO
        return xinu(ast.method, ast.args, ast);

    }
    
    public Types.Type visit(XinuCallStmt ast) {
        //TODO
        xinu(ast.method, ast.args, ast);
        return VOID;
    }
    private Types.Type equality(String op, Expr a, Expr b, Object node) {
        Types.Type l = exprType(a);
        Types.Type r = exprType(b);
        if (!assignable(l, r) && !assignable(r, l))
            error("operator " + op + " cannot be applied to " + show(l) + ", " + show(r), node);
        return BOOLEAN;
    }
    private Types.Type arrayLength(ArrayLength ast) {
        Types.Type a = exprType(ast.array);
        if (!(a instanceof ARRAY)) error("incompatible types: array required, but " + show(a) + " found", ast);
        return INT;
    }
    
    private void checkClassBodies(ClassDecl ast){
        current = classNamed(ast.name);
        for(MethodDecl m : ast.methods) checkMethod(m);
        current = null;
    }
    private void checkMethod(MethodDecl m) {
        vars.beginScope();
        returnType = typeOf(m.returnType);
        Set<String> formalNames = new HashSet<String>();
        for (Formal f : m.params) {
            if (formalNames.add(f.name)) vars.put(Symbol.symbol(f.name), typeOf(f.type));
        }
        Set<String> declared = new HashSet<String>();    
        List<VarDecl> locals = new ArrayList<VarDecl>();
        if (m.locals != null) locals.addAll(m.locals);
        for (VarDecl v : locals) {
            Types.Type t = typeOf(v.type);
            if (!declared.add(v.name)) {
                error(v.name + " is already defined in " + m.name, v);
                continue;
            }
            vars.put(Symbol.symbol(v.name), t);
            notes.put(v, t);
        }
        for (VarDecl v : locals) {
            if (v.init == null) continue;
            Types.Type t = typeOf(v.type);
            Types.Type it = exprType(v.init);
            if (!assignable(it, t))
                error("incompatible types: " + show(t) + " required, but " + show(it) + " found", v.init);
        }
        for (Object s : m.stmts) stmtType(s);
        if (m.returnType != null && m.returnVal != null) {
            Types.Type rt = exprType(m.returnVal);
            if (!assignable(rt, returnType))
                error("incompatible types: " + show(returnType) + " required, but " + show(rt) + " found", m.returnVal);
        }
        vars.endScope();
    }
    private static String show(Types.Type t) {
        if (t instanceof INT) return "int";
        if (t instanceof BOOLEAN) return "boolean";
        if (t instanceof VOID) return "void";
        if (t instanceof NIL) return "NIL";
        if (t instanceof STRING) return "STRING:String";
        if (t instanceof OBJECT) return "OBJECT:" + ((OBJECT) t).myClass.name;
        if (t instanceof ARRAY) return show(((ARRAY) t).element) + "[]";
        return String.valueOf(t);
    }
    private boolean assignable(Types.Type from, Types.Type to) {
        if (from == null || to == null) return false;
        if (from instanceof INT) return to instanceof INT;
        if (from instanceof BOOLEAN) return to instanceof BOOLEAN;
        if (from instanceof VOID) return to instanceof VOID;
        if (from instanceof STRING)
            return to instanceof STRING 
                || (to instanceof OBJECT && ((OBJECT) to).myClass == stringClass);
        if (from instanceof NIL) return to instanceof NIL || to instanceof OBJECT || to instanceof ARRAY;
        if (from instanceof OBJECT) {
            if (!(to instanceof OBJECT)) return false;
            CLASS target = ((OBJECT) to).myClass;
            for (CLASS c = ((OBJECT) from).myClass; c != null; c = c.parent)
                if (c == target) return true;
            return false;
        }
        if (from instanceof ARRAY) {
            if (!(to instanceof ARRAY)) return false;
            Types.Type a = ((ARRAY) from).element, b = ((ARRAY) to).element;
            return assignable(a, b) && assignable(b, a);
        }
        return false;
    }
    private boolean isThread(Types.Type t) {
        if (!(t instanceof OBJECT)) return false;
        for (CLASS c = ((OBJECT) t).myClass; c != null; c = c.parent)
            if (c == threadClass || threadClasses.contains(c)) return true;
        return false;
    }



    private void expect(Types.Type required, Expr e) {
        //TODO
        Types.Type got = exprType(e);
        if (!assignable(got, required))
            error("incompatible types: " + show(required) + " required, but " + show(got) + " found", e);
    }

    private Types.Type xinu(String method, java.util.AbstractList<?> args, Object node) {
        int wanted = (method.equals("print") || method.equals("printint")
                || method.equals("threadCreate")) ? 1 : 0;
        if (args.size() != wanted) {
            for (Object a : args) exprType(a);
            error("mismatch in number of arguments", node);
            return method.equals("readint") ? INT : VOID;
        }
        if (method.equals("print")) {
            for (Object a : args) expect(stringClass.instance, (Expr) a);
        } else if (method.equals("printint")) {
            for (Object a : args) expect(INT, (Expr) a);
        } else if (method.equals("threadCreate")) {
            for (Object a : args) {
                Types.Type t = exprType(a);
                if (!isThread(t))
                    error("incompatible types: " + show(threadClass.instance) + " required, but " + show(t) + " found", a);
            }
        }
        return method.equals("readint") ? INT : VOID;
    }

    private Types.Type binary(String op, Expr a, Expr b, Types.Type operand, Types.Type result, Object node) {
        Types.Type l = exprType(a);
        Types.Type r = exprType(b);
        if (!assignable(l, operand) || !assignable(r, operand))
            error("operator " + op + " cannot be applied to " + show(l) + ", " + show(r), node);
        return result;
    }

    
    private Types.Type exprType(Object e) {
        if (e instanceof AddExpr) return visit((AddExpr) e);
        if (e instanceof AndExpr) return visit((AndExpr) e);
        if (e instanceof ArrayExpr) return visit((ArrayExpr) e);
        if (e instanceof ArrayLength) return arrayLength((ArrayLength) e);
        if (e instanceof CallExpr) return visit((CallExpr) e);
        if (e instanceof DivExpr ) return visit((DivExpr) e);
        if (e instanceof EqualExpr) return visit((EqualExpr) e);
        if (e instanceof FalseExpr ) return visit((FalseExpr) e);
        if (e instanceof FieldExpr ) return visit((FieldExpr) e);
        if (e instanceof GreaterExpr ) return visit((GreaterExpr) e);
        if (e instanceof IdentifierExpr ) return visit((IdentifierExpr) e);
        if (e instanceof IntegerLiteral ) return visit((IntegerLiteral) e);
        if (e instanceof LesserExpr ) return visit((LesserExpr) e);
        if (e instanceof MulExpr ) return visit((MulExpr) e);
        if (e instanceof NegExpr ) return visit((NegExpr) e);
        if (e instanceof NewArrayExpr ) return visit((NewArrayExpr) e);
        if (e instanceof NewObjectExpr ) return visit((NewObjectExpr) e);
        if (e instanceof NotEqExpr ) return visit((NotEqExpr) e);
        if (e instanceof NotExpr ) return visit((NotExpr) e);
        if (e instanceof NullExpr ) return visit((NullExpr) e);
        if (e instanceof StringLiteral ) return visit((StringLiteral) e);
        if (e instanceof SubExpr ) return visit((SubExpr) e);
        if (e instanceof ThisExpr ) return visit((ThisExpr) e);
        if (e instanceof TrueExpr ) return visit((TrueExpr) e);
        if (e instanceof XinuCallExpr) return visit((XinuCallExpr) e);
        throw new IllegalArgumentException("unknown expression " + e.getClass().getName());
    }
    private Types.Type stmtType(Object s){
        if (s instanceof AssignStmt) return visit((AssignStmt) s);
        if (s instanceof BlockStmt) return visit((BlockStmt) s);
        if (s instanceof IfStmt) return visit((IfStmt) s);
        if (s instanceof WhileStmt) return visit((WhileStmt) s);
        if (s instanceof XinuCallStmt) return visit((XinuCallStmt) s);
        throw new IllegalArgumentException("unknown statement " + s.getClass().getName());
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