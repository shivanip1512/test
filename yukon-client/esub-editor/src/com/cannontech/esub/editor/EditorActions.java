package com.cannontech.esub.editor;

/**
 * Creation date: (12/17/2001 1:46:44 PM)
 * @author: 
 */

import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.Set;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.esub.element.AlarmTextElement;
import com.cannontech.esub.element.CurrentAlarmsTable;
import com.cannontech.esub.element.DynamicGraphElement;
import com.cannontech.esub.util.Util;
import com.cannontech.message.dispatch.ClientConnection;
import com.loox.jloox.LxAbstractAction;
import com.loox.jloox.LxComponent;
import com.loox.jloox.LxGraph;
import com.loox.jloox.LxView;

class EditorActions {

	// More global type actions
	public static final String NEW_DRAWING = "NEW DRAWING";
	public static final String OPEN_DRAWING = "OPEN DRAWING";
	public static final String SAVE_DRAWING = "SAVE DRAWING";
	public static final String SAVE_AS_DRAWING = "SAVE AS DRAWING";
//	public static final String EXPORT_DRAWING = "EXPORT DRAWING";
	public static final String DRAWING_PROPERTIES = "DRAWING PROPERTIES";
	public static final String EXIT_EDITOR = "EXIT EDITOR";
	public static final String MAGNETIC_GRID = "MAGNETIC GRID";

	// Element creation related actions
	public static final String CREATE_LINE = "CREATE LINE";
	public static final String CREATE_LINK = "CREATE LINK";
	public static final String CREATE_RECTANGLE = "CREATE RECTANGLE";
	public static final String CREATE_IMAGE = "CREATE IMAGE";
	public static final String CREATE_TEXT = "CREATE TEXT";

	public static final String STATIC_IMAGE = "STATIC IMAGE";
	public static final String DYNAMIC_TEXT = "DYNAMIC TEXT";
	public static final String STATE_IMAGE = "STATE IMAGE";
	public static final String STATIC_TEXT = "STATIC TEXT";
	public static final String DYNAMIC_GRAPH = "DYNAMIC GRAPH";
	public static final String ALARM_TABLE_ELEMENT = "ALARM TABLE";
	public static final String ALARM_TEXT_ELEMENT = "ALARM TEXT";
	
	//Element or group of elements related actions
	public static final String ALIGN_ELEMENTS_LEFT = "ALIGN LEFT";
	public static final String ALIGN_ELEMENTS_RIGHT = "ALIGN RIGHT";
	public static final String ALIGN_ELEMENTS_TOP = "ALIGN TOP";
	public static final String ALIGN_ELEMENTS_BOTTOM = "ALIGN BOTTOM";
	public static final String ALIGN_ELEMENTS_VERTICAL = "ALIGN VERTICAL";
	public static final String ALIGN_ELEMENTS_HORIZONTAL = "ALIGN HORIZONTAL";
	
	public static final String ROTATE_ELEMENT_90 = "ROTATE 90";
	public static final String ROTATE_ELEMENT_180 = "ROTATE 180";
	public static final String ROTATE_ELEMENT_270 = "ROTATE 270";
	
	public static final String UNDO_OPERATION = "UNDO";
	public static final String REDO_OPERATION = "REDO";
	
	public static final String CUT_ELEMENT = "CUT ELEMENT";
	public static final String COPY_ELEMENT = "COPY ELEMENT";
	public static final String PASTE_ELEMENT = "PASTE ELEMENT";
	
	public static final String DELETE_ELEMENT = "DELETE ELEMENT";
	public static final String TO_FRONT_LAYER = "TO FRONT";
	public static final String TO_BACK_LAYER = "TO BACK";
	public static final String MOVE_SELECTED_UP_1 = "MOVE SELECTED U1";
	public static final String MOVE_SELECTED_RIGHT_1 = "MOVE SELECTED R1";
	public static final String MOVE_SELECTED_DOWN_1 = "MOVE SELECTED D1";
	public static final String MOVE_SELECTED_LEFT_1 = "MOVE SELECTED L1";	
	public static final String EDIT_ELEMENT = "EDIT ELEMENT";

