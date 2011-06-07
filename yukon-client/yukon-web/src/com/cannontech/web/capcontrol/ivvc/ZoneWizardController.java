package com.cannontech.web.capcontrol.ivvc;

import java.util.Arrays;
import java.util.List;

import javax.activation.UnsupportedDataTypeException;
import javax.jms.IllegalStateException;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.MessageCodesResolver;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.capcontrol.CapBankToZoneMapping;
import com.cannontech.capcontrol.PointToZoneMapping;
import com.cannontech.capcontrol.exception.RootZoneExistsException;
import com.cannontech.capcontrol.model.AbstractZoneNotThreePhase;
import com.cannontech.capcontrol.model.AbstractZoneThreePhase;
import com.cannontech.capcontrol.model.Zone;
import com.cannontech.capcontrol.model.ZoneAssignmentCapBankRow;
import com.cannontech.capcontrol.model.ZoneAssignmentPointRow;
import com.cannontech.capcontrol.model.AbstractZone;
import com.cannontech.capcontrol.service.ZoneService;
import com.cannontech.cbc.cache.CapControlCache;
import com.cannontech.cbc.cache.FilterCacheFactory;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.validator.YukonMessageCodeResolver;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.pao.ZoneType;
import com.cannontech.enums.Phase;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.PageEditMode;
import com.cannontech.web.capcontrol.ivvc.validators.ZoneDtoValidator;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.common.flashScope.FlashScopeMessageType;
import com.cannontech.yukon.cbc.SubBus;
import com.cannontech.yukon.cbc.VoltageRegulatorFlags;
import com.google.common.collect.Lists;


@RequestMapping("/ivvc/wizard/*")
@Controller
public class ZoneWizardController {
    
    private ZoneService zoneService;
    private ZoneDtoValidator zoneDtoValidator;
    private FilterCacheFactory filterCacheFactory;
    private ZoneDtoHelper zoneDtoHelper;
    private YukonUserContextMessageSourceResolver messageSourceResolver;
    
    @RequestMapping
    public String zoneCreationWizard(ModelMap model, LiteYukonUser user, int subBusId) {
        Zone zone = new Zone();
        zone.setSubstationBusId(subBusId);
        return wizardSelectParent(model, user, zone);
    }

    @RequestMapping
    public String wizardSelectParent(ModelMap model, LiteYukonUser user, Zone zone) {
        List<Zone> parentZones = zoneService.getZonesBySubBusId(zone.getSubstationBusId());
        model.addAttribute("parentZones", parentZones);
        model.addAttribute("zone", zone);
        
        addZoneEnums(model);
        
        return "ivvc/zoneWizardParent.jsp";
    }

    @RequestMapping
    public String wizardParentSelected(ModelMap model, YukonUserContext userContext, Zone zone) {
        List<ZoneType> availableZoneTypes = Lists.newArrayListWithCapacity(3);
        List<Phase> availableZonePhases = Lists.newArrayListWithCapacity(3);
        Integer parentId = zone.getParentId();

        if (parentId == null) {
            availableZoneTypes = getAllZoneTypes();
            availableZonePhases.addAll(Lists.newArrayList(Phase.A, Phase.B, Phase.C));
            MessageSourceAccessor messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(userContext);
            String root = messageSourceAccessor.getMessage("yukon.web.modules.capcontrol.ivvc.zoneWizard.label.creatingAsRootZone");
            model.addAttribute("parentZoneName", root);
        } else {
            Zone parentZone = zoneService.getZoneById(parentId);
            if (parentZone.getZoneType() == ZoneType.GANG_OPERATED) {
                availableZoneTypes = getAllZoneTypes();
                availableZonePhases.addAll(Lists.newArrayList(Phase.A, Phase.B, Phase.C));
            } else if (parentZone.getZoneType() == ZoneType.THREE_PHASE) {
                availableZoneTypes.add(ZoneType.THREE_PHASE);
                availableZoneTypes.add(ZoneType.SINGLE_PHASE);
                availableZonePhases.addAll(Lists.newArrayList(Phase.A, Phase.B, Phase.C));
            } else {
                availableZoneTypes.add(ZoneType.SINGLE_PHASE);
                Phase parentPhase = parentZone.getRegulators().get(0).getPhase();
                availableZonePhases.add(parentPhase);
            }

            model.addAttribute("parentZoneName", parentZone.getName());
        }

        model.addAttribute("zone", zone);
        model.addAttribute("availableZoneTypes", availableZoneTypes);
        model.addAttribute("availableZonePhases", availableZonePhases);
        addZoneEnums(model);

        return "ivvc/zoneWizardType.jsp";
    }

