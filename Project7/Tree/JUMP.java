/* Copyright (C) 1997-2003, Purdue Research Foundation of Purdue University.
 * All rights reserved.  */
package Tree;
import Temp.Temp;
import Temp.Label;
import java.util.List;
import java.util.LinkedList;

/**
 * Jump (transfer control) to address e.
 */

public class JUMP extends Stm 
{
    public Exp exp;
    public LinkedList<Label> targets;
    public JUMP(Exp e, LinkedList<Label> t) { exp=e; targets=t; }
    public JUMP(Label target) 
	{
		exp = new NAME(target);
		targets = new LinkedList<Label>();
		targets.addFirst(target);
    }

    public void accept(IntVisitor v, int d) { v.visit(this, d); }
}