	public static final String ABOUT_ESUB_EDITOR = "ABOUT ESUB EDITOR";	

	public static final String SET_DYNAMIC_TEXT_COLOR =
		"SET DYNAMIC TEXT COLOR";

	// Action definitions
	private final LxAbstractAction newDrawingAction =
		new LxAbstractAction(
			NEW_DRAWING,
			"New",
			"Create a new drawing",
			null,
			true) {
		public void processAction(ActionEvent e) {
				// Give an option to save the current graph if modified.
	int r = editor.saveOption();

			if (r != JOptionPane.CANCEL_OPTION) {
				editor.newDrawing();

			}
		}
	};

	private final LxAbstractAction openDrawingAction =
		new LxAbstractAction(
			OPEN_DRAWING,
			"Open",
			"Open a new drawing",
			null,
			true) {

		/* 
		* Opens a file and if neceesary asks the
		* user to save current file.
		*/
		public void processAction(ActionEvent e) {
			editor.openDrawing();

		}
	};

	private final LxAbstractAction saveDrawingAction =
		new LxAbstractAction(
			SAVE_DRAWING,
			"Save",
			"Save the current drawing",
			null,
			true) {
		public void processAction(ActionEvent evt) {
			String openFile = editor.getOpenFile();
			if (openFile == null)
				editor.saveAsDrawing();
			else
				editor.saveDrawing();
		}
	};

	private final LxAbstractAction saveDrawingAsAction =
		new LxAbstractAction(
			SAVE_AS_DRAWING,
			"Save As...",
			"Save the current drawing",
			null,
			true) {
		public void processAction(ActionEvent evt) {
			editor.saveAsDrawing();
		}
	};

	private final LxAbstractAction drawingPropertiesAction =
		new LxAbstractAction(
			DRAWING_PROPERTIES,
			"Properties...",
			"Set drawing properties",
			null,
			true) {
		public void processAction(ActionEvent evt) {
			editor.editDrawingProperties();
		}
	};

	private final LxAbstractAction exitEditorAction =
		new LxAbstractAction(
			EXIT_EDITOR,
			"Exit",
			"Exit the editor",
			null,
			true) {
		public void processAction(ActionEvent evt) {
			int r = editor.saveOption();
			if (r != JOptionPane.CANCEL_OPTION) {
				try {	
				ClientConnection conn = Util.getConnToDispatch();
				if ( conn != null && conn.isValid() ) {  // free up Dispatchs resources		
					com.cannontech.message.dispatch.message.Command comm = new com.cannontech.message.dispatch.message.Command();
					comm.setPriority(15);				
					comm.setOperation( 
						com.cannontech.message.dispatch.message.Command.CLIENT_APP_SHUTDOWN );

					conn.write( comm );
					conn.disconnect();
				}
				}
				catch ( java.io.IOException e ) {
					CTILogger.error( e.getMessage(), e );
				}

				System.exit(0);
			}
		}
	};

	private final LxAbstractAction deleteElementAction =
		new LxAbstractAction(DELETE_ELEMENT, "Delete", "Delete", null, true) {
		public void processAction(ActionEvent e) {
			LxGraph graph = editor.getDrawing().getLxGraph();
			graph.startUndoEdit("delete elements");
			Object[] allSelected = graph.getSelectedObjects();
			for(int i = 0; i < allSelected.length; i++) {
			 	if(allSelected[i] instanceof com.loox.jloox.LxComponent) {
			 		graph.remove((com.loox.jloox.LxComponent) allSelected[i]);
			 	}
			}
			graph.finishUndoEdit();
		}
	};

