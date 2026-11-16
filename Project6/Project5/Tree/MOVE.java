package Tree;

public class MOVE extends Stm{
    public Exp dest, sorc;
    
    public MOVE(Exp d, Exp s){
        dest = d;
        sorc = s;
    }

    public void accept(IntVisitor v, int d){
        v.visit(this, d);
    }
}
