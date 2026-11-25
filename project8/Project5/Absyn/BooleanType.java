package Absyn;


public class BooleanType extends Type
{
    public BooleanType() {}


    public void accept(Visitor v) {v.visit(this); }
    public Translate.Exp accept(TreeVisitor v) { return v.visit(this); }
}