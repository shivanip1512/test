package com.cannontech.esub.editor;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.util.Timer;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.undo.UndoManager;
import javax.swing.undo.UndoableEdit;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.editor.PropertyPanelEvent;
import com.cannontech.common.editor.PropertyPanelListener;
import com.cannontech.common.gui.util.SplashWindow;
import com.cannontech.common.login.ClientSession;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.PoolManager;
import com.cannontech.database.cache.DefaultDatabaseCache;
import com.cannontech.esub.*;
import com.cannontech.esub.element.CurrentAlarmsTable;
import com.cannontech.esub.element.DrawingElement;
import com.cannontech.esub.element.DrawingMetaElement;
import com.cannontech.esub.element.DynamicGraphElement;
import com.cannontech.esub.util.DrawingUpdater;
import com.cannontech.roles.application.EsubEditorRole;

import com.loox.jloox.LxComponent;
import com.loox.jloox.LxGraph;
import com.loox.jloox.LxMouseAdapter;
import com.loox.jloox.LxMouseEvent;
import com.loox.jloox.LxMouseListener;
import com.loox.jloox.LxView;

/**
 * Main editor class.
 * Creation date: (12/11/2001 3:53:52 PM)
 * @author: alauinger
 */
public class Editor extends JPanel {
	
	private static final String APPLICATION_NAME = "Esubstation Editor";
	private static final Dimension defaultSize = new Dimension(800, 600);

	// the drawing to edit
	// Synchronize on the drawing to stop any updates
	// from happening 
	private Drawing drawing;
	
	private UndoManager undoManager;
	
	// timer to update the drawing
	private Timer drawingUpdateTimer;
	private DrawingUpdater drawingUpdater;
	
	// JDialog to re-use
	private JDialog propertyDialog;

	// Place to keep track of a new element being created
	ElementPlacer elementPlacer;

	// All the actions for this editor
	EditorActions editorActions;
	
	// Handles clicks on the LxView	
	private final MouseListener viewMouseListener = new MouseAdapter() {
		public void mousePressed(MouseEvent evt) {
			if (elementPlacer.isPlacing()) {
							
				elementPlacer.setXPosition(evt.getX());
				elementPlacer.setYPosition(evt.getY());
				configureObject(elementPlacer);
				
			}
		}
	};
	
	// Handles double clicks on an LxElement in the LxView
	private final LxMouseListener editElementMouseListener = new LxMouseAdapter() {
		public void mouseDoubleClicked(LxMouseEvent evt) {
			synchronized(getDrawing()) {				
				editElement(evt.getLxComponent());
			}
		}		
	};
	
	/* Get a notification when something is selected */
	private final ItemListener itemListener = new ItemListener() {
		public void itemStateChanged(ItemEvent e) {
			synchActionsWithSelection();
		}
	};
	
	/**
	 * Creates a new editor
	 */
	public Editor() {
		super();
		initEditor(this);
	}
	/**
	 * When an element is ready to be placed this
	 * method will actually put it onto the drawing
	 * Creation date: (12/13/2001 12:07:21 PM)
	 * @param placer ElementPlacer
	 */
	void configureObject(final ElementPlacer placer) {

		SwingUtilities.invokeLater(new Runnable() {

			public void run() {
				
				synchronized(getDrawing()) {

				try {
					LxComponent elem = placer.getElement();
					elem.setCenter(
						placer.getXPosition(),
						placer.getYPosition());

					if (elem instanceof DrawingElement) {
						((DrawingElement) elem).setDrawing(getDrawing());
					}
					getDrawing().getLxGraph().add(elem);
	
					editElement(elem);
				} catch (Exception e) {
					e.printStackTrace();
				} finally { //make to always get back into not placing mode
					elementPlacer.setIsPlacing(false);
					getDrawing().getLxView().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
				}
				
				}
			}
		});
	}
	