    @RequestMapping
    public String wizardTypeSelected(ModelMap model, YukonUserContext userContext, Zone zone) throws IllegalStateException, UnsupportedDataTypeException {
        Integer parentId = zone.getParentId();
        if (parentId != null) {
            Zone parentZone = zoneService.getZoneById(parentId);
            model.addAttribute("parentZone", parentZone);
        } else {
            MessageSourceAccessor messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(userContext);
            String creatingAsRootString = messageSourceAccessor.getMessage("yukon.web.modules.capcontrol.ivvc.zoneWizard.label.creatingAsRootZone");
            model.addAttribute("parentZoneName", creatingAsRootString);
        }

        AbstractZone zoneDto = zoneDtoHelper.getAbstractZoneFromZone(zone, userContext.getYukonUser());
        setupZoneCreation(model, userContext.getYukonUser(), zoneDto);
        
        return "ivvc/zoneWizardDetails.jsp";
    }

    private void setupZoneCreation(ModelMap model, LiteYukonUser user, AbstractZone zoneDto) {
        CapControlCache cache = filterCacheFactory.createUserAccessFilteredCache(user);
        SubBus subBus = cache.getSubBus(zoneDto.getSubstationBusId());
        model.addAttribute("subBusName", subBus.getCcName());

        model.addAttribute("zoneDto", zoneDto);
        
        List<Phase> phases = getPhasesForZone(zoneDto);
        model.addAttribute("phases", phases);
        
        addZoneEnums(model);

        boolean phaseUneditable = isPhaseUneditable(zoneDto.getZoneType());
        model.addAttribute("phaseUneditable", phaseUneditable);

        model.addAttribute("mode", PageEditMode.CREATE.name());
    }
    
    private void addZoneEnums(ModelMap model) {
        model.addAttribute("gangOperated", ZoneType.GANG_OPERATED);
        model.addAttribute("threePhase", ZoneType.THREE_PHASE);
        model.addAttribute("singlePhase", ZoneType.SINGLE_PHASE);
        model.addAttribute("phaseA", Phase.A);
        model.addAttribute("phaseB", Phase.B);
        model.addAttribute("phaseC", Phase.C);
    }
    
    @RequestMapping
    public String createZone(ModelMap model, HttpServletRequest request, FlashScope flashScope, 
                             YukonUserContext userContext, ZoneType zoneType) {
        AbstractZone zoneDto = zoneDtoHelper.getAbstractZoneFromZoneType(zoneType);
        BindingResult bindingResult = bind(model, request, zoneDto, "zoneDto", userContext);

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
            setupZoneCreation(model, userContext.getYukonUser(), zoneDto);
            
            //Add Errors to flash scope
            List<MessageSourceResolvable> messages = YukonValidationUtils.errorsForBindingResult(bindingResult);
            flashScope.setMessage(messages, FlashScopeMessageType.ERROR);
        }

