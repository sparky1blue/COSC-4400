package Absyn;

public class FalseExpr extends Expr{
    public boolean value;

    public FalseExpr(){}

    public void accept(Visitor v) {v.visit(this); }
    public Translate.Exp accept(TreeVisitor v) { return v.visit(this); }

}
