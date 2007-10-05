package com.cannontech.web.amr.device.configuration;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.cannontech.common.device.config.dao.ConfigurationType;
import com.cannontech.common.device.config.dao.DeviceConfigurationDao;
import com.cannontech.common.device.config.model.ConfigurationBase;
import com.cannontech.common.device.config.model.ConfigurationTemplate;

/**
 * Spring controller class for Device Configuration
 */
public class DeviceConfigurationController extends MultiActionController {

    public DeviceConfigurationDao deviceConfigurationDao = null;

    public void setDeviceConfigurationDao(DeviceConfigurationDao deviceConfigurationDao) {
        this.deviceConfigurationDao = deviceConfigurationDao;
    }

    public ModelAndView home(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {

        ModelAndView mav = new ModelAndView("home");

        List<String> configurationTemplateList = new ArrayList<String>();
        List<ConfigurationTemplate> templates = deviceConfigurationDao.getAllConfigurationTemplates();
        for (ConfigurationTemplate template : templates) {
            configurationTemplateList.add(template.getName());
        }

        mav.addObject("configurationTemplateList", configurationTemplateList);

        List<ConfigurationBase> existingConfigs = deviceConfigurationDao.getAllConfigurations();
        mav.addObject("existingConfigs", existingConfigs);

        return mav;
    }

    public ModelAndView createConfig(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {

        String configTemplate = ServletRequestUtils.getStringParameter(request,
                                                                       "configurationTemplate");

        ConfigurationTemplate template = deviceConfigurationDao.getConfigurationTemplate(configTemplate);

        String viewPath = new String("redirect:/spring/deviceConfiguration/" + template.getView());

        ModelAndView mav = new ModelAndView(viewPath);

        return mav;
    }

    public ModelAndView editConfig(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {

        Integer configId = ServletRequestUtils.getIntParameter(request, "configuration");

        ConfigurationBase configuration = deviceConfigurationDao.getConfiguration(configId);

        ConfigurationType type = configuration.getType();
        String templateName = type.getConfigurationTemplateName();
        ConfigurationTemplate template = deviceConfigurationDao.getConfigurationTemplate(templateName);

        String viewPath = new String("redirect:/spring/deviceConfiguration/" + template.getView());

        ModelAndView mav = new ModelAndView(viewPath);

        mav.addObject("configurationId", configId.toString());

        return mav;
    }

    public ModelAndView removeConfig(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {

        Integer configId = ServletRequestUtils.getIntParameter(request, "configuration");

        deviceConfigurationDao.delete(configId);

        String viewPath = new String("redirect:/spring/deviceConfiguration?home");

        ModelAndView mav = new ModelAndView(viewPath);

        mav.addObject("message", "Successfully removed device configuration");

        return mav;
    }

    public ModelAndView cloneConfig(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {

        Integer configId = ServletRequestUtils.getIntParameter(request, "configuration");

        ConfigurationBase configuration = deviceConfigurationDao.getConfiguration(configId);

        // Save config with new name and id
        configuration.setName(configuration.getName() + " (copy)");
        configuration.setId(null);
        deviceConfigurationDao.save(configuration);

        ConfigurationType type = configuration.getType();
        String templateName = type.getConfigurationTemplateName();
        ConfigurationTemplate template = deviceConfigurationDao.getConfigurationTemplate(templateName);

        String viewPath = new String("redirect:/spring/deviceConfiguration/" + template.getView());

        ModelAndView mav = new ModelAndView(viewPath);

        mav.addObject("configurationId", configuration.getId().toString());

        return mav;
    }

}
