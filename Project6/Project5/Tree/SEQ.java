package Tree;

public class SEQ extends Stm {
    public Stm left, right;

    public SEQ(Stm l, Stm r){
        left = l;
        right = r;
    }

    public void accept(IntVisitor v, int d){
        v.visit(this, d);
    }
}
