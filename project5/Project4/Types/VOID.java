/* Copyright (C) 2007, Marquette University.  All rights reserved. */
package Types;

/**
 * Base type void".
 */

public class VOID extends Type
{
    public VOID()
    {}

    public String toString()
    { return "void"; }

    public boolean coerceTo(Type t)
    {   return (t instanceof VOID);   }

    /** Visitor pattern dispatch. */
    public void accept(Visitor v)
    {   v.visit(this);   }
}
