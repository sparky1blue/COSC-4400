/* Copyright (C) 2007, Marquette University.  All rights reserved. */
package Absyn;

/**
 * Parent class of all abstract syntax tree nodes.
 */

public abstract class Absyn implements Visitable
{
    /** Visitor pattern dispatch. */
    public abstract void accept(Visitor v);
}
