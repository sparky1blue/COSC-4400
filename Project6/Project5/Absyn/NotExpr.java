package Absyn;

public class NotExpr extends Expr{
    public Expr expr;

    public NotExpr(Expr expr){
        this.expr = expr;
    }
    
    public void accept(Visitor v) {v.visit(this);}
    public Translate.Exp accept(TreeVisitor v) { return v.visit(this); }
}
