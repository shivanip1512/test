package com.cannontech.esub.editor;

/**
 * Creation date: (12/17/2001 1:46:54 PM)
 * @author: 
 */

import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;
import javax.swing.KeyStroke;

import com.loox.jloox.LxAbstractAction;

class EditorMenus {

	private JMenuBar menuBar;

	// JMenuItem that listens to action propertychange for
	// enabling disabling
	class EMenuItem extends JMenuItem implements PropertyChangeListener {
		EMenuItem(String label) {
			super(label);
		}

		public void propertyChange(PropertyChangeEvent e) {
			if( e.getPropertyName().equals("enabled") ) {
				Boolean newValue = (Boolean) e.getNewValue();
				setEnabled( newValue.booleanValue());
			}
		}
	}	
	EditorMenus(EditorActions actions) {
		initialize(actions);
	}
/**
 * Creation date: (12/18/2001 12:21:21 PM)
 * @return javax.swing.JMenuItem
 * @param label java.lang.String
 * @param action com.loox.jloox.LxAbstractAction
 */
private JMenuItem createMenuItem(String label, LxAbstractAction action) {
	EMenuItem menuItem = new EMenuItem(label);
	menuItem.addActionListener(action);
	action.addPropertyChangeListener(menuItem);
	
	return menuItem;	
}
	JMenuBar getMenuBar() {
		return menuBar;
	}
/**
 * Creation date: (12/17/2001 2:15:13 PM)
 * @param actions com.cannontech.esub.editor.EditorActions
 */
private void initialize(EditorActions actions) {

	LxAbstractAction action;
	
	menuBar = new JMenuBar();
		
	JMenu fileMenu = new JMenu("File");

	action = actions.getAction(EditorActions.NEW_DRAWING);
	JMenuItem newItem = createMenuItem(action.getLabel(), action);
	newItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, KeyEvent.CTRL_DOWN_MASK));
	
	action = actions.getAction(EditorActions.OPEN_DRAWING);
	JMenuItem openItem = createMenuItem(action.getLabel(), action);
	openItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, KeyEvent.CTRL_DOWN_MASK));

	action = actions.getAction(EditorActions.SAVE_DRAWING);
	JMenuItem saveItem = createMenuItem(action.getLabel(), action);
	saveItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK));
	
	action = actions.getAction(EditorActions.SAVE_AS_DRAWING);
	JMenuItem saveAsItem = createMenuItem(action.getLabel(), action);

	action = actions.getAction(EditorActions.DRAWING_PROPERTIES);	
	JMenuItem propertiesItem = createMenuItem(action.getLabel(), action);
		
	action = actions.getAction(EditorActions.EXIT_EDITOR);
	JMenuItem exitItem = createMenuItem(action.getLabel(), action);

	fileMenu.add(newItem);
	fileMenu.add(openItem);
	fileMenu.add(saveItem);
	fileMenu.add(saveAsItem);
	fileMenu.add(new JSeparator());
	fileMenu.add(propertiesItem);
	fileMenu.add(new JSeparator());
	fileMenu.add(exitItem);

	JMenu editMenu = new JMenu("Edit");
	
	action = actions.getAction(EditorActions.CUT_ELEMENT);
	JMenuItem cutItem = createMenuItem(action.getLabel(), action);
	cutItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, KeyEvent.CTRL_DOWN_MASK));
	
	action = actions.getAction(EditorActions.COPY_ELEMENT);
	JMenuItem copyItem = createMenuItem(action.getLabel(), action);
	copyItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, KeyEvent.CTRL_DOWN_MASK));
	
	action = actions.getAction(EditorActions.PASTE_ELEMENT);
	JMenuItem pasteItem = createMenuItem(action.getLabel(), action);
	pasteItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, KeyEvent.CTRL_DOWN_MASK));

	action = actions.getAction(EditorActions.DELETE_ELEMENT);
	JMenuItem deleteItem = createMenuItem(action.getLabel(), action);	
			
	JMenu rotateSubMenu = new JMenu("Rotate");
	
	action = actions.getAction(EditorActions.ROTATE_ELEMENT_90);
	JMenuItem rotateNinetyItem = createMenuItem(action.getLabel(), action);
	
	action = actions.getAction(EditorActions.ROTATE_ELEMENT_180);
	JMenuItem rotateOneEightyItem = createMenuItem(action.getLabel(), action);
	
	action = actions.getAction(EditorActions.ROTATE_ELEMENT_270);
	JMenuItem rotateTwoSeventyItem = createMenuItem(action.getLabel(), action);
	
	action = actions.getAction(EditorActions.TO_FRONT_LAYER);
	JMenuItem toFrontItem = createMenuItem(action.getLabel(), action);

	action = actions.getAction(EditorActions.TO_BACK_LAYER);
	JMenuItem toBackItem = createMenuItem(action.getLabel(), action);

	rotateSubMenu.add(rotateNinetyItem);
	rotateSubMenu.add(rotateOneEightyItem);
	rotateSubMenu.add(rotateTwoSeventyItem);	
		
	editMenu.add(cutItem);
	editMenu.add(copyItem);
	editMenu.add(pasteItem);	
	editMenu.add(deleteItem);
	editMenu.add(new JSeparator());
	editMenu.add(rotateSubMenu);
	editMenu.add(new JSeparator());	
	editMenu.add(toFrontItem);
	editMenu.add(toBackItem);
	
	// Add cut copy paste here

	JMenu viewMenu = new JMenu("View");

	action = actions.getAction(EditorActions.MAGNETIC_GRID);
	JMenuItem gridItem = createMenuItem(action.getLabel(), action);	
	viewMenu.add(gridItem);
	
	JMenu helpMenu = new JMenu("Help");
	
	action = actions.getAction(EditorActions.ABOUT_ESUB_EDITOR);
	JMenuItem aboutEditorItem = createMenuItem(action.getLabel(), action);
	helpMenu.add(aboutEditorItem);
	
	menuBar.add(fileMenu);
	menuBar.add(editMenu);
	menuBar.add(viewMenu);
	menuBar.add(helpMenu);
}
}
