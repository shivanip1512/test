package com.cannontech.web.api.dr.setup;

import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.Errors;

import com.cannontech.api.error.model.ApiErrorDetails;
import com.cannontech.common.dr.setup.LoadGroupDigiSep;
import com.cannontech.common.validator.YukonApiValidationUtils;

@Service
public class LoadGroupDigiSepValidator extends LoadGroupSetupValidator<LoadGroupDigiSep> {


    public LoadGroupDigiSepValidator() {
        super(LoadGroupDigiSep.class);
    }

    @Override
    public boolean supports(Class clazz) {
        return LoadGroupDigiSep.class.isAssignableFrom(clazz);
    }

    @Override
    protected void doValidation(LoadGroupDigiSep loadGroup, Errors errors) {

        YukonApiValidationUtils.checkIfFieldRequired("deviceClassSet", errors, loadGroup.getDeviceClassSet(), "Device Class set");

        if (!errors.hasFieldErrors("deviceClassSet")) {
            if (CollectionUtils.isEmpty(loadGroup.getDeviceClassSet())) {
                errors.rejectValue("deviceClassSet", ApiErrorDetails.FIELD_REQUIRED.getCodeString(),
                        new Object[] { "Device Class set" }, "");
            }
        }

        // Utility Enrollment Group
        YukonApiValidationUtils.checkIfFieldRequired("utilityEnrollmentGroup", errors, loadGroup.getUtilityEnrollmentGroup(), "Utility Enrollment Group");

        if (!errors.hasFieldErrors("utilityEnrollmentGroup")) {
            if (loadGroup.getId() != null && loadGroup.getUtilityEnrollmentGroup() == 0) {
                YukonApiValidationUtils.checkRange(errors, "utilityEnrollmentGroup", loadGroup.getUtilityEnrollmentGroup(), 1,
                        255, true);
            }
        }
        
        if (!errors.hasFieldErrors("utilityEnrollmentGroup")) {
            YukonApiValidationUtils.checkRange(errors, "utilityEnrollmentGroup", loadGroup.getUtilityEnrollmentGroup(), 1,
                255, true);
        }

        // Ramp In Time
        YukonApiValidationUtils.checkIfFieldRequired("rampInMinutes", errors, loadGroup.getRampInMinutes(), "Ramp In Time" );
        if (!errors.hasFieldErrors("rampInMinutes")) {
            YukonApiValidationUtils.checkRange(errors, "rampInMinutes", loadGroup.getRampInMinutes(), -99999, 99999, true);
        }

        // Ramp Out Time
        YukonApiValidationUtils.checkIfFieldRequired("rampOutMinutes", errors, loadGroup.getRampOutMinutes(), "Ramp Out Time" );
        if (!errors.hasFieldErrors("rampOutMinutes")) {
            YukonApiValidationUtils.checkRange(errors, "rampOutMinutes", loadGroup.getRampOutMinutes(), -99999, 99999,
                true);
        }
    }
}
