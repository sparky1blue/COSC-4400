package Tree;

public class CONST extends Exp {
    public int value;

    public CONST(int v){
        value = v;
    }

    public void accept(IntVisitor v, int d){
        v.visit(this, d);
    }
}
