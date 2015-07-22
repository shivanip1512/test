package com.cannontech.common.pao.dao;

import java.util.List;
import java.util.Set;

import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.pao.model.PaoLocation;

public interface PaoLocationDao {
    
    /**
     * Get most recent location for each pao in the collection.
     * Paos without a location are ignored. If no paos have a
     * location, an empty set is returned.
     */
    Set<PaoLocation> getLocations(Iterable<? extends YukonPao> paos);
    
    /** Returns the location for the pao or null if the pao does not have a location. */
    PaoLocation getLocation(int paoId);
    
    /** Saves the location to the database. */
    void save(PaoLocation location);
    
    List<PaoLocation> getAllLocations();

    /**
     * Deletes location
     */
    void delete(int paoId);
}