package Absyn;
import java.util.LinkedList;

public class BlockStmt extends Stmt{
    public LinkedList<Stmt> stmts;

    public BlockStmt(LinkedList<Stmt> stmts){
		this.stmts = stmts;
    }

    public void accept(Visitor v) {v.visit(this); }
    
}
