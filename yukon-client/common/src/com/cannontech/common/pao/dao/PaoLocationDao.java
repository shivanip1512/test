package com.cannontech.common.pao.dao;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.pao.model.LocationData;
import com.cannontech.common.pao.model.PaoLocation;
import com.cannontech.common.pao.model.PaoLocationDetails;
import com.cannontech.common.rfn.message.RfnIdentifier;
import com.cannontech.common.rfn.message.location.Origin;

public interface PaoLocationDao {
    
    /**
     * Get most recent location for each pao in the collection.
     * Paos without a location are ignored. If no paos have a
     * location, an empty set is returned.
     */
    Set<PaoLocation> getLocations(Iterable<? extends YukonPao> paos);
    
    /**
     * Get most recent location for each device id.
     * Paos without a location are ignored. If no paos have a
     * location, an empty set is returned.
     */
    Set<PaoLocation> getLocations(Set<Integer> paoIds);
    
    /** Returns the location for the pao or null if the pao does not have a location. */
    PaoLocation getLocation(int paoId);
    
    /** Saves the location to the database. */
    void save(PaoLocation location);
    
    List<PaoLocation> getAllLocations();

    /**
     * Deletes location
     */
    void delete(int paoId);

    /**
     * Deletes locations
     */
    void delete(Origin origin);

    void save(List<PaoLocation> location);

    /**
     * Get all locations for the origin
     */
    List<PaoLocation> getLocations(Origin origin);

    /**
     * Get location details for all paoIds
     */
    List<PaoLocationDetails> getPaoLocationDetails(List<Integer> paoIds);

    /**
     * Returns locations for devices linked to gateways
     */
    List<PaoLocation> getLocationsByGateway(Set<Integer> gatewayIds);

    /**
     * Returns locations for devices linked to gateways that do not have locations
     */
    List<SimpleDevice> getDevicesWithoutLocationByGateway(List<Integer> gatewayIds);

    /**
     * Returns locations data for all paoTypes based on index.
     */
    List<LocationData> getLocationDetailForPaoType(Iterable<PaoType> paoTypes, int startIndex, int endIndex);
}