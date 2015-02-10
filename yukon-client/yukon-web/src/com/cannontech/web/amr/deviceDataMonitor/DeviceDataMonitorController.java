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

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.remoting.RemoteAccessException;
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
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cannontech.amr.deviceDataMonitor.dao.DeviceDataMonitorDao;
import com.cannontech.amr.deviceDataMonitor.model.DeviceDataMonitor;
import com.cannontech.amr.deviceDataMonitor.model.DeviceDataMonitorProcessor;
import com.cannontech.amr.deviceDataMonitor.service.DeviceDataMonitorService;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.bulk.collection.DeviceMemoryCollectionProducer;
import com.cannontech.common.bulk.collection.device.DeviceGroupCollectionHelper;
import com.cannontech.common.bulk.collection.device.model.DeviceCollection;
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
import com.cannontech.web.common.pao.PaoPopupHelper;
import com.cannontech.web.input.StateIdPairingPropertyEditor;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

@Controller
@RequestMapping("/deviceDataMonitor/*")
@CheckRoleProperty(YukonRoleProperty.DEVICE_DATA_MONITORING)
public class DeviceDataMonitorController {
    private static final Logger log = YukonLogManager.getLogger(DeviceDataMonitorController.class);

    @Autowired private AttributeService attributeService;
    @Autowired private DeviceDataMonitorDao deviceDataMonitorDao;
    @Autowired private DeviceGroupCollectionHelper deviceGroupCollectionHelper;
    @Autowired private DeviceDataMonitorService deviceDataMonitorService;
    @Autowired private DeviceGroupEditorDao deviceGroupEditorDao;
    @Autowired private DeviceGroupService deviceGroupService;
    @Autowired private DeviceMemoryCollectionProducer memoryCollectionProducer;
    @Autowired private PaoPopupHelper popupHelper;
    @Autowired private PointService pointService;
    @Autowired private StateIdPairingPropertyEditor stateIdPairingPropertyEditor;
    @Autowired private StateDao stateDao;
    @Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;

    private final int MAX_ROWS_FROM_ATTRIBUTE_POINT_QUERY = 3500;
    // Send this to i18n messages rather than a count so their logic knows "all" devices are one in a state.
    private final int MESSAGE_MAGIC_NUMBER__ALL = -1;
    // Send to i18n messages to indicate that the count is invalid, so don't report numbers.
    private final int MESSAGE_MAGIC_NUMBER_LIMITED_QUERY= -2;

    private static final String baseKey = "yukon.web.modules.amr.deviceDataMonitor";
    
