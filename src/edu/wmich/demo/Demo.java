package edu.wmich.demo;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.ObjectInputStream.GetField;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.eclipse.core.internal.dtree.DeletedNode;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.text.edits.TextEdit;

import edu.wmich.cs661.pa2.BasicBlock;
import edu.wmich.cs661.pa2.ControlBlock;
import edu.wmich.cs661.pa2.CreateCFGVisitor;
import edu.wmich.cs661.pa2.INode;


/**
 * this class is used to retrieve abstract syntax tree from the given Java file.
 * 
 * The whole process is very simple
 * <ol>
 * <li>Reads the content from Java File;</li>
 * <li>Feeds content to ASTParser, which is the core class used to parse Java source code;</li>
 * <li>Gets abstract syntax tree;</li>
 * </ol>
 * 
 * After getting AST, it is simple to do following tasks:
 * <ol>
 * <li>Iterates the AST, visits the AST node by invoking visitors;</li>
 * <li>Modifies the AST, adds more AST nodes, removes existing AST nodes or changes the content of the AST nodes;</li>
 * </ol>
 * 
 * @author Chang
 *
 */
public class Demo {

private static DemoVisitor visitor;
private static List<String> visited;
public static int num_block;
public static List<CBlock> blocks;
public static Map<String,Integer> connection; 
private static int size;
/**
* reads the java file from given location and returns the file in String format
* @param path the absolute path contains the java file
* @return content of the java file
*/
public String readJavaFile(String path) {
	StringBuilder sourceCode = new StringBuilder();
	String NL = System.getProperty("line.separator");
	Scanner scanner = null;
	try {
		FileInputStream fis = new FileInputStream(path);
		scanner = new Scanner(fis);
		while (scanner.hasNextLine()) {
		sourceCode.append(scanner.nextLine() + NL);
		}
	} 
	catch (FileNotFoundException e) {
		e.printStackTrace();
		return null;
	} 
	finally {
		scanner.close();
	}
	return sourceCode.toString();
}


/**
* creates the parsed AST based on given source code in String format and return CompilationUnit
* @param javaSource
* @return
*/
public CompilationUnit parse(String javaSource) {
// JLS2: Java version up to and including J2SE 1.4
// JLS3: Java version up to and including J2SE 5 (aka 1.5)
// JLS4: Java version up to and including J2SE 7 (aka 1.7)
ASTParser parser = ASTParser.newParser(AST.JLS3);
/*
* because the whole Java file is parsed, it is called Compilation Unit in Eclipse;
* if you want to parse partial Java file, there are other choices:
* K_EXPRESSION, K_STATEMENTS, K_CLASS_BODY_DECLARATIONS,
* and the names are self-explaining.
*/
parser.setKind(ASTParser.K_COMPILATION_UNIT);
// feeds source code to compiler.
parser.setSource(javaSource.toCharArray());
// requests that the compiler should provide binding information for the AST nodes it creates
parser.setResolveBindings(true);
// creates AST from given source code and returns it
return (CompilationUnit)parser.createAST(null);
}


/**
* This method is manually to create an AST which is similar to the Test.java file.
* @return
*/

/**
* translates the AST into Java source code
* @param unit the given AST
* @return char array which holds the Java source code
*/
public char[] getContents(CompilationUnit unit) {
	char[] contents = null;
	try {
	Document doc = new Document();
	TextEdit edits = unit.rewrite(doc, null);
	edits.apply(doc);
	String sourceCode = doc.get();
	if (sourceCode != null) {
	contents = sourceCode.toCharArray();
	}
	} catch (BadLocationException e) {
	throw new RuntimeException(e);
	}
	return contents;
}


/**
* This main takes the java file as input, parse it, and use cfg visitor object  to set def b for each block.

*/

public static void main(String args[]) {
	Demo demo = new Demo();
	String curDir = new File("./").getAbsolutePath();
	String source = demo.readJavaFile(curDir+"/src/edu/wmich/demo/Test.java");
	visitor = new DemoVisitor();
	visited = new ArrayList<String>();
	blocks = new ArrayList<CBlock>();
	connection = new HashMap<String, Integer>();
	num_block = -1;
	// section 1, reads the Java file, parses it into AST
	// get AST
	CompilationUnit unit = demo.parse(source);
	// this is used for parsing. 
	// let visitor visit the given AST
	CreateCFGVisitor cfVisitor = new CreateCFGVisitor();
	unit.accept(cfVisitor);
	INode node_i = cfVisitor.get_Root();
	visitNode(node_i);
	//Setting DEF[B] for each block
	size = blocks.size();
	for(int i=0;i < size;i++){
		CBlock cb = blocks.get(i);
		Iterator<Pair<String,String>> its = cb.defHelp.iterator();
		while(its.hasNext()){
			addDef(its.next().getLeft(),i);
		}
	}
	print();
	DuChain du = new DuChain();
	
	return;
	}

/**
 * 
* @param string s integer n
* @return
*/


