package com.cannontech.common.wizard;

import com.cannontech.common.editor.PropertyPanelEvent;

/**
 * This type was created in VisualAge.
 */
public class WizardPanelEvent extends PropertyPanelEvent {

	public static final int BACK_SELECTION = 10000;
	public static final int NEXT_SELECTION = 10001;
	public static final int FINISH_SELECTION = 10002;
	public static final int CANCEL_SELECTION = 10003;

//	private int id;
	
/**
 * WizardPanelEvent constructor comment.
 * @param source java.lang.Object
 */
public WizardPanelEvent(Object source, int id) {
	super(source,id);
}

public WizardPanelEvent(Object source, int id, Object data_ ) {
	super(source,id, data_);
}

}
