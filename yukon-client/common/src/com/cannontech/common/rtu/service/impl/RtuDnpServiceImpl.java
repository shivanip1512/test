package com.cannontech.common.rtu.service.impl;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.device.config.dao.DeviceConfigurationDao;
import com.cannontech.common.device.config.model.DNPConfiguration;
import com.cannontech.common.device.config.model.DeviceConfiguration;
import com.cannontech.common.device.config.model.HeartbeatConfiguration;
import com.cannontech.common.device.config.model.LightDeviceConfiguration;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.rtu.model.RtuDnp;
import com.cannontech.common.rtu.service.RtuDnpService;
import com.cannontech.core.dao.DBPersistentDao;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.database.data.device.DNPBase;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.db.DBPersistent;
import com.cannontech.yukon.IDatabaseCache;

public class RtuDnpServiceImpl implements RtuDnpService {

    @Autowired private IDatabaseCache dbCache;
    @Autowired private DBPersistentDao dbPersistentDao;
    @Autowired private DeviceConfigurationDao configurationDao;
    @Autowired private DeviceDao deviceDao;
    
    @Override
    public RtuDnp getRtuDnp(int id) {
        RtuDnp rtu = new RtuDnp();
        LiteYukonPAObject pao = dbCache.getAllPaosMap().get(id);
        rtu.setName(pao.getPaoName());
        rtu.setId(pao.getPaoIdentifier().getPaoId());
        rtu.setPaoType(pao.getPaoType());
        rtu.setDisableFlag(Boolean.parseBoolean(pao.getDisableFlag()));
        DBPersistent dbPersistent = dbPersistentDao.retrieveDBPersistent(pao);
        DNPBase dnpBase = (DNPBase) dbPersistent;
        rtu.setDeviceScanRateMap(dnpBase.getDeviceScanRateMap());
        rtu.setDeviceWindow(dnpBase.getDeviceWindow());
        rtu.setDeviceAddress(dnpBase.getDeviceAddress());
        rtu.setDeviceDirectCommSettings(dnpBase.getDeviceDirectCommSettings());
        LightDeviceConfiguration config =
            configurationDao.findConfigurationForDevice(new SimpleDevice(pao.getPaoIdentifier()));
        DeviceConfiguration deviceConfig =
            configurationDao.getDeviceConfiguration(config.getConfigurationId());
        DNPConfiguration dnp = configurationDao.getDnpConfiguration(deviceConfig);
        rtu.setDnpConfig(dnp);
        HeartbeatConfiguration heartbeat  = configurationDao.getHeartbeatConfiguration(deviceConfig);
        rtu.setHeartbeatConfig(heartbeat);
        rtu.setChildDevices(deviceDao.getChildDevices(id));
        return rtu;
    }
}
