package com.cannontech.database.data.capcontrol;

import java.sql.Connection;
import java.sql.SQLException;

import com.cannontech.common.pao.PaoType;
import com.cannontech.database.data.device.DNPBase;
import com.cannontech.database.db.DBCopiable;
import com.cannontech.database.db.capcontrol.DeviceCBC;

public class CapBankController6510 extends DNPBase implements DBCopiable, ICapBankController {
	
    private DeviceCBC deviceCBC = null;
    
    public CapBankController6510() {
        super(PaoType.DNP_CBC_6510);
    }
    
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
    
    @Override
    public void setCommID( Integer comID ) {
		getDeviceDirectCommSettings().setPortID( comID );
	}

	@Override
    public Integer getCommID() {
		return getDeviceDirectCommSettings().getPortID();
	}
	
	@Override
    public void add() throws SQLException {
        super.add();
        getDeviceCBC().add();
    }
    
    @Override
    public void delete() throws SQLException {
        if (!isPartialDelete) {
            getDeviceCBC().delete();
        }
        super.delete();
    }

    @Override
    public void retrieve() throws SQLException {
        super.retrieve();
        getDeviceCBC().retrieve();

    }

    @Override
    public void setDbConnection(Connection conn) {
        super.setDbConnection(conn);
        getDeviceCBC().setDbConnection(conn);

    }

    @Override
    public void setDeviceID(Integer deviceID) {
        super.setDeviceID(deviceID);
        getDeviceCBC().setDeviceID(deviceID);
    }

    @Override
    public void update() throws SQLException {
        super.update();
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