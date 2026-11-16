/* Copyright (C) 2007, Marquette University.  All rights reserved. */
package Absyn;

/**
 * Expression abstract class.
 */

public abstract class Expr extends Absyn
{
    /** Visitor pattern dispatch. */
    public abstract void accept(Visitor v);
}
