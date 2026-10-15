
/**
 * COSC 4400 - Project #
 * Explain briefly the functionality of the program.
 * @authors Jacob Stroebel, Val eherman
 * Instructor [your instructor]
 * TA-BOT:MAILTO jacob.stroebel@marquette.edu, valerie.ehrman@marquette.edu
 */
package Semant;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import Absyn.ArrayType;
import Absyn.BooleanType;
import Absyn.ClassDecl;
import Absyn.Formal;
import Absyn.IdentifierType;
import Absyn.IntegerType;
import Absyn.MethodDecl;
import Absyn.Program;
import Absyn.Stmt;
import Absyn.ThreadDecl;
import Absyn.VarDecl;
import Absyn.VoidDecl;
import Absyn.AddExpr;      
import Absyn.AndExpr;       
import Absyn.ArrayExpr;
import Absyn.ArrayLength;  
import Absyn.AssignStmt;    
import Absyn.BlockStmt;
import Absyn.CallExpr;     
import Absyn.DivExpr;       
import Absyn.EqualExpr;
import Absyn.Expr;         
import Absyn.FalseExpr;     
import Absyn.FieldExpr;
import Absyn.GreaterExpr;  
import Absyn.IdentifierExpr; 
import Absyn.IfStmt;
import Absyn.IntegerLiteral; 
import Absyn.LesserExpr;  
import Absyn.MulExpr;
import Absyn.NegExpr;      
import Absyn.NewArrayExpr;  
import Absyn.NewObjectExpr;
import Absyn.NotEqExpr;    
import Absyn.NotExpr;       
import Absyn.NullExpr;
import Absyn.StringLiteral; 
import Absyn.SubExpr;      
import Absyn.ThisExpr;
import Absyn.TrueExpr;     
import Absyn.WhileStmt;     
import Absyn.XinuCallExpr;
import Absyn.XinuCallStmt;


public class Main {
    public static void main(String[] args) {
        boolean classesOnly = false;
        String file = null;
        for (String a : args) {
            if (a.equals("-c")) classesOnly = true;
            else file = a;
        }

        StringBuilder text = new StringBuilder();
        try {
            Reader in = (file == null) ? new InputStreamReader(System.in) : new FileReader(file);
            BufferedReader br = new BufferedReader(in);
            char[] buf = new char[8192];
            int k;
            while ((k = br.read(buf)) > 0) text.append(buf, 0, k);
        } catch (IOException e) {
            System.err.println("cannot read input: " + e.getMessage());
            System.exit(1);
            return;
        }

        Program program;
        if (text.toString().trim().startsWith("Program(")) {
            try {
                program = AstReader.read(text.toString());
            } catch (AstReader.FormatError e) {
                System.err.println("bad AST: " + e.getMessage());
                System.exit(1);
                return;
            }
        } else {
            program = parseSource(text.toString());
        }

        TypeChecker checker = new TypeChecker();
        checker.checkBodies = !classesOnly;  
        checker.visit(program);
        if (classesOnly) {
            System.out.print(checker.dump());
        } else if (checker.errors == 0) {
            PrintVisitor printer = new PrintVisitor(new java.io.PrintWriter(System.out));
            printer.notes = checker.notes;
            printer.visit(program);
            System.out.flush();
        }

        if (checker.errors > 0) System.exit(1);
    }

    private static Program parseSource(String source) {
        try {
            Class<?> pc = Class.forName("Parse.MiniJavaParser");
            Object parser = pc.getConstructor(Reader.class).newInstance(new java.io.StringReader(source));
            return (Program) pc.getMethod("Goal").invoke(parser);
        } catch (java.lang.reflect.InvocationTargetException e) {
            System.err.println(e.getCause());
        } catch (Exception e) {
            System.err.println("cannot run the parser: " + e);
        }
        System.exit(1);
        return null;
    }

    private static final class AstReader {
        static final class FormatError extends RuntimeException {
            FormatError(String msg) { super(msg); }
        }

        private static final class Node {
            final String name;
            final List<Object> kids = new ArrayList<Object>();
            Node(String name) { this.name = name; }
        }

        private final List<String> toks = new ArrayList<String>();
        private int pos = 0;

        private AstReader(String text) {
            int i = 0, n = text.length();
            while (i < n) {
                char c = text.charAt(i);
                if (Character.isWhitespace(c)) { i++; }
                else if (c == '(' || c == ')') { toks.add(String.valueOf(c)); i++; }
                else if (c == '"') {                       
                    int j = i + 1;
                    while (j < n && text.charAt(j) != '"') {
                        if (text.charAt(j) == '\\') j++;
                        j++;
                    }
                    toks.add(text.substring(i, Math.min(j + 1, n)));
                    i = j + 1;
                } else {
                    int j = i;
                    while (j < n && !Character.isWhitespace(text.charAt(j))
                            && text.charAt(j) != '(' && text.charAt(j) != ')') j++;
                    toks.add(text.substring(i, j));
                    i = j;
                }
            }
        }

