package com.cannontech.web.tools.mapping.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.geojson.Crs;
import org.geojson.Feature;
import org.geojson.FeatureCollection;
import org.geojson.Point;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.config.MasterConfigStringKeysEnum;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.pao.dao.PaoLocationDao;
import com.cannontech.common.pao.model.DistanceUnit;
import com.cannontech.common.pao.model.PaoLocation;
import com.cannontech.web.tools.mapping.service.PaoLocationService;
import com.google.common.collect.Maps;

public class PaoLocationServiceImpl implements PaoLocationService {

    @Autowired private PaoLocationDao paoLocationDao;

    private final String projection;

    @Autowired
    public PaoLocationServiceImpl(ConfigurationSource configSource) {
        projection = configSource.getString(MasterConfigStringKeysEnum.MAP_PROJECTION, "EPSG:4326");
    }
    
    @Override
    public FeatureCollection getFeatureCollection(Iterable<PaoLocation> locations) {
        
        FeatureCollection features = new FeatureCollection();
        // Set coordinate reference system for these locations.
        Map<String, Object> crsProperties = Maps.newHashMap();
        crsProperties.put("name", projection);
        Crs crs = new Crs();
        crs.setProperties(crsProperties);
        features.setCrs(crs);
        
        for (PaoLocation location : locations) {
            Feature feature = new Feature();
            // Feature "id" is paoId.
            feature.setId(Integer.toString(location.getPaoIdentifier().getPaoId()));
            Point point = new Point(location.getLongitude(), location.getLatitude());
            feature.setGeometry(point);
            // Set feature properties.
            feature.getProperties().put(FeaturePropertyType.PAO_IDENTIFIER.getKeyName(),
                    location.getPaoIdentifier());
            features.add(feature);
        }
        
        return features;
    }
    
    @Override
    public FeatureCollection getLocationsAsGeoJson(Iterable<? extends YukonPao> paos) {
        Set<PaoLocation> locations = paoLocationDao.getLocations(paos);
        return getFeatureCollection(locations);
    }
    
    @Override
    public List<PaoLocation> getNearbyLocations(PaoLocation location, double distance, DistanceUnit unit) {
        
        List<PaoLocation> locations = paoLocationDao.getAllLocations();
        List<PaoLocation> nearby = new ArrayList<>();
        for (PaoLocation current : locations) {
            if (location.distanceTo(current, unit) <= distance) nearby.add(current); 
        }
        
        return nearby;
    }
    
}