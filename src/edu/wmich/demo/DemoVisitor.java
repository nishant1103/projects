package edu.wmich.demo;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.ExpressionStatement;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.InfixExpression;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;
import org.eclipse.jdt.core.dom.WhileStatement;

/**
 * A visitor for abstract syntax trees.
 * 
 * for more information, please refer to 
 * <a href="http://help.eclipse.org/indigo/topic/org.eclipse.jdt.doc.isv/reference/api/org/eclipse/jdt/core/dom/ASTVisitor.html">ASTVisitor</a>
 * 
 * @author Chang
 * @author nishantgupta
 *
 */
public class DemoVisitor extends ASTVisitor {
	
	List<Vector<String>> stats = new ArrayList<Vector<String>>();
	int block = 0,statm = 0;
	
	public boolean visit(IfStatement node) {
		List<CBlock> cblock = Demo.blocks;
		statm++;
		System.out.println("Statement:" + statm + '\n' +  node.getExpression().toString());
		InfixExpression inf = (InfixExpression) node.getExpression();
		System.out.println("Use : " + inf.getLeftOperand().toString());
		System.out.println("Use : " + inf.getRightOperand().toString());
		Pair<String,String> p1 = new Pair(inf.getLeftOperand().toString(), "S" + statm);
		cblock.get(Demo.num_block).use.add(p1);
		Pair<String,String> p2 = new Pair(inf.getRightOperand().toString(), "S" + statm);
		cblock.get(Demo.num_block).use.add(p2);
		System.out.println("============ IF Statement() is visited ============" + statm);
		System.out.print(node.toString());
		System.out.println("=================================================");
		return super.visit(node);
	}
	
	
	public void endVisit(IfStatement node)
	{
		System.out.println("============ IF Statement is END visited ============");
		System.out.println("=================================================");	
	}
	
	public boolean visit(WhileStatement node) {
		statm++;
		System.out.println("Statement:" + statm + '\n' +  node.getExpression().toString());
		InfixExpression inf = (InfixExpression) node.getExpression();
		System.out.println("Use : " + inf.getLeftOperand().toString());
		System.out.println("Use : " + inf.getRightOperand().toString());
		System.out.println("============ While Statement is visited ============" + statm);
		System.out.print(node.toString());
		System.out.println("=================================================");
		return super.visit(node);
		// return false;   // to see what happens when false is returned.
	}
	//
	public void endVisit(WhileStatement node) {
		//block;
	}
	
	public boolean visit(MethodDeclaration node) {
		if( node.getName().toString().equals("main"))
		{
			System.out.println("========= METHOD DECLARATION is visited =========");
			System.out.print(node.toString());
			System.out.println("=================================================");
			return super.visit(node);
		}else
			return false;
	}
	
	public boolean visit(PackageDeclaration node) {
		System.out.println("======== PACKAGE DECLARATION is visited =========");
		System.out.print(node.toString());
		System.out.println("=================================================");
		return super.visit(node);
		//return true;
	}
	
	public boolean visit(VariableDeclarationStatement node) {
		System.out.println("======== VARIABLE DECLARATION is visited ========" + statm);
		System.out.print(node.toString());
		System.out.println("=================================================");
		return super.visit(node);
	}

	public boolean visit(InfixExpression node) {
		System.out.println("Infix Block No : " + Demo.num_block);
		statm++;
		String s1 = node.getLeftOperand().toString(),s2 = node.getRightOperand().toString();
		if(!Character.isDigit(s1.charAt(0)))
			addUse(s1, "S" + statm);
		if(!Character.isDigit(s2.charAt(0)))
			addUse(s2, "S" + statm);
		System.out.println("Infix Node Visited");
		System.out.println("Use : " + node.getLeftOperand().toString());
		System.out.println("Use : " + node.getRightOperand().toString());
		return super.visit(node);
	}

	public boolean visit(ExpressionStatement node) {
		int t = Demo.num_block;
		System.out.println(" Expression Block No : " + Demo.num_block);
		statm++;
		List<CBlock> cblock = Demo.blocks;
		int size = cblock.size();
		System.out.println("Statement:" + statm + '\n' +  node.getExpression().toString());
		InfixExpression inf;
		if (node.getExpression().getNodeType() == ExpressionStatement.ASSIGNMENT) {
			System.out.println("======== Assignment EXPRESSION STATEMENT is visited ========" + statm);
			Assignment as = (Assignment) node.getExpression();
		
			Expression left = as.getLeftHandSide();
			if(!Character.isDigit(as.getLeftHandSide().toString().charAt(0)))
				addDef(as.getLeftHandSide().toString(),"S" + statm);			
			System.out.println("def : " + left.toString());
			
			Expression right = as.getRightHandSide();
			if(right.getNodeType() != ExpressionStatement.INFIX_EXPRESSION){
				System.out.println("Use : " + right.toString());
				if(!Character.isDigit(right.toString().charAt(0)))
					addUse(right.toString(), "S" + statm);
			}
			
			else{
				inf = (InfixExpression) right;
				System.out.println("Use : " + inf.getLeftOperand().toString());
				System.out.println("Use : " + inf.getRightOperand().toString());
				if(!Character.isDigit(inf.getLeftOperand().toString().charAt(0)))
					addUse(inf.getLeftOperand().toString(), "S" + statm);
				if(!Character.isDigit(inf.getRightOperand().toString().charAt(0)))
					addUse(inf.getRightOperand().toString(), "S" + statm);
			}
		}
		System.out.println("======== EXPRESSION STATEMENT is visited ========");
		System.out.print(node.toString());
		System.out.println("=================================================");
		return false;
	}
	
	static void addDef(String s1,String s2) {
		List<CBlock> cblock = Demo.blocks;
		int size = cblock.size();	
		if(Demo.num_block > (size - 1))
			cblock.add(new CBlock());
		cblock.get(Demo.num_block).defHelp.add(new Pair(s1 , s2));
		System.out.println("Adding" + "(" + s1 + ")" + "in DefHelp of Block No" + Demo.num_block);		
	}

	/** addUse adds the use for the following blocks
	*/
	static void addUse(String s1, String s2) {
		List<CBlock> cblock = Demo.blocks;
		int size = cblock.size();	
		if(Demo.num_block > (size - 1))
			cblock.add(new CBlock());
		cblock.get(Demo.num_block).use.add(new Pair(s1, s2));
		System.out.println("Adding" + "(" + s1 + "," + s2 + ")" + "in Block No" + Demo.num_block);		
	}
}
