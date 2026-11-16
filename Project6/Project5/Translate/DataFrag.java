/* Copyright (C) 1997-2003, Purdue Research Foundation of Purdue University.
 * All rights reserved.  */
package Translate;

/**
 * Fragment that consists of a string literal.
 */

public class DataFrag extends Frag {
    public String data;
    public DataFrag(String s) {
	data = s;
    }
    public String toString() { return data; }
}
