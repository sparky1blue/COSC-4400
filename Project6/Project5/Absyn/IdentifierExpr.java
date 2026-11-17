package Absyn;

public class IdentifierExpr extends Expr{
    public String name;

    public IdentifierExpr(String name){
        this.name = name;        
    }
    public void accept(Visitor v) {v.visit(this); }
    public Translate.Exp accept(TreeVisitor v) { return v.visit(this); }
}
