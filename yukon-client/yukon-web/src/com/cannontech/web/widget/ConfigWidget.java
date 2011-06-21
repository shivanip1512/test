package com.cannontech.web.widget;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.cannontech.amr.meter.dao.MeterDao;
import com.cannontech.amr.meter.model.Meter;
import com.cannontech.common.device.commands.CommandResultHolder;
import com.cannontech.common.device.config.dao.ConfigurationType;
import com.cannontech.common.device.config.dao.DeviceConfigurationDao;
import com.cannontech.common.device.config.model.ConfigurationBase;
import com.cannontech.common.device.config.model.VerifyResult;
import com.cannontech.common.device.config.service.DeviceConfigService;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDao;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.servlet.YukonUserContextUtils;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.widget.support.WidgetControllerBase;
import com.cannontech.web.widget.support.WidgetParameterHelper;
import com.google.common.collect.Lists;

/**
 * Widget used to display basic device information
 */
@Controller
@RequestMapping("/configWidget/*")
public class ConfigWidget extends WidgetControllerBase {

    private MeterDao meterDao;
    private DeviceConfigurationDao deviceConfigurationDao;
    private DeviceConfigService deviceConfigService;
    private PaoDefinitionDao paoDefinitionDao;
    
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
        
        List<ConfigurationBase> existingConfigs = Lists.newArrayList();
        for (ConfigurationType type : ConfigurationType.values()) {
        	if (paoDefinitionDao.isTagSupported(meter.getPaoType(), type.getSupportedDeviceTag())) {
                existingConfigs = deviceConfigurationDao.getAllConfigurationsByType(type);
                break;
        	}
        }
        mav.addObject("existingConfigs", existingConfigs);
        
        ConfigurationBase config = deviceConfigurationDao.findConfigurationForDevice(meter);
        mav.addObject("currentConfigId", config != null ? config.getId() : -1);
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
        
        final int configId = ServletRequestUtils.getRequiredIntParameter(request, "configuration");
        if(configId > -1) {
            ConfigurationBase configuration = deviceConfigurationDao.getConfiguration(configId);
            deviceConfigurationDao.assignConfigToDevice(configuration, meter);
        }else {
            deviceConfigurationDao.unassignConfig(deviceId);
        }
        
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

    @Required
    public void setMeterDao(MeterDao meterDao) {
        this.meterDao = meterDao;
    }

    @Required
    public void setDeviceConfigurationDao(DeviceConfigurationDao deviceConfigurationDao) {
        this.deviceConfigurationDao = deviceConfigurationDao;
    }
    
    @Required
    public void setDeviceConfigService(DeviceConfigService deviceConfigService) {
        this.deviceConfigService = deviceConfigService;
    }
    
    @Required
    public void setPaoDefinitionDao(PaoDefinitionDao paoDefinitionDao) {
        this.paoDefinitionDao = paoDefinitionDao;
    }
}

