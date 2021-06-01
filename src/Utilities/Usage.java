package Utilities;

import org.eclipse.jdt.core.dom.ASTNode;

public class Usage {
	UsagePattern pattern;
	ASTNode node;
	
	public UsagePattern getPattern() {
		return pattern;
	}
	public void setPattern(UsagePattern pattern) {
		this.pattern = pattern;
	}
	public ASTNode getNode() {
		return node;
	}
	public void setNode(ASTNode node) {
		this.node = node;
	} 
	
	

}
