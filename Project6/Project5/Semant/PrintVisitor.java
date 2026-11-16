/* Copyright (C) 2007, Marquette University.  All rights reserved. */
package Semant;

import Absyn.AddExpr;
import Absyn.AndExpr;
import Absyn.ArrayExpr;
import Absyn.ArrayLength;
import Absyn.ArrayType;
import Absyn.AssignStmt;
import Absyn.BlockStmt;
import Absyn.BooleanType;
import Absyn.CallExpr;
import Absyn.ClassDecl;
import Absyn.DivExpr;
import Absyn.EqualExpr;
import Absyn.FalseExpr;
import Absyn.FieldExpr;
import Absyn.Formal;
import Absyn.GreaterExpr;
import Absyn.IdentifierExpr;
import Absyn.IdentifierType;
import Absyn.IfStmt;
import Absyn.IntegerLiteral;
import Absyn.IntegerType;
import Absyn.LesserExpr;
import Absyn.MethodDecl;
import Absyn.MulExpr;
import Absyn.NegExpr;
import Absyn.NewArrayExpr;
import Absyn.NewObjectExpr;
import Absyn.NotEqExpr;
import Absyn.NotExpr;
import Absyn.NullExpr;
import Absyn.Program;
import Absyn.StringLiteral;
import Absyn.SubExpr;
import Absyn.ThisExpr;
import Absyn.ThreadDecl;
import Absyn.TrueExpr;
import Absyn.VarDecl;
import Absyn.Visitable;
import Absyn.VoidDecl;
import Absyn.WhileStmt;
import Absyn.XinuCallExpr;
import Absyn.XinuCallStmt;
import Types.ARRAY;
import Types.BOOLEAN;
import Types.CLASS;
import Types.FIELD;
import Types.FUNCTION;
import Types.INT;
import Types.NIL;
import Types.OBJECT;
import Types.RECORD;
import Types.STRING;
import Types.VOID;
import java.io.PrintWriter;



public class PrintVisitor implements Absyn.Visitor, Types.Visitor
{
    PrintWriter out;
    public int indentCount = 0;

	public java.util.Map<Object,Object> notes = null;

	private void annotate(Object node)
	{
		if (notes == null) return;
		Object t = notes.get(node);
		if (t == null) return;
		indent();
		if (t instanceof Types.Type) ((Types.Type) t).accept(this);
		else out.print(t);
	}

    public PrintVisitor(PrintWriter out)
    {
		this.out = out;
    }

    public PrintVisitor()
    {
		this.out = new PrintWriter(System.out);
    }

    private void indent()
    {
		out.print('\n');
		for(int i = 0; i < indentCount; i++)
	    { out.print(' '); }
    }

 

    public void visit(Program ast)
    {
		out.print("Program(");
		indentCount++;
		visit(ast.classes);
		indentCount--;
		out.println(")");
		out.flush();
    }

    public void visit(java.util.AbstractList list)
    {
		if (null == list)
	    {
			indent();
			out.print("null");
			return;
	    }
		indent();
		out.print("AbstractList(");
		indentCount++;
		for (Object o : list)
	    {
			if (null == o)
		    {   indent();   out.print("null");   }
			else
		    {   ((Visitable)o).accept(this);    }	
	    }
		out.print(")");
		indentCount--;
    }

    public void visit(ClassDecl ast)
    {
		indent();
		out.print("ClassDecl(");
		indentCount++;
		out.print(ast.name + " " + ast.parent); 
		visit(ast.fields);
		visit(ast.methods);
		annotate(ast);
		indentCount--;
		out.print(")");
    }

    public void visit(MethodDecl ast)
    {
		indent();
		out.print("MethodDecl(");
		indentCount++;
		if (null != ast.returnType)
	    { ast.returnType.accept(this); }
		else
	    { out.print("public_static_void"); }
		if (ast.synced) { out.print(" synchronized"); }
		out.print(" " + ast.name);
		visit(ast.params);
        visit(ast.locals);
		visit(ast.stmts);
		if (null == ast.returnVal)
	    {   indent();   out.print("null");   }
		else
	    {   ast.returnVal.accept(this);   }
		annotate(ast);
		indentCount--;
		out.print(")");
    }
    
    public void visit(Formal ast)
    {
		indent();
		out.print("Formal(");
		ast.type.accept(this);
		out.print(" " + ast.name + ")");
    }

    public void visit(IdentifierType ast)
    {
		out.print("IdentifierType(" + ast.id + ")");
    }

