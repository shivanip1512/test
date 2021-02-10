package com.cannontech.web.api.customAttribute;

import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;

import com.cannontech.common.pao.attribute.model.Assignment;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonApiValidationUtils;
import com.cannontech.stars.util.ServletUtils;

@Service
public class CustomAttributeAssignmentApiValidator extends SimpleValidator<Assignment> {

    public CustomAttributeAssignmentApiValidator() {
        super(Assignment.class);
    }

    @Override
    protected void doValidation(Assignment assignment, Errors errors) {

        String strattributeAssignmentId = ServletUtils.getPathVariable("attributeAssignmentId");
        Integer attributeAssignmentId = strattributeAssignmentId == null ? null : Integer.valueOf(strattributeAssignmentId);

        YukonApiValidationUtils.checkIsPositiveInt(errors, "attributeAssignmentId", attributeAssignmentId);
    }

}
