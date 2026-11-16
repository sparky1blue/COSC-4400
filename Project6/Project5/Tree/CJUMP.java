package Tree;

public class CJUMP extends Stm{
    public static final int EQ = 0;
    public static final int NE = 1;
    public static final int LT = 2;
    public static final int  GT = 3;
    public static final int LE = 4;
    public static final int GE = 5;
    public static final int ULT = 6;
    public static final int ULE = 7;
    public static final int UGT = 8;
    public static final int UGE = 9;

    public int reloop;
    public Exp left, right;
    public Temp.Label ifTLabel, ifFLabel;

    public CJUMP(int rel, Exp l, Exp r, Temp.Label t, Temp.Label f){
        reloop = rel;
        left = l;
        right = r;
        ifTLabel = t;
        ifFLabel = f;
    }

    public void accept(IntVisitor v, int d){
        v.visit(this, d);
    }

}
