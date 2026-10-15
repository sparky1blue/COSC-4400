/* Copyright (C) 2007, Marquette University.  All rights reserved. */
package Absyn;
import java.util.AbstractList;

/**
 * Goal 	::= 	MainClass ( ClassDeclaration )* <EOF>
 */

public class Program extends Absyn
{
    public AbstractList<ClassDecl> classes;

    public Program(AbstractList<ClassDecl> classes)
    {
		this.classes = classes;
    }

    /** Visitor pattern dispatch. */
    public void accept(Visitor v) {v.visit(this); }
}
