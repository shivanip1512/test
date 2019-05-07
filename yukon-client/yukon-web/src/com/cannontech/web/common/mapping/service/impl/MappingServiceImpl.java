package com.cannontech.web.common.mapping.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.config.MasterConfigBoolean;
import com.cannontech.common.config.MasterConfigString;
import com.cannontech.common.config.UnknownKeyException;
import com.cannontech.web.common.mapping.MappingType;
import com.cannontech.web.common.mapping.service.MappingService;


@Service
public class MappingServiceImpl implements MappingService {
    
    @Autowired private ConfigurationSource configSource;
    
    private final static String mappingStreetUrl = "https://api.tiles.mapbox.com/v4/mapbox.streets/{z}/{x}/{y}.png?access_token=";
    private final static String mappingSatelliteUrl = "https://api.tiles.mapbox.com/v4/mapbox.satellite/{z}/{x}/{y}.png?access_token=";
    private final static String mappingHybridUrl = "https://api.tiles.mapbox.com/v4/mapbox.streets-satellite/{z}/{x}/{y}.png?access_token=";
    private final static String mappingElevationUrl = "https://{a-d}.tiles.mapbox.com/v4/mapbox.mapbox-terrain-v2/{z}/{x}/{y}.mvt?access_token=";
    private final static String mappingDevKey = "pk.eyJ1IjoiZWFzeXVrb25kZXYiLCJhIjoiY2lydzVjbnNyMGo3eHQxbmtidGVoNWt5bSJ9.ddhkDSTm2ONf47E9DVaNFw";
    private final static String mappingProdKey = "pk.eyJ1IjoiZWFzeXVrb24iLCJhIjoiY2p0aGVsajNqMmRpdDRibzN1aXczNDJiciJ9.pkeDA9vCymtYlubad4d4_A";

    @Override
    public String getMappingUrl(String viewType) {
        boolean devMode = configSource.getBoolean(MasterConfigBoolean.DEVELOPMENT_MODE);
        boolean disableAnalytics = configSource.getBoolean(MasterConfigBoolean.DISABLE_ANALYTICS);
        String streetUrl = configSource.getString(MasterConfigString.MAP_DEVICES_STREET_URL, mappingStreetUrl);
        String satelliteUrl = configSource.getString(MasterConfigString.MAP_DEVICES_SATELLITE_URL, mappingSatelliteUrl);
        String hybridUrl = configSource.getString(MasterConfigString.MAP_DEVICES_HYBRID_URL, mappingHybridUrl);
        String elevationUrl = configSource.getString(MasterConfigString.MAP_DEVICES_ELEVATION_URL, mappingElevationUrl);
        String devMappingKey = configSource.getString(MasterConfigString.MAP_DEVICES_KEY, mappingDevKey);
        String prodMappingKey = configSource.getString(MasterConfigString.MAP_DEVICES_KEY, mappingProdKey);
        
        if (StringUtils.isBlank(viewType)) {
            throw new IllegalArgumentException("viewType is required");
        }
        
        String mappingUrl = "";
        String key = prodMappingKey;

        try {
            boolean mapDevicesDevMode = configSource.getRequiredBoolean(MasterConfigBoolean.MAP_DEVICES_DEV_MODE);
            if (mapDevicesDevMode) {
                key = devMappingKey;
            }
        } catch (UnknownKeyException e) {
            if (devMode || disableAnalytics) {
                key = devMappingKey;
            }
        }
        
        if (viewType.equals(MappingType.STREET.name())) {
            mappingUrl = streetUrl + key;
        } else if (viewType.equals(MappingType.SATELLITE.name())) {
            mappingUrl = satelliteUrl + key;
        } else if (viewType.equals(MappingType.HYBRID.name())) {
            mappingUrl = hybridUrl + key;
        } else if (viewType.equals(MappingType.ELEVATION.name())) {
            mappingUrl = elevationUrl + key;
        }
    
        return mappingUrl;
    }


}