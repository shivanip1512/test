package com.cannontech.common.device.config.model;

import org.springframework.validation.Errors;

import com.cannontech.common.device.config.dao.DeviceConfigurationDao;
import com.cannontech.web.input.validate.InputValidator;

/**
 * InputValidator implementation which validates that a ConfigurationBase
 * has a different name than any other existing ConfigurationBase
 */
public class ConfigurationBaseValidator implements InputValidator<ConfigurationBase> {

    private DeviceConfigurationDao dao;

    public DeviceConfigurationDao getDeviceConfigurationDao() {
        return dao;
    }

    public void setDeviceConfigurationDao(DeviceConfigurationDao dao) {
        this.dao = dao;
    }

    public String getDescription() {
        return "Name must be unique";
    }

    public void validate(String path, String displayName, ConfigurationBase value, Errors errors) {
        String name = value.getName();
        Integer id = value.getId();

        if(dao.checkForNameConflict(name, id) ){
           errors.rejectValue(path, 
                               "yukon.web.input.error.unique", 
                               new Object[] {"Name", name  },
                               "Name must be unique. '" + name + "' is already taken.");
        }
    }
}