	private final LxAbstractAction alignLeft = 
		new LxAbstractAction(
			ALIGN_ELEMENTS_LEFT,
			"Align Left",
			"Align Left",
			null,
			true) {
		public void processAction(ActionEvent e) {
			LxGraph graph = editor.getDrawing().getLxGraph();
			Object[] allSelected = graph.getSelectedObjects();
			
			double minX = Double.MAX_VALUE;
			for(int i = 0; i < allSelected.length; i++) {
				if(allSelected[i] instanceof LxComponent) {
					LxComponent comp = (LxComponent) allSelected[i];
					if(comp.getX() < minX) {
						minX = comp.getX();
					}
				}
			}
			
			graph.startUndoEdit("align left");
			for(int i = 0; i < allSelected.length; i++) {
				if(allSelected[i] instanceof LxComponent) {
					LxComponent comp = (LxComponent) allSelected[i];
					comp.setX(minX);
				}
			}
			graph.finishUndoEdit();
		}
	};
	
	private final LxAbstractAction alignRight = 
		new LxAbstractAction(
			ALIGN_ELEMENTS_RIGHT,
			"Align Right",
			"Align Right",
			null,
			true) {
		public void processAction(ActionEvent e) {
			LxGraph graph = editor.getDrawing().getLxGraph();
			Object[] allSelected = graph.getSelectedObjects();
			
			double maxX = Double.MIN_VALUE;
			for(int i = 0; i < allSelected.length; i++) {
				if(allSelected[i] instanceof LxComponent) {
					LxComponent comp = (LxComponent) allSelected[i];
					if(comp.getX()+comp.getWidth() > maxX) {
						maxX = comp.getX()+comp.getWidth();
					}
				}
			}
			graph.startUndoEdit("align right");
			for(int i = 0; i < allSelected.length; i++) {
				if(allSelected[i] instanceof LxComponent) {
					LxComponent comp = (LxComponent) allSelected[i];
					comp.setX(maxX-comp.getWidth());
				}
			}
			graph.finishUndoEdit();
		}
	};
	
	private final LxAbstractAction alignTop = 
		new LxAbstractAction(
			ALIGN_ELEMENTS_TOP,
			"Align Top",
			"Align Top",
			null,
			true) {
		public void processAction(ActionEvent e) {
			LxGraph graph = editor.getDrawing().getLxGraph();
			Object[] allSelected = graph.getSelectedObjects();
			
			double minY = Double.MAX_VALUE;
			for(int i = 0; i < allSelected.length; i++) {
				if(allSelected[i] instanceof LxComponent) {
					LxComponent comp = (LxComponent) allSelected[i];
					if(comp.getY() < minY) {
						minY = comp.getY();
					}
				}
			}
			graph.startUndoEdit("align top");
			for(int i = 0; i < allSelected.length; i++) {
				if(allSelected[i] instanceof LxComponent) {
					LxComponent comp = (LxComponent) allSelected[i];
					comp.setY(minY);
				}
			}
			graph.finishUndoEdit();
		}
	};
	
	private final LxAbstractAction alignBottom = 
		new LxAbstractAction(
			ALIGN_ELEMENTS_BOTTOM,
			"Align Bottom",
			"Align Bottom",
			null,
			true) {
		public void processAction(ActionEvent e) {
			LxGraph graph = editor.getDrawing().getLxGraph();
			Object[] allSelected = graph.getSelectedObjects();
			
			double maxY = Double.MIN_VALUE;
			for(int i = 0; i < allSelected.length; i++) {
				if(allSelected[i] instanceof LxComponent) {
					LxComponent comp = (LxComponent) allSelected[i];
					if(comp.getY()+comp.getHeight() > maxY) {
						maxY = comp.getY()+comp.getHeight();
					}
				}
			}
			graph.startUndoEdit("align bottom");
			for(int i = 0; i < allSelected.length; i++) {
				if(allSelected[i] instanceof LxComponent) {
					LxComponent comp = (LxComponent) allSelected[i];
					comp.setY(maxY-comp.getHeight());
				}
			}
			graph.finishUndoEdit();
		}
	};
		
