package Semant;

import java.io.PrintWriter;

import Types.ARRAY;
import Types.BOOLEAN;
import Types.CLASS;
import Types.FIELD;
import Types.FUNCTION;
import Types.INT;
import Types.NIL;
import Types.OBJECT;
import Types.RECORD;
import Types.STRING;
import Types.VOID;


public class PrintVisitor implements Types.Visitor {
    PrintWriter out;
    public int indentCount = 0;

    public PrintVisitor(PrintWriter out) {
        this.out = out;
    }

    public PrintVisitor() {
        this.out = new PrintWriter(System.out);
    }

    private void indent() {
        out.print('\n');
        for (int i = 0; i < indentCount; i++) out.print(' ');
    }

   
    public void visit(Types.Type t) {
        t.accept(this);
    }


    public void visit(ARRAY a) {
        out.print("ARRAY(");
        indentCount++;
        indent();
        visit(a.element);
        indentCount--;
        out.print(")");
    }

    public void visit(BOOLEAN b) { out.print("BOOLEAN"); }

    public void visit(CLASS c) {
        out.print("CLASS(" + c.name);
        indentCount++;
        indent();
        out.print(c.parent == null ? "null" : c.parent.name);
        indent();
        visit(c.methods);
        indent();
        visit(c.fields);
        indent();
        out.print("OBJECT(" + c.name);
        indentCount++;
        indent();
        visit(c.instance.methods);
        indent();
        visit(c.instance.fields);
        indentCount--;
        out.print(")");
        indentCount--;
        out.print(")");
    }

    public void visit(FIELD f) {
        out.print("FIELD(" + f.index + " " + f.name);
        indentCount++;
        indent();
        visit(f.type);
        indentCount--;
        out.print(")");
    }

    public void visit(FUNCTION f) {
        out.print("FUNCTION(" + f.name);
        indentCount++;
        indent();
        visit(f.self);
        indent();
        visit(f.formals);
        indent();
        visit(f.result);
        indentCount--;
        out.print(")");
    }

    public void visit(INT i) { out.print("INT"); }

    public void visit(NIL n) { out.print("NIL"); }


    public void visit(OBJECT o) { out.print("OBJECT(" + o.myClass.name + ")"); }

    public void visit(RECORD r) {
        if (r.size() == 0) {
            out.print("RECORD()");
            return;
        }
        out.print("RECORD(");
        indentCount++;
        for (FIELD f : r) {
            indent();
            visit(f);
        }
        indentCount--;
        out.print(")");
    }

    public void visit(STRING s) { out.print("STRING"); }

    public void visit(VOID v) { out.print("VOID"); }
}