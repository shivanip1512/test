package com.cannontech.esub.editor;

/**
 * Creation date: (12/17/2001 1:46:44 PM)
 * @author: 
 */

import java.util.HashMap;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JFileChooser;

import com.cannontech.esub.util.Util;
import com.loox.jloox.LxAbstractAction;
import com.loox.jloox.LxGraph;
import com.loox.jloox.LxView;

class EditorActions {

	public static final String NEW_DRAWING = "NEW DRAWING";
	public static final String OPEN_DRAWING = "OPEN DRAWING";
	public static final String SAVE_DRAWING = "SAVE DRAWING";
	public static final String SAVE_AS_DRAWING = "SAVE AS DRAWING";
	public static final String EXPORT_DRAWING = "EXPORT DRAWING";
	public static final String EXIT_EDITOR = "EXIT EDITOR";

	public static final String DELETE_ELEMENT = "DELETE ELEMENT";
	
	public static final String TO_FRONT_LAYER = "TO FRONT";
	public static final String TO_BACK_LAYER = "TO BACK";
	
	public static final String MAGNETIC_GRID = "MAGNETIC GRID";
	
	public static final String EDIT_ELEMENT = "EDIT ELEMENT";

	public static final String CREATE_LINE = "CREATE LINE";	
	public static final String CREATE_LINK = "CREATE LINK";
	public static final String CREATE_RECTANGLE = "CREATE RECTANGLE";
	public static final String CREATE_IMAGE = "CREATE IMAGE";
	public static final String CREATE_TEXT = "CREATE TEXT";

	public static final String STATIC_IMAGE = "STATIC IMAGE";
	public static final String DYNAMIC_TEXT = "DYNAMIC TEXT";
	public static final String STATE_IMAGE = "STATE IMAGE";
	public static final String STATIC_TEXT = "STATIC TEXT";	
	
	public static final String SET_DYNAMIC_TEXT_COLOR = "SET DYNAMIC TEXT COLOR";
	
	private final LxAbstractAction newDrawingAction = 
		new LxAbstractAction(NEW_DRAWING, "New", "Create a new drawing",null, true) {
		public void processAction(ActionEvent e) {
			// Give an option to save the current graph if modified.
			int r = editor.saveOption();

            if(r != JOptionPane.CANCEL_OPTION){

				// Create blank graph by removing all.
				editor.getLxGraph().removeAll();
				editor.getLxGraph().setModified(false);
				editor.setOpenFile(null);
				//undoEdits.discardAllEdits();
			}
		}
	};

	private final LxAbstractAction openDrawingAction = 
		new LxAbstractAction(OPEN_DRAWING, "Open", "Open a new drawing", null, true) {

		/* 
		* Opens a file and if neceesary asks the
		* user to save current file.
		*/
		public void processAction(ActionEvent e) {

			// Give an option to save the current graph if modified.
			int r = editor.saveOption();
			if(r != JOptionPane.CANCEL_OPTION){

				JFileChooser fileChooser = com.cannontech.esub.util.Util.getDrawingJFileChooser();
				fileChooser.setApproveButtonText("Open");
				
				String currentDir = 
					EditorPrefs.getPreferences().getWorkingDir();
							
				fileChooser.setCurrentDirectory(new File(currentDir));
				int returnVal = fileChooser.showOpenDialog(null);

				if(returnVal == JFileChooser.APPROVE_OPTION) {

					// Remove old components.
					String newDrawing = fileChooser.getSelectedFile().getPath();										
					editor.loadDrawing(newDrawing);	
					
					try {
					EditorPrefs.getPreferences().setWorkingDir(
						fileChooser.getSelectedFile().getParentFile().getCanonicalPath());				
					}
					catch(IOException ioe) {
						ioe.printStackTrace();
					}			

				}	
				}
	    }
	};

	private final LxAbstractAction saveDrawingAction = 
		new LxAbstractAction(SAVE_DRAWING, "Save", "Save the current drawing", null, true) {
		public void processAction(ActionEvent evt) {
			String openFile = editor.getOpenFile();
			if( openFile == null )
				editor.saveFile();
		 	else
		 		editor.getLxGraph().save(openFile);
		}
	};

