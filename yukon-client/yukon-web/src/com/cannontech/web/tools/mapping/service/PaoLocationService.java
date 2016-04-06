package com.cannontech.web.tools.mapping.service;

import java.util.Comparator;
import java.util.List;

import org.geojson.FeatureCollection;

import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.pao.definition.model.PaoTag;
import com.cannontech.common.pao.model.DistanceUnit;
import com.cannontech.common.pao.model.PaoDistance;
import com.cannontech.common.pao.model.PaoLocation;

public interface PaoLocationService {
    
    public enum Icon {
        METER_ELECTRIC,
        METER_WATER,
        METER_GAS,
        TRANSMITTER,
        RELAY,
        GENERIC_GREY,
        GENERIC_RED,
    }
    
    public enum FeatureProperty {
        
        PAO_IDENTIFIER("paoIdentifier"),
        ICON("icon");
        
        private final String keyName;
        
        FeatureProperty(String keyName) {
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
    
    public static final Comparator<PaoDistance> ON_DISTANCE = new Comparator<PaoDistance>() {
        @Override
        public int compare(PaoDistance o1, PaoDistance o2) {
            return Double.compare(o1.getDistance(), o2.getDistance());
        }
    };
    
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
     * Returns a list of {@link PaoDistance}s for paos that are within the specified distance to the 
     * supplied location. Optionally filtered by a {@link PaoTag}s.
     */
    List<PaoDistance> getNearbyLocations(PaoLocation location, double distance, DistanceUnit unit, PaoTag... tags);
    
    /**
     * Delete the location information for the specified pao.
     */
    public void deleteLocationForPaoId(int paoId);
}