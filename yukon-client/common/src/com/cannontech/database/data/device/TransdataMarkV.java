package com.cannontech.database.data.device;

import com.cannontech.database.data.multi.SmartMultiDBPersistent;
import com.cannontech.database.data.point.PointFactory;
import com.cannontech.database.data.point.PointTypes;


/**
 * This type was created in VisualAge.
 */
public class TransdataMarkV extends IEDMeter {
/**
 * Alpha constructor comment.
 */
public TransdataMarkV() {
	super();
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
