package com.cannontech.database.db.device.lm;

import com.cannontech.common.util.CtiUtilities;

/**
 * This type was created in VisualAge.
 */
public class LMGroupMCT extends com.cannontech.database.db.DBPersistent 
{
	private Integer deviceID = null;
	private Integer address = new Integer(CtiUtilities.NONE_ZERO_ID);
	private String level = "L";
	private String relayUsage = "1      ";
	private Integer routeID = null;	
	private Integer mctDeviceID = new Integer(CtiUtilities.NONE_ZERO_ID);

	public static final String SETTER_COLUMNS[] = 
	{ 
		"MCTAddress", "MCTLevel", "RelayUsage", "RouteID",
		"MCTDeviceID"
	};

	public static final String CONSTRAINT_COLUMNS[] = { "DeviceID" };

	public static final String TABLE_NAME = "LMGroupMCT";

	/**
	 * LMGroupMCT constructor comment.
	 */
	public LMGroupMCT() {
		super();
	}
	/**
	 * add method comment.
	 */
	public void add() throws java.sql.SQLException 
	{
		Object addValues[] = { getDeviceID(), getAddress(), 
			getLevel(), getRelayUsage(), getRouteID(), getMctDeviceID() }; 

		add( TABLE_NAME, addValues );
	}
	/**
	 * delete method comment.
	 */
	public void delete() throws java.sql.SQLException {
		delete( TABLE_NAME, CONSTRAINT_COLUMNS[0], getDeviceID() );
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
	 * @return java.lang.String
	 */
	public String getRelayUsage() {
		return relayUsage;
	}
	/**
	 * This method was created in VisualAge.
	 * @return java.lang.Integer
	 */
	public Integer getRouteID() {
		return routeID;
	}
	/**
	 * retrieve method comment.
	 */
	public void retrieve() throws java.sql.SQLException 
	{
		Object constraintValues[] = { getDeviceID() };
	
		Object results[] = retrieve( SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );
	
		if( results.length == SETTER_COLUMNS.length )
		{			
			setAddress( (Integer) results[0] );
			setLevel( (String) results[1] );
			setRelayUsage( (String) results[2] );
			setRouteID( (Integer) results[3] );
			setMctDeviceID( (Integer) results[4] );
		}
		else
			throw new Error(getClass() + " - Incorrect Number of results retrieved");
	
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
	 * @param newValue java.lang.String
	 */
	public void setRelayUsage(String newValue) {
		this.relayUsage = newValue;
	}
	/**
	 * This method was created in VisualAge.
	 * @param newValue java.lang.Integer
	 */
	public void setRouteID(Integer newValue) {
		this.routeID = newValue;
	}
	/**
	 * update method comment.
	 */
	public void update() throws java.sql.SQLException 
	{
		Object setValues[] = { getAddress(), 
			getLevel(), getRelayUsage(), getRouteID(), getMctDeviceID() }; 
	
		Object constraintValues[] = { getDeviceID() };
	
		update( TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues );
	}
	/**
	 * @return
	 */
	public Integer getAddress()
	{
		return address;
	}

	/**
	 * @return
	 */
	public String getLevel()
	{
		return level;
	}

	/**
	 * @param integer
	 */
	public void setAddress(Integer integer)
	{
		address = integer;
	}

	/**
	 * @param string
	 */
	public void setLevel(String string)
	{
		level = string;
	}

	/**
	 * @return
	 */
	public Integer getMctDeviceID()
	{
		return mctDeviceID;
	}

	/**
	 * @param integer
	 */
	public void setMctDeviceID(Integer integer)
	{
		mctDeviceID = integer;
	}

}
