package Absyn;

public class IfStmt extends Stmt
{
    public Expr cond;
    public Stmt thenStmt;
    public Stmt elseStmt;

    public IfStmt(Expr cond, Stmt thenStmt, Stmt elseStmt)
    {
		this.cond = cond;
		this.thenStmt = thenStmt;
		this.elseStmt = elseStmt;
    }

    public void accept(Visitor v) {v.visit(this); }
}