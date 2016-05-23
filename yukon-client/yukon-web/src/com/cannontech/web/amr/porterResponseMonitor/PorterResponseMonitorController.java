package com.cannontech.web.amr.porterResponseMonitor;

import java.beans.PropertyEditorSupport;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
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
import org.springframework.web.bind.annotation.ResponseBody;

import com.cannontech.amr.MonitorEvaluatorStatus;
import com.cannontech.amr.errors.dao.DeviceErrorTranslatorDao;
import com.cannontech.amr.errors.model.DeviceErrorDescription;
import com.cannontech.amr.porterResponseMonitor.dao.PorterResponseMonitorDao;
import com.cannontech.amr.porterResponseMonitor.model.PorterResponseMonitor;
import com.cannontech.amr.porterResponseMonitor.model.PorterResponseMonitorDto;
import com.cannontech.amr.porterResponseMonitor.model.PorterResponseMonitorErrorCode;
import com.cannontech.amr.porterResponseMonitor.model.PorterResponseMonitorMatchStyle;
import com.cannontech.amr.porterResponseMonitor.model.PorterResponseMonitorRule;
import com.cannontech.amr.porterResponseMonitor.service.PorterResponseMonitorService;
import com.cannontech.clientutils.LogHelper;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.bulk.collection.device.DeviceGroupCollectionHelper;
import com.cannontech.common.bulk.collection.device.model.DeviceCollection;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.device.groups.service.DeviceGroupService;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.events.loggers.OutageEventLogService;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.common.pao.service.PointService;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.core.dao.DuplicateException;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.StateDao;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.data.lite.LiteState;
import com.cannontech.database.data.lite.LiteStateGroup;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.PageEditMode;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.common.flashScope.FlashScopeMessageType;
import com.cannontech.web.input.EnumPropertyEditor;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

@Controller
@RequestMapping("/porterResponseMonitor/*")
@CheckRoleProperty(YukonRoleProperty.PORTER_RESPONSE_MONITORING)
public class PorterResponseMonitorController {

    @Autowired private PorterResponseMonitorDao porterResponseMonitorDao;
    @Autowired private DeviceErrorTranslatorDao deviceErrorTranslatorDao;
    @Autowired private RolePropertyDao rolePropertyDao;
    @Autowired private PorterResponseMonitorService porterResponseMonitorService;
    @Autowired private OutageEventLogService outageEventLogService;
    @Autowired private AttributeService attributeService;
    @Autowired private DeviceGroupService deviceGroupService;
    @Autowired private DeviceGroupCollectionHelper deviceGroupCollectionHelper;
    @Autowired private PointService pointService;
    @Autowired private StateDao stateDao;
    @Autowired protected YukonUserContextMessageSourceResolver messageSourceResolver;
    
    private final static String baseKey = "yukon.web.modules.amr.porterResponseMonitor";
    private final Logger log = YukonLogManager.getLogger(PorterResponseMonitorController.class);
    
