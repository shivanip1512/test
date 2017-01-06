package com.cannontech.web.widget;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.cannontech.common.device.commands.CommandResultHolder;
import com.cannontech.common.device.config.dao.DeviceConfigurationDao;
import com.cannontech.common.device.config.dao.InvalidDeviceTypeException;
import com.cannontech.common.device.config.model.DeviceConfiguration;
import com.cannontech.common.device.config.model.LightDeviceConfiguration;
import com.cannontech.common.device.config.model.VerifyResult;
import com.cannontech.common.device.config.service.DeviceConfigService;
import com.cannontech.common.device.config.service.DeviceConfigurationService;
import com.cannontech.common.events.loggers.DeviceConfigEventLogService;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.pao.YukonDevice;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.authorization.service.RoleAndPropertyDescriptionService;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.mbean.ServerDatabaseCache;
import com.cannontech.servlet.YukonUserContextUtils;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.rfn.dataStreaming.model.DataStreamingConfig;
import com.cannontech.web.rfn.dataStreaming.model.DiscrepancyResult;
import com.cannontech.web.rfn.dataStreaming.service.DataStreamingService;
import com.cannontech.web.widget.support.SimpleWidgetInput;
import com.cannontech.web.widget.support.WidgetControllerBase;
import com.cannontech.web.widget.support.WidgetParameterHelper;

@Controller
@RequestMapping("/configWidget/*")
public class ConfigWidget extends WidgetControllerBase {

    @Autowired private DeviceDao deviceDao;
    @Autowired private DeviceConfigurationDao deviceConfigurationDao;
    @Autowired private DeviceConfigService deviceConfigService;
    @Autowired private DeviceConfigurationService deviceConfigurationService;
    @Autowired private DeviceConfigEventLogService eventLogService;
    @Autowired private DataStreamingService dataStreamingService;
    @Autowired private ServerDatabaseCache serverDatabaseCache;
    @Autowired protected YukonUserContextMessageSourceResolver messageSourceResolver;
    
    @Autowired
    public ConfigWidget(@Qualifier("widgetInput.deviceId") SimpleWidgetInput simpleWidgetInput,
            RoleAndPropertyDescriptionService roleAndPropertyDescriptionService) {
        
        String checkRole = YukonRole.METERING.name();
        addInput(simpleWidgetInput);
        setRoleAndPropertiesChecker(roleAndPropertyDescriptionService.compile(checkRole));
    }
    
    @Override
    @RequestMapping("render")
    public ModelAndView render(HttpServletRequest request, HttpServletResponse response) throws ServletRequestBindingException {
        ModelAndView mav = getConfigModelAndView(request);
        
        return mav;
    }

    private ModelAndView getConfigModelAndView(HttpServletRequest request) throws ServletRequestBindingException {
        ModelAndView mav = new ModelAndView("configWidget/render.jsp");
        YukonDevice device = getYukonDevice(request);
        int deviceId = device.getPaoIdentifier().getPaoId();
        mav.addObject("deviceId", deviceId);
        
        List<LightDeviceConfiguration> existingConfigs = 
            deviceConfigurationDao.getAllConfigurationsByType(device.getPaoIdentifier().getPaoType());
        
        mav.addObject("existingConfigs", existingConfigs);
        
        LightDeviceConfiguration config = deviceConfigurationDao.findConfigurationForDevice(device);
        
        mav.addObject("currentConfigId", config != null ? config.getConfigurationId() : null);
        mav.addObject("currentConfigName", config != null ? config.getName() : CtiUtilities.STRING_NONE);
        
        //check for data streaming config
        YukonUserContext userContext = YukonUserContextUtils.getYukonUserContext(request);
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        DataStreamingConfig dsConfig = dataStreamingService.findDataStreamingConfigurationForDevice(deviceId);
        if (dsConfig != null) {
            dsConfig.setAccessor(accessor);
        }
        mav.addObject("dataStreamingConfig", dsConfig);
        
        DiscrepancyResult discrepancy = dataStreamingService.findDiscrepancy(deviceId);
        mav.addObject("dataStreamingDiscrepancy", discrepancy);

        return mav;
    }

    private YukonDevice getYukonDevice(HttpServletRequest request) throws ServletRequestBindingException {
        int deviceId = WidgetParameterHelper.getRequiredIntParameter(request, "deviceId");
        return deviceDao.getYukonDevice(deviceId);
    }
    
