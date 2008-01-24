package com.cannontech.web.amr.device.configuration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;

import com.cannontech.common.device.config.dao.DeviceConfigurationDao;
import com.cannontech.common.device.config.model.CategoryTemplate;
import com.cannontech.common.device.config.model.ConfigurationBase;
import com.cannontech.common.device.config.model.ConfigurationTemplate;
import com.cannontech.web.input.Input;
import com.cannontech.web.input.InputFormController;
import com.cannontech.web.input.InputRoot;
import com.cannontech.web.input.InputSource;

/**
 * Spring controller class for Device Configuration form input
 */
public class DeviceConfigurationFormController extends InputFormController {

    private InputRoot inputRoot = null;
    private ConfigurationTemplate configurationTemplate = null;
    private DeviceConfigurationDao deviceConfigurationDao = null;

    public void setDeviceConfigurationDao(DeviceConfigurationDao deviceConfigurationDao) {
        this.deviceConfigurationDao = deviceConfigurationDao;
    }

    public void setConfigurationTemplate(ConfigurationTemplate configurationTemplate) {
        this.configurationTemplate = configurationTemplate;
    }

    public void setInputRoot(InputRoot inputRoot) {
        this.inputRoot = inputRoot;
    }

    public void init() {

        List<Input<?>> inputList = inputRoot.getInputList();

        for (CategoryTemplate catTemplate : configurationTemplate.getCategoryList()) {
            inputList.addAll(catTemplate.getInputList());
        }

    }

    @Override
    public InputRoot getInputRoot() {
        return inputRoot;
    }

    @Override
    protected Map<String, Object> referenceData(HttpServletRequest request) throws Exception {

        Map<String, Object> dataMap = new HashMap<String, Object>();

        dataMap.put("configurationTemplate", configurationTemplate);

        InputSource<?> nameInput = inputRoot.getInputMap().get("name");
        dataMap.put("name", nameInput);

        return dataMap;
    }

    @Override
    protected Object formBackingObject(HttpServletRequest request) throws Exception {

        Integer configurationId = ServletRequestUtils.getIntParameter(request, "configurationId");

        if (configurationId != null) {
            return deviceConfigurationDao.getConfiguration(configurationId);
        } else {
            return super.formBackingObject(request);
        }
    }

    @Override
    protected ModelAndView onSubmit(Object command) throws Exception {

        ConfigurationBase config = (ConfigurationBase) command;

        deviceConfigurationDao.save(config);

        ModelAndView mav = new ModelAndView(getSuccessView());

        mav.addObject("message", "Successfully saved: " + config.getName());

        return mav;
    }

}
