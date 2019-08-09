package com.cannontech.common.pao.service;

import java.util.Comparator;
import java.util.List;

import com.cannontech.common.pao.definition.model.PaoTag;
import com.cannontech.common.pao.model.DistanceUnit;
import com.cannontech.common.pao.model.PaoDistance;
import com.cannontech.common.pao.model.PaoLocation;
import com.cannontech.database.data.lite.LiteYukonUser;

public interface LocationService {

    void deleteLocation(int deviceId, LiteYukonUser user);
    
    /**
     * Save the location information
     */
    public void saveLocation(PaoLocation paoLocation, String paoName, LiteYukonUser user);
    
    public static final Comparator<PaoDistance> ON_DISTANCE = new Comparator<PaoDistance>() {
        @Override
        public int compare(PaoDistance o1, PaoDistance o2) {
            return Double.compare(o1.getDistance(), o2.getDistance());
        }
    };
    /**
     * Returns a list of {@link PaoDistance}s for paos that are within the specified distance to the 
     * supplied location. Optionally filtered by a {@link PaoTag}s.
     */
    List<PaoDistance> getNearbyLocations(PaoLocation location, double distance, DistanceUnit unit, List<PaoTag> tags);
    
    /**
     * Searches a list supplied locations for paos that are within the specified distance to the
     * supplied location. Optionally filtered by a {@link PaoTag}s.
     */

    List<PaoDistance> getNearbyLocations(List<PaoLocation> locations, PaoLocation location, double distance,
            DistanceUnit unit, List<PaoTag> tags);
}
