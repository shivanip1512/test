package com.cannontech.capcontrol.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.capcontrol.CapBankToZoneMapping;
import com.cannontech.capcontrol.PointToZoneMapping;
import com.cannontech.capcontrol.dao.ZoneDao;
import com.cannontech.capcontrol.exception.RootZoneExistsException;
import com.cannontech.capcontrol.model.CapBankPointDelta;
import com.cannontech.capcontrol.model.CcEvent;
import com.cannontech.capcontrol.model.Zone;
import com.cannontech.capcontrol.model.ZoneAssignmentCapBankRow;
import com.cannontech.capcontrol.model.ZoneAssignmentPointRow;
import com.cannontech.capcontrol.model.ZoneDto;
import com.cannontech.capcontrol.model.ZoneGangDto;
import com.cannontech.capcontrol.model.ZoneHierarchy;
import com.cannontech.capcontrol.model.ZoneSinglePhaseDto;
import com.cannontech.capcontrol.model.ZoneThreePhaseDto;
import com.cannontech.capcontrol.service.ZoneService;
import com.cannontech.core.dao.DBPersistentDao;
import com.cannontech.database.data.pao.PAOGroups;
import com.cannontech.database.data.pao.ZoneType;
import com.cannontech.enums.Phase;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.message.dispatch.message.DbChangeType;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;

public class ZoneServiceImpl implements ZoneService {

    private ZoneDao zoneDao;
    private DBPersistentDao dbPersistantDao;
    
    @Override
    @Transactional
    public boolean saveZone(ZoneDto zoneDto) {
        DbChangeType dbChangeType = DbChangeType.UPDATE;
        
        if(zoneDto.getZoneId() == null) {
            //We are creating a new zone.
            dbChangeType = DbChangeType.ADD;
            
            //If we are creating this as the root. Make sure one is not already on the subbus.
            if (zoneDto.getParentId() == null) {
                Zone rootZone = zoneDao.findParentZoneByBusId(zoneDto.getSubstationBusId());
                if (rootZone != null) {
                    throw new RootZoneExistsException();
                }
            }
        }
        
        Zone zone = new Zone();
        zone.setId(zoneDto.getZoneId());
        zone.setName(zoneDto.getName());
        zone.setParentId(zoneDto.getParentId());
        zone.setRegulators(zoneDto.getRegulators());
        zone.setSubstationBusId(zoneDto.getSubstationBusId());
        zone.setGraphStartPosition(zoneDto.getGraphStartPosition());
        zone.setZoneType(zoneDto.getZoneType());
        
        zoneDao.save(zone);
        //Sets the new Id if this was an insert.
        zoneDto.setZoneId(zone.getId());
        
        List<CapBankToZoneMapping> banksToZone = getCapBankToZoneMappingByDto(zoneDto);
        List<PointToZoneMapping> pointsToZone = getPointToZoneMappingByDto(zoneDto);
        
        zoneDao.updateCapBankToZoneMapping(zoneDto.getZoneId(), banksToZone);
        zoneDao.updatePointToZoneMapping(zoneDto.getZoneId(), pointsToZone);
        
        sendZoneChangeDbMessage(zone.getId(),dbChangeType);
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
        for (ZoneAssignmentCapBankRow bankRow : zoneDto.getBankAssignments()) {
        	int bankId = bankRow.getId();
        	int zoneId = zoneDto.getZoneId();
        	double position = bankRow.getGraphPositionOffset();
        	double dist = bankRow.getDistance();
        	CapBankToZoneMapping bank = new CapBankToZoneMapping(bankId, zoneId, position, dist);
        	banks.add(bank);
        }
        return banks;
    }
    
    private List<PointToZoneMapping> getPointToZoneMappingByDto(ZoneDto zoneDto) {
    	List<PointToZoneMapping> points = Lists.newArrayList();
        for (ZoneAssignmentPointRow pointRow : zoneDto.getPointAssignments()) {
        	int  pointId = pointRow.getId();
        	int zoneId = zoneDto.getZoneId();
        	double position = pointRow.getGraphPositionOffset();
        	double dist = pointRow.getDistance();
        	Phase phase = pointRow.getPhase();
        	PointToZoneMapping point = new PointToZoneMapping(pointId, zoneId, position, dist, phase);
        	points.add(point);
        }
        return points;
    }
    
    @Override 
    public List<Zone> getZonesBySubBusId(int subbusId) {
        List<Zone> zones = zoneDao.getZonesBySubBusId(subbusId);
        return zones;
    }
    
    private List<ZoneDto> getZoneDtoListFromZones(List<Zone> zones) {
        List<ZoneDto> zoneDtoList = Lists.newArrayList();
        for (Zone zone: zones) {
            ZoneDto zoneDto = null;
            if (zone.getZoneType() == ZoneType.GANG_OPERATED) {
                zoneDto = new ZoneGangDto(zone);
            } else if (zone.getZoneType() == ZoneType.SINGLE_PHASE) {
                zoneDto = new ZoneSinglePhaseDto(zone);
            } else if (zone.getZoneType() == ZoneType.THREE_PHASE) {
                zoneDto = new ZoneThreePhaseDto(zone);
            }
            zoneDtoList.add(zoneDto);
        }
        return zoneDtoList;
    }
    
