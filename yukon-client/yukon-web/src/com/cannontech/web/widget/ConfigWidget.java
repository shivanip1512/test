package com.cannontech.web.widget;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.cannontech.amr.rfn.dataStreaming.model.DataStreamingConfig;
import com.cannontech.amr.rfn.dataStreaming.model.DiscrepancyResult;
import com.cannontech.amr.rfn.dataStreaming.service.DataStreamingService;
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.config.MasterConfigLicenseKey;
import com.cannontech.common.device.config.dao.DeviceConfigurationDao;
import com.cannontech.common.device.config.dao.DeviceConfigurationDao.ConfigState;
import com.cannontech.common.device.config.dao.InvalidDeviceTypeException;
import com.cannontech.common.device.config.model.DeviceConfigState;
import com.cannontech.common.device.config.model.DeviceConfiguration;
import com.cannontech.common.device.config.model.LightDeviceConfiguration;
import com.cannontech.common.device.config.model.VerifyResult;
import com.cannontech.common.device.config.service.DeviceConfigService;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.pao.YukonDevice;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDao;
import com.cannontech.common.pao.definition.model.PaoTag;
import com.cannontech.common.rfn.dataStreaming.DataStreamingAttributeHelper;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.authorization.service.RoleAndPropertyDescriptionService;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.servlet.YukonUserContextUtils;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.tools.device.programming.dao.MeterProgrammingSummaryDao;
import com.cannontech.web.tools.device.programming.model.MeterProgramWidgetDisplay;
import com.cannontech.web.widget.support.AdvancedWidgetControllerBase;
import com.cannontech.web.widget.support.SimpleWidgetInput;
import com.cannontech.web.widget.support.WidgetParameterHelper;
import com.cannontech.yukon.IDatabaseCache;

@Controller
@RequestMapping("/configWidget/*")
public class ConfigWidget extends AdvancedWidgetControllerBase {

    @Autowired private DeviceDao deviceDao;
    @Autowired private DeviceConfigurationDao deviceConfigurationDao;
    @Autowired private DeviceConfigService deviceConfigService;
    @Autowired private DataStreamingService dataStreamingService;
    @Autowired private ConfigurationSource configurationSource;
    @Autowired private DataStreamingAttributeHelper dataStreamingAttributeHelper;
    @Autowired private MeterProgrammingSummaryDao meterProgrammingSummaryDao;
    @Autowired private PaoDefinitionDao paoDefinitionDao;
    private ExecutorService executor = Executors.newCachedThreadPool();

    @Autowired protected YukonUserContextMessageSourceResolver messageSourceResolver;
    @Autowired private IDatabaseCache dbCache;
    
    private final static String baseKey = "yukon.web.widgets.configWidget.";

    @Autowired
    public ConfigWidget(@Qualifier("widgetInput.deviceId") SimpleWidgetInput simpleWidgetInput,
            RoleAndPropertyDescriptionService roleAndPropertyDescriptionService) {
        
        String checkRole = YukonRole.METERING.name();
        addInput(simpleWidgetInput);
        setRoleAndPropertiesChecker(roleAndPropertyDescriptionService.compile(checkRole));
    }
    
    @GetMapping("render")
    public String render(ModelMap model, int deviceId, YukonUserContext userContext) {
        getConfigModelAndView(model, deviceId, userContext);
        return "configWidget/render.jsp";
    }

    private void getConfigModelAndView(ModelMap model, int deviceId, YukonUserContext userContext) {
        YukonDevice device = deviceDao.getYukonDevice(deviceId);
        model.addAttribute("deviceId", deviceId);
        
        List<LightDeviceConfiguration> existingConfigs = 
            deviceConfigurationDao.getAllConfigurationsByType(device.getPaoIdentifier().getPaoType());
        
        model.addAttribute("existingConfigs", existingConfigs);
        
        LightDeviceConfiguration config = deviceConfigurationDao.findConfigurationForDevice(device);
        
        model.addAttribute("currentConfigId", config != null ? config.getConfigurationId() : null);
        model.addAttribute("currentConfigName", config != null ? config.getName() : CtiUtilities.STRING_NONE);
        
         
        //check for data streaming config
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        DataStreamingConfig dsConfig = dataStreamingService.findDataStreamingConfigurationForDevice(deviceId);
        if (dsConfig != null) {
            dsConfig.setAccessor(accessor);
        }
        model.addAttribute("dataStreamingConfig", dsConfig);
        
        DiscrepancyResult discrepancy = dataStreamingService.findDiscrepancy(deviceId);
        model.addAttribute("dataStreamingDiscrepancy", discrepancy);

        boolean configurableDevice = !existingConfigs.isEmpty();
        boolean dataStreamingEnabled =
            configurationSource.isLicenseEnabled(MasterConfigLicenseKey.RF_DATA_STREAMING_ENABLED);
        boolean streamableDevice =
            dataStreamingEnabled
                && !dataStreamingAttributeHelper.getSupportedAttributes(device.getPaoIdentifier().getPaoType()).isEmpty();
        model.addAttribute("configurableDevice", configurableDevice);
        model.addAttribute("streamableDevice", streamableDevice);
        
        //check for meter programming
        boolean enableMeterProgramming = configurationSource.isLicenseEnabled(MasterConfigLicenseKey.METER_PROGRAMMING_ENABLED);
        if (enableMeterProgramming) {
            boolean deviceSupported = paoDefinitionDao.isTagSupported(device.getPaoIdentifier().getPaoType(), PaoTag.METER_PROGRAMMING);
            if (deviceSupported) {
                model.addAttribute("showMeterProgramming", true);
                try {
                    MeterProgramWidgetDisplay program = meterProgrammingSummaryDao.getProgramConfigurationByDeviceId(deviceId, userContext);
                    model.addAttribute("meterProgram", program);
                } catch (NotFoundException e) {
                    //not programmed yet
                }
            }
        }
    }

