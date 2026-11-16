/* Copyright (C) 1997-2003, Purdue Research Foundation of Purdue University.
 * All rights reserved.  */
package Tree;
import Temp.Temp;
import java.util.LinkedList;

/**
 * An abstract class that represents a statment in the Tree language.
 */

abstract public class Stm implements Hospitable
{
    public abstract void accept(IntVisitor v, int d);
}
