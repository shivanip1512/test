package com.cannontech.web.capcontrol.ivvc.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.capcontrol.CapBankToZoneMapping;
import com.cannontech.capcontrol.PointToZoneMapping;
import com.cannontech.cbc.dao.ZoneDao;
import com.cannontech.cbc.model.Zone;
import com.cannontech.cbc.model.ZoneHierarchy;
import com.cannontech.core.dao.DBPersistentDao;
import com.cannontech.database.data.pao.PAOGroups;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.message.dispatch.message.DbChangeType;
import com.cannontech.web.capcontrol.ivvc.models.ZoneAssignmentRow;
import com.cannontech.web.capcontrol.ivvc.models.ZoneDto;
import com.cannontech.web.capcontrol.ivvc.service.ZoneService;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;

public class ZoneServiceImpl implements ZoneService {

    private ZoneDao zoneDao;
    private DBPersistentDao dbPersistantDao;
    
    @Override
    @Transactional
    public boolean createZone(ZoneDto zoneDto) {
        String name = zoneDto.getName();
        int parentId = zoneDto.getParentZoneId();
        int regulatorId = zoneDto.getRegulatorId();
        int substationBusId = zoneDto.getSubstationBusId();
        
        Zone zone = new Zone();
        zone.setName(name);
        
        if (parentId == -1) {
            zone.setParentId(null);
        } else {
            zone.setParentId(parentId);
        }
        
        zone.setRegulatorId(regulatorId);
        zone.setSubstationBusId(substationBusId);
        
        List<CapBankToZoneMapping> banksToZone = getCapBankToZoneMappingByDto(zoneDto);
        List<PointToZoneMapping> pointsToZone = getPointToZoneMappingByDto(zoneDto);
        
        zoneDao.save(zone);
        zoneDao.updateCapBankToZoneMapping(zoneDto.getZoneId(), banksToZone);
        zoneDao.updatePointToZoneMapping(zoneDto.getZoneId(), pointsToZone);

        sendZoneChangeDbMessage(zone.getId(), DbChangeType.ADD);
        
        return true;
    }
    
    @Override
    @Transactional
    public boolean updateZone(ZoneDto zoneDto) {
        int id = zoneDto.getZoneId();
        String name = zoneDto.getName();
        int parentId = zoneDto.getParentZoneId();
        int regulatorId = zoneDto.getRegulatorId();
        int substationBusId = zoneDto.getSubstationBusId();
        
        Zone zone = new Zone();
        zone.setId(id);
        zone.setName(name);
        
        if (parentId == -1) {
            zone.setParentId(null);
        } else {
            zone.setParentId(parentId);
        }
        
        zone.setRegulatorId(regulatorId);
        zone.setSubstationBusId(substationBusId);
        
        List<CapBankToZoneMapping> banksToZone = getCapBankToZoneMappingByDto(zoneDto);
        List<PointToZoneMapping> pointsToZone = getPointToZoneMappingByDto(zoneDto);
        
        zoneDao.save(zone);
        zoneDao.updateCapBankToZoneMapping(zoneDto.getZoneId(), banksToZone);
        zoneDao.updatePointToZoneMapping(zoneDto.getZoneId(), pointsToZone);
        
        sendZoneChangeDbMessage(zone.getId(), DbChangeType.UPDATE);
        
        return true;
    }
    
    @Override
    @Transactional
    public boolean deleteZone(int zoneId) {
        zoneDao.delete(zoneId);      
        sendZoneChangeDbMessage(zoneId, DbChangeType.DELETE);
        return true;
    }
    
    private List<CapBankToZoneMapping> getCapBankToZoneMappingByDto(ZoneDto zoneDto) {
    	List<CapBankToZoneMapping> banks = Lists.newArrayList();
        for (ZoneAssignmentRow bankRow : zoneDto.getBankAssignments()) {
        	int bankId = bankRow.getId();
        	int zoneId = zoneDto.getZoneId();
        	double order = bankRow.getOrder();
        	CapBankToZoneMapping bank = new CapBankToZoneMapping(bankId, zoneId, order);
        	banks.add(bank);
        }
        return banks;
    }
    
