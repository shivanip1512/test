package com.cannontech.database.data.device;

import com.cannontech.database.data.pao.DeviceClasses;

/**
 * This type was created in VisualAge.
 */
public class RTUBase extends IDLCBase {
/**
 * RTU constructor comment.
 */
public RTUBase() {
	super();
	setDeviceClass( DeviceClasses.STRING_CLASS_RTU );
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
public void setDeviceID(Integer deviceID) {
	super.setDeviceID(deviceID);
}
}
