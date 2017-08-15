package com.cannontech.web.amr.macsscheduler;

import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;

import com.cannontech.amr.macsscheduler.model.MacsSchedule;
import com.cannontech.amr.macsscheduler.model.MacsStartPolicy;
import com.cannontech.common.pao.PaoUtils;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonValidationUtils;

@Service
public class MacsScheduleValidator extends SimpleValidator<MacsSchedule> {

    public MacsScheduleValidator() {
        super(MacsSchedule.class);
    }

    @Override
    public void doValidation(MacsSchedule schedule, Errors errors) {

        validateName(schedule, errors);
        YukonValidationUtils.rejectIfEmptyOrWhitespace(errors, "categoryName", "yukon.web.error.isBlank");
        YukonValidationUtils.checkExceedsMaxLength(errors, "categoryName", schedule.getCategoryName(), 50);
        if (schedule.getStartPolicy().getPolicy() == MacsStartPolicy.StartPolicy.DAYOFMONTH) {
            YukonValidationUtils.checkRange(errors, "startPolicy.dayOfMonth", schedule.getStartPolicy().getDayOfMonth(), 1, 31, true);
        }
    }

    private void validateName(MacsSchedule schedule, Errors errors) {
        YukonValidationUtils.rejectIfEmptyOrWhitespace(errors, "scheduleName", "yukon.web.error.isBlank");

        if (!errors.hasFieldErrors("scheduleName")) {
            if (!PaoUtils.isValidPaoName(schedule.getScheduleName())) {
                errors.rejectValue("scheduleName", "yukon.web.error.paoName.containsIllegalChars");
            }
        }
        YukonValidationUtils.checkExceedsMaxLength(errors, "scheduleName", schedule.getScheduleName(), 60);
    }
}
