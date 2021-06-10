package Utilities;

import org.eclipse.core.resources.IMarker;
import org.eclipse.jdt.core.dom.ASTNode;

public class Usage {
	UsagePattern pattern;
	ASTNode node;
	IMarker error;
	int priority;
	boolean treated;
	public boolean isTreated() {
		return treated;
	}
	public void setTreated(boolean treated) {
		this.treated = treated;
	}
	public int getPriority() {
		return priority;
	}
	public void setPriority(int priority) {
		this.priority = priority;
	}
	public IMarker getError() {
		return error;
	}
	public void setError(IMarker error) {
		this.error = error;
	}
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
