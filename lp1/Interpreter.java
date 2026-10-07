/**
 * Interpreter.java
 *
 * A skeleton of the interpreter for the "straight-line" language of
 *    Appel chapter 1.
 *
 * @author Dennis Brylow <brylow@cs.mu.edu>
 * @version 1.1
 */

public class Interpreter{
    private boolean debug = false;

    java.util.Hashtable table = new java.util.Hashtable();

    public static void main(String[] args)
    {
	Interpreter interpreter = new Interpreter();
	System.out.println("Evaluating program.");
	Absyn.Goal g = null;
	try
	  {
	    java.io.FileReader reader = new java.io.FileReader(args[0]);
	    g = new LP1Parser(reader).Goal();
	  }
	catch (java.io.IOException e)
	  {
	    System.err.println(e.toString());
	  }
	catch (ParseException p)
	  {
	    System.out.println(p.toString());
	    System.exit(-1);
	  }
	System.out.println(interpreter.visit(g));
    }

    ////////////////////////////////
    // Visit methods.             //
    // This is where all the REAL //
    //  interpreter action takes  //
    //  place.                    //
    ////////////////////////////////

    int visit(Absyn.Goal goal) 
    { 
      return visit(goal.e);
    }

    int visit(Absyn.Exp exp)
    {
      if      (exp instanceof Absyn.AddExp) return visit((Absyn.AddExp)exp);
      else if (exp instanceof Absyn.SubExp) return visit((Absyn.SubExp)exp);
      else if (exp instanceof Absyn.MulExp) return visit((Absyn.MulExp)exp);
      else if (exp instanceof Absyn.DivExp) return visit((Absyn.DivExp)exp);
      else if (exp instanceof Absyn.ModExp) return visit((Absyn.ModExp)exp);
      else if (exp instanceof Absyn.NumExp) return visit((Absyn.NumExp)exp);
      else if (exp instanceof Absyn.IdExp)  return visit((Absyn.IdExp)exp);
      else return 0;
    }

    int visit(Absyn.IdExp exp)
    {
      System.err.println("ERROR Interpreter::"
			 + " Unbound identifier, \""
			 + exp.id + "\"");
      return 0;
    }

    int visit(Absyn.NumExp exp)
    {
	return exp.num;
    }

    int visit(Absyn.AddExp exp)
    {   return visit(exp.left) + visit(exp.right);   }

    int visit(Absyn.SubExp exp)
    {   return visit(exp.left) - visit(exp.right);   }

    int visit(Absyn.MulExp exp)
    {   return visit(exp.left) * visit(exp.right);   }

    int visit(Absyn.DivExp exp)
    {   return visit(exp.left) / visit(exp.right);   }
    int visit (Absyn.ModExp exp){
	    return visit(exp.left) % visit(exp.right);}





} // Interpreter
