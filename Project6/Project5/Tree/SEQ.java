/* Copyright (C) 1997-2003, Purdue Research Foundation of Purdue University.
 * All rights reserved.  */
package Tree;
import Temp.Temp;
import Temp.Label;
import java.util.LinkedList;

/**
 * Sequence: statement 1 followed by statment 2.
 */

public class SEQ extends Stm 
{
    public Stm left, right;
    public SEQ(Stm l, Stm r) { left=l; right=r; }
    public void accept(IntVisitor v, int d) { v.visit(this, d); }
}
