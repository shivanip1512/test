package com.cannontech.database.db.capcontrol;

/**
 * This type was created in VisualAge.
 */
public class DeviceCBC extends com.cannontech.database.db.DBPersistent 
{
	private Integer deviceID = null;
	private Integer serialNumber = null;
	private Integer routeID = null;

	public static String TABLE_NAME = "DeviceCBC";
/**
 * DeviceTwoWayFlags constructor comment.
 */
public DeviceCBC() {
	super();
	initialize( null, null, null );
}
/**
 * DeviceTwoWayFlags constructor comment.
 */
public DeviceCBC(Integer deviceID) {
	super();
	initialize( deviceID, null, null );
}
/**
 * add method comment.
 */
public void add() throws java.sql.SQLException {
	Object[] addValues = { getDeviceID(), getSerialNumber(), getRouteID() };

	add( TABLE_NAME, addValues );
}
/**
 * delete method comment.
 */
public void delete() throws java.sql.SQLException {

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
 */
public void initialize( Integer deviceID, Integer serialNumber, Integer routeID ) {

	setDeviceID( deviceID );
	setSerialNumber( serialNumber );
	setRouteID( routeID );
}
/**
 * This method was created in VisualAge.
 * @return String[]
 *
 * This method returns the name of the PAOBjects that have the same address,
 *  or it will return null if the address is unique
 */
public static String[] isSerialNumberUnique(long serialnumber_, Integer excludedPAOId) throws java.sql.SQLException
{
	java.sql.Connection conn = com.cannontech.database.PoolManager.getInstance().getConnection(
											com.cannontech.common.util.CtiUtilities.getDatabaseAlias() );

	java.sql.Statement stmt = null;
	java.sql.ResultSet rset = null;
	java.util.Vector devices = new java.util.Vector(5);

	String sql = 
			"select y.paoname " +
			"from " + com.cannontech.database.db.pao.YukonPAObject.TABLE_NAME + " y, " + 
			TABLE_NAME + " d " +
			"where y.paobjectid=d.deviceid " +
			"and d.serialnumber= " + serialnumber_ +
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
		try
		{			
			if( stmt != null ) stmt.close();
			if( conn != null ) conn.close();
		} 
		catch( java.sql.SQLException e2 )
		{
			com.cannontech.clientutils.CTILogger.error( e2.getMessage(), e2 );//something is up
		}	
	}

	if( devices.size() <= 0 )
		return null;
	else
	{
		String[] s = new String[devices.size()];
		return (String[])devices.toArray(s);
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
