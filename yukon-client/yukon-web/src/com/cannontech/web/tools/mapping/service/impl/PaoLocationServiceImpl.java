package com.cannontech.web.tools.mapping.service.impl;

import java.util.ArrayList;
import java.util.Collections;
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
import com.cannontech.common.pao.definition.dao.PaoDefinitionDao;
import com.cannontech.common.pao.definition.model.PaoTag;
import com.cannontech.common.pao.model.DistanceUnit;
import com.cannontech.common.pao.model.PaoDistance;
import com.cannontech.common.pao.model.PaoLocation;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.mbean.ServerDatabaseCache;
import com.cannontech.web.tools.mapping.service.PaoLocationService;
import com.google.common.collect.Maps;

public class PaoLocationServiceImpl implements PaoLocationService {

    @Autowired private ServerDatabaseCache cache;
    @Autowired private PaoLocationDao paoLocationDao;
    @Autowired private PaoDefinitionDao paoDefinitionDao;

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
    public List<PaoDistance> getNearbyLocations(PaoLocation location, double distance, DistanceUnit unit, PaoTag... tags) {
        
        List<PaoLocation> locations = paoLocationDao.getAllLocations();
        if (tags != null) {
            for (PaoTag tag : tags) {
                locations = paoDefinitionDao.filterPaosForTag(locations, tag);
            }
        }
        
        List<PaoDistance> nearby = new ArrayList<>();
        for (PaoLocation current : locations) {
            if (location.equals(current)) continue;
            double distanceTo = location.distanceTo(current, unit);
            if (distanceTo <= distance) {
                LiteYukonPAObject pao = cache.getAllPaosMap().get(current.getPaoIdentifier().getPaoId());
                nearby.add(PaoDistance.of(pao, distanceTo, unit, current)); 
            }
        }
        
        Collections.sort(nearby, onDistance);
        
        return nearby;
    }
    
}