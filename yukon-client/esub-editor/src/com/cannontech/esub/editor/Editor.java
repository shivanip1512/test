package com.cannontech.esub.editor;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
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
import com.cannontech.common.editor.EditorInputValidationException;
import com.cannontech.common.editor.PropertyPanel;
import com.cannontech.common.editor.PropertyPanelEvent;
import com.cannontech.common.editor.PropertyPanelListener;
import com.cannontech.common.gui.image.ImageChooser;
import com.cannontech.common.login.ClientStartupHelper;
import com.cannontech.common.util.SwingUtil;
import com.cannontech.database.cache.DefaultDatabaseCache;
import com.cannontech.debug.gui.AboutDialog;
import com.cannontech.esub.Drawing;
import com.cannontech.esub.editor.element.ElementEditorFactory;
import com.cannontech.esub.element.AlarmTextElement;
import com.cannontech.esub.element.CurrentAlarmsTable;
import com.cannontech.esub.element.DrawingElement;
import com.cannontech.esub.element.DrawingMetaElement;
import com.cannontech.esub.element.DynamicGraphElement;
import com.cannontech.esub.element.DynamicText;
import com.cannontech.esub.element.FunctionElement;
import com.cannontech.esub.element.LineElement;
import com.cannontech.esub.element.RectangleElement;
import com.cannontech.esub.element.StateImage;
import com.cannontech.esub.element.StaticImage;
import com.cannontech.esub.element.StaticText;
import com.cannontech.esub.util.ESubDrawingUpdater;
import com.cannontech.roles.application.EsubEditorRole;
import com.cannontech.user.SystemUserContext;
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
	
	private static final String APPLICATION_NAME = "eSubstation Editor";
	
    public static final URL ESUBEDITOR_IMG_16 = Editor.class.getResource("/Esubstation16.gif");
    public static final URL ESUBEDITOR_IMG_24 = Editor.class.getResource("/Esubstation24.gif");
    public static final URL ESUBEDITOR_IMG_32 = Editor.class.getResource("/Esubstation32.gif");
    public static final URL ESUBEDITOR_IMG_48 = Editor.class.getResource("/Esubstation48.gif");
    public static final URL ESUBEDITOR_IMG_64 = Editor.class.getResource("/Esubstation64.gif");
    
    public static List<Image> getIconsImages() {
        
        List<Image> iconsImages = new ArrayList<Image>();
        iconsImages.add(Toolkit.getDefaultToolkit().getImage(ESUBEDITOR_IMG_16));
        iconsImages.add(Toolkit.getDefaultToolkit().getImage(ESUBEDITOR_IMG_24));
        iconsImages.add(Toolkit.getDefaultToolkit().getImage(ESUBEDITOR_IMG_32));
        iconsImages.add(Toolkit.getDefaultToolkit().getImage(ESUBEDITOR_IMG_48));
        iconsImages.add(Toolkit.getDefaultToolkit().getImage(ESUBEDITOR_IMG_64));
        
        return iconsImages;
    }
    
    private static JFrame frame;
	/*
	 * The drawing to edit.
	 * Synchronize on the drawing to stop any updates from happening.
	 */
	private Drawing drawing;
	
	private UndoManager undoManager;
	
	// Timer to update the drawing
	private Timer drawingUpdateTimer;
	private ESubDrawingUpdater drawingUpdater;
	
	// JDialog to re-use
	private JDialog propertyDialog;

	// Place to keep track of a new element being created
	ElementPlacer elementPlacer;

	// All the actions for this editor
	EditorActions editorActions;
	boolean placingObject = false;
    boolean drawingObject = false;
    private static final int UPATE_TIMER = 15000;
    
	// Handles clicks on the LxView	
	private final MouseListener viewMouseListener = new MouseAdapter() {
		@Override
        public void mousePressed(MouseEvent evt) {
			if (elementPlacer.isPlacing()) {
							
				elementPlacer.setXPosition(evt.getX());
				elementPlacer.setYPosition(evt.getY());
				configureObject(elementPlacer);
			}else if( placingObject == true) {
                
                elementPlacer.setXPosition(evt.getX());
                elementPlacer.setYPosition(evt.getY());
                configureDrawableObject(elementPlacer);
                
            }
		}

        @Override
        public void mouseReleased(MouseEvent e) {
            if(drawingObject == true){
                
                drawingObject = false;
                elementPlacer.setXPosition(e.getX());
                elementPlacer.setYPosition(e.getY());
                drawingObject(elementPlacer);
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
            if(drawingObject) {
                if(elementPlacer.getElement() instanceof LineElement) {
                    LineElement elem =  (LineElement) elementPlacer.getElement();
                    elem.setPoint2(evt.getX(), evt.getY());
                } else {
                   RectangleElement elem = (RectangleElement) elementPlacer.getElement();
                   elem.setSize(Math.abs(evt.getX() - elem.getX()),Math.abs(evt.getY() - elem.getY()));
                }
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
    
    void configureDrawableObject(final ElementPlacer placer) {

        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                
                synchronized(getDrawing()) {

                    try {
                        if(placer.getElement() instanceof LineElement) {
                            LineElement elem = (LineElement) placer.getElement();
                            elem.setPoint1(
                                placer.getXPosition(),
                                placer.getYPosition());
                            
                            elem.setPoint2(
                                placer.getXPosition(),
                                placer.getYPosition());
        
                            ((DrawingElement) elem).setDrawing(getDrawing());
                            
                            getDrawing().getLxGraph().add(elem);
                        } else {
                            RectangleElement elem = (RectangleElement) placer.getElement();
                            elem.setX(placer.getXPosition());
                            elem.setY(placer.getYPosition());
                            ((DrawingElement) elem).setDrawing(getDrawing());
                            getDrawing().getLxGraph().add(elem);
                        }
        
                        placingObject = false;
                        drawingObject = true;
                        getDrawing().getLxView().addMouseMotionListener(mouseMotionListener);
                    } catch (Exception e) {
                        e.printStackTrace();
                    } 
                }
            }
        });
    }
    
    void drawingObject(final ElementPlacer placer) {

        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                
                synchronized(getDrawing()) {

                    try {
                        if(placer.getElement() instanceof LineElement) {
                            LineElement elem = (LineElement) placer.getElement();
                            elem.setPoint2(placer.getXPosition(), placer.getYPosition());
                        } else {
                            RectangleElement elem = (RectangleElement) placer.getElement();
                            elem.setSize(Math.abs(placer.getXPosition() - elem.getX()), Math.abs(placer.getYPosition() - elem.getY()));
                        }
                        placer.getElement().setSelected(true);
                        placingObject = false;
                        drawingObject = false;
                        getDrawing().getLxView().removeMouseMotionListener(mouseMotionListener);
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
			propertyDialog = new JDialog(SwingUtil.getParentFrame(getDrawing().getLxView()), true);
        }
        final PropertyPanel editor = ElementEditorFactory.getInstance().createEditorPanel(elem.getClass());
        
		if( editor != null ) {
			editor.addPropertyPanelListener(new PropertyPanelListener() {
				public void selectionPerformed(PropertyPanelEvent e) {
					if (e.getID() == PropertyPanelEvent.CANCEL_SELECTION) {}
					else if (e.getID() == PropertyPanelEvent.OK_SELECTION) {
						try {
                            editor.getValue(elem);
                        } catch (EditorInputValidationException e1) { /* ignore */ }
					}
					propertyDialog.setVisible(false);
				}
			});
			getDrawing().getLxGraph().startUndoEdit("edit object");
			editor.getPropertyButtonPanel().getApplyJButton().setVisible(false);
			editor.setValue(elem);
			propertyDialog.setTitle("Edit Element Settings");
            propertyDialog.setContentPane(editor);		
			propertyDialog.pack();
			propertyDialog.setLocationRelativeTo(SwingUtil.getParentFrame(getDrawing().getLxView()));
			propertyDialog.setVisible(true);
			getDrawing().getLxGraph().cancelUndoEdit();
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
		final EditorPrefs prefs = EditorPrefs.getPreferences();
		
		drawing = new Drawing();
		drawing.setUserContext(new SystemUserContext());
		
		elementPlacer = new ElementPlacer();
		undoManager = new UndoManager() {
			@Override
            public boolean addEdit(UndoableEdit e) {
			    CTILogger.debug(e.getPresentationName());
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
		EditorKeys.registerKeysForActions(editorActions);
		
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
            }else if(comps[i] instanceof RectangleElement) {
                ((RectangleElement)comps[i]).setIsNew(false);
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
	    try {
            ClientStartupHelper clientStartupHelper = new ClientStartupHelper();
            clientStartupHelper.setAppName(APPLICATION_NAME);
            clientStartupHelper.setRequiredRole(EsubEditorRole.ROLEID);
            frame = new JFrame() {};
            clientStartupHelper.setParentFrame(frame);
            clientStartupHelper.doStartup();

            frame.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosing(java.awt.event.WindowEvent e) {
                    System.exit(0);
                }
            });

            frame.setIconImages(getIconsImages());

            Editor editor = new Editor();

            frame.getContentPane().add(editor);
            editor.setFrameTitle("Untitled");

            javax.swing.SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    frame.pack();
                    frame.setVisible(true);
                }
            });

            // get this stuff loaded into the cache asap
            DefaultDatabaseCache.getInstance().getAllDevices();
            DefaultDatabaseCache.getInstance().getAllStateGroupMap();
        } catch (Exception e) {
            CTILogger.error(e.getMessage(), e);
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
			JOptionPane.showMessageDialog(SwingUtil.getParentFrame(getDrawing().getLxView()),
			    "An error occurred saving the drawing", "Saving Error", JOptionPane.ERROR_MESSAGE);
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
	}

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
            || elem instanceof LineElement
            || elem instanceof RectangleElement) {
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
		Frame pFrame = SwingUtil.getParentFrame(this);
		if (pFrame != null) {
			pFrame.setTitle(title + " - eSubstation Editor");
			return;
		}

		JInternalFrame piFrame = SwingUtil.getParentInternalFrame(this);
		if (piFrame != null) {
			piFrame.setTitle(title + " - eSubstation Editor");
			return;
		}
	}
	
	/**
	 * Displays the about dialog
	 */
	public void showAboutDialog() {
		AboutDialog aboutDialog = new AboutDialog(SwingUtil.getParentFrame(getDrawing().getLxView()), "About eSubstation Editor", true);		
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
		
		/*
		 *  Add behavior back to all these elements.
		 *  Would be nice to find a way to get at all the elements just created!
		 *  Visit here if paste gets slow!!!
		 */
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
		drawingUpdater.setUserContext(new SystemUserContext());
		drawingUpdateTimer = new Timer();
		drawingUpdateTimer.schedule(drawingUpdater, 0, UPATE_TIMER);
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
        List<DynamicText> textElems = new ArrayList<DynamicText>();
        List<StateImage> stateImageElems = new ArrayList<StateImage>();
        
        for(Object obj : drawing.getLxGraph().getSelectedObjects()) {
            if(obj instanceof DynamicText) {
                textElems.add((DynamicText) obj);
            }else if(obj instanceof StateImage) {
                stateImageElems.add((StateImage)obj);
            }
        }
        ChangeDeviceDialog cd = new ChangeDeviceDialog(frame ,textElems, stateImageElems);
        cd.setVisible(true);
    }
}
