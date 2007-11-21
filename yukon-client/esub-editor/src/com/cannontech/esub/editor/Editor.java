package com.cannontech.esub.editor;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Timer;

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
import com.cannontech.common.editor.PropertyPanel;
import com.cannontech.common.editor.PropertyPanelEvent;
import com.cannontech.common.editor.PropertyPanelListener;
import com.cannontech.common.gui.image.ImageChooser;
import com.cannontech.common.gui.util.SplashWindow;
import com.cannontech.common.login.ClientSession;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.cache.DefaultDatabaseCache;
import com.cannontech.debug.gui.AboutDialog;
import com.cannontech.esub.Drawing;
import com.cannontech.esub.editor.element.ElementEditorFactory;
import com.cannontech.esub.editor.element.LineElementEditor;
import com.cannontech.esub.element.AlarmTextElement;
import com.cannontech.esub.element.CurrentAlarmsTable;
import com.cannontech.esub.element.DrawingElement;
import com.cannontech.esub.element.DrawingMetaElement;
import com.cannontech.esub.element.DynamicGraphElement;
import com.cannontech.esub.element.DynamicText;
import com.cannontech.esub.element.FunctionElement;
import com.cannontech.esub.element.LineElement;
import com.cannontech.esub.element.StateImage;
import com.cannontech.esub.element.StaticImage;
import com.cannontech.esub.element.StaticText;
import com.cannontech.esub.util.ESubDrawingUpdater;
import com.cannontech.message.dispatch.ClientConnection;
import com.cannontech.message.util.Command;
import com.cannontech.roles.application.EsubEditorRole;
import com.cannontech.yukon.conns.ConnPool;
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
    public static final URL ESUBEDITOR_GIF = Editor.class.getResource("/esubEditorIcon.gif");
    private static JFrame frame;
    final PropertyPanel lineEditor;
	// the drawing to edit
	// Synchronize on the drawing to stop any updates
	// from happening 
	private Drawing drawing;
	
	private UndoManager undoManager;
	
	// timer to update the drawing
	private Timer drawingUpdateTimer;
	private ESubDrawingUpdater drawingUpdater;
	
	// JDialog to re-use
	private JDialog propertyDialog;

	// Place to keep track of a new element being created
	ElementPlacer elementPlacer;

	// All the actions for this editor
	EditorActions editorActions;
	boolean placingLine = false;
    boolean drawingLine = false;
    
	// Handles clicks on the LxView	
	private final MouseListener viewMouseListener = new MouseAdapter() {
		@Override
        public void mousePressed(MouseEvent evt) {
			if (elementPlacer.isPlacing()) {
							
				elementPlacer.setXPosition(evt.getX());
				elementPlacer.setYPosition(evt.getY());
				configureObject(elementPlacer);
			}else if( placingLine == true) {
                
                elementPlacer.setXPosition(evt.getX());
                elementPlacer.setYPosition(evt.getY());
                configureLine(elementPlacer);
                
            }
		}

        @Override
        public void mouseReleased(MouseEvent e) {
            if(drawingLine == true){
                
                drawingLine = false;
                elementPlacer.setXPosition(e.getX());
                elementPlacer.setYPosition(e.getY());
                drawingLine(elementPlacer);
            }
        }
    };
	
	// Handles double clicks on an LxElement in the LxView
	private final LxMouseListener editElementMouseListener = new LxMouseAdapter() {
		@Override
        public void mouseDoubleClicked(LxMouseEvent evt) {
			synchronized(getDrawing()) {				
				editElement(evt.getLxComponent());
			}
		}		
	};
    
    private final MouseMotionListener mouseMotionListener = new MouseMotionListener()
    {
        public void mouseDragged(MouseEvent evt) {
            if(drawingLine) {
                LineElement elem =  (LineElement) elementPlacer.getElement();
                elem.setPoint2(evt.getX(), evt.getY());
            }
        }
        public void mouseMoved(MouseEvent evt) {}
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
        lineEditor = ElementEditorFactory.getInstance().createEditorPanel(LineElement.class);
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
    					CTILogger.error("Caught an exception in configureObject", e);
    				} finally { //make to always get back into not placing mode
    					elementPlacer.setIsPlacing(false);
    					getDrawing().getLxView().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    				}
				}
			}
		});
	}
    
    void configureLine(final ElementPlacer placer) {

        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                
                synchronized(getDrawing()) {

                    try {
                        LineElement elem = (LineElement) placer.getElement();
                        elem.setPoint1(
                            placer.getXPosition(),
                            placer.getYPosition());
                        
                        elem.setPoint2(
                            placer.getXPosition(),
                            placer.getYPosition());
    
                        ((DrawingElement) elem).setDrawing(getDrawing());
                        
                        getDrawing().getLxGraph().add(elem);
        
                        placingLine = false;
                        drawingLine = true;
                        getDrawing().getLxView().addMouseMotionListener(mouseMotionListener);
                    } catch (Exception e) {
                        e.printStackTrace();
                    } 
                }
            }
        });
    }
    
    void drawingLine(final ElementPlacer placer) {

        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                
                synchronized(getDrawing()) {

                    try {
                        LineElement elem = (LineElement) placer.getElement();
                        elem.setPoint2(
                            placer.getXPosition(),
                            placer.getYPosition());
        
                        placingLine = false;
                        drawingLine = false;
                        getDrawing().getLxView().removeMouseMotionListener(mouseMotionListener);
                        elem.setSelected(true);
                        getDrawing().getLxView().lassoSetDisplayed(true);
                        
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

		//no more updates for a bit
		stopUpdating();
		
        if (propertyDialog == null){
			propertyDialog = new JDialog(CtiUtilities.getParentFrame(getDrawing().getLxView()), true);
        }
        if (elem instanceof LineElement) {
            if( lineEditor != null ) {
                
                
                getDrawing().getLxGraph().startUndoEdit("edit object");
                lineEditor.getPropertyButtonPanel().getApplyJButton().setVisible(false);
                lineEditor.setValue(elem);
                propertyDialog.setContentPane(lineEditor);      
                propertyDialog.pack();
                propertyDialog.setLocationRelativeTo(CtiUtilities.getParentFrame(getDrawing().getLxView()));
                propertyDialog.setVisible(true);
    
                getDrawing().getLxGraph().cancelUndoEdit();
                
                // start the updates again
                startUpdating();
            }
        }else {
            final PropertyPanel editor = ElementEditorFactory.getInstance().createEditorPanel(elem.getClass());
            
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
    			propertyDialog.setVisible(true);
    
    			getDrawing().getLxGraph().cancelUndoEdit();
    			
    			// start the updates again
    			startUpdating();
            }
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
			@Override
            public boolean addEdit(UndoableEdit e) {
// uncomment for debugging	System.out.println(e.getPresentationName());
				return super.addEdit(e);
			}			
		};
				
		final LxGraph lxGraph = getDrawing().getLxGraph();
		final LxView lxView = getDrawing().getLxView();
		
		drawing.getMetaElement().setDrawingWidth(prefs.getDefaultDrawingWidth());
		drawing.getMetaElement().setDrawingHeight(prefs.getDefaultDrawingHeight());
		drawing.getMetaElement().setDrawingRgbColor(prefs.getDefaultDrawingRGBColor());
		
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
		
		final JMenuBar menuBar = editorMenus.getMenuBar();
		final JPopupMenu popupMenu = editorMenus.getPopupMenu();
		JToolBar toolBar = editorToolBar.getToolBar();

		p.setLayout(new java.awt.BorderLayout());
		p.add(scrollPane, java.awt.BorderLayout.CENTER);
		p.add(menuBar, java.awt.BorderLayout.NORTH);
		p.add(toolBar, java.awt.BorderLayout.WEST);
		lxView.addMouseListener(viewMouseListener);
        lxView.addMouseListener(new MouseAdapter() { 
			@Override
            public void mousePressed(MouseEvent e) {
				maybeShowPopup(e);
			}
			@Override
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
        
        lineEditor.addPropertyPanelListener(new PropertyPanelListener() {
            public void selectionPerformed(PropertyPanelEvent e) {
                if (e.getID() == PropertyPanelEvent.CANCEL_SELECTION) {
                    
                } else if (e.getID() == PropertyPanelEvent.OK_SELECTION) {
                    ((LineElementEditor)lineEditor).selectionPerformed();
                }
                propertyDialog.setVisible(false);                               
            }
        });
        
		updateSize();
		updateColor();
		
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
			fileChooser.setDialogTitle("Open Drawing");
			
			String currentDir = EditorPrefs.getPreferences().getWorkingDir();

			fileChooser.setCurrentDirectory(new File(currentDir));
			int returnVal = fileChooser.showOpenDialog(null);

			if (returnVal == JFileChooser.APPROVE_OPTION) {

				// Remove old components.
				String newDrawing = fileChooser.getSelectedFile().getPath();
				loadDrawing(newDrawing);

				try {
					EditorPrefs.getPreferences().setWorkingDir(fileChooser.getSelectedFile().getParentFile().getCanonicalPath());
				} catch (IOException ioe) {
					ioe.printStackTrace();
				}

				//HACKHACK
				currentDir = EditorPrefs.getPreferences().getWorkingDir();
				ImageChooser.getInstance().setCurrentDirectory(new File(currentDir));
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
		dme.setDrawingRgbColor(prefs.getDefaultDrawingRGBColor());
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
            if(comps[i] instanceof LineElement) {
                ((LineElement)comps[i]).setIsNew(false);
            }
			setBehavior(comps[i]);
		}

		elementPlacer.setIsPlacing(false);
		setFrameTitle(drawingFile);

		updateSize();
		updateColor();
		startUpdating();
		
		} 
		finally {
				
			int currentWidth = getDrawing().getMetaElement().getDrawingWidth();
			int currentHeight = getDrawing().getMetaElement().getDrawingHeight();
			int currentRGBColor = getDrawing().getMetaElement().getDrawingRGBColor();
			EditorPrefs.getPreferences().setDefaultDrawingWidth(currentWidth);
			EditorPrefs.getPreferences().setDefaultDrawingHeight(currentHeight);
			EditorPrefs.getPreferences().setDefaultDrawingRGBColor(currentRGBColor);
			
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
        CTILogger.info(APPLICATION_NAME + " starting...");
		CtiUtilities.setLaF();
		frame = new JFrame();

		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			@Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                // Make sure to shutdown properly so dispatch is not left with a vagrant connect for a while.
                // Ugly cast.  We want to call disconnect though so that our shutdown message gets
                // written out.
                ClientConnection conn = (ClientConnection) ConnPool.getInstance().getDefDispatchConn();
                if ( conn != null && conn.isValid() ) {  // free up Dispatchs resources     
                    Command comm = new Command();
                    comm.setPriority(15);               
                    comm.setOperation( Command.CLIENT_APP_SHUTDOWN );
                    conn.write( comm );
                    conn.disconnect();
                }
            
                System.exit(0);
            }
		});
		
		SplashWindow.createYukonSplash(frame);
	
		frame.setSize(defaultSize);
		frame.setIconImage(java.awt.Toolkit.getDefaultToolkit().getImage(ESUBEDITOR_GIF));

		final ClientSession session = ClientSession.getInstance(); 
		if(!session.establishSession(frame)) {
			System.exit(-1);			
		}
	  	
		if(session == null) {
			System.exit(-1);
		}
		
		if(!session.checkRole(EsubEditorRole.ROLEID)) {
            javax.swing.SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    JOptionPane.showMessageDialog(null, "User: '" + session.getUser().getUsername() + "' is not authorized to use this application, exiting.", "Access Denied", JOptionPane.WARNING_MESSAGE);
                    System.exit(-1);                        
                }
            });
			
		}
		
		Editor editor = new Editor();

		frame.getContentPane().add(editor);
		editor.setFrameTitle("Untitled");
        
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                frame.pack();
                frame.setVisible(true);        
            }
        });
		
		
		//get this stuff loaded into the cache asap
		DefaultDatabaseCache.getInstance().getAllDevices();
		DefaultDatabaseCache.getInstance().getAllStateGroupMap();
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
			ImageChooser.getInstance().setCurrentDirectory(new File(currentDir));
				
			int currentWidth = getDrawing().getMetaElement().getDrawingWidth();
			int currentHeight = getDrawing().getMetaElement().getDrawingHeight();
			int currentRGBColor = getDrawing().getMetaElement().getDrawingRGBColor();
			EditorPrefs.getPreferences().setDefaultDrawingWidth(currentWidth);
			EditorPrefs.getPreferences().setDefaultDrawingHeight(currentHeight);
			EditorPrefs.getPreferences().setDefaultDrawingRGBColor(currentRGBColor);
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
		if (elem instanceof DynamicText
			|| elem instanceof StateImage
			|| elem instanceof StaticImage
			|| elem instanceof StaticText 
			|| elem instanceof AlarmTextElement
            || elem instanceof FunctionElement) {
			elem.setUserResizable(false);
			elem.removeDefaultDoubleClickBehavior();
			elem.removeMouseListener(editElementMouseListener);
			elem.addMouseListener(editElementMouseListener);
		}
		
		if( elem instanceof DynamicGraphElement 
            || elem instanceof CurrentAlarmsTable
            || elem instanceof LineElement) {
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
			pFrame.setTitle(title + " - Esubstation Editor");
			return;
		}

		JInternalFrame piFrame = CtiUtilities.getParentInternalFrame(this);
		if (piFrame != null) {
			piFrame.setTitle(title + " - Esubstation Editor");
			return;
		}
	}
	
	/**
	 * Displays the about dialog
	 */
	public void showAboutDialog() {
		AboutDialog aboutDialog = new AboutDialog(CtiUtilities.getParentFrame(getDrawing().getLxView()), "About Esubstation Editor", true);		
		aboutDialog.setLocationRelativeTo(getDrawing().getLxView());
		aboutDialog.setVisible(true);
		aboutDialog.dispose();
	}
	
	private void updateColor() {
	    final int rgbColor = getDrawing().getMetaElement().getDrawingRGBColor();
	    final LxView view = getDrawing().getLxView();
	    
	    SwingUtilities.invokeLater( new Runnable() {
            public void run() {
                view.setBackground(new Color(rgbColor));
                view.repaint();
            }
        });
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
		updateColor();
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
		drawingUpdater = new ESubDrawingUpdater();	
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
		editorActions.getAction(EditorActions.HORIZONTAL_ALIGN_LEFT_ELEMENTS).setEnabled(align);
		editorActions.getAction(EditorActions.HORIZONTAL_ALIGN_RIGHT_ELEMENTS).setEnabled(align);
		editorActions.getAction(EditorActions.VERTICAL_ALIGN_TOP_ELEMENTS).setEnabled(align);
		editorActions.getAction(EditorActions.VERTICAL_ALIGN_BOTTOM_ELEMENTS).setEnabled(align);
		editorActions.getAction(EditorActions.VERTICAL_ALIGN_CENTER_ELEMENTS).setEnabled(align);
		editorActions.getAction(EditorActions.HORIZONTAL_ALIGN_CENTER_ELEMENTS).setEnabled(align);
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
    
    /**
     * Starts the dialog that deals with changing
     * the device on one to many elements.
     */
    public void changeDevice() {
        ChangeDeviceDialog cd = new ChangeDeviceDialog(frame ,drawing.getLxGraph().getSelectedObjects());
        cd.show();
    }
}
