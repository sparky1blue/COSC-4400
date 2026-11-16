/* Copyright (C) 1997-2003, Purdue Research Foundation of Purdue University.
 * All rights reserved.  */
package Translate;
import Temp.Label;

/**
 * Conditional subclass that holds the left and right 
 * expressions and the comparison operator.
 */

public class RelCx extends Cx {
    int op;
    Tree.Exp left, right;

    RelCx(int o, Tree.Exp l, Tree.Exp r) {
	op = o;
	left = l;
	right = r;
    }

    Tree.Stm unCx(Label t, Label f) {
	return new Tree.CJUMP(op, left, right, t, f);
    }
}
