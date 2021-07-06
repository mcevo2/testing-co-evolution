package Utilities;


import java.util.Iterator;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.Annotation;
import org.eclipse.jdt.core.dom.ArrayAccess;
import org.eclipse.jdt.core.dom.ArrayCreation;
import org.eclipse.jdt.core.dom.ArrayInitializer;
import org.eclipse.jdt.core.dom.AssertStatement;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.BooleanLiteral;
import org.eclipse.jdt.core.dom.BreakStatement;
import org.eclipse.jdt.core.dom.CastExpression;
import org.eclipse.jdt.core.dom.CharacterLiteral;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.ConditionalExpression;
import org.eclipse.jdt.core.dom.ConstructorInvocation;
import org.eclipse.jdt.core.dom.ContinueStatement;
import org.eclipse.jdt.core.dom.DoStatement;
import org.eclipse.jdt.core.dom.EmptyStatement;
import org.eclipse.jdt.core.dom.EnhancedForStatement;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.ExpressionMethodReference;
import org.eclipse.jdt.core.dom.ExpressionStatement;
import org.eclipse.jdt.core.dom.FieldAccess;
import org.eclipse.jdt.core.dom.ForStatement;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.InfixExpression;
import org.eclipse.jdt.core.dom.InstanceofExpression;
import org.eclipse.jdt.core.dom.LabeledStatement;
import org.eclipse.jdt.core.dom.LambdaExpression;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.MethodReference;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.NullLiteral;
import org.eclipse.jdt.core.dom.NumberLiteral;
import org.eclipse.jdt.core.dom.ParenthesizedExpression;
import org.eclipse.jdt.core.dom.PostfixExpression;
import org.eclipse.jdt.core.dom.PrefixExpression;
import org.eclipse.jdt.core.dom.ReturnStatement;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.StringLiteral;
import org.eclipse.jdt.core.dom.SuperConstructorInvocation;
import org.eclipse.jdt.core.dom.SuperFieldAccess;
import org.eclipse.jdt.core.dom.SuperMethodInvocation;
import org.eclipse.jdt.core.dom.SwitchCase;
import org.eclipse.jdt.core.dom.SwitchStatement;
import org.eclipse.jdt.core.dom.SynchronizedStatement;
import org.eclipse.jdt.core.dom.ThisExpression;
import org.eclipse.jdt.core.dom.ThrowStatement;
import org.eclipse.jdt.core.dom.TryStatement;
import org.eclipse.jdt.core.dom.TypeDeclarationStatement;
import org.eclipse.jdt.core.dom.TypeLiteral;
import org.eclipse.jdt.core.dom.VariableDeclarationExpression;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;
import org.eclipse.jdt.core.dom.WhileStatement;

public class DeleteAllPathCall {

	/* here we replace x.y.z.deletedPro.w... by nothing (we remove it) in its parent node 
	 */
	public static void replaceExpressionInParentNode(ASTNode oldExp){
		
		if(oldExp.getParent() instanceof Statement){
			replaceExpressionInParentNodeStatement(oldExp);
			
		} else if(oldExp.getParent() instanceof Expression){
			replaceExpressionInParentNodeExpression(oldExp);
			
		} else if(oldExp.getParent() instanceof VariableDeclarationFragment){
			
			System.out.println("			VariableDeclarationFragment "+oldExp + " >> class "+oldExp.getClass());
			System.out.println("			parent VariableDeclarationFragment "+oldExp.getParent() + " >> class "+oldExp.getParent().getClass());
			
			if(((VariableDeclarationFragment)oldExp.getParent()).getInitializer().equals(oldExp)){
				
				((VariableDeclarationFragment)oldExp.getParent()).getInitializer().delete();	
	
			}
			
		} else if(oldExp.getParent() instanceof SingleVariableDeclaration){
			
//			System.out.println("			yay deidara SingleVariableDeclaration "+oldExp + " >> class "+oldExp.getClass());
//			System.out.println("			yay deidara parent SingleVariableDeclaration "+oldExp.getParent() + " >> class "+oldExp.getParent().getClass());
			
			if(((SingleVariableDeclaration)oldExp.getParent()).getInitializer().equals(oldExp)){
				
				((SingleVariableDeclaration)oldExp.getParent()).getInitializer().delete();	
				
			}
		} 
	}
	
