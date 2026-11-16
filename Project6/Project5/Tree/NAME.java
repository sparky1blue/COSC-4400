package Tree;

public class NAME extends Exp{
    public Temp.Label label;

    public NAME(Temp.Label l){
        label = l;
    }

    public void accept(IntVisitor v, int d){
        v.visit(this, d);
    }
}
