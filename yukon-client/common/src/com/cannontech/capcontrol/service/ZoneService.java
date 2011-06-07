package com.cannontech.capcontrol.service;

import java.util.List;
import java.util.Map;

import com.cannontech.capcontrol.CapBankToZoneMapping;
import com.cannontech.capcontrol.PointToZoneMapping;
import com.cannontech.capcontrol.model.CapBankPointDelta;
import com.cannontech.capcontrol.model.CcEvent;
import com.cannontech.capcontrol.model.Zone;
import com.cannontech.capcontrol.model.AbstractZone;
import com.cannontech.capcontrol.model.ZoneHierarchy;
import com.cannontech.enums.Phase;

public interface ZoneService {
    
    /**
     * Inserts or Updates the passed in Zone. if the Zone Id is null it will insert, otherwise attempt to update.
     * 
     * Throws ParentZoneExistsException if a parent already exists on the Substation Bus.
     * 
     * @param zoneDto
     * @return
     */
    public boolean saveZone(AbstractZone zoneDto);
    public boolean deleteZone(int zoneId);
    
    public List<Zone> getZonesBySubBusId(int subbusId);

    public ZoneHierarchy getZoneHierarchyBySubBusId(int subbusId);
    public Zone getZoneById(int zoneId);

    /**
     * Returns list of Cap Bank Ids on the given subBusId that are not assigned to any zone.
     * @param subBusId
     * @return
     */
    public List<Integer> getUnassignedCapBankIdsForSubBusId(int subBusId);
    
    /**
     * Returns a list of Cap Bank Ids assigned to any zone by subBusId.
     * @param subBusId
     * @return
     */
    public List<Integer> getCapBankIdsForSubBusId(int subBusId);
    
    /**
     * Returns a list of Cap Bank Ids assigned to the zone with zoneId.
     * @param zoneId
     * @return
     */
    public List<Integer> getCapBankIdsForZoneId(int zoneId);
    
    /**
     * Returns a list of pointIds assigned to the Zone with zoneId.
     * @param zoneId
     * @return
     */
    public List<Integer> getPointIdsForZoneId(int zoneId);
    
    /**
     * Returns a list of CapBankToZoneMapping objects by zoneId.
     * @param zoneId
     * @return
     */
    public List<CapBankToZoneMapping> getCapBankToZoneMapping(int zoneId);
    
    /**
     * Returns a list of PointToZoneMapping objects by zoneId.
     * @param zoneId
     * @return
     */
    public List<PointToZoneMapping> getPointToZoneMapping(int zoneId);
    
    /**
     * Handles any changes needed for the Zones based on an update to feeder that has been updated.
     * This will handle removing deleted capbanks or adding new ones.
     * @param feederId
     */
    public void handleFeederUpdate(int feederId);
    /**
     * Handles any changes needed for the Zones based on an update to the Substation Bus.
     * This will handle the case of assigning or unassigning a feeder from the Substation Bus.
     * @param subBusId
     */
    public void handleSubstationBusUpdate(int subBusId);
    
    /**
     * Removes all banks on the feeder from any Zone they are assigned to.
     * @param feederId
     */
    public void unassignBanksByFeeder(int feederId);
    
    /**
     * Removes the bank with the id bankId from any Zone it is assigned to.
     * @param bankId
     */
    public void unassignBank(int bankId);
    
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
    List<CcEvent> getLatestEvents(int zoneId, int subBusId, int rowLimit);
}