	private static void replaceExpressionInParentNodeStatement(ASTNode oldExp){
		//TODObelow if the expression is mandatory , then delete the statement, otherwise delete the expression (or set it to null)
		
		
		if(oldExp.getParent() instanceof AssertStatement){
			//if(((AssertStatement)oldExp.getParent()).get)
			
			if(((AssertStatement)oldExp.getParent()).getExpression().equals(oldExp)){
				
				((AssertStatement)oldExp.getParent()).delete();
				
			} else if(((AssertStatement)oldExp.getParent()).getMessage().equals(oldExp)){
				
				((AssertStatement)oldExp.getParent()).getMessage().delete();//or set it to nul?? setMessage(null);
			}
			
		} else if(oldExp.getParent() instanceof Block){
			
		} else if(oldExp.getParent() instanceof BreakStatement){
			
		} else if(oldExp.getParent() instanceof ConstructorInvocation){
			
			if(((ConstructorInvocation)oldExp.getParent()).arguments().contains(oldExp)){
				
				((ConstructorInvocation)oldExp.getParent()).arguments().remove(oldExp);
			} 

		} else if(oldExp.getParent() instanceof ContinueStatement){
			
		} else if(oldExp.getParent() instanceof DoStatement){
			
			if(((DoStatement)oldExp.getParent()).getExpression().equals(oldExp)){
				
				((DoStatement)oldExp.getParent()).delete();
			}
			
		} else if(oldExp.getParent() instanceof EmptyStatement){
			
		} else if(oldExp.getParent() instanceof EnhancedForStatement){
			
			if(((EnhancedForStatement)oldExp.getParent()).getExpression().equals(oldExp)){
				
				((EnhancedForStatement)oldExp.getParent()).delete();	
			} 
//			else if(((EnhancedForStatement)oldExp.getParent()).getBody().equals(oldExp)){
//				
//			}
			
		} else if(oldExp.getParent() instanceof ExpressionStatement){
			
			if(((ExpressionStatement)oldExp.getParent()).getExpression().equals(oldExp)){
				
				((ExpressionStatement)oldExp.getParent()).delete();	
			} 

		} 
		else if(oldExp.getParent() instanceof ForStatement){
			
			if(((ForStatement)oldExp.getParent()).getExpression().equals(oldExp)){
				
				((ForStatement)oldExp.getParent()).getExpression().delete();//or set it to null	
				
			} else if(((ForStatement)oldExp.getParent()).initializers().contains(oldExp)){
				
				((ForStatement)oldExp.getParent()).initializers().remove(oldExp);
				
			} else if(((ForStatement)oldExp.getParent()).updaters().contains(oldExp)){
				
				((ForStatement)oldExp.getParent()).updaters().remove(oldExp);
			} 
			
		} else if(oldExp.getParent() instanceof IfStatement){
			
			if(((IfStatement)oldExp.getParent()).getExpression().equals(oldExp)){
				
				((IfStatement)oldExp.getParent()).delete();	
			}

		} else if(oldExp.getParent() instanceof LabeledStatement){
			
		} else if(oldExp.getParent() instanceof ReturnStatement){
			
			if(((ReturnStatement)oldExp.getParent()).getExpression().equals(oldExp)){
				
				((ReturnStatement)oldExp.getParent()).getExpression().delete();	
			}

		} else if(oldExp.getParent() instanceof SuperConstructorInvocation){
			
			if(((SuperConstructorInvocation)oldExp.getParent()).getExpression().equals(oldExp)){
				
				((SuperConstructorInvocation)oldExp.getParent()).getExpression().delete();	
				
			} else if(((SuperConstructorInvocation)oldExp.getParent()).arguments().contains(oldExp)){
				
				((SuperConstructorInvocation)oldExp.getParent()).arguments().remove(oldExp);	
			}

		} else if(oldExp.getParent() instanceof SwitchCase){
			
			if(((SwitchCase)oldExp.getParent()).getExpression().equals(oldExp)){
				
				((SwitchCase)oldExp.getParent()).getExpression().delete();;		
			}
			
		} else if(oldExp.getParent() instanceof SwitchStatement){
			
			if(((SwitchStatement)oldExp.getParent()).getExpression().equals(oldExp)){
				
				((SwitchStatement)oldExp.getParent()).delete();		
			}//TODO check if the switch statments are treated along with the switch cases, but it should be treated as they are statements
			
		} else if(oldExp.getParent() instanceof SynchronizedStatement){
			
			if(((SynchronizedStatement)oldExp.getParent()).getExpression().equals(oldExp)){
				
				((SynchronizedStatement)oldExp.getParent()).delete();		
			}
			
		} else if(oldExp.getParent() instanceof ThrowStatement){
			
			if(((ThrowStatement)oldExp.getParent()).getExpression().equals(oldExp)){
				
				((ThrowStatement)oldExp.getParent()).delete();		
			}
			
		} else if(oldExp.getParent() instanceof TryStatement){
			//TODO check the resources and catch clauses, althought as they are based on paremeters and blocks, stetemenst, they should be treated too
			
		} else if(oldExp.getParent() instanceof TypeDeclarationStatement){
			//this type wraps TypeDeclaration (class declaration) and EnumDeclaration (ennum declaration)
			
		} else if(oldExp.getParent() instanceof VariableDeclarationStatement){
			//this si treated in VariableDeclarationFragment
			
		} else if(oldExp.getParent() instanceof WhileStatement){
			
			if(((WhileStatement)oldExp.getParent()).getExpression().equals(oldExp)){
				
				((WhileStatement)oldExp.getParent()).delete();		
			}
		}
	}
	
