package com.cannontech.tdc.roweditor;

/**
 * Insert the type's description here.
 * Creation date: (3/12/00 4:15:01 PM)
 * @author: 
 */
public class ObservedPointDataChange 
{
	private int type = -1;
	private String value = null;
	private long pointID = -1;
	private boolean rowAlarmed = false;
	private int tags = -1;

	public final static int POINT_VALUE_TYPE = 1;
	public final static int POINT_QUALITY_TYPE = 2;
	public final static int POINT_TIMESTAMP_TYPE = 3;
	public final static int POINT_TAG_TYPE = 4;
	public final static int POINT_STATE = 5;
	
/**
 * ObservedChange constructor comment.
 */
public ObservedPointDataChange() {
	super();
}
/**
 * ObservedChange constructor comment.
 */
public ObservedPointDataChange( String msg, int changeType, long ptID, boolean alarming, int tags_ ) 
{
	super();

	rowAlarmed = alarming;
	pointID = ptID;
	type = changeType;
	value = msg;	
	tags = tags_;
}
/**
 * Insert the method's description here.
 * Creation date: (3/12/00 4:18:37 PM)
 * @return int
 */
public long getPointID() 
{
	return pointID;
}
/**
 * Insert the method's description here.
 * Creation date: (3/12/00 4:18:37 PM)
 * @return int
 */
public int getType() 
{
	return type;
}
/**
 * Insert the method's description here.
 * Creation date: (3/12/00 4:18:37 PM)
 * @return int
 */
public String getValue() 
{
	return value;
}
/**
 * Insert the method's description here.
 * Creation date: (3/30/00 10:09:48 AM)
 * Version: <version>
 * @return boolean
 */
public boolean isAlarming() 
{
	return rowAlarmed;
}

	/**
	 * Returns the tags.
	 * @return int
	 */
	public int getTags()
	{
		return tags;
	}

}
