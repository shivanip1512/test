package com.cannontech.web.api.dr.setup;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.Errors;

import com.cannontech.common.dr.setup.LoadGroupDigiSep;
import com.cannontech.common.validator.YukonValidationUtils;

@Service
public class LoadGroupDigiSepValidator extends LoadGroupSetupValidator<LoadGroupDigiSep> {

    private final static String key = "yukon.web.modules.dr.setup.loadGroup.error.";
    @Autowired private LMValidatorHelper lmValidatorHelper;

    public LoadGroupDigiSepValidator() {
        super(LoadGroupDigiSep.class);
    }

    @Override
    public boolean supports(Class clazz) {
        return LoadGroupDigiSep.class.isAssignableFrom(clazz);
    }

    @Override
    protected void doValidation(LoadGroupDigiSep loadGroup, Errors errors) {

        lmValidatorHelper.checkIfFieldRequired("deviceClassSet", errors, loadGroup.getDeviceClassSet(), "Device Class set");

        if (!errors.hasFieldErrors("deviceClassSet")) {
            if (CollectionUtils.isEmpty(loadGroup.getDeviceClassSet())) {
                errors.rejectValue("deviceClassSet", key + "deviceClassSet.required");
            }
        }

        // Utility Enrollment Group
        lmValidatorHelper.checkIfFieldRequired("utilityEnrollmentGroup", errors, loadGroup.getUtilityEnrollmentGroup(), "Utility Enrollment Group");

        if (!errors.hasFieldErrors("utilityEnrollmentGroup")) {
            if (loadGroup.getId() != null && loadGroup.getUtilityEnrollmentGroup() == 0) {
                errors.rejectValue("utilityEnrollmentGroup", key + "utilityEnrollmentGroup.rangeCheck");
            }
        }
        
        if (!errors.hasFieldErrors("utilityEnrollmentGroup")) {
            YukonValidationUtils.checkRange(errors, "utilityEnrollmentGroup", loadGroup.getUtilityEnrollmentGroup(), 1,
                255, true);
        }

        // Ramp In Time
        lmValidatorHelper.checkIfFieldRequired("rampInMinutes", errors, loadGroup.getRampInMinutes(), "Ramp In Minutes" );
        if (!errors.hasFieldErrors("rampInMinutes")) {
            YukonValidationUtils.checkRange(errors, "rampInMinutes", loadGroup.getRampInMinutes(), -99999, 99999, true);
        }

        // Ramp Out Time
        lmValidatorHelper.checkIfFieldRequired("rampOutMinutes", errors, loadGroup.getRampOutMinutes(), "Ramp Out Minutes" );
        if (!errors.hasFieldErrors("rampOutMinutes")) {
            YukonValidationUtils.checkRange(errors, "rampOutMinutes", loadGroup.getRampOutMinutes(), -99999, 99999,
                true);
        }
    }
}