        private Object item() {
            if (pos >= toks.size()) throw new FormatError("unexpected end of AST");
            String w = toks.get(pos++);
            if (w.equals("(") || w.equals(")"))
                throw new FormatError("unexpected '" + w + "' in AST");
            if (pos < toks.size() && toks.get(pos).equals("(")) {
                pos++;
                Node n = new Node(w);
                while (pos < toks.size() && !toks.get(pos).equals(")")) n.kids.add(item());
                if (pos >= toks.size()) throw new FormatError("missing ')' in " + w);
                pos++; // ')'
                return n;
            }
            return w;
        }

        static Program read(String text) {
            AstReader r = new AstReader(text);
            Object root = r.item();
            if (!(root instanceof Node) || !((Node) root).name.equals("Program"))
                throw new FormatError("AST must start with Program(");
            return program((Node) root);
        }

        private static Program program(Node n) {
            LinkedList<ClassDecl> classes = new LinkedList<ClassDecl>();
            for (Object o : listKids(n.kids.isEmpty() ? null : n.kids.get(0)))
                classes.add(classDecl(node(o, "class")));
            return new Program(classes);
        }

        private static ClassDecl classDecl(Node n) {
            String name = atom(n, 0);
            String parent = atom(n, 1);
            if (parent.equals("null")) parent = null;

            LinkedList<VarDecl> fields = new LinkedList<VarDecl>();
            for (Object o : listKids(n.kids.get(2))) {
                fields.add(varDecl(node(o, "field")));
            }
            LinkedList<MethodDecl> methods = new LinkedList<MethodDecl>();
            for (Object o : listKids(n.kids.get(3))) methods.add(methodDecl(node(o, "method")));

            if (n.name.equals("ThreadDecl")) return new ThreadDecl(name, parent, fields, methods);
            return new ClassDecl(name, parent, fields, methods);
        }

        private static MethodDecl methodDecl(Node n) {
            if (n.name.equals("VoidDecl")) {
                return new VoidDecl(atom(n, 0), formals(n.kids.get(1)), stmts(n.kids.get(2)));
            }
            int i = 0;
            Absyn.Type ret = null;
            Object first = n.kids.get(i++);
            if (!"public_static_void".equals(first)) ret = type(first);
            boolean synced = false;
            if ("synchronized".equals(n.kids.get(i))) { synced = true; i++; }
            String name = atom(n, i++);
            LinkedList<Formal> params = formals(n.kids.get(i++));
            LinkedList<VarDecl> locals = new LinkedList<VarDecl>();
            for (Object o : listKids(n.kids.get(i++))) locals.add(varDecl(node(o, "local")));
            LinkedList<Stmt> body = stmts(n.kids.get(i++));
            Expr rv = (i < n.kids.size()) ? optExpr(n.kids.get(i)) : null;
            return new MethodDecl(ret, synced, name, params, locals, body, rv);
        }

        private static LinkedList<Formal> formals(Object list) {
            LinkedList<Formal> out = new LinkedList<Formal>();
            for (Object o : listKids(list)) {
                Node f = node(o, "formal");
                out.add(new Formal(type(f.kids.get(0)), atom(f, 1)));
            }
            return out;
        }
        private static VarDecl varDecl(Node v) {
            Expr init = (v.kids.size() > 2) ? optExpr(v.kids.get(2)) : null;
            return new VarDecl(type(v.kids.get(0)), atom(v, 1), init);
        }

        private static Expr optExpr(Object o) {
            if ("null".equals(o)) return null;
            return expr(o);
        }
        private static LinkedList<Stmt> stmts(Object list) {
            LinkedList<Stmt> out = new LinkedList<Stmt>();
            for (Object o : listKids(list)) out.add(stmt(o));
            return out;
        }

