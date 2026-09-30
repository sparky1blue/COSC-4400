package Absyn;
public class NegExpr extends Expr {
    
    public Expr expr;
    
    public NegExpr(Expr expr) { 
        this.expr = expr; 
    }
    
    public void accept(Visitor v) {v.visit(this); }
}
