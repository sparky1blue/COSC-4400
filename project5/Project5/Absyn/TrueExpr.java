package Absyn;

public class TrueExpr extends Expr{
    public boolean value;

    public TrueExpr(){}

    public void accept(Visitor v) {v.visit(this); }

}