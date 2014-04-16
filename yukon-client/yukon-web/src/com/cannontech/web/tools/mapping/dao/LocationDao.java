package com.cannontech.web.tools.mapping.dao;

import java.util.Set;

import org.geojson.FeatureCollection;

import com.cannontech.common.pao.YukonPao;
import com.cannontech.web.tools.mapping.model.Location;

public interface LocationDao {

    /**
     * Get most recent location for each pao in the collection.
     * Paos without a location are ignored. If no paos have a
     * location, an empty set is returned.
     */
    public Set<Location> getLocations(Iterable<? extends YukonPao> paos);

    /**
     * Returns the most recent location for each pao in the given collection as GeoJSON. Paos
     * without a location are ignored. If no paos have a location, an empty set of features is
     * returned.
     * @param paos
     * @return a {@link FeatureCollection}
     */
    public FeatureCollection getLocationsAsGeoJson(Iterable<? extends YukonPao> paos);

    /** Get most recent location for PAObjectId. */
    public Location getLocation(int paoId);

    /** Saves the location to the database. */
    public void save(Location location);

    /** Saves all locations to the database. */
    public void saveAll(Iterable<Location> locations);

}