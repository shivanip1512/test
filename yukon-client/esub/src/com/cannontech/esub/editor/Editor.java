package com.cannontech.esub.editor;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.util.Timer;

import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;

import com.cannontech.common.editor.PropertyPanelEvent;
import com.cannontech.common.editor.PropertyPanelListener;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.cache.DefaultDatabaseCache;
import com.cannontech.esub.editor.element.CurrentAlarmsTable;
import com.cannontech.esub.editor.element.DrawingElement;
import com.cannontech.esub.editor.element.DynamicGraphElement;
import com.cannontech.esub.util.DrawingUpdater;

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
	
	private static final Dimension defaultSize = new Dimension(800, 600);

	// the drawing to edit
	// Synchronize on the drawing to stop any updates
	// from happening 
	private Drawing drawing;
	
	// timer to update the drawing
	private Timer drawingUpdateTimer;
	private DrawingUpdater drawingUpdater;
	
	// JDialog to re-use
	private JDialog propertyDialog;

	// Place to keep track of a new element being created
	ElementPlacer elementPlacer;

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
					getDrawing().getLxView().setCursor(
						new Cursor(Cursor.DEFAULT_CURSOR));
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
		drawingUpdateTimer.cancel();
		
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
	
			editor.getPropertyButtonPanel().getApplyJButton().setVisible(false);
			editor.setValue(elem);
			propertyDialog.setContentPane(editor);		
			propertyDialog.pack();
			propertyDialog.setLocationRelativeTo(CtiUtilities.getParentFrame(getDrawing().getLxView()));
			propertyDialog.show();

			// start the updates again
			drawingUpdater = new DrawingUpdater();	
			drawingUpdater.setDrawing( getDrawing() );
			drawingUpdateTimer = new Timer();
			drawingUpdateTimer.schedule(drawingUpdater, 0, 15000);
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
	private void initEditor(JPanel p) {
		//editPopup.add(deletePopupItem);
		//	Lx.setActionProcessor(actionProcessor);

		EditorPrefs prefs = EditorPrefs.getPreferences();
		
		drawing = new Drawing();

		elementPlacer = new ElementPlacer();

		LxGraph lxGraph = getDrawing().getLxGraph();
		LxView lxView = getDrawing().getLxView();
				
		JPanel viewPanel = new JPanel();
		viewPanel.setLayout(new FlowLayout());		
		viewPanel.add(lxView);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setViewportView(viewPanel);
		
		EditorActions editorActions = new EditorActions(this);
		EditorMenus editorMenus = new EditorMenus(editorActions);
		EditorToolBar editorToolBar = new EditorToolBar(editorActions);
		EditorKeys editorKeys = new EditorKeys(editorActions);
		JMenuBar menuBar = editorMenus.getMenuBar();
		JToolBar toolBar = editorToolBar.getToolBar();

		p.setLayout(new java.awt.BorderLayout());
		p.add(scrollPane, java.awt.BorderLayout.CENTER);
		p.add(menuBar, java.awt.BorderLayout.NORTH);
		p.add(toolBar, java.awt.BorderLayout.WEST);
//		p.setPreferredSize(new Dimension(prefs.getDefaultDrawingWidth(),prefs.getDefaultDrawingHeight()));
		lxView.addMouseListener(viewMouseListener);		
						
		drawingUpdater = new DrawingUpdater();	
		drawingUpdater.setDrawing( getDrawing() );
		drawingUpdateTimer = new Timer();
		drawingUpdateTimer.schedule(drawingUpdater, 0, 15000);
	}

	public void openDrawing() {
		// Give an option to save the current graph if modified.
		int r = saveOption();
		if (r != JOptionPane.CANCEL_OPTION) {

			JFileChooser fileChooser =
				com.cannontech.esub.util.Util.getDrawingJFileChooser();
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
		getDrawing().clear();	
		setFrameTitle("Untitled");
	}

	/**
	 * URL to a .jlx file
	 * Creation date: (12/11/2001 4:02:39 PM)
	 * @param drawing java.net.URL
	 */
	public void loadDrawing(String drawingFile) {
	
		getDrawing().load(drawingFile);
	
		LxComponent[] comps = getDrawing().getLxGraph().getComponents();
		for (int i = 0; i < comps.length; i++) {
			setBehavior(comps[i]);
		}

		elementPlacer.setIsPlacing(false);
		setFrameTitle(drawingFile);
		
		updateSize();
	}

	/**
	 * Creation date: (12/11/2001 3:54:10 PM)
	 * @param args java.lang.String[]
	 */
	public static void main(String[] args) {
		
		CtiUtilities.setLaF();
		JFrame frame = new JFrame();
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			}
		});

		Editor editor = new Editor();

		frame.getContentPane().add(editor);
		frame.setSize(defaultSize);
		frame.setTitle("Untitled");		
        frame.setIconImage(java.awt.Toolkit.getDefaultToolkit().getImage("esubEditorIcon.png"));

		frame.pack();
		frame.show();
		
		com
			.cannontech
			.database
			.cache
			.DefaultDatabaseCache
			.getInstance()
			.getAllDevices();
		com
			.cannontech
			.database
			.cache
			.DefaultDatabaseCache
			.getInstance()
			.getAllPoints();

		// fire up the db change listener
		DefaultDatabaseCache.getInstance().addDBChangeListener(new DBChangeCaptain());	
	}
	/**
	 * Creation date: (12/12/2001 3:29:49 PM)
	 */

	void saveAsDrawing() {

		JFileChooser fileChooser =
			com.cannontech.esub.util.Util.getDrawingJFileChooser();
		fileChooser.setApproveButtonText("Save");
		fileChooser.setDialogTitle("Save Drawing");
		String currentDir = EditorPrefs.getPreferences().getWorkingDir();
		fileChooser.setCurrentDirectory(new File(currentDir));

		int returnVal = fileChooser.showSaveDialog(null);

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			String selectedFile = fileChooser.getSelectedFile().getPath();
			
			getDrawing().save(selectedFile);
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
		getDrawing().save();
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
				option.showConfirmDialog(
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
		if (elem instanceof com.cannontech.esub.editor.element.DynamicText
			|| elem instanceof com.cannontech.esub.editor.element.StateImage
			|| elem instanceof com.cannontech.esub.editor.element.StaticImage
			|| elem instanceof com.cannontech.esub.editor.element.StaticText ) {				
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
}