	/**
	 * Creation date: (12/18/2001 2:53:17 PM)
	 * @param elem com.loox.jloox.LxElement
	 */
	void editElement(final LxComponent elem) {
		boolean retVal = false;
		
		//no more updates for a bit
		stopUpdating();
		
		
		if (propertyDialog == null)
			propertyDialog =
				new JDialog(
					com.cannontech.common.util.CtiUtilities.getParentFrame(
						getDrawing().getLxView()),
					true);
		
		final com.cannontech.common.editor.PropertyPanel editor =
			com
				.cannontech
				.esub
				.editor
				.element
				.ElementEditorFactory
				.getInstance()
				.createEditorPanel(elem.getClass());

		if( editor != null ) {
			editor.addPropertyPanelListener(new PropertyPanelListener() {
				public void selectionPerformed(PropertyPanelEvent e) {
					if (e.getID() == PropertyPanelEvent.CANCEL_SELECTION) {
						
					} else if (e.getID() == PropertyPanelEvent.OK_SELECTION) {
						editor.getValue(elem);						
					}
					propertyDialog.setVisible(false);								
				}
			});
			
			getDrawing().getLxGraph().startUndoEdit("edit object");
			editor.getPropertyButtonPanel().getApplyJButton().setVisible(false);
			editor.setValue(elem);
			propertyDialog.setContentPane(editor);		
			propertyDialog.pack();
			propertyDialog.setLocationRelativeTo(CtiUtilities.getParentFrame(getDrawing().getLxView()));
			propertyDialog.show();
			getDrawing().getLxGraph().cancelUndoEdit();
			
			// start the updates again
			startUpdating();
			
		}
	}

	/**
	 * Creation date: (12/17/2001 2:02:26 PM)
	 * @return java.lang.String
	 */
	java.lang.String getOpenFile() {
		return getDrawing().getFileName();
	}
	/**
	 * Initialize the editor on the given panel
	 * and optionally load a default drawing.
	 * Creation date: (12/11/2001 3:59:21 PM)
	 */
	private void initEditor(final JPanel p) {
		//editPopup.add(deletePopupItem);
		//	Lx.setActionProcessor(actionProcessor);

		final EditorPrefs prefs = EditorPrefs.getPreferences();
		
		drawing = new Drawing();
		
		elementPlacer = new ElementPlacer();
		undoManager = new UndoManager() {
			public boolean addEdit(UndoableEdit e) {
// uncomment for debugging	System.out.println(e.getPresentationName());
				return super.addEdit(e);
			}			
		};
				
		final LxGraph lxGraph = getDrawing().getLxGraph();
		final LxView lxView = getDrawing().getLxView();
		
		drawing.getMetaElement().setDrawingWidth(prefs.getDefaultDrawingWidth());
		drawing.getMetaElement().setDrawingHeight(prefs.getDefaultDrawingHeight());
		
		lxGraph.addUndoableEditListener(undoManager);
		lxGraph.addItemListener(itemListener);
		
		final JPanel viewPanel = new JPanel();
		viewPanel.setLayout(new FlowLayout());		
		viewPanel.add(lxView);
		
		final JScrollPane scrollPane = new JScrollPane();
		scrollPane.setViewportView(viewPanel);
		
		editorActions = new EditorActions(this);
		EditorMenus editorMenus = new EditorMenus(editorActions);
		EditorToolBar editorToolBar = new EditorToolBar(editorActions);
		EditorKeys editorKeys = new EditorKeys(editorActions);
		final JMenuBar menuBar = editorMenus.getMenuBar();
		final JPopupMenu popupMenu = editorMenus.getPopupMenu();
		JToolBar toolBar = editorToolBar.getToolBar();

		p.setLayout(new java.awt.BorderLayout());
		p.add(scrollPane, java.awt.BorderLayout.CENTER);
		p.add(menuBar, java.awt.BorderLayout.NORTH);
		p.add(toolBar, java.awt.BorderLayout.WEST);
		lxView.addMouseListener(viewMouseListener);				
		lxView.addMouseListener(new MouseAdapter() { 
			public void mousePressed(MouseEvent e) {
				maybeShowPopup(e);
			}
			public void mouseReleased(MouseEvent e) {
				maybeShowPopup(e);
			}
			private void maybeShowPopup(MouseEvent e) {
				if (e.isPopupTrigger()) {
					popupMenu.show(e.getComponent(),
							   e.getX(), e.getY());
				}
			}
		});
		
		updateSize();
		
		synchActionsWithSelection();
		startUpdating();		
	}

