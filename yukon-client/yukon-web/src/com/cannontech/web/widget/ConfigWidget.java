package com.cannontech.web.widget;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.cannontech.amr.meter.dao.MeterDao;
import com.cannontech.amr.meter.model.Meter;
import com.cannontech.common.device.YukonDevice;
import com.cannontech.common.device.commands.CommandRequestDeviceExecutor;
import com.cannontech.common.device.commands.CommandRequestExecutionType;
import com.cannontech.common.device.commands.CommandResultHolder;
import com.cannontech.common.device.config.dao.ConfigurationType;
import com.cannontech.common.device.config.dao.DeviceConfigurationDao;
import com.cannontech.common.device.config.model.ConfigurationBase;
import com.cannontech.common.device.config.model.VerifyResult;
import com.cannontech.common.device.groups.service.DeviceConfigService;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.web.widget.support.WidgetControllerBase;
import com.cannontech.web.widget.support.WidgetParameterHelper;

/**
 * Widget used to display basic device information
 */
public class ConfigWidget extends WidgetControllerBase {

    private MeterDao meterDao;
    private DeviceConfigurationDao deviceConfigurationDao;
    private CommandRequestDeviceExecutor commandRequestExecutor;
    private DeviceConfigService deviceConfigService;
    
    /**
     * This method renders the default deviceGroupWidget
     * 
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ModelAndView render(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ModelAndView mav = getConfigModelAndView(request);
        
        return mav;
    }

    private ModelAndView getConfigModelAndView(HttpServletRequest request) throws ServletRequestBindingException {
        ModelAndView mav = new ModelAndView("configWidget/render.jsp");
        Meter meter = getMeter(request);
        int deviceId = WidgetParameterHelper.getRequiredIntParameter(request, "deviceId");
        String meterType = meter.getTypeStr();
        ConfigurationType type = ConfigurationType.MCT410;
        if(meterType.contains("470")) {
            type = ConfigurationType.MCT470;
        }else if(meterType.contains("430")) {
            type = ConfigurationType.MCT430;
        }
        List<ConfigurationBase> existingConfigs = deviceConfigurationDao.getAllConfigurationsByType(type);
        mav.addObject("existingConfigs", existingConfigs);
        ConfigurationBase config = deviceConfigurationDao.getConfigurationForDevice(deviceId);
        mav.addObject("currentDeviceId", config != null ? config.getId() : -1);
        mav.addObject("currentConfigName", config != null ? config.getName() : CtiUtilities.STRING_NONE);
        return mav;
    }

    /**
     * @param request
     * @return
     * @throws ServletRequestBindingException
     */
    private Meter getMeter(HttpServletRequest request) throws ServletRequestBindingException {
        int deviceId = WidgetParameterHelper.getRequiredIntParameter(request, "deviceId");
        Meter meter = meterDao.getForId(deviceId);
        return meter;
    }
    
    @RequestMapping
    public ModelAndView assignConfig(HttpServletRequest request, HttpServletResponse response) throws Exception {
        int deviceId = WidgetParameterHelper.getRequiredIntParameter(request, "deviceId");
        Meter meter = getMeter(request);
        YukonDevice device = new YukonDevice(deviceId, meter.getType());
        
        final int configId = ServletRequestUtils.getRequiredIntParameter(request, "configuration");
        if(configId > -1) {
            ConfigurationBase configuration = deviceConfigurationDao.getConfiguration(configId);
            deviceConfigurationDao.assignConfigToDevice(configuration, device);
        }else {
            deviceConfigurationDao.unassignConfig(deviceId);
        }
        
        ModelAndView mav = getConfigModelAndView(request);
        return mav;
    }
    
    @RequestMapping
    public ModelAndView pushConfig(HttpServletRequest request, HttpServletResponse response, LiteYukonUser user) throws Exception {
        ModelAndView mav = new ModelAndView("configWidget/configWidgetResult.jsp");
        int deviceId = WidgetParameterHelper.getRequiredIntParameter(request, "deviceId");
        Meter meter = getMeter(request);
        YukonDevice device = new YukonDevice(deviceId, meter.getType());
        String commandString = "putconfig emetcon install all force";
        CommandResultHolder resultHolder = commandRequestExecutor.execute(device, commandString, CommandRequestExecutionType.DEVICE_CONFIG_PUSH, user);
        mav.addObject("pushResult", resultHolder);
        return mav;
    }
    
    @RequestMapping
    public ModelAndView readConfig(HttpServletRequest request, HttpServletResponse response, LiteYukonUser user) throws Exception {
        ModelAndView mav = new ModelAndView("configWidget/configWidgetResult.jsp");
        int deviceId = WidgetParameterHelper.getRequiredIntParameter(request, "deviceId");
        Meter meter = getMeter(request);
        YukonDevice device = new YukonDevice(deviceId, meter.getType());
        String commandString = "getconfig model";
        CommandResultHolder resultHolder = commandRequestExecutor.execute(device, commandString, CommandRequestExecutionType.DEVICE_COMMAND, user);
        mav.addObject("readResult", resultHolder);
        return mav;
    }
    
    @RequestMapping
    public ModelAndView verifyConfig(HttpServletRequest request, HttpServletResponse response, LiteYukonUser user) throws Exception {
        ModelAndView mav = new ModelAndView("configWidget/configWidgetResult.jsp");
        int deviceId = WidgetParameterHelper.getRequiredIntParameter(request, "deviceId");
        Meter meter = getMeter(request);
        YukonDevice device = new YukonDevice(deviceId, meter.getType());
        VerifyResult verifyResult = deviceConfigService.verifyConfig(device, user);
        mav.addObject("verifyResult", verifyResult);
        return mav;
    }

    @Required
    public void setMeterDao(MeterDao meterDao) {
        this.meterDao = meterDao;
    }

    @Required
    public void setDeviceConfigurationDao(DeviceConfigurationDao deviceConfigurationDao) {
        this.deviceConfigurationDao = deviceConfigurationDao;
    }
    
    @Required
    public void setCommandRequestExecutor(CommandRequestDeviceExecutor commandRequestExecutor) {
        this.commandRequestExecutor = commandRequestExecutor;
    }
    
    @Required
    public void setDeviceConfigService(DeviceConfigService deviceConfigService) {
        this.deviceConfigService = deviceConfigService;
    }
}

