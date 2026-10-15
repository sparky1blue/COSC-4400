/* Copyright (C) 2007, Marquette University.  All rights reserved. */
package Types;

import java.util.Iterator;

/**
 * Type for methods.
 */

public class FUNCTION extends Type
{
    public String name = null;
    public Type self   = null;
    public RECORD formals = null;
    public Type result = null;
    public FUNCTION(String name, Type self, RECORD formals, Type result)
    {
		this.name = name;
		this.self = self;
		this.formals = formals;
		this.result = result;
    }

    public FIELD addFormal(Type type, String name)
    {
		return formals.put(type, name);
    }

    public String toString()
    { return formals.toString() + " -> " + result; }

    public boolean coerceTo(Type t)
    {
		// Check that both are FUNCTION type.
		if (t instanceof FUNCTION)
	    {
			FUNCTION f = (FUNCTION)t;
			// Check that result types are equal.
			if (!(result.coerceTo(f.result)))
				return false;
			Iterator iter1 = formals.iterator();
			Iterator iter2 = f.formals.iterator();
			// Check that self type is coerceable.
			if (!self.coerceTo(f.self)) return false;
			// Check that all other formal types are equal.
			while (iter1.hasNext() && iter2.hasNext())
		    {
				FIELD f1 = (FIELD)iter1.next();
				FIELD f2 = (FIELD)iter2.next();
				if (!(f1.coerceTo(f2) && (f2.coerceTo(f1))))
					return false;
		    }
			// Check that number of formals was equal.
			if (iter1.hasNext() || iter2.hasNext())
				return false;
			return true;
	    }
		return false;   
    }

    /** Visitor pattern dispatch. */
    public void accept(Visitor v)
    {   v.visit(this);   }
}
