package com.cannontech.capcontrol.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.capcontrol.CapBankToZoneMapping;
import com.cannontech.capcontrol.PointToZoneMapping;
import com.cannontech.capcontrol.dao.CcMonitorBankListDao;
import com.cannontech.capcontrol.dao.ZoneDao;
import com.cannontech.capcontrol.exception.RootZoneExistsException;
import com.cannontech.capcontrol.model.AbstractZone;
import com.cannontech.capcontrol.model.CapBankPointDelta;
import com.cannontech.capcontrol.model.CcEvent;
import com.cannontech.capcontrol.model.RegulatorToZoneMapping;
import com.cannontech.capcontrol.model.VoltageLimitedDeviceInfo;
import com.cannontech.capcontrol.model.Zone;
import com.cannontech.capcontrol.model.ZoneAssignmentCapBankRow;
import com.cannontech.capcontrol.model.ZoneAssignmentPointRow;
import com.cannontech.capcontrol.model.ZoneHierarchy;
import com.cannontech.capcontrol.service.ZoneService;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.model.Phase;
import com.cannontech.common.pao.PaoCategory;
import com.cannontech.common.util.TimeRange;
import com.cannontech.message.DbChangeManager;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.message.dispatch.message.DbChangeType;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;

public class ZoneServiceImpl implements ZoneService {
    @Autowired private ZoneDao zoneDao;
    @Autowired private DbChangeManager dbChangeManager;
    @Autowired private CcMonitorBankListDao ccMonitorBankListDao;
    
    private static final Logger log = YukonLogManager.getLogger(ZoneServiceImpl.class);
    
    @Override
    @Transactional
    public boolean saveZone(AbstractZone abstractZone) {
        DbChangeType dbChangeType = DbChangeType.UPDATE;
        
        List<Integer> oldRegulatorIds = Lists.newArrayList();
        if(abstractZone.getZoneId() == null) {
            //We are creating a new zone.
            dbChangeType = DbChangeType.ADD;
            
            //If we are creating this as the root. Make sure one is not already on the subbus.
            if (abstractZone.getParentId() == null) {
                Zone rootZone = zoneDao.findParentZoneByBusId(abstractZone.getSubstationBusId());
                if (rootZone != null) {
                    throw new RootZoneExistsException();
                }
            }
        } else {
            //get list of regulators associated with zone before update
            List<RegulatorToZoneMapping> oldMappings = zoneDao.getRegulatorToZoneMappingsByZoneId(abstractZone.getZoneId());
            for(RegulatorToZoneMapping oldMap : oldMappings) {
                oldRegulatorIds.add(oldMap.getRegulatorId());
            }
        }
        
        Zone zone = getNewZoneFromAbstractZone(abstractZone);
        
        zoneDao.save(zone);
        //Sets the new Id if this was an insert.
        abstractZone.setZoneId(zone.getId());
        
        List<CapBankToZoneMapping> banksToZone = getCapBankToZoneMappingByDto(abstractZone);
        List<PointToZoneMapping> pointsToZone = getPointToZoneMappingByDto(abstractZone);
        
        zoneDao.updateCapBankToZoneMapping(abstractZone.getZoneId(), banksToZone);
        zoneDao.updatePointToZoneMapping(abstractZone, pointsToZone);
        for(RegulatorToZoneMapping regToZone : zone.getRegulators()) {
            Integer regId = regToZone.getRegulatorId();
            if(oldRegulatorIds.contains(regId)) {
                ccMonitorBankListDao.updateRegulatorPhase(regId, regToZone.getPhase());
                oldRegulatorIds.remove(regId);
            } else {
                ccMonitorBankListDao.addRegulatorPoint(regId, regToZone.getPhase(), abstractZone.getSubstationBusId());
            }
        }
        //remove regulators no longer attached
        if(!oldRegulatorIds.isEmpty()) {
            ccMonitorBankListDao.removeDevices(oldRegulatorIds);
        }
        
        sendZoneChangeDbMessage(zone.getId(),dbChangeType);
        return true;
    }
    
