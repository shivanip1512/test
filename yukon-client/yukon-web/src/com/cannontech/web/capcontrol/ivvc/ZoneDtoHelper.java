package com.cannontech.web.capcontrol.ivvc;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.capcontrol.CapBankToZoneMapping;
import com.cannontech.capcontrol.PointToZoneMapping;
import com.cannontech.capcontrol.model.AbstractZone;
import com.cannontech.capcontrol.model.AbstractZoneNotThreePhase;
import com.cannontech.capcontrol.model.Zone;
import com.cannontech.capcontrol.model.ZoneAssignmentCapBankRow;
import com.cannontech.capcontrol.model.ZoneAssignmentPointRow;
import com.cannontech.capcontrol.service.ZoneService;
import com.cannontech.cbc.cache.CapControlCache;
import com.cannontech.cbc.cache.FilterCacheFactory;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.PointDao;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.pao.ZoneType;
import com.cannontech.common.model.Phase;
import com.cannontech.message.capcontrol.streamable.CapBankDevice;
import com.google.common.collect.Lists;

public class ZoneDtoHelper {
    
    @Autowired private ZoneService zoneService;
    @Autowired private PaoDao paoDao;
    @Autowired private PointDao pointDao;
    @Autowired private FilterCacheFactory filterCacheFactory;
    
    public AbstractZone getAbstractZoneFromZoneId(int zoneId, LiteYukonUser user) {
        
        Zone zone = zoneService.getZoneById(zoneId);
        AbstractZone zoneDto = getAbstractZoneFromZone(zone, user);
        
        return zoneDto;
    }
    
    public AbstractZone getAbstractZoneFromZone(Zone zone, LiteYukonUser user) {
        
        CapControlCache cache = filterCacheFactory.createUserAccessFilteredCache(user);
        AbstractZone zoneDto = AbstractZone.create(zone);
        
        //Add Bank Assignments
        if (zone.getId() != null) {
            List<ZoneAssignmentCapBankRow> bankAssignments = getBankAssignmentList(zone.getId(), cache);
            zoneDto.setBankAssignments(bankAssignments);
        }
        
        //Add Point Assignments
        if (zone.getId() != null) {
            List<ZoneAssignmentPointRow> pointAssignments = getPointAssignmentList(zone.getId());
            zoneDto.setPointAssignments(pointAssignments);
        }
        
        return zoneDto;
    }
    
    public List<ZoneType> getAvailableChildZoneTypesFromParentZoneType(ZoneType parentZoneType) {
        
        List<ZoneType> availableZoneTypes = Lists.newArrayListWithExpectedSize(3);
        if (parentZoneType == ZoneType.GANG_OPERATED) {
            availableZoneTypes =
                Lists.newArrayList(ZoneType.GANG_OPERATED, ZoneType.THREE_PHASE, ZoneType.SINGLE_PHASE);
        } else if (parentZoneType == ZoneType.THREE_PHASE) {
            availableZoneTypes.add(ZoneType.THREE_PHASE);
            availableZoneTypes.add(ZoneType.SINGLE_PHASE);
        } else {
            availableZoneTypes.add(ZoneType.SINGLE_PHASE);
        }
        
        return availableZoneTypes;
    }
    
    public List<Phase> getAvailableChildPhasesFromParentZone(AbstractZone parentZone) {
        
        List<Phase> availableZonePhases = Lists.newArrayListWithExpectedSize(3);
        if (parentZone.getZoneType() == ZoneType.SINGLE_PHASE) {
            Phase parentPhase = ((AbstractZoneNotThreePhase)parentZone).getRegulator().getPhase();
            availableZonePhases.add(parentPhase);
        } else {
            availableZonePhases.addAll(Lists.newArrayList(Phase.getRealPhases()));
        }
        
        return availableZonePhases;
    }
    
    private List<ZoneAssignmentCapBankRow> getBankAssignmentList(int zoneId, CapControlCache cache) {
        
        List<ZoneAssignmentCapBankRow> rows = Lists.newArrayList();
        List<CapBankToZoneMapping> banksToZone = zoneService.getCapBankToZoneMapping(zoneId);
        
        for (CapBankToZoneMapping bankToZone : banksToZone) {
            ZoneAssignmentCapBankRow row = getBankAssignmentFromMapping(bankToZone, cache);
            rows.add(row);
        }
        
        return rows;
    }
    
    public ZoneAssignmentCapBankRow getBankAssignmentFromMapping(CapBankToZoneMapping bankToZone, CapControlCache cache) {
        
        int bankId = bankToZone.getDeviceId();
        CapBankDevice bank = cache.getCapBankDevice(bankId);
        LiteYukonPAObject controller = paoDao.getLiteYukonPAO(bank.getControlDeviceID());
        
        ZoneAssignmentCapBankRow row = new ZoneAssignmentCapBankRow();
        row.setId(bankId);        
        row.setName(bank.getCcName());
        row.setDevice(controller.getPaoName());
        row.setGraphPositionOffset(bankToZone.getGraphPositionOffset());
        row.setDistance(bankToZone.getDistance());
        
        return row;
    }
    
    private List<ZoneAssignmentPointRow> getPointAssignmentList(int zoneId) {
        
        List<PointToZoneMapping> pointsToZone = zoneService.getPointToZoneMapping(zoneId);
        
        List<ZoneAssignmentPointRow> rows = Lists.newArrayList();
        for (PointToZoneMapping pointToZone : pointsToZone) {
            ZoneAssignmentPointRow row = getPointAssignmentFromMapping(pointToZone);
            rows.add(row);
        }
        
        return rows;
    }
    
    public ZoneAssignmentPointRow getPointAssignmentFromMapping(PointToZoneMapping pointToZone) {
        
        int pointId = pointToZone.getPointId();
        LitePoint point = pointDao.getLitePoint(pointId);
        LiteYukonPAObject pao = paoDao.getLiteYukonPAO(point.getPaobjectID());
        
        ZoneAssignmentPointRow row = new ZoneAssignmentPointRow();
        row.setId(pointId);        
        row.setName(point.getPointName());
        row.setDevice(pao.getPaoName());
        row.setGraphPositionOffset(pointToZone.getGraphPositionOffset());
        row.setDistance(pointToZone.getDistance());
        row.setPhase(pointToZone.getPhase());
        row.setIgnore(pointToZone.isIgnore());
        
        return row;
    }
    
}