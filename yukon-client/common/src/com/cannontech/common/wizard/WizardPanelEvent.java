package com.cannontech.common.wizard;

/**
 * This type was created in VisualAge.
 */
public class WizardPanelEvent extends java.util.EventObject {

	public static final int BACK_SELECTION = 10000;
	public static final int NEXT_SELECTION = 10001;
	public static final int FINISH_SELECTION = 10002;
	public static final int CANCEL_SELECTION = 10003;

	private int id;
	
/**
 * WizardPanelEvent constructor comment.
 * @param source java.lang.Object
 */
public WizardPanelEvent(Object source, int id) {
	super(source);
	this.id = id;
}
/**
 * This method was created in VisualAge.
 * @return int
 */
public int getID() {
	return this.id;
}
}
