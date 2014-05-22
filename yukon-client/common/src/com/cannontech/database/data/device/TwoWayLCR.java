package com.cannontech.database.data.device;

import com.cannontech.common.pao.PaoType;
import com.cannontech.database.db.device.DeviceLoadProfile;

public class TwoWayLCR extends CarrierBase {

	private DeviceLoadProfile deviceLoadProfile = null;
	
	public TwoWayLCR() {
		super(PaoType.LCR3102);
	}
	
	public DeviceLoadProfile getDeviceLoadProfile() {
	 	if( deviceLoadProfile == null )
	 		deviceLoadProfile = new DeviceLoadProfile();
	 		
		return deviceLoadProfile;
	}
	
	@Override
    public void add() throws java.sql.SQLException {
		super.add();
		getDeviceLoadProfile().add();
	}
	
	@Override
    public void addPartial() throws java.sql.SQLException {
		super.addPartial();
		getDeviceLoadProfile().add();
	}
	
	@Override
    public void delete() throws java.sql.SQLException {
		getDeviceLoadProfile().delete();
		super.delete();
	}
	
	@Override
    public void retrieve() throws java.sql.SQLException {
		super.retrieve();
		getDeviceLoadProfile().retrieve();
	}
	
	@Override
    public void setDbConnection(java.sql.Connection conn) {
		super.setDbConnection(conn);
		getDeviceLoadProfile().setDbConnection(conn);
	}
	
	@Override
    public void setDeviceID(Integer deviceID) {
		super.setDeviceID(deviceID);
		getDeviceLoadProfile().setDeviceID(deviceID);
	}
	
	@Override
    public void update() throws java.sql.SQLException {
		super.update();
		getDeviceLoadProfile().update();
	}
}
