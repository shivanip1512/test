package com.cannontech.cbc.dao;

import java.util.List;

import com.cannontech.capcontrol.CapBankToZoneMapping;
import com.cannontech.capcontrol.PointToZoneMapping;
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
    public void updateCapBankToZoneMapping(int zoneId, List<CapBankToZoneMapping> banksToZone);
    
    /**
     * This function will assign the passed in list of point ids to the zoneId.
     * It will remove any existing assigned points.
     * @param zoneId
     * @param pointids
     */
    public void updatePointToZoneMapping(int zoneId, List<PointToZoneMapping> pointsToZone);
    
    
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
     * Returns Parent Zone for Substation Id.
     * 
     * Returns null if there is no parent on the Substation bus.
     * 
     * @param subBusId
     * @return
     */
    public Zone getParentZoneByBusId(int subBusId);
    
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

	/**
     * Return the CapBankToZoneMapping object for a zone.
     * @param zoneId
     * @return
     */
	public List<CapBankToZoneMapping> getBankToZoneMappingById(int zoneId);
	
	/**
     * Return the PointToZoneMapping object for a zone.
     * @param zoneId
     * @return
     */
	public List<PointToZoneMapping> getPointToZoneMappingById(int zoneId);
}
