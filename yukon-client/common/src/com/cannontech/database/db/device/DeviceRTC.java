/*
 * Created on Mar 19, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.database.db.device;

import com.cannontech.database.db.DBPersistent;

/**
 * @author jdayton
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class DeviceRTC extends DBPersistent 
{
	private Integer deviceID;
	private Integer rtcAddress;
	private String response = "N";
	private Integer lbtMode = new Integer(0);
	
	public static final String SETTER_COLUMNS[] = 
		{ 
			"DEVICEID", "RTCADDRESS", "RESPONSE", 
			"LBTMODE"
		};

		public static final String CONSTRAINT_COLUMNS[] = { "DEVICEID" };

		public static final String TABLE_NAME = "DeviceRTC";

	/**
	 * DeviceRTC constructor comment.
	 */
	public DeviceRTC() {
		super();
	}
	/**
	 * DeviceRTC constructor comment.
	 */
	public DeviceRTC(Integer dID, Integer rtcAdd) {
		super();
		deviceID = dID;
		rtcAddress = rtcAdd;

	
	}


	public void add() throws java.sql.SQLException
	{
		Object addValues[] = 
		{ 
			getDeviceID(), getRTCAddress(),
			getResponse(), getLBTMode()
		};

		add( TABLE_NAME, addValues );
	}

	public void delete() throws java.sql.SQLException
	{
		delete( TABLE_NAME, CONSTRAINT_COLUMNS[0], getDeviceID());
	}


	public Integer getDeviceID() {
		return deviceID;
	}

	public Integer getRTCAddress() {
		return rtcAddress;
	}

	public String getResponse() {
		return response;
	}

	public Integer getLBTMode() {
		return lbtMode;
	}
	
	public void retrieve() 
	{
		Integer constraintValues[] = { getDeviceID() };	
	
		try
		{
			Object results[] = retrieve( SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );
	
			if( results.length == SETTER_COLUMNS.length )
			{
				setRTCAddress( (Integer) results[1] );
				setResponse( (String) results[2]);
				setLBTMode( (Integer) results[3]);
			
			}
		else
			throw new Error(getClass() + " - Incorrect Number of results retrieved");
		}
		catch (Exception e)
		{
			com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
		}
	}


	public void setDeviceID(Integer newDeviceID) {
		deviceID = newDeviceID;
	}

	public void setRTCAddress(Integer newAddress) {
		rtcAddress = newAddress;
	}

	public void setResponse(String newResponse) {
		response = newResponse;
	}

	public void setLBTMode(Integer newMode) {
		lbtMode = newMode;
	}
	
	public void update() 
	{
		Object setValues[] =
		{ 
			getDeviceID(),
			getRTCAddress(),
			getResponse(),
			getLBTMode()	
		};
	
		Object constraintValues[] = { getDeviceID() };
	
		try
		{
			update( TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues );
		}
		catch (Exception e)
		{
			com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
		}
	}
}