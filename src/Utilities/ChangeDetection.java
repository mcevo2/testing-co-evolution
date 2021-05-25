package Utilities;

import java.util.ArrayList;

import fr.lip6.meta.ComplexChangeDetection.Change;
import fr.lip6.meta.ComplexChangeDetection.AtomicChanges.RenameClass;
import fr.lip6.meta.ComplexChangeDetection.AtomicChanges.RenameProperty;

public class ChangeDetection {
	public static ArrayList<Change> initializeChangements()
	{
		RenameClass renameClass = new RenameClass("Person","Contact","mainTest"); 
		
		 RenameProperty renameProperty = new RenameProperty("getList","getSortedList","Test");
		 
		ArrayList<Change> changes = new ArrayList<Change>();
	    changes.add(renameClass);
	 
	    changes.add(renameProperty);
	    
	    return changes;
	}

}
