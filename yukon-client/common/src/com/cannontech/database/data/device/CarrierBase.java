package com.cannontech.database.data.device;

import java.util.Vector;

import com.cannontech.database.db.device.*;


/**
 * This type was created in VisualAge.
 */
public class CarrierBase extends TwoWayDevice {

	private DeviceRoutes deviceRoutes = null;

	private DeviceCarrierSettings deviceCarrierSettings = null;
/**
 * CarrierBase constructor comment.
 */
public CarrierBase() {
	super();
}
/**
 * This method was created in VisualAge.
 */
public void add() throws java.sql.SQLException {
	super.add();

	getDeviceCarrierSettings().add();
	getDeviceRoutes().add();
}
/**
 * Insert the method's description here.
 * Creation date: (6/15/2001 9:46:43 AM)
 * @param deviceID int
 * @exception java.sql.SQLException The exception description.
 */
public void addPartial() throws java.sql.SQLException {
	super.addPartial();

	getDeviceCarrierSettings().add();
	getDeviceRoutes().add();
	
	
	
	}
/**
 * This method was created in VisualAge.
 */
public void delete() throws java.sql.SQLException {

	getDeviceRoutes().delete();
	getDeviceCarrierSettings().delete();
	super.delete();
}
/**
 * Insert the method's description here.
 * Creation date: (6/14/2001 11:11:50 AM)
 * @exception java.sql.SQLException The exception description.
 */
public void deletePartial() throws java.sql.SQLException {

	super.deletePartial();

}
/**
 * This method was created in VisualAge.
 * @return com.cannontech.database.db.device.DeviceCarrierSettings
 */
public DeviceCarrierSettings getDeviceCarrierSettings() {
 	if( deviceCarrierSettings == null )
 		deviceCarrierSettings = new DeviceCarrierSettings();
 		
	return deviceCarrierSettings;
}
/**
 * This method was created in VisualAge.
 * @return java.util.Vector
 */
public DeviceRoutes getDeviceRoutes() {

	if( deviceRoutes == null )
		deviceRoutes = new DeviceRoutes();
	
	return deviceRoutes;
}
/**
 * This method was created in VisualAge.
 */
public void retrieve() throws java.sql.SQLException {

	super.retrieve();
	getDeviceCarrierSettings().retrieve();
	getDeviceRoutes().retrieve();	
}
/**
 * Insert the method's description here.
 * Creation date: (1/4/00 3:30:12 PM)
 * @param conn java.sql.Connection
 */
public void setDbConnection(java.sql.Connection conn) 
{
	super.setDbConnection(conn);
	getDeviceCarrierSettings().setDbConnection(conn);
	getDeviceRoutes().setDbConnection(conn);
}
/**
 * This method was created in VisualAge.
 * @param newValue com.cannontech.database.db.device.DeviceCarrierSettings
 */
public void setDeviceCarrierSettings(DeviceCarrierSettings newValue) {
	this.deviceCarrierSettings = newValue;
}
/**
 * This method was created in VisualAge.
 * @param deviceID java.lang.Integer
 */
public void setDeviceID(Integer deviceID) {
	super.setDeviceID(deviceID);
	getDeviceCarrierSettings().setDeviceID(deviceID);
	getDeviceRoutes().setDeviceID(deviceID);
}
/**
 * This method was created in VisualAge.
 * @param newValue java.util.Vector
 */
public void setDeviceRoutes(DeviceRoutes newValue) {
	this.deviceRoutes = newValue;
}
/**
 * This method was created in VisualAge.
 */
public void update() throws java.sql.SQLException{
	super.update();

	getDeviceCarrierSettings().update();
	getDeviceRoutes().update();
}
}
