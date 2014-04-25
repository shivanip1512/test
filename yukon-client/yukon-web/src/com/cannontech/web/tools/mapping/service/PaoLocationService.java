package com.cannontech.web.tools.mapping.service;

import org.geojson.FeatureCollection;

import com.cannontech.common.pao.YukonPao;

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
}
