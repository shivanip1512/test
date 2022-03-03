package com.cannontech.web.capcontrol.ivvc;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
import com.cannontech.capcontrol.model.AbstractZone;
import com.cannontech.capcontrol.model.RegulatorToZoneMapping;
import com.cannontech.capcontrol.model.Zone;
import com.cannontech.capcontrol.model.ZoneAssignmentCapBankRow;
import com.cannontech.capcontrol.model.ZoneAssignmentPointRow;
import com.cannontech.capcontrol.model.ZoneGang;
import com.cannontech.capcontrol.service.ZoneService;
import com.cannontech.cbc.cache.CapControlCache;
import com.cannontech.cbc.cache.FilterCacheFactory;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.util.JsonUtils;
import com.cannontech.common.validator.YukonMessageCodeResolver;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.pao.ZoneType;
import com.cannontech.common.model.Phase;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.message.capcontrol.streamable.SubBus;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.PageEditMode;
import com.cannontech.web.capcontrol.ivvc.validators.ZoneDtoValidator;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.common.flashScope.FlashScopeMessageType;
import com.google.common.collect.Lists;

@RequestMapping("/ivvc/wizard/*")
@Controller
public class ZoneWizardController {

    @Autowired private ZoneService zoneService;
    @Autowired private ZoneDtoValidator zoneDtoValidator;
    @Autowired private FilterCacheFactory filterCacheFactory;
    @Autowired private ZoneDtoHelper zoneDtoHelper;
    @Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;

    @RequestMapping("zoneCreationWizard")
    public String zoneCreationWizard(ModelMap model, LiteYukonUser user, int subBusId) {
        AbstractZone zoneDto = new ZoneGang();
        zoneDto.setSubstationBusId(subBusId);
        return getAndSetupParentSelectionPage(model, zoneDto);
    }

    @RequestMapping("wizardSelectParent")
    public String wizardSelectParent(HttpServletRequest request, ModelMap model,
                                     YukonUserContext userContext, ZoneType zoneType) {
        AbstractZone zoneDto =
            getBoundAbstractZoneFromZoneType(request, model, userContext, zoneType);
        return getAndSetupParentSelectionPage(model, zoneDto);
    }
    
    private String getAndSetupParentSelectionPage(ModelMap model, AbstractZone zoneDto) {
        List<Zone> parentZones = zoneService.getZonesBySubBusId(zoneDto.getSubstationBusId());
        model.addAttribute("parentZones", parentZones);
        model.addAttribute("zoneDto", zoneDto);
        addZoneEnums(model);
        return "ivvc/zoneWizardParent.jsp";
    }

    @RequestMapping("wizardParentSelected")
    public String wizardParentSelected(HttpServletRequest request, ModelMap model,
                                       YukonUserContext userContext, ZoneType zoneType) {
        AbstractZone zoneDto =
            getBoundAbstractZoneFromZoneType(request, model, userContext, zoneType);
        List<ZoneType> availableZoneTypes;
        List<Phase> availableZonePhases = Lists.newArrayListWithCapacity(3);
        Integer parentId = zoneDto.getParentId();

        if (parentId == null) {
            availableZoneTypes = Lists.newArrayList(ZoneType.values());
            availableZonePhases.addAll(Lists.newArrayList(Phase.getRealPhases()));
            MessageSourceAccessor messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(userContext);
            String root = messageSourceAccessor.getMessage("yukon.web.modules.capcontrol.ivvc.zoneWizard.label.creatingAsRootZone");
            model.addAttribute("parentZoneName", root);
        } else {
            AbstractZone parentZone = zoneDtoHelper.getAbstractZoneFromZoneId(parentId, 
                                                                              userContext.getYukonUser());
            availableZoneTypes = zoneDtoHelper.getAvailableChildZoneTypesFromParentZoneType(parentZone.getZoneType());
            availableZonePhases = zoneDtoHelper.getAvailableChildPhasesFromParentZone(parentZone);
            model.addAttribute("parentZoneName", parentZone.getName());
        }

        model.addAttribute("zoneDto", zoneDto);
        model.addAttribute("availableZoneTypes", availableZoneTypes);
        model.addAttribute("availableZonePhases", availableZonePhases);
        addZoneEnums(model);

        return "ivvc/zoneWizardType.jsp";
    }

    @RequestMapping("wizardTypeSelected")
    public String wizardTypeSelected(HttpServletRequest request, ModelMap model,
                                     YukonUserContext userContext, ZoneType zoneType) {
        AbstractZone zoneDto =
            getBoundAbstractZoneFromZoneType(request, model, userContext, zoneType);
        if (zoneDto.getZoneType() == ZoneType.THREE_PHASE) {
            for (Phase phase : Phase.getRealPhases()) {
                zoneDto.getRegulators().put(phase, new RegulatorToZoneMapping(phase));
            }
        }
        setupZoneCreation(model, userContext, zoneDto);
        return "ivvc/zoneWizardDetails.jsp";
    }

