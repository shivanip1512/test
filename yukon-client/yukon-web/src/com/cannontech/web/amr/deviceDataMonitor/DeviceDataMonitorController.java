package com.cannontech.web.amr.deviceDataMonitor;

import static com.cannontech.web.amr.deviceDataMonitor.DeviceViolationEnum.ATTRIBUTE;
import static com.cannontech.web.amr.deviceDataMonitor.DeviceViolationEnum.POINT;
import static com.cannontech.web.amr.deviceDataMonitor.DeviceViolationEnum.STATE_GROUP;

import java.beans.PropertyEditorSupport;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.remoting.RemoteAccessException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cannontech.amr.deviceDataMonitor.dao.DeviceDataMonitorDao;
import com.cannontech.amr.deviceDataMonitor.model.DeviceDataMonitor;
import com.cannontech.amr.deviceDataMonitor.model.DeviceDataMonitorProcessor;
import com.cannontech.amr.deviceDataMonitor.model.ProcessorType;
import com.cannontech.amr.deviceDataMonitor.service.DeviceDataMonitorService;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.bulk.collection.DeviceMemoryCollectionProducer;
import com.cannontech.common.bulk.collection.device.DeviceGroupCollectionHelper;
import com.cannontech.common.bulk.collection.device.model.DeviceCollection;
import com.cannontech.common.device.groups.DeviceGroupInUse;
import com.cannontech.common.device.groups.DeviceGroupInUseException;
import com.cannontech.common.device.groups.editor.dao.DeviceGroupEditorDao;
import com.cannontech.common.device.groups.editor.dao.SystemGroupEnum;
import com.cannontech.common.device.groups.editor.model.StoredDeviceGroup;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.device.groups.service.DeviceGroupService;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.common.pao.attribute.model.AttributeGroup;
import com.cannontech.common.pao.attribute.model.AttributeStateGroup;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.common.pao.definition.model.PointIdentifier;
import com.cannontech.common.pao.service.PointService;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.StateGroupDao;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.database.data.lite.LiteState;
import com.cannontech.database.data.lite.LiteStateGroup;
import com.cannontech.i18n.WebMessageSourceResolvable;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.PageEditMode;
import com.cannontech.web.amr.monitor.validators.DeviceDataMonitorValidator;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.common.flashScope.FlashScopeListType;
import com.cannontech.web.common.flashScope.FlashScopeMessageType;
import com.cannontech.web.common.pao.PaoPopupHelper;
import com.cannontech.web.input.StateIdPairingPropertyEditor;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

@Controller
@CheckRoleProperty(YukonRoleProperty.DEVICE_DATA_MONITORING)
public class DeviceDataMonitorController {
    
    @Autowired private AttributeService attributeService;
    @Autowired private DeviceDataMonitorDao deviceDataMonitorDao;
    @Autowired private DeviceGroupCollectionHelper deviceGroupCollectionHelper;
    @Autowired private DeviceDataMonitorService monitorService;
    @Autowired private DeviceGroupEditorDao deviceGroupEditorDao;
    @Autowired private DeviceGroupService deviceGroupService;
    @Autowired private DeviceMemoryCollectionProducer collectionProducer;
    @Autowired private PaoPopupHelper popupHelper;
    @Autowired private PointService pointService;
    @Autowired private StateIdPairingPropertyEditor stateIdPairingPropertyEditor;
    @Autowired private StateGroupDao stateGroupDao;
    @Autowired private YukonUserContextMessageSourceResolver messageResolver;
    @Autowired private DeviceDataMonitorValidator validator;
    private static final Logger log = YukonLogManager.getLogger(DeviceDataMonitorController.class);
    
    private final int MAX_ROWS_FROM_ATTRIBUTE_POINT_QUERY = 3500;
    // Send this to i18n messages rather than a count so their logic knows "all" devices are one in a state.
    private final int MESSAGE_MAGIC_NUMBER__ALL = -1;
    // Send to i18n messages to indicate that the count is invalid, so don't report numbers.
    private final int MESSAGE_MAGIC_NUMBER_LIMITED_QUERY= -2;
    
    private static final String baseKey = "yukon.web.modules.amr.deviceDataMonitor";
  
