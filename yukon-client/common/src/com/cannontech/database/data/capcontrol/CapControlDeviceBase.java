package com.cannontech.database.data.capcontrol;

/**
 * This type was created in VisualAge.
 */
public abstract class CapControlDeviceBase extends com.cannontech.database.data.device.DeviceBase 
{	
/**
 * TwoWayDevice constructor comment.
 */
public CapControlDeviceBase() {
	super();
}
/**
 * This method was created in VisualAge.
 */
public void add() throws java.sql.SQLException {
	super.add();
}
/**
 * This method was created in VisualAge.
 */
public void delete() throws java.sql.SQLException{
	super.delete();
}
/**
 * This method was created in VisualAge.
 */
public void retrieve() throws java.sql.SQLException{
	super.retrieve();
}
/**
 * Insert the method's description here.
 * Creation date: (1/4/00 3:32:03 PM)
 * @param conn java.sql.Connection
 */
public void setDbConnection(java.sql.Connection conn) {
	super.setDbConnection(conn);
}
/**
 * This method was created in VisualAge.
 * @param deviceID java.lang.Integer
 */
public void setDeviceID(Integer deviceID) {
	super.setDeviceID(deviceID);
}
/**
 * This method was created in VisualAge.
 */
public void update() throws java.sql.SQLException{
	super.update();
}
}
