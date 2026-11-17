package Absyn;


public class IntegerType extends Type
{
    public IntegerType() {}


    public void accept(Visitor v) {v.visit(this); }
    public Translate.Exp accept(TreeVisitor v) { return v.visit(this); }
}