        return "ivvc/zoneWizardDetails.jsp";
    }
    
    @RequestMapping
    public String zoneEditor(ModelMap model, LiteYukonUser user, int zoneId) {
        setupZoneEditor(model, user, zoneId);
        
        return "ivvc/zoneWizardDetails.jsp";
    }
    
    private void setupZoneEditor(ModelMap model, LiteYukonUser user, int zoneId) {
        CapControlCache cache = filterCacheFactory.createUserAccessFilteredCache(user);
        AbstractZone zoneDto = zoneDtoHelper.getAbstractZoneFromZoneId(zoneId, user);

        SubBus subBus = cache.getSubBus(zoneDto.getSubstationBusId());

        if (zoneDto instanceof AbstractZoneNotThreePhase) {
            VoltageRegulatorFlags regulatorFlags =
                cache.getVoltageRegulatorFlags(((AbstractZoneNotThreePhase) zoneDto).getRegulator().getRegulatorId());
            model.addAttribute("regulatorName", regulatorFlags.getCcName());
        } else {
            AbstractZoneThreePhase abstractZoneThreePhase = (AbstractZoneThreePhase) zoneDto;
            VoltageRegulatorFlags regulatorFlagsPhaseA =
                cache.getVoltageRegulatorFlags(abstractZoneThreePhase.getRegulatorA().getRegulatorId());
            VoltageRegulatorFlags regulatorFlagsPhaseB =
                cache.getVoltageRegulatorFlags(abstractZoneThreePhase.getRegulatorB().getRegulatorId());
            VoltageRegulatorFlags regulatorFlagsPhaseC =
                cache.getVoltageRegulatorFlags(abstractZoneThreePhase.getRegulatorC().getRegulatorId());
            model.addAttribute("regulatorNamePhaseA", regulatorFlagsPhaseA.getCcName());
            model.addAttribute("regulatorNamePhaseB", regulatorFlagsPhaseB.getCcName());
            model.addAttribute("regulatorNamePhaseC", regulatorFlagsPhaseC.getCcName());
        }

        model.addAttribute("zoneDto", zoneDto);

        Integer parentId = zoneDto.getParentId();
        if (parentId != null) {
            Zone parent = zoneService.getZoneById(parentId);
            AbstractZone parentZone = zoneDtoHelper.getAbstractZoneFromZone(parent, user);
            model.addAttribute("parentZone", parentZone);
        }

        List<Phase> phases = getPhasesForZone(zoneDto);
        model.addAttribute("phases", phases);
        
        addZoneEnums(model);

        boolean phaseUneditable = isPhaseUneditable(zoneDto.getZoneType());
        model.addAttribute("phaseUneditable", phaseUneditable);

        model.addAttribute("subBusName", subBus.getCcName());
        model.addAttribute("mode", PageEditMode.EDIT.name());
    }

    private List<ZoneType> getAllZoneTypes() {
        List<ZoneType> zoneTypes = Lists.newArrayList();
        zoneTypes.add(ZoneType.GANG_OPERATED);
        zoneTypes.add(ZoneType.THREE_PHASE);
        zoneTypes.add(ZoneType.SINGLE_PHASE);
        return zoneTypes;
    }

    private List<Phase> getPhasesForZone(AbstractZone zoneDto) {
        List<Phase> phases = Lists.newArrayListWithCapacity(3);
        if (zoneDto.getZoneType() == ZoneType.SINGLE_PHASE) {
            Phase phase = ((AbstractZoneNotThreePhase)zoneDto).getRegulator().getPhase();
            phases.add(phase);
        } else {
            phases.addAll(Lists.newArrayList(Phase.A, Phase.B, Phase.C));
        }
        return phases;
    }

    private List<Phase> getPhasesForZoneTypeAndPhase(ZoneType zoneType, Phase phase) {
        List<Phase> phases = Lists.newArrayListWithCapacity(3);
        if (zoneType == ZoneType.SINGLE_PHASE) {
            phases.add(phase);
        } else {
            phases.addAll(Lists.newArrayList(Phase.A, Phase.B, Phase.C));
        }
        
        return phases;
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

    private boolean isPhaseUneditable(ZoneType zoneType) {
        if (zoneType == ZoneType.SINGLE_PHASE) {
            return true;
        }
        return false;
    }

    @RequestMapping
    public String updateZone(ModelMap model, HttpServletRequest request, FlashScope flashScope, 
                             YukonUserContext userContext, ZoneType zoneType, 
                             Integer[] banksToRemove, Integer[] pointsToRemove) {

        AbstractZone zoneDto = zoneDtoHelper.getAbstractZoneFromZoneType(zoneType);
        BindingResult bindingResult = bind(model, request, zoneDto, "zoneDto", userContext);

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

            setupZoneEditor(model, userContext.getYukonUser(), zoneDto.getZoneId());
            
            //Add Errors to flash scope
            List<MessageSourceResolvable> messages = YukonValidationUtils.errorsForBindingResult(bindingResult);
            flashScope.setMessage(messages, FlashScopeMessageType.ERROR);
            
            return "ivvc/zoneWizardDetails.jsp";
        }        
    }

    private BindingResult bind(ModelMap model, HttpServletRequest request,
                               Object target, String objectName, YukonUserContext userContext) {
        ServletRequestDataBinder binder =
            new ServletRequestDataBinder(target, objectName);
        initBinder(binder, userContext);
        binder.bind(request);
        BindingResult bindingResult = binder.getBindingResult();
        model.addAttribute("zoneDto", target);
        model.putAll(binder.getBindingResult().getModel());
        return bindingResult;
    }

    @InitBinder
    public void initBinder(WebDataBinder binder, YukonUserContext userContext) {
        if (binder.getTarget() != null) {
            MessageCodesResolver msgCodesResolver =
                new YukonMessageCodeResolver("yukon.web.modules.capcontrol.ivvc.zoneWizard.");
            binder.setMessageCodesResolver(msgCodesResolver);
        }
    }

    private boolean saveZone(AbstractZone zoneDto, BindingResult bindingResult, FlashScope flashScope) {
        zoneDtoValidator.validate(zoneDto, bindingResult);
        if (bindingResult.hasErrors()) {
            return false;
        } else {
            zoneService.saveZone(zoneDto);
        }
        return true;
    }
    
    private String closeDialog(ModelMap modelMap) {
        modelMap.addAttribute("popupId", "zoneWizardPopup");
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
    public String addCapBank(ModelMap modelMap, LiteYukonUser user, int id, Integer zoneId, int index) {
        CapControlCache cache = filterCacheFactory.createUserAccessFilteredCache(user);

        CapBankToZoneMapping bankToZone = new CapBankToZoneMapping();
        bankToZone.setDeviceId(id);
        bankToZone.setGraphPositionOffset(0);
        ZoneAssignmentCapBankRow row = zoneDtoHelper.getBankAssignmentFromMapping(bankToZone, cache);        
        modelMap.addAttribute("row",row);
        modelMap.addAttribute("index", index);
        
        return "ivvc/addZoneCapBankRow.jsp";
    }
    
    @RequestMapping
    public String addVoltagePoint(ModelMap modelMap, LiteYukonUser user, int id, String zoneType, String phase, int index) {
    	PointToZoneMapping pointToZone = new PointToZoneMapping();
    	pointToZone.setPointId(id);
    	pointToZone.setGraphPositionOffset(0);
        ZoneAssignmentPointRow row = zoneDtoHelper.getPointAssignmentFromMapping(pointToZone);        
        modelMap.addAttribute("row",row);
        modelMap.addAttribute("index", index);

        ZoneType type = ZoneType.valueOf(zoneType);
        Phase regPhase = null;
        if (phase != null) {
            regPhase = Phase.valueOf(phase);
        }
        List<Phase> phases = getPhasesForZoneTypeAndPhase(type, regPhase);
        modelMap.addAttribute("phases", phases);
        
        boolean phaseUneditable = isPhaseUneditable(type);
        modelMap.addAttribute("phaseUneditable", phaseUneditable);
        
        return "ivvc/addZoneVoltagePointRow.jsp";
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
    public void setZoneDtoValidator(ZoneDtoValidator zoneDtoValidator) {
        this.zoneDtoValidator = zoneDtoValidator;
    }
    
    @Autowired
    public void setMessageSourceResolver(YukonUserContextMessageSourceResolver messageSourceResolver) {
        this.messageSourceResolver = messageSourceResolver;
    }
    
    @Autowired
    public void setZoneDtoHelpers(ZoneDtoHelper zoneDtoHelpers) {
        this.zoneDtoHelper = zoneDtoHelpers;
    }
}