    @RequestMapping(method = RequestMethod.GET, value = "deviceDataMonitor/view")
    public String view(int monitorId, ModelMap model) {
        
        DeviceDataMonitor monitor = deviceDataMonitorDao.getMonitorById(monitorId);
        setupCommonEditViewModelMap(monitor, model);
        model.addAttribute("mode", PageEditMode.VIEW);
        
        return "deviceDataMonitor/view.jsp";
    }
    
    @RequestMapping(method = RequestMethod.GET, value = "deviceDataMonitor/createPage")
    public String createPage(ModelMap model, YukonUserContext userContext) {
        setupCreateModelMap(new DeviceDataMonitor(), model, userContext);
        return "deviceDataMonitor/edit.jsp";
    }
    
    @RequestMapping(method = RequestMethod.GET, value = "deviceDataMonitor/editPage")
    public String editPage(int monitorId, ModelMap model, YukonUserContext userContext) {
        
        DeviceDataMonitor monitor = deviceDataMonitorDao.getMonitorById(monitorId);
        setupEditModelMap(monitor, model, userContext);
        
        return "deviceDataMonitor/edit.jsp";
    }
    
    @RequestMapping(method = RequestMethod.POST, value = "deviceDataMonitor/create")
    public String create(@ModelAttribute("monitor") DeviceDataMonitor monitor,
            BindingResult result,
            ModelMap model, 
            YukonUserContext userContext,
            FlashScope flash) {

        validator.validate(monitor, result);
        
        List<DeviceDataMonitorProcessor> remaining = getRemainingProcessors(monitor.getProcessors());
        monitor.setProcessors(remaining);
                
        if (result.hasErrors()) {
            
            List<MessageSourceResolvable> messages = YukonValidationUtils.errorsForBindingResult(result);
            flash.setMessage(messages, FlashScopeMessageType.ERROR);
            setupCreateModelMap(monitor, model, userContext);
            
            return "deviceDataMonitor/edit.jsp";
        }
        
        try {
            monitor = monitorService.create(monitor);
        } catch (RemoteAccessException e) {
            
            log.error("Cannot create monitor. Yukon Service Manager is down " 
                    + "or we are not configured properly to talk to it.", e);
            flash.setError(new YukonMessageSourceResolvable(baseKey + ".unableToUpdate.yukonServiceManager"));
            setupEditModelMap(monitor, model, userContext);
            
            return "deviceDataMonitor/edit.jsp";
        }
        
        MessageSourceResolvable createMessage = new YukonMessageSourceResolvable(baseKey + ".created");
        flash.setConfirm(Collections.singletonList(createMessage));
        model.addAttribute("monitorId", monitor.getId());
        return "redirect:/amr/deviceDataMonitor/view";
    }
    
    @RequestMapping(method = RequestMethod.POST, value = "deviceDataMonitor/update")
    public String update(@ModelAttribute("monitor") DeviceDataMonitor monitor,
                      BindingResult result,
                      ModelMap model, 
                      YukonUserContext userContext,
                      FlashScope flash) {
    	monitor.setProcessors(getRemainingProcessors(monitor.getProcessors()));
        validator.validate(monitor, result);
        
        if (result.hasErrors()) {
            List<MessageSourceResolvable> messages = YukonValidationUtils.errorsForBindingResult(result);
            flash.setMessage(messages, FlashScopeMessageType.ERROR);
            setupEditModelMap(monitor, model, userContext);
            return "deviceDataMonitor/edit.jsp";
        }
        
        List<DeviceDataMonitorProcessor> remainingProcessors = getRemainingProcessors(monitor.getProcessors());
        monitor.setProcessors(remainingProcessors);
        
        try {
            monitorService.update(monitor);
        } catch (RemoteAccessException e) {
            
            log.error("Cannot update monitor. Yukon Service Manager is down " 
                    + "or we are not configured properly to talk to it.", e);
            flash.setError(new YukonMessageSourceResolvable(baseKey + ".unableToUpdate.yukonServiceManager"));
            setupEditModelMap(monitor, model, userContext);
            
            return "deviceDataMonitor/edit.jsp";
        }
        
        MessageSourceResolvable updateMessage = new YukonMessageSourceResolvable(baseKey + ".updated", monitor.getName());
        flash.setConfirm(Collections.singletonList(updateMessage));
        model.addAttribute("monitorId", monitor.getId());
        return "redirect:/amr/deviceDataMonitor/view";
    }
    