        private static LinkedList<Expr> exprs(Object list) {
            LinkedList<Expr> out = new LinkedList<Expr>();
            for (Object o : listKids(list)) out.add("null".equals(o) ? null : expr(o));
            return out;
        }
        private static Stmt stmt(Object o) {
            Node n = node(o, "statement");
            if (n.name.equals("BlockStmt")) return new BlockStmt(stmts(n.kids.get(0)));
            if (n.name.equals("AssignStmt"))
                return new AssignStmt(expr(n.kids.get(0)), expr(n.kids.get(1)));
            if (n.name.equals("IfStmt")) {
                Object e = n.kids.get(2);
                return new IfStmt(expr(n.kids.get(0)), stmt(n.kids.get(1)),
                        "null".equals(e) ? null : stmt(e));
            }
            if (n.name.equals("WhileStmt"))
                return new WhileStmt(expr(n.kids.get(0)), stmt(n.kids.get(1)));
            if (n.name.equals("XinuCallStmt"))
                return new XinuCallStmt(atom(n, 0), exprs(n.kids.get(1)));
            throw new FormatError("unknown statement in AST: " + n.name);
        }
        private static Expr expr(Object o) {
        if (o instanceof String) {
            String s = (String) o;
            if (s.equals("TrueExpr")) return new TrueExpr();
            if (s.equals("FalseExpr")) return new FalseExpr();
            if (s.equals("ThisExpr")) return new ThisExpr();
            if (s.equals("NullExpr")) return new NullExpr();
            throw new FormatError("unknown expression in AST: " + s);
        }
        Node n = (Node) o;
        String k = n.name;
        if (k.equals("IdentifierExpr")) return new IdentifierExpr(atom(n, 0));
        if (k.equals("IntegerLiteral")) return new IntegerLiteral(Integer.parseInt(atom(n, 0)));
        if (k.equals("StringLiteral")) {
            StringBuilder sb = new StringBuilder();
            for (Object x : n.kids) {
                if (sb.length() > 0) sb.append(' ');
                sb.append(x instanceof String ? (String) x : "");
            }
            return new StringLiteral(sb.toString());
        }
        if (k.equals("NotExpr")) return new NotExpr(expr(n.kids.get(0)));
        if (k.equals("NegExpr")) return new NegExpr(expr(n.kids.get(0)));
        if (k.equals("ArrayLength")) return new ArrayLength(expr(n.kids.get(0)));
        if (k.equals("ArrayExpr")) return new ArrayExpr(expr(n.kids.get(0)), expr(n.kids.get(1)));
        if (k.equals("FieldExpr")) return new FieldExpr(expr(n.kids.get(0)), atom(n, 1));
        if (k.equals("CallExpr"))
            return new CallExpr(expr(n.kids.get(0)), atom(n, 1), exprs(n.kids.get(2)));
        if (k.equals("XinuCallExpr")) return new XinuCallExpr(atom(n, 0), exprs(n.kids.get(1)));
        if (k.equals("NewObjectExpr"))
                return new NewObjectExpr(type(n.kids.get(0)));
        if (k.equals("NewArrayExpr"))
            return new NewArrayExpr(type(n.kids.get(0)), exprs(n.kids.get(1)));
        Expr a = null, b = null;
        if (k.endsWith("Expr") && n.kids.size() == 2) {
            a = expr(n.kids.get(0));
            b = expr(n.kids.get(1));
        }
        if (k.equals("AddExpr")) return new AddExpr(a, b);
        if (k.equals("SubExpr")) return new SubExpr(a, b);
        if (k.equals("MulExpr")) return new MulExpr(a, b);
        if (k.equals("DivExpr")) return new DivExpr(a, b);
        if (k.equals("LesserExpr")) return new LesserExpr(a, b);
        if (k.equals("GreaterExpr")) return new GreaterExpr(a, b);
        if (k.equals("AndExpr")) return new AndExpr(a, b);
        if (k.equals("EqualExpr")) return new EqualExpr(a, b);
        if (k.equals("NotEqExpr")) return new NotEqExpr(a, b);
        throw new FormatError("unknown expression in AST: " + k);
    }

        


        private static Absyn.Type type(Object o) {
            if (o instanceof String) {
                if (o.equals("IntegerType")) return new IntegerType();
                if (o.equals("BooleanType")) return new BooleanType();
            } else {
                Node n = (Node) o;
                if (n.name.equals("ArrayType")) return new ArrayType(type(n.kids.get(0)));
                if (n.name.equals("IdentifierType")) return new IdentifierType(atom(n, 0));
            }
            throw new FormatError("unknown type in AST: " + (o instanceof Node ? ((Node) o).name : o));
        }

        private static List<Object> listKids(Object o) {
            if (o instanceof Node && ((Node) o).name.equals("AbstractList"))
                return ((Node) o).kids;
            return new ArrayList<Object>();
        }

        private static Node node(Object o, String what) {
            if (o instanceof Node) return (Node) o;
            throw new FormatError("expected a " + what + " node but found '" + o + "'");
        }

        private static String atom(Node n, int i) {
            if (i < n.kids.size() && n.kids.get(i) instanceof String) return (String) n.kids.get(i);
            throw new FormatError(n.name + ": expected a name at position " + i);
        }
    }
}