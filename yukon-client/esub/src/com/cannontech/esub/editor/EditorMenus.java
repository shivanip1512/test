package com.cannontech.esub.editor;

/**
 * Creation date: (12/17/2001 1:46:54 PM)
 * @author: 
 */

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;

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

	action = actions.getAction(EditorActions.OPEN_DRAWING);
	JMenuItem openItem = createMenuItem(action.getLabel(), action);

	action = actions.getAction(EditorActions.SAVE_DRAWING);
	JMenuItem saveItem = createMenuItem(action.getLabel(), action);

	action = actions.getAction(EditorActions.SAVE_AS_DRAWING);
	JMenuItem saveAsItem = createMenuItem(action.getLabel(), action);

	action = actions.getAction(EditorActions.EXPORT_DRAWING);	
	//action = actions.getAction("ZOOM"); 
	JMenuItem exportItem = createMenuItem(action.getLabel(), action);
	
	action = actions.getAction(EditorActions.EXIT_EDITOR);
	JMenuItem exitItem = createMenuItem(action.getLabel(), action);

	fileMenu.add(newItem);
	fileMenu.add(openItem);
	fileMenu.add(saveItem);
	fileMenu.add(saveAsItem);
	fileMenu.add(new JSeparator());
	fileMenu.add(exportItem);
	fileMenu.add(new JSeparator());
	fileMenu.add(exitItem);

	JMenu editMenu = new JMenu("Edit");

	action = actions.getAction(EditorActions.TO_FRONT_LAYER);
	JMenuItem toFrontItem = createMenuItem(action.getLabel(), action);

	action = actions.getAction(EditorActions.TO_BACK_LAYER);
	JMenuItem toBackItem = createMenuItem(action.getLabel(), action);

	action = actions.getAction(EditorActions.DELETE_ELEMENT);
	JMenuItem deleteItem = createMenuItem(action.getLabel(), action);
	
	editMenu.add(toFrontItem);
	editMenu.add(toBackItem);
	editMenu.add(new javax.swing.JSeparator());
	editMenu.add(deleteItem);
	
	// Add cut copy paste here

	JMenu viewMenu = new JMenu("View");

	action = actions.getAction(EditorActions.MAGNETIC_GRID);
	JMenuItem gridItem = createMenuItem(action.getLabel(), action);	
	viewMenu.add(gridItem);
	
	menuBar.add(fileMenu);
	menuBar.add(editMenu);
	menuBar.add(viewMenu);
}
}