    @RequestMapping(method = RequestMethod.POST, value = "deviceDataMonitor/delete")
    public String delete(int monitorId, YukonUserContext userContext, FlashScope flash) {
        
        DeviceDataMonitor monitor = deviceDataMonitorDao.getMonitorById(monitorId);
        try {
            monitorService.delete(monitor, userContext);
            MessageSourceResolvable deleteMessage = new YukonMessageSourceResolvable(baseKey + ".deleted", monitor.getName());
            flash.setConfirm(deleteMessage);
        } catch (DeviceGroupInUseException e) {
            log.error("Could not delete device data monitor : ", e);
            List<MessageSourceResolvable> messages = new ArrayList<>();
            messages.add(new WebMessageSourceResolvable(baseKey + ".delete.error", monitor.getName()));
            for (DeviceGroupInUse deviceGroupInUse : e.getReferences()) {
                MessageSourceResolvable message = new WebMessageSourceResolvable(e.getMessageKey(), deviceGroupInUse.getGroupName(), deviceGroupInUse.getReferenceType(), 
                                                                                   deviceGroupInUse.getName(), deviceGroupInUse.getOwner());
                messages.add(message);
            }
            flash.setError(messages, FlashScopeListType.NONE);
        }
        return "redirect:/meter/start";
    }
    
    @RequestMapping(method = RequestMethod.POST, value = "deviceDataMonitor/toggleEnabled")
    public String toggleEnabled(int monitorId, ModelMap model, FlashScope flash) {
        boolean enabled;
        try {
            enabled = monitorService.toggleEnabled(monitorId);
            model.addAttribute("monitorId", monitorId);
        } catch (NotFoundException e) {
            return "redirect:/meter/start";
        }
        
        MessageSourceResolvable updateMessage = new YukonMessageSourceResolvable(baseKey + ".updatedEnabled" + enabled);
        flash.setConfirm(Collections.singletonList(updateMessage));
        
        return "redirect:editPage";
    }
    
    @RequestMapping(method = RequestMethod.GET, value = "deviceDataMonitor/state-group-states")
    public @ResponseBody Map<String, ?> getStatesForGroup(int stateGroupId) {
        
        LiteStateGroup stateGroup = stateGroupDao.getStateGroup(stateGroupId);
        List<Object> states = new ArrayList<>();
        for (LiteState state : stateGroup.getStatesList()) {
            Map<String, Object> stateObj = Maps.newHashMapWithExpectedSize(3);
            stateObj.put("id", state.getLiteID());
            stateObj.put("text", state.getStateText());
            stateObj.put("raw_text", state.getStateRawState());
            states.add(stateObj);
        }
        
        return Collections.singletonMap("states", states);
    }
    
    @RequestMapping(method = RequestMethod.GET, value = "deviceDataMonitor/attribute-state-groups")
    public @ResponseBody Map<String, ?> getStateGroupsForAttribute(String groupName, BuiltInAttribute attribute) {
        
        List<LiteStateGroup> stateGroupList = attributeService.findStateGroups(groupName, attribute);
        List<Object> jsonSGs = new ArrayList<>();
        
        for (LiteStateGroup group : stateGroupList) {
            Map<String, Object> sgObj = Maps.newHashMapWithExpectedSize(2);
            sgObj.put("id", group.getStateGroupID());
            sgObj.put("name", group.getStateGroupName());
            jsonSGs.add(sgObj);
        }
        
        return Collections.singletonMap("stateGroups", jsonSGs);
    }
    
