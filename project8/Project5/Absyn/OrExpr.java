package Absyn;

public class OrExpr extends Expr {

    public Expr first, second;

    public OrExpr(Expr first, Expr second) {
        this.first = first;
        this.second = second;
    }

    public void accept(Visitor v) { v.visit(this); }
    public Translate.Exp accept(TreeVisitor v) { return v.visit(this); }
}