    private void setupZoneCreation(ModelMap model, YukonUserContext userContext, AbstractZone zoneDto) {
        CapControlCache cache = filterCacheFactory.createUserAccessFilteredCache(userContext.getYukonUser());
        SubBus subBus = cache.getSubBus(zoneDto.getSubstationBusId());
        model.addAttribute("subBusName", subBus.getCcName());

        Integer parentId = zoneDto.getParentId();
        if (parentId != null) {
            AbstractZone parentZone = zoneDtoHelper.getAbstractZoneFromZoneId(parentId, 
                                                                              userContext.getYukonUser());
            model.addAttribute("parentZone", parentZone);
        } else {
            MessageSourceAccessor messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(userContext);
            String creatingAsRootString = messageSourceAccessor.getMessage("yukon.web.modules.capcontrol.ivvc.zoneWizard.label.creatingAsRootZone");
            model.addAttribute("parentZoneName", creatingAsRootString);
        }

        setupCommonAttributes(model, zoneDto);
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
    
    @RequestMapping("createZone")
    public String createZone(HttpServletResponse resp, ModelMap model, HttpServletRequest request, FlashScope flashScope, 
                             YukonUserContext userContext, ZoneType zoneType) throws IOException {
        AbstractZone zoneDto = AbstractZone.create(zoneType);
        BindingResult bindingResult = bind(model, request, zoneDto, "zoneDto", userContext);

        boolean noErrors = true;
        try {
            List<ZoneAssignmentCapBankRow> bankAssignments =
                getRemainingBankAssignments(zoneDto.getBankAssignments());
            List<ZoneAssignmentPointRow> pointAssignments =
                getRemainingPointAssignments(zoneDto.getPointAssignments());
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
            resp.setContentType("application/json");
            resp.getWriter().print(JsonUtils.toJson(Collections.singletonMap("action", "reload")));
            return null; 
        }

        setupZoneCreation(model, userContext, zoneDto);
        
        //Add Errors to flash scope
        List<MessageSourceResolvable> messages = YukonValidationUtils.errorsForBindingResult(bindingResult);
        flashScope.setMessage(messages, FlashScopeMessageType.ERROR);
        return "ivvc/zoneWizardDetails.jsp";
    }
    
    @RequestMapping("zoneEditor")
    public String zoneEditor(ModelMap model, LiteYukonUser user, int zoneId) {
        AbstractZone zoneDto = zoneDtoHelper.getAbstractZoneFromZoneId(zoneId, user);
        setupZoneEditor(model, user, zoneDto);
        return "ivvc/zoneWizardDetails.jsp";
    }
    
    private void setupZoneEditor(ModelMap model, LiteYukonUser user, AbstractZone zoneDto) {
        Integer parentId = zoneDto.getParentId();
        if (parentId != null) {
            AbstractZone parentZone = zoneDtoHelper.getAbstractZoneFromZoneId(parentId, user);
            model.addAttribute("parentZone", parentZone);
        }

        setupCommonAttributes(model, zoneDto);
        CapControlCache cache = filterCacheFactory.createUserAccessFilteredCache(user);
        SubBus subBus = cache.getSubBus(zoneDto.getSubstationBusId());
        model.addAttribute("subBusName", subBus.getCcName());
        model.addAttribute("mode", PageEditMode.EDIT.name());
        //get possible parent zones
        model.addAttribute("parentZones", zoneDtoHelper.getAvailableParentZonesForZone(zoneDto));
    }
    
    private void setupCommonAttributes(ModelMap model, AbstractZone zoneDto) {
        model.addAttribute("zoneDto", zoneDto);
        boolean phaseUneditable = isPhaseUneditable(zoneDto.getZoneType());
        model.addAttribute("phaseUneditable", phaseUneditable);
        List<Phase> phases = Lists.newArrayList(Phase.getRealPhases());
        model.addAttribute("phases", phases);
        List<Integer> usedPointIds = zoneService.getAllUsedPointIds();
        model.addAttribute("usedPointIds", usedPointIds);
        addZoneEnums(model);
    }

    private List<Phase> getPhasesForZoneTypeAndPhase(ZoneType zoneType, Phase phase) {
        List<Phase> phases = Lists.newArrayListWithCapacity(3);
        if (zoneType == ZoneType.SINGLE_PHASE) {
            phases.add(phase);
        } else {
            phases.addAll(Lists.newArrayList(Phase.getRealPhases()));
        }
        
        return phases;
    }

    private List<ZoneAssignmentCapBankRow> getRemainingBankAssignments(List<ZoneAssignmentCapBankRow> bankAssignments) {
    	List<ZoneAssignmentCapBankRow> remainingBankAssignments = Lists.newArrayList(); 
    	for (ZoneAssignmentCapBankRow bankRow : bankAssignments) {
    		if (!bankRow.isDeletion()) {
    			remainingBankAssignments.add(bankRow);
    		}
    	}
    	return remainingBankAssignments;
    }
    
    private List<ZoneAssignmentPointRow> getRemainingPointAssignments(List<ZoneAssignmentPointRow> pointAssignments) {
    	List<ZoneAssignmentPointRow> remainingPointAssignments = Lists.newArrayList();
    	for (ZoneAssignmentPointRow pointRow : pointAssignments) {
    		if (!pointRow.isDeletion()) {
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

    @RequestMapping("updateZone")
    public String updateZone(HttpServletResponse resp, ModelMap model, HttpServletRequest request, FlashScope flashScope, 
                             YukonUserContext userContext, ZoneType zoneType) throws IOException {

        AbstractZone zoneDto = AbstractZone.create(zoneType);
        BindingResult bindingResult = bind(model, request, zoneDto, "zoneDto", userContext);

        boolean noErrors = true;
        try {
            List<ZoneAssignmentCapBankRow> bankAssignments =
                getRemainingBankAssignments(zoneDto.getBankAssignments());
            List<ZoneAssignmentPointRow> pointAssignments =
                getRemainingPointAssignments(zoneDto.getPointAssignments());
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
            resp.setContentType("application/json");
            resp.getWriter().print(JsonUtils.toJson(Collections.singletonMap("action", "reload")));
            return null; 
        }

        bindingResult.reject("yukon.web.modules.capcontrol.ivvc.zoneWizard.error.problemSavingZone");

        setupZoneEditor(model, userContext.getYukonUser(), zoneDto);
        
        //Add Errors to flash scope
        List<MessageSourceResolvable> messages = YukonValidationUtils.errorsForBindingResult(bindingResult);
        flashScope.setMessage(messages, FlashScopeMessageType.ERROR);
        
        return "ivvc/zoneWizardDetails.jsp";        
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

    private AbstractZone getBoundAbstractZoneFromZoneType(HttpServletRequest request,
                                                          ModelMap model,
                                                          YukonUserContext userContext,
                                                          ZoneType zoneType) {
        AbstractZone zoneDto = AbstractZone.create(zoneType);
        bind(model, request, zoneDto, "zoneDto", userContext);
        return zoneDto;
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
    
    @RequestMapping("deleteZone")
    public String deleteZone(ModelMap modelMap, int zoneId, FlashScope flashScope) {
        Zone zone = zoneService.getZoneById(zoneId);
        try{
            zoneService.deleteZone(zoneId);
            flashScope.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.capcontrol.ivvc.zoneWizard.success.zoneDeleted"));
        } catch (DataIntegrityViolationException e) {
            flashScope.setError(new YukonMessageSourceResolvable("yukon.web.modules.capcontrol.ivvc.zoneWizard.error.zoneDeleteError"));
        }

        modelMap.addAttribute("subBusId", zone.getSubstationBusId());
        
        return "redirect:/capcontrol/ivvc/bus/detail";
    }
    
    @RequestMapping("addCapBank")
    public String addCapBank(ModelMap modelMap, LiteYukonUser user, int id, int itemIndex) {
        CapControlCache cache = filterCacheFactory.createUserAccessFilteredCache(user);

        CapBankToZoneMapping bankToZone = new CapBankToZoneMapping();
        bankToZone.setDeviceId(id);
        bankToZone.setGraphPositionOffset(0);
        ZoneAssignmentCapBankRow row = zoneDtoHelper.getBankAssignmentFromMapping(bankToZone, cache);        
        modelMap.addAttribute("row",row);
        modelMap.addAttribute("itemIndex", itemIndex);
        
        return "ivvc/addZoneCapBankRow.jsp";
    }
    
    @RequestMapping("addVoltagePoint")
    public String addVoltagePoint(ModelMap modelMap, LiteYukonUser user, int id, String zoneType, String phase, int itemIndex) {
    	PointToZoneMapping pointToZone = new PointToZoneMapping();
    	pointToZone.setPointId(id);
    	pointToZone.setGraphPositionOffset(0);
        ZoneAssignmentPointRow row = zoneDtoHelper.getPointAssignmentFromMapping(pointToZone);        
        modelMap.addAttribute("row",row);
        modelMap.addAttribute("itemIndex", itemIndex);

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
}
