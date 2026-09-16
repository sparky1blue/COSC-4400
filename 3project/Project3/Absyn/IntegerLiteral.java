/* Copyright (C) 2007, Marquette University.  All rights reserved. */
package Absyn;

/**
 * Integer Literals.
 */

public class IntegerLiteral extends Expr
{
    public int value;

    public IntegerLiteral(int value)
    {
		this.value = value;
    }

    public IntegerLiteral(Integer value)
    {
		this.value = value.intValue();
    }

    /** Visitor pattern dispatch. */
    public void accept(Visitor v) {v.visit(this); }
}