	private final LxAbstractAction alignVertical = 
		new LxAbstractAction(
			ALIGN_ELEMENTS_VERTICAL,
			"Align Vertical",
			"Align Vertical",
			null,
			true) {
		public void processAction(ActionEvent e) {
			LxGraph graph = editor.getDrawing().getLxGraph();
			Object[] allSelected = graph.getSelectedObjects();
			
			int selComp = 0;
			double avgX = 0.0;
			for(int i = 0; i < allSelected.length; i++) {
				if(allSelected[i] instanceof LxComponent) {
					LxComponent comp = (LxComponent) allSelected[i];
					avgX += comp.getCenterX();
					selComp++;
				}
			}
			avgX /= selComp;
			graph.startUndoEdit("align vertical");
			for(int i = 0; i < allSelected.length; i++) {
				if(allSelected[i] instanceof LxComponent) {
					LxComponent comp = (LxComponent) allSelected[i];
					comp.setCenterX(avgX);
				}
			}		
			graph.finishUndoEdit();
		}
	};
		
	private final LxAbstractAction alignHorizontal = 
		new LxAbstractAction(
			ALIGN_ELEMENTS_HORIZONTAL,
			"Align Horizontal",
			"Align Horizontal",
			null,
			true) {
		public void processAction(ActionEvent e) {
			LxGraph graph = editor.getDrawing().getLxGraph();
			Object[] allSelected = graph.getSelectedObjects();
			
			int selComp = 0;
			double avgY = 0.0;
			for(int i = 0; i < allSelected.length; i++) {
				if(allSelected[i] instanceof LxComponent) {
					LxComponent comp = (LxComponent) allSelected[i];
					avgY += comp.getCenterY();
					selComp++;
				}
			}
			avgY /= selComp;
			graph.startUndoEdit("align horizontal");
			for(int i = 0; i < allSelected.length; i++) {
				if(allSelected[i] instanceof LxComponent) {
					LxComponent comp = (LxComponent) allSelected[i];
					comp.setCenterY(avgY);
				}
			}		
			graph.finishUndoEdit();			
		}
	};
	
	private final LxAbstractAction rotateElement90Action = 
		new LxAbstractAction(
			ROTATE_ELEMENT_90,
			"90",
			"90",
			null,
			true) {
		public void processAction(ActionEvent e) {
			Util.rotateSelected(editor.getDrawing().getLxGraph(), Math.PI/2.0);
		}
	};
		
	private final LxAbstractAction rotateElement180Action = 
		new LxAbstractAction(
			ROTATE_ELEMENT_180,
			"180",
			"180",
			null,
			true) {
		public void processAction(ActionEvent e) {
			/* WORKAROUND FOR JLOOX BUG */
			/* ROTATE FAILS FOR 180 DEGREE ROTATION!! */
			Util.rotateSelected(editor.getDrawing().getLxGraph(), Math.PI/2.0);
			Util.rotateSelected(editor.getDrawing().getLxGraph(), Math.PI/2.0);
		}
	};
	
	private final LxAbstractAction rotateElement270Action = 
		new LxAbstractAction (
			ROTATE_ELEMENT_270,
			"270",
			"270",
			null,
			true) {
		public void processAction(ActionEvent e) {
			Util.rotateSelected(editor.getDrawing().getLxGraph(), 3.0*Math.PI/2.0);
		}
	};
		
			
	private final LxAbstractAction undoOperationAction =
		new LxAbstractAction(
			UNDO_OPERATION,
			"Undo",
			"Undo",
			null,
			true) {
		public void processAction(ActionEvent e) {
			try {			
				editor.getUndoManager().undo();
				editor.synchActionsWithSelection();
			}
			catch(CannotUndoException cue) {
				CTILogger.debug("Cannot undo");
			}
		}
	};
	
