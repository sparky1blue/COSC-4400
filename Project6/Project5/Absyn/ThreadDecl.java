package Absyn;

import java.util.LinkedList;

public class ThreadDecl extends ClassDecl{
    public ThreadDecl(String name, String parent, LinkedList<VarDecl> fields, LinkedList<MethodDecl> methods){
		super(name, parent, fields, methods);
    }

    public void accept(Visitor v) {v.visit(this); }
    public Translate.Exp accept(TreeVisitor v) { return v.visit(this); }
}
