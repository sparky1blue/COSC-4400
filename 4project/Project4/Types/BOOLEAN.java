/* Copyright (C) 2007, Marquette University.  All rights reserved. */
package Types;

/**
 * Basetype "boolean".
 */

public class BOOLEAN extends Type
{
    public BOOLEAN()
    {}

    public String toString()
    { return "boolean"; }

    public boolean coerceTo(Type t)
    {   return (t instanceof BOOLEAN);   }

    /** Visitor pattern dispatch. */
    public void accept(Visitor v)
    {   v.visit(this);   }
}
