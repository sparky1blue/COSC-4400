/* Copyright (C) 1997-2003, Purdue Research Foundation of Purdue University.
 * All rights reserved.  */
package Tree;
import Temp.Temp;
import Temp.Label;
import java.util.LinkedList;

/**
 * Define constant value as current code address.
 */

public class LABEL extends Stm 
{ 
    public Label label;
    public LABEL(Label l) { label=l; }
    public LinkedList<Exp> kids() { return new LinkedList<Exp>(); }
    public void accept(IntVisitor v, int d) { v.visit(this, d); }
}
