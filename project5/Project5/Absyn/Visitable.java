/* Copyright (C) 2007, Marquette University.  All rights reserved. */
package Absyn;

/**
 * Interface for nodes that permit Visitor Pattern traversals.
 */

public interface Visitable
{
    /** Visitor pattern dispatch. */
    public void accept(Visitor v);
}
