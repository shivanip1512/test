package com.cannontech.database.db.device;

/**
 * This type was created in VisualAge.
 */
public class DeviceCarrierSettings extends com.cannontech.database.db.DBPersistent {
	private Integer deviceID = null;
	private Integer address = null;
/**
 * DeviceCarrierSettings constructor comment.
 */
public DeviceCarrierSettings() {
	super();
	initialize( null, null );
}
/**
 * DeviceCarrierSettings constructor comment.
 */
public DeviceCarrierSettings(Integer deviceID) {
	super();
	initialize( deviceID, null );
}
/**
 * DeviceCarrierSettings constructor comment.
 */
public DeviceCarrierSettings(Integer deviceID, Integer address ) {
	super();
	initialize( deviceID, address);
}
/**
 * add method comment.
 */
public void add() throws java.sql.SQLException {

	Object addValues[] = { getDeviceID(), getAddress() };

	add( "DeviceCarrierSettings", addValues );
}
/**
 * delete method comment.
 */
public void delete() throws java.sql.SQLException {

	delete( "DeviceCarrierSettings", "DeviceID", getDeviceID() );
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Integer
 */
public Integer getAddress() {
	return address;
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
 * @param deviceID java.lang.Integer
 * @param address java.lang.Integer
 * @param demandInterval Intger
 */
public void initialize( Integer deviceID, Integer address ) {

	setDeviceID( deviceID );
	setAddress( address );
}
/**
 * This method was created in VisualAge.
 * @return String[]
 *
 * This method returns the name of the PAOBjects that have the same address,
 *  or it will return null if the address is unique
 */
public static String[] isAddressUnique(int address, Integer excludedPAOId ) throws java.sql.SQLException
{
	java.sql.Connection conn = com.cannontech.database.PoolManager.getInstance().getConnection(
											com.cannontech.common.util.CtiUtilities.getDatabaseAlias() );

	java.sql.Statement stmt = null;
	java.sql.ResultSet rset = null;
	java.util.Vector devices = new java.util.Vector(5);

	String sql = 
			"select y.paoname " +
			"from " + com.cannontech.database.db.pao.YukonPAObject.TABLE_NAME + " y, " + 
			"devicecarriersettings d " +
			"where y.paobjectid=d.deviceid " +
			"and d.address= " + address +
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
		e.printStackTrace();
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
			e2.printStackTrace();//something is up
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

	String selectColumns[] = { "Address" };
	String constraintColumns[] = { "DeviceID" };

	Object constraintValues[] = { getDeviceID() };

	Object results[] = retrieve( selectColumns, "DeviceCarrierSettings", constraintColumns, constraintValues );

	if( results.length == selectColumns.length )
	{
		setAddress( (Integer) results[0] );
	}
}
/**
 * This method was created in VisualAge.
 * @param newValue java.lang.Integer
 */
public void setAddress(Integer newValue) {
	this.address = newValue;
}
/**
 * This method was created in VisualAge.
 * @param newValue java.lang.Integer
 */
public void setDeviceID(Integer newValue) {
	this.deviceID = newValue;
}
/**
 * update method comment.
 */
public void update() throws java.sql.SQLException {

	String setColumns[] = { "Address" };
	Object setValues[] = { getAddress() };

	String constraintColumns[] = { "DeviceID" };
	Object constraintValues[] = { getDeviceID() };

	update( "DeviceCarrierSettings", setColumns, setValues, constraintColumns, constraintValues );
}
}
