package Absyn;


public class WhileStmt extends Stmt
{
    public Expr cond;
    public Stmt body;

    public WhileStmt(Expr cond, Stmt body)
    {
		this.cond = cond;
		this.body = body;
    }

    public void accept(Visitor v) {v.visit(this); }
}