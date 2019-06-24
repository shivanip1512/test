package com.cannontech.web.api.dr.setup;

import org.springframework.validation.Errors;

import com.cannontech.common.dr.setup.LMDelete;
import com.cannontech.common.pao.PaoUtils;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonValidationUtils;

public class LMDeleteValidator extends SimpleValidator<LMDelete> {

    private final static String key = "yukon.web.modules.dr.setup.loadGroup.error.";

    public LMDeleteValidator() {
        super(LMDelete.class);
    }

    @Override
    protected void doValidation(LMDelete loadGroup, Errors errors) {

        // Group Name
        YukonValidationUtils.rejectIfEmptyOrWhitespace(errors, "name", key + "required", new Object[] { "Group Name" });
        if (!errors.hasFieldErrors("name")) {
            YukonValidationUtils.checkExceedsMaxLength(errors, "name", loadGroup.getName(), 60);
        }

        if (!errors.hasFieldErrors("name")) {
            if (!PaoUtils.isValidPaoName(loadGroup.getName())) {
                errors.rejectValue("name", "yukon.web.error.paoName.containsIllegalChars");
            }
        }
    }
}
