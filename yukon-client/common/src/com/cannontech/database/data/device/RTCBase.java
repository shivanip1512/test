/*
 * Created on Mar 22, 2004
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
public class RTCBase extends RemoteBase 
{
	
	com.cannontech.database.db.device.DeviceRTC deviceRTC = null;
	
	//strings for the Listen Before Talk column
	public static final String LBT0 = "No LBT";
	public static final String LBT1 = "Wait for next slot";
	public static final String LBT2 = "Wait for freq. clear";
	public static final String LBT3 = "Override after 1 slot";
	
	/**
	 * Constructor for RTC.
	 */
	public RTCBase()
	{
		super();
		setDeviceClass( DeviceClasses.STRING_CLASS_TRANSMITTER );
	}

   public void assignAddress( Integer newAddress )
   {
	  getDeviceRTC().setRTCAddress( newAddress );
   }

   /**
	* This method was created in VisualAge.
	*/
   public void add() throws java.sql.SQLException 
   {
	  super.add();
	  getDeviceRTC().add();
   }

   /**
	* This method was created in VisualAge.
	*/
   public void delete() throws java.sql.SQLException
   {
	  getDeviceRTC().delete();
	  super.delete();
   }

   /**
	* This method was created in VisualAge.
	*/
   public void retrieve() throws java.sql.SQLException
   {
	  super.retrieve();
	  getDeviceRTC().retrieve();
   }
   /**
	* Insert the method's description here.
	* Creation date: (1/4/00 3:32:03 PM)
	* @param conn java.sql.Connection
	*/
   public void setDbConnection(java.sql.Connection conn) 
   {
	  super.setDbConnection(conn);
	  getDeviceRTC().setDbConnection(conn);
   }
   
   /**
	* This method was created in VisualAge.
	* @param deviceID java.lang.Integer
	*/
   public void setDeviceID(Integer deviceID)
   {
	  super.setDeviceID(deviceID);
	  getDeviceRTC().setDeviceID(deviceID);
   }
   /**
	* This method was created in VisualAge.
	*/
   public void update() throws java.sql.SQLException
   {
	  super.update();
	  getDeviceRTC().update();
   }

   /**
	* Returns the deviceDNP.
	* @return com.cannontech.database.db.device.DeviceDNP
	*/
   public com.cannontech.database.db.device.DeviceRTC getDeviceRTC()
   {
	  if( deviceRTC == null )
		 deviceRTC = new com.cannontech.database.db.device.DeviceRTC();

	  return deviceRTC;
   }

   /**
	* Sets the deviceDNP.
	* @param deviceDNP The deviceDNP to set
	*/
   public void setDeviceRTC(com.cannontech.database.db.device.DeviceRTC deviceRTC)
   {
	  this.deviceRTC = deviceRTC;
   }
   
   public void setLBTMode(String newMode)
   {
   		if(newMode.compareTo(LBT0) == 0)
   			getDeviceRTC().setLBTMode(new Integer(0));
		if(newMode.compareTo(LBT1) == 0)
			getDeviceRTC().setLBTMode(new Integer(1));
		if(newMode.compareTo(LBT2) == 0)
			getDeviceRTC().setLBTMode(new Integer(2));
		if(newMode.compareTo(LBT3) == 0)
			getDeviceRTC().setLBTMode(new Integer(3));
   }
   
   public static String getLBTModeString(Integer newMode)
   {
		String lbtFun = LBT0;
		
		if(newMode.intValue() == 1)
			lbtFun = LBT1;
		if(newMode.intValue() == 2)
			lbtFun = LBT2;
		if(newMode.intValue() == 3)
			lbtFun = LBT3;
			
		return lbtFun;
   }

}
