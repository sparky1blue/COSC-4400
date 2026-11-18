/* Copyright (C) 1997-2003, Purdue Research Foundation of Purdue University.
 * All rights reserved.  */
package Translate;
import Temp.Temp;
import Temp.Label;
import Tree.*;
import java.util.List;

/**
 * Represents an If-Then-Else Expression, a special case that can be 
 * an expression or statement depending on it's use.
 */

public class IfThenElseExp extends Exp {
    Exp cond, trueExp, falseExp;

    private static Tree.Exp CONST(int value)  { return new Tree.CONST(value); }
    private static Tree.Exp NAME(Label label) {	return new Tree.NAME(label); }
    private static Tree.Exp TEMP(Temp temp)   {	return new Tree.TEMP(temp); }
    private static Tree.Exp BINOP(int binop, Tree.Exp left, Tree.Exp right) 
    {   return new Tree.BINOP(binop, left, right);   }
    private static Tree.Exp MEM(Tree.Exp exp) {	return new Tree.MEM(exp); }
    private static Tree.Exp CALL(Tree.Exp func, List<Tree.Exp> args) 
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
	private static Integer constValue(Exp e) {
		if (e instanceof Ex && ((Ex) e).exp instanceof Tree.CONST) {
			return ((Tree.CONST) ((Ex) e).exp).value;
		}
		return null;
	}

    IfThenElseExp(Exp cExp, Exp tExp, Exp fExp) {
	cond = cExp; 
	trueExp = tExp; 
	falseExp = fExp;
    }

    Tree.Stm unCx(Label tt, Label ff) {
		Integer tVal = constValue(trueExp);
		Integer fVal = constValue(falseExp);

		
		if (tVal != null && fVal != null) {
			if (tVal != 0 && fVal == 0) return cond.unCx(tt, ff);
			if (tVal == 0 && fVal != 0) return cond.unCx(ff, tt);
		}

		
		if (tVal == null && fVal != null && fVal == 0) {
			Label z = new Label();
			return SEQ(cond.unCx(z, ff),
					SEQ(LABEL(z), trueExp.unCx(tt, ff)));
		}

		
		if (fVal == null && tVal != null && tVal != 0) {
			Label z = new Label();
			return SEQ(cond.unCx(tt, z),
					SEQ(LABEL(z), falseExp.unCx(tt, ff)));
		}

		Label t = new Label();
		Label f = new Label();
		return SEQ(cond.unCx(t, f),
				SEQ(SEQ(LABEL(t), trueExp.unCx(tt, ff)),
					SEQ(LABEL(f), falseExp.unCx(tt, ff))));
	}

    Tree.Exp unEx() {
	// TODO: Check for optimizations
        Tree.Exp tExp = trueExp.unEx();
	Tree.Exp fExp = falseExp.unEx();
	Temp r = new Temp();
	Label t = new Label();
	Label f = new Label();
	Label join = new Label();
	return ESEQ(SEQ(SEQ(cond.unCx(t, f),
			    SEQ(SEQ(LABEL(t),
				    SEQ(MOVE(TEMP(r), tExp),
					JUMP(join))),
				SEQ(LABEL(f),
				    SEQ(MOVE(TEMP(r), fExp),
					JUMP(join))))),
			LABEL(join)),
		    TEMP(r));
    }

    Tree.Stm unNx() {
	// TODO: Check for optimizations
        Tree.Stm tStm = trueExp.unNx();
        Tree.Stm fStm = falseExp.unNx();
	Label t, f, join;
	t = new Label();
	f = new Label();
	join = new Label();
	tStm = SEQ(SEQ(LABEL(t), tStm), JUMP(join));
	fStm = SEQ(SEQ(LABEL(f), fStm), JUMP(join));
	return SEQ(SEQ(cond.unCx(t, f), SEQ(tStm, fStm)), LABEL(join));
    }
}
