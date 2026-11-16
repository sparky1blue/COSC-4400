/* Copyright (C) 1997-2003, Purdue Research Foundation of Purdue University.
 * All rights reserved.  */
package Translate;

/**
 * Implements translation of abstract syntax expressions
 * into intermediate trees.
 */

public class Translate implements Absyn.TreeVisitor 
{
    public Frame.Frame frame;
	
    public Translate(Frame.Frame frame) { this.frame = frame; }
	
	
    private static Tree.Exp CONST(int value)  { return new Tree.CONST(value); }
    private static Tree.Exp NAME(Label label) {	return new Tree.NAME(label); }
    private static Tree.Exp TEMP(Temp temp)   {	return new Tree.TEMP(temp); }
    private static Tree.Exp BINOP(int binop, Tree.Exp left, Tree.Exp right) 
    {   return new Tree.BINOP(binop, left, right);   }
    private static Tree.Exp MEM(Tree.Exp exp) {	return new Tree.MEM(exp); }
    private static Tree.Exp CALL(Tree.Exp func, List args) 
    {	return new Tree.CALL(func, args);   }
    private static Tree.Exp ESEQ(Tree.Stm stm, Tree.Exp exp)
    {
	if (stm == null) return exp;
	return new Tree.ESEQ(stm, exp);
    }
	
    private static Tree.Stm MOVE(Tree.Exp dst, Tree.Exp src) 
    {
	return new Tree.MOVE(dst, src);
    }
    private static Tree.Stm EXP(Tree.Exp exp) {	return new Tree.EXP(exp); }
    private static Tree.Stm JUMP(Label target) 
    {	return new Tree.JUMP(target);   }
    private static Tree.Stm CJUMP(int relop, Tree.Exp l, Tree.Exp r,
				  Label t, Label f)
    {	return new Tree.CJUMP(relop, l, r, t, f);   }
    private static Tree.Stm SEQ(Tree.Stm left, Tree.Stm right) 
    {
	if (left == null)
	    return right;
	if (right == null)
	    return left;
	return new Tree.SEQ(left, right);
    }
    private static Tree.Stm LABEL(Label label) 
    {
	return new Tree.LABEL(label);
    }
	
    private LinkedList<Frag> frags = new LinkedList<Frag>();
    public List<Frag> results() { return frags; }

    private HashMap<String, CLASS>  classEnv  = new HashMap<String, CLASS>();
    private HashMap<Symbol, Label>  methodEnv = new HashMap<Symbol, Label>();
    private HashMap<String, Label>  staticEnv = new HashMap<String, Label>();
    private HashMap<String, Label>  vtableEnv = new HashMap<String, Label>();
    private Table<Frame.Access>     varEnv    = new Table<Frame.Access>();
	
    public Exp visit(Absyn.Program n)
    {
	// TODO
	return null;
    }
    
    public Exp visit(Absyn.ClassDecl c) 
    {
	// TODO
	return null;
    }

    public Exp visit(Absyn.ThreadDecl c) 
    {
	// TODO
	return null;
    }

    public Exp visit(Absyn.MethodDecl m) 
    {
	// TODO
	return null;
    }

    public Exp visit(Absyn.VoidDecl m) 
    {
	// TODO
	return null;
    }

    public Exp visit(Absyn.Formal f) 
    {
	// TODO
	return null;
    }

    public Exp visit(Absyn.VarDecl v) 
    {
	// TODO
	return null;
    }

    public Exp visit(Absyn.BooleanType  t)   { return new Nx(null); }
    public Exp visit(Absyn.IntegerType  t)   { return new Nx(null); }
    public Exp visit(Absyn.ArrayType t)      { return new Nx(null); }
    public Exp visit(Absyn.IdentifierType t) { return new Nx(null); }

    public Exp visit(Absyn.AssignStmt s) 
    {
	// TODO
	return null;
    }
	
    public Exp visit(Absyn.BlockStmt b)
    {
	// TODO
	return null;
    }

    public Exp visit(Absyn.IfStmt s)
    {
	// TODO
	return null;
    }

