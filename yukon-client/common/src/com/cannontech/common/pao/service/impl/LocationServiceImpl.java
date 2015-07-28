package com.cannontech.common.pao.service.impl;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.events.loggers.EndpointEventLogService;
import com.cannontech.common.pao.dao.PaoLocationDao;
import com.cannontech.common.pao.service.LocationService;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;
import com.cannontech.yukon.IDatabaseCache;

public class LocationServiceImpl implements LocationService{
    @Autowired private EndpointEventLogService endpointEventLogService;
    @Autowired private GlobalSettingDao globalSettingDao;
    @Autowired private IDatabaseCache databaseCache;
    @Autowired private PaoLocationDao paoLocationDao;
    
    @Override
    public void deleteLocation(int deviceId, LiteYukonUser user) {
        boolean preserveLocation = globalSettingDao.getBoolean(GlobalSettingType.PRESERVE_ENDPOINT_LOCATION);
        if (!preserveLocation && paoLocationDao.getLocation(deviceId) != null) {
            LiteYukonPAObject pao = databaseCache.getAllPaosMap().get(deviceId);
            paoLocationDao.delete(deviceId);
            endpointEventLogService.locationRemoved(pao.getPaoName(), user);
        }
    }
}
