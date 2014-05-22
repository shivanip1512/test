package com.cannontech.sensus;

import org.apache.log4j.Logger;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.pao.PaoType;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.database.data.lite.LiteYukonPAObject;

public class OneDeviceForAllLookup implements YukonDeviceLookup {
    private DeviceDao deviceDao;
    private Logger log = YukonLogManager.getLogger(ConfigurableDeviceLookup.class);
    private String deviceName;

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }
    @Override
    public LiteYukonPAObject getDeviceForRepId(int repId) {
        LiteYukonPAObject yukonPAObject = deviceDao.getLiteYukonPAObject(deviceName, PaoType.VIRTUAL_SYSTEM);
        if (yukonPAObject == null) {
            log.warn("Device '" + deviceName + "' could not be found for category device, class virtual, and type virtual_system.");
        }
        return yukonPAObject;
    }
    @Override
    public boolean isDeviceConfigured(int repId) {
        return true;
    }
    public void setDeviceDao(DeviceDao deviceDao) {
        this.deviceDao = deviceDao;
    }

}
