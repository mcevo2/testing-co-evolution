package Utilities;

import java.util.ArrayList;

import fr.lip6.meta.ComplexChangeDetection.Change;
import fr.lip6.meta.ComplexChangeDetection.AtomicChanges.DeleteClass;
import fr.lip6.meta.ComplexChangeDetection.AtomicChanges.RenameClass;
import fr.lip6.meta.ComplexChangeDetection.AtomicChanges.RenameProperty;

public class ChangeDetection {
	public static ArrayList<Change> initializeChangements()
	{
		//RenameClass renameClass = new RenameClass("Person","Contact","???"); 
		//RenameClass renameClass1 = new RenameClass("Loc","Address","???"); 
		
		 //RenameProperty renameProperty = new RenameProperty("getList","getSortedList","???");
		// RenameProperty renameProperty1 = new RenameProperty("Num","Number","???");
		 
	//	RenameClass renameClass = new RenameClass("Feature","Featuretoto","org.eclipse.ocl.pivot"); 
		//RenameClass renameClass1 = new RenameClass("Import","Importtoto","org.eclipse.ocl.pivot"); 
		//RenameClass renameClass2= new RenameClass("Annotation","Annotationtoto","org.eclipse.ocl.pivot"); 
		
	//DeleteClass deleteClass =  new DeleteClass("Addresses");
	DeleteClass deleteClass =  new DeleteClass("SelfType");
		ArrayList<Change> changes = new ArrayList<Change>();
//  changes.add(renameClass);
	//  changes.add(renameClass1);
	  
	 //  changes.add(renameClass1);
	  // changes.add(renameClass2);
	 
	   // changes.add(renameProperty);
	    //changes.add(renameProperty1);
		changes.add(deleteClass);
	    
	    return changes;
	}

}
