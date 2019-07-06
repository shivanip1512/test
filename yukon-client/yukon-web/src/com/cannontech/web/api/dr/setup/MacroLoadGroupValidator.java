package com.cannontech.web.api.dr.setup;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;

import com.cannontech.common.dr.setup.MacroLoadGroup;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonValidationUtils;

@Service
public class MacroLoadGroupValidator extends SimpleValidator<MacroLoadGroup> {

    private final static String key = "yukon.web.modules.dr.setup.loadGroup.error.";

    @Autowired private LMValidatorHelper lmValidatorHelper;

    public MacroLoadGroupValidator() {
        super(MacroLoadGroup.class);
    }

    @Override
    protected void doValidation(MacroLoadGroup loadGroup, Errors errors) {

        lmValidatorHelper.checkIfEmptyPaoType(errors);
        lmValidatorHelper.validateNewPaoName(loadGroup.getName(), loadGroup.getType(), errors, "Group Name");
        ;

        if (!errors.hasFieldErrors("assignedLoadGroups")) {
            if (CollectionUtils.isEmpty(loadGroup.getAssignedLoadGroups())) {
                errors.rejectValue("assignedLoadGroups", key + "assignedLoadGroup.required");
            }
        }

        if (!errors.hasFieldErrors("type")) {
            if (loadGroup.getType() != PaoType.MACRO_GROUP) {
                errors.rejectValue("type", key + "type.invalid");
            }
        }

    }

}
