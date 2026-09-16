/* Copyright (C) 2007, Marquette University.  All rights reserved. */
package Absyn;

/**
 * Statement abstract class.
 */

public abstract class Stmt extends Absyn
{
    /** Visitor pattern dispatch. */
    public abstract void accept(Visitor v);
}
