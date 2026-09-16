/* Copyright (C) 2007, Marquette University.  All rights reserved. */
package Absyn;

/**
 * Interface for Visitor Pattern traversals.
 */

public interface Visitor
{
    /** Visitor pattern dispatch. */
    //    public void visit(Absyn ast);
    public void visit(java.util.AbstractList<Visitable> list);
    public void visit(ArrayType ast);
    public void visit(ClassDecl ast);
    public void visit(Formal ast);
    public void visit(IdentifierType ast);
    public void visit(IntegerLiteral ast);
    public void visit(MethodDecl ast);
    public void visit(Program ast);
    public void visit(StringLiteral ast);
    public void visit(VarDecl ast);
    public void visit(XinuCallStmt ast);
}
