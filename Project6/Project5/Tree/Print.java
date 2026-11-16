package Tree;

import java.io.PrintWriter;

public class Print implements IntVisitor{
    private PrintWriter out;

    private static final String[] BINOPS = {
        "PLUS", "MINUS", "MUL", "DIV", "AND", "OR",
        "BITAND", "BITOR", "BITXOR", "LSHIFT", "RSHIFT", "ARSHIFT"
    };

    private static final String[] RELOPS = {
        "EQ", "NE", "LT", "GT", "LE", "GE", "ULT", "ULE", "UGT", "UGE"
    };

    public Print(PrintWriter o, Stm s){
        out = o;
        s.accept(this, 0);
        out.println();
        out.flush();
    }
    private void indent(int d){
        for (int i = 0; i < d; i++){
            out.print(" ");
        }
    }

    public void visit(SEQ s, int d){
        indent(d);
        out.println("SEQ(");
        s.left.accept(this, d + 1);
        out.println();
        s.right.accept(this, d+1);
        out.println(")");
    }
    public void visit(LABEL s, int d){
        indent(d);
        out.println("LABEL(" + s.label + ")");
    }
    public void visit(JUMP s, int d){
        indent(d);
        out.println("JUMP(");
        s.exp.accept(this, d + 1);
        out.println(")");
    }
    public void visit(CJUMP s, int d){
        indent(d);
        out.println("CJUMP(" + RELOPS[s.reloop]);
        s.left.accept(this, d + 1);
        out.println();
        s.right.accept(this, d+1);
        out.println();
        indent(d + 1);
        out.print(s.ifTLabel + " " + s.ifFLabel + ")");
    }
    public void visit(MOVE s, int d){
        indent(d);
        out.println("MOVE(");
        s.dest.accept(this, d + 1);
        out.println();
        s.sorc.accept(this, d + 1);
        out.print(")");
    }
    public void visit(EXP s, int d){
        indent(d);
        out.println("EXP(");
        s.exp.accept(this, d + 1);
        out.print(")");
    }


    public void visit(BINOP e, int d){
        indent(d);
        out.println("BINOP(" + BINOPS[e.binop]);
        e.left.accept(this, d + 1);
        out.println();
        e.right.accept(this, d + 1);
        out.print(")");
    }
    public void visit(MEM e, int d){
        indent(d);
        out.println("MEM(");
        e.exp.accept(this, d + 1);
        out.print(")");
    }
    public void visit(TEMP e, int d){
        indent(d);
        out.print("TEMP(" e.temp + ")");
    }
    public void visit(ESEQ e, int d){
        indent(d);
        out.println("ESEQ(");
        e.stm.accept(this, d + 1);
        out.println();
        e.exp.accept(this, d + 1);
        out.print(")");
    }
    public void visit(NAME e, int d){
        indent(d);
        out.print("NAME(" + e.label + ")");
    }
    public void visit(CONST e, int d){
        indent(d);
        out.print("CONST(" + e.value + ")");
    }
    public void visit(CALL e, int d){
        indent(d);
        out.println("CALL(");
        e.function.accept(this, d + 1);
        for(Exp a : e.args){
            out.println();
            a.accept(this, d + 1);
        }
        out.println();
        indent(d);
        out.print(")");
    }

}