    public Exp visit(Absyn.WhileStmt s)
    {
	// TODO
	return null;
    }

    private Exp Ex(int op, Absyn.BinOpExpr e) 
    {
        Exp e1 = e.e1.accept(this);
		Exp e2 = e.e2.accept(this);
		return new Ex(BINOP(op, e1.unEx(), e2.unEx()));
    }

    public Exp visit(Absyn.AddExpr e) { return Ex(Tree.BINOP.PLUS, e); }
    public Exp visit(Absyn.DivExpr e) { return Ex(Tree.BINOP.DIV, e); }
    public Exp visit(Absyn.MulExpr e) { return Ex(Tree.BINOP.MUL, e); }
    public Exp visit(Absyn.SubExpr e) { return Ex(Tree.BINOP.MINUS, e); }

    public Exp visit(Absyn.NegExpr e)
    {
	return new Ex(BINOP(Tree.BINOP.MINUS, 
			    CONST(0), e.e1.accept(this).unEx()));
    }

    public Exp visit(Absyn.AndExpr e) 
    {
        Exp e1 = e.e1.accept(this);
	Exp e2 = e.e2.accept(this);
	// Short-circuit Boolean AND, with false == 0.
	return new IfThenElseExp(e1, e2, new Ex(CONST(0)));
    }

    public Exp visit(Absyn.OrExpr e)
    {
	// TODO
	return null;
    }

    private Exp RelCx(int op, Absyn.BinOpExpr e)
    {
        Exp e1 = e.e1.accept(this);
	Exp e2 = e.e2.accept(this);
	return new RelCx(op, e1.unEx(), e2.unEx());
    }

    public Exp visit(Absyn.GreaterExpr e)  { return RelCx(Tree.CJUMP.GT, e); }
    public Exp visit(Absyn.LesserExpr e)   { return RelCx(Tree.CJUMP.LT, e); }
    public Exp visit(Absyn.EqualExpr e)    { return RelCx(Tree.CJUMP.EQ, e); }
    public Exp visit(Absyn.NotEqExpr e)    { return RelCx(Tree.CJUMP.NE, e); }

    public Exp visit(Absyn.NotExpr e)
    {
        Exp exp = e.e1.accept(this);
        return new IfThenElseExp(exp, new Ex(CONST(0)), new Ex(CONST(1)));
    }

    public Exp visit(Absyn.NewArrayExpr e) 
    {
	// TODO
	return null;
    }

    public Exp visit(Absyn.XinuCallStmt s)
    {
	// TODO
	return null;
    }

    public Exp visit(Absyn.XinuCallExpr e)
    {
	// TODO
	return null;
    }

    public Exp visit(Absyn.CallExpr e) 
    {
	// TODO
	return null;
    }

    public Exp visit(Absyn.FalseExpr e)
    {   return new Ex(CONST(0));  }

    public Exp visit(Absyn.IntegerLiteral e)    
    {   return new Ex(CONST(e.value));   }

    public Exp visit(Absyn.NullExpr e)
    {   return new Ex(CONST(0));   }

    public Exp visit (Absyn.NewObjectExpr e) 
    {
    	// TODO
	return null;
    }
    
    private HashMap<String, Label> strings = new HashMap<String, Label>();

    public Exp visit(Absyn.StringLiteral e) 
    {
	String result = e.value.intern();
	Label lab = strings.get(u);
	if (lab != null) return new Ex(NAME(lab));
	lab = new Label();
	strings.put(result, lab);
	
	DataFrag frag = new DataFrag(frame.string(lab, result.intern()));
	frags.add(frag);
	return new Ex(NAME(lab));
    }

    public Exp visit(Absyn.ThisExpr e) 
    {
    	// TODO
	return null;
    }
    
    public Exp visit(Absyn.TrueExpr e)
    {   return new Ex(CONST(1));   }

    public Exp visit(Absyn.FieldExpr e) 
    {
    	// TODO
	return null;
    }

    public Exp visit(Absyn.IdentifierExpr e) 
    {
    	// TODO
	return null;
    }

    public Exp visit(Absyn.ArrayExpr e)
    {
    	// TODO
	return null;
    }
}
