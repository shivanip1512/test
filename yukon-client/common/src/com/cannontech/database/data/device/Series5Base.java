/*
 * Created on Mar 15, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.database.data.device;


import com.cannontech.database.data.pao.DeviceClasses;
/**
 * @author jdayton
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class Series5Base extends RemoteBase {
	
	//although this is not a DNP device, we use the DeviceDNP table
	//to store the address
	com.cannontech.database.db.device.DeviceDNP deviceDNP = null;
	
	public Series5Base() 
	{
		super();
		setDeviceClass( DeviceClasses.STRING_CLASS_TRANSMITTER );	
	} 
	
	
	public void add() throws java.sql.SQLException 
	{
	   super.add();
	   getSeries5().add();
	}

	/**
	 * This method was created in VisualAge.
	 */
	public void delete() throws java.sql.SQLException
	{
	   getSeries5().delete();
	   super.delete();
	}

	/**
	 * This method was created in VisualAge.
	 */
	public void retrieve() throws java.sql.SQLException
	{
	   super.retrieve();
	   getSeries5().retrieve();
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (1/4/00 3:32:03 PM)
	 * @param conn java.sql.Connection
	 */
	public void setDbConnection(java.sql.Connection conn) 
	{
	   super.setDbConnection(conn);
	   getSeries5().setDbConnection(conn);
	}
   
	/**
	 * This method was created in VisualAge.
	 * @param deviceID java.lang.Integer
	 */
	public void setDeviceID(Integer deviceID)
	{
	   super.setDeviceID(deviceID);
	   getSeries5().setDeviceID(deviceID);
	}
	/**
	 * This method was created in VisualAge.
	 */
	public void update() throws java.sql.SQLException
	{
	   super.update();
	   getSeries5().update();
	}

	/**
	 * Returns the deviceDNP.
	 * @return com.cannontech.database.db.device.DeviceDNP
	 */
	public com.cannontech.database.db.device.DeviceDNP getSeries5()
	{
	   if( deviceDNP == null )
		  deviceDNP = new com.cannontech.database.db.device.DeviceDNP();

	   return deviceDNP;
	}

	/**
	 * Sets the deviceDNP.
	 * @param deviceDNP The deviceDNP to set
	 */
	public void setSeries5(com.cannontech.database.db.device.DeviceDNP series5)
	{
	   this.deviceDNP = series5;
	}
	


}
