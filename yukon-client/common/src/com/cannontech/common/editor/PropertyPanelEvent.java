package com.cannontech.common.editor;

/**
 * This type was created in VisualAge.
 */
public class PropertyPanelEvent extends java.util.EventObject {

	public static final int OK_SELECTION = 20000;
	public static final int CANCEL_SELECTION = 20001;
	public static final int APPLY_SELECTION = 20002;

	private int id;
	
/**
 * PropertyPanelEvent constructor comment.
 * @param source java.lang.Object
 */
public PropertyPanelEvent(Object source, int id) {
	super(source);

	this.id = id;
}
/**
 * This method was created in VisualAge.
 * @return int
 */
public int getID() {
	return id;
}
}
