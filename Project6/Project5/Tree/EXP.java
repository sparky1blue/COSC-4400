package Tree;

public class EXP extends Stm{
    public Exp exp;

    public EXP(Exp e){
        exp = e;
    }

    public void accept(IntVisitor v, int d){
        v.visit(this, d);
    }
}
