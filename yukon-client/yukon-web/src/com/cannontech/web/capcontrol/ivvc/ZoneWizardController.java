package com.cannontech.web.capcontrol.ivvc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.capcontrol.CapBankToZoneMapping;
import com.cannontech.capcontrol.PointToZoneMapping;
import com.cannontech.capcontrol.RootZoneExistsException;
import com.cannontech.capcontrol.model.Zone;
import com.cannontech.capcontrol.model.ZoneAssignmentCapBankRow;
import com.cannontech.capcontrol.model.ZoneAssignmentPointRow;
import com.cannontech.capcontrol.model.ZoneDto;
import com.cannontech.capcontrol.service.ZoneService;
import com.cannontech.cbc.cache.CapControlCache;
import com.cannontech.cbc.cache.FilterCacheFactory;
import com.cannontech.common.util.CtiUtilities;
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
        
        return "ivvc/zoneWizard.jsp";
    }
    
    private void setupZoneCreation(ModelMap model, LiteYukonUser user, int subBusId) {
        CapControlCache cache = filterCacheFactory.createUserAccessFilteredCache(user);
        SubBus subBus = cache.getSubBus(subBusId);
        
        List<Zone> zones = zoneService.getZonesBySubBusId(subBusId);
        model.addAttribute("zones",zones);

        ZoneDto zoneDto = new ZoneDto();
        zoneDto.setSubstationBusId(subBusId);
        
        model.addAttribute("zoneDto", zoneDto);
        model.addAttribute("regulatorName", CtiUtilities.STRING_NONE);
        model.addAttribute("subBusName", subBus.getCcName());
        
        model.addAttribute("mode", PageEditMode.CREATE.name());
    }
    
    @RequestMapping
    public String createZone(@ModelAttribute("zoneDto") ZoneDto zoneDto, BindingResult bindingResult,
                             ModelMap model, FlashScope flashScope, LiteYukonUser user) {
        
        boolean errors = false;
        try {
            errors = saveZone(zoneDto,bindingResult,flashScope);
        } catch (RootZoneExistsException e) {
            errors = true;
            flashScope.setMessage(new YukonMessageSourceResolvable("yukon.web.modules.capcontrol.ivvc.zoneWizard.error.rootZoneExists"), FlashScopeMessageType.ERROR);
        }

        if (errors) {
            setupZoneCreation(model,user,zoneDto.getSubstationBusId());
            model.addAttribute("zoneDto", zoneDto);
            
            if (zoneDto.getRegulatorId() != -1) {
                CapControlCache cache = filterCacheFactory.createUserAccessFilteredCache(user);
                VoltageRegulatorFlags regulatorFlags = cache.getVoltageRegulatorFlags(zoneDto.getRegulatorId());
                model.addAttribute("regulatorName", regulatorFlags.getCcName());
                flashScope.setMessage(new YukonMessageSourceResolvable("yukon.web.modules.capcontrol.ivvc.zoneWizard.error.problemSavingZone"), FlashScopeMessageType.ERROR);
            } else {
                flashScope.setMessage(new YukonMessageSourceResolvable("yukon.web.modules.capcontrol.ivvc.zoneWizard.error.required.regulatorId"), FlashScopeMessageType.ERROR);
            }
        } else {
            //Close normally
            return closeDialog(model);                
        }

        return "ivvc/zoneWizard.jsp";   
    }
    
    @RequestMapping
    public String zoneEditor(ModelMap model, LiteYukonUser user, int zoneId) {

        setupZoneEditor(model,user,zoneId);
        
        return "ivvc/zoneWizard.jsp";
    }
    
    private void setupZoneEditor(ModelMap model, LiteYukonUser user, int zoneId) {
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
        zoneDto.setGraphStartPosition(zone.getGraphStartPosition());
        
        Integer parentId = zone.getParentId();
        String parentName = "---";
        if (parentId != null) {
            zoneDto.setParentZoneId(parentId);
            Zone parentZone = zoneService.getZoneById(parentId);
            parentName = parentZone.getName();
        }
        model.addAttribute("parentZoneName", parentName);
        
        //Add Bank Assignments
        List<ZoneAssignmentCapBankRow> bankAssignments = buildBankAssignmentList(zone,cache);
        zoneDto.setBankAssignments(bankAssignments);

        //Add Point Assignments
        List<ZoneAssignmentPointRow> pointAssignments = buildPointAssignmentList(zone);
        zoneDto.setPointAssignments(pointAssignments);
        
        model.addAttribute("zoneDto", zoneDto);
        model.addAttribute("regulatorName", regulatorFlags.getCcName());
        model.addAttribute("subBusName", subBus.getCcName());
        model.addAttribute("mode", PageEditMode.EDIT.name());
    }
    
    private List<ZoneAssignmentCapBankRow> getRemainingBankAssignments(List<ZoneAssignmentCapBankRow> bankAssignments, Integer[] removedBanks) {
    	if (removedBanks == null) {
    		return bankAssignments;
    	}
    	
    	List<ZoneAssignmentCapBankRow> remainingBankAssignments = new ArrayList<ZoneAssignmentCapBankRow>(bankAssignments); 
    	Collections.copy(remainingBankAssignments, bankAssignments);
    	
    	for (Integer removedBank : removedBanks) {
    		for (ZoneAssignmentCapBankRow bankRow : bankAssignments) {
    			if (removedBank.intValue() == bankRow.getId()) {
    				remainingBankAssignments.remove(bankRow);
    			}
    		}
    	}
    	
    	return remainingBankAssignments;
    }
    
    private List<ZoneAssignmentPointRow> getRemainingPointAssignments(List<ZoneAssignmentPointRow> pointAssignments, Integer[] removedPoints) {
    	if (removedPoints == null) {
    		return pointAssignments;
    	}
    	
    	List<ZoneAssignmentPointRow> remainingPointAssignments = new ArrayList<ZoneAssignmentPointRow>(pointAssignments); 
    	Collections.copy(remainingPointAssignments, pointAssignments);
    	
    	for (Integer removedPoint : removedPoints) {
    		for (ZoneAssignmentPointRow pointRow : pointAssignments) {
    			if (removedPoint.intValue() == pointRow.getId()) {
    				remainingPointAssignments.remove(pointRow);
    			}
    		}
    	}
    	
    	return remainingPointAssignments;
    }
    
    @RequestMapping
    public String updateZone(@ModelAttribute("zoneDto") ZoneDto zoneDto, BindingResult bindingResult,
    						 Integer[] banksToRemove, Integer[] pointsToRemove,
                             ModelMap model, FlashScope flashScope, LiteYukonUser user) {
        boolean errors = false;
        try {
        	List<ZoneAssignmentCapBankRow> bankAssignments = getRemainingBankAssignments(zoneDto.getBankAssignments(), banksToRemove);
        	List<ZoneAssignmentPointRow> pointAssignments = getRemainingPointAssignments(zoneDto.getPointAssignments(), pointsToRemove);
        	zoneDto.setBankAssignments(bankAssignments);
        	zoneDto.setPointAssignments(pointAssignments);
        	
            errors = saveZone(zoneDto,bindingResult,flashScope);
        } catch (RootZoneExistsException e) {
            errors = true;
            flashScope.setMessage(new YukonMessageSourceResolvable("yukon.web.modules.capcontrol.ivvc.zoneWizard.error.rootZoneExists"), FlashScopeMessageType.ERROR);
        }
        
        if (errors) {
            flashScope.setMessage(new YukonMessageSourceResolvable("yukon.web.modules.capcontrol.ivvc.zoneWizard.error.problemSavingZone"), FlashScopeMessageType.ERROR);
            setupZoneEditor(model,user,zoneDto.getZoneId());
            model.addAttribute("zoneDto", zoneDto);
            return "ivvc/zoneWizard.jsp";   
        } else {
            //Close normally
            return closeDialog(model);                
        }        
    }
    
    private boolean saveZone(ZoneDto zoneDto, BindingResult bindingResult, FlashScope flashScope) {
        zoneDtoValidator.validate(zoneDto, bindingResult);
        if (bindingResult.hasErrors()) {
            //Add Errors to flash scope
            List<MessageSourceResolvable> messages = YukonValidationUtils.errorsForBindingResult(bindingResult);
            flashScope.setMessage(messages, FlashScopeMessageType.ERROR);
            return true;
        } else {
            zoneService.saveZone(zoneDto);
            flashScope.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.capcontrol.ivvc.zoneWizard.success.saved"));
        }
        return false;
    }
    
    private String closeDialog(ModelMap modelMap) {
        modelMap.addAttribute("popupId", "tierContentPopup");
        return "ivvc/closePopup.jsp";
    }
    
    @RequestMapping
    public String deleteZone(ModelMap modelMap, int zoneId) {
        Zone zone = zoneService.getZoneById(zoneId);

        zoneService.deleteZone(zoneId);

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
