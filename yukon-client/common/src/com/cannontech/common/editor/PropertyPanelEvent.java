package com.cannontech.common.editor;

/**
 * This type was created in VisualAge.
 */
public class PropertyPanelEvent extends java.util.EventObject {

	public static final int EVENT_INVALID			= -1;
	public static final int EVENT_FORCE_APPLY		= 1;
	public static final int EVENT_FORCE_CANCEL	= 2;
	public static final int EVENT_FORCE_OK			= 3;

	public static final int EVENT_DB_INSERT		= 4;
	public static final int EVENT_DB_DELETE		= 5;
	public static final int EVENT_DB_UPDATE		= 6;


	public static final int OK_SELECTION = 20000;
	public static final int CANCEL_SELECTION = 20001;
	public static final int APPLY_SELECTION = 20002;

	private int id;
	private Object dataChanged = null;
	
	
	/**
	 * PropertyPanelEvent constructor comment.
	 * @param source java.lang.Object
	 */
	public PropertyPanelEvent(Object source) 
	{
		super(source);
	}
		
	public PropertyPanelEvent(Object source, int id) 
	{
		this(source);
		this.id = id;
	}

	public PropertyPanelEvent( Object source, int anEventID, Object data )
	{
		this( source, anEventID );
		dataChanged = data;
	}
	
	/**
	 * This method was created in VisualAge.
	 * @return int
	 */
	public int getID() {
		return id;
	}

	public Object getDataChanged()
	{
		return dataChanged;
	}
}
