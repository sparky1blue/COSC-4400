package Absyn;

public class AddExpr extends Expr{
    
    public Expr first, second;

    public AddExpr(Expr first, Expr second){
        this.first = first;
        this.second = second;
    }
    public void accept(Visitor v) {v.visit(this);}
}
