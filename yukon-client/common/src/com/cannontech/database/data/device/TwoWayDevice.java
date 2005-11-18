package com.cannontech.database.data.device;

import java.util.HashMap;
import java.util.Iterator;

import com.cannontech.database.db.DBPersistent;
import com.cannontech.database.db.device.DeviceScanRate;
import com.cannontech.database.db.device.DeviceTwoWayFlags;
import com.cannontech.database.db.device.DeviceWindow;

/**
 * This type was created in VisualAge.
 */
public class TwoWayDevice extends DeviceBase 
{

	private DeviceTwoWayFlags deviceTwoWayFlags = null;
	private DeviceWindow deviceWindow = null;

	//contains <String:ScanType, DeviceScanRate>
	private HashMap deviceScanRateMap = null;

/**
 * TwoWayDevice constructor comment.
 */
public TwoWayDevice() {
	super();
}
/**
 * This method was created in VisualAge.
 */
public void add() throws java.sql.SQLException {
	super.add();
	getDeviceTwoWayFlags().add();

/*	if( getDeviceStatisticsVector() != null )
		for( int i = 0; i < getDeviceStatisticsVector().size(); i++ )
			((DBPersistent) getDeviceStatisticsVector().elementAt(i)).add();
*/

	Iterator it = getDeviceScanRateMap().values().iterator();
	while( it.hasNext() )
		((DBPersistent)it.next()).add();

	getDeviceWindow().add();
}
/**
 * Insert the method's description here.
 * Creation date: (6/15/2001 9:29:41 AM)
 * @param deviceID int
 * @exception java.sql.SQLException The exception description.
 */
public void addPartial() throws java.sql.SQLException {

    super.addPartial();

    getDeviceTwoWayFlagsDefaults().add();

}
/**
 * This method was created in VisualAge.
 */
public void delete() throws java.sql.SQLException {

	if (!isPartialDelete)
	{
		//DeviceStatistics.deleteDeviceStatistics(getDevice().getDeviceID());
		DeviceScanRate.deleteDeviceScanRates( getDevice().getDeviceID(), getDbConnection() );

		getDeviceWindow().delete();
	}
	
	getDeviceTwoWayFlags().delete();
	super.delete();
}
/**
 * Insert the method's description here.
 * Creation date: (6/14/2001 11:13:30 AM)
 * @exception java.sql.SQLException The exception description.
 */
public void deletePartial() throws java.sql.SQLException {

	super.deletePartial();

}
/**
 * This method was created in VisualAge.
 * @return java.util.Vector
 */
public HashMap getDeviceScanRateMap() {
	if( deviceScanRateMap == null )
		deviceScanRateMap = new HashMap();
	return deviceScanRateMap;
}
/**
 * This method was created in VisualAge.
 * @return com.cannontech.database.db.device.DeviceTwoWayFlags
 */
public DeviceTwoWayFlags getDeviceTwoWayFlags() {
	if ( deviceTwoWayFlags == null )
		deviceTwoWayFlags = new DeviceTwoWayFlags();
		
	return deviceTwoWayFlags;
}
/**
 * This method was created in VisualAge.
 * @return com.cannontech.database.db.device.DeviceTwoWayFlags
 */
public DeviceTwoWayFlags getDeviceTwoWayFlagsDefaults()
{

	getDeviceTwoWayFlags().setMonthlyStats(new Character('N'));
	getDeviceTwoWayFlags().setTwentyFourHourStats(new Character('N'));
	getDeviceTwoWayFlags().setHourlyStats(new Character('N'));
	getDeviceTwoWayFlags().setFailureAlarm(new Character('N'));
	getDeviceTwoWayFlags().setPerformThreshold(new Integer(0));
	getDeviceTwoWayFlags().setPerformAlarm(new Character('N'));
	getDeviceTwoWayFlags().setPerformTwentyFourAlarm(new Character('N'));

	return getDeviceTwoWayFlags();
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
public void retrieve() throws java.sql.SQLException{
	super.retrieve();

	getDeviceTwoWayFlags().retrieve();
	getDeviceWindow().retrieve();

	try
	{
		com.cannontech.database.db.device.DeviceScanRate rArray[] = 
            com.cannontech.database.db.device.DeviceScanRate.getDeviceScanRates( 
                  getDevice().getDeviceID(), getDbConnection() );

		for( int i = 0; i < rArray.length; i++ )
			getDeviceScanRateMap().put( rArray[i].getScanType(), rArray[i] );

		Iterator it = getDeviceScanRateMap().values().iterator();
		while( it.hasNext() ) {
			DBPersistent o = (DBPersistent)it.next();
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
public void setDbConnection(java.sql.Connection conn) 
{
	super.setDbConnection(conn);

	getDeviceTwoWayFlags().setDbConnection(conn);
	getDeviceWindow().setDbConnection(conn);


	Iterator it = getDeviceScanRateMap().values().iterator();
	while( it.hasNext() )
		((DBPersistent)it.next()).setDbConnection(conn);
}
/**
 * This method was created in VisualAge.
 * @param deviceID java.lang.Integer
 */
public void setDeviceID(Integer deviceID) 
{
	super.setDeviceID(deviceID);
	
	getDeviceTwoWayFlags().setDeviceID(deviceID);
	getDeviceWindow().setDeviceID(deviceID);

	Iterator it = getDeviceScanRateMap().values().iterator();
	while( it.hasNext() )
		((DeviceScanRate)it.next()).setDeviceID(deviceID);
}
/**
 * Ensure this guy is never null
 */
public void setDeviceScanRateMap(HashMap newValue) {
	
	if( newValue == null )
		newValue = new HashMap();

	this.deviceScanRateMap = newValue;
}

/**
 * This method was created in VisualAge.
 * @param newValue com.cannontech.database.db.device.DeviceTwoWayFlags
 */
public void setDeviceTwoWayFlags(DeviceTwoWayFlags newValue) {
	this.deviceTwoWayFlags = newValue;
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
public void update() throws java.sql.SQLException
{
	super.update();
	
	getDeviceTwoWayFlags().update();
	getDeviceWindow().update();

	DeviceScanRate.deleteDeviceScanRates( getDevice().getDeviceID(), getDbConnection() );

	Iterator it = getDeviceScanRateMap().values().iterator();
	while( it.hasNext() )
		((DBPersistent)it.next()).add();
}
}
