package Tree;

public class TEMP extends Exp {
    public Temp.Temp temp;

    public TEMP(Temp.Temp t){
        temp = t;
    }

    public void accept(IntVisitor v, int d){
        v.visit(this, d);
    }
}
