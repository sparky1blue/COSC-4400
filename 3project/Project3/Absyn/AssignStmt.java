package Absyn;

public class AssignStmt extends Stmt{
    public Expr target;
    public Expr value;

    public AssignStmt(Expr target, Expr value)
    {
		this.target = target;
		this.value = value;
    }

    public void accept(Visitor v) {v.visit(this); }
}
