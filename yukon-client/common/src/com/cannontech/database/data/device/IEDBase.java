package com.cannontech.database.data.device;

import com.cannontech.database.db.device.*;
/**
 * This type was created in VisualAge.
 */
public class IEDBase extends RemoteBase 
{
	private DeviceIED deviceIED = null;

	//static strings for the SLAVE ADDRESS field
	public static final String SLAVE_STAND_ALONE = "Standalone";
	public static final String SLAVE_MASTER = "Master";
	public static final String SLAVE_SLAVE1 = "Slave1";
	public static final String SLAVE_SLAVE2 = "Slave2";
	public static final String SLAVE_SLAVE3 = "Slave3";
	public static final String SLAVE_SLAVE4 = "Slave4";
	
/**
 * IEDMeter constructor comment.
 */
public IEDBase() {
	super();
}
/**
 * This method was created in VisualAge.
 */
public void add() throws java.sql.SQLException {
	super.add();
	getDeviceIED().add();
}
/**
 * Insert the method's description here.
 * Creation date: (6/15/2001 9:44:28 AM)
 * @param deviceID int
 * @exception java.sql.SQLException The exception description.
 */
public void addPartial() throws java.sql.SQLException {
	super.addPartial();
	getDeviceIEDDefaults().add();
	
	
	}
/**
 * This method was created in VisualAge.
 */
public void delete() throws java.sql.SQLException {
	getDeviceIED().delete();
	super.delete();
}
/**
 * Insert the method's description here.
 * Creation date: (6/14/2001 11:32:35 AM)
 * @exception java.sql.SQLException The exception description.
 */
public void deletePartial() throws java.sql.SQLException {

	super.deletePartial();

}
/**
 * This method was created in VisualAge.
 * @return com.cannontech.database.db.device.DeviceIED
 */
public DeviceIED getDeviceIED() {
	if( deviceIED == null )
		deviceIED = new DeviceIED();
		
	return deviceIED;
}
/**
 * Insert the method's description here.
 * Creation date: (6/18/2001 11:26:36 AM)
 * @return com.cannontech.database.db.device.DeviceIED
 */
public DeviceIED getDeviceIEDDefaults()
{

	getDeviceIED().setPassword("0");
	getDeviceIED().setSlaveAddress(IEDBase.SLAVE_STAND_ALONE);

	return getDeviceIED();
}
/**
 * This method was created in VisualAge.
 */
public void retrieve() throws java.sql.SQLException {
	super.retrieve();
	getDeviceIED().retrieve();
}
/**
 * Insert the method's description here.
 * Creation date: (1/4/00 3:32:03 PM)
 * @param conn java.sql.Connection
 */
public void setDbConnection(java.sql.Connection conn)
{
	super.setDbConnection(conn);

	getDeviceIED().setDbConnection(conn);
}
/**
 * This method was created in VisualAge.
 * @param deviceID java.lang.Integer
 */
public void setDeviceID(Integer deviceID) {
	super.setDeviceID(deviceID);
	getDeviceIED().setDeviceID(deviceID);
}
/**
 * This method was created in VisualAge.
 * @param newValue com.cannontech.database.db.device.DeviceIED
 */
public void setDeviceIED(DeviceIED newValue) {
	this.deviceIED = newValue;
}
/**
 * This method was created in VisualAge.
 */
public void update() throws java.sql.SQLException {
	super.update();
	getDeviceIED().update();
}
}
