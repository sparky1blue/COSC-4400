/*
 * Copyright 2024 - Marquette University
 */
package Riscv;
import java.util.HashMap;
import java.util.List;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Iterator;
import Symbol.Symbol;
import Temp.Temp;
import Temp.Label;
import java.util.Arrays;

/**
 * Implements a RISC-V assembly language frame.
 * See MCIJ2e Section 6.2.
 */

public class RiscvFrame extends Frame.Frame 
{
    // Keeps track of current negative offset (bytes) of next local below fp
    public int localsOffset = 0;
    // Keeps track of maximum args (bytes) spilled into frame
    public int maxArgOffset = 0;
    // Bytes per word on target architecture
    private static final int wordSize = 4;

    // Frame API provides this for new frames to be allocated per MJ method
    public Frame.Frame newFrame(Symbol name) 
    {
        return new RiscvFrame(name);
    }

    // Default constructor not used
    public RiscvFrame() {}

    // Private constructor does actual initialization, called from newFrame
    private RiscvFrame(Symbol n) 
    {
	name = new Label(n);
	formals = new LinkedList<Frame.Access>();
	actuals = new LinkedList<Frame.Access>();
    }

    public int wordSize() { return wordSize; }

    // Frame API allows formal parameters to be allocated
    public Frame.Access allocFormal () { return allocFormal(false); }
    public Frame.Access allocFormal (boolean inFrame) 
    {
	Frame.Access a;
	int index = formals.size();
	// Check for room in arg passing registers
	if (index < argRegs.length) 
	{
	    // Allocate room for actual parameter for each formal
	    actuals.add(new InReg(argRegs[index]));
	    if (inFrame)
		a = new InFrame((index - argRegs.length) * wordSize);
	    else
		a = new InReg(new Temp());
	}
	// Otherwise, spill args to frame
	else 
	{
	    a = new InFrame((index - argRegs.length) * wordSize);
	    actuals.add(a);
	    if (!inFrame)
		a = new InReg(new Temp());
	}
	formals.add(a);
	return a;
    }
    
    // Frame API allows local variables to be allocated.
    // By default, allocate locals in a register.  Allocator will spill
    //  to frame later, if necessary.
    public Frame.Access allocLocal () { return allocLocal(false); }
    public Frame.Access allocLocal (boolean inFrame) 
    {
	Frame.Access a;
	if (inFrame) 
	{
	    a = new InFrame(localsOffset -= wordSize);
	} else {
	    a = new InReg(new Temp());
	}
	return a;
    }
    public Frame.Access allocLocal(Temp t) {	return new InReg(t);  }
    public Frame.Access allocLocal(int i)  {  return new InFrame(i); }

    // RISC-V specific register assignments
    static final Temp ZERO = new Temp(); // hardwired zero
    static final Temp RA = new Temp();   // return address
    static final Temp SP = new Temp();   // stack pointer
    static final Temp GP = new Temp();   // global pointer
    static final Temp TP = new Temp();   // thread pointer
    static final Temp T0 = new Temp();   // temporary register 0
    static final Temp T1 = new Temp();   // temporary register 1
    static final Temp T2 = new Temp();   // temporary register 2
    static final Temp S0 = new Temp();   // saved register 0 / frame pointer
    static final Temp S1 = new Temp();   // saved register 1
    static final Temp A0 = new Temp();   // function argument 0 / return value 0
    static final Temp A1 = new Temp();   // function argument 1 / return value 1
    static final Temp A2 = new Temp();   // function argument 2
    static final Temp A3 = new Temp();   // function argument 3
    static final Temp A4 = new Temp();   // function argument 4
    static final Temp A5 = new Temp();   // function argument 5
    static final Temp A6 = new Temp();   // function argument 5
    static final Temp A7 = new Temp();   // function argument 5
    static final Temp S2 = new Temp();   // saved register 2
    static final Temp S3 = new Temp();   // saved register 3
    static final Temp S4 = new Temp();   // saved register 4
    static final Temp S5 = new Temp();   // saved register 5
    static final Temp S6 = new Temp();   // saved register 6
    static final Temp S7 = new Temp();   // saved register 7
    static final Temp S8 = new Temp();   // saved register 8
    static final Temp S9 = new Temp();   // saved register 9
    static final Temp S10 = new Temp();  // saved register 10
    static final Temp S11 = new Temp();  // saved register 11
    static final Temp T3 = new Temp();   // temporary register 3
    static final Temp T4 = new Temp();   // temporary register 4
    static final Temp T5 = new Temp();   // temporary register 5
    static final Temp T6 = new Temp();   // temporary register 6

