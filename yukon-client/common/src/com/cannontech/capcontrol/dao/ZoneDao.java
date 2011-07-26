package com.cannontech.capcontrol.dao;

import java.util.List;
import java.util.Map;

import com.cannontech.capcontrol.CapBankToZoneMapping;
import com.cannontech.capcontrol.PointToZoneMapping;
import com.cannontech.capcontrol.model.CapBankPointDelta;
import com.cannontech.capcontrol.model.CcEvent;
import com.cannontech.capcontrol.model.Zone;
import com.cannontech.enums.Phase;

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
     * Returns the zone with the regulatorId attached to it.
     * 
     * throws OrphanedRegulatorException if there is no zone associtated with this regulator.
     * 
     * @param regulatorId
     * @return
     */
    public Zone getZoneByRegulatorId(int regulatorId);
    
    /**
     * Returns Parent Zone for Substation Id.
     * 
     * Returns null if there is no parent on the Substation bus.
     * 
     * @param subBusId
     * @return
     */
    public Zone findParentZoneByBusId(int subBusId);
 
    /**
     * Returns a list of all unassigned capBankIds on the subBusId.
     * @param subBusId
     * @return
     */
    public List<Integer> getUnassignedCapBankIdsBySubBusId(int subBusId);
    
    /**
     * Returns a list of all assigned capBankIds on the subBusId.
     * @param subBusId
     * @return
     */
    public List<Integer> getCapBankIdsBySubBusId(int subBusId);
    
    /**
     * Return all Cap Banks attached to a zoneId.
     * @param zoneId
     * @return
     */
    public List<Integer> getCapBankIdsByZone(int zoneId);
    
    /**
     * Returns all Voltage Points assigned to a zone.
     * @return
     */
    public List<Integer> getAllUsedPointIds();

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
	public List<CapBankToZoneMapping> getBankToZoneMappingByZoneId(int zoneId);
	
	/**
     * Return the PointToZoneMapping object for a zone.
     * @param zoneId
     * @return
     */
	public List<PointToZoneMapping> getPointToZoneMappingByZoneId(int zoneId);
	
	/**
	 * Finds all Cap Banks on the feeder and compares the Banks assigned to zones. 
	 * Removes any assignments that are missing.
	 * @param feederId
	 */
	public void cleanUpBanksByFeeder(int feederId);
	
	/**
     * Finds all Cap Banks on the Sub Bus and compares the Banks assigned to zones. 
     * Removes any assignments that are missing.
     * @param subBusId
     */
	public void cleanUpBanksBySubBus(int subBusId);
	
	/**
	 * Removes to all bankIds on Feeder with the Id passed in from the Bank to Zone Mapping Table.
	 * @param feederId
	 */
	public void removeBankToZoneMappingByFeederId(int feederId);
	
	/**
	 * Removes any mapping to the passed in bankId from the Bank to Zone Mapping Table.
	 * @param bankId
	 */
	public void removeBankToZoneMapping(int bankId);

	/**
	 * Returns a list of all CapBankPointDeltas for the given list of bankIds.
	 * @param bankIds
	 * @return
	 */
    public List<CapBankPointDelta> getAllPointDeltasForBankIds(List<Integer> bankIds);
    
    /**
     * Returns a list of Point Ids for the given Cap Bank Id.
     * @param bankId
     * @return
     */
    public List<Integer> getMonitorPointsForBank(int bankId);

    /**
     * Returns a Map of point Id to Phase for the given Cap Bank Id
     * @param bankId
     * @return
     */
    public Map<Integer, Phase> getMonitorPointsForBankAndPhase(int bankId);

    /**
     * Returns the latest CCEvents for the zone and subBus passed in. 
     * It will limit the return by rowLimit
     * @param zoneId
     * @param subBusId
     * @param rowLimit
     * @return
     */
    public List<CcEvent> getLatestEvents(int zoneId, int subBusId, int rowLimit);
}
