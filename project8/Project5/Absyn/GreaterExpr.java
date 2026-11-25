package Absyn;
public class GreaterExpr extends Expr {
    
    public Expr left, right;
    
    public GreaterExpr(Expr left, Expr right) { 
        this.left = left; 
        this.right = right; 
    }

    public void accept(Visitor v) {v.visit(this); }
    public Translate.Exp accept(TreeVisitor v) { return v.visit(this); }
}