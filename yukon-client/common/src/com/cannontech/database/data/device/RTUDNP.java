package com.cannontech.database.data.device;

/**
 * @author rneuharth
 * Aug 9, 2002 at 12:43:42 PM
 * 
 * A undefined generated comment
 */
public class RTUDNP extends TwoWayDevice
{
   com.cannontech.database.db.device.DeviceDNP deviceDNP = null;

	/**
	 * Constructor for RTUDNP.
	 */
	public RTUDNP()
	{
		super();
	}

   /**
    * This method was created in VisualAge.
    */
   public void add() throws java.sql.SQLException 
   {
      super.add();
      getDeviceDNP().add();
   }

   /**
    * This method was created in VisualAge.
    */
   public void delete() throws java.sql.SQLException
   {
      getDeviceDNP().delete();
      super.delete();
   }

   /**
    * This method was created in VisualAge.
    */
   public void retrieve() throws java.sql.SQLException
   {
      super.retrieve();
      getDeviceDNP().retrieve();
   }
   /**
    * Insert the method's description here.
    * Creation date: (1/4/00 3:32:03 PM)
    * @param conn java.sql.Connection
    */
   public void setDbConnection(java.sql.Connection conn) 
   {
      super.setDbConnection(conn);
      getDeviceDNP().setDbConnection(conn);
   }
   
   /**
    * This method was created in VisualAge.
    * @param deviceID java.lang.Integer
    */
   public void setDeviceID(Integer deviceID)
   {
      super.setDeviceID(deviceID);
      getDeviceDNP().setDeviceID(deviceID);
   }
   /**
    * This method was created in VisualAge.
    */
   public void update() throws java.sql.SQLException
   {
      super.update();
      getDeviceDNP().update();
   }

   /**
    * Returns the deviceDNP.
    * @return com.cannontech.database.db.device.DeviceDNP
    */
   public com.cannontech.database.db.device.DeviceDNP getDeviceDNP()
   {
      if( deviceDNP == null )
         deviceDNP = new com.cannontech.database.db.device.DeviceDNP();

      return deviceDNP;
   }

   /**
    * Sets the deviceDNP.
    * @param deviceDNP The deviceDNP to set
    */
   public void setDeviceDNP(com.cannontech.database.db.device.DeviceDNP deviceDNP)
   {
      this.deviceDNP = deviceDNP;
   }

}