	private static void replaceExpressionInParentNodeExpression(ASTNode oldExp){//below if the used element is in a mandatoru=y expression then delete the whole parent node 
		
		//ASTNode ast = ASTNode.copySubtree(oldExp.getAST(), newExp);
		
		if(oldExp.getParent() instanceof Annotation){
			//TODO the diffrent anotations are not treated, especially the SingleMemberAnnotation wich has an expression 
			
		} else if(oldExp.getParent() instanceof ArrayAccess){
			
			if(((ArrayAccess)oldExp.getParent()).getIndex().equals(oldExp) || ((ArrayAccess)oldExp.getParent()).getArray().equals(oldExp)){
				
				
				try{
					((ArrayAccess)oldExp.getParent()).delete();//delete in both cases where like x.y.deletdprop[0] or z[x.y.deletdprop]
				} catch (IllegalArgumentException e){
					replaceExpressionInParentNode(oldExp.getParent()); //it should be treated by ExpressionStatement
				}
			}
			//TODO check if we must not rather delete the statment containing it

		} else if(oldExp.getParent() instanceof ArrayCreation){
			
			if(((ArrayCreation)oldExp.getParent()).dimensions().contains(oldExp)){
				
				((ArrayCreation)oldExp.getParent()).dimensions().remove(oldExp);					
			}

		} else if(oldExp.getParent() instanceof ArrayInitializer){
			
			if(((ArrayInitializer)oldExp.getParent()).expressions().contains(oldExp)){
				
				((ArrayInitializer)oldExp.getParent()).expressions().remove(oldExp);					
			}
			
		} else if(oldExp.getParent() instanceof Assignment){
			
			if(((Assignment)oldExp.getParent()).getLeftHandSide().equals(oldExp) ||
					((Assignment)oldExp.getParent()).getRightHandSide().equals(oldExp)){
				
				try{
					((Assignment)oldExp.getParent()).delete();
				} catch (IllegalArgumentException e){
					replaceExpressionInParentNode(oldExp.getParent()); //it should be treated by ExpressionStatement
				}
			}
			
		} else if(oldExp.getParent() instanceof BooleanLiteral){
			
		} else if(oldExp.getParent() instanceof CastExpression){
			
			if(((CastExpression)oldExp.getParent()).getExpression().equals(oldExp)){
				
				try{
					((CastExpression)oldExp.getParent()).delete();//delete in both cases where like x.y.deletdprop[0] or z[x.y.deletdprop]
				} catch (IllegalArgumentException e){
					replaceExpressionInParentNode(oldExp.getParent()); //it should be treated by ExpressionStatement
				}
			}
			
		} else if(oldExp.getParent() instanceof CharacterLiteral){
			
		} else if(oldExp.getParent() instanceof ClassInstanceCreation){
			
			if(((ClassInstanceCreation)oldExp.getParent()).arguments().contains(oldExp)){
				
				((ClassInstanceCreation)oldExp.getParent()).arguments().remove(oldExp);					
			}
			
		} else if(oldExp.getParent() instanceof ConditionalExpression){
			
			if(((ConditionalExpression)oldExp.getParent()).getExpression().equals(oldExp) ||
					((ConditionalExpression)oldExp.getParent()).getThenExpression().equals(oldExp) ||
					((ConditionalExpression)oldExp.getParent()).getElseExpression().equals(oldExp)){
					
				try{
					((ConditionalExpression)oldExp.getParent()).delete();//delete in both cases where like x.y.deletdprop[0] or z[x.y.deletdprop]
				} catch (IllegalArgumentException e){
					replaceExpressionInParentNode(oldExp.getParent()); //it should be treated by ExpressionStatement
				}
			}
			
		} else if(oldExp.getParent() instanceof FieldAccess){
			
			if(((FieldAccess)oldExp.getParent()).getExpression().equals(oldExp)){
				
				try{
					((FieldAccess)oldExp.getParent()).delete();//delete in both cases where like x.y.deletdprop[0] or z[x.y.deletdprop]
				} catch (IllegalArgumentException e){
					replaceExpressionInParentNode(oldExp.getParent()); //it should be treated by ExpressionStatement
				}
			}
			
		} else if(oldExp.getParent() instanceof InfixExpression){
			
			if(((InfixExpression)oldExp.getParent()).getLeftOperand().equals(oldExp)){
				
				if(((InfixExpression)oldExp.getParent()).extendedOperands().size() == 0){
					
					ResolutionsUtils.replaceDirectExpressionInParentNode(oldExp.getParent(), ((InfixExpression)oldExp.getParent()).getRightOperand());
					
				} else {
					
					InfixExpression infixExpression = (InfixExpression) oldExp.getAST().createInstance(InfixExpression.class);
					infixExpression.setLeftOperand((Expression) ASTNode.copySubtree(oldExp.getAST(), ((InfixExpression)oldExp.getParent()).getRightOperand()));
					infixExpression.setRightOperand((Expression) ASTNode.copySubtree(oldExp.getAST(), (ASTNode) ((InfixExpression)oldExp.getParent()).extendedOperands().get(0)));
					((InfixExpression)oldExp.getParent()).extendedOperands().remove(0);
					Iterator it = ((InfixExpression)oldExp.getParent()).extendedOperands().iterator();
					while(it.hasNext()){
						infixExpression.extendedOperands().add(ASTNode.copySubtree(oldExp.getAST(), (ASTNode) it.next()));
					}
					infixExpression.setOperator(((InfixExpression)oldExp.getParent()).getOperator());
					ResolutionsUtils.replaceDirectExpressionInParentNode(oldExp.getParent(), infixExpression);
				}
				
				
			} else if(((InfixExpression)oldExp.getParent()).getRightOperand().equals(oldExp)){
				
				if(((InfixExpression)oldExp.getParent()).extendedOperands().size() == 0){
					
					ResolutionsUtils.replaceDirectExpressionInParentNode(oldExp.getParent(), ((InfixExpression)oldExp.getParent()).getLeftOperand());
					
				} else {
					
					InfixExpression infixExpression = (InfixExpression) oldExp.getAST().createInstance(InfixExpression.class);
					infixExpression.setLeftOperand((Expression) ASTNode.copySubtree(oldExp.getAST(), ((InfixExpression)oldExp.getParent()).getLeftOperand()));
					infixExpression.setRightOperand((Expression) ASTNode.copySubtree(oldExp.getAST(), (ASTNode) ((InfixExpression)oldExp.getParent()).extendedOperands().get(0)));
					((InfixExpression)oldExp.getParent()).extendedOperands().remove(0);
					Iterator it = ((InfixExpression)oldExp.getParent()).extendedOperands().iterator();
					while(it.hasNext()){
						infixExpression.extendedOperands().add(ASTNode.copySubtree(oldExp.getAST(), (ASTNode) it.next()));
					}
					infixExpression.setOperator(((InfixExpression)oldExp.getParent()).getOperator());
					ResolutionsUtils.replaceDirectExpressionInParentNode(oldExp.getParent(), infixExpression);
				}	
				
			} else if(((InfixExpression)oldExp.getParent()).extendedOperands().contains(oldExp)){
				
				((InfixExpression)oldExp.getParent()).extendedOperands().remove(oldExp);					
			}
			
		} 
		else if(oldExp.getParent() instanceof InstanceofExpression){

			if(((InstanceofExpression)oldExp.getParent()).getLeftOperand().equals(oldExp)){
				
				try{
					((InstanceofExpression)oldExp.getParent()).delete();//delete in both cases where like x.y.deletdprop[0] or z[x.y.deletdprop]
				} catch (IllegalArgumentException e){
					replaceExpressionInParentNode(oldExp.getParent()); //it should be treated by ExpressionStatement
				}
			}
			
		} else if(oldExp.getParent() instanceof LambdaExpression){
			//TODO not treated here, but should already be treated as it has an expression and praamreters a s var declarations
			
		} else if(oldExp.getParent() instanceof MethodInvocation){
		
				if(((MethodInvocation)oldExp.getParent()).arguments().contains(oldExp)){
					
					((MethodInvocation)oldExp.getParent()).arguments().remove(oldExp);
					
				} 
//				else if(((MethodInvocation)oldExp.getParent()).getExpression().equals(oldExp)){// here thsi case is treated by another call to these methods here
//					 
//					//((MethodInvocation)oldExp.getParent()).setExpression((Expression) ast);	
//				}
				
		} else if(oldExp.getParent() instanceof MethodReference){
			
			if(oldExp.getParent() instanceof ExpressionMethodReference && ((ExpressionMethodReference)oldExp.getParent()).getExpression().equals(oldExp)){
				 
				try{
					((ExpressionMethodReference)oldExp.getParent()).delete();//delete in both cases where like x.y.deletdprop[0] or z[x.y.deletdprop]
				} catch (IllegalArgumentException e){
					replaceExpressionInParentNode(oldExp.getParent()); //it should be treated by ExpressionStatement
				}
			}
			
		} else if(oldExp.getParent() instanceof Name){
			
		} else if(oldExp.getParent() instanceof NullLiteral){
			
		} else if(oldExp.getParent() instanceof NumberLiteral){
			
		} else if(oldExp.getParent() instanceof ParenthesizedExpression){

			if(((ParenthesizedExpression)oldExp.getParent()).getExpression().equals(oldExp)){
				
				try{
					((ParenthesizedExpression)oldExp.getParent()).delete();//delete in both cases where like x.y.deletdprop[0] or z[x.y.deletdprop]
				} catch (IllegalArgumentException e){
					replaceExpressionInParentNode(oldExp.getParent()); //it should be treated by ExpressionStatement
				}
			}
			 
		} else if(oldExp.getParent() instanceof PostfixExpression){

			if(((PostfixExpression)oldExp.getParent()).getOperand().equals(oldExp)){
				
				try{
					((PostfixExpression)oldExp.getParent()).delete();//delete in both cases where like x.y.deletdprop[0] or z[x.y.deletdprop]
				} catch (IllegalArgumentException e){
					replaceExpressionInParentNode(oldExp.getParent()); //it should be treated by ExpressionStatement
				}
			}
			
		} else if(oldExp.getParent() instanceof PrefixExpression){

			if(((PrefixExpression)oldExp.getParent()).getOperand().equals(oldExp)){
				
				try{
					((PrefixExpression)oldExp.getParent()).delete();//delete in both cases where like x.y.deletdprop[0] or z[x.y.deletdprop]
				} catch (IllegalArgumentException e){
					replaceExpressionInParentNode(oldExp.getParent()); //it should be treated by ExpressionStatement
				}
			}
			
		} else if(oldExp.getParent() instanceof StringLiteral){
			
		} else if(oldExp.getParent() instanceof SuperFieldAccess){
			//TODO here the class and prop as in names, not expression
		} else if(oldExp.getParent() instanceof SuperMethodInvocation){
			
			if(((SuperMethodInvocation)oldExp.getParent()).arguments().contains(oldExp)){
				
				((SuperMethodInvocation)oldExp.getParent()).arguments().remove(oldExp);
			
			} else {
				//TODO here the case wehre Class.super.deletedProp(), => delete the whole expressio ??
			}
			
		} else if(oldExp.getParent() instanceof ThisExpression){
			
		} else if(oldExp.getParent() instanceof TypeLiteral){
			
		} else if(oldExp.getParent() instanceof VariableDeclarationExpression){
			
		}
	}
}
