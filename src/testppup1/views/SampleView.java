package testppup1.views;


import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.*;

import Utilities.ASTManager;
import Utilities.ASTModificationManager;
import Utilities.ErrorsRetriever;
import Utilities.UtilProjectParser;
import fr.lip6.meta.ComplexChangeDetection.Change;
import fr.lip6.meta.ComplexChangeDetection.AtomicChanges.RenameClass;
import fr.lip6.meta.ComplexChangeDetection.AtomicChanges.RenameProperty;

import org.eclipse.jface.viewers.*;
import org.eclipse.swt.graphics.Image;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.commands.IHandlerListener;
import org.eclipse.core.internal.resources.ResourceException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.QualifiedName;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jface.action.*;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.*;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.SWT;

import java.util.ArrayList;

import javax.inject.Inject;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;


/**
 * This sample class demonstrates how to plug-in a new
 * workbench view. The view shows data obtained from the
 * model. The sample creates a dummy model on the fly,
 * but a real implementation would connect to the model
 * available either in this or another plug-in (e.g. the workspace).
 * The view is connected to the model using a content provider.
 * <p>
 * The view uses a label provider to define how model
 * objects should be presented in the view. Each
 * view can present the same model objects using
 * different labels and icons, if needed. Alternatively,
 * a single label provider can be shared between views
 * in order to ensure that objects of the same type are
 * presented in the same way everywhere.
 * <p>
 */

public class SampleView extends ViewPart implements IHandler {
	public SampleView() {
		System.out.println(" HERE IS HANDLER ");
	} 

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "testppup1.views.SampleView";

	@Inject IWorkbench workbench;
	
	private TableViewer viewer;
	private Action action1;
	private Action action2;
	private Action doubleClickAction;
	private Text txtEnterProject;
	 

	class ViewLabelProvider extends LabelProvider implements ITableLabelProvider {
		@Override
		public String getColumnText(Object obj, int index) {
			return getText(obj);
		}
		@Override
		public Image getColumnImage(Object obj, int index) {
			return getImage(obj);
		}
		@Override
		public Image getImage(Object obj) {
			return workbench.getSharedImages().getImage(ISharedImages.IMG_OBJ_ELEMENT);
		}
	}

