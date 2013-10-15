package com.cannontech.web.widget;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.cannontech.common.device.commands.CommandResultHolder;
import com.cannontech.common.device.config.dao.DeviceConfigurationDao;
import com.cannontech.common.device.config.model.DeviceConfiguration;
import com.cannontech.common.device.config.model.LightDeviceConfiguration;
import com.cannontech.common.device.config.model.VerifyResult;
import com.cannontech.common.device.config.service.DeviceConfigService;
import com.cannontech.common.device.config.service.DeviceConfigurationService;
import com.cannontech.common.pao.YukonDevice;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.servlet.YukonUserContextUtils;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.widget.support.WidgetControllerBase;
import com.cannontech.web.widget.support.WidgetParameterHelper;

/**
 * Widget used to display basic device information
 */
@Controller
@RequestMapping("/configWidget/*")
public class ConfigWidget extends WidgetControllerBase {

    @Autowired private DeviceDao deviceDao;
    @Autowired private DeviceConfigurationDao deviceConfigurationDao;
    @Autowired private DeviceConfigService deviceConfigService;
    @Autowired private DeviceConfigurationService deviceConfigurationService;
    
    /**
     * This method renders the default deviceGroupWidget
     * 
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @Override
    public ModelAndView render(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ModelAndView mav = getConfigModelAndView(request);
        
        return mav;
    }

    private ModelAndView getConfigModelAndView(HttpServletRequest request) throws ServletRequestBindingException {
        ModelAndView mav = new ModelAndView("configWidget/render.jsp");
        YukonDevice device = getYukonDevice(request);
        
        List<LightDeviceConfiguration> existingConfigs = 
            deviceConfigurationDao.getAllConfigurationsByType(device.getPaoIdentifier().getPaoType());
        
        mav.addObject("existingConfigs", existingConfigs);
        
        LightDeviceConfiguration config = deviceConfigurationDao.findConfigurationForDevice(device);
        
        mav.addObject("currentConfigId", config != null ? config.getConfigurationId() : null);
        mav.addObject("currentConfigName", config != null ? config.getName() : CtiUtilities.STRING_NONE);
        
        return mav;
    }

    /**
     * @param request
     * @return
     * @throws ServletRequestBindingException
     */
    private YukonDevice getYukonDevice(HttpServletRequest request) throws ServletRequestBindingException {
        int deviceId = WidgetParameterHelper.getRequiredIntParameter(request, "deviceId");
        return deviceDao.getYukonDevice(deviceId);
    }
    
    @RequestMapping
    public ModelAndView assignConfig(HttpServletRequest request, HttpServletResponse response) throws Exception {
        YukonDevice device = getYukonDevice(request);
        
        final int configId = ServletRequestUtils.getRequiredIntParameter(request, "configuration");
        if (configId > -1) {
            DeviceConfiguration configuration = deviceConfigurationDao.getDeviceConfiguration(configId);
            deviceConfigurationService.assignConfigToDevice(configuration, device);
        } else {
            deviceConfigurationService.unassignConfig(device);
        }
        
        ModelAndView mav = getConfigModelAndView(request);
        return mav;
    }
    
    @RequestMapping
    public ModelAndView unassignConfig(HttpServletRequest request, HttpServletResponse response) throws Exception {
        YukonDevice device = getYukonDevice(request);
        
        deviceConfigurationService.unassignConfig(device);
        
        ModelAndView mav = getConfigModelAndView(request);
        return mav;
    }
    
    @RequestMapping
    public ModelAndView sendConfig(HttpServletRequest request, HttpServletResponse response) throws Exception {
        YukonUserContext userContext = YukonUserContextUtils.getYukonUserContext(request);
        ModelAndView mav = new ModelAndView("configWidget/configWidgetResult.jsp");
        YukonDevice device = getYukonDevice(request);
        CommandResultHolder resultHolder = deviceConfigService.sendConfig(device, userContext.getYukonUser());
        mav.addObject("sendResult", resultHolder);
        return mav;
    }
    
    @RequestMapping
    public ModelAndView readConfig(HttpServletRequest request, HttpServletResponse response) throws Exception {
        YukonUserContext userContext = YukonUserContextUtils.getYukonUserContext(request);
        ModelAndView mav = new ModelAndView("configWidget/configWidgetResult.jsp");
        YukonDevice device = getYukonDevice(request);
        CommandResultHolder resultHolder = deviceConfigService.readConfig(device, userContext.getYukonUser());
        mav.addObject("readResult", resultHolder);
        return mav;
    }
    
    @RequestMapping
    public ModelAndView verifyConfig(HttpServletRequest request, HttpServletResponse response) throws Exception {
        YukonUserContext userContext = YukonUserContextUtils.getYukonUserContext(request);
        ModelAndView mav = new ModelAndView("configWidget/configWidgetResult.jsp");
        YukonDevice device = getYukonDevice(request);
        VerifyResult verifyResult = deviceConfigService.verifyConfig(device, userContext.getYukonUser());
        mav.addObject("verifyResult", verifyResult);
        return mav;
    }
}

