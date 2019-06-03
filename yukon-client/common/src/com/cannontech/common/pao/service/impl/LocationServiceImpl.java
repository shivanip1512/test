package com.cannontech.common.pao.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.events.loggers.EndpointEventLogService;
import com.cannontech.common.pao.dao.PaoLocationDao;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDao;
import com.cannontech.common.pao.definition.model.PaoTag;
import com.cannontech.common.pao.model.DistanceUnit;
import com.cannontech.common.pao.model.GPS;
import com.cannontech.common.pao.model.PaoDistance;
import com.cannontech.common.pao.model.PaoLocation;
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
    @Autowired private PaoDefinitionDao paoDefinitionDao;
    
    @Override
    public void deleteLocation(int deviceId, LiteYukonUser user) {
        boolean preserveLocation = globalSettingDao.getBoolean(GlobalSettingType.PRESERVE_ENDPOINT_LOCATION);
        if (!preserveLocation && paoLocationDao.getLocation(deviceId) != null) {
            LiteYukonPAObject pao = databaseCache.getAllPaosMap().get(deviceId);
            paoLocationDao.delete(deviceId);
            endpointEventLogService.locationRemoved(pao.getPaoName(), user);
        }
    }
    
    @Override
    public List<PaoDistance> getNearbyLocations(PaoLocation location, double distance, DistanceUnit unit, List<PaoTag> tags) {
        
        List<PaoLocation> locations = paoLocationDao.getAllLocations();
        return getNearbyLocations(locations, location, distance, unit, tags);
        
    }
    
    @Override
    public List<PaoDistance> getNearbyLocations(List<PaoLocation> locations, PaoLocation location, double distance, DistanceUnit unit,  List<PaoTag> tags) {
        
        if (tags != null) {
            for (PaoTag tag : tags) {
                locations = paoDefinitionDao.filterPaosForTag(locations, tag);
            }
        }
        
        List<PaoDistance> nearby = new ArrayList<>();
        for (PaoLocation current : locations) {
            if (location.equals(current)) {
                continue;
            }
            double distanceTo = location.distanceTo(current, unit);
            if (distanceTo <= distance) {
                LiteYukonPAObject pao = databaseCache.getAllPaosMap().get(current.getPaoIdentifier().getPaoId());
                nearby.add(PaoDistance.of(pao, distanceTo, unit, current)); 
            }
        }
        
        Collections.sort(nearby, ON_DISTANCE);
        
        return nearby;
    }

    /**
     * Returns GPS object if latitude and longitude contains number else throw IllegalArgumentException
     */
    public static GPS getValidLocationFormat(String latitude, String longitude) {
        boolean isValidLatitude = NumberUtils.isNumber(latitude);
        boolean isValidLongitude = NumberUtils.isNumber(longitude);
        if (!(isValidLatitude || isValidLongitude)) {
            throw new IllegalArgumentException("Latitude and Longitude must be Numeric.");
        } else if (!isValidLatitude) {
            throw new IllegalArgumentException("Latitude must be Numeric.");
        } else if (!isValidLongitude) {
            throw new IllegalArgumentException("Longitude must be Numeric.");
        }
        GPS gps = new GPS();
        gps.setLatitude(Double.valueOf(latitude));
        gps.setLongitude(Double.valueOf(longitude));
        return gps;
    }
}