    @Override
    @Transactional
    public boolean deleteZone(int zoneId) {
        ccMonitorBankListDao.removePointsByZone(zoneId);
        zoneDao.delete(zoneId);
        sendZoneChangeDbMessage(zoneId, DbChangeType.DELETE);
        return true;
    }
    
    @Override
    public void saveVoltagePointInfo(AbstractZone abstractZone, List<VoltageLimitedDeviceInfo> deviceInfos) {
        List<PointToZoneMapping> pointToZoneMappings = getPointToZoneMappingByDto(abstractZone);
        for (PointToZoneMapping mapping : pointToZoneMappings) {
            VoltageLimitedDeviceInfo deviceInfo = findDeviceInfoWithPointId(deviceInfos, mapping.getPointId());
            if (deviceInfo == null) {
               log.error("couldn't find device in VoltageLimitedDeviceInfo list");
            } else {
                mapping.setPhase(deviceInfo.getPhase());
                mapping.setIgnore(deviceInfo.isIgnore());
            }
        }

        zoneDao.updatePointToZoneMapping(abstractZone, pointToZoneMappings);
        ccMonitorBankListDao.updateDeviceInfo(deviceInfos);
        dbChangeManager.processDbChange(abstractZone.getZoneId(),
                                        DBChangeMsg.CHANGE_IVVC_ZONE,
                                        PaoCategory.CAPCONTROL.getDbString(),
                                        "Zone",
                                        DbChangeType.UPDATE);
    }

    private VoltageLimitedDeviceInfo findDeviceInfoWithPointId(List<VoltageLimitedDeviceInfo> deviceInfos, int pointId) {
        for (VoltageLimitedDeviceInfo deviceInfo : deviceInfos) {
            if (deviceInfo.getPointId() == pointId) {
                return deviceInfo;
            }
        }
        return null;
    }
    
    private List<CapBankToZoneMapping> getCapBankToZoneMappingByDto(AbstractZone abstractZone) {
    	List<CapBankToZoneMapping> banks = Lists.newArrayList();
        for (ZoneAssignmentCapBankRow bankRow : abstractZone.getBankAssignments()) {
        	int bankId = bankRow.getId();
        	int zoneId = abstractZone.getZoneId();
        	double position = bankRow.getGraphPositionOffset();
        	double dist = bankRow.getDistance();
        	CapBankToZoneMapping bank = new CapBankToZoneMapping(bankId, zoneId, position, dist);
        	banks.add(bank);
        }
        return banks;
    }
    
    private List<PointToZoneMapping> getPointToZoneMappingByDto(AbstractZone abstractZone) {
    	List<PointToZoneMapping> points = Lists.newArrayList();
        for (ZoneAssignmentPointRow pointRow : abstractZone.getPointAssignments()) {
        	int  pointId = pointRow.getId();
        	int zoneId = abstractZone.getZoneId();
        	double position = pointRow.getGraphPositionOffset();
        	double dist = pointRow.getDistance();
        	Phase phase = pointRow.getPhase();
        	boolean ignore = pointRow.isIgnore();
        	PointToZoneMapping point = new PointToZoneMapping(pointId, zoneId, position, dist, phase, ignore);
        	points.add(point);
        }
        return points;
    }
    
    @Override 
    public List<Zone> getZonesBySubBusId(int subbusId) {
        List<Zone> zones = zoneDao.getZonesBySubBusId(subbusId);
        return zones;
    }
    
    private List<AbstractZone> getAbstractZoneListFromZones(List<Zone> zones) {
        List<AbstractZone> abstractZoneList = Lists.newArrayList();
        for (Zone zone: zones) {
            AbstractZone abstractZone = AbstractZone.create(zone);
            abstractZoneList.add(abstractZone);
        }
        return abstractZoneList;
    }

