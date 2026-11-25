/* Copyright (C) 1997-2003, Purdue Research Foundation of Purdue University.
 * All rights reserved.  */
package Translate;
import Temp.Label;

/**
 * Stands for "no result", represented as a Tree statement.
 */

public class Nx extends Exp {
    Tree.Stm stm;
    Nx(Tree.Stm s) { stm = s; }

    Tree.Exp unEx() { return null; }

    Tree.Stm unNx() { return stm; }

    Tree.Stm unCx(Label t, Label f) { return null; }
}
