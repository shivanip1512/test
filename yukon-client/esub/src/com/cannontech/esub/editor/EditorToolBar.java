package com.cannontech.esub.editor;

/**
 * Creation date: (12/17/2001 3:09:25 PM)
 * @author: 
 */

import java.awt.Dimension;

import javax.swing.AbstractButton;
import javax.swing.JToolBar;

import com.loox.jloox.LxAbstractAction;

public class EditorToolBar {
	
	private JToolBar toolBar;

	//Default size of each 'tool'
	private static final Dimension DEFAULT_TOOL_SIZE = new Dimension(35,38);	
	EditorToolBar(EditorActions actions) {
		initialize(actions);
	}
/**
 * Creation date: (12/19/2001 5:01:48 PM)
 */
public void addToToolBar(LxAbstractAction action, JToolBar toolBar) {
	AbstractButton button = action.addTo(toolBar);
	button.setPreferredSize(DEFAULT_TOOL_SIZE);		
	button.setText(null);
	button.setToolTipText( action.getTooltip() );
}
	JToolBar getToolBar() {
		return toolBar;
	}
	private void initialize(EditorActions actions) {
				
		toolBar = new javax.swing.JToolBar(JToolBar.VERTICAL);

		addToToolBar( actions.getAction(EditorActions.CREATE_LINE ), toolBar);
		addToToolBar( actions.getAction(EditorActions.CREATE_RECTANGLE), toolBar);
		addToToolBar( actions.getAction(EditorActions.STATIC_IMAGE), toolBar);
		addToToolBar( actions.getAction(EditorActions.STATIC_TEXT), toolBar);
		//addToToolBar( actions.getAction(EditorActions.CREATE_IMAGE), toolBar);
		//addToToolBar( actions.getAction(EditorActions.CREATE_TEXT), toolBar);		
		//addToToolBar( actions.getAction(EditorActions.CREATE_LINK), toolBar);
		addToToolBar( actions.getAction(EditorActions.DYNAMIC_TEXT), toolBar);
		addToToolBar( actions.getAction(EditorActions.STATE_IMAGE), toolBar);
		addToToolBar( actions.getAction(EditorActions.DYNAMIC_GRAPH), toolBar);
		addToToolBar( actions.getAction(EditorActions.ALARM_TABLE_ELEMENT), toolBar);
		addToToolBar( actions.getAction(EditorActions.ALARM_TEXT_ELEMENT), toolBar);
	
		
	}
}