	private final LxAbstractAction redoOperationAction =
		new LxAbstractAction(
			REDO_OPERATION,
			"Redo",
			"Redo",
			null,
			true) {
		public void processAction(ActionEvent e) {
			try {
				editor.getUndoManager().redo();
				editor.synchActionsWithSelection();
			}
			catch(CannotRedoException cre) {
				CTILogger.debug("Cannot redo");
			}
		}
	};
				
	private final LxAbstractAction cutElementAction =
		new LxAbstractAction(
			CUT_ELEMENT,
			"Cut",
			"Cut",
			null,
			true) {
		public void processAction(ActionEvent e) {
			editor.cutSelection();
		}
	};
	
	private final LxAbstractAction copyElementAction = 
		new LxAbstractAction(
			COPY_ELEMENT,
			"Copy",
			"Copy", 
			null,
			true) {
		public void processAction(ActionEvent e) {
			editor.copySelection();
		}
	};
	
	private final LxAbstractAction pasteElementAction =
		new LxAbstractAction(
			PASTE_ELEMENT,
			"Paste",
			"Paste",
			null,
			true) {
		public void processAction(ActionEvent e) {
			editor.pasteSelection();
		}
	}; 
			
	private final LxAbstractAction toFrontLayerAction =
		new LxAbstractAction(
			TO_FRONT_LAYER,
			"Send to front",
			"Send to front",
			null,
			true) {
		public void processAction(ActionEvent e) {
			editor.getDrawing().getLxGraph().raiseSelection();
		}
	};

	private final LxAbstractAction toBackLayerAction =
		new LxAbstractAction(
			TO_BACK_LAYER,
			"Send to back",
			"Send to back",
			null,
			true) {
		public void processAction(ActionEvent e) {
			editor.getDrawing().getLxGraph().lowerSelection();
		}
	};

	private final LxAbstractAction moveSelectedElementUpAction = 
		new LxAbstractAction(
			MOVE_SELECTED_UP_1,
			"Move up",
			"Move up",
			null,
			true) {
				public void processAction(ActionEvent e) {
					LxGraph g = editor.getDrawing().getLxGraph();
					for( 	int i = 0;
							i < g.getSelectedObjectCount();
							i++ ) {
								LxComponent comp = g.getSelectedObject(i);								
								Point2D pc = comp.getLocation();
								pc.setLocation( pc.getX(), pc.getY()-1);
								comp.setLocation(pc);
							}
		
				}	
			};

	private final LxAbstractAction moveSelectedElementRightAction = 
		new LxAbstractAction(
			MOVE_SELECTED_RIGHT_1,
			"Move right",
			"Move right",
			null,
			true) {
				public void processAction(ActionEvent e) {
					LxGraph g = editor.getDrawing().getLxGraph();
					for( 	int i = 0;
							i < g.getSelectedObjectCount();
							i++ ) {
								LxComponent comp = g.getSelectedObject(i);								
								Point2D pc = comp.getLocation();
								pc.setLocation( pc.getX()+1, pc.getY());
								comp.setLocation(pc);
							}
		
				}	
			};
			
	private final LxAbstractAction moveSelectedElementDownAction = 
		new LxAbstractAction(
			MOVE_SELECTED_DOWN_1,
			"Move down",
			"Move down",
			null,
			true) {
				public void processAction(ActionEvent e) {
					LxGraph g = editor.getDrawing().getLxGraph();
					for( 	int i = 0;
							i < g.getSelectedObjectCount();
							i++ ) {
								LxComponent comp = g.getSelectedObject(i);								
								Point2D pc = comp.getLocation();
								pc.setLocation( pc.getX(), pc.getY()+1);
								comp.setLocation(pc);
							}
		
				}	
			};
			
