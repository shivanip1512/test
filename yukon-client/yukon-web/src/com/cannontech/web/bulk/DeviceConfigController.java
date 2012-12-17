package com.cannontech.web.bulk;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.cannontech.common.alert.service.AlertService;
import com.cannontech.common.bulk.BulkProcessor;
import com.cannontech.common.bulk.callbackResult.BackgroundProcessResultHolder;
import com.cannontech.common.bulk.callbackResult.ConfigurationCallbackResult;
import com.cannontech.common.bulk.collection.device.DeviceCollection;
import com.cannontech.common.bulk.collection.device.DeviceGroupCollectionHelper;
import com.cannontech.common.bulk.mapper.PassThroughMapper;
import com.cannontech.common.bulk.processor.Processor;
import com.cannontech.common.bulk.processor.ProcessorFactory;
import com.cannontech.common.device.commands.GroupCommandResult;
import com.cannontech.common.device.commands.VerifyConfigCommandResult;
import com.cannontech.common.device.config.dao.DeviceConfigurationDao;
import com.cannontech.common.device.config.model.ConfigurationBase;
import com.cannontech.common.device.config.model.VerifyResult;
import com.cannontech.common.device.config.service.DeviceConfigService;
import com.cannontech.common.device.groups.editor.dao.DeviceGroupMemberEditorDao;
import com.cannontech.common.device.groups.editor.model.StoredDeviceGroup;
import com.cannontech.common.device.groups.service.TemporaryDeviceGroupService;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.util.ObjectMapper;
import com.cannontech.common.util.RecentResultsCache;
import com.cannontech.common.util.SimpleCallback;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.group.GroupCommandCompletionAlert;
import com.cannontech.web.security.annotation.CheckRoleProperty;

@RequestMapping("/config/*")
@CheckRoleProperty(YukonRoleProperty.MASS_CHANGE)
public class DeviceConfigController extends BulkControllerBase {

    private BulkProcessor bulkProcessor;
    private RecentResultsCache<BackgroundProcessResultHolder> recentResultsCache;

    @Autowired private ProcessorFactory processorFactory;
    @Autowired private DeviceConfigurationDao deviceConfigurationDao;
    @Autowired private TemporaryDeviceGroupService temporaryDeviceGroupService;
    @Autowired private DeviceGroupMemberEditorDao deviceGroupMemberEditorDao;
    @Autowired private DeviceGroupCollectionHelper deviceGroupCollectionHelper;
    @Autowired private DeviceConfigService deviceConfigService;
    @Autowired private AlertService alertService;
    @Autowired private RolePropertyDao rolePropertyDao;
    
    @RequestMapping
    public String assignConfig(DeviceCollection deviceCollection, ModelMap model, YukonUserContext userContext) throws ServletException {
        rolePropertyDao.verifyProperty(YukonRoleProperty.ASSIGN_CONFIG, userContext.getYukonUser());
        // pass along deviceCollection
        model.addAttribute("deviceCollection", deviceCollection);
        
        long deviceCount = deviceCollection.getDeviceCount();
        model.addAttribute("deviceCount", deviceCount);
        
        List<ConfigurationBase> existingConfigs = deviceConfigurationDao.getAllConfigurations();
        model.addAttribute("existingConfigs", existingConfigs);
        
        return "config/assignConfig.jsp";
    }
    
    @RequestMapping(method=RequestMethod.POST)
    public ModelAndView doAssignConfig(HttpServletRequest request, HttpServletResponse response, YukonUserContext userContext) throws ServletException {
        rolePropertyDao.verifyProperty(YukonRoleProperty.ASSIGN_CONFIG, userContext.getYukonUser());
        ModelAndView mav = null;
        DeviceCollection deviceCollection = deviceCollectionFactory.createDeviceCollection(request);
        // CALLBACK
        String resultsId = StringUtils.replace(UUID.randomUUID().toString(), "-", "");
        StoredDeviceGroup successGroup = temporaryDeviceGroupService.createTempGroup(null);
        StoredDeviceGroup processingExceptionGroup = temporaryDeviceGroupService.createTempGroup(null);
        
        ConfigurationCallbackResult callbackResult = 
                new ConfigurationCallbackResult(resultsId,
                                                deviceCollection, 
                                                successGroup, 
                                                processingExceptionGroup, 
                                                deviceGroupMemberEditorDao,
                                                deviceGroupCollectionHelper);
        
        // CACHE
        recentResultsCache.addResult(resultsId, callbackResult);
        
        // PROCESS
        final int configId = ServletRequestUtils.getRequiredIntParameter(request, "configuration"); 
        ConfigurationBase configuration = deviceConfigurationDao.getConfiguration(configId);

        Processor<SimpleDevice> processor = processorFactory.createAssignConfigurationToYukonDeviceProcessor(configuration);
        
        ObjectMapper<SimpleDevice, SimpleDevice> mapper = new PassThroughMapper<>();
        bulkProcessor.backgroundBulkProcess(deviceCollection.iterator(), mapper, processor, callbackResult);
        
        mav = new ModelAndView("redirect:assignConfigResults");
        mav.addObject("resultsId", resultsId);
        
        return mav;
    }
    
    @RequestMapping
    public String assignConfigResults(HttpServletRequest request, ModelMap model, YukonUserContext userContext) throws ServletException {
        doConfigResultsCore(request, model, userContext);
        return "config/assignConfigResults.jsp";
    }
    
    @RequestMapping
    public String unassignConfig(DeviceCollection deviceCollection, ModelMap model, YukonUserContext userContext) throws ServletException {
        rolePropertyDao.verifyProperty(YukonRoleProperty.ASSIGN_CONFIG, userContext.getYukonUser());
        
        // pass along deviceCollection
        model.addAttribute("deviceCollection", deviceCollection);
        
        long deviceCount = deviceCollection.getDeviceCount();
        model.addAttribute("deviceCount", deviceCount);
        
        return "config/unassignConfig.jsp";
    }
    
