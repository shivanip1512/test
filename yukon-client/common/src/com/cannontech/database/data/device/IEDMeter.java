package com.cannontech.database.data.device;

import com.cannontech.database.db.device.DeviceLoadProfile;
import com.cannontech.database.db.device.DeviceMeterGroup;
/**
 * This type was created in VisualAge.
 */
public class IEDMeter extends IEDBase 
{
	private DeviceMeterGroup deviceMeterGroup = null;
	private DeviceLoadProfile deviceLoadProfile = null;
/**
 * IEDMeter constructor comment.
 */
public IEDMeter() {
	super();
}
/**
 * This method was created in VisualAge.
 */
public void add() throws java.sql.SQLException 
{
	super.add();
	getDeviceMeterGroup().add();
	getDeviceLoadProfile().add();
}
/**
 * Insert the method's description here.
 * Creation date: (6/15/2001 9:43:18 AM)
 * @param deviceID int
 * @exception java.sql.SQLException The exception description.
 */
public void addPartial() throws java.sql.SQLException
{
	super.addPartial();
	//getDeviceMeterGroupDefaults().add();
	getDeviceMeterGroup().add();
	getDeviceLoadProfileDefaults().add();
}
/**
 * This method was created in VisualAge.
 */
public void delete() throws java.sql.SQLException 
{
	getDeviceMeterGroup().delete();
	getDeviceLoadProfile().delete();
	super.delete();
}
/**
 * Insert the method's description here.
 * Creation date: (6/14/2001 11:31:41 AM)
 * @exception java.sql.SQLException The exception description.
 */
public void deletePartial() throws java.sql.SQLException {

	super.deletePartial();

}
/**
 * This method was created in VisualAge.
 * @return com.cannontech.database.db.device.DeviceCarrierSettings
 */
public DeviceLoadProfile getDeviceLoadProfile() 
{
 	if( deviceLoadProfile == null )
 		deviceLoadProfile = new DeviceLoadProfile();
 		
	return deviceLoadProfile;
}
/**
 * Insert the method's description here.
 * Creation date: (6/18/2001 11:35:05 AM)
 * @return com.cannontech.database.db.device.DeviceLoadProfile
 */
public DeviceLoadProfile getDeviceLoadProfileDefaults()
{

	getDeviceLoadProfile().setLastIntervalDemandRate(new Integer(0));
	getDeviceLoadProfile().setLoadProfileDemandRate(new Integer(0));
	getDeviceLoadProfile().setLoadProfileCollection("NNNN");

	return getDeviceLoadProfile();
}
/**
 * This method was created in VisualAge.
 * @return com.cannontech.database.db.device.DeviceMeterGroup
 */
public DeviceMeterGroup getDeviceMeterGroup() {
	if( deviceMeterGroup == null )
		deviceMeterGroup = new DeviceMeterGroup();
		
	return deviceMeterGroup;
}
/**
 * This method was created in VisualAge.
 */
public void retrieve() throws java.sql.SQLException 
{
	super.retrieve();
	getDeviceMeterGroup().retrieve();
	getDeviceLoadProfile().retrieve();
}
/**
 * Insert the method's description here.
 * Creation date: (1/4/00 3:32:03 PM)
 * @param conn java.sql.Connection
 */
public void setDbConnection(java.sql.Connection conn)
{
	super.setDbConnection(conn);

	getDeviceMeterGroup().setDbConnection(conn);
	getDeviceLoadProfile().setDbConnection(conn);
}
/**
 * This method was created in VisualAge.
 * @param deviceID java.lang.Integer
 */
public void setDeviceID(Integer deviceID) 
{
	super.setDeviceID(deviceID);
	getDeviceMeterGroup().setDeviceID(deviceID);
	getDeviceLoadProfile().setDeviceID(deviceID);
}
/**
 * Insert the method's description here.
 * Creation date: (6/15/2001 9:55:31 AM)
 * @param x com.cannontech.database.db.device.DeviceLoadProfile
 */
public void setDeviceLoadProfile(DeviceLoadProfile newValue) {
	deviceLoadProfile = newValue;
	
	}
/**
 * This method was created in VisualAge.
 * @param newValue com.cannontech.database.db.device.DeviceMeterGroup
 */
public void setDeviceMeterGroup(DeviceMeterGroup newValue) {
	this.deviceMeterGroup = newValue;
}
/**
 * This method was created in VisualAge.
 */
public void update() throws java.sql.SQLException 
{
	super.update();
	getDeviceMeterGroup().update();
	getDeviceLoadProfile().update();
}
}
