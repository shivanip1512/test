package com.cannontech.web.api.customAttribute;

import org.springframework.validation.Errors;

import com.cannontech.common.pao.attribute.model.Assignment;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonApiValidationUtils;
import com.cannontech.database.data.point.PointType;

public class CustomAttributeAssignmentCreationApiValidator extends SimpleValidator<Assignment> {

    public CustomAttributeAssignmentCreationApiValidator() {
        super(Assignment.class);
    }

    @Override
    protected void doValidation(Assignment assignment, Errors errors) {
        YukonApiValidationUtils.checkIfFieldRequired("attributeId", errors, assignment.getAttributeId(), "AttributeId");
        YukonApiValidationUtils.checkIfFieldRequired("paoType", errors, assignment.getPaoType(), "PaoType");
        YukonApiValidationUtils.checkIfFieldRequired("offset", errors, assignment.getOffset(), "Offset");
        YukonApiValidationUtils.checkIfFieldRequired("pointType", errors, assignment.getPointType(), "PointType");

        YukonApiValidationUtils.checkIsFieldValueGreaterThenTargetValueInt("attributeId", assignment.getAttributeId(), 0, errors);

        if (assignment.getPointType() == PointType.CalcAnalog || assignment.getPointType() == PointType.CalcStatus) {
            YukonApiValidationUtils.checkIsPositiveInt(errors, "offset", assignment.getOffset());
        } else {
            YukonApiValidationUtils.checkIsFieldValueGreaterThenTargetValueInt("offset", assignment.getOffset(), 0, errors);
        }

    }

}
