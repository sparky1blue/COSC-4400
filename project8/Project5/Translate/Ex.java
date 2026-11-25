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
        if (exp instanceof Tree.CONST) return null;
        return new Tree.EXP(exp);
    }
    Tree.Stm unCx(Label t, Label f) {
        if (exp instanceof Tree.CONST) {
            Tree.CONST c = (Tree.CONST) exp;
            if (c.value != 0) return new Tree.JUMP(t);
            return new Tree.JUMP(f);
        }
        return new Tree.CJUMP(Tree.CJUMP.NE, exp, new Tree.CONST(0), t, f);
    }
}