	private final LxAbstractAction saveDrawingAsAction = 
		new LxAbstractAction(SAVE_AS_DRAWING, "Save As...", "Save the current drawing", null, true) {
		public void processAction(ActionEvent evt) {
			editor.saveFile();
		}
	};

	private final LxAbstractAction exportDrawingAction = 
		new LxAbstractAction(EXPORT_DRAWING, "Export...", "Export drawings to svg", null, true ) {
			public void processAction(ActionEvent evt) {
				
			}
		};

	private final LxAbstractAction exitEditorAction = 
		new LxAbstractAction(EXIT_EDITOR, "Exit", "Exit the editor", null, true) {
		public void processAction(ActionEvent evt) {
			int r = editor.saveOption();
            if( r != JOptionPane.CANCEL_OPTION)
				System.exit(0);
		}
	};

	private final LxAbstractAction deleteElementAction =
		new LxAbstractAction(DELETE_ELEMENT, "Delete", "Delete", null, true ) {
			public void processAction(ActionEvent e) {
				LxGraph graph = editor.getLxGraph();
				for( int i = 0; i < graph.getSelectedObjectCount(); i++ ) {
					com.loox.jloox.LxComponent c = graph.getSelectedObject(i);
					graph.remove(c);
				}
					
			}
		};
		
	private final LxAbstractAction toFrontLayerAction =
		new LxAbstractAction(TO_FRONT_LAYER, "Send to front", "Send to front", null, true ) {
			public void processAction(ActionEvent e) {
				editor.getLxGraph().raiseSelection();
			}
		};

	private final LxAbstractAction toBackLayerAction =
		new LxAbstractAction(TO_BACK_LAYER, "Send to back", "Send to back", null, true ) {
			public void processAction(ActionEvent e) {
				editor.getLxGraph().lowerSelection();
			}
		};
	
	private final LxAbstractAction toggleGridAction = 
		new LxAbstractAction(MAGNETIC_GRID, "Grid...", "Grid Settings", null, true ) {
			
			public void processAction(ActionEvent evt) {
			LxView view = editor.getLxView();
			MagneticGridPanel p = new MagneticGridPanel(view);
			JOptionPane.showMessageDialog(editor.getLxView(), p, "Magnetic Grid Settings", javax.swing.JOptionPane.PLAIN_MESSAGE, null);
			}
		};

		private final LxAbstractAction editElementAction = new LxAbstractAction(EDIT_ELEMENT,
	"Edit Element", "Edit Element", null, true )  {
		public void processAction(ActionEvent evt) {
			editor.editElement(null);
		}
	};
	
	// ELEMENT ACTIONS
	
	private final LxAbstractAction staticImageAction = new LxAbstractAction(STATIC_IMAGE,
    "Static Image", "Static Image", "GraphicIcon.gif",true){
    
    	public void processAction(java.awt.event.ActionEvent e){
	    	
	    	 com.cannontech.esub.editor.element.StaticImage image = 
	    	 	new com.cannontech.esub.editor.element.StaticImage();
	    	 
	    	 editor.setBehavior(image);
			 editor.elementPlacer.setElement(image);
			 editor.elementPlacer.setIsPlacing(true);
    		 editor.getLxView().setCursor(new java.awt.Cursor(java.awt.Cursor.CROSSHAIR_CURSOR)); 
    	}
	};
    
	private final LxAbstractAction dynamicTextAction = new LxAbstractAction(DYNAMIC_TEXT,
    "Dynamic Text", "Dynamic Text", "AttIcon.gif",true){
    
    	public void processAction(java.awt.event.ActionEvent e){

	    	 com.cannontech.esub.editor.element.DynamicText text = 
	    	 	new com.cannontech.esub.editor.element.DynamicText();
	    	 
	    	 editor.setBehavior(text);
			 editor.elementPlacer.setElement(text);
			 editor.elementPlacer.setIsPlacing(true);
    		 editor.getLxView().setCursor(new java.awt.Cursor(java.awt.Cursor.CROSSHAIR_CURSOR)); 
    	}
	};

    private final LxAbstractAction setDynamicTextColor = new LxAbstractAction(SET_DYNAMIC_TEXT_COLOR,
	    "Set Color", "Set Color", null, true) {
		    public void processAction(ActionEvent e) {
			    Object[] o = editor.getLxGraph().getSelectedObjects();
			    for( int i = 0; i < o.length; i++ ) {
				    System.out.println(o.getClass());
			    }
		    }
	    };