    @RequestMapping(method = RequestMethod.GET, value = "deviceDataMonitor/recalculate")
    public void recalculate(int monitorId, HttpServletResponse resp) {

        DeviceDataMonitor monitor = deviceDataMonitorDao.getMonitorById(monitorId);
        monitorService.recaclulate(monitor);
        resp.setStatus(HttpStatus.NO_CONTENT.value());
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
        if (StringUtils.isNotBlank(monitor.getGroupName())) {
            DeviceGroup monitoringGroup = deviceGroupService.findGroupName(monitor.getGroupName());
            if (monitoringGroup != null) {
                DeviceCollection deviceCollection = deviceGroupCollectionHelper.buildDeviceCollection(monitoringGroup);
                model.addAttribute("deviceCollection", deviceCollection);
                monitoringCount = deviceGroupService.getDeviceCount(Collections.singleton(monitoringGroup));
            }
        }
        
        model.addAttribute("monitoringCount", monitoringCount);
        model.addAttribute("mode", PageEditMode.CREATE);
        setupAttributes(model, userContext);
        setupProcessorTypes(model);
    }
    
    private void setupAttributes(ModelMap model, YukonUserContext userContext) {
        // attributes
        Set<BuiltInAttribute> allAttributes = BuiltInAttribute.getAllStatusTypes();
        
        Map<AttributeGroup, List<BuiltInAttribute>> allGroupedReadableAttributes = 
                attributeService.getGroupedAttributeMapFromCollection(allAttributes, userContext);
        
        model.addAttribute("allGroupedReadableAttributes", allGroupedReadableAttributes);

        // value attributes
        model.addAttribute("allGroupedValueAttributes", BuiltInAttribute.getValueGroupedAttributes());
    }
    
    private void setupProcessorTypes(ModelMap model) {
        List<ProcessorType> selectableTypes = new ArrayList<>();
        selectableTypes.add(ProcessorType.GREATER);
        selectableTypes.add(ProcessorType.LESS);
        selectableTypes.add(ProcessorType.RANGE);
        selectableTypes.add(ProcessorType.OUTSIDE);
        model.addAttribute("processorTypes", selectableTypes);
    }
    
    private void setupCommonEditViewModelMap(DeviceDataMonitor monitor, ModelMap model) {
        
        model.addAttribute("monitor", monitor);
        int monitoringCount = 0;
        DeviceGroup monitoringGroup = deviceGroupService.findGroupName(monitor.getGroupName());
        if (monitoringGroup != null) {
            DeviceCollection deviceCollection = deviceGroupCollectionHelper.buildDeviceCollection(monitoringGroup);
            model.addAttribute("deviceCollection", deviceCollection);
            monitoringCount = deviceGroupService.getDeviceCount(Collections.singleton(monitoringGroup));
        }
        model.addAttribute("monitoringCount", monitoringCount);
        model.addAttribute("violationsDeviceGroupPath", deviceGroupService.getFullPath(SystemGroupEnum.DEVICE_DATA)
                + monitor.getViolationsDeviceGroupName());
        StoredDeviceGroup violationsDeviceGroup = deviceGroupEditorDao.getStoredGroup(SystemGroupEnum.DEVICE_DATA, 
                monitor.getViolationsDeviceGroupName(), true);
        model.addAttribute("violationsDeviceGroup", violationsDeviceGroup);
    }
    
    private void setupEditModelMap(DeviceDataMonitor monitor, ModelMap model, YukonUserContext userContext) {
        
        model.addAttribute("mode", PageEditMode.EDIT);
        setupCommonEditViewModelMap(monitor, model);
        setupAttributes(model, userContext);
        Map<String, List<LiteStateGroup>> lookups = new HashMap<>();
        
        for (AttributeStateGroup asg : monitor.getAttributeStateGroups()) {
            final BuiltInAttribute attribute = (BuiltInAttribute) asg.getAttribute();
            final List<LiteStateGroup> stateGroupList = attributeService
                    .findStateGroups(monitor.getGroupName(), attribute);
            lookups.put(attribute.getKey(), stateGroupList);
        }
        setupProcessorTypes(model);
        model.addAttribute("mapAttributeKeyToStateGroupList", lookups);
    }
    
    /* AJAX methods */
    
    @RequestMapping(method = RequestMethod.GET, value = "deviceDataMonitor/device-group-count")
    public @ResponseBody Map<String, Integer> getDeviceGroupCount(String groupName) {
        
        int monitoringCount = 0;
        DeviceGroup monitoringGroup = deviceGroupService.findGroupName(groupName);
        if (monitoringGroup != null) {
            monitoringCount = deviceGroupService.getDeviceCount(Collections.singleton(monitoringGroup));
        }
        
        return Collections.singletonMap("count", monitoringCount);
    }
    
