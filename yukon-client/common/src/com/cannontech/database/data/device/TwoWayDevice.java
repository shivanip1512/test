package com.cannontech.database.data.device;

import java.util.Vector;

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
	//private Vector deviceStatisticsVector = null;
	private Vector deviceScanRateVector = null;
	private DeviceWindow deviceWindow = null;
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

	if( getDeviceScanRateVector() != null )
		for( int i = 0; i < getDeviceScanRateVector().size(); i++ )
			((DBPersistent) getDeviceScanRateVector().elementAt(i)).add();

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
public Vector getDeviceScanRateVector() {
	if( deviceScanRateVector == null )
		deviceScanRateVector = new Vector();
	return deviceScanRateVector;
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

	deviceScanRateVector = new java.util.Vector();
	try
	{
		com.cannontech.database.db.device.DeviceScanRate rArray[] = 
            com.cannontech.database.db.device.DeviceScanRate.getDeviceScanRates( 
                  getDevice().getDeviceID(), getDbConnection() );

		for( int i = 0; i < rArray.length; i++ )
		{
			rArray[i].setDbConnection(getDbConnection());
			deviceScanRateVector.addElement( rArray[i] );
		}

		for( int j = 0; j < getDeviceScanRateVector().size(); j++ )
		{	
			DBPersistent o = ((DBPersistent) getDeviceScanRateVector().elementAt(j));
			o.setDbConnection( getDbConnection() );
			o.retrieve();
			o.setDbConnection(null);
		}
	}
	catch(java.sql.SQLException e )
	{
		com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
	}


/*	deviceStatisticsVector = new java.util.Vector();
	try
	{
		com.cannontech.database.db.device.DeviceStatistics rArray[] = com.cannontech.database.db.device.DeviceStatistics.getDeviceStatistics( getDevice().getDeviceID(), getDbConnection().toString() );
		for( int i = 0; i < rArray.length; i++ )
		{
			rArray[i].setDbConnection(getDbConnection());
			deviceStatisticsVector.addElement( rArray[i] );
		}

		for( int j = 0; j < getDeviceStatisticsVector().size(); j++ )
		{	
			DBPersistent o = ((DBPersistent) getDeviceStatisticsVector().elementAt(j));
			o.setDbConnection( getDbConnection() );
			o.retrieve();
			o.setDbConnection(null);
		}
	}
	catch(java.sql.SQLException e )
	{
		com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
	}
*/

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

/*	Vector v = getDeviceStatisticsVector();
	if( v != null )
		for( int i = 0; i < v.size(); i++ )
			((DBPersistent) v.elementAt(i)).setDbConnection(conn);
*/
	Vector v2 = getDeviceScanRateVector();

	if( v2 != null )
	{
		for( int i = 0; i < v2.size(); i++ )
			((DBPersistent) v2.elementAt(i)).setDbConnection(conn);
	}
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

	if( getDeviceScanRateVector() != null )
		for( int i = 0; i < getDeviceScanRateVector().size(); i++ )
			((DeviceScanRate) getDeviceScanRateVector().elementAt(i)).setDeviceID(deviceID);
}
/**
 * This method was created in VisualAge.
 * @param newValue java.util.Vector
 */
public void setDeviceScanRateVector(Vector newValue) {
	this.deviceScanRateVector = newValue;
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

/*	if( getDeviceStatisticsVector() != null )
		for( int i = 0; i < getDeviceStatisticsVector().size(); i++ )
			((DBPersistent) getDeviceStatisticsVector().elementAt(i)).update();
*/

	DeviceScanRate.deleteDeviceScanRates( getDevice().getDeviceID(), getDbConnection() );

	if( getDeviceScanRateVector() != null )
		for( int i = 0; i < getDeviceScanRateVector().size(); i++ )
			((DBPersistent) getDeviceScanRateVector().elementAt(i)).add();
}
}
