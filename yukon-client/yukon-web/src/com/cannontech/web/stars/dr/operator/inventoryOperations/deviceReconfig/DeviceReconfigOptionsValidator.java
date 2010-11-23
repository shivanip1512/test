package com.cannontech.web.stars.dr.operator.inventoryOperations.deviceReconfig;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;

import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.stars.dr.hardware.dao.InventoryConfigTaskDao;
import com.cannontech.web.stars.dr.operator.inventoryOperations.deviceReconfig.model.DeviceReconfigOptions;

public class DeviceReconfigOptionsValidator extends SimpleValidator<DeviceReconfigOptions> {
    
    InventoryConfigTaskDao inventoryConfigTaskDao;

    public DeviceReconfigOptionsValidator() {
        super(DeviceReconfigOptions.class);
    }

    @Override
    protected void doValidation(DeviceReconfigOptions target, Errors errors) {
        YukonValidationUtils.rejectIfEmptyOrWhitespace(errors, "name", "yukon.web.modules.operator.deviceReconfig.error.required.name");
        YukonValidationUtils.checkExceedsMaxLength(errors, "name", target.getName(), 250);
        
        if (inventoryConfigTaskDao.findTask(target.getName()) != null) {
            errors.rejectValue("name", "yukon.web.modules.operator.deviceReconfig.error.unavailable.name");
        }
    }
    
    @Autowired
    public void setInventoryConfigTaskDao(InventoryConfigTaskDao inventoryConfigTaskDao) {
        this.inventoryConfigTaskDao = inventoryConfigTaskDao;
    }
    
}