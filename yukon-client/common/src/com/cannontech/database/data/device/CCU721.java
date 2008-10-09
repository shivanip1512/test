package com.cannontech.database.data.device;

import com.cannontech.database.db.device.DeviceAddress;
import com.cannontech.database.data.pao.DeviceClasses;

/**
 * CCU 721
 */
public class CCU721 extends RemoteBase {
	private DeviceAddress deviceAddress = null;

	public CCU721() {
        super();
		setDeviceClass( DeviceClasses.STRING_CLASS_TRANSMITTER );	
    }

    public void add() throws java.sql.SQLException {
    	super.add();
    	getDeviceAddress().add();
    }

    public void addPartial() throws java.sql.SQLException
    {
    	super.addPartial();

    	getDeviceAddress().add();
    }

    public void delete() throws java.sql.SQLException {
    	getDeviceAddress().delete();
    	super.delete();
    }

    public void deletePartial() throws java.sql.SQLException {

    	super.deletePartial();

    }

    public DeviceAddress getDeviceAddress() 
    {
    	if( deviceAddress == null)
    	{
    		deviceAddress = new DeviceAddress();
    	}
    		
    	return deviceAddress;
    }

    public void retrieve() throws java.sql.SQLException {

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

    public void setDeviceAddress(DeviceAddress newValue) {
    	this.deviceAddress = newValue;
    }

    public void setDeviceID( Integer deviceID) {
    	super.setDeviceID(deviceID);

    	getDeviceAddress().setDeviceID( deviceID);
    }
    /**
     * This method was created in VisualAge.
     */
    public void update() throws java.sql.SQLException {
    	super.update();
    	getDeviceAddress().update();
    }
}
