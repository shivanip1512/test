package com.cannontech.database.data.device;

import com.cannontech.common.device.config.model.DNPConfiguration;
import com.cannontech.database.data.pao.DeviceClasses;
import com.cannontech.database.db.DBCopiable;

public class DNPBase extends AddressBase implements DBCopiable {
    private DNPConfiguration dnpConfiguration = null;

    public DNPBase() {
        super();
        setDeviceClass(DeviceClasses.STRING_CLASS_RTU);
    }

    public void add() throws java.sql.SQLException {
        super.add();

        Object[] configValues = { getPAObjectID(), getDnpConfiguration().getId() };
        add("DeviceConfigurationDeviceMap", configValues);
    }
    
    public void delete() throws java.sql.SQLException {
        super.delete();
    }

    public void retrieve() throws java.sql.SQLException {
        super.retrieve();
    }

    public void setDbConnection(java.sql.Connection conn) {
        super.setDbConnection(conn);
    }

    public void setDeviceID(Integer deviceID) {
        super.setDeviceID(deviceID);
    }

    public void update() throws java.sql.SQLException {
        super.update();
    }

    public void setDnpConfiguration(DNPConfiguration dnpConfiguration) {
        this.dnpConfiguration = dnpConfiguration;
    }

    public DNPConfiguration getDnpConfiguration() {
        return dnpConfiguration;
    }

}
