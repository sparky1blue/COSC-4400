/* Copyright (C) 2007, Marquette University.  All rights reserved. */
package Types;

/**
 * Type for arrays.
 */

public class ARRAY extends Type
{
    public Type element;
    public ARRAY(Type element)
    {
	this.element = element;
    }

    public String toString()
    { return element.toString() + "[]"; }

    public boolean coerceTo(Type t)
    {   
	if (t instanceof ARRAY)
	    return (element.coerceTo(((ARRAY)t).element));
	return false;   
    }

    /** Visitor pattern dispatch. */
    public void accept(Visitor v)
    {   v.visit(this);   }
}
