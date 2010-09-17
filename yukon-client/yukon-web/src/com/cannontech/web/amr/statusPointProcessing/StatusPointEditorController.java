package com.cannontech.web.amr.statusPointProcessing;

import java.beans.PropertyEditorSupport;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.cannontech.amr.MonitorEvaluatorStatus;
import com.cannontech.amr.statusPointProcessing.dao.StatusPointMonitorDao;
import com.cannontech.amr.statusPointProcessing.model.OutageActionType;
import com.cannontech.amr.statusPointProcessing.model.StatusPointMonitor;
import com.cannontech.amr.statusPointProcessing.model.StatusPointMonitorStateType;
import com.cannontech.amr.statusPointProcessing.service.StatusPointMonitorService;
import com.cannontech.common.events.loggers.StatusPointMonitorEventLogService;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.core.dao.DuplicateException;
import com.cannontech.core.dao.StateDao;
import com.cannontech.core.dao.StatusPointMonitorNotFoundException;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.database.data.lite.LiteStateGroup;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.common.flashScope.FlashScopeMessageType;
import com.cannontech.web.security.annotation.CheckRoleProperty;

@Controller
@RequestMapping("/statusPointProcessing/*")
@CheckRoleProperty(YukonRoleProperty.STATUS_POINT_PROCESSING)
public class StatusPointEditorController {
	
    private final static String baseKey = "yukon.web.modules.amr.statusPointEditor";
	private StatusPointMonitorDao statusPointMonitorDao;
	private StatusPointMonitorService statusPointMonitorService;
	private AttributeService attributeService;
	private StateDao stateDao;
	private StatusPointMonitorEventLogService statusPointMonitorEventLogService;
	private YukonUserContextMessageSourceResolver messageSourceResolver;
    
