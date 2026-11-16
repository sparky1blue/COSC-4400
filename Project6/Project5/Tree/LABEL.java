package Tree;

public class LABEL extends Stm{
    public Temp.Label label;

    public LABEL(Temp.Label l){
        label = l;
    }

    public void accept(IntVisitor v, int d){
        v.visit(this, d);
    }
}
