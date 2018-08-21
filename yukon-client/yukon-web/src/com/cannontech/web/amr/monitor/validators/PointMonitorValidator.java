package com.cannontech.web.amr.monitor.validators;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;

import com.cannontech.amr.monitors.PointMonitor;
import com.cannontech.common.device.groups.service.DeviceGroupService;
import com.cannontech.common.device.groups.util.DeviceGroupUtil;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonValidationUtils;

public abstract class PointMonitorValidator<T extends PointMonitor> extends SimpleValidator<T> {

    @Autowired public DeviceGroupService deviceGroupService;
    
    public PointMonitorValidator(Class<T> objectType) {
        super(objectType);
    }

    public void doValidation(T monitor, Errors errors) {
        validateName(monitor, errors);
        if (!errors.hasErrors()
            && (monitor.getGroupName() == null || deviceGroupService.findGroupName(monitor.getGroupName()) == null)) {
            errors.reject("yukon.web.modules.amr.invalidGroupName");
        }
    }

    private void validateName(T monitor, Errors errors) {
        isNameValid(monitor, errors);
        isNameAvailable(monitor, errors);
    }
    
    abstract void isNameAvailable(T monitor, Errors errors);

    private void isNameValid(PointMonitor monitor, Errors errors) {
        YukonValidationUtils.rejectIfEmptyOrWhitespace(errors, "name", "yukon.web.error.isBlank");
        if (!errors.hasFieldErrors("name")) {
            YukonValidationUtils.checkExceedsMaxLength(errors, "name", monitor.getName(), 60);
            // Check the monitor name does not contain any invalid characters as we use it in Violations Group
            if (!DeviceGroupUtil.isValidName(monitor.getName())) {
                errors.rejectValue("name", "yukon.web.error.deviceGroupName.containsIllegalChars");
            }
        }
    }
}
