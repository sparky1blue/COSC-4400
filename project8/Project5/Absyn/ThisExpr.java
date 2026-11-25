package Absyn;

public class ThisExpr extends Expr{
    public ThisExpr(){}

    public void accept(Visitor v) {v.visit(this);}
    public Translate.Exp accept(TreeVisitor v) { return v.visit(this); }
}
