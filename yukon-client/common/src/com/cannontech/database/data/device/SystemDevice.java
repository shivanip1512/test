package com.cannontech.database.data.device;

import com.cannontech.database.data.pao.DeviceClasses;

/**
 * A place holder device type to be loaded only from the database.
 * 
 */
public class SystemDevice extends DeviceBase 
{
	/**
	 * SystemDevice constructor comment.
	 */
	public SystemDevice() {
		super();
		setDeviceClass( DeviceClasses.STRING_CLASS_SYSTEM );
	}
	/**
	 * This method was created in VisualAge.
	 */
	public void add() throws java.sql.SQLException {
		throw new IllegalArgumentException("Instances of " + this.getClass().getName() +
			" can not be ADDED to the database");
	}
	/**
	 * @param deviceID int
	 * @exception java.sql.SQLException The exception description.
	 */
	public void addPartial() throws java.sql.SQLException {
		throw new IllegalArgumentException("Instances of " + this.getClass().getName() +
			" can not be PARTIALLY_ADDED to the database");
		}
	/**
	 * This method was created in VisualAge.
	 */
	public void delete() throws java.sql.SQLException{
		throw new IllegalArgumentException("Instances of " + this.getClass().getName() +
			" can not be DELETE from the database");
	}
	/**
	 * @exception java.sql.SQLException The exception description.
	 */
	public void deletePartial() throws java.sql.SQLException {
		throw new IllegalArgumentException("Instances of " + this.getClass().getName() +
			" can not be PARTIALLY_DELETED from the database");
		}
	/**
	 * This method was created in VisualAge.
	 */
	public void retrieve() throws java.sql.SQLException
	{
		super.retrieve();
	}
	/**
	 * @param conn java.sql.Connection
	 */
	public void setDbConnection(java.sql.Connection conn) 
	{
		super.setDbConnection(conn);
	}
	/**
	 * This method was created in VisualAge.
	 * @param deviceID java.lang.Integer
	 */
	public void setDeviceID(Integer deviceID) 
	{
		super.setDeviceID(deviceID);
	}
	/**
	 * This method was created in VisualAge.
	 */
	public void update() throws java.sql.SQLException
	{
		throw new IllegalArgumentException("Instances of " + this.getClass().getName() +
			" can not be UPDATED in the database");
	}
}
