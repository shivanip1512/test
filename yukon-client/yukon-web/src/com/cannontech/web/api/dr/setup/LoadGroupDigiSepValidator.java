package com.cannontech.web.api.dr.setup;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.Errors;

import com.cannontech.api.error.model.ApiErrorDetails;
import com.cannontech.common.dr.setup.LoadGroupDigiSep;
import com.cannontech.common.validator.YukonApiValidationUtils;

@Service
public class LoadGroupDigiSepValidator extends LoadGroupSetupValidator<LoadGroupDigiSep> {

    @Autowired private YukonApiValidationUtils yukonApiValidationUtils;
    public LoadGroupDigiSepValidator() {
        super(LoadGroupDigiSep.class);
    }

    @Override
    public boolean supports(Class clazz) {
        return LoadGroupDigiSep.class.isAssignableFrom(clazz);
    }

    @Override
    protected void doValidation(LoadGroupDigiSep loadGroup, Errors errors) {

        yukonApiValidationUtils.checkIfFieldRequired("deviceClassSet", errors, loadGroup.getDeviceClassSet(), "Device Class set");

        if (!errors.hasFieldErrors("deviceClassSet")) {
            if (CollectionUtils.isEmpty(loadGroup.getDeviceClassSet())) {
                errors.rejectValue("deviceClassSet", ApiErrorDetails.FIELD_REQUIRED.getCodeString(),
                        new Object[] { "Device Class set" }, "");
            }
        }

        // Utility Enrollment Group
        yukonApiValidationUtils.checkIfFieldRequired("utilityEnrollmentGroup", errors, loadGroup.getUtilityEnrollmentGroup(), "Utility Enrollment Group");

        if (!errors.hasFieldErrors("utilityEnrollmentGroup")) {
            if (loadGroup.getId() != null && loadGroup.getUtilityEnrollmentGroup() == 0) {
                yukonApiValidationUtils.checkRange(errors, "utilityEnrollmentGroup", loadGroup.getUtilityEnrollmentGroup(), 1,
                        255, true);
            }
        }
        
        if (!errors.hasFieldErrors("utilityEnrollmentGroup")) {
            yukonApiValidationUtils.checkRange(errors, "utilityEnrollmentGroup", loadGroup.getUtilityEnrollmentGroup(), 1,
                255, true);
        }

        // Ramp In Time
        yukonApiValidationUtils.checkIfFieldRequired("rampInMinutes", errors, loadGroup.getRampInMinutes(), "Ramp In Time" );
        if (!errors.hasFieldErrors("rampInMinutes")) {
            yukonApiValidationUtils.checkRange(errors, "rampInMinutes", loadGroup.getRampInMinutes(), -99999, 99999, true);
        }

        // Ramp Out Time
        yukonApiValidationUtils.checkIfFieldRequired("rampOutMinutes", errors, loadGroup.getRampOutMinutes(), "Ramp Out Time" );
        if (!errors.hasFieldErrors("rampOutMinutes")) {
            yukonApiValidationUtils.checkRange(errors, "rampOutMinutes", loadGroup.getRampOutMinutes(), -99999, 99999,
                true);
        }
    }
}
