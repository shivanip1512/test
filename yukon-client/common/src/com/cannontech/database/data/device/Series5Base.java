
package com.cannontech.database.data.device;


import com.cannontech.database.data.pao.DeviceClasses;
import com.cannontech.database.db.device.DeviceAddress;
import com.cannontech.database.db.device.DeviceSeries5RTU;
import com.cannontech.database.db.device.DeviceVerification;
/**
 * @author jdayton
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class Series5Base extends RemoteBase {
	
	//we use the DeviceAddress table
	//to store the address
	private DeviceAddress deviceAddress = null;
	private DeviceSeries5RTU series5RTU = null;
	//DeviceVerification entry that references itself (identical IDs)
	private DeviceVerification lmiVerification = null;
	
	
	public Series5Base() 
	{
		super();
		setDeviceClass( DeviceClasses.STRING_CLASS_TRANSMITTER );	
	} 
	
	
	public void add() throws java.sql.SQLException 
	{
		super.add();
	   	getSeries5().add();
		getSeries5RTU().add();
		getVerification().add();
	}

	/**
	 * This method was created in VisualAge.
	 */
	public void delete() throws java.sql.SQLException
	{
		getSeries5RTU().delete();
	   	getSeries5().delete();
	   	getVerification().delete();
	   	super.delete();
	}

	/**
	 * This method was created in VisualAge.
	 */
	public void retrieve() throws java.sql.SQLException
	{
	   	super.retrieve();
	  	getSeries5().retrieve();
		getSeries5RTU().retrieve();
		getVerification().retrieve();
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
		getSeries5RTU().setDbConnection(conn);
		getVerification().setDbConnection(conn);
	}
   
	/**
	 * This method was created in VisualAge.
	 * @param deviceID java.lang.Integer
	 */
	public void setDeviceID(Integer deviceID)
	{
	   	super.setDeviceID(deviceID);
	   	getSeries5().setDeviceID(deviceID);
		getSeries5RTU().setDeviceID(deviceID);
		
		//funky
		getVerification().setReceiverID(deviceID);
		getVerification().setTransmitterID(deviceID);
	}
	/**
	 * This method was created in VisualAge.
	 */
	public void update() throws java.sql.SQLException
	{
	   	super.update();
	   	getSeries5().update();
		getSeries5RTU().update();
		getVerification().update();
	}

	/**
	 * Returns the deviceDNP.
	 * @return com.cannontech.database.db.device.DeviceAddress
	 */
	public DeviceAddress getSeries5()
	{
	   if( deviceAddress == null )
			deviceAddress = new DeviceAddress();

	   return deviceAddress;
	}

	/**
	 * Sets the deviceDNP.
	 * @param deviceDNP The deviceDNP to set
	 */
	public void setSeries5(DeviceAddress series5)
	{
	   this.deviceAddress = series5;
	}

	/**
	 * @return
	 */
	public DeviceSeries5RTU getSeries5RTU()
	{
		if( series5RTU == null )
			series5RTU = new DeviceSeries5RTU();

		return series5RTU;
	}

	/**
	 * @param series5RTU
	 */
	public void setSeries5RTU(DeviceSeries5RTU series5RTU)
	{
		this.series5RTU = series5RTU;
	}
	
	public DeviceVerification getVerification()
	{
		if(lmiVerification == null)
			lmiVerification = new DeviceVerification();
			
		return lmiVerification;
	}
	
	public void setVerification(DeviceVerification verify)
	{
		this.lmiVerification = verify;
	}
	

}
