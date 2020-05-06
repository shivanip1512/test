package com.cannontech.web.widget;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

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
import com.cannontech.common.device.config.model.VerifyResult;
import com.cannontech.common.device.config.service.DeviceConfigService;
import com.cannontech.common.device.config.service.DeviceConfigurationService;
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
import com.cannontech.web.widget.support.SimpleWidgetInput;
import com.cannontech.web.widget.support.WidgetControllerBase;
import com.cannontech.web.widget.support.WidgetParameterHelper;
import com.cannontech.yukon.IDatabaseCache;

@Controller
@RequestMapping("/configWidget/*")
public class ConfigWidget extends WidgetControllerBase {

    @Autowired private DeviceDao deviceDao;
    @Autowired private DeviceConfigurationDao deviceConfigurationDao;
    @Autowired private DeviceConfigService deviceConfigService;
    @Autowired private DataStreamingService dataStreamingService;
    @Autowired private ConfigurationSource configurationSource;
    @Autowired private DataStreamingAttributeHelper dataStreamingAttributeHelper;
    @Autowired private MeterProgrammingSummaryDao meterProgrammingSummaryDao;
    @Autowired private PaoDefinitionDao paoDefinitionDao;
    private ExecutorService executor = Executors.newCachedThreadPool();
    private final static String baseKey = "yukon.web.modules.tools.configs.summary.";

    @Autowired protected YukonUserContextMessageSourceResolver messageSourceResolver;
    @Autowired private IDatabaseCache dbCache;

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
        YukonDevice device = getDevice(request);
        int deviceId = device.getPaoIdentifier().getPaoId();
        mav.addObject("deviceId", deviceId);
        
        List<LightDeviceConfiguration> existingConfigs = 
            deviceConfigurationDao.getAllConfigurationsByType(device.getPaoIdentifier().getPaoType());
        
        mav.addObject("existingConfigs", existingConfigs);
        
        LightDeviceConfiguration config = deviceConfigurationDao.findConfigurationForDevice(device);
        
        mav.addObject("currentConfigId", config != null ? config.getConfigurationId() : null);
        mav.addObject("currentConfigName", config != null ? config.getName() : CtiUtilities.STRING_NONE);
        
        
        DeviceConfigState configState = deviceConfigurationDao.getDeviceConfigStatesByDeviceId(deviceId);
        
        if (configState == null
                || (configState.getState() == ConfigState.UNASSIGNED && configState.getState() == ConfigState.UNKNOWN)) {
            // "Current Configuration: None" should display. No Status row, no Actions row, but with the Change Configuration row
            // still below.
        } else if (configState.getStatus() == LastActionStatus.IN_PROGRESS) {
            // disable buttons
            // display status "Validation in progress" "Upload in progress" "Verify in progress" - not sure since we do not have a
            // button, but we can end up in this state for a short while
            configState.getAction();
        } else if (configState.getState() == ConfigState.IN_SYNC) {
            // display status "in sync"
        } else if (configState.getState() == ConfigState.OUT_OF_SYNC) {
            // display status "out of sync"
        } else if (configState.getState() == ConfigState.UNCONFIRMED && configState.getState() == ConfigState.UNREAD) {
            // display status "needs validation"
        }
 
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

        boolean configurableDevice = !existingConfigs.isEmpty();
        boolean dataStreamingEnabled =
            configurationSource.isLicenseEnabled(MasterConfigLicenseKey.RF_DATA_STREAMING_ENABLED);
        boolean streamableDevice =
            dataStreamingEnabled
                && !dataStreamingAttributeHelper.getSupportedAttributes(device.getPaoIdentifier().getPaoType()).isEmpty();
        mav.addObject("configurableDevice", configurableDevice);
        mav.addObject("streamableDevice", streamableDevice);
        