    // Register lists: must not overlap and must include every register that
    // might show up in code
    static final Temp[]
    // registers dedicated to special purposes
	specialRegs = { ZERO, SP, GP, TP, S0 },
    // registers to pass outgoing arguments
	argRegs	= { A0, A1, A2, A3, A4, A5, A6, A7 },
    // registers that a callee must preserve for its caller
	calleeSaves = { RA, S1, S2, S3, S4, S5, S6, S7, S8, S9, S10, S11 },
    // registers that a callee may use without preserving
	callerSaves = { T0, T1, T2, T3, T4, T5, T6};

    public static final Temp[] registers = 
    {  ZERO, RA, SP, GP, TP, T0, T1, T2, S1, A0, A1, A2, A3, A4, A5, A6, A7 ,
    S2, S3, S4, S5, S6, S7, S8, S9, S10, S11, T3, T4, T5, T6};

    public static final Temp[] colorRegs = 
    { A0, A1, A2, A3, A4, A5, A6, A7, T0, T1, T2, T3, T4, T5, T6,
      S1, S2, S3, S4, S5, S6, S7, S8, S9, S10, S11, RA};

    public static final Temp[] calldefs =
    { RA, T0, T1, T2, T3, T4, T5, T6, A0, A1, A2, A3, A4, A5, A6, A7 };

    private static final Temp[] returnSink =
    { ZERO, GP, SP, TP, RA, S1, S2, S3, S4, S5, S6, S7, S8, S9, S10, S11 };
	
    static final Temp FP = S0; // virtual frame pointer (eliminated)
    public Temp FP() { return FP; }
    public Temp RV() { return A0; }

    public Temp[] colorRegs()   { return colorRegs; }
    public Temp[] specialRegs() { return specialRegs; }
    public Temp[] allRegs()     { return registers; }
    

    private static HashMap<Symbol,Label> labels = new HashMap<Symbol,Label>();

    public Tree.Exp externalCall(Symbol s, List<Tree.Exp> args) 
    {
	Label l = labels.get(s);
	if (l == null) 
	{
	    l = new Label("_" + s.toString());
	    labels.put(s, l);
	}

	return new Tree.CALL(new Tree.NAME(l), args);
    }

    public String string(Label lab, String string) 
    {
	int length = string.length();
	String lit = "";
	for (int i = 0; i < length; i++) 
	{
	    char c = string.charAt(i);
	    switch(c) 
	    {
	    case '\b': lit += "\\b"; break;
	    case '\t': lit += "\\t"; break;
	    case '\n': lit += "\\n"; break;
	    case '\f': lit += "\\f"; break;
	    case '\r': lit += "\\r"; break;
	    case '\"': lit += "\\\""; break;
	    case '\\': lit += "\\\\"; break;
	    default:
		if (c < ' ' || c > '~') 
		{
		    int v = (int)c;
		    lit += "\\" + ((v>>6)&7) + ((v>>3)&7) + (v&7);
		} 
		else 
		{
		    lit += c;
		}
		break;
	    }
	}
	return "\t.data\n" + lab.toString()
	    + ":\t.asciz\t\"" + lit + "\"";
    }

