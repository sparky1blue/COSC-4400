/* Copyright (C) 1997-2003, Purdue Research Foundation of Purdue University.
 * All rights reserved.  */
package Translate;

/**
 * Class from which all translate expressions inherit.
 */

public abstract class Exp {
    abstract Tree.Exp unEx();
    abstract Tree.Stm unNx();
    abstract Tree.Stm unCx(Temp.Label t, Temp.Label f);
}