	public void openDrawing() {
		// Give an option to save the current graph if modified.
		int r = saveOption();
		if (r != JOptionPane.CANCEL_OPTION) {

			JFileChooser fileChooser =
				Util.getDrawingJFileChooser();
			fileChooser.setApproveButtonText("Open");

			String currentDir = EditorPrefs.getPreferences().getWorkingDir();

			fileChooser.setCurrentDirectory(new File(currentDir));
			int returnVal = fileChooser.showOpenDialog(null);

			if (returnVal == JFileChooser.APPROVE_OPTION) {

				// Remove old components.
				String newDrawing = fileChooser.getSelectedFile().getPath();
				loadDrawing(newDrawing);

				try {
					EditorPrefs.getPreferences().setWorkingDir(
						fileChooser
							.getSelectedFile()
							.getParentFile()
							.getCanonicalPath());
				} catch (IOException ioe) {
					ioe.printStackTrace();
				}

				//HACKHACK
				currentDir = EditorPrefs.getPreferences().getWorkingDir();
				com.cannontech.common.gui.image.ImageChooser.getInstance().setCurrentDirectory(
					new File(currentDir));

			}
		}
	}
	public void newDrawing() {
		Drawing d = getDrawing();
		
		d.clear();	
		
		//set the size of the new drawing, since we cleared the old one
		DrawingMetaElement dme = d.getMetaElement();
		EditorPrefs prefs = EditorPrefs.getPreferences();		
		dme.setDrawingWidth(prefs.getDefaultDrawingWidth());
		dme.setDrawingHeight(prefs.getDefaultDrawingHeight());
		//getUndoManager().discardAllEdits();  
		setFrameTitle("Untitled");
	}

	/**
	 * Load a .jlx file into the editor
	 * Creation date: (12/11/2001 4:02:39 PM)
	 * @param drawingFile
	 */
	public void loadDrawing(final String drawingFile) {
		
		try {
		setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
								
		stopUpdating();	
		getDrawing().load(drawingFile);

		LxComponent[] comps = getDrawing().getLxGraph().getComponents();
		for (int i = 0; i < comps.length; i++) {
			setBehavior(comps[i]);
		}

		elementPlacer.setIsPlacing(false);
		setFrameTitle(drawingFile);

		updateSize();
		startUpdating();
		
		} 
		finally {
				
			int currentWidth = getDrawing().getMetaElement().getDrawingWidth();
			int currentHeight = getDrawing().getMetaElement().getDrawingHeight();
			EditorPrefs.getPreferences().setDefaultDrawingWidth(currentWidth);
			EditorPrefs.getPreferences().setDefaultDrawingHeight(currentHeight);
			
			getUndoManager().discardAllEdits();
			setCursor(Cursor.getDefaultCursor());
		}
	}

	/**
	 * Creation date: (12/11/2001 3:54:10 PM)
	 * @param args java.lang.String[]
	 */
	public static void main(String[] args) {
		System.setProperty("cti.app.name", APPLICATION_NAME);
		CtiUtilities.setLaF();
		JFrame frame = new JFrame();

		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			}
		});
		
		SplashWindow splash = new SplashWindow(
			frame,
			"ctismall.gif",
			"Loading " + System.getProperty("cti.app.name") + "...",
			new Font("dialog", Font.BOLD, 14 ), Color.black, Color.blue, 2 );
	
		frame.setSize(defaultSize);
		frame.setTitle("Untitled");		
		ImageIcon icon = new ImageIcon(ClassLoader.getSystemResource("esubEditorIcon.gif"));
		frame.setIconImage(icon.getImage());

		ClientSession session = ClientSession.getInstance(); 
		if(!session.establishSession(frame)) {
			System.exit(-1);			
		}
	  	
		if(session == null) {
			System.exit(-1);
		}
		
		if(!session.checkRole(EsubEditorRole.ROLEID)) {
			JOptionPane.showMessageDialog(null, "User: '" + session.getUser().getUsername() + "' is not authorized to use this application, exiting.", "Access Denied", JOptionPane.WARNING_MESSAGE);
			System.exit(-1);				
		}
		
		Editor editor = new Editor();

		frame.getContentPane().add(editor);
	
		frame.pack();
		frame.show();

		
		//get this stuff loaded into the cache asap
		DefaultDatabaseCache.getInstance().getAllDevices();
		DefaultDatabaseCache.getInstance().getAllPoints();
		DefaultDatabaseCache.getInstance().getAllStateGroups();
		
//		fire up the db change listener
		DefaultDatabaseCache.getInstance().addDBChangeListener(new DBChangeCaptain());	
		
