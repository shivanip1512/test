package com.cannontech.web.capcontrol.ivvc;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.capcontrol.CapBankToZoneMapping;
import com.cannontech.capcontrol.PointToZoneMapping;
import com.cannontech.cbc.cache.CapControlCache;
import com.cannontech.cbc.cache.FilterCacheFactory;
import com.cannontech.cbc.model.Zone;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.PointDao;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.web.PageEditMode;
import com.cannontech.web.capcontrol.ivvc.models.ZoneAssignmentRow;
import com.cannontech.web.capcontrol.ivvc.models.ZoneDto;
import com.cannontech.web.capcontrol.ivvc.service.ZoneService;
import com.cannontech.web.capcontrol.ivvc.validators.ZoneDtoValidator;
import com.cannontech.web.capcontrol.models.ViewableCapBank;
import com.cannontech.web.capcontrol.util.CapControlWebUtils;
import com.cannontech.yukon.cbc.CapBankDevice;
import com.cannontech.yukon.cbc.SubBus;
import com.cannontech.yukon.cbc.VoltageRegulatorFlags;
import com.google.common.collect.Lists;


@RequestMapping("/ivvc/wizard/*")
@Controller
public class ZoneWizardController {
    
    private ZoneService zoneService;
    private ZoneDtoValidator zoneDtoValidator;
    private FilterCacheFactory filterCacheFactory;
    private PaoDao paoDao;
    private PointDao pointDao;
    
    @RequestMapping
    public String zoneCreation(ModelMap model, LiteYukonUser user, int subBusId) {
        CapControlCache cache = filterCacheFactory.createUserAccessFilteredCache(user);
        SubBus subBus = cache.getSubBus(subBusId);
        
        List<Zone> zones = zoneService.getZonesBySubBusId(subBusId);
        model.addAttribute("zones",zones);

        List<ViewableCapBank> capBanks = CapControlWebUtils.createViewableCapBank(cache.getCapBanksBySubBus(subBusId));
        model.addAttribute("capBanks",capBanks);

        ZoneDto zoneDto = new ZoneDto();
        zoneDto.setSubstationBusId(subBusId);
        
        model.addAttribute("zoneDto", zoneDto);
        model.addAttribute("regulatorName", CtiUtilities.STRING_NONE);
        model.addAttribute("subBusName", subBus.getCcName());
        
        model.addAttribute("mode", PageEditMode.CREATE.name());
        return "ivvc/zoneWizard.jsp";
    }
    
    @RequestMapping
    public String createZone(@ModelAttribute("zoneDto") ZoneDto zoneDto, BindingResult bindingResult,
                             ModelMap modelMap) {

        zoneDtoValidator.validate(zoneDto, bindingResult);
        
        if (!bindingResult.hasErrors()) {
            zoneService.createZone(zoneDto);
        }
        
        modelMap.addAttribute("subBusId", zoneDto.getSubstationBusId());
        
        return "redirect:/spring/capcontrol/ivvc/bus/detail";
    }
    
    @RequestMapping
    public String zoneEditor(ModelMap model, LiteYukonUser user, int zoneId) {
        CapControlCache cache = filterCacheFactory.createUserAccessFilteredCache(user);
        
        Zone zone = zoneService.getZoneById(zoneId);
        int substationBusId = zone.getSubstationBusId();
        
        SubBus subBus = cache.getSubBus(substationBusId);
        VoltageRegulatorFlags regulatorFlags = cache.getVoltageRegulatorFlags(zone.getRegulatorId());
        
        List<Zone> zones = zoneService.getZonesBySubBusId(substationBusId);
        model.addAttribute("zones",zones);

        ZoneDto zoneDto = new ZoneDto();
        zoneDto.setZoneId(zoneId);
        zoneDto.setName(zone.getName());
        zoneDto.setSubstationBusId(substationBusId);
        zoneDto.setRegulatorId(zone.getRegulatorId());
        
        Integer parentId = zone.getParentId();
        String parentName = "---";
        if (parentId == null) {
            zoneDto.setParentZoneId(-1);
        } else {
            zoneDto.setParentZoneId(parentId);
            Zone parentZone = zoneService.getZoneById(parentId);
            parentName = parentZone.getName();
        }

        model.addAttribute("parentZoneName", parentName);
        
        //Add Bank Assignments
        List<ZoneAssignmentRow> bankAssignments = buildBankAssignmentList(zone,cache);
        zoneDto.setBankAssignments(bankAssignments);

        //Add Point Assignments
        List<ZoneAssignmentRow> pointAssignments = buildPointAssignmentList(zone);
        zoneDto.setPointAssignments(pointAssignments);
        
        model.addAttribute("zoneDto", zoneDto);
        model.addAttribute("regulatorName", regulatorFlags.getCcName());
        model.addAttribute("subBusName", subBus.getCcName());
        model.addAttribute("mode", PageEditMode.EDIT.name());

        return "ivvc/zoneWizard.jsp";
    }
    
