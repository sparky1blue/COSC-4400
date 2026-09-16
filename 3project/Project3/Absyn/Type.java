/* Copyright (C) 2007, Marquette University.  All rights reserved. */
package Absyn;

/**
 * Type abstract class.
 */

public abstract class Type extends Absyn
{
    /** Visitor pattern dispatch. */
    public abstract void accept(Visitor v);
}