    public String word(Label lab, int value) 
    {
	return "\t.data\n" + lab + ":\n\t.word " + value;
    }

    public String record(Label lab, int words) 
    {
	String result = "\t.data\n" + lab + ":";
	while (--words >= 0)
	    result += "\n\t.word 0";
	return result + "\n";
    }

    public String record(Label lab, List<Label> values) 
    {
	String result = "\t.data\n" + lab + ":";
	for (Label label : values)
	{
	    result += "\n\t.word " + label;
	}
	return result + "\n";
    }

    private static final Label badPtr = new Label("_BADPTR");
    public Label badPtr() { return badPtr; }

    private static final Label badSub = new Label("_BADSUB");
    public Label badSub() {	return badSub; }

    private static final
    HashMap<Temp,String> tempMap = new HashMap<Temp,String>(32);
    static {
        tempMap.put(ZERO, "zero");
        tempMap.put(RA,   "ra");
        tempMap.put(SP,   "sp");
        tempMap.put(GP,   "gp");
        tempMap.put(TP,   "tp");
        tempMap.put(T0,   "t0");
        tempMap.put(T1,   "t1");
        tempMap.put(T2,   "t2");
        tempMap.put(FP,   "fp");
        tempMap.put(S1,   "s1");
        tempMap.put(A0,   "a0");
        tempMap.put(A1,   "a1");
        tempMap.put(A2,   "a2");
        tempMap.put(A3,   "a3");
        tempMap.put(A4,   "a4");
        tempMap.put(A5,   "a5");
        tempMap.put(S2,   "s2");
        tempMap.put(S3,   "s3");
        tempMap.put(S4,   "s4");
        tempMap.put(S5,   "s5");
        tempMap.put(S6,   "s6");
        tempMap.put(S7,   "s7");
        tempMap.put(S8,   "s8");
        tempMap.put(S9,   "s9");
        tempMap.put(S10,  "s10");
        tempMap.put(S11,  "s11");
        tempMap.put(T3,   "t3");
        tempMap.put(T4,   "t4");
        tempMap.put(T5,   "t5");
        tempMap.put(T6,   "t6");
    }

    public String tempMap(Temp temp)
    {
        return tempMap.get(temp);
    }

    private static Tree.Stm SEQ(Tree.Stm left, Tree.Stm right) 
    {
	if (left == null)
	    return right;
	if (right == null)
	    return left;
	return new Tree.SEQ(left, right);
    }

    private static Tree.MOVE MOVE(Tree.Exp d, Tree.Exp s) 
    {
	return new Tree.MOVE(d, s);
    }

    private static Tree.TEMP TEMP(Temp t) 
    {
	return new Tree.TEMP(t);
    }

    public void printFrame(java.io.PrintWriter writer)
    {
	writer.println("Riscv.RiscvFrame(");
	writer.println(this.name + ":");
	writer.print("Formals(");
    
	for (Frame.Access access : formals)
	{
	    if (access instanceof InReg)
	    {
		writer.print("InReg(" + access + ")");
	    }
	    else if (access instanceof InFrame)
	    {
		writer.print("InFrame(" + access + ")");
	    }
	    else
	    {
		writer.print("?");
	    }
	    writer.print("\n\t");
	}
	writer.println(")");

	writer.print("Actuals(");
	for (Frame.Access access : actuals)
	{
	    if (access instanceof InReg)
	    {
		writer.print("InReg(" + access + ")");
	    }
	    else if (access instanceof InFrame)
	    {
		writer.print("InFrame(" + access + ")");
	    }
	    else
	    {
		writer.print("?");
	    }
	    writer.print("\n\t");
	}
	writer.println(")");
	if (null != badPtr) writer.println("BadPtr(" + badPtr + ")");
	if (null != badSub) writer.println("BadSub(" + badSub + ")");
	writer.println(")");
    }

    public void procEntryExit1(List<Tree.Stm> body)
    {
    }
}
