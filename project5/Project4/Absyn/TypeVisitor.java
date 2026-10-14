package Absyn;

/**
 * Interface for Visitor Pattern traversals that compute a Types.Type.
 */
public interface TypeVisitor
{
    /** Visitor pattern dispatch. */
    public Types.Type visit(java.util.AbstractList<Visitable> list);
    public Types.Type visit(AddExpr ast);
    public Types.Type visit(AndExpr ast);
    public Types.Type visit(ArrayExpr ast);
    public Types.Type visit(ArrayType ast);
    public Types.Type visit(AssignStmt ast);
    public Types.Type visit(BlockStmt ast);
    public Types.Type visit(BooleanType ast);
    public Types.Type visit(CallExpr ast);
    public Types.Type visit(ClassDecl ast);
    public Types.Type visit(DivExpr ast);
    public Types.Type visit(EqualExpr ast);
    public Types.Type visit(FalseExpr ast);
    public Types.Type visit(FieldExpr ast);
    public Types.Type visit(Formal ast);
    public Types.Type visit(GreaterExpr ast);
    public Types.Type visit(IdentifierExpr ast);
    public Types.Type visit(IdentifierType ast);
    public Types.Type visit(IfStmt ast);
    public Types.Type visit(IntegerLiteral ast);
    public Types.Type visit(IntegerType ast);
    public Types.Type visit(LesserExpr ast);
    public Types.Type visit(MethodDecl ast);
    public Types.Type visit(MulExpr ast);
    public Types.Type visit(NegExpr ast);
    public Types.Type visit(NewArrayExpr ast);
    public Types.Type visit(NewObjectExpr ast);
    public Types.Type visit(NotEqExpr ast);
    public Types.Type visit(NotExpr ast);
    public Types.Type visit(NullExpr ast);
    //public Types.Type visit(OrExpr ast);
    public Types.Type visit(Program ast);
    public Types.Type visit(StringLiteral ast);
    public Types.Type visit(SubExpr ast);
    public Types.Type visit(ThisExpr ast);
    public Types.Type visit(ThreadDecl ast);
    public Types.Type visit(TrueExpr ast);
    public Types.Type visit(VarDecl ast);
    public Types.Type visit(VoidDecl ast);
    public Types.Type visit(WhileStmt ast);
    public Types.Type visit(XinuCallExpr ast);
    public Types.Type visit(XinuCallStmt ast);
}