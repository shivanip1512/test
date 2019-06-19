package com.cannontech.web.api.dr.setup;

import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;

import com.cannontech.common.dr.setup.LoadGroupDigiSep;
import com.cannontech.common.validator.YukonValidationUtils;

@Service
public class LoadGroupDigiSepValidator extends LoadGroupSetupValidator<LoadGroupDigiSep> {

    private final static String key = "yukon.web.modules.dr.setup.loadGroup.error.";

    public LoadGroupDigiSepValidator() {
        super(LoadGroupDigiSep.class);
    }

    @Override
    public boolean supports(Class clazz) {
        return LoadGroupDigiSep.class.isAssignableFrom(clazz);
    }

    @Override
    protected void doValidation(LoadGroupDigiSep loadGroup, Errors errors) {
        YukonValidationUtils.rejectIfEmptyOrWhitespace(errors, "deviceClassSet", key + "deviceClassSet.required");

        // Utility Enrollment Group
        YukonValidationUtils.rejectIfEmptyOrWhitespace(errors, "utilityEnrollmentGroup",
            key + "utilityEnrollmentGroup.required");
        YukonValidationUtils.checkIsPositiveInt(errors, "utilityEnrollmentGroup", loadGroup.getUtilityEnrollmentGroup());

        if (loadGroup.getId() != null && loadGroup.getUtilityEnrollmentGroup() == 0) {
            errors.rejectValue("utilityEnrollmentGroup", key + "utilityEnrollmentGroup.rangeCheck");
        }
        if (!errors.hasFieldErrors("utilityEnrollmentGroup")) {
            YukonValidationUtils.checkRange(errors, "utilityEnrollmentGroup", loadGroup.getUtilityEnrollmentGroup(), 1,
                255, true);
        }

        // Ramp In Time
        YukonValidationUtils.rejectIfEmptyOrWhitespace(errors, "rampInMinutes", key + "rampInMinutes.required");
        YukonValidationUtils.checkRange(errors, "rampInMinutes", loadGroup.getRampInMinutes(), -99999, 99999, true);
        // Ramp Out Time
        YukonValidationUtils.rejectIfEmptyOrWhitespace(errors, "utilityEnrollmentGroup", key + "rampOutMinutes.required");
        YukonValidationUtils.checkRange(errors, "rampOutMinutes", loadGroup.getRampOutMinutes(), -99999, 99999, true);

    }
}
