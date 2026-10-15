package Absyn;
import java.util.LinkedList;
import java.util.concurrent.LinkedBlockingDeque;

public class CallExpr extends Expr{
    
    public Expr object;
    public String methString;
    public LinkedList<Expr> args;

    public CallExpr(Expr object, String methString, LinkedList args){
        this.object = object;
        this.methString = methString;
        this.args = args;
    }

    public void accept(Visitor v){v.visit(this);}
}

