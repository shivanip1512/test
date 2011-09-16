package com.cannontech.web.stars.dr.operator.hardware.validator;

import org.apache.commons.lang.StringUtils;
import org.springframework.validation.Errors;

import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.stars.dr.hardware.model.Thermostat;

public class ThermostatValidator extends SimpleValidator<Thermostat> {
    public ThermostatValidator() {
        super(Thermostat.class);
    }

    @Override
    public void doValidation(Thermostat thermostat, Errors errors) {
        
        /* Serial Number */
        if (StringUtils.isBlank(thermostat.getSerialNumber())) {
            errors.rejectValue("serialNumber", "yukon.web.error.required");
        }
        YukonValidationUtils.checkExceedsMaxLength(errors, "serialNumber", thermostat.getSerialNumber(), 30);
        
        /* Device Label */
        YukonValidationUtils.checkExceedsMaxLength(errors, "deviceLabel", thermostat.getDeviceLabel(), 60);
        
        /* Hardware Type */
        /* Inventory Category */
        /* Route ID */
        /* Hardware Status */
        /* Scheduleable Thermostat Type */
    }
}
