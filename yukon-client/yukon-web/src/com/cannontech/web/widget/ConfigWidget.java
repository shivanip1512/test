package com.cannontech.web.widget;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cannontech.amr.rfn.dataStreaming.model.DataStreamingConfig;
import com.cannontech.amr.rfn.dataStreaming.model.DiscrepancyResult;
import com.cannontech.amr.rfn.dataStreaming.service.DataStreamingService;
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.config.MasterConfigLicenseKey;
import com.cannontech.common.device.config.dao.DeviceConfigurationDao;
import com.cannontech.common.device.config.dao.DeviceConfigurationDao.ConfigState;
import com.cannontech.common.device.config.dao.DeviceConfigurationDao.LastActionStatus;
import com.cannontech.common.device.config.dao.InvalidDeviceTypeException;
import com.cannontech.common.device.config.model.DeviceConfigState;
import com.cannontech.common.device.config.model.DeviceConfiguration;
import com.cannontech.common.device.config.model.LightDeviceConfiguration;
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
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.bulk.service.DeviceConfigAssignService;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.cannontech.web.tools.device.programming.dao.MeterProgrammingSummaryDao;
import com.cannontech.web.tools.device.programming.model.MeterProgramSummaryDetail;
import com.cannontech.web.widget.support.AdvancedWidgetControllerBase;
import com.cannontech.web.widget.support.SimpleWidgetInput;
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
    @Autowired private RolePropertyDao rolePropertyDao;
    @Autowired private DeviceConfigAssignService deviceConfigAssignService;

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
                    MeterProgramSummaryDetail program = meterProgrammingSummaryDao.getProgramConfigurationByDeviceId(deviceId, userContext);
                    model.addAttribute("isInsufficentFirmware", program.getProgramInfo().getSource().isOldFirmware());
                    model.addAttribute("meterProgram", program);
                } catch (NotFoundException e) {
                    //not programmed yet
                }
            }
        }
    }
    
    @GetMapping("getStatus")
    @ResponseBody
    public Map<String, Object> getStatus(ModelMap model, int deviceId, YukonUserContext userContext) {
        Map<String, Object> jsonResponse = new HashMap<>();
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        DeviceConfigState configState = deviceConfigurationDao.getDeviceConfigStateByDeviceId(deviceId); 
        boolean isInProgress = false;
        boolean isInSync = false;
        boolean notConfigured = false;
        boolean isOutOfSync = false;
  
        String statusText = null;
                        
        if (configState == null
                || (configState.getCurrentState() == ConfigState.UNASSIGNED || configState.getCurrentState() == ConfigState.UNKNOWN)) {
            // "Current Configuration: None" should display. No Status row, no Actions row, but with the Change Configuration row
            // still below.
            notConfigured = true;
        } else if (configState.getLastActionStatus() == LastActionStatus.IN_PROGRESS) {
            statusText = accessor.getMessage(configState.getLastActionStatus());
            isInProgress = true;
            // disable buttons
            // display status "In Progress" -  we can end up in this state for a short while
        } else if (configState.getCurrentState() == ConfigState.IN_SYNC) {
            // display status "in sync"
            statusText = accessor.getMessage(configState.getCurrentState());
            isInSync = true;
        } else if (configState.getCurrentState() == ConfigState.OUT_OF_SYNC || configState.getCurrentState() == ConfigState.UNREAD) {
            // display status "needs upload"
            statusText = accessor.getMessage(configState.getCurrentState());
            isOutOfSync = true;
        } else if (configState.getCurrentState() == ConfigState.UNCONFIRMED) {
            // display status "needs validation"
            statusText = accessor.getMessage(configState.getCurrentState());
        }
        
        jsonResponse.put("statusText", statusText);
        jsonResponse.put("notConfigured", notConfigured);
        jsonResponse.put("isInProgress", isInProgress);
        jsonResponse.put("isInSync", isInSync);
        jsonResponse.put("isOutOfSync", isOutOfSync);
        
        if (configState != null && configState.getLastActionStatus() == LastActionStatus.FAILURE
                && configState.getCreId() != null) {
            Integer errorCode = deviceConfigurationDao.getErrorCodeByDeviceId(deviceId);
            jsonResponse.put("errorCode", errorCode);
        }
        return jsonResponse;

    }
    
    @PostMapping("changeConfig")
    @CheckRoleProperty(YukonRoleProperty.ASSIGN_CONFIG)
    @ResponseBody
    public Map<String, Object> changeConfig(ModelMap model, int deviceId, int configuration, YukonUserContext userContext) throws InvalidDeviceTypeException {
        Map<String, Object> jsonResponse = new HashMap<>();
        SimpleDevice device = deviceDao.getYukonDevice(deviceId);
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        DeviceConfigState currentConfigState = deviceConfigurationDao.getDeviceConfigStateByDeviceId(deviceId);
        if (currentConfigState != null && currentConfigState.getLastActionStatus() == LastActionStatus.IN_PROGRESS) {
            jsonResponse.put("errorMessage", accessor.getMessage(baseKey + "actionInProgress"));
        } else {
            if (configuration > -1) {
                DeviceConfiguration deviceConfig = deviceConfigurationDao.getDeviceConfiguration(configuration);
                deviceConfigAssignService.assign(configuration, device, userContext);
                DeviceConfigState configState = deviceConfigurationDao.getDeviceConfigStateByDeviceId(deviceId);
                
                if (configState != null && 
                        (configState.getCurrentState() == ConfigState.OUT_OF_SYNC || configState.getCurrentState() == ConfigState.UNREAD)) {
                    //check for upload permission
                    if (rolePropertyDao.checkProperty(YukonRoleProperty.SEND_READ_CONFIG, userContext.getYukonUser())) {
                        LiteYukonPAObject pao = dbCache.getAllPaosMap().get(device.getDeviceId());
                        // display popup suggesting upload
                        jsonResponse.put("displayUploadPopup", true);
                        jsonResponse.put("popupMessage", accessor.getMessage(baseKey + "uploadPopup.message", deviceConfig.getName(), pao.getPaoName()));
                    }
                }
            } else {
                deviceConfigAssignService.unassign(device, userContext);
            }
        }

        return jsonResponse;

    }
    
    @PostMapping("removeConfig")
    @CheckRoleProperty(YukonRoleProperty.ASSIGN_CONFIG)
    public String removeConfig(ModelMap model, int deviceId, YukonUserContext userContext) throws InvalidDeviceTypeException {
        SimpleDevice device = deviceDao.getYukonDevice(deviceId);
        DeviceConfigState configState = deviceConfigurationDao.getDeviceConfigStateByDeviceId(deviceId);
        if (configState != null && configState.getLastActionStatus() == LastActionStatus.IN_PROGRESS) {
            MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(userContext);
            model.addAttribute("errorMessage", accessor.getMessage(baseKey + "actionInProgress"));
        } else {
            deviceConfigAssignService.unassign(device, userContext);
        }
        getConfigModelAndView(model, deviceId, userContext);
        return "configWidget/render.jsp";
    }
    
    @PostMapping("uploadConfig")
    @CheckRoleProperty(YukonRoleProperty.SEND_READ_CONFIG)
    public String uploadConfig(ModelMap model, int deviceId, YukonUserContext userContext) {
        SimpleDevice device = deviceDao.getYukonDevice(deviceId);
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        DeviceConfigState configState = deviceConfigurationDao.getDeviceConfigStateByDeviceId(deviceId);
        if (configState != null && configState.getLastActionStatus() == LastActionStatus.IN_PROGRESS) {
            model.addAttribute("errorMessage", accessor.getMessage(baseKey + "actionInProgress"));
        } else {
            executor.submit(() -> deviceConfigService.sendConfig(device, userContext.getYukonUser()));
            model.addAttribute("userMessage", accessor.getMessage(baseKey + "uploadSent"));
        }
        getConfigModelAndView(model, deviceId, userContext);
        return "configWidget/render.jsp";
    }

    @PostMapping("validateConfig")
    @CheckRoleProperty(YukonRoleProperty.SEND_READ_CONFIG)
    public String validateConfig(ModelMap model, int deviceId, YukonUserContext userContext) {
        SimpleDevice device = deviceDao.getYukonDevice(deviceId);
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        DeviceConfigState configState = deviceConfigurationDao.getDeviceConfigStateByDeviceId(deviceId);
        if (configState != null && configState.getLastActionStatus() == LastActionStatus.IN_PROGRESS) {
            model.addAttribute("errorMessage", accessor.getMessage(baseKey + "actionInProgress"));
        } else {
            executor.submit(() -> deviceConfigService.readConfig(device, userContext.getYukonUser()));
            model.addAttribute("userMessage", accessor.getMessage(baseKey + "validateSent"));
        }
        getConfigModelAndView(model, deviceId, userContext);
        return "configWidget/render.jsp";
    }
    
}

