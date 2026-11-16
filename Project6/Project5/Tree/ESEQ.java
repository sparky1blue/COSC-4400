package Tree;

public class ESEQ extends Exp {
    public Stm stm;
    public Exp exp;

    public ESEQ(Stm s, Exp e){
        stm = s;
        exp = e;
    }

    public void accept(IntVisitor v, int d){
        v.visit(this, d);
    }
}
