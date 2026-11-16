/* Copyright (C) 1997-2003, Purdue Research Foundation of Purdue University.
 * All rights reserved.  */
package Temp;
import Symbol.Symbol;

/**
 * A Label represents an address in assembly language.
 */

public class Label  
{
   private String name;		/* We give a name to the label */
   private static int count=0;

	/**
	 * a printable representation of the label, for use in assembly 
	 * language output.
	 */
	public String toString() {return name;}
	
	/**
	 * Makes a new label that prints as "name".
	 * Warning: avoid repeated calls to <tt>new Label(s)</tt> with
	 * the same name <tt>s</tt>.
	 */
	public Label(String n) 
	{
		name = n;
	}
	
	/**
	 * Makes a new label with an arbitrary name.
	 */
	public Label() 
	{
		this("L" + count++);
	}
	
	/**
	 * Makes a new label whose name is the same as a symbol.
	 */
	public Label(Symbol s) 
	{
		this(s.toString());
	}
}
