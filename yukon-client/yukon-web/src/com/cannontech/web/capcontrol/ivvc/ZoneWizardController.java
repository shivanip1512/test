package com.cannontech.web.capcontrol.ivvc;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.capcontrol.CapBankToZoneMapping;
import com.cannontech.capcontrol.PointToZoneMapping;
import com.cannontech.capcontrol.exception.RootZoneExistsException;
import com.cannontech.capcontrol.model.Zone;
import com.cannontech.capcontrol.model.ZoneAssignmentCapBankRow;
import com.cannontech.capcontrol.model.ZoneAssignmentPointRow;
import com.cannontech.capcontrol.model.ZoneDto;
import com.cannontech.capcontrol.service.ZoneService;
import com.cannontech.cbc.cache.CapControlCache;
import com.cannontech.cbc.cache.FilterCacheFactory;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.PointDao;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.web.PageEditMode;
import com.cannontech.web.capcontrol.ivvc.validators.ZoneDtoValidator;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.common.flashScope.FlashScopeMessageType;
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
        
        setupZoneCreation(model,user,subBusId);
        ZoneDto zoneDto = new ZoneDto();
        zoneDto.setSubstationBusId(subBusId);
        
        model.addAttribute("zoneDto", zoneDto);
        
        return "ivvc/zoneWizard.jsp";
    }
    
    private void setupZoneCreation(ModelMap model, LiteYukonUser user, int subBusId) {
        CapControlCache cache = filterCacheFactory.createUserAccessFilteredCache(user);
        SubBus subBus = cache.getSubBus(subBusId);
        
        List<Zone> zones = zoneService.getZonesBySubBusId(subBusId);
        model.addAttribute("zones",zones);
        model.addAttribute("subBusName", subBus.getCcName());
        
        model.addAttribute("mode", PageEditMode.CREATE.name());
    }
    
    @RequestMapping
    public String createZone(@ModelAttribute("zoneDto") ZoneDto zoneDto, BindingResult bindingResult,
                             ModelMap model, FlashScope flashScope, LiteYukonUser user) {
        
        boolean noErrors = true;
        try {
            noErrors = saveZone(zoneDto,bindingResult,flashScope);
        } catch (RootZoneExistsException e) {
            noErrors = false;
            bindingResult.reject("yukon.web.modules.capcontrol.ivvc.zoneWizard.error.rootZoneExists");
        }

        if (noErrors) {
            //Close normally
            flashScope.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.capcontrol.ivvc.zoneWizard.success.saved"));
            return closeDialog(model);                
        } else {
            setupZoneCreation(model,user,zoneDto.getSubstationBusId());
            
            if (zoneDto.getRegulatorId() != -1) {
                CapControlCache cache = filterCacheFactory.createUserAccessFilteredCache(user);
                VoltageRegulatorFlags regulatorFlags = cache.getVoltageRegulatorFlags(zoneDto.getRegulatorId());
                model.addAttribute("regulatorName", regulatorFlags.getCcName());
            }
            
            //Add Errors to flash scope
            List<MessageSourceResolvable> messages = YukonValidationUtils.errorsForBindingResult(bindingResult);
            flashScope.setMessage(messages, FlashScopeMessageType.ERROR);
        }

        return "ivvc/zoneWizard.jsp";   
    }
    
    @RequestMapping
    public String zoneEditor(ModelMap model, LiteYukonUser user, int zoneId) {
        Zone zone = zoneService.getZoneById(zoneId);
        CapControlCache cache = filterCacheFactory.createUserAccessFilteredCache(user);
        
        ZoneDto zoneDto = new ZoneDto();
        zoneDto.setZoneId(zoneId);
        zoneDto.setName(zone.getName());
        zoneDto.setSubstationBusId(zone.getSubstationBusId());
        zoneDto.setRegulatorId(zone.getRegulatorId());
        zoneDto.setParentZoneId(zone.getParentId());
        zoneDto.setGraphStartPosition(zone.getGraphStartPosition());
        
        //Add Bank Assignments
        List<ZoneAssignmentCapBankRow> bankAssignments = buildBankAssignmentList(zone,cache);
        zoneDto.setBankAssignments(bankAssignments);
        
        //Add Point Assignments
        List<ZoneAssignmentPointRow> pointAssignments = buildPointAssignmentList(zone);
        zoneDto.setPointAssignments(pointAssignments);
        
        model.addAttribute("zoneDto", zoneDto);
        
        setupZoneEditor(model,cache,zone);
        
        return "ivvc/zoneWizard.jsp";
    }
    
    private void setupZoneEditor(ModelMap model, CapControlCache cache, Zone zone) {       
        SubBus subBus = cache.getSubBus(zone.getSubstationBusId());
        VoltageRegulatorFlags regulatorFlags = cache.getVoltageRegulatorFlags(zone.getRegulatorId());
        
        List<Zone> zones = zoneService.getZonesBySubBusId(zone.getSubstationBusId());
        model.addAttribute("zones",zones);

        Integer parentId = zone.getParentId();
        String parentName = "---";
        if (parentId != null) {
            Zone parentZone = zoneService.getZoneById(parentId);
            parentName = parentZone.getName();
        }
        model.addAttribute("parentZoneName", parentName);
        
        model.addAttribute("regulatorName", regulatorFlags.getCcName());
        model.addAttribute("subBusName", subBus.getCcName());
        model.addAttribute("mode", PageEditMode.EDIT.name());
    }
    
    private List<ZoneAssignmentCapBankRow> getRemainingBankAssignments(List<ZoneAssignmentCapBankRow> bankAssignments, Integer[] removedBanks) {
    	if (removedBanks == null) {
    		return bankAssignments;
    	}
    	
    	List<ZoneAssignmentCapBankRow> remainingBankAssignments = Lists.newArrayList(); 
    	
    	for (ZoneAssignmentCapBankRow bankRow : bankAssignments) {
    		boolean isRemoved = Arrays.asList(removedBanks).contains(bankRow.getId());
    		if (!isRemoved) {
    			remainingBankAssignments.add(bankRow);
    		}
    	}
    	
    	return remainingBankAssignments;
    }
    
    private List<ZoneAssignmentPointRow> getRemainingPointAssignments(List<ZoneAssignmentPointRow> pointAssignments, Integer[] removedPoints) {
    	if (removedPoints == null) {
    		return pointAssignments;
    	}
    	
    	List<ZoneAssignmentPointRow> remainingPointAssignments = Lists.newArrayList();
    	
    	for (ZoneAssignmentPointRow pointRow : pointAssignments) {
    		boolean isRemoved = Arrays.asList(removedPoints).contains(pointRow.getId());
    		if (!isRemoved) {
    			remainingPointAssignments.add(pointRow);
    		}
    	}
    	
    	return remainingPointAssignments;
    }
    
    @RequestMapping
    public String updateZone(@ModelAttribute("zoneDto") ZoneDto zoneDto, BindingResult bindingResult,
    						 Integer[] banksToRemove, Integer[] pointsToRemove,
                             ModelMap model, FlashScope flashScope, LiteYukonUser user) {
        boolean noErrors = true;
        try {
        	List<ZoneAssignmentCapBankRow> bankAssignments = getRemainingBankAssignments(zoneDto.getBankAssignments(), banksToRemove);
        	List<ZoneAssignmentPointRow> pointAssignments = getRemainingPointAssignments(zoneDto.getPointAssignments(), pointsToRemove);
        	zoneDto.setBankAssignments(bankAssignments);
        	zoneDto.setPointAssignments(pointAssignments);
        	
            noErrors = saveZone(zoneDto,bindingResult,flashScope);
        } catch (RootZoneExistsException e) {
            noErrors = false;
            bindingResult.reject("yukon.web.modules.capcontrol.ivvc.zoneWizard.error.rootZoneExists");
        }
        
        if (noErrors) {
            //Close normally
            flashScope.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.capcontrol.ivvc.zoneWizard.success.saved"));
            return closeDialog(model); 
        } else {
            bindingResult.reject("yukon.web.modules.capcontrol.ivvc.zoneWizard.error.problemSavingZone");
            Zone zone = zoneService.getZoneById(zoneDto.getZoneId());
            CapControlCache cache = filterCacheFactory.createUserAccessFilteredCache(user);
            
            model.addAttribute("zoneDto", zoneDto);
            setupZoneEditor(model,cache,zone);
            
            //Add Errors to flash scope
            List<MessageSourceResolvable> messages = YukonValidationUtils.errorsForBindingResult(bindingResult);
            flashScope.setMessage(messages, FlashScopeMessageType.ERROR);
            
            return "ivvc/zoneWizard.jsp";                  
        }        
    }
    
    private boolean saveZone(ZoneDto zoneDto, BindingResult bindingResult, FlashScope flashScope) {
        zoneDtoValidator.validate(zoneDto, bindingResult);
        if (bindingResult.hasErrors()) {
            return false;
        } else {
            zoneService.saveZone(zoneDto);
        }
        return true;
    }
    
    private String closeDialog(ModelMap modelMap) {
        modelMap.addAttribute("popupId", "tierContentPopup");
        return "ivvc/closePopup.jsp";
    }
    
    @RequestMapping
    public String deleteZone(ModelMap modelMap, int zoneId, FlashScope flashScope) {
        Zone zone = zoneService.getZoneById(zoneId);
        try{
            zoneService.deleteZone(zoneId);
        } catch (DataIntegrityViolationException e) {
            flashScope.setError(new YukonMessageSourceResolvable("yukon.web.modules.capcontrol.ivvc.zoneWizard.error.zoneDeleteError"));
        }

        modelMap.addAttribute("subBusId", zone.getSubstationBusId());
        
        return "redirect:/spring/capcontrol/ivvc/bus/detail";
    }
    
    @RequestMapping
    public String addCapBank(ModelMap modelMap, LiteYukonUser user, int id, int index) {
        CapControlCache cache = filterCacheFactory.createUserAccessFilteredCache(user);

        CapBankToZoneMapping bankToZone = new CapBankToZoneMapping();
        bankToZone.setDeviceId(id);
        bankToZone.setGraphPositionOffset(0);
        ZoneAssignmentCapBankRow row = buildBankAssignment(bankToZone, cache);        
        modelMap.addAttribute("row",row);
        modelMap.addAttribute("index", index);
        
        return "ivvc/addZoneTableRow.jsp";
    }
    
    @RequestMapping
    public String addVoltagePoint(ModelMap modelMap, LiteYukonUser user, int id, int index) {
    	PointToZoneMapping pointToZone = new PointToZoneMapping();
    	pointToZone.setPointId(id);
    	pointToZone.setGraphPositionOffset(0);
        ZoneAssignmentPointRow row = buildPointAssignment(pointToZone);        
        modelMap.addAttribute("row",row);
        modelMap.addAttribute("index", index);
        
        return "ivvc/addZoneTableRow.jsp";
    }

    private List<ZoneAssignmentPointRow> buildPointAssignmentList(Zone zone) {
    	List<PointToZoneMapping> pointsToZone = zoneService.getPointToZoneMapping(zone.getId());
        
        List<ZoneAssignmentPointRow> rows = Lists.newArrayList();
        for (PointToZoneMapping pointToZone : pointsToZone) {
        	ZoneAssignmentPointRow row = buildPointAssignment(pointToZone);
            rows.add(row);
        }
        
        return rows;
    }
    
    private ZoneAssignmentPointRow buildPointAssignment(PointToZoneMapping pointToZone) {
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
        
        return row;
    }
    
    private List<ZoneAssignmentCapBankRow> buildBankAssignmentList(Zone zone, CapControlCache cache) {
        List<ZoneAssignmentCapBankRow> rows = Lists.newArrayList();
        List<CapBankToZoneMapping> banksToZone = zoneService.getCapBankToZoneMapping(zone.getId());
        
        for (CapBankToZoneMapping bankToZone : banksToZone) {
        	ZoneAssignmentCapBankRow row = buildBankAssignment(bankToZone, cache);
            rows.add(row);
        }
        
        return rows;
    }
    
    private ZoneAssignmentCapBankRow buildBankAssignment(CapBankToZoneMapping bankToZone, CapControlCache cache) {
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
