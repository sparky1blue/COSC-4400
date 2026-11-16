package Absyn;

public class ArrayExpr extends Expr{
    
    public Expr array, index;

    public ArrayExpr(Expr array, Expr index){
        this.array = array;
        this.index = index;
    }

    public void accept(Visitor v) {v.visit(this);}
}
