package com.cannontech.common.pao.dao;

import java.util.Set;

import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.pao.model.PaoLocation;

public interface PaoLocationDao {
    /**
     * Get most recent location for each pao in the collection.
     * Paos without a location are ignored. If no paos have a
     * location, an empty set is returned.
     */
    public Set<PaoLocation> getLocations(Iterable<? extends YukonPao> paos);

    /** Get most recent location for PAObjectId. */
    public PaoLocation getLocation(int paoId);

    /** Saves the location to the database. */
    public void save(PaoLocation location);

    /** Saves all locations to the database. */
    public void saveAll(Iterable<PaoLocation> locations);

}