    private SimpleDevice getDevice(HttpServletRequest request) throws ServletRequestBindingException {
        int deviceId = WidgetParameterHelper.getRequiredIntParameter(request, "deviceId");
        return deviceDao.getYukonDevice(deviceId);
    }
    
    @PostMapping("changeConfig")
    @ResponseBody
    public Map<String, Object> changeConfig(ModelMap model, int deviceId, int configuration, YukonUserContext userContext) throws InvalidDeviceTypeException {
        Map<String, Object> jsonResponse = new HashMap<>();
        SimpleDevice device = deviceDao.getYukonDevice(deviceId);
        if (configuration > -1) {
            DeviceConfiguration deviceConfig = deviceConfigurationDao.getDeviceConfiguration(configuration);
            DeviceConfigState configState = deviceConfigService.assignConfigToDevice(device, deviceConfig,
                    userContext.getYukonUser());
            
            if(configState.getState() == ConfigState.OUT_OF_SYNC) {
                MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(userContext);
                LiteYukonPAObject pao = dbCache.getAllPaosMap().get(device.getDeviceId());
                // display popup suggesting upload
                jsonResponse.put("displayUploadPopup", true);
                jsonResponse.put("popupMessage", accessor.getMessage(baseKey + "uploadPopup.message", deviceConfig.getName(), pao.getPaoName()));
            }
        } else {
            deviceConfigService.unassignConfig(device, userContext.getYukonUser());
        }

        return jsonResponse;

    }
    
    @PostMapping("removeConfig")
    public String removeConfig(ModelMap model, int deviceId, YukonUserContext userContext) throws InvalidDeviceTypeException {
        SimpleDevice device = deviceDao.getYukonDevice(deviceId);
        deviceConfigService.unassignConfig(device, userContext.getYukonUser());
        getConfigModelAndView(model, deviceId, userContext);
        return "configWidget/render.jsp";
    }
    
    @PostMapping("uploadConfig")
    public String uploadConfig(ModelMap model, int deviceId, YukonUserContext userContext) {
        SimpleDevice device = deviceDao.getYukonDevice(deviceId);
        executor.submit(() -> deviceConfigService.sendConfig(device, userContext.getYukonUser()));
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        model.addAttribute("userMessage", accessor.getMessage(baseKey + "uploadSent"));
        getConfigModelAndView(model, deviceId, userContext);
        return "configWidget/render.jsp";
    }

    @PostMapping("validateConfig")
    public String validateConfig(ModelMap model, int deviceId, YukonUserContext userContext) {
        SimpleDevice device = deviceDao.getYukonDevice(deviceId);
        executor.submit(() -> deviceConfigService.readConfig(device, userContext.getYukonUser()));
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        model.addAttribute("userMessage", accessor.getMessage(baseKey + "validateSent"));
        getConfigModelAndView(model, deviceId, userContext);
        return "configWidget/render.jsp";
    }
    
    @RequestMapping(value = "verifyConfig", method = RequestMethod.POST)
    public ModelAndView verifyConfig(HttpServletRequest request, HttpServletResponse response) throws Exception {
        //Use this method when the user clicks on "Out of Sync" 
        YukonUserContext userContext = YukonUserContextUtils.getYukonUserContext(request);
        ModelAndView mav = new ModelAndView("summary/outOfSync.jsp");
        SimpleDevice device = getDevice(request);
        VerifyResult verifyResult = deviceConfigService.verifyConfig(device, userContext.getYukonUser());
        mav.addObject("verifyResult", verifyResult);
        return mav;
    }
}

