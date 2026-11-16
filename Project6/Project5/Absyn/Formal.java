/* Copyright (C) 2007, Marquette University.  All rights reserved. */
package Absyn;

/**
 * Declarations for formal method parameters.
 */

public class Formal extends Absyn
{
    public Type type;
    public String name;
    public Formal(Type type, String name)
    {
		this.type = type;
		this.name = name;
    }

    /** Visitor pattern dispatch. */
    public void accept(Visitor v) {v.visit(this); }
}
