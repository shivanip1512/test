package com.cannontech.web.amr.deviceDataMonitor;

import java.beans.PropertyEditorSupport;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.log4j.Logger;
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
import org.springframework.web.bind.annotation.ResponseBody;

import com.cannontech.amr.deviceDataMonitor.dao.DeviceDataMonitorDao;
import com.cannontech.amr.deviceDataMonitor.model.DeviceDataMonitor;
import com.cannontech.amr.deviceDataMonitor.model.DeviceDataMonitorProcessor;
import com.cannontech.amr.deviceDataMonitor.service.DeviceDataMonitorService;
import com.cannontech.clientutils.LogHelper;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.bulk.collection.device.DeviceCollection;
import com.cannontech.common.bulk.collection.device.DeviceGroupCollectionHelper;
import com.cannontech.common.device.groups.editor.dao.DeviceGroupEditorDao;
import com.cannontech.common.device.groups.editor.model.StoredDeviceGroup;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.device.groups.service.DeviceGroupService;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.common.pao.attribute.model.AttributeGroup;
import com.cannontech.common.pao.attribute.model.AttributeStateGroup;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.common.pao.definition.attribute.lookup.AttributeDefinition;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDao;
import com.cannontech.common.pao.service.PointService;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.core.dao.DuplicateException;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.StateDao;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.database.data.lite.LiteState;
import com.cannontech.database.data.lite.LiteStateGroup;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.PageEditMode;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.common.flashScope.FlashScopeMessageType;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

@Controller
@RequestMapping("/deviceDataMonitor/*")
@CheckRoleProperty(YukonRoleProperty.DEVICE_DATA_MONITORING)
public class DeviceDataMonitorController {
	
    private static final String baseKey = "yukon.web.modules.amr.deviceDataMonitor";
    private static final Logger log = YukonLogManager.getLogger(DeviceDataMonitorController.class);

    @Autowired private AttributeService attributeService;
	@Autowired private DeviceDataMonitorDao deviceDataMonitorDao;
	@Autowired private DeviceGroupCollectionHelper deviceGroupCollectionHelper;
	@Autowired private DeviceDataMonitorService deviceDataMonitorService;
	@Autowired private DeviceGroupEditorDao deviceGroupEditorDao;
	@Autowired private DeviceGroupService deviceGroupService;
	@Autowired private PaoDefinitionDao paoDefinitionDao;
	@Autowired private PointService pointService;
	@Autowired private StateDao stateDao;
	@Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;
    
