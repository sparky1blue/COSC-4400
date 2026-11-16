package Absyn;

public class FieldExpr extends Expr
{
    public Expr object;
    public String field;

    public FieldExpr(Expr object, String field)
    {
		this.object = object;
		this.field = field;
    }

    public void accept(Visitor v) {v.visit(this); }
}