package Absyn;

public class NullExpr extends Expr
{
    public NullExpr() {}
    
    public void accept(Visitor v) {v.visit(this); }
    public Translate.Exp accept(TreeVisitor v) { return v.visit(this); }
}