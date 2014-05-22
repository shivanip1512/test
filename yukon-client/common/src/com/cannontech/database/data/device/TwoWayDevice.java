package com.cannontech.database.data.device;

import java.util.HashMap;
import java.util.Iterator;

import com.cannontech.common.pao.PaoType;
import com.cannontech.database.db.DBPersistent;
import com.cannontech.database.db.device.DeviceScanRate;
import com.cannontech.database.db.device.DeviceWindow;

/**
 * This type was created in VisualAge.
 */
public class TwoWayDevice extends DeviceBase 
{
	private DeviceWindow deviceWindow = null;

	private HashMap<String, DeviceScanRate> deviceScanRateMap = null;

/**
 * TwoWayDevice constructor comment.
 */
public TwoWayDevice(PaoType paoType) {
	super(paoType);
}
/**
 * This method was created in VisualAge.
 */
@Override
public void add() throws java.sql.SQLException {
	super.add();

	Iterator<DeviceScanRate> it = getDeviceScanRateMap().values().iterator();
	while( it.hasNext() )
		it.next().add();

	getDeviceWindow().add();
}
/**
 * Insert the method's description here.
 * Creation date: (6/15/2001 9:29:41 AM)
 * @param deviceID int
 * @exception java.sql.SQLException The exception description.
 */
@Override
public void addPartial() throws java.sql.SQLException {

    super.addPartial();
}
/**
 * This method was created in VisualAge.
 */
@Override
public void delete() throws java.sql.SQLException {

	if (!isPartialDelete)
	{
		DeviceScanRate.deleteDeviceScanRates( getDevice().getDeviceID(), getDbConnection() );

		getDeviceWindow().delete();
	}
	
	super.delete();
}
/**
 * Insert the method's description here.
 * Creation date: (6/14/2001 11:13:30 AM)
 * @exception java.sql.SQLException The exception description.
 */
@Override
public void deletePartial() throws java.sql.SQLException {

	super.deletePartial();

}
/**
 * This method was created in VisualAge.
 * @return java.util.Vector
 */
public HashMap<String, DeviceScanRate> getDeviceScanRateMap() {
	if( deviceScanRateMap == null )
		deviceScanRateMap = new HashMap<String, DeviceScanRate>();
	return deviceScanRateMap;
}

/**
 * Insert the method's description here.
 * Creation date: (9/27/2001 9:54:28 AM)
 * @return com.cannontech.database.db.device.DeviceScanWindow
 */
public com.cannontech.database.db.device.DeviceWindow getDeviceWindow() 
{
	if( deviceWindow == null )
		deviceWindow = new DeviceWindow();

	return deviceWindow;
}
/**
 * This method was created in VisualAge.
 */
@Override
public void retrieve() throws java.sql.SQLException{
	super.retrieve();

	getDeviceWindow().retrieve();

	try
	{
		com.cannontech.database.db.device.DeviceScanRate rArray[] = 
            com.cannontech.database.db.device.DeviceScanRate.getDeviceScanRates( 
                  getDevice().getDeviceID(), getDbConnection() );

		for( int i = 0; i < rArray.length; i++ )
			getDeviceScanRateMap().put( rArray[i].getScanType(), rArray[i] );

		Iterator<DeviceScanRate> it = getDeviceScanRateMap().values().iterator();
		while( it.hasNext() ) {
			DBPersistent o = it.next();
			o.setDbConnection( getDbConnection() );
			o.retrieve();
			o.setDbConnection(null);
		}

	}
	catch(java.sql.SQLException e )
	{
		com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
	}

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

	getDeviceWindow().setDbConnection(conn);


	Iterator<DeviceScanRate> it = getDeviceScanRateMap().values().iterator();
	while( it.hasNext() )
		it.next().setDbConnection(conn);
}
/**
 * This method was created in VisualAge.
 * @param deviceID java.lang.Integer
 */
@Override
public void setDeviceID(Integer deviceID) 
{
	super.setDeviceID(deviceID);
	
	getDeviceWindow().setDeviceID(deviceID);

	Iterator<DeviceScanRate> it = getDeviceScanRateMap().values().iterator();
	while( it.hasNext() )
		it.next().setDeviceID(deviceID);
}
/**
 * Ensure this guy is never null
 */
public void setDeviceScanRateMap(HashMap<String, DeviceScanRate> newValue) {
	
	if( newValue == null )
		newValue = new HashMap<String, DeviceScanRate>();

	this.deviceScanRateMap = newValue;
}

/**
 * Insert the method's description here.
 * Creation date: (9/27/2001 9:54:28 AM)
 */
public void setDeviceWindow(com.cannontech.database.db.device.DeviceWindow newDeviceWindow) {
	deviceWindow = newDeviceWindow;
}
/**
 * This method was created in VisualAge.
 */
@Override
public void update() throws java.sql.SQLException
{
	super.update();
	
	getDeviceWindow().update();

	DeviceScanRate.deleteDeviceScanRates( getDevice().getDeviceID(), getDbConnection() );

	Iterator<DeviceScanRate> it = getDeviceScanRateMap().values().iterator();
	while( it.hasNext() )
		it.next().add();
}
}
