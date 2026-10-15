/* Copyright (C) 2007, Marquette University.  All rights reserved. */
package Absyn;

/**
 * Array type.
 */

public class ArrayType extends Type
{
    public Type base;
    public ArrayType(Type base)
    {
		this.base = base;
    }

    /** Visitor pattern dispatch. */
    public void accept(Visitor v) {v.visit(this); }
}
