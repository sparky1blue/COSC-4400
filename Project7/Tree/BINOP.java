/* Copyright (C) 1997-2003, Purdue Research Foundation of Purdue University.
 * All rights reserved.  */
package Tree;
import Temp.Temp;
import java.util.LinkedList;

/**
 * Implements a binary operator.
 */

public class BINOP extends Exp {
    public int binop;
    public Exp left, right;
    public BINOP(int b, Exp l, Exp r) {	binop=b; left=l; right=r; }
    public final static	int PLUS=0, MINUS=1, MUL=2, DIV=3, AND=4, OR=5,
	LSHIFT=6,RSHIFT=7,ARSHIFT=8,BITAND=9,BITOR=10,BITXOR=11;

    public void accept(IntVisitor v, int d) { v.visit(this, d); }
}
