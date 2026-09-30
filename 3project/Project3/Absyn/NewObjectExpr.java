package Absyn;

public class NewObjectExpr extends Expr{
    public Type claName;

    public NewObjectExpr(Type claName){
        this.claName = claName;
    }
    public void accept(Visitor v){v.visit(this);}
    
}