	private final LxAbstractAction stateImageAction = new LxAbstractAction(STATE_IMAGE,
    "State Image", "State Image", "StateIcon.gif",true){
    
    	public void processAction(java.awt.event.ActionEvent e){
	    	 
	    	 com.cannontech.esub.editor.element.StateImage si = 
	    	 	new com.cannontech.esub.editor.element.StateImage();
	    	 
	    	 editor.setBehavior(si);
			 editor.elementPlacer.setElement(si);
			 editor.elementPlacer.setIsPlacing(true);
    		 editor.getLxView().setCursor(new java.awt.Cursor(java.awt.Cursor.CROSSHAIR_CURSOR)); 
    	}
	};

    private final LxAbstractAction staticTextAction = new LxAbstractAction(STATIC_TEXT,
    "Static Text", "Static Text", "TypeIcon.gif",true){
    
    	public void processAction(java.awt.event.ActionEvent e){

	    	 com.cannontech.esub.editor.element.StaticText text = 
	    	 	new com.cannontech.esub.editor.element.StaticText();
	    	 
	    	 editor.setBehavior(text);
			 editor.elementPlacer.setElement(text);
			 editor.elementPlacer.setIsPlacing(true);
    		 editor.getLxView().setCursor(new java.awt.Cursor(java.awt.Cursor.CROSSHAIR_CURSOR)); 
    	}
	};	
    	
    private Editor editor;
    private HashMap actionMap;

	EditorActions(Editor e) {
		editor = e;
		actionMap = new HashMap(20);
		
		actionMap.put(NEW_DRAWING, newDrawingAction);
		actionMap.put(OPEN_DRAWING, openDrawingAction);
		actionMap.put(SAVE_DRAWING, saveDrawingAction);
		actionMap.put(SAVE_AS_DRAWING, saveDrawingAsAction);
		actionMap.put(EXPORT_DRAWING, exportDrawingAction);
		actionMap.put(EXIT_EDITOR, exitEditorAction);

		actionMap.put(DELETE_ELEMENT, deleteElementAction);
		
		actionMap.put(TO_FRONT_LAYER, toFrontLayerAction );
		actionMap.put(TO_BACK_LAYER, toBackLayerAction);
		
		actionMap.put(MAGNETIC_GRID, toggleGridAction );
		
		actionMap.put(EDIT_ELEMENT, editElementAction);

		actionMap.put(STATIC_IMAGE, staticImageAction);
		actionMap.put(DYNAMIC_TEXT, dynamicTextAction);
		actionMap.put(STATE_IMAGE, stateImageAction);
		actionMap.put(STATIC_TEXT, staticTextAction );
		actionMap.put(SET_DYNAMIC_TEXT_COLOR, setDynamicTextColor);

		LxView v = e.getLxView();
		
		LxAbstractAction action = (LxAbstractAction) v.getAction(LxView.CREATE_LINE_ACTION);					
		action.setIcon(new ImageIcon( Util.loadImage("LineIcon.gif")));
	
		actionMap.put(CREATE_LINE, action);

//		action = (LxAbstractAction) v.getAction(LxView.CREATE_LINK_ACTION);
//		action.setIcon(
//		actionMap.put(CREATE_LINK, action);
		 
		action = (LxAbstractAction) v.getAction(LxView.CREATE_RECTANGLE_ACTION); 
		action.setIcon(new ImageIcon( Util.loadImage("SquareIcon.gif")));	
		actionMap.put(CREATE_RECTANGLE, action);
//		actionMap.put(CREATE_IMAGE, v.getAction(LxView.CREATE_IMAGE_ACTION));

		actionMap.put(CREATE_TEXT, v.getAction(LxView.CREATE_TEXT_ACTION));
	}
    LxAbstractAction getAction(String actionName) {
	    return (LxAbstractAction) actionMap.get(actionName);
    }
/**
 * Creation date: (1/2/2002 3:26:05 PM)
 * @return com.cannontech.esub.editor.Editor
 */
Editor getEditor() {
	return editor;
}
/**
 * Creation date: (1/2/2002 3:26:05 PM)
 * @param newEditor com.cannontech.esub.editor.Editor
 */
void setEditor(Editor newEditor) {
	editor = newEditor;
}
}
