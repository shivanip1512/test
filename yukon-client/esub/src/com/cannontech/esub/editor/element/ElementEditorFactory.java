package com.cannontech.esub.editor.element;


import java.util.HashMap;
import com.cannontech.common.editor.PropertyPanel;

/**
 * Creation date: (12/18/2001 12:53:50 PM)
 * @author: Aaron Lauinger 
 */
public class ElementEditorFactory {
	private static final Class[][] classEditorTable = {
		{ DynamicText.class, DynamicTextEditor.class },
		{ StateImage.class, StateImageEditor.class },
		{ StaticImage.class, StaticImageEditor.class },
		{ StaticText.class, StaticTextEditor.class }
	};
		
	private static ElementEditorFactory instance = null;
/**
 * ElementEditorFactory constructor comment.
 */
ElementEditorFactory() {
	super();
}
/**
 * Creation date: (1/9/2002 2:06:07 PM)
 * @return com.cannontech.common.editor.PropertyPanel
 * @param element java.lang.Class
 */
public PropertyPanel createEditorPanel(Class element) {

	PropertyPanel panel = null;
	for( int i = 0; i < classEditorTable.length; i++ ) {
		if( classEditorTable[i][0] == element ) {
			try {
				panel = (PropertyPanel) classEditorTable[i][1].newInstance();
			}
			catch(InstantiationException ie) {
				ie.printStackTrace();
			}
			catch(IllegalAccessException ia) {
				ia.printStackTrace();
			}
		}
	}
	
	return panel;
}
/**
 * Creation date: (1/9/2002 2:04:34 PM)
 * @return com.cannontech.esub.editor.element.ElementEditorFactory
 */
public synchronized static ElementEditorFactory getInstance() {
	if( instance == null )
		instance = new ElementEditorFactory();

	return instance;
}
}
