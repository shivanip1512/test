package com.cannontech.database.data.device;

import com.cannontech.database.db.device.DeviceDialupSettings;
import com.cannontech.database.db.device.DeviceDirectCommSettings;

/**
 * This type was created in VisualAge.
 */
public class RemoteBase extends TwoWayDevice {
	private DeviceDirectCommSettings deviceDirectCommSettings = null;
	private DeviceDialupSettings deviceDialupSettings = null;
/**
 * RemoteBase constructor comment.
 */
public RemoteBase() {
	super();
}
/**
 * This method was created in VisualAge.
 */
public void add() throws java.sql.SQLException {
	super.add();
	getDeviceDialupSettings().add();
	getDeviceDirectCommSettings().add();
}
/**
 * Insert the method's description here.
 * Creation date: (6/15/2001 9:28:26 AM)
 * @param deviceID int
 */
public void addPartial() throws java.sql.SQLException
{
	super.addPartial();

	getDeviceDialupSettingsDefaults().add();
	getDeviceDirectCommSettings().add();
}
/**
 * This method was created in VisualAge.
 */
public void delete() throws java.sql.SQLException {
	getDeviceDialupSettings().delete();
	getDeviceDirectCommSettings().delete();
	super.delete();
}
/**
 * Insert the method's description here.
 * Creation date: (6/14/2001 11:18:49 AM)
 * @exception java.sql.SQLException The exception description.
 */
public void deletePartial() throws java.sql.SQLException {

	super.deletePartial();

}
/**
 * This method was created in VisualAge.
 * @return com.cannontech.database.db.device.DeviceDialupSettings
 */
public DeviceDialupSettings getDeviceDialupSettings() 
{
	if( deviceDialupSettings == null)
		deviceDialupSettings = new DeviceDialupSettings();
		
	return deviceDialupSettings;
}
/**
 * Insert the method's description here.
 * Creation date: (6/18/2001 10:54:04 AM)
 * @return com.cannontech.database.db.device.DeviceDialupSettings
 */
public DeviceDialupSettings getDeviceDialupSettingsDefaults()
{
	if (this instanceof PagingTapTerminal)
		getDeviceDialupSettings().setLineSettings("7E2");
	else
		getDeviceDialupSettings().setLineSettings("8N1");

	return getDeviceDialupSettings();
}
/**
 * This method was created in VisualAge.
 * @return com.cannontech.database.db.device.DeviceDirectCommSettings
 */
public DeviceDirectCommSettings getDeviceDirectCommSettings() {
	if( deviceDirectCommSettings == null )
		deviceDirectCommSettings = new DeviceDirectCommSettings();
		
	return deviceDirectCommSettings;
}
/**
 * This method was created in VisualAge.
 */
public void retrieve() throws java.sql.SQLException {

	super.retrieve();
	getDeviceDialupSettings().retrieve();
	getDeviceDirectCommSettings().retrieve();
}
/**
 * Insert the method's description here.
 * Creation date: (1/4/00 3:32:03 PM)
 * @param conn java.sql.Connection
 */
public void setDbConnection(java.sql.Connection conn) 
{
	super.setDbConnection(conn);

	if( getDeviceDialupSettings() != null )
		getDeviceDialupSettings().setDbConnection(conn);

	getDeviceDirectCommSettings().setDbConnection(conn);
}
/**
 * This method was created in VisualAge.
 * @param newValue com.cannontech.database.db.device.DeviceDialupSettings
 */
public void setDeviceDialupSettings(DeviceDialupSettings newValue) {
	this.deviceDialupSettings = newValue;
}
/**
 * This method was created in VisualAge.
 * @param newValue com.cannontech.database.db.device.DeviceDirectCommSettings
 */
public void setDeviceDirectCommSettings(DeviceDirectCommSettings newValue) {
	this.deviceDirectCommSettings = newValue;
}
/**
 * This method was created in VisualAge.
 * @param deviceID java.lang.Integer
 */
public void setDeviceID( Integer deviceID) {
	super.setDeviceID(deviceID);

	if( getDeviceDialupSettings() != null )
		getDeviceDialupSettings().setDeviceID(deviceID);

	getDeviceDirectCommSettings().setDeviceID( deviceID);
}
/**
 * This method was created in VisualAge.
 */
public void update() throws java.sql.SQLException {
	super.update();
	getDeviceDialupSettings().update();
	getDeviceDirectCommSettings().update();
}

public boolean hasPhoneNumber()
{
	if(getDeviceDialupSettings().getPhoneNumber() == null)
		return false;
	return (!(getDeviceDialupSettings().getPhoneNumber().compareTo("0") == 0 ||getDeviceDialupSettings().getPhoneNumber() == null));

}
}
