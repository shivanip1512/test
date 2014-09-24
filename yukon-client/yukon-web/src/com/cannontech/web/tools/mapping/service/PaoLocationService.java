package com.cannontech.web.tools.mapping.service;

import java.util.List;

import org.geojson.FeatureCollection;

import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.pao.model.DistanceUnit;
import com.cannontech.common.pao.model.PaoLocation;

public interface PaoLocationService {
    
    enum FeaturePropertyType {
        
        PAO_IDENTIFIER("paoIdentifier");
        
        private final String keyName;
        
        FeaturePropertyType(String keyName) {
            this.keyName = keyName;
        }
        
        public String getKeyName() {
            return keyName;
        }
        
        @Override
        public String toString() {
            return keyName;
        }
    }
    
    /**
     * Returns the most recent location for each pao in the given collection as GeoJSON. Paos
     * without a location are ignored. If no paos have a location, an empty set of features is
     * returned.
     * @param paos
     * @return a {@link FeatureCollection}
     */
    FeatureCollection getLocationsAsGeoJson(Iterable<? extends YukonPao> paos);
    
    /**
     * Converts the collection of locations to a GeoJSON {@link FeatureCollection}.
     * @param paos
     * @return a {@link FeatureCollection}
     */
    FeatureCollection getFeatureCollection(Iterable<PaoLocation> locations);
    
    /**
     * Returns a list of {@PaoLocation} that are within the specified distance to the 
     * supplied location.
     */
    List<PaoLocation> getNearbyLocations(PaoLocation location, double distance, DistanceUnit unit);
    
}