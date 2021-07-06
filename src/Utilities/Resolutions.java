package Utilities;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IMarker;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ExpressionStatement;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.PrimitiveType;
import org.eclipse.jdt.core.dom.QualifiedName;
import org.eclipse.jdt.core.dom.ReturnStatement;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.StringLiteral;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;
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
	public static void renaming( CompilationUnit cu,Usage usage, String newName)
	{
		//System.out.println("Renaming  "+an+"to "+newName);
		ASTNode an = usage.getNode();
		switch(usage.getPattern())
		{
		case variableDeclarationRename:
			System.out.println("Renaming var declar   "+an+"to "+newName);
			ASTModificationManager.RenameSimpleName(cu, an, newName);
			break;
		case createObjectRename:
			System.out.println("Renaming create method  "+an+"to "+newName);
			ASTModificationManager.RenameSimpleName(cu, an, "create"+newName);
			break;
		default:
			break;

		}

	}

	public static void renameImport(CompilationUnit cu,Change change,Usage usage,IMarker amarker, String newName)
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
					usage.setNode(n);
					System.out.println("Renaming  "+n+"to "+(((RenameClass)change).getNewname()));

					ASTModificationManager.RenameSimpleName(cu, n, newName);
				}
			}
		}

		//(((ImportDeclaration)an).getName());

	}
	public static void deleteImport(CompilationUnit cu, Change change,ASTNode node)
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

	public static void DeleteImport3(CompilationUnit cu,ASTNode node)
	{


		AST ast = cu.getAST();
		ASTRewrite rewriter1 = ASTRewrite.create(ast);

		//  IPath pathcu = cu.getJavaElement().getPath();

		Document document=null;
		ICompilationUnit iCompilUnit=(ICompilationUnit) cu.getJavaElement();
		try {
			document = new Document(iCompilUnit.getSource());
		} catch (JavaModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		rewriter1.remove(node.getParent(), null);  
		System.out.println(" parent of deleted  "+node.getParent());
		// edits.apply(document);
		// ListRewrite lrw1 = rewriter1.getListRewrite(cu, CompilationUnit.IMPORTS_PROPERTY);
		//lrw1.remove(node, null);

		TextEdit edits = rewriter1.rewriteAST(document, null);

		SaveModification.SaveModif(cu, edits);






	}

	public static void deleteUsedVariables(CompilationUnit cu,ASTNode foundDeclaration) {

		AST ast = cu.getAST();
		ASTRewrite rewriter1 = ASTRewrite.create(ast);

		//  IPath pathcu = cu.getJavaElement().getPath();

		Document document=null;
		TextEdit edits =null; 
		ICompilationUnit iCompilUnit=(ICompilationUnit) cu.getJavaElement();


		Iterator it = null;
		//now we retrive the declared variables
		if(foundDeclaration instanceof FieldDeclaration){
			it = ((FieldDeclaration) foundDeclaration).fragments().iterator();
			System.out.println("		in field declaration ");
		} else if(foundDeclaration instanceof VariableDeclarationStatement){
			it = ((VariableDeclarationStatement) foundDeclaration).fragments().iterator();
		} else if(foundDeclaration instanceof SingleVariableDeclaration){
			ArrayList<SingleVariableDeclaration> list = new ArrayList<SingleVariableDeclaration>();
			list.add((SingleVariableDeclaration) foundDeclaration);
			it = list.iterator();
		} 

		while(it != null && it.hasNext()){

			Object obj = it.next();
			System.out.println("frgament "+obj);
			//System.out.println("frgament class "+obj.getClass());
			/*here what I should do is 
			 * 1) check the build table of bindings,, 
			 * 2) find used varaiables
			 * 3) delete their usage statements or the element if it is a parameter
			 */
			ArrayList<ASTNode> list_of_usage = new ArrayList<ASTNode>();
			if(obj instanceof VariableDeclarationFragment){
				System.out.println("VariableDeclarationFragment "+obj);
				
				System.out.println("BINDIIIING "+((VariableDeclarationFragment) obj).resolveBinding());
				System.out.println(" keys OOO " +JavaVisitor.getManageBindings().getBindingsNodes().keySet().contains((IBinding)((VariableDeclarationFragment) obj).resolveBinding()));
				//
				if (JavaVisitor.getManageBindings().getBindingsNodes().isEmpty())
				{
					System.out.println("EMPTYYY BINDING "); 
				} 
				
				if(JavaVisitor.getManageBindings().getBindingsNodes().containsKey(((VariableDeclarationFragment) obj).resolveBinding())){
					System.out.println("		found variable >>> "+((VariableDeclarationFragment) obj).resolveBinding().getName());

					list_of_usage = JavaVisitor.getManageBindings().getBindingsNodes().get(((VariableDeclarationFragment) obj).resolveBinding());
				}

			} else if (obj instanceof SingleVariableDeclaration){

				if(JavaVisitor.getManageBindings().getBindingsNodes().containsKey(((SingleVariableDeclaration) obj).resolveBinding())){
					//System.out.println("		found variable >>> "+((SingleVariableDeclaration) obj).resolveBinding().getName());

					list_of_usage = JavaVisitor.getManageBindings().getBindingsNodes().get(((SingleVariableDeclaration) obj).resolveBinding());
				}

			}
			try {
				document = new Document(iCompilUnit.getSource());



			} catch (JavaModelException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			for(ASTNode astNode : list_of_usage){

				ASTNode foundStatement = ASTManager.findStatement(astNode);
				//here we delete the statements where the variable is used


				if(foundStatement != null && foundStatement instanceof ExpressionStatement || foundStatement instanceof VariableDeclarationStatement){

					//foundStatement.delete();
					System.out.println(" DELETE USAGE of DECLAR  "+foundStatement );
					try {
						document = new Document(iCompilUnit.getSource());



					} catch (JavaModelException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					rewriter1.remove(foundStatement, null);  

					






				}

			}	

			rewriter1.remove(foundDeclaration, null);
			edits = rewriter1.rewriteAST(document, null);
		}
		if( edits!=null)
		{
			SaveModification.SaveModif(cu, edits);
		}
	}

	public static void deleteParameter(CompilationUnit compilUnit,ASTNode foundParameter)
	{

		AST ast = compilUnit.getAST();
		ASTRewrite rewriter1 = ASTRewrite.create(ast);

		//  IPath pathcu = cu.getJavaElement().getPath();

		Document document=null;
		TextEdit edits =null;
		ICompilationUnit iCompilUnit=(ICompilationUnit) compilUnit.getJavaElement();

		Resolutions.deleteUsedVariables(compilUnit,foundParameter);
		int pos = -1;
		int parametersSize = -1;
		if(foundParameter.getParent() != null && foundParameter.getParent() instanceof MethodDeclaration){
			//here we take the position of the deleted parameter, and the size of the parameter list 
			pos = ((MethodDeclaration)foundParameter.getParent()).parameters().indexOf(foundParameter);
			parametersSize = ((MethodDeclaration)foundParameter.getParent()).parameters().size();
			//System.out.println("pos "+pos +" number of params"+ parametersSize);

			if(JavaVisitor.getManageBindings().getBindingsNodes().containsKey(((MethodDeclaration) foundParameter.getParent()).resolveBinding())){
				//System.out.println("I found the methods that I need to check");
				//here we check for all its method invocation if it the parameter was removed, 
				//if not (for example using an external method returning the deleted type, but the method still existing with a different returned type), we will remove it
				ArrayList<ASTNode> list_of_usage = JavaVisitor.getManageBindings().getBindingsNodes().get(((MethodDeclaration) foundParameter.getParent()).resolveBinding());
				for(ASTNode astNode : list_of_usage){

					ASTNode invocation = ASTManager.findMethodInvocation(astNode);
					//					if(invocation != null && invocation instanceof MethodInvocation){
					//						System.out.println("is it method decla ? "+astNode+" pos "+" number of params "+ ((MethodInvocation)invocation).arguments().size());
					//					}
					//here we delete the parameter value if it is not deleted above in the medthod invocation
					if(invocation != null && invocation instanceof MethodInvocation && ((MethodInvocation)invocation).arguments().size() == parametersSize){
						((MethodInvocation)invocation).arguments().remove(pos);
					}
				}
			}
		}

		try {
			document = new Document(iCompilUnit.getSource());



		} catch (JavaModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		rewriter1.remove(foundParameter, null);  


		edits = rewriter1.rewriteAST(document, null);
		if( edits!=null)
		{
			SaveModification.SaveModif(compilUnit, edits);
		}
		//		((SingleVariableDeclaration)foundParameter).getName().get 



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
	public static void deleteSuperClass(CompilationUnit cu,Change change, ASTNode node)
	{
		AST ast = cu.getAST();
		ASTRewrite rewriter1 = ASTRewrite.create(ast);

		//  IPath pathcu = cu.getJavaElement().getPath();

		Document document=null; 
		ICompilationUnit iCompilUnit=(ICompilationUnit) cu.getJavaElement();
		if(((SimpleName)node).getIdentifier().equals(((DeleteClass)change).getName())) {
			System.out.println("  here in change= error");
			try {
				document = new Document(iCompilUnit.getSource());


				rewriter1.remove(node.getParent(), null);  

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
	public static void deleteCompextStatemnt(CompilationUnit cu,Change change, ASTNode node)
	{
		AST ast = cu.getAST();
		ASTRewrite rewriter1 = ASTRewrite.create(ast);

		//  IPath pathcu = cu.getJavaElement().getPath();

		Document document=null; 
		ICompilationUnit iCompilUnit=(ICompilationUnit) cu.getJavaElement();
		ASTNode complexStatement=ASTManager.findIfWhileForStatement(node);
		if(((SimpleName)node).getIdentifier().equals(((DeleteClass)change).getName())) {
			
			try {
				document = new Document(iCompilUnit.getSource());


				rewriter1.remove(complexStatement, null);  

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

	public static void deleteInstanceClass(CompilationUnit cu, ASTNode foundInstanceCreation)
	{
		ASTNode nodeTemp = foundInstanceCreation;
		AST ast = cu.getAST();
		ASTRewrite rewriter1 = ASTRewrite.create(ast);

		//  IPath pathcu = cu.getJavaElement().getPath();

		Document document=null;
		ICompilationUnit iCompilUnit=(ICompilationUnit) cu.getJavaElement();

		boolean gotcha = false;
		while (nodeTemp != null && !(nodeTemp instanceof CompilationUnit)) {
			//check if it is in a variable declaration
			if(nodeTemp instanceof VariableDeclarationStatement){
				gotcha = true;//it means that this is treated above as part of declaration
			}

			nodeTemp = nodeTemp.getParent();
		}

		if(!gotcha){
			System.out.println(" IN INSTANCE CASE "); 
			//here we treat the new DeletedType() that is not part of declaration statement
			ASTNode foundStatement = ASTManager.findStatement(foundInstanceCreation);
			//here we delete the statements where the variable is used
			if(foundStatement != null && foundStatement instanceof ExpressionStatement){//TODO what about checking var decla ??
				//System.out.println("		*** found statement to delete >>> "+foundStatement);
				try {
					document = new Document(iCompilUnit.getSource());



				} catch (JavaModelException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				rewriter1.remove(foundStatement, null);  


				TextEdit edits = rewriter1.rewriteAST(document, null);
				if( edits!=null)
				{
					//System.out.println(" FOUND Instance in saving part");
					SaveModification.SaveModif(cu, edits);
				}
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
	public static void deleteLiteral(CompilationUnit cu, Change change,ASTNode node)
	{
		ASTNode st;
		if(node instanceof SimpleName && ASTManager.isLiteral(node) )
		{
String deletedClass=((DeleteClass)change).getName();
String literal=((SimpleName)node).getIdentifier();
literal= literal.replaceAll("_", "");

			
			if(literal.toLowerCase().equals(deletedClass.toLowerCase()))
			{
				//st=ASTManager.findFieldOrVariableDeclarations(node);

				
					
				//Resolutions.deleteUsedVariables(cu,st);

				AST ast = cu.getAST();
				Document document=null;
				TextEdit edits=null;
				ICompilationUnit iCompilUnit=(ICompilationUnit) cu.getJavaElement();
				StringLiteral sl = (StringLiteral) ast.createInstance(StringLiteral.class);
				sl.setLiteralValue("");
				ASTRewrite rewriter1 = ASTRewrite.create(ast);
				try {
					
					document = new Document(iCompilUnit.getSource());
					ClassInstanceCreation newInstance =(ClassInstanceCreation)ASTManager.findClassInstanceCreations(node);
					
					//newInstance.arguments().add(sl);
					//rewriter1.remove(node.getParent(), null);  
					rewriter1.replace(node.getParent(), sl, null);
					edits = rewriter1.rewriteAST(document, null);	
					SaveModification.SaveModif(cu, edits);

				} catch (JavaModelException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				
				
				
			}
		}
	}
	public static void deleteReturnType(CompilationUnit cu, Change change,ASTNode node)
	{

		if(node instanceof SimpleName )
		{

			if((( (DeleteClass)change).getName()).equals(((SimpleName)node).getIdentifier()))
			{

				AST ast = cu.getAST();
				Document document=null;
				TextEdit edits=null;
				ICompilationUnit iCompilUnit=(ICompilationUnit) cu.getJavaElement();
				ASTRewrite rewriter1 = ASTRewrite.create(ast);


				//  IPath pathcu = cu.getJavaElement().getPath();
				JavaVisitor jv =new JavaVisitor();
				cu.accept(jv);
				List<ReturnStatement> returnStms =jv.getReturnStatments();

				try {
					System.out.println("In return type resolution   ");

					document = new Document(iCompilUnit.getSource());
				} catch (JavaModelException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				for(ReturnStatement rs :returnStms )
				{


					rewriter1.remove((ASTNode)rs, null);  
					edits = rewriter1.rewriteAST(document, null);	



				}
				if( edits != null)
					SaveModification.SaveModif(cu, edits);

				ASTModificationManager.RenameSimpleName(cu, node, "void");	
			}
		}
		else if (node instanceof QualifiedName)
		{
			
		}
	}

	public static void deleteParameter2(CompilationUnit cu, Change change,ASTNode node)
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
	public static void DeleteVisitClassMethod(CompilationUnit cu, Change change,ASTNode node)
	{
		ASTNode method= ASTManager.findMethodDeclaration(node);
		AST ast = cu.getAST();
		ASTRewrite rewriter1 = ASTRewrite.create(ast);

		//  IPath pathcu = cu.getJavaElement().getPath();

		Document document=null;
		ICompilationUnit iCompilUnit=(ICompilationUnit) cu.getJavaElement();
		try {
			document = new Document(iCompilUnit.getSource());
			rewriter1.remove(method, null);  

			TextEdit edits = rewriter1.rewriteAST(document, null);

			SaveModification.SaveModif(cu, edits);
		}
		catch (JavaModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
