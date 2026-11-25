package Absyn;

public class TrueExpr extends Expr{
    public boolean value;

    public TrueExpr(){}

    public void accept(Visitor v) {v.visit(this); }
    public Translate.Exp accept(TreeVisitor v) { return v.visit(this); }

}