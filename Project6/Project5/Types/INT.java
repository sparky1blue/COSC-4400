/* Copyright (C) 2007, Marquette University.  All rights reserved. */
package Types;

/**
 * Base type "int".
 */

public class INT extends Type
{
    public INT()
    {}

    public String toString()
    { return "int"; }

    public boolean coerceTo(Type t)
    {   return (t instanceof INT);   }

    /** Visitor pattern dispatch. */
    public void accept(Visitor v)
    {   v.visit(this);   }
}
