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
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

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
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.StateGroupDao;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.database.data.lite.LiteStateGroup;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.amr.monitor.validators.StatusPointMonitorValidator;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.common.flashScope.FlashScopeMessageType;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.google.common.collect.Lists;

@Controller
@RequestMapping("/statusPointMonitoring/*")
@CheckRoleProperty(YukonRoleProperty.STATUS_POINT_MONITORING)
public class StatusPointMonitorController {
    
    @Autowired private StatusPointMonitorDao statusPointMonitorDao;
    @Autowired private StatusPointMonitorService statusPointMonitorService;
    @Autowired private AttributeService attributeService;
    @Autowired private StateGroupDao stateGroupDao;
    @Autowired private OutageEventLogService outageEventLogService;
    @Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;
    @Autowired private StatusPointMonitorValidator statusPointMonitorValidator;
    
    @RequestMapping(value = "viewPage", method = RequestMethod.GET)
    public String viewPage(int statusPointMonitorId, 
                          ModelMap model, 
                          YukonUserContext userContext,
                          FlashScope flashScope) {
        
        setupViewPageModelMap(statusPointMonitorId, model, userContext, flashScope);
        return "statusPointMonitoring/view.jsp";
    }
    
    @RequestMapping(value = "creationPage", method = RequestMethod.GET)
    public String creationPage(ModelMap modelMap, YukonUserContext userContext) {
        
        StatusPointMonitor statusPointMonitor = new StatusPointMonitor();
        setupCreationPageModelMap(statusPointMonitor, modelMap);
        
        return "statusPointMonitoring/create.jsp";
    }
    
    @RequestMapping(value = "editPage", method = RequestMethod.GET)
    public String editPage(Integer statusPointMonitorId, ModelMap modelMap, YukonUserContext userContext, FlashScope flashScope) {
        
        StatusPointMonitor statusPointMonitor = statusPointMonitorDao.getStatusPointMonitorById(statusPointMonitorId);
        setupEditPageModelMap(statusPointMonitor, modelMap, userContext);
        
        return "statusPointMonitoring/edit.jsp";
    }
    
    @RequestMapping(value="create", params="cancel", method = RequestMethod.POST)
    public String cancel() {
        return "redirect:/meter/start";
    }
    
    @RequestMapping(value = "create", method = RequestMethod.POST)
    public String create(@ModelAttribute StatusPointMonitor statusPointMonitor,
                                     BindingResult bindingResult,
                                     ModelMap modelMap, 
                                     YukonUserContext userContext,
                                     FlashScope flashScope, HttpServletRequest request) {
        
        statusPointMonitorValidator.validate(statusPointMonitor, bindingResult);
          
        if (bindingResult.hasErrors()) {
            List<MessageSourceResolvable> messages = YukonValidationUtils.errorsForBindingResult(bindingResult);
            flashScope.setMessage(messages, FlashScopeMessageType.ERROR);
            setupCreationPageModelMap(statusPointMonitor, modelMap);
            return "statusPointMonitoring/create.jsp";
        }
        
        statusPointMonitorService.create(statusPointMonitor);
        
        modelMap.addAttribute("statusPointMonitorId", statusPointMonitor.getStatusPointMonitorId());
        
        MessageSourceResolvable createMessage = new YukonMessageSourceResolvable("yukon.web.modules.amr.statusPointMonitor.created");
        flashScope.setConfirm(Collections.singletonList(createMessage));
        
        outageEventLogService.statusPointMonitorCreated(statusPointMonitor.getStatusPointMonitorId(), 
                                                        statusPointMonitor.getName(), 
                                                        statusPointMonitor.getGroupName(), 
                                                        statusPointMonitor.getAttribute().getKey(), 
                                                        statusPointMonitor.getStateGroup().toString(), 
                                                        statusPointMonitor.getEvaluatorStatus().getDescription(), 
                                                        userContext.getYukonUser());
        return "redirect:/amr/statusPointMonitoring/editPage";
    }
    
    @RequestMapping(value = "update", method = RequestMethod.POST)
    public String update(@ModelAttribute StatusPointMonitor statusPointMonitor,
                                                  BindingResult bindingResult,
                                                  ModelMap modelMap, 
                                                  YukonUserContext userContext,
                                                  FlashScope flashScope) {

        statusPointMonitorValidator.validate(statusPointMonitor, bindingResult);
        
        if (bindingResult.hasErrors()) {
            List<MessageSourceResolvable> messages = YukonValidationUtils.errorsForBindingResult(bindingResult);
            flashScope.setMessage(messages, FlashScopeMessageType.ERROR);
            setupEditPageModelMap(statusPointMonitor, modelMap, userContext);
            return "statusPointMonitoring/edit.jsp";
        }
        
        statusPointMonitorService.update(statusPointMonitor);
        
        MessageSourceResolvable updateMessage = new YukonMessageSourceResolvable("yukon.web.modules.amr.statusPointMonitor.updated", statusPointMonitor.getName());
        flashScope.setConfirm(Collections.singletonList(updateMessage));
        
        outageEventLogService.statusPointMonitorUpdated(statusPointMonitor.getStatusPointMonitorId(), 
                                                        statusPointMonitor.getName(), 
                                                        statusPointMonitor.getGroupName(), 
                                                        statusPointMonitor.getAttribute().getKey(), 
                                                        statusPointMonitor.getStateGroup().toString(), 
                                                        statusPointMonitor.getEvaluatorStatus().getDescription(), 
                                                        userContext.getYukonUser());
        modelMap.addAttribute("statusPointMonitorId", statusPointMonitor.getStatusPointMonitorId());
        return "redirect:viewPage";
    }
    
    @RequestMapping(value = "delete", method = RequestMethod.POST)
    public String delete(Integer statusPointMonitorId,
                         ModelMap modelMap,
                         FlashScope flashScope,
                         YukonUserContext userContext) {

        StatusPointMonitor statusPointMonitor = statusPointMonitorDao.getStatusPointMonitorById(statusPointMonitorId);
        
        statusPointMonitorService.delete(statusPointMonitorId);
        
        MessageSourceResolvable deleteMessage = new YukonMessageSourceResolvable("yukon.web.modules.amr.statusPointMonitor.deleted", statusPointMonitor.getName());
        flashScope.setConfirm(deleteMessage);
        
        outageEventLogService.statusPointMonitorDeleted(statusPointMonitor.getStatusPointMonitorId(), 
                                                        statusPointMonitor.getName(), 
                                                        statusPointMonitor.getGroupName(), 
                                                        statusPointMonitor.getAttribute().getKey(), 
                                                        statusPointMonitor.getStateGroup().toString(), 
                                                        statusPointMonitor.getEvaluatorStatus().getDescription(), 
                                                        userContext.getYukonUser());
        return "redirect:/meter/start";
    }
    
    @RequestMapping(value = "toggleEnabled", method = RequestMethod.POST)
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