    public void visit(VarDecl ast){
		indent();
		out.print("VarDecl(");
		ast.type.accept(this);
		out.print(" " + ast.name);
		boolean typed = notes != null && notes.get(ast) != null;  
		if (typed) indentCount++;                               
		if (null == ast.init)
		{   out.print(" null");   }
		else
		{   ast.init.accept(this);   }
		if (typed)                                 
		{
			out.print(" ");
			annotate(ast);
			indentCount--;
		}
		out.print(")");
	}

    public void visit(XinuCallStmt ast)
    {
		indent();
		out.print("XinuCallStmt(");
		indentCount++;
		out.print(ast.method);
		visit(ast.args);
		indentCount--;
		out.print(")");
    }

    public void visit(IntegerLiteral ast)
    {
		indent();
		out.print("IntegerLiteral(" + ast.value + ")");
    }

    public void visit(StringLiteral ast)
    {
		indent();
		out.print("StringLiteral(" + ast.value + ")");
    }
	public void visit(IntegerType ast)
    {
		out.print("IntegerType");
    }

    public void visit(BooleanType ast)
    {
		out.print("BooleanType");
    }

    public void visit(ArrayType ast)
    {
		out.print("ArrayType(");
		ast.base.accept(this);
		out.print(")");
    }
	public void visit(IdentifierExpr ast){
		indent();
		out.print("IdentifierExpr(" + ast.name + ")");
    }
	public void visit(TrueExpr ast){
		indent();
		out.print("TrueExpr");
	}
	public void visit(FalseExpr ast){
		indent();
		out.print("FalseExpr");
	}
	public void visit(ThisExpr ast){
		indent();
		out.print("ThisExpr");
	}
	public void visit(NotExpr ast){
		indent();
		out.print("NotExpr(");
		indentCount++;
		ast.expr.accept(this);
		indentCount--;
		out.print(")");
	}
	public void visit(MulExpr ast){
		indent();
		out.print("MulExpr(");
		indentCount++;
		ast.first.accept(this);
		ast.second.accept(this);
		indentCount--;
		out.print(")");
	}
	public void visit(AddExpr ast){
		indent();
		out.print("AddExpr(");
		indentCount++;
		ast.first.accept(this);
		ast.second.accept(this);
		indentCount--;
		out.print(")");
	}
	public void visit(SubExpr ast){
		indent();
		out.print("SubExpr(");
		indentCount++;
		ast.first.accept(this);
		ast.second.accept(this);
		indentCount--;
		out.print(")");
	}
	public void visit(LesserExpr ast){
		indent();
		out.print("LesserExpr(");
		indentCount++;
		ast.first.accept(this);
		ast.second.accept(this);
		indentCount--;
		out.print(")");
	}
	public void visit(AndExpr ast){
		indent();
		out.print("AndExpr(");
		indentCount++;
		ast.first.accept(this);
		ast.second.accept(this);
		indentCount--;
		out.print(")");
	}
	public void visit(NullExpr ast){
		indent();
		out.print("NullExpr");
    }

    public void visit(DivExpr ast){
		indent();
		out.print("DivExpr(");
		indentCount++;
		ast.left.accept(this);
		ast.right.accept(this);
		indentCount--;
		out.print(")");
    }

    public void visit(GreaterExpr ast){
		indent();
		out.print("GreaterExpr(");
		indentCount++;
		ast.left.accept(this);
		ast.right.accept(this);
		indentCount--;
		out.print(")");
    }

    public void visit(EqualExpr ast){
		indent();
		out.print("EqualExpr(");
		indentCount++;
		ast.left.accept(this);
		ast.right.accept(this);
		indentCount--;
		out.print(")");
    }

    public void visit(NotEqExpr ast){
		indent();
		out.print("NotEqExpr(");
		indentCount++;
		ast.left.accept(this);
		ast.right.accept(this);
		indentCount--;
		out.print(")");
    }

