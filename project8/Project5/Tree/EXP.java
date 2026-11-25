/* Copyright (C) 1997-2003, Purdue Research Foundation of Purdue University.
 * All rights reserved.  */
package Tree;
import Temp.Temp;
import Temp.Label;
import java.util.LinkedList;

/**
 * Evaluates the expression and discards the result.
 */

public class EXP extends Stm 
{
    public Exp exp; 
    public EXP(Exp e) { exp=e; }
    public void accept(IntVisitor v, int d) { v.visit(this, d); }

    public LinkedList<Exp> kids() {
        LinkedList<Exp> k = new LinkedList<Exp>();
        k.add(exp);
        return k;
    }
    public Stm build(LinkedList<Exp> kids) { return new EXP(kids.get(0)); }
}
