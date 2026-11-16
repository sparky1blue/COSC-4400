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

    public void visit(IdentifierExpr ast);
    public void visit(IntegerType ast);
    public void visit(BooleanType ast);
    public void visit(FalseExpr ast);
    public void visit(TrueExpr ast);
    public void visit(ThisExpr ast);
    public void visit(NotExpr ast);
    public void visit(MulExpr ast);
    public void visit(AddExpr ast);
    public void visit(SubExpr ast);
    public void visit(LesserExpr ast);
    public void visit(AndExpr ast);
    public void visit(NullExpr ast);
    public void visit(DivExpr ast);
    public void visit(GreaterExpr ast);
    public void visit(EqualExpr ast);
    public void visit(NotEqExpr ast);
    public void visit(NegExpr ast);
    public void visit(ArrayExpr ast);
    public void visit(CallExpr ast);
    public void visit(NewArrayExpr ast);
    public void visit(NewObjectExpr ast);
    public void visit(ArrayLength ast);
    public void visit(BlockStmt ast);
    public void visit(AssignStmt ast);
    public void visit(IfStmt ast);
    public void visit(WhileStmt ast);
    public void visit(ThreadDecl ast);
    public void visit(VoidDecl ast);
    public void visit(FieldExpr ast);
    public void visit(XinuCallExpr ast);
}
