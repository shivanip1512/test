package com.cannontech.tdc.roweditor;

/**
 * Insert the type's description here.
 * Creation date: (1/9/2001 3:30:47 PM)
 * @author: 
 */
public class EditorDialogData 
{
	private int pointID = -1;
	private int deviceID = -1;
	private String deviceName = null;
	private String pointName = null;
	private long tags = 0;
	private String[] allStates = null;  // for status points
	private int pointType = com.cannontech.database.data.point.PointTypes.SYS_PID_SYSTEM;
/**
 * EditorDialogData constructor comment.
 */
public EditorDialogData() {
	super();
}
/**
 * EditorDialogData constructor comment.
 */
public EditorDialogData(com.cannontech.tdc.PointValues pt, String[] allStates)
{
	super();

	setPointID( (int)pt.getPointData().getId() );
	setDeviceID( pt.getDeviceID() );
	setDeviceName( pt.getDeviceName().toString() );
	setPointName( pt.getPointName() );
	setTags( pt.getPointData().getTags() );
	setAllStates(allStates);
	setPointType( pt.getPointData().getType() );
}
/**
 * Insert the method's description here.
 * Creation date: (1/9/2001 3:35:58 PM)
 * @return java.lang.String[]
 */
public java.lang.String[] getAllStates() {
	return allStates;
}
/**
 * Insert the method's description here.
 * Creation date: (1/9/2001 3:35:58 PM)
 * @return int
 */
public int getDeviceID() {
	return deviceID;
}
/**
 * Insert the method's description here.
 * Creation date: (1/9/2001 3:35:58 PM)
 * @return java.lang.String
 */
public java.lang.String getDeviceName() {
	return deviceName;
}
/**
 * Insert the method's description here.
 * Creation date: (1/9/2001 3:35:58 PM)
 * @return int
 */
public int getPointID() {
	return pointID;
}
/**
 * Insert the method's description here.
 * Creation date: (1/9/2001 3:35:58 PM)
 * @return java.lang.String
 */
public java.lang.String getPointName() {
	return pointName;
}
/**
 * Insert the method's description here.
 * Creation date: (7/23/2001 4:12:56 PM)
 * @return int
 */
public int getPointType() {
	return pointType;
}
public long getTags() {
	return tags;
}/**
 * Insert the method's description here.
 * Creation date: (1/9/2001 3:35:58 PM)
 * @param newTags int
 */
/**
 * Insert the method's description here.
 * Creation date: (1/9/2001 3:35:58 PM)
 * @param newAllStates java.lang.String[]
 */
public void setAllStates(java.lang.String[] newAllStates) {
	allStates = newAllStates;
}
/**
 * Insert the method's description here.
 * Creation date: (1/9/2001 3:35:58 PM)
 * @param newDeviceID int
 */
public void setDeviceID(int newDeviceID) {
	deviceID = newDeviceID;
}
/**
 * Insert the method's description here.
 * Creation date: (1/9/2001 3:35:58 PM)
 * @param newDeviceName java.lang.String
 */
public void setDeviceName(java.lang.String newDeviceName) {
	deviceName = newDeviceName;
}
/**
 * Insert the method's description here.
 * Creation date: (1/9/2001 3:35:58 PM)
 * @param newPointID int
 */
public void setPointID(int newPointID) {
	pointID = newPointID;
}
/**
 * Insert the method's description here.
 * Creation date: (1/9/2001 3:35:58 PM)
 * @param newPointName java.lang.String
 */
public void setPointName(java.lang.String newPointName) {
	pointName = newPointName;
}
/**
 * Insert the method's description here.
 * Creation date: (7/23/2001 4:12:56 PM)
 * @param newPointType int
 */
public void setPointType(int newPointType) {
	pointType = newPointType;
}
/**
 * Insert the method's description here.
 * Creation date: (1/9/2001 3:35:58 PM)
 * @param newTags int
 */
public void setTags(int newTags) {
	tags = newTags;
}
public void setTags(long newTags) {
	tags = newTags;
}
}
