package Tree;

import java.util.List;

public class CALL extends Exp{
    public Exp function;
    public List<Exp> args;

    public CALL(Exp f, List<Exp> a){
        function = f;
        args = a;
    }

    public void accept(IntVisitor v, int d){
        v.visit(this, d);
    }
}
