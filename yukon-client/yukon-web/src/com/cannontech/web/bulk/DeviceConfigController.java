package com.cannontech.web.bulk;

import java.util.List;

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
import com.cannontech.common.bulk.collection.device.model.CollectionActionResult;
import com.cannontech.common.bulk.collection.device.model.DeviceCollection;
import com.cannontech.common.bulk.collection.device.service.CollectionActionService;
import com.cannontech.common.device.config.dao.DeviceConfigurationDao;
import com.cannontech.common.device.config.model.LightDeviceConfiguration;
import com.cannontech.common.device.config.service.DeviceConfigService;
import com.cannontech.common.util.SimpleCallback;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.bulk.service.DeviceConfigAssignService;
import com.cannontech.web.security.annotation.CheckRoleProperty;

@Controller
@RequestMapping("/config/*")
@CheckRoleProperty(YukonRoleProperty.MASS_CHANGE)
public class DeviceConfigController {

    @Resource(name="oneAtATimeProcessor") private BulkProcessor bulkProcessor;

    @Autowired private DeviceConfigurationDao deviceConfigurationDao;
    @Autowired private DeviceConfigService deviceConfigService;
    @Autowired private AlertService alertService;
    @Autowired private DeviceCollectionFactory deviceCollectionFactory;
    @Autowired protected CollectionActionService collectionActionService;
    @Autowired private YukonUserContextMessageSourceResolver messageResolver;
    @Autowired private DeviceConfigAssignService deviceConfigAssignService;
    
    @RequestMapping(value = "deviceConfigs", method = RequestMethod.GET)
    public String deviceConfigs(DeviceCollection deviceCollection, ModelMap model, String action) throws ServletException {
        setupModel(deviceCollection, model, action);
        model.addAttribute("action", "DEVICE_CONFIGS");
        model.addAttribute("actionInputs", "/WEB-INF/pages/bulk/config/deviceConfigs.jsp");
        return "../collectionActions/collectionActionsHome.jsp";
    }
    
    @RequestMapping(value = "deviceConfigsInputs", method = RequestMethod.GET)
    public String deviceConfigsInputs(DeviceCollection deviceCollection, ModelMap model, String action) throws ServletException {
        setupModel(deviceCollection, model, action);
        return "config/deviceConfigs.jsp";
    }
    
    private void setupModel(DeviceCollection deviceCollection, ModelMap model, String action) {
        model.addAttribute("deviceCollection", deviceCollection);
        
        //used for assigning a config
        List<LightDeviceConfiguration> existingConfigs = deviceConfigurationDao.getAllLightDeviceConfigurations();
        model.addAttribute("existingConfigs", existingConfigs);
        
        //used for uploading a config
        model.addAttribute("someRF", deviceCollection.getDeviceList().stream().anyMatch(device -> device.getDeviceType().isRfn()));
        model.addAttribute("somePlc", deviceCollection.getDeviceList().stream().anyMatch(device -> device.getDeviceType().isPlc()));

        model.addAttribute("configAction", action);
    }
    
    @RequestMapping(value = "deviceConfigs", method = RequestMethod.POST)
    public String postDeviceConfigs(ModelMap model, HttpServletRequest request, YukonUserContext userContext, String action, 
                                    int configuration, String method) throws ServletException {
        DeviceCollection deviceCollection = deviceCollectionFactory.createDeviceCollection(request);
        if (action.equals("ASSIGN")) {
            return doAssignConfig(model, request, userContext, configuration);
        } else if (action.equals("UNASSIGN")) {
            return doUnassignConfig(model, request, userContext);
        } else if (action.equals("SEND")){
            return doSendConfig(request, deviceCollection, method, model, userContext);
        } else if (action.equals("READ")) {
            return doReadConfig(request, deviceCollection, model, userContext);
        }
        return null;
    }
    
    @CheckRoleProperty(YukonRoleProperty.ASSIGN_CONFIG)
    @RequestMapping(value = "doAssignConfig", method = RequestMethod.POST)
    public String doAssignConfig(ModelMap model, HttpServletRequest request, YukonUserContext userContext, int configuration)
            throws ServletException {
        DeviceCollection deviceCollection = deviceCollectionFactory.createDeviceCollection(request);
        CollectionActionResult result =  deviceConfigAssignService.assign(configuration, deviceCollection, userContext);
        return "redirect:/collectionActions/progressReport/detail?key=" + result.getCacheKey();
    }

    @CheckRoleProperty(YukonRoleProperty.ASSIGN_CONFIG)
    @RequestMapping(value = "doUnassignConfig", method = RequestMethod.POST)
    public String doUnassignConfig(ModelMap model, HttpServletRequest request, YukonUserContext userContext)
            throws ServletException {
        DeviceCollection deviceCollection = deviceCollectionFactory.createDeviceCollection(request);
        CollectionActionResult result =  deviceConfigAssignService.unassign(deviceCollection, userContext);
        return "redirect:/collectionActions/progressReport/detail?key=" + result.getCacheKey();
    }
    
    @CheckRoleProperty(YukonRoleProperty.SEND_READ_CONFIG)
    @RequestMapping(value = "doReadConfig", method = RequestMethod.POST)
    public String doReadConfig(HttpServletRequest request, DeviceCollection deviceCollection, ModelMap model, 
                               YukonUserContext context) throws ServletException {
        SimpleCallback<CollectionActionResult> alertCallback =
            CollectionActionAlertHelper.createAlert(AlertType.GROUP_COMMAND_COMPLETION, alertService,
                messageResolver.getMessageSourceAccessor(context), request);
        int key = deviceConfigService.readConfigs(deviceCollection, alertCallback, context);
        return "redirect:/collectionActions/progressReport/detail?key=" + key;
    }

    @CheckRoleProperty(YukonRoleProperty.SEND_READ_CONFIG)
    @RequestMapping(value = "doSendConfig", method = RequestMethod.POST)
    public String doSendConfig(HttpServletRequest request, DeviceCollection deviceCollection, String method,
            ModelMap model, YukonUserContext context) throws ServletException {
        SimpleCallback<CollectionActionResult> alertCallback =
            CollectionActionAlertHelper.createAlert(AlertType.GROUP_COMMAND_COMPLETION, alertService,
                messageResolver.getMessageSourceAccessor(context), request);
        int key = deviceConfigService.sendConfigs(deviceCollection, method, alertCallback, context);
        return "redirect:/collectionActions/progressReport/detail?key=" + key;
    }
}