package com.cannontech.esub.editor.element;

import com.cannontech.common.editor.PropertyPanel;
import com.cannontech.esub.element.AlarmTextElement;
import com.cannontech.esub.element.CurrentAlarmsTable;
import com.cannontech.esub.element.DrawingMetaElement;
import com.cannontech.esub.element.DynamicGraphElement;
import com.cannontech.esub.element.DynamicText;
import com.cannontech.esub.element.StateImage;
import com.cannontech.esub.element.StaticImage;
import com.cannontech.esub.element.StaticText;

/**
 * Creation date: (12/18/2001 12:53:50 PM)
 * @author: Aaron Lauinger 
 */
public class ElementEditorFactory {
	private static final Class[][] classEditorTable = {
		{ DynamicText.class, DynamicTextEditor.class },
		{ StateImage.class, StateImageEditor.class },
		{ StaticImage.class, StaticImageEditor.class },
		{ StaticText.class, StaticTextEditor.class },
		{ DynamicGraphElement.class, DynamicGraphElementEditor.class },
		{ DrawingMetaElement.class, DrawingMetaElementEditor.class },
		{ CurrentAlarmsTable.class, CurrentAlarmsTableEditor.class },
		{ AlarmTextElement.class, AlarmTextElementEditor.class }
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