    @Override
    public ZoneHierarchy getZoneHierarchyBySubBusId(int subbusId) {
        //Find parent zone and build lookup map
        ZoneDto root = null;
        List<Zone> zones = getZonesBySubBusId(subbusId);
        List<ZoneDto> zoneDtoList = getZoneDtoListFromZones(zones);
        Multimap<Integer, ZoneDto> childOfLookup = ArrayListMultimap.create();
        for (ZoneDto zoneDto : zoneDtoList) {
            if (zoneDto.getParentId() == null) {
                root = zoneDto;
            } else {
                childOfLookup.put(zoneDto.getParentId(), zoneDto);
            }
        }

        //Build Hierarchy
        ZoneHierarchy hierarchy = new ZoneHierarchy();
        hierarchy.setZone(root);
        //Recursive call to set children
        if (root != null) {
            setChildHierarchy(hierarchy, root.getZoneId(), childOfLookup);
        }

        return hierarchy;
    }

    private void setChildHierarchy(ZoneHierarchy hierarchy, Integer rootId, Multimap<Integer, ZoneDto> childOfLookup) {       
        List<ZoneHierarchy> childZones = Lists.newArrayList();
        
        Iterable<ZoneDto> childGroups = childOfLookup.get(rootId);
        for (ZoneDto zoneDto : childGroups) {
            ZoneHierarchy childHierarchy = new ZoneHierarchy();
            childHierarchy.setZone(zoneDto);
            
            setChildHierarchy(childHierarchy, zoneDto.getZoneId(), childOfLookup);
            
            childZones.add(childHierarchy);
        }

        hierarchy.setChildZones(childZones);
    }
    
    @Override
    public Zone getZoneById(int zoneId) {
        return zoneDao.getZoneById(zoneId);
    }
    
    @Override
    public List<Integer> getUnassignedCapBankIdsForSubBusId(int subBusId) {
        return zoneDao.getUnassignedCapBankIdsBySubBusId(subBusId);
    }
    
    @Override
    public List<Integer> getCapBankIdsForSubBusId(int subBusId) {
        return zoneDao.getCapBankIdsBySubBusId(subBusId);
    }
    
    @Override
    public List<Integer> getCapBankIdsForZoneId(int zoneId) {        
        return zoneDao.getCapBankIdsByZone(zoneId);
    }
    
    @Override
    public List<Integer> getPointIdsForZoneId(int zoneId) {        
        return zoneDao.getPointIdsByZone(zoneId);
    }
    
    @Override
    public List<CapBankToZoneMapping> getCapBankToZoneMapping(int zoneId) {
    	return zoneDao.getBankToZoneMappingByZoneId(zoneId);
    }
    
    @Override
    public List<PointToZoneMapping> getPointToZoneMapping(int zoneId) {
    	return zoneDao.getPointToZoneMappingByZoneId(zoneId);
    }
    
    @Override
    public void handleFeederUpdate(int feederId) {
        zoneDao.cleanUpBanksByFeeder(feederId);
    }
    
    @Override
    public void handleSubstationBusUpdate(int subBusId) {
        zoneDao.cleanUpBanksBySubBus(subBusId);
    }
    
    @Override
    public void unassignBanksByFeeder(int feederId) {
        zoneDao.removeBankToZoneMappingByFeederId(feederId);
    }
    
    @Override
    public void unassignBank(int bankId) {
        zoneDao.removeBankToZoneMapping(bankId);
    }
    
    @Override
    public List<CcEvent> getLatestEvents(int zoneId,int subBusId, int rowLimit) {
        return zoneDao.getLatestEvents(zoneId, subBusId, rowLimit);
    }
    
    private void sendZoneChangeDbMessage(int zoneId, DbChangeType dbChangeType) {
        DBChangeMsg msg = new DBChangeMsg(zoneId,
                                          DBChangeMsg.CHANGE_IVVC_ZONE,
                                          PAOGroups.STRING_CAT_CAPCONTROL,
                                          "Zone",
                                          dbChangeType);
        
        dbPersistantDao.processDBChange(msg);
    }
    
    public List<CapBankPointDelta> getAllPointDeltasForBankIds(List<Integer> bankIds) {
        return zoneDao.getAllPointDeltasForBankIds(bankIds);
    }
    
    public List<Integer> getMonitorPointsForBank(int bankId) {
        return zoneDao.getMonitorPointsForBank(bankId);
    }
    
    public Map<Integer, Phase> getMonitorPointsForBankAndPhase(int bankId) {
        return zoneDao.getMonitorPointsForBankAndPhase(bankId);
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
