/* Copyright (C) 1997-2003, Purdue Research Foundation of Purdue University.
 * All rights reserved.  */
package Temp;

/**
 * Class used to create a temp, an abstract name for a local variable.
 */

public class Temp 
{
    private static int count;
    private int num;
    public String toString() {return "t" + num;}
    public Temp() 
	{ 
		num = count++; 
	}
    public int hashCode() { return num; }
	public boolean spillTemp = false;
    public int spillCost;

}
