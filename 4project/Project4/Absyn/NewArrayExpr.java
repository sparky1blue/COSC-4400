package Absyn;
import java.util.LinkedList;

public class NewArrayExpr extends Expr{
    public Type baseType;
    public LinkedList<Expr> sizes;

    public NewArrayExpr(Type baseType, LinkedList<Expr> sizes)
    {
		this.baseType = baseType;
		this.sizes = sizes;
    }

    public void accept(Visitor v) {v.visit(this); }
}
