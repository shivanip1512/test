package com.cannontech.database.db.capcontrol;

import java.util.ArrayList;
import java.util.List;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.PoolManager;
import com.cannontech.database.SqlUtils;

/**
 * This type was created in VisualAge.
 */
public class DeviceCBC extends com.cannontech.database.db.DBPersistent 
{
	private Integer deviceID = null;
	private Integer serialNumber = new Integer(CtiUtilities.NONE_ZERO_ID);
	private Integer routeID = new Integer(CtiUtilities.NONE_ZERO_ID);

	public static String TABLE_NAME = "DeviceCBC";
	public static String DYN_TABLE_NAME = "dynamiccctwowaycbc";
/**
 * Constructor comment.
 */
public DeviceCBC() {
	super();
}
/**
 * DeviceTwoWayFlags constructor comment.
 */
public DeviceCBC(Integer deviceID) {
	this();
	setDeviceID( deviceID );
}

/**
 * This method returns all the DeviceCBCs that are in the system
 * 
 */
public static DeviceCBC[] getAllDeviceCBCs()
{
    List<DeviceCBC> deviceCbcs = new ArrayList<>();
	java.sql.Connection conn = null;
	java.sql.PreparedStatement pstmt = null;
	java.sql.ResultSet rset = null;
	
	String sql = "SELECT DeviceID, SerialNumber, RouteID FROM " + TABLE_NAME +
					" ORDER BY DeviceID";

	try
	{		
		conn = PoolManager.getInstance().getConnection(CtiUtilities.getDatabaseAlias());
	
		if( conn == null )
		{
			throw new IllegalStateException("Error getting database connection.");
		}
		else
		{
			pstmt = conn.prepareStatement(sql.toString());
				
			rset = pstmt.executeQuery();
		
			while( rset.next() ) {
				DeviceCBC devCBC = new DeviceCBC( new Integer(rset.getInt(1)) );
				devCBC.setSerialNumber( new Integer(rset.getInt(2)) );
				devCBC.setRouteID( new Integer(rset.getInt(3)) );
				
				deviceCbcs.add( devCBC );
			}

		}		
	}
	catch( java.sql.SQLException e ) {
		CTILogger.error( e.getMessage(), e );
	}
	finally {
		SqlUtils.close(rset, pstmt, conn );
	}
	
	
	DeviceCBC[] cbcs = new DeviceCBC[deviceCbcs.size()];
	return deviceCbcs.toArray( cbcs );
}

/**
 * add method comment.
 */
public final static String usedCapBankController(Integer controllerID)
{
  com.cannontech.database.SqlStatement stmt =
     new com.cannontech.database.SqlStatement(
              "SELECT PAOName FROM " + 
      com.cannontech.database.db.pao.YukonPAObject.TABLE_NAME + " y, " +
      com.cannontech.database.db.capcontrol.CapBank.TABLE_NAME + " c" +
      " WHERE y.PAObjectID = c.DeviceID" +
      " and c.ControlDeviceID =" + controllerID,
                  com.cannontech.common.util.CtiUtilities.getDatabaseAlias() );
   
      try
      {
         stmt.execute();
         if( stmt.getRowCount() > 0 )
            return stmt.getRow(0)[0].toString();
         else
            return null;
      }
      catch( Exception e )
      {
         return null;
      }
}

public void add() throws java.sql.SQLException {
	Object[] addValues = { getDeviceID(), getSerialNumber(), getRouteID() };

	add( TABLE_NAME, addValues );
}
/**
 * delete method comment.
 */
public void delete() throws java.sql.SQLException {

	delete( DYN_TABLE_NAME, "DeviceID", getDeviceID() );
	delete( TABLE_NAME, "DeviceID", getDeviceID() );
	
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Integer
 */
public Integer getDeviceID() {
	return deviceID;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Integer
 */
public Integer getRouteID() {
	return routeID;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Integer
 */
public Integer getSerialNumber() {
	return serialNumber;
}
/**
 * This method was created in VisualAge.
 * @return String[]
 *
 * This method returns the name of the PAOBjects that have the same address,
 *  or it will return null if the address is unique
 */
public static String[] isSerialNumberUnique(long serialnumber_, Integer excludedPAOId)
{
	java.sql.Connection conn = com.cannontech.database.PoolManager.getInstance().getConnection(
											com.cannontech.common.util.CtiUtilities.getDatabaseAlias() );

	java.sql.Statement stmt = null;
	java.sql.ResultSet rset = null;
	List<String> devices = new ArrayList<>();

	String sql = 
			"select y.paoname " +
			"from " + com.cannontech.database.db.pao.YukonPAObject.TABLE_NAME + " y, " + 
			TABLE_NAME + " d " +
			"where y.paobjectid=d.deviceid " +
			"and d.serialnumber= " + serialnumber_ +
			"and d.serialnumber!=0 " +
			(excludedPAOId != null 
					? " and y.paobjectid <> " + excludedPAOId
					: "");

	try
	{		
		stmt = conn.createStatement();
		rset = stmt.executeQuery( sql.toString() );

		while( rset.next() )
			devices.add( rset.getString(1) );

	}
	catch( java.sql.SQLException e )
	{
		com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
	}
	finally
	{
		SqlUtils.close(rset, stmt, conn );
	}

	if( devices.size() <= 0 )
		return null;
	else
	{
		String[] s = new String[devices.size()];
		return devices.toArray(s);
	}

}
/**
 * retrieve method comment.
 */
public void retrieve() throws java.sql.SQLException {

	String selectColumns[] = { "SerialNumber", "RouteID" };
	String constraintColumns[] = { "DeviceID" };
	Object constraintValues[] = { getDeviceID() };

	Object results[] = retrieve( selectColumns, TABLE_NAME, constraintColumns, constraintValues );

	if( results.length == selectColumns.length )
	{
		setSerialNumber( (Integer) results[0] );
		setRouteID( (Integer) results[1] );
	}
}
/**
 * This method was created in VisualAge.
 * @param newValue java.lang.Integer
 */
public void setDeviceID(Integer newValue) {
	this.deviceID = newValue;
}
/**
 * This method was created in VisualAge.
 * @param newValue java.lang.Integer
 */
public void setRouteID(Integer newValue) {
	this.routeID = newValue;
}
/**
 * This method was created in VisualAge.
 * @param newValue java.lang.Integer
 */
public void setSerialNumber(Integer newValue) {
	this.serialNumber = newValue;
}
/**
 * update method comment.
 */
public void update() throws java.sql.SQLException {

	String setColumns[] = { "SerialNumber", "RouteID" };
	Object setValues[]= { getSerialNumber(), getRouteID() };

	String constraintColumns[] = { "DeviceID" };
	Object constraintValues[] = { getDeviceID() };

	update( TABLE_NAME, setColumns, setValues, constraintColumns, constraintValues );
}
}
