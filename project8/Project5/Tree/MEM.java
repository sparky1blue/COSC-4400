/* Copyright (C) 1997-2003, Purdue Research Foundation of Purdue University.
 * All rights reserved.  */
package Tree;
import Temp.Temp;
import Temp.Label;
import java.util.LinkedList;

/**
 * Contents of a word of memory starting at address e.
 */

public class MEM extends Exp 
{
    public Exp exp;
    public MEM(Exp e) { exp=e; }
    public void accept(IntVisitor v, int d) { v.visit(this, d); }
    public LinkedList<Exp> kids() {
        LinkedList<Exp> k = new LinkedList<Exp>();
        k.add(exp);
        return k;
    }
    public Exp build(LinkedList<Exp> kids) { return new MEM(kids.get(0)); }
}
