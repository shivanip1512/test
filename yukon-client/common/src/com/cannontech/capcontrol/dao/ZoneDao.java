package com.cannontech.capcontrol.dao;

import java.util.List;
import java.util.Map;

import com.cannontech.capcontrol.CapBankToZoneMapping;
import com.cannontech.capcontrol.PointToZoneMapping;
import com.cannontech.capcontrol.model.AbstractZone;
import com.cannontech.capcontrol.model.CapBankPointDelta;
import com.cannontech.capcontrol.model.CcEvent;
import com.cannontech.capcontrol.model.CymePaoPoint;
import com.cannontech.capcontrol.model.RegulatorToZoneMapping;
import com.cannontech.capcontrol.model.Zone;
import com.cannontech.common.model.Phase;
import com.cannontech.common.util.TimeRange;

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
     * This function will assign the passed in list of point ids to the zone.
     * It will remove any existing assigned points.
     * @param zoneId
     * @param pointids
     */
    public void updatePointToZoneMapping(AbstractZone abstractZone, List<PointToZoneMapping> pointsToZone);
    
    
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
     * Returns latest pre-op value for the specified point
     * @param bankId
     * @param pointId
     * @return preOpValue 
     */
    public Double getPreOpForPoint(Integer bankId, Integer pointId);
    
    /**
     * Returns latest delta value for the specified point
     * @param bankId
     * @param pointId
     * @return deltaValue
     */
    public Double getDeltaForPoint(Integer bankId, Integer pointId);
    
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
     * Returns the RegulatorToZoneMappings for the specified zone.
     */
    public List<RegulatorToZoneMapping> getRegulatorToZoneMappingsByZoneId(int zoneId);
    
    public List<CymePaoPoint> getTapPointsBySubBusId(int substationBusId);

    /**
     * Returns the latest cap bank events.
     */
    List<CcEvent> getLatestCapBankEvents(List<Integer> zoneIds, TimeRange range);
    
    /**
     * Returns the latest comm status events.
     */
    List<CcEvent> getLatestCommStatusEvents(int subBusId, TimeRange range);

}
