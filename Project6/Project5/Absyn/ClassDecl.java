/* Copyright (C) 2007, Marquette University.  All rights reserved. */
package Absyn;
import java.util.LinkedList;

/**
 * Class Declaration Blocks
 */

public class ClassDecl extends Absyn
{
    public String name;
    public String parent;
    public LinkedList<VarDecl> fields;
    public LinkedList<MethodDecl> methods;
    public ClassDecl(String name, String parent, 
		     LinkedList<VarDecl> fields, LinkedList<MethodDecl> methods)
    {
		this.name = name;
		this.parent = parent;
		this.fields = fields;
		this.methods = methods;
    }

    /** Visitor pattern dispatch. */
    public void accept(Visitor v) {v.visit(this); }
}
