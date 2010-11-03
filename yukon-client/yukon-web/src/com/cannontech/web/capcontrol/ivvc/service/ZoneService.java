package com.cannontech.web.capcontrol.ivvc.service;

import java.util.List;

import com.cannontech.capcontrol.CapBankToZoneMapping;
import com.cannontech.capcontrol.PointToZoneMapping;
import com.cannontech.cbc.model.Zone;
import com.cannontech.cbc.model.ZoneHierarchy;
import com.cannontech.web.capcontrol.ivvc.models.ZoneDto;

public interface ZoneService {
    
    /**
     * Inserts or Updates the passed in Zone. if the Zone Id is null it will insert, otherwise attempt to update.
     * 
     * Throws ParentZoneExistsException if a parent already exists on the Substation Bus.
     * 
     * @param zoneDto
     * @return
     */
    public boolean saveZone(ZoneDto zoneDto);
    public boolean deleteZone(int zoneId);
    
    public List<Zone> getZonesBySubBusId(int subbusId);
    
    public ZoneHierarchy getZoneHierarchyBySubBusId(int subbusId);
    public Zone getZoneById(int zoneId);
    
    public List<Integer> getCapBankIdsForZoneId(int zoneId);
    public List<Integer> getPointIdsForZoneId(int zoneId);
    
    public List<CapBankToZoneMapping> getCapBankToZoneMapping(int zoneId);
    public List<PointToZoneMapping> getPointToZoneMapping(int zoneId);
}
