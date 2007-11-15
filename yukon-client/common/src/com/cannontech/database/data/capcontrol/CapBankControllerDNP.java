package com.cannontech.database.data.capcontrol;

import com.cannontech.database.db.capcontrol.DeviceCBC;
import com.cannontech.database.db.device.DeviceAddress;


public class CapBankControllerDNP extends com.cannontech.database.data.device.RemoteBase implements com.cannontech.database.db.DBCopiable, ICapBankController
{
   private DeviceAddress deviceAddress = null;
   private DeviceCBC deviceCBC = null;

    public CapBankControllerDNP()
    {
        super();
      
    }
    
    public Integer getSerialNumber() {
        return getDeviceCBC().getSerialNumber();
    }


    public void setSerialNumber(Integer serialNumber_) {
        getDeviceCBC().setSerialNumber(serialNumber_);
    }


    public Integer getRouteId() {
        return getDeviceCBC().getRouteID(); 
        //getDeviceAddress().getRouteId();
    }


    public void setRouteId(Integer routeId_) {
        getDeviceCBC().setRouteID(routeId_);
    }

   public Integer getAddress() 
   {
      return getDeviceAddress().getMasterAddress();
   }

   public void setAddress( Integer newAddress )
   {
      getDeviceAddress().setMasterAddress( newAddress );
   }


    public void setCommID( Integer comID )
    {
        getDeviceDirectCommSettings().setPortID( comID );
    }

    public Integer getCommID()
    {
        return getDeviceDirectCommSettings().getPortID();
    }
    
   /**
    * This method was created in VisualAge.
    */
   public void add() throws java.sql.SQLException 
   {
      super.add();
      getDeviceAddress().add();
      getDeviceCBC().add();

   }

   /**
    * This method was created in VisualAge.
    */
   public void delete() throws java.sql.SQLException
   {
      if (!isPartialDelete) {
         getDeviceAddress().delete();
         getDeviceCBC().delete();
      }
      super.delete();
   }

   /**
    * This method was created in VisualAge.
    */
   public void retrieve() throws java.sql.SQLException
   {
      super.retrieve();
      getDeviceAddress().retrieve();
      getDeviceCBC().retrieve();
      

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
      getDeviceCBC().setDbConnection(conn);
     
   }
   
   /**
    * This method was created in VisualAge.
    * @param deviceID java.lang.Integer
    */
   public void setDeviceID(Integer deviceID)
   {
      super.setDeviceID(deviceID);
      getDeviceAddress().setDeviceID(deviceID);
      getDeviceCBC().setDeviceID(deviceID);
     
   }
   /**
    * This method was created in VisualAge.
    */
   public void update() throws java.sql.SQLException
   {
      super.update();
      getDeviceAddress().update();
      getDeviceCBC().update();

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

    public DeviceCBC getDeviceCBC() {
        if (deviceCBC == null)
            deviceCBC = new DeviceCBC();
        return deviceCBC;
    }

    public void setDeviceCBC(DeviceCBC deviceCBC) {
        this.deviceCBC = deviceCBC;
        
    }
    

}
