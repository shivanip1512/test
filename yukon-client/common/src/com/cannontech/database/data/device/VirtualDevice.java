package com.cannontech.database.data.device;

import com.cannontech.database.data.pao.DeviceClasses;

/**
 * Insert the type's description here.
 * Creation date: (10/4/00 2:56:37 PM)
 * @author: 
 */
public class VirtualDevice extends DeviceBase {
/**
 * Virtual constructor comment.
 */
public VirtualDevice() {
	super();
	setDeviceClass( DeviceClasses.STRING_CLASS_VIRTUAL );
}
/**
 * This method was created in VisualAge.
 */
public void add() throws java.sql.SQLException {
	super.add();
}
/**
 * Insert the method's description here.
 * Creation date: (6/15/2001 9:34:07 AM)
 * @param deviceID int
 * @exception java.sql.SQLException The exception description.
 */
public void addPartial() throws java.sql.SQLException {
	super.addPartial();
	
	
	}
/**
 * This method was created in VisualAge.
 */
public void delete() throws java.sql.SQLException{
	super.delete();
}
/**
 * Insert the method's description here.
 * Creation date: (6/15/2001 9:32:25 AM)
 * @exception java.sql.SQLException The exception description.
 */
public void deletePartial() throws java.sql.SQLException {
	super.deletePartial();
	
	
	}
/**
 * This method was created in VisualAge.
 */
public void retrieve() throws java.sql.SQLException
{
	super.retrieve();
}
/**
 * Insert the method's description here.
 * Creation date: (1/4/00 3:32:03 PM)
 * @param conn java.sql.Connection
 */
public void setDbConnection(java.sql.Connection conn) 
{
	super.setDbConnection(conn);
}
/**
 * This method was created in VisualAge.
 * @param deviceID java.lang.Integer
 */
public void setDeviceID(Integer deviceID) 
{
	super.setDeviceID(deviceID);
}
/**
 * This method was created in VisualAge.
 */
public void update() throws java.sql.SQLException
{
	super.update();
}
}