    private Validator nameValidator = new SimpleValidator<PorterResponseMonitor>(PorterResponseMonitor.class) {
        @Override
        public void doValidation(PorterResponseMonitor monitor, Errors errors) {
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "name","yukon.web.error.required");
            YukonValidationUtils.checkExceedsMaxLength(errors, "name", monitor.getName(), 50);
        }
    };

    private Validator nameAndRulesValidator = new SimpleValidator<PorterResponseMonitor>(PorterResponseMonitor.class) {
        @Override
        public void doValidation(PorterResponseMonitor monitor, Errors errors) {
            
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "name", "yukon.web.error.required");
            YukonValidationUtils.checkExceedsMaxLength(errors, "name", monitor.getName(), 50);
            
            // --- uniqueness checks ---

            List<Integer> orderList = Lists.newArrayList();
            List<PorterResponseMonitorRule> rules = monitor.getRules();
            for (int i = 0; i < rules.size(); i++) {
                PorterResponseMonitorRule rule = rules.get(i);
                List<PorterResponseMonitorErrorCode> errorCodes = rule.getErrorCodes();

                orderList.add(rule.getRuleOrder());

                // Error Code Uniqueness
                List<Integer> errorsList = Lists.newArrayList();
                for (PorterResponseMonitorErrorCode errorCode : errorCodes) {
                    errorsList.add(errorCode.getErrorCode());
                }
                if (containsDuplicates(errorsList)) {
                    // we have duplicate errors for this rule
                    errors.reject(baseKey + ".rulesTable.errorCodesFormat");
                }
            }

            // Order Uniqueness check
            // which should not be a problem now that I am normalizing the order in the Constructor
            // -- keeping this here in case that normalization is ever changed / removed
            if (containsDuplicates(orderList)) {
                // we have duplicate orders for this monitor
                errors.reject(baseKey + ".rulesTable.uniqueOrder");
            }
        }
    };

    private <T> boolean containsDuplicates(List<T> list) {
        Set<T> set = new HashSet<T>(list);
        if (set.size() < list.size()) {
            // duplicates were removed
            return true;
        }
        return false;
    }

    @RequestMapping("viewPage")
    public String viewPage(int monitorId, ModelMap model, YukonUserContext userContext, FlashScope flashScope) {

        setupViewPageModelMap(monitorId, model, userContext, flashScope);

        return "porterResponseMonitor/view.jsp";
    }

    @RequestMapping("editPage")
    public String editPage(int monitorId, ModelMap model) {

        PorterResponseMonitor monitor = porterResponseMonitorDao.getMonitorById(monitorId);
        PorterResponseMonitorDto monitorDto = new PorterResponseMonitorDto(monitor);
        setupEditPageModelMap(monitorDto, model);

        return "porterResponseMonitor/edit.jsp";
    }

    @RequestMapping("createPage")
    public String createPage(ModelMap model) {

        setupCreatePageModelMap(model);

        return "porterResponseMonitor/create.jsp";
    }

    @RequestMapping("create")
    public String create(@ModelAttribute("monitor") PorterResponseMonitor monitor, BindingResult bindingResult, ModelMap modelMap,
            YukonUserContext userContext, FlashScope flashScope) {

        nameValidator.validate(monitor, bindingResult);

        if (bindingResult.hasErrors()) {
            List<MessageSourceResolvable> messages = YukonValidationUtils.errorsForBindingResult(bindingResult);
            flashScope.setMessage(messages, FlashScopeMessageType.ERROR);
            setupCreatePageModelMap(modelMap);
            return "porterResponseMonitor/create.jsp";
        }

        try {
            porterResponseMonitorService.create(monitor);
        } catch (DuplicateException e) {
            bindingResult.rejectValue("name", baseKey + ".alreadyExists");
            List<MessageSourceResolvable> messages = YukonValidationUtils.errorsForBindingResult(bindingResult);
            flashScope.setMessage(messages, FlashScopeMessageType.ERROR);
            setupCreatePageModelMap(modelMap);
            return "porterResponseMonitor/create.jsp";
        }

        modelMap.addAttribute("monitorId", monitor.getMonitorId());

        MessageSourceResolvable createMessage = new YukonMessageSourceResolvable("yukon.web.modules.amr.porterResponseMonitor.created");
        flashScope.setConfirm(createMessage);

        outageEventLogService.porterResponseMonitorCreated(monitor.getMonitorId(),
                                                           monitor.getName(),
                                                           monitor.getAttribute().getKey(), 
                                                           monitor.getStateGroup().toString(), 
                                                           monitor.getEvaluatorStatus().getDescription(),
                                                           userContext.getYukonUser());
        return "redirect:editPage";
    }

    @RequestMapping("update")
    public String update(@ModelAttribute("monitorDto") PorterResponseMonitorDto monitorDto,
                    BindingResult bindingResult,
                    ModelMap modelMap,
                    YukonUserContext userContext, 
                    FlashScope flashScope) {

        PorterResponseMonitor monitor = null;

        try {
            monitor = new PorterResponseMonitor(monitorDto);
        } catch (NumberFormatException e) {
            bindingResult.reject(baseKey + ".rulesTable.errorCodesFormat");
            setupErrorEditPageModelMap(monitorDto, modelMap, bindingResult, flashScope);
            return "porterResponseMonitor/edit.jsp";
        }

        nameAndRulesValidator.validate(monitor, bindingResult);

        if (bindingResult.hasErrors()) {
            setupErrorEditPageModelMap(monitorDto, modelMap, bindingResult, flashScope);
            return "porterResponseMonitor/edit.jsp";
        }

        try {
            porterResponseMonitorService.update(monitor);
        } catch (DuplicateException e) {
            bindingResult.rejectValue("name", baseKey + ".alreadyExists");
            setupErrorEditPageModelMap(monitorDto, modelMap, bindingResult, flashScope);
            return "porterResponseMonitor/edit.jsp";
        }

        modelMap.addAttribute("monitorId", monitor.getMonitorId());

        MessageSourceResolvable updateMessage = new YukonMessageSourceResolvable(
                "yukon.web.modules.amr.porterResponseMonitor.updated", monitorDto.getName());
        flashScope.setConfirm(updateMessage);
        
        outageEventLogService.porterResponseMonitorUpdated(monitorDto.getMonitorId(), 
                monitorDto.getName(),
                monitorDto.getAttribute().getKey(), 
                monitorDto.getStateGroup().toString(), 
                monitorDto.getEvaluatorStatus().getDescription(),
                userContext.getYukonUser());
        return "redirect:viewPage";
    }

    @RequestMapping(value="update", params = "delete")
    public String delete(@ModelAttribute PorterResponseMonitorDto monitorDto, ModelMap modelMap, FlashScope flashScope, LiteYukonUser user) {

        porterResponseMonitorService.delete(monitorDto.getMonitorId());

        MessageSourceResolvable deleteMessage = new YukonMessageSourceResolvable(
                "yukon.web.modules.amr.porterResponseMonitor.deleted", monitorDto.getName());
        flashScope.setConfirm(deleteMessage);

        outageEventLogService.porterResponseMonitorDeleted(monitorDto.getMonitorId(),
                                                           monitorDto.getName(),
                                                           monitorDto.getAttribute().getKey(),
                                                           monitorDto.getStateGroup().toString(),
                                                           monitorDto.getEvaluatorStatus().getDescription(),
                                                           user);
        return "redirect:/meter/start";
    }

    @RequestMapping(value="update", params = "toggleEnabled")
    public String toggleEnabled(@ModelAttribute PorterResponseMonitorDto monitorDto, ModelMap modelMap, 
                    YukonUserContext userContext) {

        MonitorEvaluatorStatus status = monitorDto.getEvaluatorStatus();

        try {
            status = porterResponseMonitorService.toggleEnabled(monitorDto.getMonitorId());
            modelMap.addAttribute("monitorId", monitorDto.getMonitorId());
        } catch (NotFoundException e) {
            return "redirect:/meter/start";
        }

        outageEventLogService.porterResponseMonitorEnableDisable(monitorDto.getMonitorId(), status.name(), userContext.getYukonUser());

        return "redirect:editPage";
    }

    @RequestMapping("counts")
    public @ResponseBody Map<String, Object> counts(int monitorId, YukonUserContext userContext) {
        
        try { // really?
            Thread.sleep(2000);
        } catch (InterruptedException e) {}

        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        
        // Default values in case group does not exist.
        String supportedDevicesMessage = accessor.getMessage("yukon.common.na");
        int totalGroupCount = 0;
        int missingPointCount = 0;
        
        PorterResponseMonitor monitor = porterResponseMonitorDao.getMonitorById(monitorId);
        DeviceGroup group = deviceGroupService.findGroupName(monitor.getGroupName());
        if (group != null) {    // check if group no longer exists.
            totalGroupCount = deviceGroupService.getDeviceCount(Collections.singleton(group));
            List<SimpleDevice> supportedDevices = attributeService.getDevicesInGroupThatSupportAttribute(group, BuiltInAttribute.OUTAGE_STATUS);
            int existingPointCount = pointService.getCountOfGroupAttributeStateGroup(group,
                                                                monitor.getAttribute(),
                                                                monitor.getStateGroup());
            missingPointCount = supportedDevices.size() - existingPointCount;
            
            
            if (missingPointCount > 0) {
                String attributeString = accessor.getMessage("yukon.web.modules.amr.porterResponseMonitor." 
                        + monitor.getAttribute().getKey());
                supportedDevicesMessage = accessor.getMessage("yukon.web.modules.amr.porterResponseMonitor.supportedDevicesMessage", 
                        supportedDevices.size(), missingPointCount, attributeString);
            } else {
                supportedDevicesMessage = String.valueOf(supportedDevices.size());
            }
        } else {
            LogHelper.warn(log, "Device Group %s no longer exists. Porter Response Monitor %s is not monitoring any data.",
                    monitor.getGroupName(), monitor.getName());
        }
        
        Map<String, Object> object = Maps.newHashMapWithExpectedSize(3);
        object.put("totalGroupCount", totalGroupCount);
        object.put("supportedDevicesMessage", supportedDevicesMessage);
        object.put("missingPointCount", missingPointCount);
        
        return object;
    }

    private void setupCreatePageModelMap(ModelMap model) {
        PorterResponseMonitor monitor = new PorterResponseMonitor();
        LiteStateGroup outageStatusStageGroup = stateDao.getLiteStateGroup("Outage Status");
        monitor.setStateGroup(outageStatusStageGroup);
        model.addAttribute("monitor", monitor);
        model.addAttribute("mode", PageEditMode.CREATE);
    }

    private void setupViewPageModelMap(int monitorId, ModelMap model,
            YukonUserContext userContext, FlashScope flashScope) {

        PorterResponseMonitor monitor = porterResponseMonitorDao.getMonitorById(monitorId);
        PorterResponseMonitorDto monitorDto = new PorterResponseMonitorDto(monitor);

        if (monitorDto.getRules().isEmpty()) {
            MessageSourceResolvable noRulesMessage = new YukonMessageSourceResolvable(baseKey + ".rulesTable.noRules");
            flashScope.setWarning(noRulesMessage);
        }

        LiteYukonUser user = userContext.getYukonUser();
        boolean showAddRemovePoints = rolePropertyDao.checkProperty(YukonRoleProperty.ADD_REMOVE_POINTS, user);
        model.addAttribute("showAddRemovePoints", showAddRemovePoints);

        DeviceGroup group = deviceGroupService.findGroupName(monitor.getGroupName());
        if (group != null) {
            DeviceCollection deviceCollection = deviceGroupCollectionHelper.buildDeviceCollection(group);
            model.addAttribute("deviceCollection", deviceCollection);
        }

        MessageSourceAccessor messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        String attributeString = messageSourceAccessor.getMessage("yukon.web.modules.amr.porterResponseMonitor." + monitor.getAttribute().getKey());
        String supportedDevicesHelpText = messageSourceAccessor.getMessage("yukon.web.modules.amr.porterResponseMonitor.supportedDevicesHelpText", monitorDto.getGroupName(), attributeString);
        model.addAttribute("supportedDevicesHelpText", supportedDevicesHelpText);

        List<LiteState> states = monitorDto.getStateGroup().getStatesList();
        model.addAttribute("states", states);
        model.addAttribute("monitorDto", monitorDto);
        model.addAttribute("mode", PageEditMode.VIEW);
    }

    private void setupErrorEditPageModelMap(PorterResponseMonitorDto monitorDto, ModelMap model, 
                            BindingResult bindingResult, FlashScope flashScope) {
        List<MessageSourceResolvable> messages = YukonValidationUtils.errorsForBindingResult(bindingResult);
        flashScope.setMessage(messages, FlashScopeMessageType.ERROR);
        setupEditPageModelMap(monitorDto, model);
    }

    private void setupEditPageModelMap(PorterResponseMonitorDto monitorDto, ModelMap model) {
        Set<Attribute> allAttributes = attributeService.getReadableAttributes();
        model.addAttribute("allAttributes", allAttributes);
        List<PorterResponseMonitorMatchStyle> matchStyleChoices = getMatchStyleChoices();
        model.addAttribute("matchStyleChoices", matchStyleChoices);
        model.addAttribute("monitorDto", monitorDto);
        model.addAttribute("mode", PageEditMode.EDIT);
    }

    private List<PorterResponseMonitorMatchStyle> getMatchStyleChoices() {
        List<PorterResponseMonitorMatchStyle> matchStyleChoices = Lists.newArrayList();
        matchStyleChoices.add(PorterResponseMonitorMatchStyle.any);
        matchStyleChoices.add(PorterResponseMonitorMatchStyle.all);
        matchStyleChoices.add(PorterResponseMonitorMatchStyle.none);
        return matchStyleChoices;
    }

    @ModelAttribute("allErrors")
    public Iterable<DeviceErrorDescription> getAllErrors() {
        Iterable<DeviceErrorDescription> allErrors = deviceErrorTranslatorDao.getAllErrors();
        return allErrors;
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
        EnumPropertyEditor.register(binder, PorterResponseMonitorMatchStyle.class);
        binder.registerCustomEditor(LiteStateGroup.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String group) throws IllegalArgumentException {
                LiteStateGroup stateGroup = stateDao.getLiteStateGroup(group);
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