    @RequestMapping(method=RequestMethod.POST)
    public ModelAndView doUnassignConfig(HttpServletRequest request, HttpServletResponse response, YukonUserContext userContext) throws ServletException {
        rolePropertyDao.verifyProperty(YukonRoleProperty.ASSIGN_CONFIG, userContext.getYukonUser());
        
        DeviceCollection deviceCollection = deviceCollectionFactory.createDeviceCollection(request);
        
        /*
         * Generate a unique ID string for the results of this operation. Strip the dashes of a 
         * random UUID and use it.
         *  i.e. bee09385-2da2-4622-863c-1e30023d9738 becomes bee093852da24622863c1e30023d9738
         */
        String resultsId = StringUtils.replace(UUID.randomUUID().toString(), "-", "");
        
        // CALLBACK
        StoredDeviceGroup successGroup = temporaryDeviceGroupService.createTempGroup(null);
        StoredDeviceGroup processingExceptionGroup = temporaryDeviceGroupService.createTempGroup(null);
        
        ConfigurationCallbackResult callbackResult = 
                new ConfigurationCallbackResult(resultsId,
                                                deviceCollection, 
                                                successGroup, 
                                                processingExceptionGroup, 
                                                deviceGroupMemberEditorDao,
                                                deviceGroupCollectionHelper);
        
        // CACHE
        recentResultsCache.addResult(resultsId, callbackResult);
        
        // PROCESS
        Processor<SimpleDevice> processor = processorFactory.createUnassignConfigurationToYukonDeviceProcessor();
        
        ObjectMapper<SimpleDevice, SimpleDevice> mapper = new PassThroughMapper<>();
        bulkProcessor.backgroundBulkProcess(deviceCollection.iterator(), mapper, processor, callbackResult);
        
        ModelAndView mav = new ModelAndView("redirect:unassignConfigResults");
        mav.addObject("resultsId", resultsId);
        
        return mav;
    }
    
    @RequestMapping
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
        String resultsId = ServletRequestUtils.getRequiredStringParameter(request, "resultsId");
        ConfigurationCallbackResult callbackResult = (ConfigurationCallbackResult)recentResultsCache.getResult(resultsId);
        model.addAttribute("deviceCollection", callbackResult.getDeviceCollection());
        model.addAttribute("callbackResult", callbackResult);
    }

    /**
     * CONFIRM CONFIG SEND
     * @param deviceCollection
     * @param model
     * @return
     * @throws ServletException
     */
    @RequestMapping
    public String sendConfig(DeviceCollection deviceCollection, ModelMap model, YukonUserContext userContext) throws ServletException {
        rolePropertyDao.verifyProperty(YukonRoleProperty.SEND_READ_CONFIG, userContext.getYukonUser());
        model.addAttribute("deviceCollection", deviceCollection);
        long deviceCount = deviceCollection.getDeviceCount();
        model.addAttribute("deviceCount", deviceCount);
        
        return "config/sendConfig.jsp";
    }
    
    /**
     * CONFIRM CONFIG VERIFY
     * @param deviceCollection
     * @param model
     * @return
     * @throws ServletException
     */
    @RequestMapping
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
    @RequestMapping
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
    @RequestMapping
    public String doVerifyConfigs(DeviceCollection deviceCollection, LiteYukonUser user, ModelMap model) {
        model.addAttribute("deviceCollection", deviceCollection);
        
        // DO VERIFY
        VerifyConfigCommandResult result = deviceConfigService.verifyConfigs(deviceCollection, user);
        StoredDeviceGroup successGroup = temporaryDeviceGroupService.createTempGroup(null);
        StoredDeviceGroup failureGroup = temporaryDeviceGroupService.createTempGroup(null);
        StoredDeviceGroup unsupportedGroup = temporaryDeviceGroupService.createTempGroup(null);
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
    @RequestMapping(method=RequestMethod.POST)
    public String doReadConfig(DeviceCollection deviceCollection, LiteYukonUser user, ModelMap model) throws ServletException {
        rolePropertyDao.verifyProperty(YukonRoleProperty.SEND_READ_CONFIG, user);
        // DO SEND
        SimpleCallback<GroupCommandResult> callback = new SimpleCallback<GroupCommandResult>() {
            @Override
            public void handle(GroupCommandResult result) {
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
    @RequestMapping(method=RequestMethod.POST)
    public String doSendConfig(DeviceCollection deviceCollection, String method, LiteYukonUser user, ModelMap model) throws ServletException {
        rolePropertyDao.verifyProperty(YukonRoleProperty.SEND_READ_CONFIG, user);
        // DO SEND
        SimpleCallback<GroupCommandResult> callback = new SimpleCallback<GroupCommandResult>() {
            @Override
            public void handle(GroupCommandResult result) {
                GroupCommandCompletionAlert commandCompletionAlert = new GroupCommandCompletionAlert(new Date(), result);
                alertService.add(commandCompletionAlert);
            }
            
        };
        
        String key = deviceConfigService.sendConfigs(deviceCollection, method, callback, user);
        model.addAttribute("resultKey", key);
        return "redirect:/group/commander/resultDetail";
    }
    
    @Required
    public void setBulkProcessor(BulkProcessor bulkProcessor) {
        this.bulkProcessor = bulkProcessor;
    }
    
    @Resource(name="recentResultsCache")
    public void setRecentResultsCache(RecentResultsCache<BackgroundProcessResultHolder> recentResultsCache) {
        this.recentResultsCache = recentResultsCache;
    }
}