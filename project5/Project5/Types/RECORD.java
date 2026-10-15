/* Copyright (C) 1997-2003, Purdue Research Foundation of Purdue University.
 * All rights reserved.  */
/* Copyright (C) 2007, Marquette University.  All rights reserved. */
package Types;

import java.util.LinkedList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Type for aggregate records.
 */

public class RECORD extends Type implements Iterable<FIELD>
{
    private HashMap<String, FIELD> map = new HashMap<String, FIELD>();
    private LinkedList<FIELD> fields = new LinkedList<FIELD>();
    private int index = 0;

    public RECORD() {}

    public FIELD put(Type type, String name)
    {
		FIELD f = new FIELD(type, name, index);
		index++;
		fields.add(f);
		return map.put(name, f);
    }

    public FIELD get(String name)
    {
		return map.get(name);
    }

    public Iterator<FIELD> iterator()
    {   return fields.iterator();   }

    public int size() { return index; }

    public String toString()
    { 
		String result = "( ";
		for (FIELD f : fields)
		{ result = result + f.toString() + " "; }
		return result + ")";
    }

    public boolean coerceTo(Type t)
    {   return (t == this);   }

    /** Visitor pattern dispatch. */
    public void accept(Visitor v)
    {   v.visit(this);   }
}
