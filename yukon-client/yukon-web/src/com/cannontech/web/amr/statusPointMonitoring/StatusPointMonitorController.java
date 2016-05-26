package com.cannontech.web.amr.statusPointMonitoring;

import java.beans.PropertyEditorSupport;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.amr.MonitorEvaluatorStatus;
import com.cannontech.amr.statusPointMonitoring.dao.StatusPointMonitorDao;
import com.cannontech.amr.statusPointMonitoring.model.OutageActionType;
import com.cannontech.amr.statusPointMonitoring.model.StatusPointMonitor;
import com.cannontech.amr.statusPointMonitoring.model.StatusPointMonitorProcessor;
import com.cannontech.amr.statusPointMonitoring.model.StatusPointMonitorStateType;
import com.cannontech.amr.statusPointMonitoring.service.StatusPointMonitorService;
import com.cannontech.common.events.loggers.OutageEventLogService;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.common.pao.attribute.model.AttributeGroup;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.core.dao.DuplicateException;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.StateGroupDao;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.database.data.lite.LiteStateGroup;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.common.flashScope.FlashScopeMessageType;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.google.common.collect.Lists;

@Controller
@RequestMapping("/statusPointMonitoring/*")
@CheckRoleProperty(YukonRoleProperty.STATUS_POINT_MONITORING)
public class StatusPointMonitorController {
    
    private final static String baseKey = "yukon.web.modules.amr.statusPointMonitorEditor";
    
    @Autowired private StatusPointMonitorDao statusPointMonitorDao;
    @Autowired private StatusPointMonitorService statusPointMonitorService;
    @Autowired private AttributeService attributeService;
    @Autowired private StateGroupDao stateGroupDao;
    @Autowired private OutageEventLogService outageEventLogService;
    @Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;
    
