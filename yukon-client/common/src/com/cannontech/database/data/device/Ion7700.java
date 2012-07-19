package com.cannontech.database.data.device;

import com.cannontech.database.db.device.DeviceMeterGroup;

/**
 * @author rneuharth
 * Aug 9, 2002 at 12:43:42 PM
 * 
 * A undefined generated comment
 */
public class Ion7700 extends AddressBase implements IDeviceMeterGroup
// implements com.cannontech.database.db.DBCopiable
{
	private DeviceMeterGroup deviceMeterGroup = null;
	
	/**
	 * Constructor for Ion7700.
	 */
	public Ion7700()
	{
		super();
	}

	public DeviceMeterGroup getDeviceMeterGroup()
	{
		if( deviceMeterGroup == null )
			deviceMeterGroup = new DeviceMeterGroup();
			
		return deviceMeterGroup;
	}

	public void setDeviceMeterGroup( DeviceMeterGroup dvMtrGrp_ )
	{
		deviceMeterGroup = dvMtrGrp_;
	}

	/**
	 * This method was created in VisualAge.
	 */
	public void add() throws java.sql.SQLException {
		super.add();
		getDeviceMeterGroup().add();
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (6/15/2001 9:41:18 AM)
	 * @param deviceID int
	 * @exception java.sql.SQLException The exception description.
	 */
	public void addPartial() throws java.sql.SQLException 
	{
		super.addPartial();
		getDeviceMeterGroup().add();
	}

	/**
	 * This method was created in VisualAge.
	 */
	public void delete() throws java.sql.SQLException 
	{
		getDeviceMeterGroup().delete();
		super.delete();
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (6/14/2001 11:10:07 AM)
	 * @exception java.sql.SQLException The exception description.
	 */
	public void deletePartial() throws java.sql.SQLException {
	
		super.deletePartial();	
	}

	/**
	 * This method was created in VisualAge.
	 */
	public void retrieve() throws java.sql.SQLException {
		super.retrieve();
		getDeviceMeterGroup().retrieve();
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (1/4/00 3:32:03 PM)
	 * @param conn java.sql.Connection
	 */
	public void setDbConnection(java.sql.Connection conn) 
	{
		super.setDbConnection(conn);
		getDeviceMeterGroup().setDbConnection(conn);
	}

	/**
	 * This method was created in VisualAge.
	 * @param deviceID java.lang.Integer
	 */
	public void setDeviceID(Integer deviceID) {
		super.setDeviceID(deviceID);
		getDeviceMeterGroup().setDeviceID(deviceID);
	}

	/**
	 * This method was created in VisualAge.
	 */
	public void update() throws java.sql.SQLException 
	{
		super.update();

		getDeviceMeterGroup().update();
	}

}