    private List<PointToZoneMapping> getPointToZoneMappingByDto(ZoneDto zoneDto) {
    	List<PointToZoneMapping> points = Lists.newArrayList();
        for (ZoneAssignmentRow pointRow : zoneDto.getPointAssignments()) {
        	int pointId = pointRow.getId();
        	int zoneId = zoneDto.getZoneId();
        	double order = pointRow.getOrder();
        	PointToZoneMapping point = new PointToZoneMapping(pointId, zoneId, order);
        	points.add(point);
        }
        return points;
    }
    
    @Override 
    public List<Zone> getZonesBySubBusId(int subbusId) {
        List<Zone> zones = zoneDao.getZonesBySubBusId(subbusId);
        return zones;
    }
    
    @Override
    public ZoneHierarchy getZoneHierarchyBySubBusId(int subbusId) {
        //Find parent zone and build lookup map
        Zone root = null;
        List<Zone> zones = getZonesBySubBusId(subbusId);
        Multimap<Integer, Zone> childOfLookup = ArrayListMultimap.create();
        for (Zone zone : zones) {
            if (zone.getParentId() == null) {
                root = zone;
            } else {
                childOfLookup.put(zone.getParentId(), zone);
            }
        }

        //Build Hierarchy
        ZoneHierarchy hierarchy = new ZoneHierarchy();
        hierarchy.setZone(root);
        //Recursive call to set children
        if (root != null) {
            setChildHierarchy(hierarchy, root.getId(), childOfLookup);
        }

        return hierarchy;
    }

    private void setChildHierarchy(ZoneHierarchy hierarchy, Integer rootId, Multimap<Integer,Zone> childOfLookup) {       
        List<ZoneHierarchy> childZones = Lists.newArrayList();
        
        Iterable<Zone> childGroups = childOfLookup.get(rootId);
        for (Zone zone : childGroups) {
            ZoneHierarchy childHierarchy = new ZoneHierarchy();
            childHierarchy.setZone(zone);
            
            setChildHierarchy(childHierarchy, zone.getId(), childOfLookup);
            
            childZones.add(childHierarchy);
        }

        hierarchy.setChildZones(childZones);
    }
    
    @Override
    public Zone getZoneById(int zoneId) {
        return zoneDao.getZoneById(zoneId);
    }
    
    public List<Integer> getCapBankIdsForZoneId(int zoneId) {        
        return zoneDao.getCapBankIdsByZone(zoneId);
    }
    
    public List<Integer> getPointIdsForZoneId(int zoneId) {        
        return zoneDao.getPointIdsByZone(zoneId);
    }
    
    @Override
    public List<CapBankToZoneMapping> getCapBankToZoneMapping(int zoneId) {
    	return zoneDao.getBankToZoneMappingById(zoneId);
    }
    
    @Override
    public List<PointToZoneMapping> getPointToZoneMapping(int zoneId) {
    	return zoneDao.getPointToZoneMappingById(zoneId);
    }
    
     private void sendZoneChangeDbMessage(int zoneId, DbChangeType dbChangeType) {
        DBChangeMsg msg = new DBChangeMsg(zoneId,
                                          DBChangeMsg.CHANGE_IVVC_ZONE,
                                          PAOGroups.STRING_CAT_CAPCONTROL,
                                          "Zone",
                                          dbChangeType);
        
        dbPersistantDao.processDBChange(msg);
    }
    
    @Autowired
    public void setZoneDao(ZoneDao zoneDao) {
        this.zoneDao = zoneDao;
    }
    
    @Autowired
    public void setDbPersistantDao(DBPersistentDao dbPersistantDao) {
        this.dbPersistantDao = dbPersistantDao;
    }
}
