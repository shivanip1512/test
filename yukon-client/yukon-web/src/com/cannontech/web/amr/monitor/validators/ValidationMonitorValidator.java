package com.cannontech.web.amr.monitor.validators;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;

import com.cannontech.common.validation.dao.ValidationMonitorDao;
import com.cannontech.common.validation.model.ValidationMonitor;
import com.cannontech.common.validator.YukonValidationUtils;

@Service
public class ValidationMonitorValidator extends PointMonitorValidator<ValidationMonitor> {
    @Autowired private ValidationMonitorDao validationMonitorDao;

    public ValidationMonitorValidator() {
        super(ValidationMonitor.class);
    }

    @Override
    public void doValidation(ValidationMonitor validationMonitor, Errors errors) {
        super.doValidation(validationMonitor, errors);
        YukonValidationUtils.checkIsPositiveDouble(errors, "reasonableMaxKwhPerDay",
            validationMonitor.getReasonableMaxKwhPerDay());
        YukonValidationUtils.checkIsPositiveDouble(errors, "kwhSlopeError", validationMonitor.getKwhSlopeError());
        YukonValidationUtils.checkIsPositiveDouble(errors, "peakHeightMinimum",
            validationMonitor.getPeakHeightMinimum());
    }

    @Override
    void isNameAvailable(ValidationMonitor monitor, Errors errors) {
        boolean idSpecified = monitor.getValidationMonitorId() != null;

        boolean nameAvailable = !validationMonitorDao.processorExistsWithName(monitor.getName());

        if (!nameAvailable) {
            if (!idSpecified) {
                // For create, we must have an available name
                errors.rejectValue("name", "yukon.web.error.nameConflict");
            } else {
                // For edit, we can use our own existing name
                ValidationMonitor existingValidator =
                    validationMonitorDao.getById(monitor.getValidationMonitorId());
                if (!existingValidator.getName().equals(monitor.getName())) {
                    errors.rejectValue("name", "yukon.web.error.nameConflict");
                }
            }
        }
    }
}
