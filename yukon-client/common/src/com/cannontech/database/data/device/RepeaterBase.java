package com.cannontech.database.data.device;

import com.cannontech.database.db.device.*;
/**
 * This type was created in VisualAge.
 */
public class RepeaterBase extends CarrierBase {
/**
 * Repeater constructor comment.
 */
public RepeaterBase() {
	super();
}
/**
 * This method was created in VisualAge.
 */
public void add() throws java.sql.SQLException {
	super.add();
}
/**
 * Insert the method's description here.
 * Creation date: (6/15/2001 9:36:12 AM)
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
 * Creation date: (6/15/2001 9:35:21 AM)
 * @exception java.sql.SQLException The exception description.
 */
public void deletePartial() throws java.sql.SQLException {
	super.deletePartial();
	
	}
/**
 * This method was created in VisualAge.
 */
public void retrieve() throws java.sql.SQLException {
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

	getDeviceCarrierSettings().setDbConnection(conn);
}
/**
 * This method was created in VisualAge.
 * @param deviceID java.lang.Integer
 */
public void setDeviceID(Integer deviceID) {
	super.setDeviceID(deviceID);
	getDeviceCarrierSettings().setDeviceID(deviceID);
}
/**
 * This method was created in VisualAge.
 */
public void update() throws java.sql.SQLException {
	super.update();
}
}
