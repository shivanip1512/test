package com.cannontech.web.api.customAttribute;

import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;

import com.cannontech.common.pao.attribute.model.Assignment;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.stars.util.ServletUtils;

@Service
public class CustomAttributeAssignmentValidator extends SimpleValidator<Assignment>{

    public CustomAttributeAssignmentValidator() {
        super(Assignment.class);
    }


    @Override
    protected void doValidation(Assignment assignment, Errors errors) {
        Integer attributeAssignmentId = Integer.valueOf(ServletUtils.getPathVariable("attributeAssignmentId"));
        
        YukonValidationUtils.checkIsPositiveInt(errors, "attributeAssignmentId", attributeAssignmentId);
        
    }

}
