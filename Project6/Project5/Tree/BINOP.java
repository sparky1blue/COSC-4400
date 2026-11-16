package Tree;

public class BINOP extends Exp {
    public static final int PLUS = 0;
    public static final int MINUS = 1;
    public static final int MUL = 2;
    public static final int DIV = 3;
    public static final int AND = 4;
    public static final int OR = 5;
    public static final int BITAND = 6;
    public static final int BITOR = 7;
    public static final int BITXOR = 8;
    public static final int LSHIFT = 9;
    public static final int RSHIFT = 10;
    public static final int ARSHIFT = 11;

    public int binop;
    public Exp left, right;

    public BINOP(int b, Exp l, Exp r){
        binop = b;
        left = l;
        right = r;
    }

    public void accept(IntVisitor v, int d){
        v.visit(this,d);
    }

}
