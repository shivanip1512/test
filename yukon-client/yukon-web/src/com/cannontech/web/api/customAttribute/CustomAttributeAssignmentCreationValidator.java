package com.cannontech.web.api.customAttribute;

import org.springframework.validation.Errors;

import com.cannontech.common.pao.attribute.model.Assignment;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.database.data.point.PointType;

public class CustomAttributeAssignmentCreationValidator extends SimpleValidator<Assignment>{
    
    public CustomAttributeAssignmentCreationValidator() {
        super(Assignment.class);
    }

    @Override
    protected void doValidation(Assignment assignment, Errors errors) {
        YukonValidationUtils.checkIfFieldRequired("attributeId", errors, assignment.getAttributeId(), "AttributeId");
        YukonValidationUtils.checkIfFieldRequired("paoType", errors, assignment.getPaoType(), "PaoType");
        YukonValidationUtils.checkIfFieldRequired("offset", errors, assignment.getOffset(), "Offset");
        YukonValidationUtils.checkIfFieldRequired("pointType", errors, assignment.getPointType(), "PointType");
        
        YukonValidationUtils.checkIsFieldValueGreaterThenTargetValueInt("attributeId", assignment.getAttributeId(), 0, errors);
        
        if(assignment.getPointType() == PointType.CalcAnalog || assignment.getPointType() == PointType.CalcStatus) {
            YukonValidationUtils.checkIsPositiveInt(errors, "offset", assignment.getOffset());
        } else {
            YukonValidationUtils.checkIsFieldValueGreaterThenTargetValueInt("offset", assignment.getOffset(), 0, errors);
        }
        
    }
    
}
