package Absyn;

public class AndExpr extends Expr{
    public Expr first, second;

    public AndExpr(Expr first, Expr second){
        this.first = first;
        this.second = second;
    }

    public void accept(Visitor v){v.visit(this);}
}
