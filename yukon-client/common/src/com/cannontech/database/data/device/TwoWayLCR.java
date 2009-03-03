package com.cannontech.database.data.device;

import com.cannontech.database.db.device.DeviceLoadProfile;

public class TwoWayLCR extends CarrierBase {

	private DeviceLoadProfile deviceLoadProfile = null;
	
	public TwoWayLCR() {
		super();
	}
	
	public DeviceLoadProfile getDeviceLoadProfile() {
	 	if( deviceLoadProfile == null )
	 		deviceLoadProfile = new DeviceLoadProfile();
	 		
		return deviceLoadProfile;
	}
	
	public void add() throws java.sql.SQLException {
		super.add();
		getDeviceLoadProfile().add();
	}
	
	public void addPartial() throws java.sql.SQLException {
		super.addPartial();
		getDeviceLoadProfile().add();
	}
	
	public void delete() throws java.sql.SQLException {
		getDeviceLoadProfile().delete();
		super.delete();
	}
	
	public void retrieve() throws java.sql.SQLException {
		super.retrieve();
		getDeviceLoadProfile().retrieve();
	}
	
	public void setDbConnection(java.sql.Connection conn) {
		super.setDbConnection(conn);
		getDeviceLoadProfile().setDbConnection(conn);
	}
	
	public void setDeviceID(Integer deviceID) {
		super.setDeviceID(deviceID);
		getDeviceLoadProfile().setDeviceID(deviceID);
	}
	
	public void update() throws java.sql.SQLException {
		super.update();
		getDeviceLoadProfile().update();
	}
}