	private final LxAbstractAction moveSelectedElementLeftAction = 
		new LxAbstractAction(
			MOVE_SELECTED_LEFT_1,
			"Move left",
			"Move left",
			null,
			true) {
				public void processAction(ActionEvent e) {
					LxGraph g = editor.getDrawing().getLxGraph();
					for( 	int i = 0;
							i < g.getSelectedObjectCount();
							i++ ) {
								LxComponent comp = g.getSelectedObject(i);								
								Point2D pc = comp.getLocation();
								pc.setLocation( pc.getX()-1, pc.getY());
								comp.setLocation(pc);
							}
		
				}	
			};
			
	private final LxAbstractAction toggleGridAction =
		new LxAbstractAction(
			MAGNETIC_GRID,
			"Grid...",
			"Grid Settings",
			null,
			true) {

		public void processAction(ActionEvent evt) {
			LxView view = editor.getDrawing().getLxView();
			MagneticGridPanel p = new MagneticGridPanel(view);
			JOptionPane.showMessageDialog(
				view,
				p,
				"Magnetic Grid Settings",
				javax.swing.JOptionPane.PLAIN_MESSAGE,
				null);
		}
	};

	private final LxAbstractAction editElementAction =
		new LxAbstractAction(
			EDIT_ELEMENT,
			"Edit Element",
			"Edit Element",
			null,
			true) {
		public void processAction(ActionEvent evt) {
			editor.editElement(null);
		}
	};

	// ELEMENT ACTIONS

	private final LxAbstractAction staticImageAction =
		new LxAbstractAction(
			STATIC_IMAGE,
			"Static Image",
			"Static Image",
			"GraphicIcon.gif",
			true) {

		public void processAction(java.awt.event.ActionEvent e) {
			editor.getDrawing().getLxView().stopInteractiveCreation();
			
			com.cannontech.esub.element.StaticImage image =
				new com.cannontech.esub.element.StaticImage();

			image.setDrawing(editor.getDrawing());

			editor.setBehavior(image);
			editor.elementPlacer.setElement(image);
			editor.elementPlacer.setIsPlacing(true);
			editor.getDrawing().getLxView().setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
		}
	};

	private final LxAbstractAction dynamicTextAction =
		new LxAbstractAction(
			DYNAMIC_TEXT,
			"Dynamic Text",
			"Dynamic Text",
			"AttIcon.gif",
			true) {

		public void processAction(java.awt.event.ActionEvent e) {
			editor.getDrawing().getLxView().stopInteractiveCreation();
			
			com.cannontech.esub.element.DynamicText text =
				new com.cannontech.esub.element.DynamicText();

			text.setDrawing(editor.getDrawing());

			editor.setBehavior(text);
			editor.elementPlacer.setElement(text);
			editor.elementPlacer.setIsPlacing(true);
			editor.getDrawing().getLxView().setCursor(
				new java.awt.Cursor(java.awt.Cursor.CROSSHAIR_CURSOR));
		}
	};

	private final LxAbstractAction setDynamicTextColor =
		new LxAbstractAction(
			SET_DYNAMIC_TEXT_COLOR,
			"Set Color",
			"Set Color",
			null,
			true) {
		public void processAction(ActionEvent e) {
			Object[] o = editor.getDrawing().getLxGraph().getSelectedObjects();
			for (int i = 0; i < o.length; i++) {
				CTILogger.info(o.getClass());
			}
		}
	};

	private final LxAbstractAction stateImageAction =
		new LxAbstractAction(
			STATE_IMAGE,
			"State Image",
			"State Image",
			"StateIcon.gif",
			true) {

		public void processAction(java.awt.event.ActionEvent e) {
			editor.getDrawing().getLxView().stopInteractiveCreation();
			
			com.cannontech.esub.element.StateImage si =
				new com.cannontech.esub.element.StateImage();

			si.setDrawing(editor.getDrawing());

			editor.setBehavior(si);
			editor.elementPlacer.setElement(si);
			editor.elementPlacer.setIsPlacing(true);
			editor.getDrawing().getLxView().setCursor(
				new java.awt.Cursor(java.awt.Cursor.CROSSHAIR_CURSOR));
		}
	};

