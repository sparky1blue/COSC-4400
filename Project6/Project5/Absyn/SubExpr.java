package Absyn;

public class SubExpr extends Expr{
    
    public Expr first, second;

    public SubExpr(Expr first, Expr second){
        this.first = first;
        this.second = second;
    }
    public void accept(Visitor v) {v.visit(this);}
    public Translate.Exp accept(TreeVisitor v) { return v.visit(this); }
}
