package com.cannontech.sensus;

import java.util.Map;

import org.apache.log4j.Logger;

import com.cannontech.core.dao.DeviceDao;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.pao.DeviceClasses;
import com.cannontech.database.data.pao.PAOGroups;

public class ConfigurableDeviceLookup implements YukonDeviceLookup {
    private Map<Integer, String> deviceMapping;
    private DeviceDao deviceDao;
    private Logger log = Logger.getLogger(ConfigurableDeviceLookup.class);

    public Map<Integer, String> getDeviceMapping() {
        return deviceMapping;
    }

    public void setDeviceMapping(Map<Integer, String> deviceMapping) {
        this.deviceMapping = deviceMapping;
    }

    public LiteYukonPAObject getDeviceForRepId(int repId) {
        String deviceName = deviceMapping.get(repId);
        if (deviceName == null) {
            return null;
        }
        LiteYukonPAObject yukonPAObject = deviceDao.getLiteYukonPAObject(deviceName, 
                                                                         PAOGroups.CAT_DEVICE, 
                                                                         DeviceClasses.VIRTUAL, 
                                                                         PAOGroups.VIRTUAL_SYSTEM);
        if (yukonPAObject == null) {
            log.warn("Device '" + deviceName + "' could not be found for category device, class virtual, and type virtual_system.");
        }
        return yukonPAObject;
    }

    public boolean isDeviceConfigured(int repId) {
        return deviceMapping.containsKey(repId);
    }

    public void setDeviceDao(DeviceDao deviceDao) {
        this.deviceDao = deviceDao;
    }

}
