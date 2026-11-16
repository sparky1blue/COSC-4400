/* Copyright (C) 2007, Marquette University.  All rights reserved. */
package Types;

/**
 * Type for fields.
 */

public class FIELD extends Type
{
    public String name;
    public Type type;
    public int index;
    public FIELD(Type type, String name, int index)
    {
	this.type = type;
	this.name = name;
	this.index = index;
    }

    public String toString()
    { return type.toString() + ":" + name; }

    public boolean coerceTo(Type t)
    {
	if (t instanceof FIELD)
	    return type.coerceTo(((FIELD)t).type);
	return type.coerceTo(t);   
    }

    /** Visitor pattern dispatch. */
    public void accept(Visitor v)
    {   v.visit(this);   }
}
