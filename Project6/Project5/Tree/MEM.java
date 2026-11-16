package Tree;

public class MEM extends Exp {
    public Exp exp;

    public MEM(Exp e){
        exp = e;
    }

    public void accept(IntVisitor v, int d){
        v.visit(this, d);
    }
}
