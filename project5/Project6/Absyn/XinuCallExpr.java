package Absyn;
import java.util.LinkedList;

public class XinuCallExpr extends Expr
{
    public String method;
    public LinkedList<Expr> args;

    public XinuCallExpr(String method, LinkedList<Expr> args)
    {
		this.method = method;
		this.args = args;
    }

    public void accept(Visitor v) {v.visit(this); }
}