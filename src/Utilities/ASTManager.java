package Utilities;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IMarker;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ExpressionStatement;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.NodeFinder;
import org.eclipse.jdt.core.dom.QualifiedName;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.StructuralPropertyDescriptor;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;

public class ASTManager {
	public static CompilationUnit getCompilationUnit(ICompilationUnit iCompilUnit)
	{
//JavaParser jp = new JavaParser();
		ASTParser parser = ASTParser.newParser(AST.JLS8);
		parser.setResolveBindings(true);
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		parser.setBindingsRecovery(true);
		parser.setSource(iCompilUnit);
		
	//	parser.setBindingsRecovery(true);
	//	CompilationUnit cu = (CompilationUnit) parser.createAST( null);
		JavaVisitor jVisitor = new JavaVisitor();
		CompilationUnit newUnit = (CompilationUnit) parser.createAST(null /* IProgressMonitor */); // parse
		jVisitor.process(newUnit);

		return newUnit;
	}
	public static CompilationUnit getCompilationUnit2(ICompilationUnit iCompilUnit)
	{
//JavaParser jp = new JavaParser();
		ASTParser parser = ASTParser.newParser(AST.JLS8);
		parser.setResolveBindings(true);
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		parser.setBindingsRecovery(true);
		parser.setSource(iCompilUnit);
		
	//	parser.setBindingsRecovery(true);
		CompilationUnit cu = (CompilationUnit) parser.createAST( null);
		

		return cu;
	}
	public static AST getCompilUnitAST (CompilationUnit cu)
	{
		AST ast= cu.getAST();
return ast;
	}
	
	public static ASTNode getErrorNode(CompilationUnit cu,IMarker marker)
	{
		int start = marker.getAttribute(IMarker.CHAR_START, 0);

		    int end = marker.getAttribute(IMarker.CHAR_END, 0);

		     

		  NodeFinder nf = new NodeFinder(cu.getRoot(), start, end-start);
		 
			 

	  ASTNode an=nf.getCoveringNode();

	  return an;
	}
	public static ArrayList<ASTNode> getErrorNodes(CompilationUnit cu,ArrayList<IMarker> markers)
	{
		ArrayList<ASTNode> ans= new ArrayList<ASTNode>(); 
		
		ASTNode an=null;
		int start,end=0;
		for( IMarker marker: markers) {
		
		 start = marker.getAttribute(IMarker.CHAR_START, 0);

		  end = marker.getAttribute(IMarker.CHAR_END, 0);

		  NodeFinder nf = new NodeFinder(cu.getRoot(), start, end-start);

	   an=nf.getCoveringNode( );
	   ans.add(an);
		}

	  return ans;
	}
	public static ASTNode findImportDeclaration(ASTNode node) {
		
		ASTNode nodeTemp = node;
		while (nodeTemp != null && !(nodeTemp instanceof CompilationUnit)) {
			//System.out.println("khelladi import ? "+nodeTemp.getClass());
			
			if(nodeTemp instanceof ImportDeclaration){
				return nodeTemp;
			}
			
			nodeTemp = nodeTemp.getParent();
		}
		
		return null;
	}
	public static boolean checkImportDeclaration( ASTNode node)
	{
		boolean isImport=false;
		
		
		
		if(node.getParent() instanceof ImportDeclaration)
			isImport=true;
		
		
		
		return isImport;
	}
	public static List<ASTNode> getChildren(ASTNode node) {
	    List<ASTNode> children = new ArrayList<ASTNode>();
	    List list = node.structuralPropertiesForType();
	    for (int i = 0; i < list.size(); i++) {
	        Object child = node.getStructuralProperty((StructuralPropertyDescriptor)list.get(i));
	        if (child instanceof ASTNode) {
	            children.add((ASTNode) child);
	            //System.out.println("HERE IS A CHILD "+child);
	        }
	    }
	    return children;
	}
	public static ASTNode findVariableDeclarationFragment(ASTNode node) {
		ASTNode nodeTemp = node;
		while (nodeTemp != null && !(nodeTemp instanceof CompilationUnit)) {
		
		
			if(nodeTemp instanceof VariableDeclarationFragment ){
				
				return nodeTemp;
			}
			
			nodeTemp = nodeTemp.getParent();
		}
		
		return null;
	}
	
	public static ASTNode findAssignment(ASTNode node) {
		ASTNode nodeTemp = node;
		while (nodeTemp != null && !(nodeTemp instanceof CompilationUnit)) {
		
		
			if(nodeTemp instanceof Assignment ){
				
				return nodeTemp;
			}
			
			nodeTemp = nodeTemp.getParent();
		}
		
		return null;
	}
public static ASTNode findFieldOrVariableDeclarations(ASTNode node) {
		
		ASTNode nodeTemp = node;
		while (nodeTemp != null && !(nodeTemp instanceof CompilationUnit)) {
			//System.out.println("khelladi Field/Variable declaration ? "+nodeTemp.getClass());
			
			if(nodeTemp instanceof FieldDeclaration || nodeTemp instanceof VariableDeclarationStatement){
				return nodeTemp;
			}
			
			nodeTemp = nodeTemp.getParent();
		}
		
		return null;
	}
public static ASTNode findExpressionStatement(ASTNode node) {
		
		ASTNode nodeTemp = node;
		while (nodeTemp != null && !(nodeTemp instanceof CompilationUnit)) {
		
		
			if(nodeTemp instanceof ExpressionStatement){
				
				return nodeTemp;
			}
			
			nodeTemp = nodeTemp.getParent();
		}
		
		return null;
	}
	public static ASTNode findParameterInMethodDeclaration(ASTNode node) {
		
		ASTNode nodeTemp = node;
		while (nodeTemp != null && !(nodeTemp instanceof CompilationUnit)) {
			//System.out.println("khelladi Parameter ? "+nodeTemp.getClass());
			
			if(nodeTemp instanceof SingleVariableDeclaration){//MethodDeclaration){no need to get to method declaration and then delete the parameter, just delete the parameter directly
				return nodeTemp;
			}
			
			nodeTemp = nodeTemp.getParent();
		}
		
		return null;
		
	}
	public static ASTNode findStatement(ASTNode node) {
		
		ASTNode nodeTemp = node;
		while (nodeTemp != null && !(nodeTemp instanceof CompilationUnit)) {
			//System.out.println("khelladi Expression/Variable declaration Statement ? "+nodeTemp.getClass());
			
			if(nodeTemp instanceof ExpressionStatement || nodeTemp instanceof VariableDeclarationStatement){
				return nodeTemp;
			}
			
			nodeTemp = nodeTemp.getParent();
		}
		
		return null;
	}

public static ASTNode findClassInstanceCreations(ASTNode node) {
		
		ASTNode nodeTemp = node;
		while (nodeTemp != null && !(nodeTemp instanceof CompilationUnit)) {
			//System.out.println("khelladi new class instance ? "+nodeTemp.getClass());
			
			if(nodeTemp instanceof ClassInstanceCreation){
				return nodeTemp;
			}
			
			nodeTemp = nodeTemp.getParent();
		}
		
		return null;
		
	}
	
public static ASTNode findMethodInvocation(ASTNode node) {
		
		ASTNode nodeTemp = node;
		while (nodeTemp != null && !(nodeTemp instanceof CompilationUnit)) {
			//System.out.println("khelladi method invocation ? "+nodeTemp.getClass());
			
			if(nodeTemp instanceof MethodInvocation){
				return nodeTemp;
			}
			
			nodeTemp = nodeTemp.getParent();
		}
		
		return null;
	}
}
