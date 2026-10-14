
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
        try {
            program = AstReader.read(text.toString());
        } catch (AstReader.FormatError e) {
            System.err.println("bad AST: " + e.getMessage());
            System.exit(1);
            return;
        }

        TypeChecker checker = new TypeChecker();
        checker.visit(program);
        System.out.print(checker.dump());

        if (checker.errors > 0) System.exit(1);
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
                Node v = node(o, "field");
                fields.add(new VarDecl(type(v.kids.get(0)), atom(v, 1), null));
            }
            LinkedList<MethodDecl> methods = new LinkedList<MethodDecl>();
            for (Object o : listKids(n.kids.get(3))) methods.add(methodDecl(node(o, "method")));

            if (n.name.equals("ThreadDecl")) return new ThreadDecl(name, parent, fields, methods);
            return new ClassDecl(name, parent, fields, methods);
        }

        private static MethodDecl methodDecl(Node n) {
            if (n.name.equals("VoidDecl")) {
                
                return new VoidDecl(atom(n, 0), formals(n.kids.get(1)), new LinkedList<Stmt>());
            }
            int i = 0;
            Absyn.Type ret = null;
            Object first = n.kids.get(i++);
            if (!"public_static_void".equals(first)) ret = type(first);
            boolean synced = false;
            if ("synchronized".equals(n.kids.get(i))) { synced = true; i++; }
            String name = atom(n, i++);
            LinkedList<Formal> params = formals(n.kids.get(i));
            return new MethodDecl(ret, synced, name, params,
                    new LinkedList<VarDecl>(), new LinkedList<Stmt>(), null);
        }

        private static LinkedList<Formal> formals(Object list) {
            LinkedList<Formal> out = new LinkedList<Formal>();
            for (Object o : listKids(list)) {
                Node f = node(o, "formal");
                out.add(new Formal(type(f.kids.get(0)), atom(f, 1)));
            }
            return out;
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