    @RequestMapping(method = RequestMethod.GET, value = "deviceDataMonitor/monitor-supported-count")
    public @ResponseBody Map<String, Object> getSupportedCountsByMonitor(@ModelAttribute DeviceDataMonitor monitor, 
            YukonUserContext userContext) {
        
        monitor.setProcessors(getRemainingProcessors(monitor.getProcessors()));
        return getSupportedCountsForMonitor(monitor, userContext);
    }
    
    @RequestMapping(method = RequestMethod.GET, value = "deviceDataMonitor/supported-count")
    public @ResponseBody Map<String, Object> getSupportedCountsById(int monitorId, YukonUserContext userContext) {
        
        DeviceDataMonitor monitor = deviceDataMonitorDao.getMonitorById(monitorId);
        return getSupportedCountsForMonitor(monitor, userContext);
    }
    
    private Map<String, Object> getSupportedCountsForMonitor(DeviceDataMonitor monitor, YukonUserContext userContext) {
        
        MessageSourceAccessor accessor = messageResolver.getMessageSourceAccessor(userContext);
        accessor.getMessage("yukon.common.na");
        
        DeviceGroup monitoring = deviceGroupService.findGroupName(monitor.getGroupName());
        long totalSupportedCount = 0;
        long totalDeviceCount = 0;
        
        Map<String, Object> returnObj = new HashMap<>();
        List<Object> missingList = new ArrayList<>();
        boolean allAttributesAreNull = false;
        
        if (monitoring != null) {
            
            Set<SimpleDevice> devices = deviceGroupService.getDevices(Collections.singleton(monitoring));
            Set<SimpleDevice> fullySupportedDevices = new HashSet<>(devices);
            totalDeviceCount = devices.size();
            
            int rowId = 0;
            int rowsMissingAttribute = 0;
            for (AttributeStateGroup stateGroup : monitor.getAttributeStateGroups()) {
                
                if (stateGroup.getAttribute() == null) {
                    rowId++;
                    rowsMissingAttribute++;
                    continue;
                }
                final List<Object> details = 
                        analyzeViolations(accessor, monitoring, devices, fullySupportedDevices, rowId, stateGroup);
                if (details != null && details.size() > 0) {
                    missingList.addAll(details);
                }
                
                rowId++;
            }
            
            // If no processor has an attribute selected...
            if (rowId > 0 && rowId == rowsMissingAttribute) {
                allAttributesAreNull = true;
            }
            
            totalSupportedCount = fullySupportedDevices.size();
        }
        
        returnObj.put("totalSupportedCount", totalSupportedCount);
        
        String keySuffix = "message";
        if (allAttributesAreNull) {
          keySuffix = "noAttributes";
        } else if (totalDeviceCount == 0) {
          keySuffix = "noDevices";
        } else if (monitor.getAttributeStateGroups().size() == 0) {
          keySuffix = "noProcessors";
        }
        
        final String keyPrefix = baseKey + ".fullySupported.";
        final long messageSignal = totalDeviceCount == totalSupportedCount ? MESSAGE_MAGIC_NUMBER__ALL : totalSupportedCount;
        returnObj.put("totalSupportedCountMessage", accessor.getMessage(keyPrefix + keySuffix, messageSignal));
        returnObj.put("totalSupportedCountHelpTitle", accessor.getMessage(keyPrefix + "help.title"));
        returnObj.put("totalSupportedCountHelp", accessor.getMessage(keyPrefix + "help.msg."
                        + keySuffix, totalDeviceCount, messageSignal));
        returnObj.put("missingPointList", missingList);
        returnObj.put("totalMissingCount", totalDeviceCount - totalSupportedCount);
        
        return returnObj;
    }

