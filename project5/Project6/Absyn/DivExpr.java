package Absyn;

public class DivExpr extends Expr {
    
    public Expr left, right;
    
    public DivExpr(Expr left, Expr right) { 
        this.left = left; 
        this.right = right; 
    }
    
    public void accept(Visitor v) {v.visit(this); }
}