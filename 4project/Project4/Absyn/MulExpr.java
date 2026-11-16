package Absyn;

public class MulExpr extends Expr{
    public Expr first, second;

    public MulExpr(Expr first, Expr second){
        this.first = first;
        this.second = second;
    }
    public void accept(Visitor v) {v.visit(this);}
}
