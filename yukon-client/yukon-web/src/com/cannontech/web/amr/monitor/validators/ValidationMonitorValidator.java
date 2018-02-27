package com.cannontech.web.amr.monitor.validators;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;

import com.cannontech.common.device.groups.service.DeviceGroupService;
import com.cannontech.common.device.groups.util.DeviceGroupUtil;
import com.cannontech.common.validation.dao.ValidationMonitorDao;
import com.cannontech.common.validation.model.ValidationMonitor;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonValidationUtils;

@Service
public class ValidationMonitorValidator extends SimpleValidator<ValidationMonitor> {
    @Autowired private ValidationMonitorDao validationMonitorDao;
    @Autowired private DeviceGroupService deviceGroupService;

    public ValidationMonitorValidator() {
        super(ValidationMonitor.class);
    }

    @Override
    public void doValidation(ValidationMonitor validationMonitor, Errors errors) {
        validateName(validationMonitor, errors);
        if (!errors.hasErrors() && (validationMonitor.getDeviceGroupName() == null
            || deviceGroupService.findGroupName(validationMonitor.getDeviceGroupName()) == null)) {
            errors.reject("yukon.web.modules.amr.invalidGroupName");
        }
        YukonValidationUtils.checkIsPositiveDouble(errors, "reasonableMaxKwhPerDay",
            validationMonitor.getReasonableMaxKwhPerDay());
        YukonValidationUtils.checkIsPositiveDouble(errors, "kwhSlopeError", validationMonitor.getKwhSlopeError());
        YukonValidationUtils.checkIsPositiveDouble(errors, "peakHeightMinimum",
            validationMonitor.getPeakHeightMinimum());
    }

    private void validateName(ValidationMonitor validationMonitor, Errors errors) {

        YukonValidationUtils.rejectIfEmptyOrWhitespace(errors, "name", "yukon.web.error.isBlank");
        if (!errors.hasFieldErrors("name")) {
            YukonValidationUtils.checkExceedsMaxLength(errors, "name", validationMonitor.getName(), 60);
            if (!DeviceGroupUtil.isValidName(validationMonitor.getName())) {
                errors.rejectValue("name", "yukon.web.error.deviceGroupName.containsIllegalChars");
            }
        }
        boolean idSpecified = validationMonitor.getValidationMonitorId() != null;

        boolean nameAvailable = !validationMonitorDao.processorExistsWithName(validationMonitor.getName());

        if (!nameAvailable) {
            if (!idSpecified) {
                // For create, we must have an available name
                errors.rejectValue("name", "yukon.web.error.nameConflict");
            } else {
                // For edit, we can use our own existing name
                ValidationMonitor existingValidator =
                    validationMonitorDao.getById(validationMonitor.getValidationMonitorId());
                if (!existingValidator.getName().equals(validationMonitor.getName())) {
                    errors.rejectValue("name", "yukon.web.error.nameConflict");
                }
            }
        }
    }
}
