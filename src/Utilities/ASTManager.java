package Utilities;

import org.eclipse.core.resources.IMarker;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.NodeFinder;

public class ASTManager {
	public static CompilationUnit getCompilationUnit(ICompilationUnit iCompilUnit)
	{
		ASTParser parser = ASTParser.newParser(AST.JLS8);
		parser.setResolveBindings(true);
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		parser.setBindingsRecovery(true);
		parser.setSource(iCompilUnit);
		parser.setResolveBindings(true);
		parser.setBindingsRecovery(true);
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

}
