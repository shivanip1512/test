package com.cannontech.esub.editor;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.util.Timer;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.editor.PropertyPanelEvent;
import com.cannontech.common.editor.PropertyPanelListener;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.esub.editor.element.DrawingElement;
import com.cannontech.esub.util.DrawingUpdater;
import com.cannontech.esub.util.Util;

import com.loox.jloox.LxComponent;
import com.loox.jloox.LxElement;
import com.loox.jloox.LxGraph;
import com.loox.jloox.LxMouseAdapter;
import com.loox.jloox.LxMouseEvent;
import com.loox.jloox.LxMouseListener;
import com.loox.jloox.LxView;

/**
 * Main editor class.
 * Creation date: (12/11/2001 3:53:52 PM)
 * @author: 
 */
public class Editor extends JPanel {
	
//	JPopupMenu editPopup = new JPopupMenu();
//	JMenuItem deletePopupItem = new JMenuItem("Delete");

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
	final LxMouseListener editElementMouseListener = new LxMouseAdapter() {
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
					LxElement elem = placer.getElement();
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

		if (propertyDialog == null)
			propertyDialog =
				new JDialog(
					com.cannontech.common.util.CtiUtilities.getParentFrame(
						getDrawing().getLxView()),
					true);

		//	final com.cannontech.esub.editor.element.DynamicTextEditor editor = new com.cannontech.esub.editor.element.DynamicTextEditor();
		final com.cannontech.common.editor.PropertyPanel editor =
			com
				.cannontech
				.esub
				.editor
				.element
				.ElementEditorFactory
				.getInstance()
				.createEditorPanel(elem.getClass());

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
		//	propertyDialog.setSize(475, 500);
		propertyDialog.pack();
		propertyDialog.setLocationRelativeTo(getDrawing().getLxView());
		propertyDialog.show();

	}
	/**
	 * Creation date: (12/17/2001 1:59:00 PM)
	 * @return com.loox.jloox.LxGraph
	 */
	/*com.loox.jloox.LxGraph getLxGraph() {
		return getDrawing().getLxGraph();
	}
	/**
	 * Creation date: (12/17/2001 1:59:00 PM)
	 * @return com.loox.jloox.LxView
	 */ /*
	com.loox.jloox.LxView getLxView() {
		return getDrawing().getLxView();
	}*/
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

		drawing = new Drawing();

		elementPlacer = new ElementPlacer();

		LxGraph lxGraph = getDrawing().getLxGraph();
		LxView lxView = getDrawing().getLxView();

		EditorActions editorActions = new EditorActions(this);
		EditorMenus editorMenus = new EditorMenus(editorActions);
		EditorToolBar editorToolBar = new EditorToolBar(editorActions);
		EditorKeys editorKeys = new EditorKeys(editorActions);
		JMenuBar menuBar = editorMenus.getMenuBar();
		JToolBar toolBar = editorToolBar.getToolBar();

		p.setLayout(new java.awt.BorderLayout());
		p.add(lxView, java.awt.BorderLayout.CENTER);
		p.add(menuBar, java.awt.BorderLayout.NORTH);
		p.add(toolBar, java.awt.BorderLayout.WEST);

		lxView.addMouseListener(viewMouseListener);		
			
//		lxView.registerKeyboardAction(E)
		/*deletePopupItem.addActionListener(
			editorActions.getAction(EditorActions.SET_DYNAMIC_TEXT_COLOR));		
		*/
		drawingUpdater = new DrawingUpdater();	
		drawingUpdater.setDrawing( getDrawing() );
		drawingUpdateTimer = new Timer();
		drawingUpdateTimer.schedule(drawingUpdater, 0, 5000);
		
		
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
		/*			
		JFileChooser fileChooser =
			com.cannontech.esub.util.Util.getDrawingJFileChooser();
		fileChooser.setApproveButtonText("Ok");
		fileChooser.setDialogTitle("Create Drawing");
		String currentDir = EditorPrefs.getPreferences().getWorkingDir();
		fileChooser.setCurrentDirectory(new File(currentDir));

		int returnVal = fileChooser.showSaveDialog(null);

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			getDrawing().clear();

			String selectedFile = fileChooser.getSelectedFile().getPath();

			getDrawing().save(selectedFile);
			setFrameTitle(selectedFile);

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
		com.cannontech.esub.util.ImageChooser.getInstance().setCurrentDirectory(new File(currentDir));
		}	*/	
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
		
		//drawingUpdater.setDrawing(getDrawing());
	}

	/**
	 * Creation date: (12/11/2001 3:54:10 PM)
	 * @param args java.lang.String[]
	 */
	public static void main(String[] args) {
		try {
			javax.swing.UIManager.setLookAndFeel(
				javax.swing.UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace(); /*Not much to do about*/
		}

		JFrame frame = new JFrame();

		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			}
		});

		Editor editor = new Editor();

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setViewportView(editor);

		frame.getContentPane().add(scrollPane);

		frame.setSize(defaultSize);
		editor.setPreferredSize(defaultSize);

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

		if (args.length == 1) {
			String file = args[0];

			CTILogger.info("Loading " + file);
			editor.loadDrawing(file);
		}

		//editor.loadDrawing("c:/temp/test.jlx");
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
			setFrameTitle(selectedFile);

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
			|| elem instanceof com.cannontech.esub.editor.element.StaticText) {
			elem.removeDefaultDoubleClickBehavior();
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

}
