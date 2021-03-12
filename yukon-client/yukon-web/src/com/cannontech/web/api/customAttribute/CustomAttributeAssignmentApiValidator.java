package com.cannontech.web.api.customAttribute;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;

import com.cannontech.api.error.model.ApiErrorDetails;
import com.cannontech.common.pao.attribute.model.Assignment;
import com.cannontech.common.pao.attribute.service.AttributeServiceImpl;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonApiValidationUtils;
import com.cannontech.stars.util.ServletUtils;

@Service
public class CustomAttributeAssignmentApiValidator extends SimpleValidator<Assignment> {

    @Autowired private AttributeServiceImpl attributeService;

    public CustomAttributeAssignmentApiValidator() {
        super(Assignment.class);
    }

    @Override
    protected void doValidation(Assignment assignment, Errors errors) {

        String strattributeAssignmentId = ServletUtils.getPathVariable("attributeAssignmentId");
        Integer attributeAssignmentId = strattributeAssignmentId == null ? null : Integer.valueOf(strattributeAssignmentId);

        YukonApiValidationUtils.checkIsPositiveInt(errors, "attributeAssignmentId", attributeAssignmentId);
        if (!attributeService.isValidAttributeId(assignment.getAttributeId())) {
            errors.rejectValue("attributeId", ApiErrorDetails.DOES_NOT_EXISTS.getCodeString(),
                    new Object[] { assignment.getAttributeId() }, "");
        }
    }

}
