package Absyn;

public class ArrayLength extends Expr{
    
    public Expr array;

    public ArrayLength(Expr array){
        this.array = array;
    }

    public void accept(Visitor v){v.visit(this);}
    public Translate.Exp accept(TreeVisitor v) { return v.visit(this); }
}