	private final LxAbstractAction staticTextAction =
		new LxAbstractAction(
			STATIC_TEXT,
			"Static Text",
			"Static Text",
			"TypeIcon.gif",
			true) {

		public void processAction(java.awt.event.ActionEvent e) {
			editor.getDrawing().getLxView().stopInteractiveCreation();
			
			com.cannontech.esub.element.StaticText text =
				new com.cannontech.esub.element.StaticText();

			text.setDrawing(editor.getDrawing());

			editor.setBehavior(text);
			editor.elementPlacer.setElement(text);
			editor.elementPlacer.setIsPlacing(true);
			editor.getDrawing().getLxView().setCursor(
				new java.awt.Cursor(java.awt.Cursor.CROSSHAIR_CURSOR));
		}
	};

	private final LxAbstractAction dynamicGraphAction = 
		new LxAbstractAction(
			DYNAMIC_GRAPH,
			"Dynamic Graph",
			"Dynamic Graph",
			"GraphIcon.gif",
			true) {
			
		public void processAction(ActionEvent e ) {
			editor.getDrawing().getLxView().stopInteractiveCreation();
						
			DynamicGraphElement graph = 
				new DynamicGraphElement();
				
			graph.setDrawing(editor.getDrawing());
			editor.setBehavior(graph);
			editor.elementPlacer.setElement(graph);
			editor.elementPlacer.setIsPlacing(true);
			editor.getDrawing().getLxView().setCursor(
				new Cursor(Cursor.CROSSHAIR_CURSOR) );
		}
	};
	
	private final LxAbstractAction alarmTableElementAction =
		new LxAbstractAction(
			ALARM_TABLE_ELEMENT,
			"Alarm Table",
			"Alarm Table",
			"TableAlarm.gif",
			true) {
		public void processAction(ActionEvent e) {	

			editor.getDrawing().getLxView().stopInteractiveCreation();
									
			CurrentAlarmsTable table = new com.cannontech.esub.element.CurrentAlarmsTable();
				
			editor.setBehavior(table);
			editor.elementPlacer.setElement(table);
			editor.elementPlacer.setIsPlacing(true);
			
			editor.getDrawing().getLxView().setCursor(
			new Cursor(Cursor.CROSSHAIR_CURSOR) );
				
		}
	};
	
	private final LxAbstractAction alarmTextElementAction =
		new LxAbstractAction(
			ALARM_TEXT_ELEMENT,
			"Alarm Text",
			"Alarm Text",
			"TextAlarm.gif",
			true) {
				public void processAction(ActionEvent e) {
					editor.getDrawing().getLxView().stopInteractiveCreation();
										
					AlarmTextElement elem = new AlarmTextElement();
					
					editor.setBehavior(elem);
					editor.elementPlacer.setElement(elem);
					editor.elementPlacer.setIsPlacing(true);
					
					editor.getDrawing().getLxView().setCursor(
						new Cursor(Cursor.CROSSHAIR_CURSOR));
				}
			};
					
