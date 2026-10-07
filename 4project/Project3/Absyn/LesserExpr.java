package Absyn;

public class LesserExpr extends Expr{
    
    public Expr first, second;

    public LesserExpr(Expr first, Expr second){
        this.first = first;
        this.second = second;
    }

    public void accept(Visitor v){v.visit(this);}
    
}
