package com.cannontech.web.bulk;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.cannontech.common.alert.model.AlertType;
import com.cannontech.common.alert.service.AlertService;
import com.cannontech.common.bulk.BulkProcessor;
import com.cannontech.common.bulk.collection.device.DeviceCollectionFactory;
import com.cannontech.common.bulk.collection.device.DeviceGroupCollectionHelper;
import com.cannontech.common.bulk.collection.device.dao.CollectionActionDao;
import com.cannontech.common.bulk.collection.device.model.CollectionAction;
import com.cannontech.common.bulk.collection.device.model.CollectionActionBulkProcessorCallback;
import com.cannontech.common.bulk.collection.device.model.CollectionActionResult;
import com.cannontech.common.bulk.collection.device.model.DeviceCollection;
import com.cannontech.common.bulk.collection.device.service.CollectionActionService;
import com.cannontech.common.bulk.mapper.PassThroughMapper;
import com.cannontech.common.bulk.processor.Processor;
import com.cannontech.common.bulk.processor.ProcessorFactory;
import com.cannontech.common.device.commands.VerifyConfigCommandResult;
import com.cannontech.common.device.config.dao.DeviceConfigurationDao;
import com.cannontech.common.device.config.model.DeviceConfiguration;
import com.cannontech.common.device.config.model.LightDeviceConfiguration;
import com.cannontech.common.device.config.model.VerifyResult;
import com.cannontech.common.device.config.service.DeviceConfigService;
import com.cannontech.common.device.groups.editor.dao.DeviceGroupMemberEditorDao;
import com.cannontech.common.device.groups.editor.model.StoredDeviceGroup;
import com.cannontech.common.device.groups.service.TemporaryDeviceGroupService;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.events.loggers.DeviceConfigEventLogService;
import com.cannontech.common.util.ObjectMapper;
import com.cannontech.common.util.SimpleCallback;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.security.annotation.CheckRoleProperty;

@Controller
@RequestMapping("/config/*")
@CheckRoleProperty(YukonRoleProperty.MASS_CHANGE)
public class DeviceConfigController {

    @Resource(name="oneAtATimeProcessor") private BulkProcessor bulkProcessor;

    @Autowired private ProcessorFactory processorFactory;
    @Autowired private DeviceConfigurationDao deviceConfigurationDao;
    @Autowired private TemporaryDeviceGroupService temporaryDeviceGroupService;
    @Autowired private DeviceGroupMemberEditorDao deviceGroupMemberEditorDao;
    @Autowired private DeviceGroupCollectionHelper deviceGroupCollectionHelper;
    @Autowired private DeviceConfigService deviceConfigService;
    @Autowired private DeviceConfigEventLogService eventLogService;
    @Autowired private AlertService alertService;
    @Autowired private DeviceCollectionFactory deviceCollectionFactory;
    @Autowired protected CollectionActionService collectionActionService;
    @Autowired private YukonUserContextMessageSourceResolver messageResolver;
    @Autowired private CollectionActionDao collectionActionDao;

    @CheckRoleProperty(YukonRoleProperty.ASSIGN_CONFIG)
    @RequestMapping(value = "assignConfig", method = RequestMethod.GET)
    public String assignConfig(DeviceCollection deviceCollection, ModelMap model) throws ServletException {
        model.addAttribute("deviceCollection", deviceCollection);
        
        List<LightDeviceConfiguration> existingConfigs = deviceConfigurationDao.getAllLightDeviceConfigurations();
        model.addAttribute("existingConfigs", existingConfigs);
        
        return "config/assignConfig.jsp";
    }
    
    @CheckRoleProperty(YukonRoleProperty.ASSIGN_CONFIG)
    @RequestMapping(value = "doAssignConfig", method = RequestMethod.POST)
    public String doAssignConfig(ModelMap model, HttpServletRequest request, YukonUserContext userContext, int configuration) throws ServletException {
        DeviceCollection deviceCollection = deviceCollectionFactory.createDeviceCollection(request);

        DeviceConfiguration deviceConfig = deviceConfigurationDao.getDeviceConfiguration(configuration);
        eventLogService.assignConfigInitiated(deviceConfig.getName(), deviceCollection.getDeviceCount(), userContext.getYukonUser());
        Processor<SimpleDevice> processor = processorFactory.createAssignConfigurationToYukonDeviceProcessor(deviceConfig, userContext.getYukonUser());

        LinkedHashMap<String, String> userInputs = new LinkedHashMap<>();
        userInputs.put("Configuration", deviceConfig.getName());
        CollectionActionResult result = collectionActionService.createResult(CollectionAction.ASSIGN_CONFIG, userInputs,
            deviceCollection, userContext);
        ObjectMapper<SimpleDevice, SimpleDevice> mapper = new PassThroughMapper<>();
        bulkProcessor.backgroundBulkProcess(deviceCollection.iterator(), mapper, processor,
            new CollectionActionBulkProcessorCallback(result, collectionActionService, collectionActionDao));
   
        return "redirect:/bulk/progressReport/detail?key=" + result.getCacheKey();
    }
    
    @CheckRoleProperty(YukonRoleProperty.ASSIGN_CONFIG)
    @RequestMapping(value = "unassignConfig", method = RequestMethod.GET)
    public String unassignConfig(DeviceCollection deviceCollection, ModelMap model) throws ServletException {        
        model.addAttribute("deviceCollection", deviceCollection);
        return "config/unassignConfig.jsp";
    }
    
