package com.cannontech.esub.editor;

import java.awt.event.KeyEvent;

import javax.swing.JComponent;
import javax.swing.KeyStroke;

import com.loox.jloox.LxView;

/**
 * Description Here
 * @author alauinger
 */
public class EditorKeys {
	public EditorKeys(EditorActions e) {
		initialize(e);
	}
	
	private void initialize(EditorActions editorActions) {
		LxView lxView = editorActions.getEditor().getDrawing().getLxView();
		
		lxView.registerKeyboardAction(
			editorActions.getAction(EditorActions.DELETE_ELEMENT),
			KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0, true),
			JComponent.WHEN_IN_FOCUSED_WINDOW);

		lxView.registerKeyboardAction(
			editorActions.getAction(EditorActions.MOVE_SELECTED_UP_1),
			KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0, true),
			JComponent.WHEN_IN_FOCUSED_WINDOW);
		
		lxView.registerKeyboardAction(
			editorActions.getAction(EditorActions.MOVE_SELECTED_RIGHT_1),
			KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0, true),
			JComponent.WHEN_IN_FOCUSED_WINDOW);
			
		lxView.registerKeyboardAction(
			editorActions.getAction(EditorActions.MOVE_SELECTED_DOWN_1),
			KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0, true),
			JComponent.WHEN_IN_FOCUSED_WINDOW);
		
		lxView.registerKeyboardAction(
			editorActions.getAction(EditorActions.MOVE_SELECTED_LEFT_1),
			KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0, true),
			JComponent.WHEN_IN_FOCUSED_WINDOW);
	}
}