        //check for meter programming
        boolean enableMeterProgramming = configurationSource.isLicenseEnabled(MasterConfigLicenseKey.METER_PROGRAMMING_ENABLED);
        if (enableMeterProgramming) {
            boolean deviceSupported = paoDefinitionDao.isTagSupported(device.getPaoIdentifier().getPaoType(), PaoTag.METER_PROGRAMMING);
            if (deviceSupported) {
                mav.addObject("showMeterProgramming", true);
                try {
                    MeterProgramWidgetDisplay program = meterProgrammingSummaryDao.getProgramConfigurationByDeviceId(deviceId, userContext);
                    mav.addObject("meterProgram", program);
                } catch (NotFoundException e) {
                    //not programmed yet
                }
            }
        }

        return mav;
    }

    private SimpleDevice getDevice(HttpServletRequest request) throws ServletRequestBindingException {
        int deviceId = WidgetParameterHelper.getRequiredIntParameter(request, "deviceId");
        return deviceDao.getYukonDevice(deviceId);
    }
    
    @RequestMapping(value = "assignConfig", method = RequestMethod.POST)
    public ModelAndView assignConfig(HttpServletRequest request, HttpServletResponse response) throws ServletRequestBindingException, InvalidDeviceTypeException {
        SimpleDevice device = new SimpleDevice(getDevice(request));
        YukonUserContext userContext = YukonUserContextUtils.getYukonUserContext(request);
        final int configId = ServletRequestUtils.getRequiredIntParameter(request, "configuration");
        if (configId > -1) {
            DeviceConfiguration configuration = deviceConfigurationDao.getDeviceConfiguration(configId);
            DeviceConfigState configState = deviceConfigService.assignConfigToDevice(device, configuration,
                    userContext.getYukonUser());
            if(configState.getState() == ConfigState.OUT_OF_SYNC) {
                // display popup suggesting upload
            }
        } else {
            deviceConfigService.unassignConfig(device, userContext.getYukonUser());
        }
        
        ModelAndView mav = getConfigModelAndView(request);
        mav.addObject("configurableDevice", true);
        return mav;
    }
    
    @RequestMapping(value = "unassignConfig", method = RequestMethod.POST)
    public ModelAndView unassignConfig(HttpServletRequest request, HttpServletResponse response) throws Exception {
        SimpleDevice device = new SimpleDevice(getDevice(request));
        YukonUserContext userContext = YukonUserContextUtils.getYukonUserContext(request);
        deviceConfigService.unassignConfig(device, userContext.getYukonUser());
        ModelAndView mav = getConfigModelAndView(request);
        return mav;
    }
    
    @RequestMapping(value = "sendConfig", method = RequestMethod.POST)
    public ModelAndView sendConfig(HttpServletRequest request, HttpServletResponse response) throws Exception {
        YukonUserContext userContext = YukonUserContextUtils.getYukonUserContext(request);
        ModelAndView mav = new ModelAndView("configWidget/configWidgetResult.jsp");
        SimpleDevice device = getDevice(request);
        LiteYukonPAObject pao = dbCache.getAllPaosMap().get(device.getDeviceId());
        executor.submit(() -> deviceConfigService.sendConfig(device, userContext.getYukonUser()));
        // Display message in widget
        // new YukonMessageSourceResolvable(baseKey + "sendConfig.success", pao.getPaoName())
        return mav;
    }

    @RequestMapping(value = "readConfig", method = RequestMethod.POST)
    public ModelAndView readConfig(HttpServletRequest request, HttpServletResponse response) throws Exception {
        YukonUserContext userContext = YukonUserContextUtils.getYukonUserContext(request);
        ModelAndView mav = new ModelAndView("configWidget/configWidgetResult.jsp");
        SimpleDevice device = getDevice(request);
        LiteYukonPAObject pao = dbCache.getAllPaosMap().get(device.getDeviceId());
        executor.submit(() -> deviceConfigService.readConfig(device, userContext.getYukonUser()));
        // Display message in widget
        // new YukonMessageSourceResolvable(baseKey + "readConfig.success", pao.getPaoName()));
        return mav;
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

