package Absyn;
public class NotEqExpr extends Expr {
    
    public Expr left, right;
    
    public NotEqExpr(Expr left, Expr right) { 
        this.left = left; 
        this.right = right; 
    }
    
    public void accept(Visitor v) {v.visit(this); }
}