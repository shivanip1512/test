package com.cannontech.database.data.device;

import com.cannontech.common.device.config.dao.DeviceConfigurationDao;
import com.cannontech.common.device.config.model.DNPConfiguration;
import com.cannontech.common.device.config.model.DeviceConfiguration;
import com.cannontech.common.device.config.model.LightDeviceConfiguration;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonDevice;
import com.cannontech.database.db.DBCopiable;
import com.cannontech.spring.YukonSpringHook;

public class DNPBase extends AddressBase implements DBCopiable {
    private DNPConfiguration dnpConfiguration = null;

    public void add() throws java.sql.SQLException {
        super.add();

        Object[] configValues = { getPAObjectID(), getDnpConfiguration().getConfigurationId() };
        add("DeviceConfigurationDeviceMap", configValues);
    }
    
    public void retrieve() throws java.sql.SQLException {
        super.retrieve();
        
        DeviceConfigurationDao configurationDao = 
                YukonSpringHook.getBean("deviceConfigurationDao", DeviceConfigurationDao.class);
        YukonDevice device = new SimpleDevice(getPAObjectID(), PaoType.getForDbString(getPAOType()));
        
        LightDeviceConfiguration configuration = configurationDao.findConfigurationForDevice(device);
        if (configuration != null) {
            DeviceConfiguration deviceConfiguration = 
                configurationDao.getDeviceConfiguration(configuration.getConfigurationId());
            
            dnpConfiguration = configurationDao.getDnpConfiguration(deviceConfiguration);
        } else {
            dnpConfiguration = null;
        }
    }

    public void setDnpConfiguration(DNPConfiguration dnpConfiguration) {
        this.dnpConfiguration = dnpConfiguration;
    }

    public DNPConfiguration getDnpConfiguration() {
        return dnpConfiguration;
    }
}