package com.cannontech.cbc.dao;

import java.util.List;

import com.cannontech.cbc.model.Zone;

public interface ZoneDao {
    
    
    /**
     * Updates or inserts existing zone with the information in zone.
     * If the zone id is 0 it will insert, otherwise update.
     * 
     * @param zone
     */
    public void save(Zone zone);
    
    /**
     * Removes the Zone with zoneId from the database.
     * @param id
     */
    public boolean delete(int zoneId);
    
    /**
     * This function will assign the passed in list of Cap Bank ids to the zoneId.
     * It will remove any existing assigned banks.
     * @param zoneId
     * @param bankIds
     */
    public void updateCapBankAssignments(int zoneId, List<Integer> bankIds);
    
    /**
     * This function will assign the passed in list of point ids to the zoneId.
     * It will remove any existing assigned points.
     * @param zoneId
     * @param pointids
     */
    public void updatePointAssignments(int zoneId, List<Integer> pointids);
    
    
    /**
     * Returns the zone stored in the database with the zoneId.
     * @param zoneId
     * @return Zone
     */
    public Zone getZoneById(int zoneId);
    
    /**
     * Returns all Zones on a SubBus with the id subBusId.
     * @param subBusId
     * @return List<Zone>
     */
    public List<Zone> getZonesBySubBusId(int subBusId);
    
    /**
     * Return all Cap Banks attached to a zoneId.
     * @param zoneId
     * @return
     */
    public List<Integer> getCapBankIdsByZone(int zoneId);
    
    /**
     * Return all Points attached to a zoneId.
     * @param zoneId
     * @return
     */
    public List<Integer> getPointIdsByZone(int zoneId);
}
