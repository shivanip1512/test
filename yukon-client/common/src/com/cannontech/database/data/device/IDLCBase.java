package com.cannontech.database.data.device;

/**
 * This type was created in VisualAge.
 */
import com.cannontech.database.db.device.DeviceIDLCRemote;

public class IDLCBase extends RemoteBase implements com.cannontech.database.db.DBCopiable
{
	private DeviceIDLCRemote deviceIDLCRemote = null;
   
   /**
    * SmartTransmitted constructor comment.
    */
   public IDLCBase() {
   	super();
   }
   
   public Integer copiableAddress() 
   {
      return getDeviceIDLCRemote().getAddress();
   }
   
   public void assignAddress( Integer newAddress )
   {
      getDeviceIDLCRemote().setAddress( newAddress );
   }
   
/**
 * This method was created in VisualAge.
 */
public void add() throws java.sql.SQLException {
	super.add();
	getDeviceIDLCRemote().add();
}
/**
 * Insert the method's description here.
 * Creation date: (6/15/2001 9:45:22 AM)
 * @param deviceID int
 * @exception java.sql.SQLException The exception description.
 */
public void addPartial() throws java.sql.SQLException 
{
	super.addPartial();
	getDeviceIDLCRemote().add();
}
/**
 * This method was created in VisualAge.
 */
public void delete() throws java.sql.SQLException {
	getDeviceIDLCRemote().delete();
	super.delete();
}
/**
 * Insert the method's description here.
 * Creation date: (6/14/2001 11:17:30 AM)
 * @exception java.sql.SQLException The exception description.
 */
public void deletePartial() throws java.sql.SQLException {

	super.deletePartial();

}
/**
 * This method was created in VisualAge.
 * @return com.cannontech.database.db.device.DeviceDirectCommSettings
 */
public DeviceIDLCRemote getDeviceIDLCRemote() {
	if( deviceIDLCRemote == null )
		deviceIDLCRemote = new DeviceIDLCRemote();
		
	return deviceIDLCRemote;
}
/**
 * This method was created in VisualAge.
 */
public void retrieve() throws java.sql.SQLException {

	super.retrieve();
	getDeviceIDLCRemote().retrieve();
}
/**
 * Insert the method's description here.
 * Creation date: (1/4/00 3:32:03 PM)
 * @param conn java.sql.Connection
 */
public void setDbConnection(java.sql.Connection conn) 
{
	super.setDbConnection(conn);

	getDeviceIDLCRemote().setDbConnection(conn);
}
/**
 * This method was created in VisualAge.
 * @param deviceID java.lang.Integer
 */
public void setDeviceID(Integer deviceID) {
	super.setDeviceID(deviceID);
	getDeviceIDLCRemote().setDeviceID(deviceID);
}
/**
 * This method was created in VisualAge.
 * @param newValue com.cannontech.database.db.device.DeviceDirectCommSettings
 */
public void setDeviceIDLCRemote(DeviceIDLCRemote newValue) {
	this.deviceIDLCRemote = newValue;
}
/**
 * This method was created in VisualAge.
 */
public void update() throws java.sql.SQLException {
	super.update();
	getDeviceIDLCRemote().update();
}
}
