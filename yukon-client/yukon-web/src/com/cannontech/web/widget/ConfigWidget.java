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

import com.cannontech.amr.meter.dao.MeterDao;
import com.cannontech.amr.meter.model.Meter;
import com.cannontech.common.device.commands.CommandResultHolder;
import com.cannontech.common.device.config.dao.DeviceConfigurationDao;
import com.cannontech.common.device.config.model.DeviceConfiguration;
import com.cannontech.common.device.config.model.LightDeviceConfiguration;
import com.cannontech.common.device.config.model.VerifyResult;
import com.cannontech.common.device.config.service.DeviceConfigService;
import com.cannontech.common.util.CtiUtilities;
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

    @Autowired private MeterDao meterDao;
    @Autowired private DeviceConfigurationDao deviceConfigurationDao;
    @Autowired private DeviceConfigService deviceConfigService;
    
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
        
        List<LightDeviceConfiguration> existingConfigs = 
            deviceConfigurationDao.getAllConfigurationsByType(meter.getPaoType());
        
        mav.addObject("existingConfigs", existingConfigs);
        
        LightDeviceConfiguration config = deviceConfigurationDao.findConfigurationForDevice(meter);
        
        mav.addObject("currentConfigId", config != null ? config.getConfigurationId() : null);
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
        Meter meter = getMeter(request);
        
        final int configId = ServletRequestUtils.getRequiredIntParameter(request, "configuration");
        if (configId > -1) {
            DeviceConfiguration configuration = deviceConfigurationDao.getDeviceConfiguration(configId);
            deviceConfigurationDao.assignConfigToDevice(configuration, meter);
        } else {
            deviceConfigurationDao.unassignConfig(meter);
        }
        
        ModelAndView mav = getConfigModelAndView(request);
        return mav;
    }
    
    @RequestMapping
    public ModelAndView unassignConfig(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Meter meter = getMeter(request);
        
        deviceConfigurationDao.unassignConfig(meter);
        
        ModelAndView mav = getConfigModelAndView(request);
        return mav;
    }
    
    @RequestMapping
    public ModelAndView sendConfig(HttpServletRequest request, HttpServletResponse response) throws Exception {
        YukonUserContext userContext = YukonUserContextUtils.getYukonUserContext(request);
        ModelAndView mav = new ModelAndView("configWidget/configWidgetResult.jsp");
        Meter meter = getMeter(request);
        CommandResultHolder resultHolder = deviceConfigService.sendConfig(meter, userContext.getYukonUser());
        mav.addObject("sendResult", resultHolder);
        return mav;
    }
    
    @RequestMapping
    public ModelAndView readConfig(HttpServletRequest request, HttpServletResponse response) throws Exception {
        YukonUserContext userContext = YukonUserContextUtils.getYukonUserContext(request);
        ModelAndView mav = new ModelAndView("configWidget/configWidgetResult.jsp");
        Meter meter = getMeter(request);
        CommandResultHolder resultHolder = deviceConfigService.readConfig(meter, userContext.getYukonUser());
        mav.addObject("readResult", resultHolder);
        return mav;
    }
    
    @RequestMapping
    public ModelAndView verifyConfig(HttpServletRequest request, HttpServletResponse response) throws Exception {
        YukonUserContext userContext = YukonUserContextUtils.getYukonUserContext(request);
        ModelAndView mav = new ModelAndView("configWidget/configWidgetResult.jsp");
        Meter meter = getMeter(request);
        VerifyResult verifyResult = deviceConfigService.verifyConfig(meter, userContext.getYukonUser());
        mav.addObject("verifyResult", verifyResult);
        return mav;
    }
}

