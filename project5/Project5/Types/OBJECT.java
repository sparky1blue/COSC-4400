/* Copyright (C) 2007, Marquette University.  All rights reserved. */
package Types;

/**
 * Type for object with inheritance taken into account.
 */

public class OBJECT extends Type
{
    public CLASS myClass;
    public RECORD methods;
    public RECORD fields;
    public OBJECT(CLASS myClass, RECORD methods, RECORD fields)
    {
	this.myClass = myClass;
	this.methods = methods;
	this.fields  = fields;
    }

    public OBJECT(CLASS myClass)
    {   this(myClass, new RECORD(), new RECORD());   }

    public String toString()
    { return "OBJECT:" + myClass.name; }

    public boolean coerceTo(Type t)
    {
	if (t instanceof OBJECT)
	    {
		OBJECT o = (OBJECT)t;
		CLASS parent = o.myClass;
		CLASS current = this.myClass;
		while (null != current)
		    {
			if (current.coerceTo(parent)) return true;
			else current = current.parent;
		    }
	    }
	return false;
    }

    /** Visitor pattern dispatch. */
    public void accept(Visitor v)
    {   v.visit(this);   }
}
