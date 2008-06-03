package com.cannontech.common.bulk.mapper;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.device.YukonDevice;
import com.cannontech.common.util.ObjectMapper;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.database.data.lite.LiteYukonPAObject;

public final class LiteYukonPAObjectToYukonDeviceMapper implements ObjectMapper<LiteYukonPAObject, YukonDevice> {
    private DeviceDao deviceDao = null;

    @Autowired
    public void setDeviceDao(DeviceDao deviceDao) {
        this.deviceDao = deviceDao;
    }        
    
    public YukonDevice map(LiteYukonPAObject from)
            throws ObjectMappingException {

        YukonDevice device = deviceDao.getYukonDevice(from);
        return device;

    }
}