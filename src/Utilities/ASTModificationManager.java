package Utilities;

import org.eclipse.core.filebuffers.FileBuffers;
import org.eclipse.core.filebuffers.ITextFileBuffer;
import org.eclipse.core.filebuffers.ITextFileBufferManager;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jdt.core.dom.rewrite.ListRewrite;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.text.edits.MalformedTreeException;
import org.eclipse.text.edits.TextEdit;


public class ASTModificationManager {
	public static void AddImportDeclaration(CompilationUnit cu, String[] importString)
	{
		  AST ast = cu.getAST();
		  ASTRewrite rewriter1 = ASTRewrite.create(ast);

		    IPath pathcu = cu.getJavaElement().getPath();

		  Document document=null;
		  ICompilationUnit iCompilUnit=(ICompilationUnit) cu.getJavaElement();
		try {
			document = new Document(iCompilUnit.getSource());
		
         ImportDeclaration id1 = ast.newImportDeclaration();
         id1.setName(ast.newName(importString));
       
        
         ListRewrite lrw1 = rewriter1.getListRewrite(cu, CompilationUnit.IMPORTS_PROPERTY);
         lrw1.insertLast(id1, null);
         System.out.println( "HERE IS IMPORT YOURS IS "+lrw1.getRewrittenList().get(lrw1.getRewrittenList().size()-1));
         
         TextEdit edits = rewriter1.rewriteAST(document, null);
        // edits.apply(document);
     	SaveModification.SaveModif(cu, edits);
		
		}
		 catch (JavaModelException | MalformedTreeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	
				    			 		
	         
        
	}

}
