package com.cannontech.common.gui.util;

/**
 * This type was created in VisualAge.
 */
public class DataInputPanelEvent extends java.util.EventObject {
/**
 * DataInputPanelEvent constructor comment.
 * @param source java.lang.Object
 */

	public static final byte EVENT_INVALID = -1;
	public static final byte EVENT_FORCE_APPLY = 1;
	public static final byte EVENT_FORCE_CANCEL = 2;
	public static final byte EVENT_FORCE_OK = 3;
	
	private byte eventID = EVENT_INVALID;
	
	public DataInputPanelEvent(Object source) {
		super(source);
	}
	
	public DataInputPanelEvent(Object source, byte anEventID)
	{
		super(source);
		eventID = anEventID;
	}
	
	public byte getEventID()
	{
		return eventID;
	}

}
