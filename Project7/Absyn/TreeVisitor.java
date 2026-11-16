/* Copyright (C) 2007, Marquette University.  All rights reserved. */
package Absyn;

/**
 * Interface for Visitor Pattern traversals.
 */

public interface TreeVisitor
{
    /** Visitor pattern dispatch. */
    public Translate.Exp visit(AddExpr ast);
    public Translate.Exp visit(AndExpr ast);
    public Translate.Exp visit(ArrayExpr ast);
    public Translate.Exp visit(ArrayType ast);
    public Translate.Exp visit(AssignStmt ast);
    public Translate.Exp visit(BlockStmt ast);
    public Translate.Exp visit(BooleanType ast);
    public Translate.Exp visit(CallExpr ast);
    public Translate.Exp visit(ClassDecl ast);
    public Translate.Exp visit(DivExpr ast);
    public Translate.Exp visit(EqualExpr ast);
    public Translate.Exp visit(FalseExpr ast);
    public Translate.Exp visit(FieldExpr ast);
    public Translate.Exp visit(Formal ast);
    public Translate.Exp visit(GreaterExpr ast);
    public Translate.Exp visit(IdentifierExpr ast);
    public Translate.Exp visit(IdentifierType ast);
    public Translate.Exp visit(IfStmt ast);
    public Translate.Exp visit(IntegerLiteral ast);
    public Translate.Exp visit(IntegerType ast);
    public Translate.Exp visit(LesserExpr ast);
    public Translate.Exp visit(MethodDecl ast);
    public Translate.Exp visit(MulExpr ast);
    public Translate.Exp visit(NegExpr ast);
    public Translate.Exp visit(NewArrayExpr ast);
    public Translate.Exp visit(NewObjectExpr ast);
    public Translate.Exp visit(NotEqExpr ast);
    public Translate.Exp visit(NotExpr ast);
    public Translate.Exp visit(NullExpr ast);
    public Translate.Exp visit(OrExpr ast);
    public Translate.Exp visit(Program ast);
    public Translate.Exp visit(SubExpr ast);
    public Translate.Exp visit(StringLiteral ast);
    public Translate.Exp visit(ThisExpr ast);
    public Translate.Exp visit(ThreadDecl ast);
    public Translate.Exp visit(TrueExpr ast);
    public Translate.Exp visit(VarDecl ast);
    public Translate.Exp visit(VoidDecl ast);
    public Translate.Exp visit(WhileStmt ast);
    public Translate.Exp visit(XinuCallExpr ast);
    public Translate.Exp visit(XinuCallStmt ast);
}
