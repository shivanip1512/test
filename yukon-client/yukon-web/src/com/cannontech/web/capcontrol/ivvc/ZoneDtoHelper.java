package com.cannontech.web.capcontrol.ivvc;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.capcontrol.CapBankToZoneMapping;
import com.cannontech.capcontrol.PointToZoneMapping;
import com.cannontech.capcontrol.model.Zone;
import com.cannontech.capcontrol.model.ZoneAssignmentCapBankRow;
import com.cannontech.capcontrol.model.ZoneAssignmentPointRow;
import com.cannontech.capcontrol.model.ZoneDto;
import com.cannontech.capcontrol.model.ZoneGangDto;
import com.cannontech.capcontrol.model.ZoneSinglePhaseDto;
import com.cannontech.capcontrol.model.ZoneThreePhaseDto;
import com.cannontech.capcontrol.service.ZoneService;
import com.cannontech.cbc.cache.CapControlCache;
import com.cannontech.cbc.cache.FilterCacheFactory;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.PointDao;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.pao.ZoneType;
import com.cannontech.yukon.cbc.CapBankDevice;
import com.google.common.collect.Lists;

public class ZoneDtoHelper {
    private ZoneService zoneService;
    private PaoDao paoDao;
    private PointDao pointDao;
    private FilterCacheFactory filterCacheFactory;
    
    public ZoneDto getNewZoneDtoFromZoneType(ZoneType zoneType) {
        ZoneDto zoneDto = null;
        switch(zoneType) {
            case GANG_OPERATED: {
                zoneDto = new ZoneGangDto();
                break;
            } 
            case SINGLE_PHASE: {                        
                zoneDto = new ZoneSinglePhaseDto();
                break;
            }
            case THREE_PHASE: {
                zoneDto = new ZoneThreePhaseDto();
                break;
            }
        }
        return zoneDto;
    }
    
    public ZoneDto getZoneDtoFromZoneId(int zoneId, LiteYukonUser user) {
        Zone zone = zoneService.getZoneById(zoneId);
        ZoneDto zoneDto = getZoneDtoFromZone(zone, user);
        return zoneDto;
    }
    
    public ZoneDto getZoneDtoFromZone(Zone zone, LiteYukonUser user) {
        CapControlCache cache = filterCacheFactory.createUserAccessFilteredCache(user);
        
        ZoneDto zoneDto = null;
        if (zone.getZoneType() == ZoneType.GANG_OPERATED) {
            zoneDto = new ZoneGangDto(zone);
        } else if (zone.getZoneType() == ZoneType.THREE_PHASE) {
            zoneDto = new ZoneThreePhaseDto(zone);
        } else if (zone.getZoneType() == ZoneType.SINGLE_PHASE) {
            zoneDto = new ZoneSinglePhaseDto(zone);
        }
        
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
        row.setType("bank");
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
        row.setType("point");
        row.setId(pointId);        
        row.setName(point.getPointName());
        row.setDevice(pao.getPaoName());
        row.setGraphPositionOffset(pointToZone.getGraphPositionOffset());
        row.setDistance(pointToZone.getDistance());
        row.setPhase(pointToZone.getPhase());
        
        return row;
    }

    @Autowired
    public void setFilterCacheFactory(FilterCacheFactory filterCacheFactory) {
        this.filterCacheFactory = filterCacheFactory;
    }
    
    @Autowired
    public void setZoneService(ZoneService zoneService) {
        this.zoneService = zoneService;
    }
    
    @Autowired
    public void setPaoDao(PaoDao paoDao) {
        this.paoDao = paoDao;
    }
    
    @Autowired
    public void setPointDao(PointDao pointDao) {
        this.pointDao = pointDao;
    }
}
