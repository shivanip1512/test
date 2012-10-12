package com.cannontech.database.data.capcontrol;

import com.cannontech.database.data.device.DNPBase;
import com.cannontech.database.db.DBCopiable;
import com.cannontech.database.db.capcontrol.DeviceCBC;

public class CapBankController702x extends DNPBase implements DBCopiable, ICapBankController {
    private DeviceCBC deviceCBC = null;

    public Integer getSerialNumber() {
        return getDeviceCBC().getSerialNumber();
    }

    public void setSerialNumber(Integer serialNumber_) {
        getDeviceCBC().setSerialNumber(serialNumber_);
    }

    public Integer getRouteId() {
        return getDeviceCBC().getRouteID(); 
    }

    public void setRouteId(Integer routeId_) {
        getDeviceCBC().setRouteID(routeId_);
    }

    public Integer getAddress() {
        return getDeviceAddress().getMasterAddress();
    }

    public void setAddress( Integer newAddress ) {
        getDeviceAddress().setMasterAddress( newAddress );
    }

	public void setCommID( Integer comID ) {
		getDeviceDirectCommSettings().setPortID( comID );
	}

	public Integer getCommID() {
		return getDeviceDirectCommSettings().getPortID();
	}
	
	public void add() throws java.sql.SQLException {
	    super.add();
	    getDeviceCBC().add();
	}

	public void delete() throws java.sql.SQLException {
	    if (!isPartialDelete) {
	        getDeviceAddress().delete();
	        getDeviceCBC().delete();
	    }
	    super.delete();
	}

	public void retrieve() throws java.sql.SQLException {
	    super.retrieve();
	    getDeviceAddress().retrieve();
	    getDeviceCBC().retrieve();
	}

	public void setDbConnection(java.sql.Connection conn) {
	    super.setDbConnection(conn);
	    getDeviceAddress().setDbConnection(conn);
	    getDeviceCBC().setDbConnection(conn);
	}

	public void setDeviceID(Integer deviceID) {
	    super.setDeviceID(deviceID);
	    getDeviceAddress().setDeviceID(deviceID);
	    getDeviceCBC().setDeviceID(deviceID);
	}

	public void update() throws java.sql.SQLException {
	    super.update();
	    getDeviceAddress().update();
	    getDeviceCBC().update();
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