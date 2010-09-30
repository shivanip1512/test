package com.cannontech.web.capcontrol.ivvc.service;

import java.util.List;

import com.cannontech.cbc.model.Zone;
import com.cannontech.cbc.model.ZoneHierarchy;
import com.cannontech.web.capcontrol.ivvc.models.ZoneDto;

public interface ZoneService {
    
    public boolean createZone(ZoneDto zoneDto);
    public boolean updateZone(ZoneDto zoneDto);
    public boolean deleteZone(int zoneId);
    
    public List<Zone> getZonesBySubBusId(int subbusId);
    
    public ZoneHierarchy getZoneHierarchyBySubBusId(int subbusId);
    public Zone getZoneById(int zoneId);
    
    public List<Integer> getCapBankIdsForZoneId(int zoneId);
    public List<Integer> getPointIdsForZoneId(int zoneId);
}