    private Zone getNewZoneFromAbstractZone(AbstractZone abstractZone) {
        Zone zone = new Zone();
        zone.setId(abstractZone.getZoneId());
        zone.setName(abstractZone.getName());
        zone.setParentId(abstractZone.getParentId());

        List<RegulatorToZoneMapping> regulators = abstractZone.getRegulatorsList();
        zone.setRegulators(regulators);
        zone.setSubstationBusId(abstractZone.getSubstationBusId());
        zone.setGraphStartPosition(abstractZone.getGraphStartPosition());
        zone.setZoneType(abstractZone.getZoneType());
        return zone;
    }
    
    @Override
    public ZoneHierarchy getZoneHierarchyBySubBusId(int subbusId) {
        //Find parent zone and build lookup map
        AbstractZone root = null;
        List<Zone> zones = getZonesBySubBusId(subbusId);
        List<AbstractZone> abstractZoneList = getAbstractZoneListFromZones(zones);
        Multimap<Integer, AbstractZone> childOfLookup = ArrayListMultimap.create();
        for (AbstractZone abstractZone : abstractZoneList) {
            if (abstractZone.getParentId() == null) {
                root = abstractZone;
            } else {
                childOfLookup.put(abstractZone.getParentId(), abstractZone);
            }
        }
        
        if (root == null) {
            return null;
        }

        //Build Hierarchy
        ZoneHierarchy hierarchy = new ZoneHierarchy();
        hierarchy.setZone(root);
        setChildHierarchy(hierarchy, root.getZoneId(), childOfLookup);
        return hierarchy;
    }

    private void setChildHierarchy(ZoneHierarchy hierarchy, Integer rootId, Multimap<Integer, AbstractZone> childOfLookup) {       
        List<ZoneHierarchy> childZones = Lists.newArrayList();
        
        Iterable<AbstractZone> childGroups = childOfLookup.get(rootId);
        for (AbstractZone abstractZone : childGroups) {
            ZoneHierarchy childHierarchy = new ZoneHierarchy();
            childHierarchy.setZone(abstractZone);
            
            setChildHierarchy(childHierarchy, abstractZone.getZoneId(), childOfLookup);
            
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
    public List<Integer> getAllUsedPointIds() {        
        return zoneDao.getAllUsedPointIds();
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
    
    private void sendZoneChangeDbMessage(int zoneId, DbChangeType dbChangeType) {
        dbChangeManager.processDbChange(zoneId,
                                        DBChangeMsg.CHANGE_IVVC_ZONE,
                                        PaoCategory.CAPCONTROL.getDbString(),
                                        "Zone",
                                        dbChangeType);
    }
    
    @Override
    public List<CapBankPointDelta> getAllPointDeltasForBankIds(List<Integer> bankIds) {
        return zoneDao.getAllPointDeltasForBankIds(bankIds);
    }
    
    @Override
    public List<Integer> getMonitorPointsForBank(int bankId) {
        return zoneDao.getMonitorPointsForBank(bankId);
    }
    
    @Override
    public Map<Integer, Phase> getMonitorPointsForBankAndPhase(int bankId) {
        return zoneDao.getMonitorPointsForBankAndPhase(bankId);
    }
    
    @Override
    public List<Integer> getRegulatorsForZone(int id) {
        
        List<Integer> regulatorIds = new ArrayList<>();
            
        Zone zone = getZoneById(id);
            
        List<RegulatorToZoneMapping> mappings = zone.getRegulators();
        
        for (RegulatorToZoneMapping mapping : mappings) {
            regulatorIds.add(mapping.getRegulatorId());
        }
        
        return regulatorIds;
    }
    
    @Override
    public List<CcEvent> getLatestCapBankEvents(List<Integer> zoneIds, TimeRange range) {
        return zoneDao.getLatestCapBankEvents(zoneIds, range);
    }

    @Override
    public List<CcEvent> getLatestCommStatusEvents(int subBusId, TimeRange range) {
        return zoneDao.getLatestCommStatusEvents(subBusId, range);
    }
}
