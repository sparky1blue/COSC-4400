/* Copyright (C) 1997-2003, Purdue Research Foundation of Purdue University.
 * All rights reserved.  */
package Translate;
import Temp.Label;

/**
 * Stands for an "expression", represented as a Tree.Exp.
 */

public class Ex extends Exp {
    Tree.Exp exp;
    Ex(Tree.Exp e)  { exp = e; }
    Tree.Exp unEx() { return exp; }
    Tree.Stm unNx() {
	// TODO: If exp is CONST, return null, else EXP
	return null;
    }
    Tree.Stm unCx(Label t, Label f) {
	// TODO: if the exp is a constant, emit JUMP statement,
	// else CJUMP( exp != false )
	return null;
    }
}