	@Override
	public void createPartControl(Composite parent) {
		parent.setLayout(null);
		
		Button btnNewButton = new Button(parent, SWT.NONE);
		btnNewButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
			}
		});
		btnNewButton.setBounds(315, 48, 179, 89);
		btnNewButton.setText("New Button");
		viewer = new TableViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		Table table = viewer.getTable();
		table.setBounds(134, -141, 460, 574);
		
		viewer.setContentProvider(ArrayContentProvider.getInstance());
		viewer.setInput(new String[] { "One", "Two", "Three" });
	viewer.setLabelProvider(new ViewLabelProvider());

		// Create the help context id for the viewer's control
		workbench.getHelpSystem().setHelp(viewer.getControl(), "testppup1.viewer");
		getSite().setSelectionProvider(viewer);
		
		Label label = new Label(parent, SWT.NONE);
		label.setBounds(124, 318, 59, 14);
		
		txtEnterProject = new Text(parent, SWT.BORDER);
		txtEnterProject.setText("enter project");
		txtEnterProject.setBounds(418, 315, 64, 19);
		
		Button btnCheckButton = new Button(parent, SWT.CHECK);
		btnCheckButton.setBounds(292, 139, 115, 18);
		btnCheckButton.setText("Check Button");
		
		Button btnI = new Button(parent, SWT.CHECK);
		btnI.setBounds(315, 223, 94, 18);
		btnI.setText("i");
		makeActions();
		hookContextMenu();
		hookDoubleClickAction();
		contributeToActionBars();
	}

	private void hookContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				SampleView.this.fillContextMenu(manager);
			}
		});
		Menu menu = menuMgr.createContextMenu(viewer.getControl());
		viewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuMgr, viewer);
	}

	private void contributeToActionBars() {
		IActionBars bars = getViewSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
	}

	private void fillLocalPullDown(IMenuManager manager) {
		manager.add(action1);
		manager.add(new Separator());
		manager.add(action2);
	}

	private void fillContextMenu(IMenuManager manager) {
		manager.add(action1);
		manager.add(action2);
		// Other plug-ins can contribute there actions here
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}
	
	private void fillLocalToolBar(IToolBarManager manager) {
		manager.add(action1);
		manager.add(action2);
	}

	private void makeActions() {
		action1 = new Action() {
			public void run() {
				showMessage("Action 1 executed");
			}
		};
		action1.setText("Action 1");
		action1.setToolTipText("Action 1 tooltip");
		action1.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().
			getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));
		
		action2 = new Action() {
			public void run() {
				showMessage("Action 2 executed");
			}
		};
		action2.setText("Action 2");
		action2.setToolTipText("Action 2 tooltip");
		action2.setImageDescriptor(workbench.getSharedImages().
				getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));
		doubleClickAction = new Action() {
			public void run() {
				IStructuredSelection selection = viewer.getStructuredSelection();
				Object obj = selection.getFirstElement();
				showMessage("Double-click detected on "+obj.toString());
			}
		};
	}

	private void hookDoubleClickAction() {
		viewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				doubleClickAction.run();
			}
		});
	}
	private void showMessage(String message) {
		MessageDialog.openInformation(
			viewer.getControl().getShell(),
			"Sample View",
			message);
	}

	@Override
	public void setFocus() {
		viewer.getControl().setFocus();
	}

	@Override
	public void addHandlerListener(IHandlerListener arg0) {
		// TODO Auto-generated method stub
		System.out.println(" HERE IS HANDLER  LISTENER");
		
	}

	@Override
	public Object execute(ExecutionEvent arg0) throws ExecutionException {
		// TODO Auto-generated method stub
		System.out.println(" HERE IS HANDLER Execute ");
		int nbIter=10;
		//boolean MAX_ITERATIONS=false;
		IProject project =UtilProjectParser.getSelectedProject();
		
		ArrayList<ICompilationUnit> ListICompilUnit =UtilProjectParser.getCompilationUnits(project);
		RenameClass renameClass = new RenameClass("Person","Contact","mainTest"); 
		
		 RenameProperty renameProperty = new RenameProperty("getList","getSortedList","Test");
		 
		ArrayList<Change> changes = new ArrayList<Change>();
	    changes.add(renameClass);
	 
	    changes.add(renameProperty);
	    if(changes.get(1) instanceof RenameClass)
	 			System.out.println(" check changes 1");
	 		else System.out.println("changes check 2");
		for(ICompilationUnit iCompilUnit : ListICompilUnit){
			CompilationUnit compilUnit =ASTManager.getCompilationUnit(iCompilUnit);
			System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++");
		
				System.out.println(compilUnit);
			
			
			System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++");
			
			AST ast = compilUnit.getAST();
			//ASTModificationManager.AddImportDeclaration(compilUnit, new String[] {"java", "util", "Set"}); 
			
			try {
				
				ArrayList<IMarker> ml =ErrorsRetriever.findJavaProblemMarkers(iCompilUnit);
				System.out.println(" Length ooooooffff" +ml.size());
				
				int counter=0;
				int indice =0;
				
				while(!ml.isEmpty() && counter<nbIter && indice<ml.size()) {
					counter++;
					IMarker amarker= ml.get(indice);
					indice++;
					//ml.remove(indice);
				ASTNode node=	ASTManager.getErrorNode(compilUnit, amarker);
				System.out.println(" ERROR NODE   " +node);
				
				
			
				if (node instanceof SimpleName)
				{ 
					System.out.println(" in IF SIMPLENAME");
					for(Change c : changes){
						System.out.println(" MY CHAAANGE IS "+ c.toString());
						if( c instanceof RenameClass ) {
							 System.out.println(" In rename class");
						  if ( ((SimpleName)node).getIdentifier().equals(((RenameClass)c).getName()))  
						  {
							 
							 // System.out.println(" NEW NAAAAME" +((RenameClass)c).getNewname());
								
					  ASTModificationManager.RenameSimpleName(compilUnit, node, ((RenameClass)c).getNewname());
					  
					  }
						}
						if ( c instanceof RenameProperty) {
							System.out.println(" in rename property");
							if ( ((SimpleName)node).getIdentifier().equals(((RenameProperty)c).getName()))  
							  {
								System.out.println(" in rename property");
								 // System.out.println(" NEW NAAAAME" +((RenameClass)c).getNewname());
									
						  ASTModificationManager.RenameSimpleName(compilUnit, node, ((RenameProperty)c).getNewname());
						  
						  }
							
						}
					  }
				
					
				}
				if (node instanceof QualifiedName)
				{
					//SimpleName sn5 = ast.newSimpleName("import");
					//SimpleName sn6 = ast.newSimpleName("addressBook.Person");
				/*	QualifiedName qn1 = ast.newQualifiedName(sn5, sn6);
					if ( ((SimpleName)node).getIdentifier().equals(qn1.getIdentifier()))
					{
						System.out.println(" getFilteredList error");
						ASTModificationManager.RenameSimpleName(compilUnit, node, "getSortedList");
					}
					*/
					ASTModificationManager.AddImportDeclaration(compilUnit,new String[] {"addressBook", "Conact"} );
					
					//ASTModificationManager.AddHelloStatement(compilUnit);
					
					
			//	ml =ErrorsRetriever.findJavaProblemMarkers(iCompilUnit);
				}	
				System.out.println(" HERE WE GET THE NEW ERROR LIST");
				ml =ErrorsRetriever.findJavaProblemMarkers(iCompilUnit);
				
				System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++");
				compilUnit =ASTManager.getCompilationUnit(iCompilUnit); // Refresh the compilation unit
				System.out.println(compilUnit);
			
			
			System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++");
			
					
				}	
			} catch (CoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
	
		return null;
	}

	@Override
	public boolean isEnabled() {
		// TODO Auto-generated method stub
		System.out.println(" HERE IS HANDLER isENABLED");
		
		return true;
	}

	@Override
	public boolean isHandled() {
		// TODO Auto-generated method stub
		System.out.println(" HERE IS HANDLER IS HANDLED ");
		return true;
	}

	@Override
	public void removeHandlerListener(IHandlerListener arg0) {
		System.out.println(" HERE IS HANDLER REMOVE Listener");
		// TODO Auto-generated method stub
		
	}
}
