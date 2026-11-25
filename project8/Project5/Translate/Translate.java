/* Copyright (C) 1997-2003, Purdue Research Foundation of Purdue University.
 * All rights reserved.  */
package Translate;

/**
 * Implements translation of abstract syntax expressions
 * into intermediate trees.
 */
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import Temp.Temp;
import Temp.Label;
import Symbol.Symbol;
import Symbol.Table;
import Types.CLASS;

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
    private Absyn.ClassDecl currentClass;
    private Frame.Access thisAccess;
    public java.util.Map<Object, Object> notes;

    private Tree.Stm stm(Absyn.Absyn node)
    {
        Exp e = node.accept(this);
        if (e == null) return null;
        return e.unNx();
    }

    private HashMap<String, CLASS>  classEnv  = new HashMap<String, CLASS>();
    private HashMap<Symbol, Label>  methodEnv = new HashMap<Symbol, Label>();
    private HashMap<String, Label>  staticEnv = new HashMap<String, Label>();
    private HashMap<String, Label>  vtableEnv = new HashMap<String, Label>();
    private HashMap<String, Absyn.ClassDecl> classDecls = new HashMap<String, Absyn.ClassDecl>();
    private Table<Frame.Access>     varEnv    = new Table<Frame.Access>();
	
    public Exp visit(Absyn.Program n)
    {
        for (Absyn.ClassDecl c : n.classes) {
            classDecls.put(c.name, c);
        }

        for (Absyn.ClassDecl c : n.classes) {
            LinkedList<Label> entries = new LinkedList<Label>();
            for (String method : vtableNames(c.name)) {
                entries.add(new Label(implementor(c.name, method) + "__" + method));
            }
            Label vtable = new Label(c.name + "__vtable");
            vtableEnv.put(c.name, vtable);
            frags.add(new DataFrag(frame.record(vtable, entries)));
        }

        for (Absyn.ClassDecl c : n.classes) {
            c.accept(this);
        }
        return null;
    }
    
    public Exp visit(Absyn.ClassDecl c) 
    {
        currentClass = c;
        for (Absyn.MethodDecl m : c.methods) {
            m.accept(this);
        }
        return null;
    }

    public Exp visit(Absyn.ThreadDecl c) 
    {
        return visit((Absyn.ClassDecl) c);
    }

    public Exp visit(Absyn.MethodDecl m)
    {
        String name = currentClass.name + "__" + m.name;
        if (m.name.equals("main")) name = "main";

        Frame.Frame outer = frame;
        frame = outer.newFrame(Symbol.symbol(name));
        varEnv.beginScope();

        thisAccess = frame.allocFormal();
        for (Absyn.Formal f : m.params) {
            f.accept(this);
        }

        Tree.Stm body = null;
        for (Absyn.VarDecl v : m.locals) {
            body = SEQ(body, stm(v));
        }
        for (Absyn.Stmt s : m.stmts) {
            body = SEQ(body, stm(s));
        }

        Tree.Exp result = CONST(0);
        if (m.returnVal != null) {
            result = m.returnVal.accept(this).unEx();
        }
        body = SEQ(body, MOVE(TEMP(frame.RV()), result));

        frags.add(new ProcFrag(body, frame));
        varEnv.endScope();
        frame = outer;
        return null;
    }

    public Exp visit(Absyn.VoidDecl m) 
    {
        return visit((Absyn.MethodDecl) m);
    }

    public Exp visit(Absyn.Formal f) 
    {
        Frame.Access a = frame.allocFormal();
        varEnv.put(Symbol.symbol(f.name), a);
        return null;
    }

    public Exp visit(Absyn.VarDecl v) 
    {
        Frame.Access a = frame.allocLocal();
        varEnv.put(Symbol.symbol(v.name), a);
        Tree.Exp init = CONST(0);
        if (v.init != null) {
            init = v.init.accept(this).unEx();
        }
        return new Nx(MOVE(a.exp(frame), init));
    }

    public Exp visit(Absyn.BooleanType  t)   { return new Nx(null); }
    public Exp visit(Absyn.IntegerType  t)   { return new Nx(null); }
    public Exp visit(Absyn.ArrayType t)      { return new Nx(null); }
    public Exp visit(Absyn.IdentifierType t) { return new Nx(null); }

    public Exp visit(Absyn.AssignStmt s) 
    {
        Tree.Exp target = s.target.accept(this).unEx();
        Tree.Exp value = s.value.accept(this).unEx();
        return new Nx(MOVE(target, value));
    }
	
    public Exp visit(Absyn.BlockStmt b)
    {
        Tree.Stm body = null;
        for (Absyn.Stmt s : b.stmts) {
            body = SEQ(body, stm(s));
        }
        return new Nx(body);
    }

    public Exp visit(Absyn.IfStmt s)
    {
        Exp cond = s.cond.accept(this);
        Exp thenExp = s.thenStmt.accept(this);
        Exp elseExp = new Nx(null);
        if (s.elseStmt != null) {
            elseExp = s.elseStmt.accept(this);
        }
        return new IfThenElseExp(cond, thenExp, elseExp);
    }

    public Exp visit(Absyn.WhileStmt s)
    {
        Exp cond = s.cond.accept(this);
        Tree.Stm body = stm(s.body);
        Label done = new Label();
        Label test = new Label();
        Label top = new Label();
        return new Nx(SEQ(SEQ(SEQ(LABEL(test), cond.unCx(top, done)),
                            SEQ(SEQ(LABEL(top), body), JUMP(test))),
                        LABEL(done)));
    }

    private Exp Ex(int op, Absyn.Expr left, Absyn.Expr right)
    {
        Exp e1 = left.accept(this);
        Exp e2 = right.accept(this);
        return new Ex(BINOP(op, e1.unEx(), e2.unEx()));
    }

    public Exp visit(Absyn.AddExpr e) { return Ex(Tree.BINOP.PLUS, e.first, e.second); }
    public Exp visit(Absyn.DivExpr e) { return Ex(Tree.BINOP.DIV, e.left, e.right); }
    public Exp visit(Absyn.MulExpr e) { return Ex(Tree.BINOP.MUL, e.first, e.second); }
    public Exp visit(Absyn.SubExpr e) { return Ex(Tree.BINOP.MINUS, e.first, e.second); }

    public Exp visit(Absyn.NegExpr e)
    {
        return new Ex(BINOP(Tree.BINOP.MINUS, 
                            CONST(0), e.expr.accept(this).unEx()));
    }

    public Exp visit(Absyn.AndExpr e) 
    {
        Exp e1 = e.first.accept(this);
        Exp e2 = e.second.accept(this);
        // Short-circuit Boolean AND, with false == 0.
        return new IfThenElseExp(e1, e2, new Ex(CONST(0)));
    }

    public Exp visit(Absyn.OrExpr e)
    {
        Exp e1 = e.first.accept(this);
        Exp e2 = e.second.accept(this);
        // Short-circuit Boolean OR, with true == 1.
        return new IfThenElseExp(e1, new Ex(CONST(1)), e2);
    }

    private Exp RelCx(int op, Absyn.Expr left, Absyn.Expr right)
    {
        Exp e1 = left.accept(this);
        Exp e2 = right.accept(this);
        return new RelCx(op, e1.unEx(), e2.unEx());
    }

    public Exp visit(Absyn.GreaterExpr e)  { return RelCx(Tree.CJUMP.GT, e.left, e.right); }
    public Exp visit(Absyn.LesserExpr e)   { return RelCx(Tree.CJUMP.LT, e.first, e.second); }
    public Exp visit(Absyn.EqualExpr e)    { return RelCx(Tree.CJUMP.EQ, e.left, e.right); }
    public Exp visit(Absyn.NotEqExpr e)    { return RelCx(Tree.CJUMP.NE, e.left, e.right); }

    public Exp visit(Absyn.NotExpr e)
    {
        Exp exp = e.expr.accept(this);
        return new IfThenElseExp(exp, new Ex(CONST(0)), new Ex(CONST(1)));
    }

    public Exp visit(Absyn.NewArrayExpr e) 
    {
        Tree.Exp size = e.sizes.getFirst().accept(this).unEx();
        Temp t = new Temp();
        LinkedList<Tree.Exp> args = new LinkedList<Tree.Exp>();
        args.add(TEMP(t));
        args.add(TEMP(t));
        return new Ex(ESEQ(MOVE(TEMP(t), size),
                        CALL(NAME(new Label("_new")), args)));
    }

    public Exp visit(Absyn.XinuCallStmt s)
    {
        LinkedList<Tree.Exp> args = new LinkedList<Tree.Exp>();
        for (Absyn.Expr a : s.args) {
            args.add(a.accept(this).unEx());
        }
        return new Nx(EXP(CALL(NAME(new Label("_" + s.method)), args)));
    }

    public Exp visit(Absyn.XinuCallExpr e)
    {
        LinkedList<Tree.Exp> args = new LinkedList<Tree.Exp>();
        for (Absyn.Expr a : e.args) {
            args.add(a.accept(this).unEx());
        }
        return new Ex(CALL(NAME(new Label("_" + e.method)), args));
    }

    public Exp visit(Absyn.CallExpr e) 
    {
        Tree.Exp object = e.object.accept(this).unEx();

        LinkedList<Tree.Exp> args = new LinkedList<Tree.Exp>();
        for (Absyn.Expr a : e.args) {
            args.add(a.accept(this).unEx());
        }

        Temp t = new Temp();
        Label ok = new Label();
        Tree.Stm check = SEQ(MOVE(TEMP(t), object),
                            SEQ(CJUMP(Tree.CJUMP.EQ, TEMP(t), CONST(0),
                                    frame.badPtr(), ok),
                                LABEL(ok)));

        int index = (Integer) notes.get(e);
        int w = frame.wordSize();
        Tree.Exp vtable = MEM(BINOP(Tree.BINOP.PLUS, TEMP(t), CONST(-w)));
        Tree.Exp method = MEM(BINOP(Tree.BINOP.PLUS, vtable, CONST(index * w)));

        args.addFirst(TEMP(t));
        return new Ex(ESEQ(check, CALL(method, args)));
    }

    public Exp visit(Absyn.FalseExpr e)
    {   return new Ex(CONST(0));  }

    public Exp visit(Absyn.IntegerLiteral e)    
    {   return new Ex(CONST(e.value));   }

    public Exp visit(Absyn.NullExpr e)
    {   return new Ex(CONST(0));   }

    public Exp visit (Absyn.NewObjectExpr e) 
    {
        String className = ((Absyn.IdentifierType) e.claName).id;
        LinkedList<Tree.Exp> args = new LinkedList<Tree.Exp>();
        args.add(CONST(fieldCount(className)));
        args.add(NAME(vtableEnv.get(className)));
        return new Ex(CALL(NAME(new Label("_new")), args));
    }
    
    private HashMap<String, Label> strings = new HashMap<String, Label>();

    public Exp visit(Absyn.StringLiteral e) 
    {
	String text = e.value;
    if (text.length() >= 2 && text.startsWith("\"") && text.endsWith("\"")) {
        text = text.substring(1, text.length() - 1);
    }
    String result = text.intern();
	Label lab = strings.get(result);
	if (lab != null) return new Ex(NAME(lab));
	lab = new Label();
	strings.put(result, lab);
	
	DataFrag frag = new DataFrag(frame.string(lab, result.intern()));
	frags.add(frag);
	return new Ex(NAME(lab));
    }

    public Exp visit(Absyn.ThisExpr e) 
    {
    	return new Ex(thisAccess.exp(frame));
    }
    
    public Exp visit(Absyn.TrueExpr e)
    {   return new Ex(CONST(1));   }

    public Exp visit(Absyn.FieldExpr e) 
    {
    	Tree.Exp object = e.object.accept(this).unEx();
        int index = (Integer) notes.get(e);
        return new Ex(fieldExp(object, index));
    }

    public Exp visit(Absyn.IdentifierExpr e) 
    {
    	Frame.Access a = varEnv.get(Symbol.symbol(e.name));
        if (a != null) {
            return new Ex(a.exp(frame));
        }
        int index = fieldIndex(currentClass.name, e.name);
        return new Ex(fieldExp(thisAccess.exp(frame), index));
    }

    public Exp visit(Absyn.ArrayExpr e)
    {
        Tree.Exp array = e.array.accept(this).unEx();
        Tree.Exp index = e.index.accept(this).unEx();
        Temp a = new Temp();
        Temp i = new Temp();
        Label ok1 = new Label();
        Label ok2 = new Label();
        Label ok3 = new Label();
        int w = frame.wordSize();

        Tree.Exp length = MEM(BINOP(Tree.BINOP.PLUS, TEMP(a), CONST(-w)));
        Tree.Stm checks =
            SEQ(MOVE(TEMP(a), array),
            SEQ(MOVE(TEMP(i), index),
            SEQ(CJUMP(Tree.CJUMP.EQ, TEMP(a), CONST(0), frame.badPtr(), ok1),
            SEQ(LABEL(ok1),
            SEQ(CJUMP(Tree.CJUMP.LT, TEMP(i), CONST(0), frame.badSub(), ok2),
            SEQ(LABEL(ok2),
                CJUMP(Tree.CJUMP.GE, TEMP(i), length, frame.badSub(), ok3)))))));

        Tree.Exp element = MEM(BINOP(Tree.BINOP.PLUS, TEMP(a),
                                    BINOP(Tree.BINOP.MUL, TEMP(i), CONST(w))));
        return new Ex(ESEQ(checks, ESEQ(LABEL(ok3), element)));
    }
    public Exp visit(Absyn.ArrayLength e)
    {
        return new Ex(fieldExp(e.array.accept(this).unEx(), -1));
    }

    private int fieldCount(String className)
    {
        int count = 0;
        while (className != null) {
            Absyn.ClassDecl c = classDecls.get(className);
            if (c == null) break;
            count += c.fields.size();
            className = c.parent;
        }
        return count;
    }
    private Tree.Exp fieldExp(Tree.Exp object, int index)
    {
        Temp t = new Temp();
        Label ok = new Label();
        return ESEQ(SEQ(MOVE(TEMP(t), object),
                        CJUMP(Tree.CJUMP.EQ, TEMP(t), CONST(0), frame.badPtr(), ok)),
                    ESEQ(LABEL(ok),
                        MEM(BINOP(Tree.BINOP.PLUS, TEMP(t),
                                CONST(index * frame.wordSize())))));
    }
    private int fieldIndex(String className, String field)
    {
        Absyn.ClassDecl c = classDecls.get(className);
        if (c == null) return -1;
        int i = fieldCount(c.parent);
        for (Absyn.VarDecl v : c.fields) {
            if (v.name.equals(field)) return i;
            i++;
        }
        return fieldIndex(c.parent, field);
    }

    private LinkedList<String> vtableNames(String className)
    {
        Absyn.ClassDecl c = classDecls.get(className);
        if (c == null) return new LinkedList<String>();
        LinkedList<String> names = vtableNames(c.parent);
        for (Absyn.MethodDecl m : c.methods) {
            if (!names.contains(m.name)) names.add(m.name);
        }
        return names;
    }

    private String implementor(String className, String method)
    {
        Absyn.ClassDecl c = classDecls.get(className);
        while (c != null) {
            for (Absyn.MethodDecl m : c.methods) {
                if (m.name.equals(method)) return c.name;
            }
            c = classDecls.get(c.parent);
        }
        return className;
    }

}
