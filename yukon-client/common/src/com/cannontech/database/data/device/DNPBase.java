package com.cannontech.database.data.device;

import com.cannontech.database.data.pao.DeviceClasses;
import com.cannontech.database.db.device.DeviceAddress;

/**
 * @author rneuharth
 * Aug 9, 2002 at 12:43:42 PM
 * 
 * A undefined generated comment
 */
public class DNPBase extends RemoteBase implements com.cannontech.database.db.DBCopiable
{
   private DeviceAddress deviceAddress = null;

	/**
	 * Constructor for DNPBase.
	 */
	public DNPBase()
	{
		super();
		setDeviceClass( DeviceClasses.STRING_CLASS_RTU );
	}

   public Integer copiableAddress() 
   {
      return getDeviceAddress().getMasterAddress();
   }

   public void assignAddress( Integer newAddress )
   {
      getDeviceAddress().setMasterAddress( newAddress );
   }

   /**
    * This method was created in VisualAge.
    */
   public void add() throws java.sql.SQLException 
   {
      super.add();
      getDeviceAddress().add();
   }

   /**
    * This method was created in VisualAge.
    */
   public void delete() throws java.sql.SQLException
   {
      getDeviceAddress().delete();
      super.delete();
   }

   /**
    * This method was created in VisualAge.
    */
   public void retrieve() throws java.sql.SQLException
   {
      super.retrieve();
      getDeviceAddress().retrieve();
   }
   /**
    * Insert the method's description here.
    * Creation date: (1/4/00 3:32:03 PM)
    * @param conn java.sql.Connection
    */
   public void setDbConnection(java.sql.Connection conn) 
   {
      super.setDbConnection(conn);
      getDeviceAddress().setDbConnection(conn);
   }
   
   /**
    * This method was created in VisualAge.
    * @param deviceID java.lang.Integer
    */
   public void setDeviceID(Integer deviceID)
   {
      super.setDeviceID(deviceID);
      getDeviceAddress().setDeviceID(deviceID);
   }
   /**
    * This method was created in VisualAge.
    */
   public void update() throws java.sql.SQLException
   {
      super.update();
      getDeviceAddress().update();
   }

   /**
    * Returns the deviceDNP.
    * @return com.cannontech.database.db.device.DeviceAddress
    */
   public DeviceAddress getDeviceAddress()
   {
      if( deviceAddress == null )
			deviceAddress = new DeviceAddress();

      return deviceAddress;
   }

   /**
    * Sets the deviceDNP.
    * @param deviceDNP The deviceDNP to set
    */
   public void setDeviceAddress(DeviceAddress deviceAddr)
   {
      this.deviceAddress = deviceAddr;
   }

}
