package Absyn;

import java.util.LinkedList;

public class VoidDecl extends MethodDecl{
    public VoidDecl(String name, LinkedList<Formal> params, LinkedList<Stmt> stmts)
    {
		super(null, false, name, params, new LinkedList<VarDecl>(), stmts, null);
    }

    public void accept(Visitor v) {v.visit(this); }
}
