package testppup1.views;


import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.*;

import Utilities.ASTManager;
import Utilities.ASTModificationManager;
import Utilities.ChangeDetection;
import Utilities.DeleteClassResolution;
import Utilities.ErrorsRetriever;
import Utilities.JavaParser;
import Utilities.JavaVisitor;
import Utilities.Resolutions;
import Utilities.Usage;
import Utilities.UsagePattern;
import Utilities.UsesManager;
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
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ExpressionStatement;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.QualifiedName;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;
import org.eclipse.jface.action.*;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.*;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.SWT;

import java.util.ArrayList;
import java.util.Map;

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
		ArrayList<Change> myChanges =ChangeDetection.initializeChangements();
		//ArrayList <ASTNode> declarations = new ArrayList <ASTNode>();

		IProject project =UtilProjectParser.getSelectedProject();
		ArrayList<ICompilationUnit> ListICompilUnit =UtilProjectParser.getCompilationUnits(project);


		ASTNode adeclaration=null;
		ASTNode anImport=null;
		JavaParser jp = new JavaParser();
		int cpterrors=0;


		for(ICompilationUnit iCompilUnit : ListICompilUnit){

			System.out.println("Compilation unit : "+iCompilUnit.getElementName());
			CompilationUnit compilUnit =ASTManager.getCompilationUnit(iCompilUnit); // Visitor used

			try {

				ArrayList<IMarker> ml =ErrorsRetriever.findJavaProblemMarkers(iCompilUnit);

				while(!ml.isEmpty()) {
					System.out.println(" Total number of errors "+ml.size());

					IMarker amarker= ml.get(0);
					ml.remove(0);

					//System.out.println(" INDICE "+indice);

					System.out.println(" CURRENT ERROR "+amarker);


					ASTNode node=	ASTManager.getErrorNode(compilUnit, amarker);

					for(Change change :myChanges)
					{
						Usage usage = UsesManager.classify(change, amarker, compilUnit);
						if( usage.getPattern()==null)
						{
							System.out.println(" Pattern null of node "+node);
						}
						else {
						switch (usage.getPattern()) {
						case variableDeclarationRename:
							System.out.println(" Rename var declar");
							Resolutions.renaming(compilUnit, usage,  (((RenameClass)change).getNewname()));
							compilUnit =ASTManager.getCompilationUnit(iCompilUnit); // Refresh the compilation unit
							//	ml =ErrorsRetriever.findJavaProblemMarkers(iCompilUnit);
							break;
						case createObjectRename:
							Resolutions.renaming(compilUnit, usage,  (((RenameClass)change).getNewname()));
							compilUnit =ASTManager.getCompilationUnit(iCompilUnit); // Refresh the compilation unit
							
							break;
						case getObjectRename:
							break;
						case setObjectRename:
							break;
						case inImportRename:
							System.out.println(" Rename import");
							Resolutions.renameImport(compilUnit, change, usage, amarker, (((RenameClass)change).getNewname()));
							compilUnit =ASTManager.getCompilationUnit(iCompilUnit); // Refresh the compilation unit
							//ml =ErrorsRetriever.findJavaProblemMarkers(iCompilUnit);
							break;
						case VariableDeclarationDelete:
							System.out.println(" Delete var declar");
							adeclaration= ASTManager.findFieldOrVariableDeclarations(node);

							if(adeclaration != null && (adeclaration instanceof FieldDeclaration || adeclaration instanceof VariableDeclarationStatement)){


								System.out.println(" FOUND DECLRATION   "+adeclaration);
								Resolutions.deleteUsedVariables(compilUnit,adeclaration);


								//compilUnit =jp.parse(iCompilUnit);//ASTManager.getCompilationUnit(iCompilUnit); // Refresh the compilation unit


								//	iCompilUnit=(ICompilationUnit) compilUnit.getJavaElement();

								//	ml =ErrorsRetriever.findJavaProblemMarkers(iCompilUnit);
								compilUnit =ASTManager.getCompilationUnit(iCompilUnit); // Refresh the compilation unit

								//System.out.println( "SOURCE CODE AFTER ITER ");
								//System.out.println(iCompilUnit.getSource());
								System.out.println(" NEW NUMBER OF ERRORS "+ml.size());


							}
							break;
						case VariableUseDelete:
							System.out.println(" Delete var use");
							break;
						case parameterDelete:
							System.out.println(" Delete parameter");
							ASTNode foundParameter = ASTManager.findParameterInMethodDeclaration(node);

							if(foundParameter != null && foundParameter instanceof SingleVariableDeclaration){

								Resolutions.deleteParameter(compilUnit, foundParameter);
								compilUnit =ASTManager.getCompilationUnit(iCompilUnit); // Refresh the compilation unit

							}
							break;
						case inImportDelete:
							System.out.println(" Delete import");
							anImport =ASTManager.findImportDeclaration(node);
							if(anImport != null && anImport instanceof ImportDeclaration){

								//System.out.println("deleting of import "+foundImport.getName().getFullyQualifiedName());			
								Resolutions.deleteImport(compilUnit,change,node);
								compilUnit =ASTManager.getCompilationUnit(iCompilUnit); // Refresh the compilation unit


							}
							break;


						default:
							System.out.println(" No pattern matches ");
							break;

						}
					}
					}



					ASTNode foundInstanceCreation = ASTManager.findClassInstanceCreations(node);
					//here treat when we have new DeletedType() alone not in a variable declaration
					if(foundInstanceCreation != null && foundInstanceCreation instanceof ClassInstanceCreation){
						//System.out.println(" FOUND Instance");
						Resolutions.deleteInstanceClass(compilUnit, foundInstanceCreation);
						compilUnit =ASTManager.getCompilationUnit(iCompilUnit); // Refresh the compilation unit

					}




					for ( IMarker mr : ml )
					{
						System.out.println(" ERROR AFTER iter  "+mr);
					}




				}


			} 
			catch (CoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		System.out.println(" HOW MANY ERRORS  "+cpterrors);

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
	public int[] findCaracter(String node, String strFind)
	{

		int count = 0, fromIndex = 0;
		int[] pos= null;

		while ((fromIndex = node.indexOf(strFind, fromIndex)) != -1 ){

			System.out.println("Found at index: " + fromIndex);
			pos[count]=fromIndex;
			count++;

			fromIndex++;

		}
		return pos;


	}
}