    @RequestMapping
    public String updateZone(@ModelAttribute("zoneDto") ZoneDto zoneDto, BindingResult bindingResult,
                             ModelMap modelMap) {

        zoneDtoValidator.validate(zoneDto, bindingResult);
        
        if (!bindingResult.hasErrors()) {
            zoneService.updateZone(zoneDto);
        }
        
        modelMap.addAttribute("subBusId", zoneDto.getSubstationBusId());
        
        return "redirect:/spring/capcontrol/ivvc/bus/detail";
    }
    
    @RequestMapping
    public String deleteZone(ModelMap modelMap, int zoneId) {
        Zone zone = zoneService.getZoneById(zoneId);

        zoneService.deleteZone(zoneId);

        modelMap.addAttribute("subBusId", zone.getSubstationBusId());
        
        return "redirect:/spring/capcontrol/ivvc/bus/detail";
    }
    
    @RequestMapping
    public String addCapBank(ModelMap modelMap, LiteYukonUser user, int id) {
        CapControlCache cache = filterCacheFactory.createUserAccessFilteredCache(user);

        ZoneAssignmentRow row = buildBankAssignment(id, 0, cache);        
        modelMap.addAttribute("row",row);
        
        return "ivvc/addZoneTableRow.jsp";
    }
    
    @RequestMapping
    public String addVoltagePoint(ModelMap modelMap, LiteYukonUser user, int id) {
        
        ZoneAssignmentRow row = buildPointAssignment(id, 0);        
        modelMap.addAttribute("row",row);
        
        return "ivvc/addZoneTableRow.jsp";
    }

    private List<ZoneAssignmentRow> buildPointAssignmentList(Zone zone) {
    	List<PointToZoneMapping> pointsToZone = zoneService.getPointToZoneMapping(zone.getId());
        
        List<ZoneAssignmentRow> rows = Lists.newArrayList();
        for (PointToZoneMapping pointToZone : pointsToZone) {
            ZoneAssignmentRow row = buildPointAssignment(pointToZone.getPointId(), pointToZone.getZoneOrder());
            rows.add(row);
        }
        
        return rows;
    }
    
    private ZoneAssignmentRow buildPointAssignment(Integer pointId, double zoneOrder) {
        LitePoint point = pointDao.getLitePoint(pointId);
        LiteYukonPAObject pao = paoDao.getLiteYukonPAO(point.getPaobjectID());

        ZoneAssignmentRow row = new ZoneAssignmentRow();
        row.setType("point");
        row.setId(pointId);        
        row.setName(point.getPointName());
        row.setDevice(pao.getPaoName());
        row.setOrder(zoneOrder);
        
        return row;
    }
    
    private List<ZoneAssignmentRow> buildBankAssignmentList(Zone zone, CapControlCache cache) {
        List<ZoneAssignmentRow> rows = Lists.newArrayList();
        List<CapBankToZoneMapping> banksToZone = zoneService.getCapBankToZoneMapping(zone.getId());
        
        for (CapBankToZoneMapping bankToZone : banksToZone) {
            ZoneAssignmentRow row = buildBankAssignment(bankToZone.getDeviceId(), bankToZone.getZoneOrder(), cache);
            rows.add(row);
        }
        
        return rows;
    }
    
    private ZoneAssignmentRow buildBankAssignment(Integer bankId, double zoneOrder, CapControlCache cache) {
        CapBankDevice bank = cache.getCapBankDevice(bankId);
        LiteYukonPAObject controller = paoDao.getLiteYukonPAO(bank.getControlDeviceID());

        ZoneAssignmentRow row = new ZoneAssignmentRow();
        row.setType("bank");
        row.setId(bankId);        
        row.setName(bank.getCcName());
        row.setDevice(controller.getPaoName());
        row.setOrder(zoneOrder);
        
        return row;
    }
    
    @Autowired
    public void setZoneService(ZoneService zoneService) {
        this.zoneService = zoneService;
    }
    
    @Autowired
    public void setFilterCacheFactory(FilterCacheFactory filterCacheFactory) {
        this.filterCacheFactory = filterCacheFactory;
    }
    
    @Autowired
    public void setPaoDao(PaoDao paoDao) {
        this.paoDao = paoDao;
    }
    
    @Autowired
    public void setPointDao(PointDao pointDao) {
        this.pointDao = pointDao;
    }
    
    @Autowired
    public void setZoneDtoValidator(ZoneDtoValidator zoneDtoValidator) {
        this.zoneDtoValidator = zoneDtoValidator;
    }
}
