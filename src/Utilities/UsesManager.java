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
				if(((SimpleName)errornode).getIdentifier().contains("create"+((RenameClass)change).getName()))
				{
					//System.out.println(" in CREATE OBJ "+errornode);
					usage.setPattern(UsagePattern.createObjectRename);
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


				if(((SimpleName)errornode).getIdentifier().equals(((DeleteClass)change).getName()))
				{
					
						
						Iterator it = null;
						ASTNode foundDeclaration =ASTManager.findFieldOrVariableDeclarations(errornode);
						if(foundDeclaration instanceof FieldDeclaration){
							it = ((FieldDeclaration) foundDeclaration).fragments().iterator();
						} else if(foundDeclaration instanceof VariableDeclarationStatement){
							it = ((VariableDeclarationStatement) foundDeclaration).fragments().iterator();
						} else if(foundDeclaration instanceof SingleVariableDeclaration){
							ArrayList<SingleVariableDeclaration> list = new ArrayList<SingleVariableDeclaration>();
							list.add((SingleVariableDeclaration) foundDeclaration);
							it = list.iterator();
						} 

						while(it != null && it.hasNext()){

							Object obj = it.next();
							System.out.println(" in boucle whileeee:  "+obj);
							//System.out.println("frgament "+obj);
							//System.out.println("frgament class "+obj.getClass());
							/*here what I should do is 
							 * 1) check the build table of bindings,, 
							 * 2) find used varaiables
							 * 3) delete their usage statements or the element if it is a parameter
							 */
							ArrayList<ASTNode> list_of_usage = new ArrayList<ASTNode>();
							if(obj instanceof VariableDeclarationFragment){
								System.out.println(" in Variable declaration fragment case ");

								if(JavaVisitor.getManageBindings().getBindingsNodes().containsKey(((VariableDeclarationFragment) obj).resolveBinding())){
									//System.out.println("		found variable >>> "+((VariableDeclarationFragment) obj).resolveBinding().getName());

									list_of_usage = JavaVisitor.getManageBindings().getBindingsNodes().get(((VariableDeclarationFragment) obj).resolveBinding());
									for(ASTNode astNode : list_of_usage){
										System.out.println(" in test boucle printing nodes :  "+astNode);
									}
								}

							} else if (obj instanceof SingleVariableDeclaration){
								System.out.println(" in SingleVariableDeclaration case ");

								if(JavaVisitor.getManageBindings().getBindingsNodes().containsKey(((SingleVariableDeclaration) obj).resolveBinding())){
									//System.out.println("		found variable >>> "+((SingleVariableDeclaration) obj).resolveBinding().getName());

									list_of_usage = JavaVisitor.getManageBindings().getBindingsNodes().get(((SingleVariableDeclaration) obj).resolveBinding());
								}

							}
							for(ASTNode astNode : list_of_usage){

								ASTNode foundStatement = ASTManager.findStatement(astNode);

								//here we delete the statements where the variable is used
								if(foundStatement != null && foundStatement instanceof ExpressionStatement || foundStatement instanceof VariableDeclarationStatement){
									System.out.println("		*** found statement to delete >>> "+foundStatement);

									//foundStatement.delete();

								}

								//ASTNode foundParameter = DeleteResolution.findParameter(astNode);
								//now we should delete the parameters and then update the method declaration
								//here two method invocation should exist where the used varaible is contained in one
							}				

						}


						//		System.out.println(" IN VAR DEC " +errornode+" BINDING: "+((SimpleName)errornode).resolveTypeBinding().getName());
						if(foundDeclaration instanceof VariableDeclarationStatement){
							it = ((VariableDeclarationStatement) foundDeclaration).fragments().iterator();
						} 
						usage.setPriority(0);
						usage.setTreated(false);
						usage.setPattern(UsagePattern.VariableDeclarationDelete);
					
					if(ASTManager.findParameterInMethodDeclaration(usage.getNode())!=null)
					{
						System.out.println("YOUR IN PARAMETER CASE");
						usage.setPattern(UsagePattern.parameterDelete);
					}

				}

			}
		}
		else if(errornode instanceof QualifiedName)
		{
			if(ASTManager.checkImportDeclaration(errornode)  )
			{
				if( change instanceof RenameClass)
				{
					usage.setPattern(UsagePattern.inImportRename);
					//System.out.println(" and its child is "+errornode.)
				}
				else if( change instanceof DeleteClass)
				{
					usage.setPattern(UsagePattern.inImportDelete);
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
