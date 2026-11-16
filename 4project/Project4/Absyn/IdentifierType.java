/* Copyright (C) 2007, Marquette University.  All rights reserved. */
package Absyn;

/**
 * Class types.
 */

public class IdentifierType extends Type
{
    public String id;
    public IdentifierType(String id)
    {
		this.id = id;
    }

    /** Visitor pattern dispatch. */
    public void accept(Visitor v) {v.visit(this); }
}