    private LiteYukonPAObject getLiteYukonPAObject(HttpServletRequest request) throws ServletRequestBindingException {
        int deviceId = WidgetParameterHelper.getRequiredIntParameter(request, "deviceId");
        return serverDatabaseCache.getAllPaosMap().get(deviceId);
    }
    
    @RequestMapping("assignConfig")
    public ModelAndView assignConfig(HttpServletRequest request, HttpServletResponse response) throws ServletRequestBindingException, InvalidDeviceTypeException {
        YukonUserContext userContext = YukonUserContextUtils.getYukonUserContext(request);
        YukonDevice device = getYukonDevice(request);
        String deviceName = getLiteYukonPAObject(request).getPaoName();
        
        final int configId = ServletRequestUtils.getRequiredIntParameter(request, "configuration");
        if (configId > -1) {
            DeviceConfiguration configuration = deviceConfigurationDao.getDeviceConfiguration(configId);
            eventLogService.assignConfigToDeviceInitiated(configuration.getName(), deviceName, userContext.getYukonUser());
            deviceConfigurationService.assignConfigToDevice(configuration, device);
        } else {
            eventLogService.unassignConfigFromDeviceInitiated(deviceName, userContext.getYukonUser());
            deviceConfigurationService.unassignConfig(device);
        }
        
        ModelAndView mav = getConfigModelAndView(request);
        mav.addObject("configurableDevice", true);
        return mav;
    }
    
    @RequestMapping("unassignConfig")
    public ModelAndView unassignConfig(HttpServletRequest request, HttpServletResponse response) throws Exception {
        YukonUserContext userContext = YukonUserContextUtils.getYukonUserContext(request);
        YukonDevice device = getYukonDevice(request);
        String deviceName = getLiteYukonPAObject(request).getPaoName();
        eventLogService.unassignConfigFromDeviceInitiated(deviceName, userContext.getYukonUser());
        deviceConfigurationService.unassignConfig(device);
        
        ModelAndView mav = getConfigModelAndView(request);
        return mav;
    }
    
    @RequestMapping("sendConfig")
    public ModelAndView sendConfig(HttpServletRequest request, HttpServletResponse response) throws Exception {
        YukonUserContext userContext = YukonUserContextUtils.getYukonUserContext(request);
        ModelAndView mav = new ModelAndView("configWidget/configWidgetResult.jsp");
        YukonDevice device = getYukonDevice(request);
        String deviceName = getLiteYukonPAObject(request).getPaoName();
        eventLogService.sendConfigToDeviceinitiated(deviceName, userContext.getYukonUser());
        CommandResultHolder resultHolder = deviceConfigService.sendConfig(device, userContext.getYukonUser());
        
        mav.addObject("sendResult", resultHolder);
        return mav;
    }
    
    @RequestMapping("readConfig")
    public ModelAndView readConfig(HttpServletRequest request, HttpServletResponse response) throws Exception {
        YukonUserContext userContext = YukonUserContextUtils.getYukonUserContext(request);
        ModelAndView mav = new ModelAndView("configWidget/configWidgetResult.jsp");
        YukonDevice device = getYukonDevice(request);
        String deviceName = getLiteYukonPAObject(request).getPaoName();
        eventLogService.readConfigFromDeviceInitiated(deviceName, userContext.getYukonUser());
        CommandResultHolder resultHolder = deviceConfigService.readConfig(device, userContext.getYukonUser());
        
        mav.addObject("readResult", resultHolder);
        return mav;
    }
    
    @RequestMapping("verifyConfig")
    public ModelAndView verifyConfig(HttpServletRequest request, HttpServletResponse response) throws Exception {
        YukonUserContext userContext = YukonUserContextUtils.getYukonUserContext(request);
        ModelAndView mav = new ModelAndView("configWidget/configWidgetResult.jsp");
        YukonDevice device = getYukonDevice(request);
        String deviceName = getLiteYukonPAObject(request).getPaoName();
        eventLogService.verifyConfigFromDeviceInitiated(deviceName, userContext.getYukonUser());
        VerifyResult verifyResult = deviceConfigService.verifyConfig(device, userContext.getYukonUser());
        
        mav.addObject("verifyResult", verifyResult);
        return mav;
    }
}