    /**
     * This method reduces 'fullySupportedDevices' as much as necessary: eg. to
     * at MOST the devices which support the specified Attribute + Point + State Group.
     * 
     * Strategy
     * We try comparing numbers as much as possible to skip unnecessary queries.
     * A = countDevicesInGroupWithAttributePointStateGroup()
     *      if this matches 'All Devices', then we don't have to calculate anything else
     * B = getDevicesInGroupThatSupportAttribute()
     *      if B == A, then we can leave now.
     * C = countDevicesInGroupWithAttributePoint()
     * D = findDeviceIdsInGroupWithAttributePointStateGroup()
     * 
     * @param accessor
     * @param monitoringGroup
     * @param allDevices
     *            Set<SimpleDevice> Used for the count and to query for the
     *            point if needed.
     * @param fullySupportedDevices
     * @param rowId
     * @param asg
     * 
     * @return List<Object> Details about the violations found for this row.
     * 
     * @postcondition fullySupportedDevices is reduced in size to the set of
     *                devices supporting the attribute, point, and stategroup
     *                specified.
     */
    private List<Object> analyzeViolations(
            final MessageSourceAccessor accessor,
            final DeviceGroup monitoringGroup,
            final Set<SimpleDevice> allDevices,
            Set<SimpleDevice> fullySupportedDevices, 
            final int rowId,
            final AttributeStateGroup asg) {

        long numTotal = allDevices.size();
        Attribute attribute = asg.getAttribute();
        LiteStateGroup stateGroup = asg.getStateGroup();

        // First: see how many support all 3 parts - attribute, point, and stateGroup
        List<Integer> supportAll;
        BuiltInAttribute bia = BuiltInAttribute.valueOf(attribute.getKey());
        if (bia.isStatusType()) {
            supportAll = pointService.findDeviceIdsInGroupWithAttributePointStateGroup(monitoringGroup, attribute, stateGroup);
        } else {
            supportAll = pointService.findDeviceIdsInGroupWithAttributePoint(monitoringGroup, attribute);
        }
        long numSupportAll = supportAll.size();

        if (numSupportAll == numTotal) {
            return null; // Everything is supported
        }

        // Create returnable Json data:
        List<Object> processorMissingList = new ArrayList<>();

        // Check vs the Attribute
        List<SimpleDevice> attrSupportedDevices = 
                    attributeService.getDevicesInGroupThatSupportAttribute(monitoringGroup, asg.getAttribute());
        long numSupportAttribute = attrSupportedDevices.size();
        
        long numMissingAttribute = numTotal - numSupportAttribute;
        if (numMissingAttribute > 0) {
            Map<String, Object> violationJson =
                    getViolationJson(numMissingAttribute, ATTRIBUTE, asg, rowId, accessor, numTotal, false);
            processorMissingList.add(violationJson);
        }

        fullySupportedDevices.retainAll(attrSupportedDevices);
        if (numSupportAll == numSupportAttribute) {
            // Only attributes were missing
            return processorMissingList;
        }

        // Check vs the Point
        boolean useLimitedQuery = numSupportAttribute > MAX_ROWS_FROM_ATTRIBUTE_POINT_QUERY;
        long numSupportPoint = useLimitedQuery ? 
                pointService.getCountDevicesInGroupWithAttributePoint(monitoringGroup,attribute, MAX_ROWS_FROM_ATTRIBUTE_POINT_QUERY)
                : pointService.getCountDevicesInGroupWithAttributePoint(monitoringGroup, attribute);

        long numMissingPoints = numSupportAttribute - numSupportPoint;
        if (numMissingPoints > 0) {
            Map<String, Object> violationJson =
                    getViolationJson(numMissingPoints, POINT, asg, rowId, accessor,
                                     numTotal, useLimitedQuery);
            violationJson.put("addPointsTxt", accessor.getMessage(baseKey + ".addPoints.label"));
            processorMissingList.add(violationJson);
        }

        // Check vs the State Group
        List<SimpleDevice> tmp = new ArrayList<>(fullySupportedDevices);
        for (SimpleDevice sd : tmp) {
            if (!supportAll.contains(sd.getPaoIdentifier().getPaoId())) {
                fullySupportedDevices.remove(sd);
            }
        }

        long numMissingStateGrp = numSupportPoint - numSupportAll;
        if (numMissingStateGrp > 0 && stateGroup != null && !useLimitedQuery) {
            Map<String, Object> violationJson =
                getViolationJson(numMissingStateGrp, STATE_GROUP, asg, rowId, accessor, numTotal, useLimitedQuery);
            processorMissingList.add(violationJson);
        }
        
        return processorMissingList;
    }
    
