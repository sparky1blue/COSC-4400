/* Copyright (C) 2007, Marquette University.  All rights reserved. */
package Types;

/**
 * Class descriptor.
 */

public class CLASS extends Type
{
    public String name;
    public CLASS parent = null;
    public RECORD methods = new RECORD();
    public RECORD fields  = new RECORD();
    public OBJECT instance;
    public CLASS(String name)
    {
		this.name = name;
		this.instance = new OBJECT(this);
    }

    public String toString()
    { return name; }

    public boolean coerceTo(Type t)
    {   return (this == t);   }

    /** Visitor pattern dispatch. */
    public void accept(Visitor v)
    {   v.visit(this);   }
}