    public void visit(NegExpr ast){
		indent();
		out.print("NegExpr(");
		indentCount++;
		ast.expr.accept(this);
		indentCount--;
		out.print(")");
    }
	public void visit(ArrayExpr ast){
		indent();
		out.print("ArrayExpr(");
		indentCount++;
		ast.array.accept(this);
		ast.index.accept(this);
		indentCount--;
		out.print(")");
	}
	public void visit(CallExpr ast){
		indent();
		out.print("CallExpr(");
		indentCount++;
		ast.object.accept(this);
		indent();
		out.print(ast.methString);
		visit(ast.args);
		annotate(ast);
		indentCount--;
		out.print(")");
	}
	public void visit(NewArrayExpr ast){
		indent();
		out.print("NewArrayExpr(");
		ast.baseType.accept(this);
		indentCount++;
		visit(ast.sizes);
		indentCount--;
		out.print(")");
	}
	public void visit(NewObjectExpr ast){
		indent();
		out.print("NewObjectExpr(");
		ast.claName.accept(this);
		out.print(")");
    }
	public void visit(ArrayLength ast){
		indent();
		out.print("ArrayLength(");
		indentCount++;
		ast.array.accept(this);
		indentCount--;
		out.print(")");
	}
	public void visit(BlockStmt ast){
		indent();
		out.print("BlockStmt(");
		indentCount++;
		visit(ast.stmts);
		indentCount--;
		out.print(")");
    }
	public void visit(AssignStmt ast){
		indent();
		out.print("AssignStmt(");
		indentCount++;
		ast.target.accept(this);
		ast.value.accept(this);
		indentCount--;
		out.print(")");
    }
	public void visit(IfStmt ast){
		indent();
		out.print("IfStmt(");
		indentCount++;
		ast.cond.accept(this);
		ast.thenStmt.accept(this);
		if (null == ast.elseStmt){   
			indent();   
			out.print("null");   
		}
		else{ 
			ast.elseStmt.accept(this); 
		}
		indentCount--;
		out.print(")");
    }
	public void visit(WhileStmt ast){
		indent();
		out.print("WhileStmt(");
		indentCount++;
		ast.cond.accept(this);
		ast.body.accept(this);
		indentCount--;
		out.print(")");
    }
	public void visit(ThreadDecl ast)
    {
		indent();
		out.print("ThreadDecl(");
		indentCount++;
		out.print(ast.name + " " + ast.parent);
		visit(ast.fields);
		visit(ast.methods);
		annotate(ast);
		indentCount--;
		out.print(")");
    }

    public void visit(VoidDecl ast)
    {
		indent();
		out.print("VoidDecl(");
		indentCount++;
		out.print(ast.name);
		visit(ast.params);
		visit(ast.stmts);
		annotate(ast);
		indentCount--;
		out.print(")");
    }
	public void visit(FieldExpr ast){
		indent();
		out.print("FieldExpr(");
		indentCount++;
		ast.object.accept(this);
		indent();
		out.print(ast.field);
		annotate(ast);
		indentCount--;
		out.print(")");
    }
	public void visit(XinuCallExpr ast){
		indent();
		out.print("XinuCallExpr(");
		indentCount++;
		out.print(ast.method);
		visit(ast.args);
		indentCount--;
		out.print(")");
    }

    public void visit(Types.Type t)
    {
		t.accept(this);
    }

    public void visit(ARRAY a)
    {
		out.print("ARRAY(");
		indentCount++;
		indent();
		visit(a.element);
		indentCount--;
		out.print(")");
    }

    public void visit(BOOLEAN b) { out.print("BOOLEAN"); }

    
    public void visit(CLASS c)
    {
		out.print("CLASS(" + c.name);
		indentCount++;
		indent();
		out.print(c.parent == null ? "null" : c.parent.name);
		indent();
		visit(c.methods);
		indent();
		visit(c.fields);
		indent();
		out.print("OBJECT(" + c.name);
		indentCount++;
		indent();
		visit(c.instance.methods);
		indent();
		visit(c.instance.fields);
		indentCount--;
		out.print(")");
		indentCount--;
		out.print(")");
    }

    public void visit(FIELD f)
    {
		out.print("FIELD(" + f.index + " " + f.name);
		indentCount++;
		indent();
		visit(f.type);
		indentCount--;
		out.print(")");
    }

    public void visit(FUNCTION f)
    {
		out.print("FUNCTION(" + f.name);
		indentCount++;
		indent();
		visit(f.self);
		indent();
		visit(f.formals);
		indent();
		visit(f.result);
		indentCount--;
		out.print(")");
    }

    public void visit(INT i) { out.print("INT"); }

    public void visit(NIL n) { out.print("NIL"); }

   
    public void visit(OBJECT o) { out.print("OBJECT(" + o.myClass.name + ")"); }

    public void visit(RECORD r)
    {
		if (r.size() == 0)
	    {
			out.print("RECORD()");
			return;
	    }
		out.print("RECORD(");
		indentCount++;
		for (FIELD f : r)
	    {
			indent();
			visit(f);
	    }
		indentCount--;
		out.print(")");
    }

    public void visit(STRING s) { out.print("STRING"); }

    public void visit(VOID v) { out.print("VOID"); }
}