    private final Validator createValidator = new SimpleValidator<StatusPointMonitor>(StatusPointMonitor.class) {
        @Override
        public void doValidation(StatusPointMonitor statusPointMonitor, Errors errors) {
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "statusPointMonitorName", baseKey + ".empty");
            YukonValidationUtils.checkExceedsMaxLength(errors, "statusPointMonitorName", statusPointMonitor.getStatusPointMonitorName(), 50);
        }
    };
    
    private final Validator updateValidator = new SimpleValidator<StatusPointMonitor>(StatusPointMonitor.class) {
        @Override
        public void doValidation(StatusPointMonitor statusPointMonitor, Errors errors) {
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "statusPointMonitorName", baseKey + ".empty");
            YukonValidationUtils.checkExceedsMaxLength(errors, "statusPointMonitorName", statusPointMonitor.getStatusPointMonitorName(), 50);
        }
    };
    
    @RequestMapping("viewPage")
    public String viewPage(int statusPointMonitorId, 
                          ModelMap model, 
                          YukonUserContext userContext,
                          FlashScope flashScope) {
        
        setupViewPageModelMap(statusPointMonitorId, model, userContext, flashScope);
        return "statusPointMonitoring/view.jsp";
    }
    
    @RequestMapping("creationPage")
    public String creationPage(ModelMap modelMap, YukonUserContext userContext) {
        
        StatusPointMonitor statusPointMonitor = new StatusPointMonitor();
        setupCreationPageModelMap(statusPointMonitor, modelMap);
        
        return "statusPointMonitoring/create.jsp";
    }
    
    @RequestMapping("editPage")
    public String editPage(Integer statusPointMonitorId, ModelMap modelMap, YukonUserContext userContext, FlashScope flashScope) {
        
        StatusPointMonitor statusPointMonitor = statusPointMonitorDao.getStatusPointMonitorById(statusPointMonitorId);
        setupEditPageModelMap(statusPointMonitor, modelMap, userContext);
        
        return "statusPointMonitoring/edit.jsp";
    }
    
    @RequestMapping(value="create", params="cancel")
    public String cancel() {
        return "redirect:/meter/start";
    }
    
    @RequestMapping("create")
    public String create(@ModelAttribute StatusPointMonitor statusPointMonitor,
                                     BindingResult bindingResult,
                                     ModelMap modelMap, 
                                     YukonUserContext userContext,
                                     FlashScope flashScope, HttpServletRequest request) {
        
        createValidator.validate(statusPointMonitor, bindingResult);
          
        if (bindingResult.hasErrors()) {
            List<MessageSourceResolvable> messages = YukonValidationUtils.errorsForBindingResult(bindingResult);
            flashScope.setMessage(messages, FlashScopeMessageType.ERROR);
            setupCreationPageModelMap(statusPointMonitor, modelMap);
            return "statusPointMonitoring/create.jsp";
        }
        
        try {
            statusPointMonitorService.create(statusPointMonitor);
        } catch (DuplicateException e) {
            bindingResult.rejectValue("statusPointMonitorName", baseKey + ".alreadyExists");
            List<MessageSourceResolvable> messages = YukonValidationUtils.errorsForBindingResult(bindingResult);
            flashScope.setMessage(messages, FlashScopeMessageType.ERROR);
            setupCreationPageModelMap(statusPointMonitor, modelMap);
            return "statusPointMonitoring/create.jsp";
        }
        
        modelMap.addAttribute("statusPointMonitorId", statusPointMonitor.getStatusPointMonitorId());
        
        MessageSourceResolvable createMessage = new YukonMessageSourceResolvable("yukon.web.modules.amr.statusPointMonitor.created");
        flashScope.setConfirm(Collections.singletonList(createMessage));
        
        outageEventLogService.statusPointMonitorCreated(statusPointMonitor.getStatusPointMonitorId(), 
                                                        statusPointMonitor.getStatusPointMonitorName(), 
                                                        statusPointMonitor.getGroupName(), 
                                                        statusPointMonitor.getAttribute().getKey(), 
                                                        statusPointMonitor.getStateGroup().toString(), 
                                                        statusPointMonitor.getEvaluatorStatus().getDescription(), 
                                                        userContext.getYukonUser());
        return "redirect:/amr/statusPointMonitoring/editPage";
    }
    
    @RequestMapping("update")
    public String update(@ModelAttribute StatusPointMonitor statusPointMonitor,
                                                  BindingResult bindingResult,
                                                  ModelMap modelMap, 
                                                  YukonUserContext userContext,
                                                  FlashScope flashScope) {
        
        updateValidator.validate(statusPointMonitor, bindingResult);
          
        if (bindingResult.hasErrors()) {
            List<MessageSourceResolvable> messages = YukonValidationUtils.errorsForBindingResult(bindingResult);
            flashScope.setMessage(messages, FlashScopeMessageType.ERROR);
            setupEditPageModelMap(statusPointMonitor, modelMap, userContext);
            return "statusPointMonitoring/edit.jsp";
        }
        
        try {
            statusPointMonitorService.update(statusPointMonitor);
        } catch (DuplicateException e) {
            bindingResult.rejectValue("statusPointMonitorName", baseKey + ".alreadyExists");
            List<MessageSourceResolvable> messages = YukonValidationUtils.errorsForBindingResult(bindingResult);
            flashScope.setMessage(messages, FlashScopeMessageType.ERROR);
            setupEditPageModelMap(statusPointMonitor, modelMap, userContext);
            return "statusPointMonitoring/edit.jsp";
        }
        
        MessageSourceResolvable updateMessage = new YukonMessageSourceResolvable("yukon.web.modules.amr.statusPointMonitor.updated", statusPointMonitor.getStatusPointMonitorName());
        flashScope.setConfirm(Collections.singletonList(updateMessage));
        
        outageEventLogService.statusPointMonitorUpdated(statusPointMonitor.getStatusPointMonitorId(), 
                                                        statusPointMonitor.getStatusPointMonitorName(), 
                                                        statusPointMonitor.getGroupName(), 
                                                        statusPointMonitor.getAttribute().getKey(), 
                                                        statusPointMonitor.getStateGroup().toString(), 
                                                        statusPointMonitor.getEvaluatorStatus().getDescription(), 
                                                        userContext.getYukonUser());
        modelMap.addAttribute("statusPointMonitorId", statusPointMonitor.getStatusPointMonitorId());
        return "redirect:viewPage";
    }
    

    @RequestMapping("confirmDelete")
    public String confirmDelete(StatusPointMonitor statusPointMonitor,
                                ModelMap model,
                                YukonUserContext userContext) {
        
        model.addAttribute("statusPointMonitor", statusPointMonitor);
        return "statusPointMonitoring/confirmDelete.jsp";
    }
    
    @RequestMapping("delete")
    public String delete(Integer statusPointMonitorId,
                         ModelMap modelMap,
                         FlashScope flashScope,
                         YukonUserContext userContext) {

        StatusPointMonitor statusPointMonitor = statusPointMonitorDao.getStatusPointMonitorById(statusPointMonitorId);
        
        statusPointMonitorService.delete(statusPointMonitorId);
        
        MessageSourceResolvable deleteMessage = new YukonMessageSourceResolvable("yukon.web.modules.amr.statusPointMonitor.deleted", statusPointMonitor.getStatusPointMonitorName());
        flashScope.setConfirm(deleteMessage);
        
        outageEventLogService.statusPointMonitorDeleted(statusPointMonitor.getStatusPointMonitorId(), 
                                                        statusPointMonitor.getStatusPointMonitorName(), 
                                                        statusPointMonitor.getGroupName(), 
                                                        statusPointMonitor.getAttribute().getKey(), 
                                                        statusPointMonitor.getStateGroup().toString(), 
                                                        statusPointMonitor.getEvaluatorStatus().getDescription(), 
                                                        userContext.getYukonUser());
        return "redirect:/meter/start";
    }
    
    @RequestMapping("toggleEnabled")
    public String toggleEnabled(int statusPointMonitorId,
                                ModelMap modelMap,
                                YukonUserContext userContext) {
        
        StatusPointMonitor statusPointMonitor = statusPointMonitorDao.getStatusPointMonitorById(statusPointMonitorId);
        MonitorEvaluatorStatus status = statusPointMonitor.getEvaluatorStatus();
        
        try {
            status = statusPointMonitorService.toggleEnabled(statusPointMonitorId);
            modelMap.addAttribute("statusPointMonitorId", statusPointMonitorId);
        } catch (NotFoundException e) {
            return "redirect:/meter/start";
        }
        
        outageEventLogService.statusPointMonitorEnableDisable(statusPointMonitorId, 
                                                              status.name(), 
                                                              userContext.getYukonUser());
        
        return "redirect:editPage";
    }
    
    private void setupViewPageModelMap(int statusPointMonitorId, ModelMap model, YukonUserContext userContext, FlashScope flashScope) {
        
        StatusPointMonitor statusPointMonitor = statusPointMonitorDao.getStatusPointMonitorById(statusPointMonitorId);
        model.addAttribute("statusPointMonitor", statusPointMonitor);
        
        MessageSourceAccessor messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        
        List<String> prevStateStrings = Lists.newArrayList();
        List<String> nextStateStrings = Lists.newArrayList();
        for (StatusPointMonitorProcessor processor : statusPointMonitor.getProcessors()) {
            //previous state handling
            String prevStateString;
            if (processor.getPrevStateType() != StatusPointMonitorStateType.EXACT) {
                prevStateString = messageSourceAccessor.getMessage(processor.getPrevStateType());
            } else {
                prevStateString =
                    stateGroupDao.findLiteState(statusPointMonitor.getStateGroup().getStateGroupID(),
                        processor.transientGetPrevStateInt()).getStateText();
            }
            prevStateStrings.add(prevStateString);
            
            //next state handling
            String nextStateString;
            if (processor.getNextStateType() != StatusPointMonitorStateType.EXACT) {
                nextStateString = messageSourceAccessor.getMessage(processor.getNextStateType());
            } else {
                nextStateString =
                    stateGroupDao.findLiteState(statusPointMonitor.getStateGroup().getStateGroupID(),
                        processor.transientGetNextStateInt()).getStateText();
            }
            nextStateStrings.add(nextStateString);
        }
        
        model.addAttribute("prevStateStrings", prevStateStrings);
        model.addAttribute("nextStateStrings", nextStateStrings);
        
        if (statusPointMonitor.getProcessors().isEmpty()) {
            MessageSourceResolvable noProcessorsMessage = new YukonMessageSourceResolvable("yukon.web.modules.amr.statusPointMonitorView.stateActionsTable.noProcessors");
            flashScope.setWarning(Collections.singletonList(noProcessorsMessage));
        }
    }
    
    private void setupCreationPageModelMap(StatusPointMonitor statusPointMonitor, ModelMap modelMap) {
        
        modelMap.addAttribute("statusPointMonitor", statusPointMonitor);
        
        // state groups
        List<LiteStateGroup> stateGroupList = stateGroupDao.getAllStateGroups();
        modelMap.addAttribute("stateGroups", stateGroupList);
    }

    private void setupEditPageModelMap(StatusPointMonitor statusPointMonitor, ModelMap modelMap, YukonUserContext userContext) {
        
        modelMap.addAttribute("statusPointMonitor", statusPointMonitor);
        
        Set<BuiltInAttribute> allStatusAttributes = BuiltInAttribute.getAllStatusTypes();
        
        Map<AttributeGroup, List<BuiltInAttribute>> allGroupedReadableAttributes = attributeService.
                getGroupedAttributeMapFromCollection(allStatusAttributes, userContext); 
        
        modelMap.addAttribute("allGroupedReadableAttributes", allGroupedReadableAttributes);
        
        modelMap.addAttribute("dontCare", StatusPointMonitorStateType.DONT_CARE);
        modelMap.addAttribute("difference", StatusPointMonitorStateType.DIFFERENCE);
        
        //Outage Event Types
        List<String> eventTypes = new ArrayList<String>();
        eventTypes.add(OutageActionType.NoResponse.name());
        eventTypes.add(OutageActionType.Outage.name());
        eventTypes.add(OutageActionType.Restoration.name());
        modelMap.addAttribute("eventTypes", eventTypes);
    }
    
    @InitBinder
    public void setupBinder(WebDataBinder binder) {
        binder.registerCustomEditor(Attribute.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String attr) throws IllegalArgumentException {
                Attribute attribute = attributeService.resolveAttributeName(attr);
                setValue(attribute);
            }
            @Override
            public String getAsText() {
                Attribute attr = (Attribute) getValue();
                return attr.getKey();
            }
        });
        binder.registerCustomEditor(LiteStateGroup.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String group) throws IllegalArgumentException {
                LiteStateGroup stateGroup = stateGroupDao.getStateGroup(group);
                setValue(stateGroup);
            }
            @Override
            public String getAsText() {
                LiteStateGroup stateGroup = (LiteStateGroup) getValue();
                return String.valueOf(stateGroup.getStateGroupName());
            }
        });
    }
    
}