	static void addDef(String s,int n){
		CBlock blck = blocks.get(n);
		for(int i=0;i < size;i++)
		if(i != n){
			CBlock cb = blocks.get(i);
			Iterator<Pair<String,String>> it = cb.use.iterator();
			while(it.hasNext()){
				Pair<String,String> p = it.next();
				if(s.equals(p.getLeft())){
					//System.out.println("Adding to Block i : (" + p.getLeft() + "," + p.getRight() + ")");
					blck.def.add(p);
				}
			}
		}
	}

	

/**
 * Increases the block position 
* @param 
* @return
*/
	static void print(){
		int pos = -1;
		Iterator<CBlock> it = blocks.iterator();
		while(it.hasNext()){
			pos++;
			System.out.println("Block : " + pos);
			CBlock temp = it.next();
			List<Pair<String, String>> use = temp.use;
			Iterator<Pair<String,String>> it1 = use.iterator();
			System.out.println("Use");
			while(it1.hasNext()){
				Pair<String,String> p1 = it1.next();
				System.out.println("(" + p1.getLeft() + "," + p1.getRight() + ")");
			}
			List<Pair<String, String>> def = temp.def;
			Iterator<Pair<String,String>> it2 = def.iterator();
			System.out.println("Def");
			while(it2.hasNext()){
				Pair<String,String> p2 = it2.next();
				System.out.println("(" + p2.getLeft() + "," + p2.getRight() + ")");
			}
		}
	}
	
	static void visitNode(INode node){
		if(node == null || visited.contains(node.get_Number()))
			return;
		node.print_node_information();
		//INode is a Basic Block
		if(node.getClass() == BasicBlock.class){
			num_block++;
			BasicBlock bb = (BasicBlock) node;
			visited.add(bb.get_Number());
			ArrayList<ASTNode> nodes = bb.get_ASTNodes();
			Iterator<ASTNode> it = nodes.iterator();
			while(it.hasNext()){
				ASTNode temp = it.next();
				temp.accept(visitor);
			}
			connection.put(bb.get_Number(), num_block);
			INode i_node = bb.get_Next();
			if(i_node != null)
				blocks.get(num_block).next.add(i_node.get_Number());
			visitNode(i_node);
		}
		/** INode is a Control Block
		*/
		
		else {
			num_block++;
			ControlBlock cb = (ControlBlock) node;
			visited.add(cb.get_Number());
			ASTNode temp = cb.get_ASTNode();
			temp.accept(visitor);
			connection.put(cb.get_Number(), num_block);
			if(cb.get_Truenext() != null)
				blocks.get(num_block).next.add(cb.get_Truenext().get_Number());
			visitNode(cb.get_Truenext());
			if(cb.get_Falsenext() != null)
				blocks.get(num_block).next.add(cb.get_Falsenext().get_Number());
			visitNode(cb.get_Falsenext());
			}
		return;
	}

}
