package com.cannontech.database.data.device.devicemetergroup;

/**
 * Insert the type's description here.
 * Creation date: (9/5/2001 5:36:15 PM)
 * @author: 
 */
public class DeviceMeterGroupBase extends com.cannontech.database.db.DBPersistent
{
	private com.cannontech.database.db.device.DeviceMeterGroup deviceMeterGroup = null;
/**
 * DeviceMeterGroupBase constructor comment.
 */
public DeviceMeterGroupBase() {
	super();
}
/**
 * DeviceMeterGroupBase constructor comment.
 */
public DeviceMeterGroupBase(Integer deviceID)
{
	super();
	getDeviceMeterGroup().setDeviceID( deviceID );
}
/**
 * This method was created in VisualAge.
 * @exception java.sql.SQLException The exception description.
 */
public void add() throws java.sql.SQLException 
{
	getDeviceMeterGroup().add();
}
/**
 * This method was created in VisualAge.
 * @exception java.sql.SQLException The exception description.
 */
public void delete() throws java.sql.SQLException 
{
	getDeviceMeterGroup().delete();
}
/**
 * Insert the method's description here.
 * Creation date: (9/5/2001 5:41:29 PM)
 * @return com.cannontech.database.db.device.DeviceMeterGroup
 */
public com.cannontech.database.db.device.DeviceMeterGroup getDeviceMeterGroup()
{
	if( deviceMeterGroup == null )
		deviceMeterGroup = new com.cannontech.database.db.device.DeviceMeterGroup();
	
	return deviceMeterGroup;
}
/**
 * This method was created in VisualAge.
 * @exception java.sql.SQLException The exception description.
 */
public void retrieve() throws java.sql.SQLException 
{
	getDeviceMeterGroup().retrieve();
}
/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
public String toString() 
{
	return getDeviceMeterGroup().getMeterNumber();
}
/**
 * This method was created in VisualAge.
 * @exception java.sql.SQLException The exception description.
 */
public void update() throws java.sql.SQLException 
{	
	getDeviceMeterGroup().update();	
}
}
