package Utilities;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;

public class Resolutions {
	public static void renaming( CompilationUnit cu,ASTNode an, String newName)
	{
		ASTModificationManager.RenameSimpleName(cu, an, newName);
	}

}