    @CheckRoleProperty(YukonRoleProperty.ASSIGN_CONFIG)
    @RequestMapping(value = "doUnassignConfig", method = RequestMethod.POST)
    public String doUnassignConfig(ModelMap model, HttpServletRequest request, YukonUserContext userContext) throws ServletException {
        DeviceCollection deviceCollection = deviceCollectionFactory.createDeviceCollection(request);

        eventLogService.unassignConfigInitiated(deviceCollection.getDeviceCount(), userContext.getYukonUser());
        Processor<SimpleDevice> processor = processorFactory.createUnassignConfigurationToYukonDeviceProcessor(userContext.getYukonUser());
        CollectionActionResult result = collectionActionService.createResult(CollectionAction.UNASSIGN_CONFIG, null,
            deviceCollection, userContext);
        ObjectMapper<SimpleDevice, SimpleDevice> mapper = new PassThroughMapper<>();
        bulkProcessor.backgroundBulkProcess(deviceCollection.iterator(), mapper, processor,
            new CollectionActionBulkProcessorCallback(result, collectionActionService, collectionActionDao));
        
        return "redirect:/bulk/progressReport/detail?key=" + result.getCacheKey();
    }

    @CheckRoleProperty(YukonRoleProperty.SEND_READ_CONFIG)
    @RequestMapping(value = "sendConfig", method = RequestMethod.GET)
    public String sendConfig(DeviceCollection deviceCollection, ModelMap model) throws ServletException {
        model.addAttribute("deviceCollection", deviceCollection);

        for (SimpleDevice sd : deviceCollection.getDeviceList()) {
            if (sd.getDeviceType().isRfn()) {
                model.addAttribute("someRF", true);
                break;
            }
        }

        return "config/sendConfig.jsp";
    }
    
    @RequestMapping(value = "verifyConfig", method = RequestMethod.GET)
    public String verifyConfig(DeviceCollection deviceCollection, ModelMap model) throws ServletException {
        model.addAttribute("deviceCollection", deviceCollection);
        return "config/verifyConfig.jsp";
    }
    
    @CheckRoleProperty(YukonRoleProperty.SEND_READ_CONFIG)
    @RequestMapping(value = "readConfig", method = RequestMethod.GET)
    public String readConfig(DeviceCollection deviceCollection, ModelMap model) throws ServletException {
        model.addAttribute("deviceCollection", deviceCollection);
        return "config/readConfig.jsp";
    }
    
    @RequestMapping(value = "doVerifyConfigs", method = RequestMethod.POST)
    public String doVerifyConfigs(DeviceCollection deviceCollection, LiteYukonUser user, ModelMap model) {
        model.addAttribute("deviceCollection", deviceCollection);
        
        VerifyConfigCommandResult result = deviceConfigService.verifyConfigs(deviceCollection, user);
        String reason =  result.getExceptionReason() == null? "": result.getExceptionReason();
        eventLogService.verifyConfigCompleted(result.getSuccessList().size(), 
                                                      result.getFailureList().size(),
                                                      result.getUnsupportedList().size(),
                                                      reason);
        
        StoredDeviceGroup successGroup = temporaryDeviceGroupService.createTempGroup();
        StoredDeviceGroup failureGroup = temporaryDeviceGroupService.createTempGroup();
        StoredDeviceGroup unsupportedGroup = temporaryDeviceGroupService.createTempGroup();
        deviceGroupMemberEditorDao.addDevices(successGroup, result.getSuccessList());
        deviceGroupMemberEditorDao.addDevices(failureGroup, result.getFailureList());
        deviceGroupMemberEditorDao.addDevices(unsupportedGroup, result.getUnsupportedList());
        DeviceCollection successCollection = deviceGroupCollectionHelper.buildDeviceCollection(successGroup);
        DeviceCollection failureCollection = deviceGroupCollectionHelper.buildDeviceCollection(failureGroup);
        DeviceCollection unsupportedCollection = deviceGroupCollectionHelper.buildDeviceCollection(unsupportedGroup);
        Map<SimpleDevice, VerifyResult> resultsMap = result.getVerifyResultsMap();
        Set<SimpleDevice> devices = resultsMap.keySet();
        String exceptionReason = result.getExceptionReason();
        model.addAttribute("exceptionReason", exceptionReason);
        model.addAttribute("devices", devices);
        model.addAttribute("resultsMap", resultsMap);
        model.addAttribute("successCollection", successCollection);
        model.addAttribute("failureCollection", failureCollection);
        model.addAttribute("unsupportedCollection", unsupportedCollection);
        
        return "config/verifyConfigResults.jsp";
    }

    @CheckRoleProperty(YukonRoleProperty.SEND_READ_CONFIG)
    @RequestMapping(value = "doReadConfig", method = RequestMethod.POST)
    public String doReadConfig(HttpServletRequest request, DeviceCollection deviceCollection, ModelMap model, 
                               YukonUserContext context) throws ServletException {
        SimpleCallback<CollectionActionResult> alertCallback =
            CollectionActionAlertHelper.createAlert(AlertType.GROUP_COMMAND_COMPLETION, alertService,
                messageResolver.getMessageSourceAccessor(context), request);
        int key = deviceConfigService.readConfigs(deviceCollection, alertCallback, context);
        return "redirect:/bulk/progressReport/detail?key=" + key;
    }

    @CheckRoleProperty(YukonRoleProperty.SEND_READ_CONFIG)
    @RequestMapping(value = "doSendConfig", method = RequestMethod.POST)
    public String doSendConfig(HttpServletRequest request, DeviceCollection deviceCollection, String method,
            ModelMap model, YukonUserContext context) throws ServletException {
        SimpleCallback<CollectionActionResult> alertCallback =
            CollectionActionAlertHelper.createAlert(AlertType.GROUP_COMMAND_COMPLETION, alertService,
                messageResolver.getMessageSourceAccessor(context), request);
        int key = deviceConfigService.sendConfigs(deviceCollection, method, alertCallback, context);
        return "redirect:/bulk/progressReport/detail?key=" + key;
    }
}