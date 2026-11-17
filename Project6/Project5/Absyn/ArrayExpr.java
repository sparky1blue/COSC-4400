package Absyn;

public class ArrayExpr extends Expr{
    
    public Expr array, index;

    public ArrayExpr(Expr array, Expr index){
        this.array = array;
        this.index = index;
    }

    public void accept(Visitor v) {v.visit(this);}
    public Translate.Exp accept(TreeVisitor v) { return v.visit(this); }
}