	private Validator createValidator = new SimpleValidator<StatusPointMonitorDto>(StatusPointMonitorDto.class) {
        @Override
        public void doValidation(StatusPointMonitorDto statusPointMonitorDto, Errors errors) {
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "statusPointMonitorName", baseKey + ".empty");
            YukonValidationUtils.checkExceedsMaxLength(errors, "statusPointMonitorName", statusPointMonitorDto.getStatusPointMonitorName(), 50);
        }
    };
	
    private Validator updateValidator = new SimpleValidator<StatusPointMonitorDto>(StatusPointMonitorDto.class) {
        @Override
        public void doValidation(StatusPointMonitorDto statusPointMonitorDto, Errors errors) {
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "statusPointMonitorName", baseKey + ".empty");
            YukonValidationUtils.checkExceedsMaxLength(errors, "statusPointMonitorName", statusPointMonitorDto.getStatusPointMonitorName(), 50);
        }
    };
    
    @RequestMapping
    public String create(ModelMap modelMap, YukonUserContext userContext) throws ServletRequestBindingException {
        
        StatusPointMonitorDto statusPointMonitorDto = new StatusPointMonitorDto();
        setupCreationAttributes(statusPointMonitorDto, modelMap);
        
        return "statusPointProcessing/create.jsp";
    }
    
    @RequestMapping
    public String edit(Integer statusPointMonitorId, ModelMap modelMap, YukonUserContext userContext, FlashScope flashScope) throws ServletRequestBindingException {
        
        StatusPointMonitorDto statusPointMonitorDto = new StatusPointMonitorDto();
        statusPointMonitorDto.setStatusPointMonitorDto(statusPointMonitorDao.getStatusPointMonitorById(statusPointMonitorId));
        
        setupEditAttributes(statusPointMonitorDto, modelMap, userContext);
        return "statusPointProcessing/edit.jsp";
    }
    
    @RequestMapping(value="doCreate", method=RequestMethod.POST)
    public String doCreate(@ModelAttribute StatusPointMonitorDto statusPointMonitorDto,
                                     BindingResult bindingResult,
                                     ModelMap modelMap, 
                                     YukonUserContext userContext,
                                     FlashScope flashScope) {
        
        createValidator.validate(statusPointMonitorDto, bindingResult);
          
        if (bindingResult.hasErrors()) {
            List<MessageSourceResolvable> messages = YukonValidationUtils.errorsForBindingResult(bindingResult);
            flashScope.setMessage(messages, FlashScopeMessageType.ERROR);
            setupCreationAttributes(statusPointMonitorDto, modelMap);
            return "statusPointProcessing/create.jsp";
        }
        
        StatusPointMonitor statusPointMonitor = statusPointMonitorDto.getStatusPointMonitor();
        
        try {
            statusPointMonitorDao.save(statusPointMonitor);
        } catch (DuplicateException e) {
            bindingResult.rejectValue("statusPointMonitorName", baseKey + ".alreadyExists");
            List<MessageSourceResolvable> messages = YukonValidationUtils.errorsForBindingResult(bindingResult);
            flashScope.setMessage(messages, FlashScopeMessageType.ERROR);
            setupCreationAttributes(statusPointMonitorDto, modelMap);
            return "statusPointProcessing/create.jsp";
        }
        
        modelMap.addAttribute("statusPointMonitorId", statusPointMonitor.getStatusPointMonitorId());
        
        MessageSourceResolvable okMessage = new YukonMessageSourceResolvable("yukon.web.modules.amr.statusPointProcessing.statusPointMonitorCreated");
        
        flashScope.setConfirm(Collections.singletonList(okMessage));
        
        statusPointMonitorEventLogService.statusPointMonitorCreated(statusPointMonitor.getStatusPointMonitorId(), 
                                                                    statusPointMonitor.getStatusPointMonitorName(), 
                                                                    statusPointMonitor.getGroupName(), 
                                                                    statusPointMonitor.getAttribute().getKey(), 
                                                                    statusPointMonitor.getStateGroup().toString(), 
                                                                    statusPointMonitor.getEvaluatorStatus().getDescription(), 
                                                                    userContext.getYukonUser());

        return "redirect:/spring/amr/statusPointProcessing/edit";
    }
    
    @RequestMapping(value="update", method=RequestMethod.POST)
    public String update(@ModelAttribute StatusPointMonitorDto statusPointMonitorDto,
                                                  BindingResult bindingResult,
                                                  ModelMap modelMap, 
                                                  YukonUserContext userContext,
                                                  FlashScope flashScope) {
        
        updateValidator.validate(statusPointMonitorDto, bindingResult);
          
        if (bindingResult.hasErrors()) {
            List<MessageSourceResolvable> messages = YukonValidationUtils.errorsForBindingResult(bindingResult);
            flashScope.setMessage(messages, FlashScopeMessageType.ERROR);
            setupEditAttributes(statusPointMonitorDto, modelMap, userContext);
            return "statusPointProcessing/edit.jsp";
        }
        
        StatusPointMonitor statusPointMonitor = statusPointMonitorDto.getStatusPointMonitor();
        
        try {
            statusPointMonitorDao.save(statusPointMonitor);
        } catch (DuplicateException e) {
            bindingResult.rejectValue("statusPointMonitorName", baseKey + ".alreadyExists");
            List<MessageSourceResolvable> messages = YukonValidationUtils.errorsForBindingResult(bindingResult);
            flashScope.setMessage(messages, FlashScopeMessageType.ERROR);
            setupEditAttributes(statusPointMonitorDto, modelMap, userContext);
            return "statusPointProcessing/edit.jsp";
        }
        
        MessageSourceResolvable okMessage = new YukonMessageSourceResolvable("yukon.web.modules.amr.statusPointProcessing.statusPointMonitorUpdated");
        
        flashScope.setConfirm(Collections.singletonList(okMessage));
        
        statusPointMonitorEventLogService.statusPointMonitorUpdated(statusPointMonitor.getStatusPointMonitorId(), 
                                                                    statusPointMonitor.getStatusPointMonitorName(), 
                                                                    statusPointMonitor.getGroupName(), 
                                                                    statusPointMonitor.getAttribute().getKey(), 
                                                                    statusPointMonitor.getStateGroup().toString(), 
                                                                    statusPointMonitor.getEvaluatorStatus().getDescription(), 
                                                                    userContext.getYukonUser());
        
        return "redirect:/spring/meter/start";
    }
    
	private void setupCreationAttributes(StatusPointMonitorDto statusPointMonitorDto, ModelMap modelMap) {
	    
        modelMap.addAttribute("statusPointMonitorDto", statusPointMonitorDto);
        
        // state groups
        LiteStateGroup[] allStateGroups = stateDao.getAllStateGroups();
        List<LiteStateGroup> stateGroupList = Arrays.asList(allStateGroups);
        modelMap.addAttribute("stateGroups", stateGroupList);
	}

    private void setupEditAttributes(StatusPointMonitorDto statusPointMonitorDto, ModelMap modelMap, YukonUserContext userContext) {
        
        modelMap.addAttribute("statusPointMonitorDto", statusPointMonitorDto);
        
        // attributes
        Set<Attribute> allAttributes = attributeService.getReadableAttributes();
        modelMap.addAttribute("allAttributes", allAttributes);
        
        modelMap.addAttribute("dontCare", StatusPointMonitorStateType.DONT_CARE);
        modelMap.addAttribute("difference", StatusPointMonitorStateType.DIFFERENCE);
        
        //Outage Event Types
        List<String> eventTypes = new ArrayList<String>();
        eventTypes.add(OutageActionType.NoResponse.name());
        eventTypes.add(OutageActionType.Outage.name());
        eventTypes.add(OutageActionType.Restoration.name());
        modelMap.addAttribute("eventTypes", eventTypes);
        
        MessageSourceAccessor messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        
        if (statusPointMonitorDto.getEvaluatorStatus().equals(MonitorEvaluatorStatus.ENABLED.name())) {
            modelMap.addAttribute("statusPointMonitorStatus", messageSourceAccessor.getMessage("yukon.web.modules.amr.statusPointEditor.enabled"));
        } else {
            modelMap.addAttribute("statusPointMonitorStatus", messageSourceAccessor.getMessage("yukon.web.modules.amr.statusPointEditor.disabled"));
        }
    }
    
	@RequestMapping
    public String confirmDelete(StatusPointMonitorDto statusPointMonitorDto,
                                ModelMap model,
                                YukonUserContext userContext) {
        model.addAttribute("statusPointMonitorDto", statusPointMonitorDto);
        return "statusPointProcessing/confirmDelete.jsp";
    }
	
	@RequestMapping
	public String delete(Integer statusPointMonitorId,
                         ModelMap modelMap,
                         FlashScope flashScope,
                         YukonUserContext userContext) throws ServletRequestBindingException {

	    StatusPointMonitor statusPointMonitor = statusPointMonitorDao.getStatusPointMonitorById(statusPointMonitorId);
	    
	    statusPointMonitorService.delete(statusPointMonitorId);
        
        MessageSourceResolvable confirmationMsg = new YukonMessageSourceResolvable(baseKey + ".monitorDeleted", statusPointMonitor.getStatusPointMonitorName());
        flashScope.setConfirm(confirmationMsg);
        
        statusPointMonitorEventLogService.statusPointMonitorDeleted(statusPointMonitor.getStatusPointMonitorId(), 
                                                                    statusPointMonitor.getStatusPointMonitorName(), 
                                                                    statusPointMonitor.getGroupName(), 
                                                                    statusPointMonitor.getAttribute().getKey(), 
                                                                    statusPointMonitor.getStateGroup().toString(), 
                                                                    statusPointMonitor.getEvaluatorStatus().getDescription(), 
                                                                    userContext.getYukonUser());
        
        return "redirect:/spring/meter/start";
	}
	
	@RequestMapping
	public String toggleEnabled(int statusPointMonitorId,
	                            ModelMap modelMap,
	                            YukonUserContext userContext) throws ServletRequestBindingException {
	    
	    StatusPointMonitor statusPointMonitor = statusPointMonitorDao.getStatusPointMonitorById(statusPointMonitorId);
	    MonitorEvaluatorStatus status = statusPointMonitor.getEvaluatorStatus();
	    
        try {
            status = statusPointMonitorService.toggleEnabled(statusPointMonitorId);
	        modelMap.addAttribute("statusPointMonitorId", statusPointMonitorId);
        } catch (StatusPointMonitorNotFoundException e) {
            return "redirect:/spring/meter/start";
        }
        
        statusPointMonitorEventLogService.statusPointMonitorEnableDisable(statusPointMonitorId, 
                                                                          status.name(), 
                                                                          userContext.getYukonUser());
        
        return "redirect:edit";
	}
	
	@InitBinder
	public void setupBinder(WebDataBinder binder) {
	    binder.registerCustomEditor(Attribute.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String attr) throws IllegalArgumentException {
                
                Attribute attribute = attributeService.resolveAttributeName(attr);
                setValue(attribute);
            }
        });
        binder.registerCustomEditor(LiteStateGroup.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String group) throws IllegalArgumentException {
                
                LiteStateGroup stateGroup = stateDao.getLiteStateGroup(group);
                setValue(stateGroup);
            }
        });
	}
	
	@Autowired
	public void setStatusPointMonitorDao(StatusPointMonitorDao statusPointMonitorDao) {
		this.statusPointMonitorDao = statusPointMonitorDao;
	}
	
	@Autowired
	public void setStatusPointMonitorService(StatusPointMonitorService statusPointMonitorService) {
		this.statusPointMonitorService = statusPointMonitorService;
	}
	
	@Autowired
	public void setAttributeService(AttributeService attributeService) {
        this.attributeService = attributeService;
    }
	
	@Autowired
	public void setStateDao(StateDao stateDao) {
        this.stateDao = stateDao;
    }
	
	@Autowired
	public void setStatusPointMonitorEventLogService(
                                                     StatusPointMonitorEventLogService statusPointMonitorEventLogService) {
        this.statusPointMonitorEventLogService = statusPointMonitorEventLogService;
    }
	
	@Autowired
    public void setMessageSourceResolver(YukonUserContextMessageSourceResolver messageSourceResolver) {
        this.messageSourceResolver = messageSourceResolver;
    }
}