package com.cannontech.database.data.device;

import com.cannontech.common.pao.PaoType;

/**
 * This type was created in VisualAge.
 */
public class MCTIEDBase extends MCTBase {
	private com.cannontech.database.db.device.DeviceMCTIEDPort deviceMCTIEDPort = null;
/**
 * MCT360 constructor comment.
 */
public MCTIEDBase(PaoType paoType) {
	super(paoType);
}
/**
 * This method was created in VisualAge.
 */
@Override
public void add() throws java.sql.SQLException {
	super.add();
	getDeviceMCTIEDPort().add();
}
/**
 * Insert the method's description here.
 * Creation date: (6/15/2001 9:39:53 AM)
 * @param deviceID int
 * @exception java.sql.SQLException The exception description.
 */
@Override
public void addPartial() throws java.sql.SQLException {
	super.addPartial();
	getDeviceMCTIEDPortDefaults().add();
	
	
	}
/**
 * This method was created in VisualAge.
 */
@Override
public void delete() throws java.sql.SQLException {
	getDeviceMCTIEDPort().delete();
	super.delete();
}
/**
 * Insert the method's description here.
 * Creation date: (6/14/2001 11:02:35 AM)
 */
@Override
public void deletePartial() throws java.sql.SQLException {

	super.deletePartial();
}
/**
 * This method was created in VisualAge.
 * @return com.cannontech.database.db.device.DeviceMeterGroup
 */
public com.cannontech.database.db.device.DeviceMCTIEDPort getDeviceMCTIEDPort() {
	if( deviceMCTIEDPort == null )
		deviceMCTIEDPort = new com.cannontech.database.db.device.DeviceMCTIEDPort();
		
	return deviceMCTIEDPort;
}
/**
 * Insert the method's description here.
 * Creation date: (6/18/2001 11:50:33 AM)
 * @return com.cannontech.database.db.device.DeviceMCTIEDPort
 */
public com.cannontech.database.db.device.DeviceMCTIEDPort getDeviceMCTIEDPortDefaults()
{

	getDeviceMCTIEDPort().setConnectedIED("None");
	getDeviceMCTIEDPort().setIEDScanRate(new Integer(60));
	getDeviceMCTIEDPort().setDefaultDataClass(new Integer(0));
	getDeviceMCTIEDPort().setDefaultDataOffset(new Integer(0));
	getDeviceMCTIEDPort().setPassword("None");
	getDeviceMCTIEDPort().setRealTimeScan(new Character('N'));

	return getDeviceMCTIEDPort();
}
/**
 * This method was created in VisualAge.
 */
@Override
public void retrieve() throws java.sql.SQLException {
	super.retrieve();
	getDeviceMCTIEDPort().retrieve();
}
/**
 * Insert the method's description here.
 * Creation date: (1/4/00 3:32:03 PM)
 * @param conn java.sql.Connection
 */
@Override
public void setDbConnection(java.sql.Connection conn) 
{
	super.setDbConnection(conn);
	getDeviceMCTIEDPort().setDbConnection(conn);
}
/**
 * This method was created in VisualAge.
 * @param deviceID java.lang.Integer
 */
@Override
public void setDeviceID(Integer deviceID) {
	super.setDeviceID(deviceID);
	getDeviceMCTIEDPort().setDeviceID(deviceID);
}
/**
 * This method was created in VisualAge.
 */
@Override
public void update() throws java.sql.SQLException {
	super.update();
	getDeviceMCTIEDPort().update();
}
}