	private final LxAbstractAction aboutEsubEditor = 
		new LxAbstractAction(
			ABOUT_ESUB_EDITOR,
			"About",
			"About",
			null,
			true ) {
				public void processAction(ActionEvent e) {
					editor.showAboutDialog();		
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
		actionMap.put(DRAWING_PROPERTIES, drawingPropertiesAction);
		actionMap.put(EXIT_EDITOR, exitEditorAction);

		actionMap.put(DELETE_ELEMENT, deleteElementAction);

		actionMap.put(ALIGN_ELEMENTS_LEFT, alignLeft);
		actionMap.put(ALIGN_ELEMENTS_RIGHT, alignRight);
		actionMap.put(ALIGN_ELEMENTS_TOP, alignTop);
		actionMap.put(ALIGN_ELEMENTS_BOTTOM, alignBottom);
		actionMap.put(ALIGN_ELEMENTS_VERTICAL, alignVertical);
		actionMap.put(ALIGN_ELEMENTS_HORIZONTAL, alignHorizontal);
		
		actionMap.put(ROTATE_ELEMENT_90, rotateElement90Action);
		actionMap.put(ROTATE_ELEMENT_180, rotateElement180Action);
		actionMap.put(ROTATE_ELEMENT_270, rotateElement270Action);
		
		actionMap.put(UNDO_OPERATION, undoOperationAction);
		actionMap.put(REDO_OPERATION, redoOperationAction);
		
		actionMap.put(CUT_ELEMENT, cutElementAction);
		actionMap.put(COPY_ELEMENT, copyElementAction);
		actionMap.put(PASTE_ELEMENT, pasteElementAction);
		
		actionMap.put(TO_FRONT_LAYER, toFrontLayerAction);
		actionMap.put(TO_BACK_LAYER, toBackLayerAction);

		actionMap.put(MOVE_SELECTED_UP_1, moveSelectedElementUpAction);
		actionMap.put(MOVE_SELECTED_RIGHT_1, moveSelectedElementRightAction);
		actionMap.put(MOVE_SELECTED_DOWN_1, moveSelectedElementDownAction);
		actionMap.put(MOVE_SELECTED_LEFT_1, moveSelectedElementLeftAction);
		
		actionMap.put(MAGNETIC_GRID, toggleGridAction);

		actionMap.put(EDIT_ELEMENT, editElementAction);

		actionMap.put(STATIC_IMAGE, staticImageAction);
		actionMap.put(DYNAMIC_TEXT, dynamicTextAction);
		actionMap.put(STATE_IMAGE, stateImageAction);
		actionMap.put(STATIC_TEXT, staticTextAction);
		actionMap.put(SET_DYNAMIC_TEXT_COLOR, setDynamicTextColor);
		actionMap.put(DYNAMIC_GRAPH, dynamicGraphAction);
		actionMap.put(ALARM_TABLE_ELEMENT, alarmTableElementAction);
		actionMap.put(ALARM_TEXT_ELEMENT, alarmTextElementAction);
		
		actionMap.put(ABOUT_ESUB_EDITOR, aboutEsubEditor);
		
		LxView v = e.getDrawing().getLxView();

		LxAbstractAction action =
			(LxAbstractAction) v.getAction(LxView.CREATE_LINE_ACTION);
		action.setIcon(new ImageIcon(Util.findImage("LineIcon.gif")));

		actionMap.put(CREATE_LINE, action);

		//		action = (LxAbstractAction) v.getAction(LxView.CREATE_LINK_ACTION);
		//		action.setIcon(
		//		actionMap.put(CREATE_LINK, action);

		action = (LxAbstractAction) v.getAction(LxView.CREATE_RECTANGLE_ACTION);
		action.setIcon(new ImageIcon(Util.findImage("SquareIcon.gif")));
		actionMap.put(CREATE_RECTANGLE, action);
		//		actionMap.put(CREATE_IMAGE, v.getAction(LxView.CREATE_IMAGE_ACTION));

		actionMap.put(CREATE_TEXT, v.getAction(LxView.CREATE_TEXT_ACTION));
		actionMap.put("ZOOM", v.getAction(LxView.ZOOM_IN_400_ACTION));
	}
	LxAbstractAction getAction(String actionName) {
		return (LxAbstractAction) actionMap.get(actionName);
	}
	
	LxAbstractAction[] getAllActions() {
		Set s = actionMap.entrySet();
		LxAbstractAction allActions[] = new LxAbstractAction[s.size()];
		s.toArray(allActions);
		return allActions;
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
