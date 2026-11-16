package Tree;

import java.util.LinkedList;

public class JUMP extends Stm{
    public Exp exp;
    public LinkedList<Temp.Label> targets;

    public JUMP(Exp e, LinkedList<Temp.Label> t){
        exp = e;
        targets = t;
    }

    public JUMP(Temp.Label target){
        this(new NAME(target), new LinkedList<Temp.Label>());
        targets.add(target);
    }

    public void accept(IntVisitor v, int d){
        v.visit(this, d);
    }

}
