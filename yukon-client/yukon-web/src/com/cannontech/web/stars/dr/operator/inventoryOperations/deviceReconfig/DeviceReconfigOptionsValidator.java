package com.cannontech.web.stars.dr.operator.inventoryOperations.deviceReconfig;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;

import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.stars.dr.deviceReconfig.dao.DeviceReconfigDao;
import com.cannontech.web.stars.dr.operator.inventoryOperations.deviceReconfig.model.DeviceReconfigOptions;

public class DeviceReconfigOptionsValidator extends SimpleValidator<DeviceReconfigOptions> {
    
    DeviceReconfigDao deviceReconfigDao;

    public DeviceReconfigOptionsValidator() {
        super(DeviceReconfigOptions.class);
    }

    @Override
    protected void doValidation(DeviceReconfigOptions target, Errors errors) {
        YukonValidationUtils.rejectIfEmptyOrWhitespace(errors, "name", "yukon.web.modules.operator.deviceReconfig.error.required.name");
        YukonValidationUtils.checkExceedsMaxLength(errors, "name", target.getName(), 250);
        
        if (deviceReconfigDao.nameInUse(target.getName())) {
            errors.rejectValue("name", "yukon.web.modules.operator.deviceReconfig.error.unavailable.name");
        }
    }
    
    @Autowired
    public void setDeviceReconfigDao(DeviceReconfigDao deviceReconfigDao) {
        this.deviceReconfigDao = deviceReconfigDao;
    }
    
}