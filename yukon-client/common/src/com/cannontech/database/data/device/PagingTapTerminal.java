package com.cannontech.database.data.device;

import com.cannontech.database.data.pao.DeviceClasses;
import com.cannontech.database.db.device.DeviceTapPagingSettings;

/**
 * This type was created in VisualAge.
 */
public class PagingTapTerminal extends IEDBase {

	private DeviceTapPagingSettings deviceTapPagingSettings = null;
/**
 * TwoWayDevice constructor comment.
 */
public PagingTapTerminal() {
	super();
	setDeviceClass( DeviceClasses.STRING_CLASS_TRANSMITTER );
}
/**
 * This method was created in VisualAge.
 */
public void add() throws java.sql.SQLException {
	super.add();
	getDeviceTapPagingSettings().add();
}
/**
 * Insert the method's description here.
 * Creation date: (6/15/2001 9:38:34 AM)
 * @param deviceID int
 * @exception java.sql.SQLException The exception description.
 */
public void addPartial() throws java.sql.SQLException {
	
	super.addPartial();
	getDeviceTapPagingSettingsDefaults().add();
	
	
	
	}
/**
 * This method was created in VisualAge.
 */
public void delete() throws java.sql.SQLException{
	getDeviceTapPagingSettings().delete();
	super.delete();
}
/**
 * Insert the method's description here.
 * Creation date: (6/15/2001 9:37:10 AM)
 * @exception java.sql.SQLException The exception description.
 */
public void deletePartial() throws java.sql.SQLException {

	super.deletePartial();

}
/**
 * This method was created in VisualAge.
 * @return com.cannontech.database.db.device.DeviceTwoWayFlags
 */
public DeviceTapPagingSettings getDeviceTapPagingSettings() {
	if ( deviceTapPagingSettings == null )
		deviceTapPagingSettings = new DeviceTapPagingSettings();
		
	return deviceTapPagingSettings;
}
/**
 * This method was created in VisualAge.
 * @return com.cannontech.database.db.device.DeviceTwoWayFlags
 */
public DeviceTapPagingSettings getDeviceTapPagingSettingsDefaults()
{

	getDeviceTapPagingSettings().setPagerNumber("None");

	return getDeviceTapPagingSettings();
}
/**
 * This method was created in VisualAge.
 */
public void retrieve() throws java.sql.SQLException{
	super.retrieve();

	getDeviceTapPagingSettings().retrieve();
}
/**
 * Insert the method's description here.
 * Creation date: (1/4/00 3:32:03 PM)
 * @param conn java.sql.Connection
 */
public void setDbConnection(java.sql.Connection conn) 
{
	super.setDbConnection(conn);

	getDeviceTapPagingSettings().setDbConnection(conn);
}
/**
 * This method was created in VisualAge.
 * @param deviceID java.lang.Integer
 */
public void setDeviceID(Integer deviceID) {
	super.setDeviceID(deviceID);
	getDeviceTapPagingSettings().setDeviceID(deviceID);
}
/**
 * This method was created in VisualAge.
 * @param newValue com.cannontech.database.db.device.DeviceTwoWayFlags
 */
public void setDeviceTapPagingSettings(DeviceTapPagingSettings newValue) {
	this.deviceTapPagingSettings = newValue;
}
/**
 * This method was created in VisualAge.
 */
public void update() throws java.sql.SQLException{
	super.update();
	getDeviceTapPagingSettings().update();
}
}