	private Validator nameValidator = new SimpleValidator<DeviceDataMonitor>(DeviceDataMonitor.class) {
        @Override
        public void doValidation(DeviceDataMonitor monitor, Errors errors) {
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "name", baseKey + ".empty");
            YukonValidationUtils.checkExceedsMaxLength(errors, "name", monitor.getName(), 100);
        }
    };

    @RequestMapping(method = RequestMethod.GET, value = "/view")
    public String view(int monitorId, 
                       ModelMap model, 
                       YukonUserContext userContext,
                       FlashScope flashScope) throws ServletRequestBindingException {
        DeviceDataMonitor monitor = deviceDataMonitorDao.getMonitorById(monitorId);
        setupCommonEditViewModelMap(monitor, model, userContext, flashScope);
        model.addAttribute("mode", PageEditMode.VIEW);
        return "deviceDataMonitor/view.jsp";
    }
    
    @RequestMapping(method = RequestMethod.GET, value = "/createPage")
    public String createPage(ModelMap modelMap, YukonUserContext userContext)
            throws ServletRequestBindingException {
        setupCreateModelMap(new DeviceDataMonitor(), modelMap, userContext);
        return "deviceDataMonitor/edit.jsp";
    }
    
    @RequestMapping(method = RequestMethod.GET, value = "/editPage")
    public String editPage(int monitorId, ModelMap modelMap, YukonUserContext userContext,
                           FlashScope flashScope) throws ServletRequestBindingException {
        DeviceDataMonitor monitor = deviceDataMonitorDao.getMonitorById(monitorId);
        setupEditModelMap(monitor, modelMap, flashScope, userContext);
        return "deviceDataMonitor/edit.jsp";
    }
    
    @RequestMapping(method = RequestMethod.POST, value = "/create")
    public String create(@ModelAttribute("monitor") DeviceDataMonitor monitor,
                                     BindingResult bindingResult,
                                     ModelMap modelMap, 
                                     YukonUserContext userContext,
                                     FlashScope flashScope, HttpServletRequest request) {
        
        nameValidator.validate(monitor, bindingResult);

        List<DeviceDataMonitorProcessor> remainingProcessors = getRemainingProcessors(monitor.getProcessors());
        monitor.setProcessors(remainingProcessors);

        if (bindingResult.hasErrors()) {
            List<MessageSourceResolvable> messages = YukonValidationUtils.errorsForBindingResult(bindingResult);
            flashScope.setMessage(messages, FlashScopeMessageType.ERROR);
            setupCreateModelMap(monitor, modelMap, userContext);
            return "deviceDataMonitor/edit.jsp";
        }
        
        try {
            monitor = deviceDataMonitorService.saveAndProcess(monitor);
        } catch (DuplicateException e) {
            setupDuplicateMonitorError(monitor, bindingResult, flashScope);
            setupCreateModelMap(monitor, modelMap, userContext);
            return "deviceDataMonitor/edit.jsp";
        }
        
        MessageSourceResolvable createMessage = new YukonMessageSourceResolvable("yukon.web.modules.amr.deviceDataMonitor.created");
        flashScope.setConfirm(Collections.singletonList(createMessage));

        modelMap.addAttribute("monitorId", monitor.getId());
        return "redirect:/amr/deviceDataMonitor/editPage";
    }

    @RequestMapping(method = RequestMethod.POST, value = "/update")
    public String update(@ModelAttribute("monitor") DeviceDataMonitor monitor,
                                                  BindingResult bindingResult,
                                                  ModelMap modelMap, 
                                                  YukonUserContext userContext,
                                                  FlashScope flashScope) {
        nameValidator.validate(monitor, bindingResult);
          
        if (bindingResult.hasErrors()) {
            List<MessageSourceResolvable> messages = YukonValidationUtils.errorsForBindingResult(bindingResult);
            flashScope.setMessage(messages, FlashScopeMessageType.ERROR);
            setupEditModelMap(monitor, modelMap, flashScope, userContext);
            return "deviceDataMonitor/edit.jsp";
        }
        
        List<DeviceDataMonitorProcessor> remainingProcessors = getRemainingProcessors(monitor.getProcessors());
        monitor.setProcessors(remainingProcessors);
        
        try {
            deviceDataMonitorService.saveAndProcess(monitor);
        } catch (DuplicateException e) {
            setupDuplicateMonitorError(monitor, bindingResult, flashScope);
            setupEditModelMap(monitor, modelMap, flashScope, userContext);
            return "deviceDataMonitor/edit.jsp";
        }
        
        MessageSourceResolvable updateMessage = new YukonMessageSourceResolvable("yukon.web.modules.amr.deviceDataMonitor.updated", monitor.getName());
        flashScope.setConfirm(Collections.singletonList(updateMessage));

        modelMap.addAttribute("monitorId", monitor.getId());
        return "redirect:/amr/deviceDataMonitor/view";
    }

    @RequestMapping(method = RequestMethod.POST, value = "/delete")
	public String delete(int monitorId,
                         ModelMap modelMap,
                         FlashScope flashScope,
                         YukonUserContext userContext) throws ServletRequestBindingException {

	    DeviceDataMonitor monitor = deviceDataMonitorDao.getMonitorById(monitorId);
	    deviceDataMonitorDao.deleteMonitor(monitorId);
        
        MessageSourceResolvable deleteMessage = new YukonMessageSourceResolvable("yukon.web.modules.amr.deviceDataMonitor.deleted", monitor.getName());
        flashScope.setConfirm(deleteMessage);
        return "redirect:/meter/start";
	}
	
	@RequestMapping(method = RequestMethod.POST, value = "/toggleEnabled")
	public String toggleEnabled(int monitorId,
	                            ModelMap modelMap,
	                            FlashScope flashScope,
	                            YukonUserContext userContext) throws ServletRequestBindingException {
	    boolean enabled;
        try {
            enabled = deviceDataMonitorService.toggleEnabled(monitorId);
	        modelMap.addAttribute("monitorId", monitorId);
        } catch (NotFoundException e) {
            return "redirect:/meter/start";
        }
        
        MessageSourceResolvable updateMessage = new YukonMessageSourceResolvable("yukon.web.modules.amr.deviceDataMonitor.updatedEnabled" + enabled);
        flashScope.setConfirm(Collections.singletonList(updateMessage));
        
        return "redirect:editPage";
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "/getStatesForGroup")
	public @ResponseBody JSONObject getStatesForGroup(int stateGroupId) {
	    LiteStateGroup stateGroup = stateDao.getLiteStateGroup(stateGroupId);
	    JSONObject statesContainer = new JSONObject();
	    JSONArray states = new JSONArray();
	    for (LiteState state : stateGroup.getStatesList()) {
	        JSONObject stateObj = new JSONObject();
	        stateObj.put("id", state.getLiteID());
	        stateObj.put("text", state.getStateText());
	        stateObj.put("raw_text", state.getStateRawState());
	        states.add(stateObj);
	    }
	    statesContainer.put("states", states);
	    return statesContainer;
	}

	private void setupDuplicateMonitorError(DeviceDataMonitor monitor, BindingResult bindingResult,
	                                        FlashScope flashScope) {
        log.info("caught error when trying to save device data monitor with duplicate name: " + monitor.getName());
        bindingResult.rejectValue("name", baseKey + ".alreadyExists");
        List<MessageSourceResolvable> messages = YukonValidationUtils.errorsForBindingResult(bindingResult);
        flashScope.setMessage(messages, FlashScopeMessageType.ERROR);
    }

    private List<DeviceDataMonitorProcessor> getRemainingProcessors(List<DeviceDataMonitorProcessor> processors) {
        List<DeviceDataMonitorProcessor> remainingProcessors = Lists.newArrayList();
        for (DeviceDataMonitorProcessor processor : processors) {
            if (!processor.isDeletion()) {
                remainingProcessors.add(processor);
            }
        }
        return remainingProcessors;
    }
    
    private void setupCreateModelMap(DeviceDataMonitor monitor, ModelMap model, YukonUserContext userContext) {
        model.addAttribute("monitor", monitor);
        
        int monitoringCount = 0;
        DeviceGroup monitoringGroup = deviceGroupService.findGroupName(monitor.getGroupName());
        if (monitoringGroup != null) {
            DeviceCollection deviceCollection = deviceGroupCollectionHelper.buildDeviceCollection(monitoringGroup);
            model.addAttribute("deviceCollection", deviceCollection);
            monitoringCount = deviceGroupService.getDeviceCount(Collections.singleton(monitoringGroup));
        }
        model.addAttribute("monitoringCount", monitoringCount);
        
        model.addAttribute("mode", PageEditMode.CREATE);
        setupAttributesAndStateGroups(model, userContext);
    }
    
    private void setupAttributesAndStateGroups(ModelMap model, YukonUserContext userContext) {
        // attributes
        Set<BuiltInAttribute> allReadableAttributes = BuiltInAttribute.getStandardGroupedAttributes().get(AttributeGroup.STATUS);
        Set<BuiltInAttribute> rfnEventStatusAttributes = BuiltInAttribute.getRfnEventStatusTypes();
        Set<BuiltInAttribute> allAttributes = Sets.newHashSet();
        allAttributes.addAll(allReadableAttributes);
        allAttributes.addAll(rfnEventStatusAttributes);
        Map<AttributeGroup, List<BuiltInAttribute>> allGroupedReadableAttributes = attributeService.
                getGroupedAttributeMapFromCollection(allAttributes, userContext);
        model.addAttribute("allGroupedReadableAttributes", allGroupedReadableAttributes);
        
        // state groups
        LiteStateGroup[] allStateGroups = stateDao.getAllStateGroups();
        List<LiteStateGroup> stateGroupList = Arrays.asList(allStateGroups);
        model.addAttribute("stateGroups", stateGroupList);
    }

    private void setupCommonEditViewModelMap(DeviceDataMonitor monitor, ModelMap model, YukonUserContext userContext, FlashScope flashScope) {
        model.addAttribute("monitor", monitor);
        
        int monitoringCount = 0;
        DeviceGroup monitoringGroup = deviceGroupService.findGroupName(monitor.getGroupName());
        if (monitoringGroup != null) {
            DeviceCollection deviceCollection = deviceGroupCollectionHelper.buildDeviceCollection(monitoringGroup);
            model.addAttribute("deviceCollection", deviceCollection);
            monitoringCount = deviceGroupService.getDeviceCount(Collections.singleton(monitoringGroup));
        }
        model.addAttribute("monitoringCount", monitoringCount);
        
        StoredDeviceGroup violationsDeviceGroup = deviceGroupEditorDao.getStoredGroup(monitor.getViolationsDeviceGroupPath(), true);
        model.addAttribute("violationsDeviceGroup", violationsDeviceGroup);

        boolean areViolationsBeingCalculated = deviceDataMonitorService.areViolationsBeingCalculatedForMonitor(monitor.getId());
        model.addAttribute("areViolationsBeingCalculated", areViolationsBeingCalculated);

        if (!areViolationsBeingCalculated) {
            int violationsCount = deviceGroupService.getDeviceCount(Collections.singleton(violationsDeviceGroup));
            model.addAttribute("violationsCount", violationsCount);
        }
    }

    private void setupEditModelMap(DeviceDataMonitor monitor, ModelMap model, FlashScope flashScope, YukonUserContext userContext) {
        model.addAttribute("mode", PageEditMode.EDIT);
        setupCommonEditViewModelMap(monitor, model, userContext, flashScope);
        setupAttributesAndStateGroups(model, userContext);
    }

    /* AJAX methods */
    
    @RequestMapping(method = RequestMethod.GET, value = "/getDeviceGroupCount")
    public @ResponseBody JSONObject getDeviceGroupCount(String groupName, YukonUserContext userContext) {
        int monitoringCount = 0;
        DeviceGroup monitoringGroup = deviceGroupService.findGroupName(groupName);
        if (monitoringGroup != null) {
            monitoringCount = deviceGroupService.getDeviceCount(Collections.singleton(monitoringGroup));
        }
        JSONObject obj = new JSONObject();
        obj.put("count", monitoringCount);
        return obj;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/getViolationsCount")
    public @ResponseBody JSONObject getViolationsCount(HttpServletRequest request, HttpServletResponse response,
                                                       int monitorId, YukonUserContext userContext) {
        String status;
        JSONObject obj = new JSONObject();
        if (deviceDataMonitorService.areViolationsBeingCalculatedForMonitor(monitorId)) {
            status = "working";
        } else {
            status = "done";
            int violationCount = deviceDataMonitorService.getMonitorViolationCountById(monitorId);
            obj.put("count", violationCount);
        }
        obj.put("status", status);
        return obj;
    }
    
    @RequestMapping(method = RequestMethod.GET, value = "/getSupportedCountsByMonitor")
    public @ResponseBody JSONObject getSupportedCountsByMonitor(@ModelAttribute DeviceDataMonitor monitor, YukonUserContext userContext) throws IOException {
        List<DeviceDataMonitorProcessor> remainingProcessors = getRemainingProcessors(monitor.getProcessors());
        monitor.setProcessors(remainingProcessors);
        return getSupportedCountsForMonitor(monitor, userContext);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/getSupportedCountsById")
    public @ResponseBody JSONObject getSupportedCountsById(int monitorId, YukonUserContext userContext) throws IOException {
        DeviceDataMonitor monitor = deviceDataMonitorDao.getMonitorById(monitorId);
        return getSupportedCountsForMonitor(monitor, userContext);
    }
    
    private class AttributeDefaultStateGroup extends AttributeStateGroup {
        private int defaultStateGroupId;
        public AttributeDefaultStateGroup(AttributeStateGroup attributeStateGroup, int defaultStateGroupId) {
            super(attributeStateGroup.getAttribute(), attributeStateGroup.getStateGroup());
            this.defaultStateGroupId = defaultStateGroupId;
        }
        public int getDefaultStateGroupId() {
            return this.defaultStateGroupId;
        }
    }

    private JSONObject getSupportedCountsForMonitor(DeviceDataMonitor monitor, YukonUserContext userContext) {
        MessageSourceAccessor messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        
        messageSourceAccessor.getMessage("yukon.web.defaults.na");
        
        Set<Integer> allSupportedPaoIds = Sets.newHashSet();
        Set<Integer> allMissingPaoIds = Sets.newHashSet();
        Map<AttributeDefaultStateGroup, Integer> attributeStateGroupMissingNumMap = Maps.newHashMap();
        DeviceGroup monitoringGroup = deviceGroupService.findGroupName(monitor.getGroupName());
        
        /* check if group no longer exists */
        if (monitoringGroup != null) {
            for (AttributeStateGroup attributeStateGroup : monitor.getAttributeStateGroups()) {
                List<SimpleDevice> supportedDevices = attributeService.getDevicesInGroupThatSupportAttribute(monitoringGroup, attributeStateGroup.getAttribute());
                List<Integer> supportedPaoIds = Lists.transform(supportedDevices, SimpleDevice.PAO_ID_FUNCITON);

                // we are only considering a device as "supported" if it supports ALL processors (hence the retainAll call)
                if (allSupportedPaoIds.isEmpty()) allSupportedPaoIds.addAll(supportedPaoIds);
                else allSupportedPaoIds.retainAll(supportedPaoIds);
                
//                allSupportedPaoIds.addAll(supportedPaoIds);

                List<Integer> existingPointPaoIds = pointService.getPaoIdsForGroupAttributeStateGroup(monitoringGroup,
                                                                                                     attributeStateGroup.getAttribute(),
                                                                                                     attributeStateGroup.getStateGroup());
                List<Integer> missingPaoPoints = Lists.newArrayList(supportedPaoIds);
                missingPaoPoints.removeAll(existingPointPaoIds);
                
//                if (allMissingPaoIds.isEmpty()) allMissingPaoIds.addAll(missingPaoPoints);
//                else allMissingPaoIds.retainAll(missingPaoPoints);
                allMissingPaoIds.addAll(missingPaoPoints);

                if (missingPaoPoints.size() > 0) {
                    SimpleDevice simpleDevice = Iterables.get(supportedDevices, 0);
                    AttributeDefinition attributeLookup = paoDefinitionDao.getAttributeLookup(simpleDevice.getDeviceType(), attributeStateGroup.getAttribute());
                    attributeStateGroupMissingNumMap.put(new AttributeDefaultStateGroup(attributeStateGroup, attributeLookup.getPointTemplate().getStateGroupId()), missingPaoPoints.size());
                }
            }
        } else {
            LogHelper.warn(log, "Device Group %s no longer exists. Device Data Monitor %s is not monitoring any data.",
                           monitor.getGroupName(), monitor.getName());
        }
        
        JSONObject returnObj = new JSONObject();
        JSONArray missingList = new JSONArray();

        if (allMissingPaoIds.size() > 0) {
            for (Entry<AttributeDefaultStateGroup, Integer> entry: attributeStateGroupMissingNumMap.entrySet()) {
                String missingStateGroup = entry.getKey().getStateGroup().getStateGroupName();
                if (entry.getKey().getStateGroup().getStateGroupID() != entry.getKey().getDefaultStateGroupId()) {
                    LiteStateGroup defaultStateGroup = stateDao.getLiteStateGroup(entry.getKey().getDefaultStateGroupId());
                    missingStateGroup = missingStateGroup + " - default: " + defaultStateGroup.getStateGroupName();
                }
                missingList.add(messageSourceAccessor.getMessage("yukon.common.attribute.builtInAttribute." + entry.getKey().getAttribute()) + " (" + missingStateGroup + ") : <span class='errorMessage'>" + entry.getValue() + "</span>");
            }
        }
        
        returnObj.put("totalSupportedCount", allSupportedPaoIds.size());
        returnObj.put("missingPointList", missingList);
        returnObj.put("totalMissingCount", allMissingPaoIds.size());
        return returnObj;
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
            public void setAsText(String groupId) throws IllegalArgumentException {
                LiteStateGroup stateGroup = stateDao.getLiteStateGroup(Integer.valueOf(groupId));
                setValue(stateGroup);
            }
            @Override
            public String getAsText() {
                LiteStateGroup stateGroup = (LiteStateGroup) getValue();
                return stateGroup.getStateGroupName();
            }
        });
        binder.registerCustomEditor(LiteState.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String stateGroupIdAndStateId) throws IllegalArgumentException {
                String stateGroupId = stateGroupIdAndStateId.substring(0, stateGroupIdAndStateId.indexOf(":"));
                String stateId = stateGroupIdAndStateId.substring(stateGroupIdAndStateId.indexOf(":")+1, stateGroupIdAndStateId.length());
                LiteStateGroup stateGroup = stateDao.getLiteStateGroup(Integer.valueOf(stateGroupId));
                LiteState liteState = null;
                for (LiteState state: stateGroup.getStatesList()) {
                    if (Integer.valueOf(stateId) == state.getLiteID()) {
                        liteState = state;
                        break;
                    }
                }
                setValue(liteState);
            }
            @Override
            public String getAsText() {
                LiteState state = (LiteState) getValue();
                return String.valueOf(state.getStateText());
            }
        });
	}
}