package com.cannontech.web.bulk;

import java.util.Date;
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
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.cannontech.common.alert.service.AlertService;
import com.cannontech.common.bulk.BulkProcessor;
import com.cannontech.common.bulk.collection.device.DeviceCollectionFactory;
import com.cannontech.common.bulk.collection.device.DeviceGroupCollectionHelper;
import com.cannontech.common.bulk.collection.device.model.CollectionAction;
import com.cannontech.common.bulk.collection.device.model.CollectionActionBulkProcessorCallback;
import com.cannontech.common.bulk.collection.device.model.CollectionActionResult;
import com.cannontech.common.bulk.collection.device.model.DeviceCollection;
import com.cannontech.common.bulk.collection.device.service.CollectionActionService;
import com.cannontech.common.bulk.mapper.PassThroughMapper;
import com.cannontech.common.bulk.processor.Processor;
import com.cannontech.common.bulk.processor.ProcessorFactory;
import com.cannontech.common.device.commands.GroupCommandResult;
import com.cannontech.common.device.commands.VerifyConfigCommandResult;
import com.cannontech.common.device.config.dao.DeviceConfigurationDao;
import com.cannontech.common.device.config.model.DeviceConfiguration;
import com.cannontech.common.device.config.model.LightDeviceConfiguration;
import com.cannontech.common.device.config.model.VerifyResult;
import com.cannontech.common.device.config.service.DeviceConfigService;
import com.cannontech.common.device.config.service.DeviceConfigService.LogAction;
import com.cannontech.common.device.groups.editor.dao.DeviceGroupMemberEditorDao;
import com.cannontech.common.device.groups.editor.model.StoredDeviceGroup;
import com.cannontech.common.device.groups.service.TemporaryDeviceGroupService;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.events.loggers.DeviceConfigEventLogService;
import com.cannontech.common.util.ObjectMapper;
import com.cannontech.common.util.SimpleCallback;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.group.GroupCommandCompletionAlert;
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
    @Autowired private RolePropertyDao rolePropertyDao;
    @Autowired private DeviceCollectionFactory deviceCollectionFactory;
    @Autowired protected CollectionActionService collectionActionService;

    @RequestMapping(value = "assignConfig", method = RequestMethod.GET)
    public String assignConfig(DeviceCollection deviceCollection, ModelMap model, YukonUserContext userContext) throws ServletException {
        rolePropertyDao.verifyProperty(YukonRoleProperty.ASSIGN_CONFIG, userContext.getYukonUser());
        // pass along deviceCollection
        model.addAttribute("deviceCollection", deviceCollection);
        
        long deviceCount = deviceCollection.getDeviceCount();
        model.addAttribute("deviceCount", deviceCount);
        
        List<LightDeviceConfiguration> existingConfigs = deviceConfigurationDao.getAllLightDeviceConfigurations();
        model.addAttribute("existingConfigs", existingConfigs);
        
        return "config/assignConfig.jsp";
    }
    
    @RequestMapping(value = "doAssignConfig", method = RequestMethod.POST)
    public String doAssignConfig(ModelMap model, HttpServletRequest request, YukonUserContext userContext) throws ServletException {
        rolePropertyDao.verifyProperty(YukonRoleProperty.ASSIGN_CONFIG, userContext.getYukonUser());
        DeviceCollection deviceCollection = deviceCollectionFactory.createDeviceCollection(request);

        final int configId = ServletRequestUtils.getRequiredIntParameter(request, "configuration"); 
        DeviceConfiguration configuration = deviceConfigurationDao.getDeviceConfiguration(configId);
        eventLogService.assignConfigInitiated(configuration.getName(), deviceCollection.getDeviceCount(), userContext.getYukonUser());
        Processor<SimpleDevice> processor = processorFactory.createAssignConfigurationToYukonDeviceProcessor(configuration, userContext.getYukonUser());

        LinkedHashMap<String, String> userInputs = new LinkedHashMap<>();
        userInputs.put("Configuration", configuration.getName());
        CollectionActionResult result = collectionActionService.createResult(CollectionAction.ASSIGN_CONFIG, userInputs,
            deviceCollection, userContext);
        ObjectMapper<SimpleDevice, SimpleDevice> mapper = new PassThroughMapper<>();
        bulkProcessor.backgroundBulkProcess(deviceCollection.iterator(), mapper, processor,
            new CollectionActionBulkProcessorCallback(result, collectionActionService));
   
        return "redirect:/bulk/progressReport/detail?key=" + result.getCacheKey();
    }
    
    @RequestMapping("assignConfigResults")
    public String assignConfigResults(HttpServletRequest request, ModelMap model, YukonUserContext userContext) throws ServletException {
        doConfigResultsCore(request, model, userContext);
        return "config/assignConfigResults.jsp";
    }
    
    @RequestMapping("unassignConfig")
    public String unassignConfig(DeviceCollection deviceCollection, ModelMap model, YukonUserContext userContext) throws ServletException {
        rolePropertyDao.verifyProperty(YukonRoleProperty.ASSIGN_CONFIG, userContext.getYukonUser());
        
        // pass along deviceCollection
        model.addAttribute("deviceCollection", deviceCollection);
        
        long deviceCount = deviceCollection.getDeviceCount();
        model.addAttribute("deviceCount", deviceCount);
        
        return "config/unassignConfig.jsp";
    }
    
    @RequestMapping(value="doUnassignConfig", method=RequestMethod.POST)
    public String doUnassignConfig(ModelMap model, HttpServletRequest request, YukonUserContext userContext) throws ServletException {
        rolePropertyDao.verifyProperty(YukonRoleProperty.ASSIGN_CONFIG, userContext.getYukonUser());
        
        DeviceCollection deviceCollection = deviceCollectionFactory.createDeviceCollection(request);

        
        eventLogService.unassignConfigInitiated(deviceCollection.getDeviceCount(), userContext.getYukonUser());
        Processor<SimpleDevice> processor = processorFactory.createUnassignConfigurationToYukonDeviceProcessor(userContext.getYukonUser());
        CollectionActionResult result = collectionActionService.createResult(CollectionAction.UNASSIGN_CONFIG, null,
            deviceCollection, userContext);
        ObjectMapper<SimpleDevice, SimpleDevice> mapper = new PassThroughMapper<>();
        bulkProcessor.backgroundBulkProcess(deviceCollection.iterator(), mapper, processor, new CollectionActionBulkProcessorCallback(result, collectionActionService));
        
        return "redirect:/bulk/progressReport/detail?key=" + result.getCacheKey();
    }
    
    @RequestMapping("unassignConfigResults")
    public String unassignConfigResults(HttpServletRequest request, ModelMap model, YukonUserContext userContext) throws ServletException {
        doConfigResultsCore(request, model, userContext);
        return "config/unassignConfigResults.jsp";
    }
    
    /**
     * Helper method for the assignConfigResults and unassignConfigResults methods that 
     * handles common statements.
     */
    private void doConfigResultsCore(HttpServletRequest request, ModelMap model, YukonUserContext userContext) throws ServletException {
        rolePropertyDao.verifyProperty(YukonRoleProperty.ASSIGN_CONFIG, userContext.getYukonUser());
      /*  String resultsId = ServletRequestUtils.getRequiredStringParameter(request, "resultsId");
        ConfigurationCallbackResult callbackResult = (ConfigurationCallbackResult)recentResultsCache.getResult(resultsId);
        model.addAttribute("deviceCollection", callbackResult.getDeviceCollection());
        model.addAttribute("callbackResult", callbackResult);
        model.addAttribute("fileName", callbackResult.getDeviceCollection().getUploadFileName());*/
    }

    /**
     * CONFIRM CONFIG SEND
     * @param deviceCollection
     * @param model
     * @return
     * @throws ServletException
     */
    @RequestMapping("sendConfig")
    public String sendConfig(DeviceCollection deviceCollection, ModelMap model, YukonUserContext userContext) throws ServletException {
        rolePropertyDao.verifyProperty(YukonRoleProperty.SEND_READ_CONFIG, userContext.getYukonUser());
        model.addAttribute("deviceCollection", deviceCollection);
        long deviceCount = deviceCollection.getDeviceCount();
        model.addAttribute("deviceCount", deviceCount);

        for (SimpleDevice sd : deviceCollection.getDeviceList()) {
            if (sd.getDeviceType().isRfn()) {
                model.addAttribute("someRF", true);
                break;
            }
        }

        return "config/sendConfig.jsp";
    }
    
    /**
     * CONFIRM CONFIG VERIFY
     * @param deviceCollection
     * @param model
     * @return
     * @throws ServletException
     */
    @RequestMapping("verifyConfig")
    public String verifyConfig(DeviceCollection deviceCollection, ModelMap model) throws ServletException {
        model.addAttribute("deviceCollection", deviceCollection);
        long deviceCount = deviceCollection.getDeviceCount();
        model.addAttribute("deviceCount", deviceCount);
        
        return "config/verifyConfig.jsp";
    }
    
    /**
     * CONFIRM CONFIG READ
     * @param deviceCollection
     * @param model
     * @return
     * @throws ServletException
     */
    @RequestMapping("readConfig")
    public String readConfig(DeviceCollection deviceCollection, ModelMap model, YukonUserContext userContext) throws ServletException {
        rolePropertyDao.verifyProperty(YukonRoleProperty.SEND_READ_CONFIG, userContext.getYukonUser());
        model.addAttribute("deviceCollection", deviceCollection);
        long deviceCount = deviceCollection.getDeviceCount();
        model.addAttribute("deviceCount", deviceCount);
        
        return "config/readConfig.jsp";
    }
    
    /**
     * DO VERIFY CONFIG
     * @param deviceCollection
     * @param user
     * @param model
     * @return
     */
    @RequestMapping("doVerifyConfigs")
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
    
    /**
     * DO READ CONFIG
     * @param deviceCollection
     * @param method
     * @param user
     * @param model
     * @return
     * @throws ServletException
     */
    @RequestMapping(value="doReadConfig", method=RequestMethod.POST)
    public String doReadConfig(DeviceCollection deviceCollection, LiteYukonUser user, ModelMap model) throws ServletException {
        rolePropertyDao.verifyProperty(YukonRoleProperty.SEND_READ_CONFIG, user);
        // DO SEND
        SimpleCallback<GroupCommandResult> callback = new SimpleCallback<GroupCommandResult>() {
            @Override
            public void handle(GroupCommandResult result) {
                
                //It would be nice to log these as they individually complete, but that would require rearchitecture of the callback.
                deviceConfigService.logCompleted(result.getSuccessCollection().getDeviceList(), LogAction.READ, true);
                deviceConfigService.logCompleted(result.getFailureCollection().getDeviceList(), LogAction.READ, false);
                String reason =  result.getExceptionReason() == null? "": result.getExceptionReason();
                eventLogService.readConfigCompleted(result.getSuccessCollection().getDeviceCount(),
                                                            result.getFailureCollection().getDeviceCount(),
                                                            result.getUnsupportedCollection().getDeviceCount(),
                                                            reason, 
                                                            result.getKey());
                GroupCommandCompletionAlert commandCompletionAlert = new GroupCommandCompletionAlert(new Date(), result);
                alertService.add(commandCompletionAlert);
            }
            
        };
                
        String key = deviceConfigService.readConfigs(deviceCollection, callback, user);
        model.addAttribute("resultKey", key);
        return "redirect:/group/commander/resultDetail";
    }
    
    /**
     * DO SEND CONFIG
     * @param deviceCollection
     * @param method
     * @param user
     * @param model
     * @return
     * @throws ServletException
     */
    @RequestMapping(value="doSendConfig", method=RequestMethod.POST)
    public String doSendConfig(DeviceCollection deviceCollection, String method, LiteYukonUser user, ModelMap model) throws ServletException {
        rolePropertyDao.verifyProperty(YukonRoleProperty.SEND_READ_CONFIG, user);
        // DO SEND
        SimpleCallback<GroupCommandResult> callback = new SimpleCallback<GroupCommandResult>() {
            @Override
            public void handle(GroupCommandResult result) {
                // It would be nice to log these as they individually complete, but that would require rearchitecture of the callback.
                deviceConfigService.logCompleted(result.getSuccessCollection().getDeviceList(), LogAction.SEND, true);
                deviceConfigService.logCompleted(result.getFailureCollection().getDeviceList(), LogAction.SEND, false);
                String reason =  result.getExceptionReason() == null? "": result.getExceptionReason();
                eventLogService.sendConfigCompleted(result.getSuccessCollection().getDeviceCount(),
                                                            result.getFailureCollection().getDeviceCount(),
                                                            result.getUnsupportedCollection().getDeviceCount(),
                                                            reason,
                                                            result.getKey());
                GroupCommandCompletionAlert commandCompletionAlert = new GroupCommandCompletionAlert(new Date(), result);
                alertService.add(commandCompletionAlert);
            }
            
        };
        String key = deviceConfigService.sendConfigs(deviceCollection, method, callback, user);
        model.addAttribute("resultKey", key);
        return "redirect:/group/commander/resultDetail";
    }
    
}