    private Map<String, Object> getViolationJson(long missingCount, 
            DeviceViolationEnum fieldType,
            AttributeStateGroup asg, 
            int rowId, 
            MessageSourceAccessor accessor,
            long deviceCount, 
            boolean useLimitedQuery) {
        
        String messageKey = baseKey + ".missing" + fieldType;
        
        long missingNumber = missingCount == deviceCount ? MESSAGE_MAGIC_NUMBER__ALL : missingCount;
        if (fieldType == POINT && useLimitedQuery) {
            missingNumber = MESSAGE_MAGIC_NUMBER_LIMITED_QUERY;
        }
        
        String fieldDisplayName = null;
        if (fieldType == DeviceViolationEnum.STATE_GROUP) {
            fieldDisplayName = asg.getStateGroup().getStateGroupName();
        } else {
            fieldDisplayName = accessor.getMessage("yukon.common.attribute.builtInAttribute." + asg.getAttribute());
        }
        LiteStateGroup sg = asg.getStateGroup();
        
        Map<String, Object> attr = new HashMap<>();
        attr.put("rowId", rowId);
        attr.put("stateGroupId", sg == null ? "" : sg.getLiteID());
        attr.put("attribute", asg.getAttribute());
        attr.put("fieldType", fieldType.name());
        attr.put("fieldDisplayName", fieldDisplayName);
        attr.put("useLimitedQuery", useLimitedQuery);
        attr.put("missingCount", missingCount);
        attr.put("missingText", accessor.getMessage(messageKey));
        attr.put("listTitle", accessor.getMessage(messageKey + ".list.title", fieldDisplayName));
        attr.put("helpTitle", accessor.getMessage(messageKey + ".help.title"));
        attr.put("helpText", accessor.getMessage(messageKey + ".help.msg", deviceCount, missingNumber, fieldDisplayName));
        
        return attr;
    }
    
    @RequestMapping("deviceDataMonitor/violating-devices")
    public String getDeviceListInViolation(ModelMap model,
            YukonUserContext context, 
            DeviceViolationEnum violationType,
            String groupName, 
            BuiltInAttribute attribute, 
            Integer stateGroup) {
        
        DeviceGroup monitoringGroup = deviceGroupService.findGroupName(groupName);
        Set<SimpleDevice> allDevices = deviceGroupService.getDevices(Collections.singleton(monitoringGroup));
        
        List<YukonPao> results = getViolationsForProblem(monitoringGroup, allDevices, attribute, stateGroup, violationType);
        DeviceCollection collection = collectionProducer.createDeviceCollection(results);
        popupHelper.buildPopupModel(collection, model, context);
        
        return "deviceDataMonitor/deviceViolationsPopup.jsp";
    }
    
    /**
     * Returns a list of devices within device group which are missing the given
     * attribute/point/state group per violationType.
     */
    private List<YukonPao> getViolationsForProblem(
            DeviceGroup monitoringGroup, 
            Set<SimpleDevice> allDevices,
            BuiltInAttribute attribute, 
            Integer stateGroupId,
            DeviceViolationEnum violationType) {
        
        // Check vs the Attribute
        List<SimpleDevice> attrSupportedDevices = attributeService
                .getDevicesInGroupThatSupportAttribute(monitoringGroup, attribute);
        
        // Will not allow casting, so must create a new collection.
        List<YukonPao> listMissingAttribute = new ArrayList<>();
        listMissingAttribute.addAll(allDevices);
        listMissingAttribute.removeAll(attrSupportedDevices);
        
        if (violationType == ATTRIBUTE) {
            return listMissingAttribute;
        }
        
        // Will not allow casting, so must create a new collection.
        List<YukonPao> hasPoint = new ArrayList<>();
        List<YukonPao> listMissingPoint = new ArrayList<>();
        hasPoint.addAll(attrSupportedDevices);
        List<Integer> deviceIdsSupportingAttributePoint = 
                pointService.findDeviceIdsInGroupWithAttributePoint(monitoringGroup, attribute);
        
        for (YukonPao pao : hasPoint) {
            if (!deviceIdsSupportingAttributePoint.contains(pao.getPaoIdentifier().getPaoId())) {
                listMissingPoint.add(pao);
            }
        }
        if (violationType == POINT) {
            return listMissingPoint;
        }
        
        hasPoint.removeAll(listMissingPoint);
        List<Integer> deviceIdsSupportingAttributePointStateGroup =
                pointService.findDeviceIdsInGroupWithAttributePointStateGroupId(monitoringGroup, attribute, stateGroupId);
        List<YukonPao> listMissingStateGroup = new ArrayList<>();
        listMissingStateGroup.addAll(hasPoint);
        
        for (YukonPao sd : hasPoint) {
            if (deviceIdsSupportingAttributePointStateGroup.contains(sd.getPaoIdentifier().getPaoId())) {
                listMissingStateGroup.remove(sd);
            }
        }
        
        if (violationType != STATE_GROUP) {
            throw new IllegalArgumentException("getViolationsForProblem(..) was passed a ViolationType " 
                    + "not in the 3-member enum: "+ violationType);
        }
        
        return listMissingStateGroup;
    }

