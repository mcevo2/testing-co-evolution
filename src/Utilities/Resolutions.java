package Utilities;

import java.util.List;

import org.eclipse.core.resources.IMarker;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jdt.core.dom.rewrite.ListRewrite;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.text.edits.MalformedTreeException;
import org.eclipse.text.edits.TextEdit;

import fr.lip6.meta.ComplexChangeDetection.Change;
import fr.lip6.meta.ComplexChangeDetection.AtomicChanges.DeleteClass;
import fr.lip6.meta.ComplexChangeDetection.AtomicChanges.RenameClass;
import fr.lip6.meta.ComplexChangeDetection.AtomicChanges.RenameProperty;

public class Resolutions {
	public static void renaming( CompilationUnit cu,ASTNode an, String newName)
	{
		//System.out.println("Renaming  "+an+"to "+newName);
		ASTModificationManager.RenameSimpleName(cu, an, newName);
	}
	public static void resolveImport(CompilationUnit cu,Change change,ASTNode an,IMarker amarker, String newName)
	{
		ASTNode errornode = ASTManager.getErrorNode(cu, amarker);

		List<ASTNode > childrennodes=ASTManager.getChildren(errornode);
		for (int i = 0; i < childrennodes.size(); i++) {
			ASTNode n= childrennodes.get(i);
			if( n instanceof SimpleName)
			{
				//System.out.println(" HERE IS AN IMPORT SIMPLE NAME "+ n);
				if(((SimpleName)n).getIdentifier().equals(((RenameClass)change).getName()))
				{
					System.out.println("Renaming  "+n+"to "+(((RenameClass)change).getNewname()));
					renaming(cu,n,(((RenameClass)change).getNewname()))	 ;
				}
			}
		}

		//(((ImportDeclaration)an).getName());

	}
	public static void DeleteImport(CompilationUnit cu, Change change,ASTNode node)
	{


		AST ast = cu.getAST();
		ASTRewrite rewriter1 = ASTRewrite.create(ast);

		//  IPath pathcu = cu.getJavaElement().getPath();

		Document document=null;
		ICompilationUnit iCompilUnit=(ICompilationUnit) cu.getJavaElement();
		List<ASTNode > childrennodes=ASTManager.getChildren(node);
		try {
			document = new Document(iCompilUnit.getSource());
			for (int i = 0; i < childrennodes.size(); i++) {
				ASTNode n= childrennodes.get(i);
				if( n instanceof SimpleName)
				{
					if(((SimpleName)n).getIdentifier().equals(((DeleteClass)change).getName())) {

						rewriter1.remove(node.getParent(), null);  
						System.out.println(" parent of deleted  "+node.getParent());
						// edits.apply(document);
						// ListRewrite lrw1 = rewriter1.getListRewrite(cu, CompilationUnit.IMPORTS_PROPERTY);
						//lrw1.remove(node, null);

						TextEdit edits = rewriter1.rewriteAST(document, null);

						SaveModification.SaveModif(cu, edits);
					}
				}


			}
		}
		catch (JavaModelException | MalformedTreeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


	}

	public static void deleteVariableDeclaration(CompilationUnit cu, Change change,ASTNode node)
	{
		ASTNode variableDeclarationToDelete=null;
		AST ast = cu.getAST();
		ASTRewrite rewriter1 = ASTRewrite.create(ast);

		//  IPath pathcu = cu.getJavaElement().getPath();

		Document document=null;
		ICompilationUnit iCompilUnit=(ICompilationUnit) cu.getJavaElement();
		System.out.println(" Node to be deleted  "+node);
		if(((SimpleName)node).getIdentifier().equals(((DeleteClass)change).getName())) {

			try {
				document = new Document(iCompilUnit.getSource());
			
			variableDeclarationToDelete=ASTManager.findFieldOrVariableDeclarations(node);
			System.out.println(" You will delete  "+variableDeclarationToDelete);
			rewriter1.remove(variableDeclarationToDelete, null);  
		
			// edits.apply(document);
			// ListRewrite lrw1 = rewriter1.getListRewrite(cu, CompilationUnit.IMPORTS_PROPERTY);
			//lrw1.remove(node, null);

			TextEdit edits = rewriter1.rewriteAST(document, null);

			SaveModification.SaveModif(cu, edits);
			} catch (JavaModelException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}


	}
	

	public static void deleteVariablAssignment(CompilationUnit cu, Change change,ASTNode node)
	{
		ASTNode variableAssignmentToDelete=null;
		AST ast = cu.getAST();
		ASTRewrite rewriter1 = ASTRewrite.create(ast);

		//  IPath pathcu = cu.getJavaElement().getPath();

		Document document=null;
		ICompilationUnit iCompilUnit=(ICompilationUnit) cu.getJavaElement();
		System.out.println(" Node to be deleted from assignment "+node);
		if(((SimpleName)node).getIdentifier().equals(((DeleteClass)change).getName())) {

			try {
				document = new Document(iCompilUnit.getSource());
			
				variableAssignmentToDelete=ASTManager.findAssignment(node);
			System.out.println(" You will delete from assignment "+variableAssignmentToDelete);
			rewriter1.remove(ASTManager.findExpressionStatement(variableAssignmentToDelete), null);  
		
			// edits.apply(document);
			// ListRewrite lrw1 = rewriter1.getListRewrite(cu, CompilationUnit.IMPORTS_PROPERTY);
			//lrw1.remove(node, null);

			TextEdit edits = rewriter1.rewriteAST(document, null);

			SaveModification.SaveModif(cu, edits);
			} catch (JavaModelException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}


	}
	
public static void deleteParameter(CompilationUnit cu, Change change,ASTNode node)
{
	int pos = -1;
	int parametersSize = -1;
	AST ast = cu.getAST();
	ASTRewrite rewriter1 = ASTRewrite.create(ast);

	//  IPath pathcu = cu.getJavaElement().getPath();

	Document document=null;
	ICompilationUnit iCompilUnit=(ICompilationUnit) cu.getJavaElement();
	try {
		document = new Document(iCompilUnit.getSource());
	
	
ASTNode foundParameter=	ASTManager.findParameterInMethodDeclaration(node);
	if(foundParameter.getParent() != null && foundParameter.getParent() instanceof MethodDeclaration){
		pos = ((MethodDeclaration)foundParameter.getParent()).parameters().indexOf(foundParameter);
		parametersSize = ((MethodDeclaration)foundParameter.getParent()).parameters().size();
		
		//System.out.println("Position: "+pos+"  Param size  "+parametersSize);
		
		rewriter1.remove(foundParameter, null);  
		
		// edits.apply(document);
		// ListRewrite lrw1 = rewriter1.getListRewrite(cu, CompilationUnit.IMPORTS_PROPERTY);
		//lrw1.remove(node, null);

		TextEdit edits = rewriter1.rewriteAST(document, null);

		SaveModification.SaveModif(cu, edits);
	}
	} catch (JavaModelException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
}

}