    private Validator nameValidator = new SimpleValidator<DeviceDataMonitor>(DeviceDataMonitor.class) {
        @Override
        public void doValidation(DeviceDataMonitor monitor, Errors errors) {
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "name", baseKey + ".empty");
            YukonValidationUtils.checkExceedsMaxLength(errors, "name", monitor.getName(), 100);
        }
    };

    @RequestMapping(method = RequestMethod.GET, value = "/view")
    public String view(int monitorId, ModelMap model) {
        DeviceDataMonitor monitor = deviceDataMonitorDao.getMonitorById(monitorId);
        setupCommonEditViewModelMap(monitor, model);
        model.addAttribute("mode", PageEditMode.VIEW);
        return "deviceDataMonitor/view.jsp";
    }

    @RequestMapping(method = RequestMethod.GET, value = "/createPage")
    public String createPage(ModelMap modelMap, YukonUserContext userContext) {
        setupCreateModelMap(new DeviceDataMonitor(), modelMap, userContext);
        return "deviceDataMonitor/edit.jsp";
    }

    @RequestMapping(method = RequestMethod.GET, value = "/editPage")
    public String editPage(int monitorId, ModelMap modelMap, YukonUserContext userContext) {
        DeviceDataMonitor monitor = deviceDataMonitorDao.getMonitorById(monitorId);
        setupEditModelMap(monitor, modelMap, userContext);
        return "deviceDataMonitor/edit.jsp";
    }

    @RequestMapping(method = RequestMethod.POST, value = "/create")
    public String create(@ModelAttribute("monitor") DeviceDataMonitor monitor,
                                     BindingResult bindingResult,
                                     ModelMap modelMap, 
                                     YukonUserContext userContext,
                                     FlashScope flashScope) {

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
        } catch (RemoteAccessException e) {
            logErrorOrDebug("Cannot create monitor. Yukon Service Manager is down or we are not configured properly to talk to it.", e);
            flashScope.setMessage(new YukonMessageSourceResolvable(baseKey + ".unableToUpdate.yukonServiceManager"), FlashScopeMessageType.ERROR);
            setupEditModelMap(monitor, modelMap, userContext);
            return "deviceDataMonitor/edit.jsp";
        }

        MessageSourceResolvable createMessage = new YukonMessageSourceResolvable(baseKey + ".created");
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
            setupEditModelMap(monitor, modelMap, userContext);
            return "deviceDataMonitor/edit.jsp";
        }

        List<DeviceDataMonitorProcessor> remainingProcessors = getRemainingProcessors(monitor.getProcessors());
        monitor.setProcessors(remainingProcessors);

        try {
            deviceDataMonitorService.saveAndProcess(monitor);
        } catch (DuplicateException e) {
            setupDuplicateMonitorError(monitor, bindingResult, flashScope);
            setupEditModelMap(monitor, modelMap, userContext);
            return "deviceDataMonitor/edit.jsp";
        } catch (RemoteAccessException e) {
            logErrorOrDebug("Cannot update monitor. Yukon Service Manager is down or we are not configured properly to talk to it.", e);
            flashScope.setMessage(new YukonMessageSourceResolvable(baseKey + ".unableToUpdate.yukonServiceManager"), FlashScopeMessageType.ERROR);
            setupEditModelMap(monitor, modelMap, userContext);
            return "deviceDataMonitor/edit.jsp";
        }
        
        MessageSourceResolvable updateMessage = new YukonMessageSourceResolvable(baseKey + ".updated", monitor.getName());
        flashScope.setConfirm(Collections.singletonList(updateMessage));

        modelMap.addAttribute("monitorId", monitor.getId());
        return "redirect:/amr/deviceDataMonitor/view";
    }

    @RequestMapping(method = RequestMethod.POST, value = "/delete")
    public String delete(int monitorId, FlashScope flashScope) {

        DeviceDataMonitor monitor = deviceDataMonitorDao.getMonitorById(monitorId);
        deviceDataMonitorDao.deleteMonitor(monitorId);

        MessageSourceResolvable deleteMessage = new YukonMessageSourceResolvable(baseKey + ".deleted", monitor.getName());
        flashScope.setConfirm(deleteMessage);
        return "redirect:/meter/start";
    }

    @RequestMapping(method = RequestMethod.POST, value = "/toggleEnabled")
    public String toggleEnabled(int monitorId, ModelMap modelMap, FlashScope flashScope) {
        boolean enabled;
        try {
            enabled = deviceDataMonitorService.toggleEnabled(monitorId);
            modelMap.addAttribute("monitorId", monitorId);
        } catch (NotFoundException e) {
            return "redirect:/meter/start";
        }

        MessageSourceResolvable updateMessage = new YukonMessageSourceResolvable(baseKey + ".updatedEnabled" + enabled);
        flashScope.setConfirm(Collections.singletonList(updateMessage));

        return "redirect:editPage";
    }

    @RequestMapping(method = RequestMethod.GET, value = "/getStatesForGroup")
    public @ResponseBody Map<String, ?> getStatesForGroup(int stateGroupId) {
        LiteStateGroup stateGroup = stateDao.getLiteStateGroup(stateGroupId);
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

    @RequestMapping(method = RequestMethod.GET, value = "/getStateGroupsForAttribute")
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
    }

    private void setupAttributes(ModelMap model, YukonUserContext userContext) {
        // attributes
        Set<BuiltInAttribute> allReadableAttributes = BuiltInAttribute.getStandardGroupedAttributes().get(AttributeGroup.STATUS);
        Set<BuiltInAttribute> rfnEventStatusAttributes = BuiltInAttribute.getRfnEventStatusTypes();
        Set<BuiltInAttribute> allAttributes = Sets.newHashSet();
        allAttributes.addAll(allReadableAttributes);
        allAttributes.addAll(rfnEventStatusAttributes);
        Map<AttributeGroup, List<BuiltInAttribute>> allGroupedReadableAttributes = 
                attributeService.getGroupedAttributeMapFromCollection(allAttributes, userContext);
        model.addAttribute("allGroupedReadableAttributes", allGroupedReadableAttributes);
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
        StoredDeviceGroup violationsDeviceGroup = deviceGroupEditorDao.getStoredGroup(SystemGroupEnum.DEVICE_DATA, monitor.getViolationsDeviceGroupName(), true);
        model.addAttribute("violationsDeviceGroup", violationsDeviceGroup);

        boolean areViolationsBeingCalculated = false;
        try {
            areViolationsBeingCalculated = deviceDataMonitorService.areViolationsBeingCalculatedForMonitor(monitor.getId());
        } catch (RemoteAccessException e) {
            logErrorOrDebug("Yukon Service Manager is down or we are not configured properly to talk to it.", e);
        }
        model.addAttribute("areViolationsBeingCalculated", areViolationsBeingCalculated);

        if (!areViolationsBeingCalculated) {
            int violationsCount = deviceGroupService.getDeviceCount(Collections.singleton(violationsDeviceGroup));
            model.addAttribute("violationsCount", violationsCount);
        }
    }

    private void logErrorOrDebug(String logMsg, Exception e) {
        if (log.isDebugEnabled()) {
            log.debug(logMsg, e);
        } else {
            log.error(logMsg);
        }
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
        model.addAttribute("mapAttributeKeyToStateGroupList", lookups);
    }

    /* AJAX methods */

    @RequestMapping(method = RequestMethod.GET, value = "/getDeviceGroupCount")
    public @ResponseBody Map<String, Integer> getDeviceGroupCount(String groupName) {
        int monitoringCount = 0;
        DeviceGroup monitoringGroup = deviceGroupService.findGroupName(groupName);
        if (monitoringGroup != null) {
            monitoringCount = deviceGroupService.getDeviceCount(Collections.singleton(monitoringGroup));
        }
        return Collections.singletonMap("count", monitoringCount);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/getViolationsCount")
    public @ResponseBody Map<String, Object> getViolationsCount(int monitorId) {
        Map<String, Object> obj = new HashMap<>();
        if (deviceDataMonitorService.areViolationsBeingCalculatedForMonitor(monitorId)) {
            obj.put("status", "working");
        } else {
            obj.put("status", "done");
            obj.put("count", deviceDataMonitorService.getMonitorViolationCountById(monitorId));
        }
        return obj;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/getSupportedCountsByMonitor")
    public @ResponseBody Map<String, Object> getSupportedCountsByMonitor(@ModelAttribute DeviceDataMonitor monitor, YukonUserContext userContext) {
        List<DeviceDataMonitorProcessor> remainingProcessors = getRemainingProcessors(monitor.getProcessors());
        monitor.setProcessors(remainingProcessors);
        return getSupportedCountsForMonitor(monitor, userContext);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/getSupportedCountsById")
    public @ResponseBody Map<String, Object> getSupportedCountsById(int monitorId, YukonUserContext userContext) {
        DeviceDataMonitor monitor = deviceDataMonitorDao.getMonitorById(monitorId);
        return getSupportedCountsForMonitor(monitor, userContext);
    }

    /**
     * YUK-11992
     * 
     * @param monitor
     * @param userContext
     * @return Map<String, Object> "missingPointList" = [[ row #, field type (ATTRIBUTE,
     *         POINT, STATE_GROUP), field ID (eg. attribute Key), field
     *         Name/string, # missing], ..]
     */
    private Map<String, Object> getSupportedCountsForMonitor(DeviceDataMonitor monitor, YukonUserContext userContext) {
        MessageSourceAccessor messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(userContext);

        messageSourceAccessor.getMessage("yukon.web.defaults.na");

        DeviceGroup monitoringGroup = deviceGroupService.findGroupName(monitor.getGroupName());
        long totalSupportedCount = 0;
        long totalDeviceCount = 0;

        Map<String, Object> returnObj = new HashMap<>();
        List<Object> missingList = new ArrayList<>();
        boolean allAttributesAreNull = false;

        if (monitoringGroup != null) {
            Set<SimpleDevice> allDevices = deviceGroupService.getDevices(Collections.singleton(monitoringGroup));
            Set<SimpleDevice> fullySupportedDevices = new HashSet<>(allDevices);
            totalDeviceCount = allDevices.size();

            int rowId = 0;
            int rowsMissingAttribute = 0;
            for (AttributeStateGroup attributeStateGroup : monitor.getAttributeStateGroups()) {
                if (attributeStateGroup.getAttribute() == null) {
                    rowId++;
                    rowsMissingAttribute++;
                    continue;
                }
                final List<Object> details = analyzeViolations(messageSourceAccessor, monitoringGroup, 
                        allDevices, fullySupportedDevices, rowId, attributeStateGroup);
                if (details != null && details.size() > 0) {
                    missingList.addAll(details);
                }
                rowId++;
            } // ENDS for loop

            // If no processor has an attribute selected...
            if (rowId > 0 && rowId == rowsMissingAttribute) {
                allAttributesAreNull = true;
            }

            totalSupportedCount = fullySupportedDevices.size();
        }

        returnObj.put("totalSupportedCount", totalSupportedCount);
        final String keySuffix = (allAttributesAreNull) ? "noAttributes"
                : (totalDeviceCount == 0 ? "noDevices"
                        : (monitor.getAttributeStateGroups().size() == 0) ? "noProcessors"
                                : "message");
        final String keyPrefix = baseKey + ".fullySupported.";
        final long messageSignal = totalDeviceCount == totalSupportedCount ? MESSAGE_MAGIC_NUMBER__ALL : totalSupportedCount;
        returnObj.put("totalSupportedCountMessage", messageSourceAccessor.getMessage(keyPrefix + keySuffix, messageSignal));
        returnObj.put("totalSupportedCountHelpTitle", messageSourceAccessor.getMessage(keyPrefix + "help.title"));
        returnObj.put("totalSupportedCountHelp", messageSourceAccessor.getMessage(keyPrefix + "help.msg."
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
     * @param messageSourceAccessor
     * @param monitoringGroup
     * @param allDevices
     *            Set<SimpleDevice> Used for the count and to query for the
     *            point if needed.
     * @param fullySupportedDevices
     * @param rowId
     * @param attributeStateGroup
     * 
     * @return List<Object> Details about the violations found for this row.
     * 
     * @postcondition fullySupportedDevices is reduced in size to the set of
     *                devices supporting the attribute, point, and stategroup
     *                specified.
     */
    private List<Object> analyzeViolations(
            final MessageSourceAccessor messageSourceAccessor,
            final DeviceGroup monitoringGroup,
            final Set<SimpleDevice> allDevices,
            Set<SimpleDevice> fullySupportedDevices, final int rowId,
            final AttributeStateGroup attributeStateGroup) {

        long numTotal = allDevices.size();
        Attribute attribute = attributeStateGroup.getAttribute();
        LiteStateGroup stateGroup = attributeStateGroup.getStateGroup();

        // First: see how many support all 3 parts - attribute, point, and stateGroup
        List<Integer> supportAll = 
                pointService.findDeviceIdsInGroupWithAttributePointStateGroup(monitoringGroup, attribute, stateGroup);
        long numSupportAll = supportAll.size();

        if (numSupportAll == numTotal) {
            return null; // Everything is supported
        }

        // Create returnable Json data:
        List<Object> processorMissingList = new ArrayList<>();

        // Check vs the Attribute
        List<SimpleDevice> attrSupportedDevices = 
                    attributeService.getDevicesInGroupThatSupportAttribute(monitoringGroup, attributeStateGroup.getAttribute());
        long numSupportAttribute = attrSupportedDevices.size();
        String attributeName = messageSourceAccessor.getMessage("yukon.common.attribute.builtInAttribute." + attribute);

        long numMissingAttribute = numTotal - numSupportAttribute;
        if (numMissingAttribute > 0) {
            Map<String, Object> violationJson =
                    getViolationJson(numMissingAttribute, ATTRIBUTE, attributeName, rowId, messageSourceAccessor,
                                     numTotal, false);
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
                    getViolationJson(numMissingPoints, POINT, attributeName, rowId, messageSourceAccessor,
                                     numTotal, useLimitedQuery);
            violationJson.put("addPointsTxt", messageSourceAccessor.getMessage(baseKey + ".addPoints.label"));
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
                getViolationJson(numMissingStateGrp, STATE_GROUP, stateGroup.getStateGroupName(), rowId,
                                 messageSourceAccessor, numTotal, useLimitedQuery);
            processorMissingList.add(violationJson);
        }

        return processorMissingList;
    }

    private Map<String, Object> getViolationJson(long missingCount, DeviceViolationEnum fieldType,
                                                 String fieldDisplayName, int rowId, MessageSourceAccessor msg,
                                                 long deviceCount, boolean useLimitedQuery) {
        String messageKey = baseKey + ".missing" + fieldType;

        long missingNumber = missingCount == deviceCount ? MESSAGE_MAGIC_NUMBER__ALL : missingCount;
        if (fieldType == POINT && useLimitedQuery) {
            missingNumber = MESSAGE_MAGIC_NUMBER_LIMITED_QUERY;
        }

        Map<String, Object> attr = new HashMap<>();
        attr.put("rowId", rowId);
        attr.put("fieldType", fieldType.name());
        attr.put("fieldDisplayName", fieldDisplayName);
        attr.put("useLimitedQuery", useLimitedQuery);
        attr.put("missingCount", missingCount);
        attr.put("missingText", msg.getMessage(messageKey));
        attr.put("listTitle", msg.getMessage(messageKey + ".list.title", fieldDisplayName));
        attr.put("helpTitle", msg.getMessage(messageKey + ".help.title"));
        attr.put("helpText", msg.getMessage(messageKey + ".help.msg", deviceCount, missingNumber, fieldDisplayName));
        return attr;
    }

    /**
     * 
     * @param violationType
     * @param groupName
     * @param attribute
     * @param stateGroup
     * @return
     */
    @RequestMapping("getDeviceListInViolation")
    public String getDeviceListInViolation(ModelMap model,
            YukonUserContext context, DeviceViolationEnum violationType,
            String groupName, BuiltInAttribute attribute, Integer stateGroup) {

        DeviceGroup monitoringGroup = deviceGroupService.findGroupName(groupName);
        Set<SimpleDevice> allDevices = deviceGroupService.getDevices(Collections.singleton(monitoringGroup));

        List<YukonPao> results = getViolationsForProblem(monitoringGroup, allDevices, attribute, stateGroup, violationType);
        DeviceCollection collection = memoryCollectionProducer.createDeviceCollection(results);

        popupHelper.buildPopupModel(collection, model, context);
        return "deviceDataMonitor/deviceViolationsPopup.jsp";
    }

    /**
     * Returns a list of devices within device group which are missing the given
     * attribute/point/state group per violationType.
     * 
     * @param monitoringGroup       DeviceGroup
     * @param allDevices            Set<SimpleDevice>
     * @param attribute             Attribute
     * @param stateGroupId          Integer
     * @param violationType         DeviceViolationEnum
     * @return List<YukonPao>
     */
    private List<YukonPao> getViolationsForProblem(
            DeviceGroup monitoringGroup, Set<SimpleDevice> allDevices,
            BuiltInAttribute attribute, Integer stateGroupId,
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
        List<Integer> deviceIdsSupportingAttributePoint = pointService
                .findDeviceIdsInGroupWithAttributePoint(monitoringGroup, attribute);
        for (YukonPao sd : hasPoint) {
            boolean doesDeviceSupportAttrAndPt = deviceIdsSupportingAttributePoint.contains(sd.getPaoIdentifier().getPaoId()); 
            if (!doesDeviceSupportAttrAndPt) {
                listMissingPoint.add(sd);
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
            throw new IllegalArgumentException("getViolationsForProblem(..) was passed a ViolationType not in the 3-member enum: "+ violationType);
        }
        return listMissingStateGroup;
    } // END getViolationsForProblem

    /**
     * As a shortcut this takes the related information, builds the list of devices,
     * and then forwards it to the Add Points function.
     * 
     * @param model
     * @param context
     * @param violationType
     * @param groupName
     * @param attribute
     * @param stateGroup
     * @return
     */
    @RequestMapping("forwardToAddPoints")
    public String forwardToAddPoints(ModelMap model, YukonUserContext context,
            DeviceViolationEnum violationType, String groupName,
            BuiltInAttribute attribute, Integer stateGroup) {
        if (violationType != POINT) {
            throw new IllegalArgumentException("Does not make sense to forward to adding points when the violation is for something other than points.");
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

        final String url = "forward:/bulk/addPoints/home?pointTypeOffsets=" + points + "&collectionType=idList&idList.ids=" + ids;
        return url;
    }

    /**
     * Build the HTTP request parameter of a comma-separated list of [point type]:[offset]
     *      eg. "Status:101,Analog:43,Analog:5" is a list of 3 points
     * @param pointIdentifiers
     * @return
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
                LiteStateGroup stateGroup = stateDao.getLiteStateGroup(Integer.valueOf(groupId));
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