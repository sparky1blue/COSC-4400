/* Copyright (C) 1997-2003, Purdue Research Foundation of Purdue University.
 * All rights reserved.  */
package Tree;
import Temp.Temp;
import Temp.Label;
import java.util.LinkedList;

/**
 * An integer constant.
 */

public class CONST extends Exp 
{
    public int value;
    public CONST(int v) { value=v; }
    public void accept(IntVisitor v, int d) { v.visit(this, d); }
}