    /**
     * As a shortcut this takes the related information, builds the list of devices,
     * and then forwards it to the Add Points function.
     */
    @RequestMapping("deviceDataMonitor/forwardToAddPoints")
    public String forwardToAddPoints(ModelMap model,
            YukonUserContext context,
            DeviceViolationEnum violationType,
            String groupName,
            BuiltInAttribute attribute,
            Integer stateGroup) {
        
        if (violationType != POINT) {
            throw new IllegalArgumentException("Does not make sense to forward to adding points" 
                    + " when the violation is for something other than points.");
        }
        
        DeviceGroup monitoringGroup = deviceGroupService.findGroupName(groupName);
        Set<SimpleDevice> allDevices = deviceGroupService.getDevices(Collections.singleton(monitoringGroup));
        
        List<YukonPao> results = getViolationsForProblem(monitoringGroup, allDevices, attribute, stateGroup, violationType);
        String ids = "";
        for (YukonPao device : results) {
            ids += ids.length() > 0 ? "," : "";
            ids += device.getPaoIdentifier().getPaoId();
        }
        
        List<PointIdentifier> pis = attributeService.findPointsForDevicesAndAttribute(results, attribute);
        String points = buildPointIdentifierParamString(pis);
        
        final String url = "forward:/bulk/addPoints/home?pointTypeOffsets=" + points 
                + "&collectionType=idList&idList.ids=" + ids;
        
        return url;
    }
    
    /**
     * Build the HTTP request parameter of a comma-separated list of [point type]:[offset]
     *      eg. "Status:101,Analog:43,Analog:5" is a list of 3 points
     */
    protected String buildPointIdentifierParamString(List<PointIdentifier> pointIdentifiers) {
        
        String points = "";
        for (PointIdentifier pi : pointIdentifiers) {
            points += points.length() > 0 ? "," : "";
            points += pi.getPointType().getPointTypeId() + "%3A" + pi.getOffset();
        }
        
        return points;
    }
    
    @InitBinder
    public void setupBinder(WebDataBinder binder) {
        
        binder.registerCustomEditor(Attribute.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String attr) throws IllegalArgumentException {
                Attribute attribute = null;
                try {
                    attribute = attributeService.resolveAttributeName(attr);
                } catch (Exception ee) {
                    return;
                } finally {
                    setValue(attribute);
                }
            }
            @Override
            public String getAsText() {
                Attribute attr = (Attribute) getValue();
                if (attr == null) {
                    return null;
                }
                return attr.getKey();
            }
        });
        
        binder.registerCustomEditor(LiteStateGroup.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String groupId) throws IllegalArgumentException {
                if (groupId == null || groupId.length() < 1) {
                    setValue(null);
                    return;
                }
                LiteStateGroup stateGroup = stateGroupDao.getStateGroup(Integer.valueOf(groupId));
                setValue(stateGroup);
            }
            @Override
            public String getAsText() {
                LiteStateGroup stateGroup = (LiteStateGroup) getValue();
                if (stateGroup == null) {
                    return null;
                }
                return stateGroup.getStateGroupName();
            }
        });
        
        binder.registerCustomEditor(LiteState.class, stateIdPairingPropertyEditor);
    }
    
}