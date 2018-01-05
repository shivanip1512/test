package com.cannontech.database.data.capcontrol;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import com.cannontech.common.device.config.dao.DeviceConfigurationDao;
import com.cannontech.common.device.config.model.DeviceConfiguration;
import com.cannontech.common.device.config.model.LightDeviceConfiguration;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonDevice;
import com.cannontech.database.db.device.DeviceParent;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.message.dispatch.message.DbChangeType;
import com.cannontech.spring.YukonSpringHook;
import com.google.common.collect.Lists;

public class CapBankControllerLogical extends CapBankController {
    private DeviceConfiguration deviceConfiguration = null;

    private DeviceParent deviceParent = null;

    public CapBankControllerLogical() {
        super(PaoType.CBC_LOGICAL);
    }
    
    public Integer getParentDeviceId() {
        return getDeviceParent().getParentId();
    }

    public void setParentDeviceId(Integer parentId) {
        getDeviceParent().setParentId(parentId);
    }

    @Override
    public void add() throws SQLException {
        super.add();
        getDeviceParent().add();
        if (getDeviceConfiguration() != null) {
            Object[] configValues = { getPAObjectID(), getDeviceConfiguration().getConfigurationId() };
            add("DeviceConfigurationDeviceMap", configValues);
        }
    }

    @Override
    public void delete() throws SQLException {
        if (!isPartialDelete) {
            getDeviceParent().delete();
        }
        super.delete();
    }

    @Override
    public void retrieve() throws SQLException {
        super.retrieve();
        getDeviceParent().retrieve();
        DeviceConfigurationDao configurationDao = 
                YukonSpringHook.getBean("deviceConfigurationDao", DeviceConfigurationDao.class);
        YukonDevice device = new SimpleDevice(getPAObjectID(), getPaoType());
        
        LightDeviceConfiguration configuration = configurationDao.findConfigurationForDevice(device);
        if (configuration != null) {
            deviceConfiguration = configurationDao.getDeviceConfiguration(configuration.getConfigurationId());
        } else {
            deviceConfiguration = null;
        }
    }

    @Override
    public void setDbConnection(Connection conn) {
        super.setDbConnection(conn);
        getDeviceParent().setDbConnection(conn);
    }

    @Override
    public void setDeviceID(Integer deviceID) {
        super.setDeviceID(deviceID);
        getDeviceParent().setDeviceId(deviceID);
    }

    @Override
    public void update() throws SQLException {
        super.update();
        getDeviceParent().update();
    }

    public DeviceParent getDeviceParent() {
        if (deviceParent == null)
            deviceParent = new DeviceParent();
        return deviceParent;
    }

    public void setDeviceParent(DeviceParent deviceParent) {
        this.deviceParent = deviceParent;
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
                                             DbChangeType.UPDATE));
            
            DBChangeMsg[] dbChange = new DBChangeMsg[dbChangeMsgs.size()];
            return dbChangeMsgs.toArray(dbChange);
        }
        
        return super.getDBChangeMsgs(dbChangeType);
    }
    
    public void setDeviceConfiguration(DeviceConfiguration deviceConfiguration) {
        this.deviceConfiguration = deviceConfiguration;
    }

    public DeviceConfiguration getDeviceConfiguration() {
        return deviceConfiguration;
    }
}