package Utilities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IMarker;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ExpressionStatement;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.QualifiedName;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;
import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.VariableBinding;
import org.eclipse.jface.text.Document;

import fr.lip6.meta.ComplexChangeDetection.Change;
import fr.lip6.meta.ComplexChangeDetection.AtomicChanges.DeleteClass;
import fr.lip6.meta.ComplexChangeDetection.AtomicChanges.RenameClass;
import fr.lip6.meta.ComplexChangeDetection.AtomicChanges.RenameProperty;
import fr.lip6.meta.ComplexChangeDetection.ComplexChanges.MoveProperty;
import fr.lip6.meta.ComplexChangeDetection.ComplexChanges.PushProperty;
import 	org.eclipse.jdt.astview.views.ASTViewContentProvider;


public class UsesManager {
	public static ArrayList<ASTNode> findUsages( Change change, ArrayList<ASTNode> errors)
	{
		ArrayList<ASTNode> usages=new ArrayList<ASTNode>();
		String type="";

		if( change instanceof RenameClass)
		{
			type="Class";
		}
		if(change instanceof  RenameProperty)
		{
			type="Property";
		}
		if(change instanceof  MoveProperty)
		{

		}
		if(change instanceof  PushProperty)
		{

		}
		for( ASTNode error: errors)
		{
			if( type.equals("Class"))
			{

				if(error.toString().contains(((RenameClass)change).getName().toUpperCase()) || error.toString().contains(((RenameClass)change).getName()))
				{
					//System.out.println(" FIND A USAGE  "+error+ " the parent "+error.getParent());
					//checkUsageType(error);
					usages.add(error);
				}

			}



		}


		return usages;
	}
	public static String checkUsagePattern( ASTNode node)
	{
		String pattern="";
		if(node instanceof SimpleName)
		{
			if(((SimpleName) node).getIdentifier().contains("get"))
			{
				pattern="get";
			}
			else if(((SimpleName) node).getIdentifier().contains("create"))
			{
				pattern="create";
			}
		}

		return pattern;

	}
	public static boolean checkUsageType( ASTNode node)
	{
		boolean isVariableDeclaration=false,stop =false;

		if(node instanceof SimpleName)
		{
			if(((SimpleName)node).resolveBinding() instanceof VariableBinding)
			{
				System.out.println(" it is a Variable");
			}
			if(((SimpleName)node).resolveBinding() instanceof MethodBinding)
			{
				System.out.println(" it is a method");
			}
			while(!stop)
			{
				/*	if(node instanceof SUPER_INTERFACE_TYPES)
				{
					stop=true;
					System.out.println(" it is a Variable");

				}
				 */
				if(node instanceof VariableDeclarationStatement)
				{
					stop=true;
					System.out.println(" it is a Variable");

				}
				if(node instanceof MethodDeclaration)
				{
					stop=true;
					System.out.println(" it is a METHOD");

				}
				node=node.getParent();
			}
		}
		return isVariableDeclaration;
	}
	public static Usage classify(Change change, IMarker error,CompilationUnit cu)
	{
		Usage usage=new Usage();
		ASTNode errornode = ASTManager.getErrorNode(cu, error);
		System.out.println(" IN CLASSIFY , FIND NODE "+errornode);
		usage.setNode(errornode);
		usage.setError(error);
		if(errornode instanceof SimpleName) {
			if (change instanceof RenameClass)
			{

				if(((SimpleName)errornode).getIdentifier().equals(((RenameClass)change).getName()))
				{
					//System.out.println(" IN VAR DEC " +errornode);
					System.out.println(" IN VAR DEC " +errornode+" BINDINGGG"+((SimpleName)errornode).resolveBinding().getName());
					usage.setPattern(UsagePattern.variableDeclarationRename);

				}
				if(((SimpleName)errornode).getIdentifier().equals("get"+((RenameClass)change).getName()))
				{
					//System.out.println(" in GET OBJ "+errornode);
					usage.setPattern(UsagePattern.getObjectRename);
				}
				if( ASTManager.findMethodInvocation(errornode)!=null) {
				if(((SimpleName)errornode).getIdentifier().equals("create"+((RenameClass)change).getName()))
				{
					System.out.println(" in CREATE OBJ "+errornode);
					usage.setPattern(UsagePattern.createObjectRename);
				}
				}
				/*	if(((SimpleName)error).getIdentifier().contains("("+((RenameClass)change).getName()+")"))
			{
				System.out.println(" in CAST "+error);
				usage.setPattern(UsagePattern.castObject);
			}*/
			}
			if (change instanceof RenameProperty)
			{
				if(((SimpleName)errornode).getIdentifier().equals(((RenameProperty)change).getName()))
				{

					//System.out.println(" in DIRECT ACCESS OF ATTRIBUTE "+errornode);
					usage.setPattern(UsagePattern.accessAttribute);
				}
				if(((SimpleName)errornode).getIdentifier().equals("get"+((RenameProperty)change).getName()))
				{
					//System.out.println(" in GET ATTRIBUTE "+errornode);
					usage.setPattern(UsagePattern.getAttribute);
				}
				if(((SimpleName)errornode).getIdentifier().contains("set"+((RenameProperty)change).getName()))
				{
					//System.out.println(" in SET ATTRIBUTE "+errornode);

					usage.setPattern(UsagePattern.setAttribute);
				}
				if(((SimpleName)errornode).getIdentifier().contains("get"+((RenameProperty)change).getClassName()+"_"+((RenameProperty)change).getName()))
				{
					//System.out.println(" in GET CLASS ATTRIBUTE "+errornode);
					usage.setPattern(UsagePattern.getClass_Attribute);
				}




			}
			if(change instanceof DeleteClass)
			{
				if(ASTManager.findAssignment(errornode)!=null)
				{


					//System.out.println("LEFT HAND SIDE "+((Assignment)ASTManager.findAssignment(errornode)).getLeftHandSide());

				//	System.out.println("LEFT HAND SIDE BINNDING  "+((SimpleName)errornode).resolveTypeBinding().getName());
					/*if(((SimpleName)errornode).resolveTypeBinding().getName().equals(((DeleteClass)change).getName()))
					{
						usage.setPriority(1);
						usage.setTreated(false);
						usage.setPattern(UsagePattern.VariableUseDelete);
					}
					*/

				}

				if(ASTManager.findVariableDeclarationFragment(errornode)!=null)
				{
					
				}
				if(((SimpleName)errornode).getIdentifier().equals(((DeleteClass)change).getName()))
				{
					
					if(ASTManager.findParameterInMethodDeclaration(usage.getNode())!=null)
					{
						
						usage.setPattern(UsagePattern.parameterDelete);
					}

				}
				if(((SimpleName)errornode).getIdentifier().equals(((DeleteClass)change).getName()))
				{
					

					if(ASTManager.findFieldOrVariableDeclarations(errornode)!=null)
					{
						
						usage.setPattern(UsagePattern.VariableDeclarationDelete);
					}

					if(ASTManager.findClassInstanceCreations(errornode)!=null)
					{
						
						usage.setPattern(UsagePattern.ClassInstanceDelete);
					}

				}

			}
		}
		else if(errornode instanceof QualifiedName)
		{
			if(ASTManager.checkImportDeclaration(errornode)  )
			{
				List<ASTNode > childrennodes=ASTManager.getChildren(errornode);
				
					
					for (int i = 0; i < childrennodes.size(); i++) {
						ASTNode n= childrennodes.get(i);
						if( n instanceof SimpleName)
						{
						

				if( change instanceof RenameClass && ((SimpleName)n).getIdentifier().equals(((RenameClass)change).getName()) )
				{
					usage.setPattern(UsagePattern.inImportRename);
					//System.out.println(" and its child is "+errornode.)
				}
				else if( change instanceof DeleteClass && ((SimpleName)n).getIdentifier().equals(((DeleteClass)change).getName()) )
				{
					usage.setPattern(UsagePattern.inImportDelete);
				}
						}
					}

			}
		}
		return usage;

	}
	
	public static  ArrayList<Usage> getUsages(Change change, ArrayList<IMarker> errors,CompilationUnit cu)
	{
		ArrayList<Usage> usages= new ArrayList<Usage>();
		Usage usage=null;
		for( IMarker error: errors)
		{
			usage= classify(change,error,cu );
			//System.out.println(" here is an error in classify "+usage.node);
			usages.add(usage);
		}



		return usages;

	}
	public static  Map<Change,  ArrayList<Usage> >  changesUsages(ArrayList<Change> changes, ArrayList<IMarker> errors, CompilationUnit cu)
	{
		HashMap<Change,  ArrayList<Usage> >  myUsages = new HashMap<Change,  ArrayList<Usage> >();
		for(Change c : changes){
			myUsages.put(c,getUsages(c, errors,cu));
		}

		return myUsages;

	}



}
