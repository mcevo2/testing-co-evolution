package Utilities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaModelMarker;

public class ErrorsRetriever {
	
	// public static IMarker[] findJavaProblemMarkers(ICompilationUnit cu) 
	public static ArrayList<IMarker> findJavaProblemMarkers(ICompilationUnit cu) 

		      throws CoreException {

	

		 //System.out.println(" Compilation unit path : "+ cu.getPath());

		 

		      IResource javaSourceFile = cu.getUnderlyingResource();

		      IMarker[] markers = 

		         javaSourceFile.findMarkers(IJavaModelMarker.JAVA_MODEL_PROBLEM_MARKER,

		            true, IResource.DEPTH_INFINITE);
		      
		      ArrayList<IMarker> list2 = new ArrayList<IMarker>();
		      for(IMarker m:markers) {
		    	 
		         list2.add(m);
		      }

		    // if(markers.length==0)

		        // System.out.println("No error detected ");

		      return list2;



		   }
	

}