while(true) {
try {
	Thread.sleep(5000);
} catch (InterruptedException e1) {
	// TODO Auto-generated catch block
	e1.printStackTrace();
}
		CTILogger.debug("TEST DEBUG MESSAGE FROM ESUB");
		CTILogger.info("TEST INFO MESSAGE FROM ESUB");
		CTILogger.warn("TEST WARN MESSAGE FROM ESUB");
		CTILogger.error("TEST ERROR MESSAGE FROM ESUB");
		CTILogger.fatal("TEST FATAL MESSAGE FROM ESUB");
}
	}
	/**
	 * Creation date: (12/12/2001 3:29:49 PM)
	 */

	void saveAsDrawing() {

		JFileChooser fileChooser =
			Util.getDrawingJFileChooser();
		fileChooser.setApproveButtonText("Save");
		fileChooser.setDialogTitle("Save Drawing");
		String currentDir = EditorPrefs.getPreferences().getWorkingDir();
		fileChooser.setCurrentDirectory(new File(currentDir));

		int returnVal = fileChooser.showSaveDialog(null);

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			String selectedFile = fileChooser.getSelectedFile().getPath();
			
			getDrawing().setFileName(selectedFile);
			saveDrawing();
			//getDrawing().save(selectedFile);
			setFrameTitle(getDrawing().getFileName());

			try {
				EditorPrefs.getPreferences().setWorkingDir(
					fileChooser
						.getSelectedFile()
						.getParentFile()
						.getCanonicalPath());
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}

			//HACKHACK
			currentDir = EditorPrefs.getPreferences().getWorkingDir();
			com.cannontech.common.gui.image.ImageChooser.getInstance().setCurrentDirectory(
				new File(currentDir));
				
			int currentWidth = getDrawing().getMetaElement().getDrawingWidth();
			int currentHeight = getDrawing().getMetaElement().getDrawingHeight();
			EditorPrefs.getPreferences().setDefaultDrawingWidth(currentWidth);
			EditorPrefs.getPreferences().setDefaultDrawingHeight(currentHeight);

		}

	} 
	
	public void saveDrawing() {	
		try {			
			getDrawing().save();
		}
		catch(Exception e) {
			CTILogger.error("Error saving drawing", e);
			JOptionPane.showMessageDialog(com.cannontech.common.util.CtiUtilities.getParentFrame(
			getDrawing().getLxView()), "An error occured saving the drawing", "Saving Error", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	/**
	 * Creation date: (12/12/2001 3:28:20 PM)
	 * @return int
	 */
	int saveOption() {

		int result = 0;
		if (getDrawing().isModified()) {
			JOptionPane option = new JOptionPane();
			result =
				JOptionPane.showConfirmDialog(
					null,
					" Save changes ",
					" Save changes ",
					JOptionPane.YES_NO_CANCEL_OPTION);

			if (result == JOptionPane.YES_OPTION)
				saveAsDrawing();
		}
		return result;
	} // saveOption()

	/**
	 * Addes gui related interaction to an element
	 * Creation date: (12/19/2001 3:13:22 PM)
	 * @param elem com.loox.jloox.LxElement
	 */
	void setBehavior(LxComponent elem) {
		if (elem instanceof com.cannontech.esub.element.DynamicText
			|| elem instanceof com.cannontech.esub.element.StateImage
			|| elem instanceof com.cannontech.esub.element.StaticImage
			|| elem instanceof com.cannontech.esub.element.StaticText 
			|| elem instanceof com.cannontech.esub.element.AlarmTextElement ) {				
			elem.setUserResizable(false);
			elem.removeDefaultDoubleClickBehavior();
			elem.removeMouseListener(editElementMouseListener);
			elem.addMouseListener(editElementMouseListener);
		}
		
		if( elem instanceof DynamicGraphElement ||
			elem instanceof CurrentAlarmsTable) {
			elem.removeDefaultDoubleClickBehavior();
			elem.removeMouseListener(editElementMouseListener);	
			elem.addMouseListener(editElementMouseListener);	
		}
		
	}
	/**
	 * Returns the drawing.
	 * @return Drawing
	 */
	public Drawing getDrawing() {
		return drawing;
	}

	/**
	 * Set the title of the parent frame
	 * Could be standard Frame or InternalFrame
	 * Does nothing if not in a frame
	 * @param title
	 */
	public void setFrameTitle(String title) {
		Frame pFrame = CtiUtilities.getParentFrame(this);
		if (pFrame != null) {
			pFrame.setTitle(title);
			return;
		}

		JInternalFrame piFrame = CtiUtilities.getParentInternalFrame(this);
		if (piFrame != null) {
			piFrame.setTitle(title);
			return;
		}

	}
	
	/**
	 * Displays the about dialog
	 */
	public void showAboutDialog() {
		
		AboutEditor aboutDialog = new AboutEditor(CtiUtilities.getParentFrame(getDrawing().getLxView()));
		aboutDialog.setModal(true);
		aboutDialog.setValue(null);
		aboutDialog.setLocationRelativeTo(getDrawing().getLxView());
		aboutDialog.setVisible(true);
		aboutDialog.dispose();
	}
	
	void updateSize() {
		final int width = getDrawing().getMetaElement().getDrawingWidth();
		final int height = getDrawing().getMetaElement().getDrawingHeight();
		final LxView view = getDrawing().getLxView();
		
		SwingUtilities.invokeLater( new Runnable() {
			public void run() {
				view.setPreferredSize(new Dimension(width,height));
				view.revalidate();					
			}
		});
	}
	
	void editDrawingProperties() {
		editElement(getDrawing().getMetaElement());				
		updateSize();
	}
	
	public void cutSelection() { 
		getDrawing().getLxGraph().cutSelection();
	}
	
	public void copySelection() { 
		getDrawing().getLxGraph().copySelection();
	}
	
	public void pasteSelection() {
		getDrawing().getLxGraph().pasteFromClipboard();
		
		// add behavior back to all these elements
		// would be nice to find a way to get at all the elemnts
		// just created!
		// visit here if paste gets slow!!!
		Object[] all = getDrawing().getLxGraph().getComponents();
		for(int i = 0; i < all.length; i++) {					
			if (all[i] instanceof DrawingElement) {
				((DrawingElement) all[i]).setDrawing(getDrawing());
			}
			if (all[i] instanceof LxComponent) {
				setBehavior((LxComponent) all[i]);
			}			
		}		
	}
	
	private void startUpdating() {
		drawingUpdater = new DrawingUpdater();	
		drawingUpdater.setUpdateGraphs(true);
		drawingUpdater.setDrawing( getDrawing() );
		drawingUpdateTimer = new Timer();
		drawingUpdateTimer.schedule(drawingUpdater, 0, 15000);
	}
	
	private void stopUpdating() {
		drawingUpdateTimer.cancel();
	}		
	
	void synchActionsWithSelection() {
		boolean cutNPaste = true;
		boolean align = true;
		boolean anySelected = false;
			
		Object comps[] = getDrawing().getLxGraph().getSelectedObjects();
		if(comps.length == 0) {
			cutNPaste = false;						
		}
		else {
			anySelected = true;			
			for(int i = 0; i < comps.length; i++) {
				if(comps[i] instanceof DrawingElement) {
					DrawingElement elem = (DrawingElement) comps[i];
					if(!elem.isCopyable()) {
						cutNPaste = false;					
					}
				}
			}
		}		
		
		if(comps.length < 2) {
			align = false;
		}
		
		editorActions.getAction(EditorActions.CUT_ELEMENT).setEnabled(cutNPaste);
		editorActions.getAction(EditorActions.COPY_ELEMENT).setEnabled(cutNPaste);
		editorActions.getAction(EditorActions.PASTE_ELEMENT).setEnabled(cutNPaste);
		editorActions.getAction(EditorActions.ALIGN_ELEMENTS_LEFT).setEnabled(align);
		editorActions.getAction(EditorActions.ALIGN_ELEMENTS_RIGHT).setEnabled(align);
		editorActions.getAction(EditorActions.ALIGN_ELEMENTS_TOP).setEnabled(align);
		editorActions.getAction(EditorActions.ALIGN_ELEMENTS_BOTTOM).setEnabled(align);
		editorActions.getAction(EditorActions.ALIGN_ELEMENTS_HORIZONTAL).setEnabled(align);
		editorActions.getAction(EditorActions.ALIGN_ELEMENTS_VERTICAL).setEnabled(align);
		editorActions.getAction(EditorActions.DELETE_ELEMENT).setEnabled(anySelected);	
		editorActions.getAction(EditorActions.TO_FRONT_LAYER).setEnabled(anySelected);
		editorActions.getAction(EditorActions.TO_BACK_LAYER).setEnabled(anySelected);
		

		editorActions.getAction(EditorActions.UNDO_OPERATION).setEnabled(getUndoManager().canUndo());
		editorActions.getAction(EditorActions.REDO_OPERATION).setEnabled(getUndoManager().canRedo());				
	}
	
	/**
	 * @return
	 */
	public UndoManager getUndoManager() {
		return undoManager;
	}

	public static void haveFun() {
		Connection conn = PoolManager.getInstance().getConnection("yukon");
	
	}
}
