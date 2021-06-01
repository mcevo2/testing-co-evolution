package Utilities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IMarker;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;
import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.VariableBinding;

import fr.lip6.meta.ComplexChangeDetection.Change;
import fr.lip6.meta.ComplexChangeDetection.AtomicChanges.RenameClass;
import fr.lip6.meta.ComplexChangeDetection.AtomicChanges.RenameProperty;
import fr.lip6.meta.ComplexChangeDetection.ComplexChanges.MoveProperty;
import fr.lip6.meta.ComplexChangeDetection.ComplexChanges.PushProperty;

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
					System.out.println(" FIND A USAGE  "+error+ " the parent "+error.getParent());
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
	public static Usage classify(Change change, ASTNode error)
	{
		Usage usage=new Usage();
		usage.setNode(error);
		if(error instanceof SimpleName) {
		if (change instanceof RenameClass)
		{
			if(((SimpleName)error).getIdentifier().equals(((RenameClass)change).getName()))
			{
				System.out.println(" IN VAR DEC " +error);
				usage.setPattern(UsagePattern.variableDeclaration);
			}
			if(((SimpleName)error).getIdentifier().equals("get"+((RenameClass)change).getName()))
			{
				System.out.println(" in GET OBJ "+error);
				usage.setPattern(UsagePattern.getObject);
			}
			if(((SimpleName)error).getIdentifier().contains("create"+((RenameClass)change).getName()))
			{
				System.out.println(" in CREATE OBJ "+error);
				usage.setPattern(UsagePattern.createObject);
			}
			if(((SimpleName)error).getIdentifier().contains("("+((RenameClass)change).getName()+")"))
			{
				System.out.println(" in CAST "+error);
				usage.setPattern(UsagePattern.castObject);
			}
		}
			if (change instanceof RenameProperty)
			{
				if(((SimpleName)error).getIdentifier().equals(((RenameProperty)change).getName()))
				{
				
					System.out.println(" in DIRECT ACCESS OF ATTRIBUTE "+error);
					usage.setPattern(UsagePattern.accessAttribute);
				}
				if(((SimpleName)error).getIdentifier().equals("get"+((RenameProperty)change).getName()))
				{
					System.out.println(" in GET ATTRIBUTE "+error);
					usage.setPattern(UsagePattern.getAttribute);
				}
				if(((SimpleName)error).getIdentifier().contains("set"+((RenameProperty)change).getName()))
				{
					System.out.println(" in SET ATTRIBUTE "+error);
					
					usage.setPattern(UsagePattern.setAttribute);
				}
				if(((SimpleName)error).getIdentifier().contains("get"+((RenameProperty)change).getClassName()+"_"+((RenameProperty)change).getName()))
				{
					System.out.println(" in GET CLASS ATTRIBUTE "+error);
					usage.setPattern(UsagePattern.getClass_Attribute);
				}
				
			
			
					
		}
		}
		return usage;
			
	}
	public static  ArrayList<Usage> getUsages(Change change, ArrayList<ASTNode> errors)
	{
		 ArrayList<Usage> usages= new ArrayList<Usage>();
		 Usage usage=null;
		 for( ASTNode error: errors)
		 {
			usage= classify(change,error);
			usages.add(usage);
		 }
		 
		 
		 
		 return usages;
		
	}
	public static  Map<Change,  ArrayList<Usage> >  changesUsages(ArrayList<Change> changes, ArrayList<ASTNode> errors)
	{
		HashMap<Change,  ArrayList<Usage> >  myUsages = new HashMap<Change,  ArrayList<Usage> >();
		for(Change c : changes){
			myUsages.put(c,getUsages(c, errors));
			}
		
		return myUsages;
		
	}
	
	

}
