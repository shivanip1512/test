package com.cannontech.web.common.mapping.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.config.MasterConfigBoolean;
import com.cannontech.common.config.MasterConfigString;
import com.cannontech.common.config.UnknownKeyException;
import com.cannontech.web.common.mapping.service.MappingService;


@Service
public class MappingServiceImpl implements MappingService {
    
    @Autowired private ConfigurationSource configSource;
    
    private final static String MAPPING_STREET_URL = "https://api.tiles.mapbox.com/v4/mapbox.streets/{z}/{x}/{y}.png?access_token=";
    private final static String MAPPING_SATELLITE_URL = "https://api.tiles.mapbox.com/v4/mapbox.satellite/{z}/{x}/{y}.png?access_token=";
    private final static String MAPPING_HYBRID_URL = "https://api.tiles.mapbox.com/v4/mapbox.streets-satellite/{z}/{x}/{y}.png?access_token=";
    private final static String MAPPING_DEV_KEY = "pk.eyJ1IjoiZWFzeXVrb25kZXYiLCJhIjoiY2lydzVjbnNyMGo3eHQxbmtidGVoNWt5bSJ9.ddhkDSTm2ONf47E9DVaNFw";
    private final static String MAPPING_PROD_KEY = "pk.eyJ1IjoiZWFzeXVrb24iLCJhIjoiY2lydzRobnI2MGlrcWZmbTZqYWloYXg5YSJ9.34csPYqVFCEKSvj6nirmgA";

    @Override
    public String getMappingUrl(String viewType) {
        boolean devMode = configSource.getBoolean(MasterConfigBoolean.DEVELOPMENT_MODE);
        boolean disableAnalytics = configSource.getBoolean(MasterConfigBoolean.DISABLE_ANALYTICS);
        String streetUrl = configSource.getString(MasterConfigString.MAP_DEVICES_STREET_URL, MAPPING_STREET_URL);
        String satelliteUrl = configSource.getString(MasterConfigString.MAP_DEVICES_SATELLITE_URL, MAPPING_SATELLITE_URL);
        String hybridUrl = configSource.getString(MasterConfigString.MAP_DEVICES_HYBRID_URL, MAPPING_HYBRID_URL);
        String devMappingKey = configSource.getString(MasterConfigString.MAP_DEVICES_KEY, MAPPING_DEV_KEY);
        String prodMappingKey = configSource.getString(MasterConfigString.MAP_DEVICES_KEY, MAPPING_PROD_KEY);
        
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
        
        if (viewType.equalsIgnoreCase("STREET")){
            mappingUrl = streetUrl + key;
        } else if (viewType.equalsIgnoreCase("SATELLITE")) {
            mappingUrl = satelliteUrl + key;
        } else if (viewType.equalsIgnoreCase("HYBRID")) {
            mappingUrl = hybridUrl + key;
        }
    
        return mappingUrl;
    }


}
