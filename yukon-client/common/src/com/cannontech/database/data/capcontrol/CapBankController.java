package com.cannontech.database.data.capcontrol;

/**
 * This type was created in VisualAge.
 */
public abstract class CapBankController extends CapControlDeviceBase implements com.cannontech.database.db.DBCopiable, ICapBankController 
{
	private com.cannontech.database.db.capcontrol.DeviceCBC deviceCBC = null;
   
/**
 */
public CapBankController() {
	super();
}

   public Integer getAddress() 
   {
      return getDeviceCBC().getSerialNumber();
   }

   public void setAddress( Integer newAddress )
   {
      getDeviceCBC().setSerialNumber( newAddress );
   }
   
   public void setCommID( Integer comID )
   {
   	getDeviceCBC().setRouteID( comID );
   }

	public Integer getCommID()
	{
		return getDeviceCBC().getRouteID();
	}
  
  
/**
 * This method was created in VisualAge.
 */
public void add() throws java.sql.SQLException {
	super.add();
	getDeviceCBC().add();
}

public void addPartial() throws java.sql.SQLException {
	super.addPartial();
	getDeviceCBC().add();
}
/**
 * This method was created in VisualAge.
 */
public void delete() throws java.sql.SQLException{
	getDeviceCBC().delete();
	super.delete();
}
/**
 * This method was created in VisualAge.
 */
public com.cannontech.database.db.capcontrol.DeviceCBC getDeviceCBC() {
	if( deviceCBC == null )
	{
		deviceCBC = new com.cannontech.database.db.capcontrol.DeviceCBC();
	}
	
	return deviceCBC;
}
/**
 * This method was created in VisualAge.
 */
public void retrieve() throws java.sql.SQLException{
	super.retrieve();
	getDeviceCBC().retrieve();
}
/**
 * Insert the method's description here.
 * Creation date: (1/4/00 3:32:03 PM)
 * @param conn java.sql.Connection
 */
public void setDbConnection(java.sql.Connection conn) {
	super.setDbConnection(conn);
	getDeviceCBC().setDbConnection(conn);
}
/**
 * This method was created in VisualAge.
 */
public void setDeviceCBC(com.cannontech.database.db.capcontrol.DeviceCBC newValue) {
	this.deviceCBC = newValue;
}
/**
 * This method was created in VisualAge.
 * @param deviceID java.lang.Integer
 */
public void setDeviceID(Integer deviceID) {
	super.setDeviceID(deviceID);
	getDeviceCBC().setDeviceID(deviceID);
}
/**
 * This method was created in VisualAge.
 */
public void update() throws java.sql.SQLException{
	super.update();
	getDeviceCBC().update();
}
}
