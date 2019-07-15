package com.cannontech.web.tools.mapping.service;

import java.util.List;

import org.geojson.FeatureCollection;

import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.pao.model.PaoLocation;
import com.cannontech.common.pao.model.PaoLocationDetails;

public interface PaoLocationService {
    
    public enum Icon {
        METER_ELECTRIC,
        METER_PLC_ELECTRIC,
        METER_WIFI_ELECTRIC,
        METER_WATER,
        METER_GAS,
        TRANSMITTER,
        RELAY,
        PLC_LCR,
        LCR,
        THERMOSTAT,
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
     * Delete the location information for the specified pao.
     */
    public void deleteLocationForPaoId(int paoId);
    /**
     * Save the location information for the specified pao.
     */
    void saveLocationForPaoId(int paoId, double latitude, double longitude);
    /**
     * Get location detail of all paoIds
     */
    public List<PaoLocationDetails> getLocationDetailsForPaos (List<Integer> paoIds);
}