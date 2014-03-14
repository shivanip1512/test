package com.cannontech.database.data.device;

import java.util.List;

import com.cannontech.common.device.config.dao.DeviceConfigurationDao;
import com.cannontech.common.device.config.model.DNPConfiguration;
import com.cannontech.common.device.config.model.DeviceConfiguration;
import com.cannontech.common.device.config.model.LightDeviceConfiguration;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonDevice;
import com.cannontech.database.db.DBCopiable;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.message.dispatch.message.DbChangeType;
import com.cannontech.spring.YukonSpringHook;
import com.google.common.collect.Lists;

public class DNPBase extends AddressBase implements DBCopiable {
    private DNPConfiguration dnpConfiguration = null;

    @Override
    public void add() throws java.sql.SQLException {
        super.add();

        Object[] configValues = { getPAObjectID(), getDnpConfiguration().getConfigurationId() };
        add("DeviceConfigurationDeviceMap", configValues);
    }
    
    @Override
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
    
    @Override
    public DBChangeMsg[] getDBChangeMsgs(DbChangeType dbChangeType) {
        if (dbChangeType == DbChangeType.ADD) {
            // We have to alert the config manager that a configuration-to-device mapping exists!
            List<DBChangeMsg> dbChangeMsgs = Lists.newArrayList(super.getDBChangeMsgs(dbChangeType));
    
            dbChangeMsgs.add(new DBChangeMsg(getPAObjectID(), 
                                             DBChangeMsg.CHANGE_CONFIG_DB, 
                                             DBChangeMsg.CAT_DEVICE_CONFIG, 
                                             DBChangeMsg.OBJ_DEVICE, 
                                             dbChangeType));
            
            DBChangeMsg[] dbChange = new DBChangeMsg[dbChangeMsgs.size()];
            return dbChangeMsgs.toArray(dbChange);
        }
        
        return super.getDBChangeMsgs(dbChangeType);
    }

    public void setDnpConfiguration(DNPConfiguration dnpConfiguration) {
        this.dnpConfiguration = dnpConfiguration;
    }

    public DNPConfiguration getDnpConfiguration() {
        return dnpConfiguration;
    }
}