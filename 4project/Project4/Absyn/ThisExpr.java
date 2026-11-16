package Absyn;

public class ThisExpr extends Expr{
    public ThisExpr(){}

    public void accept(Visitor v) {v.visit(this);}
}
