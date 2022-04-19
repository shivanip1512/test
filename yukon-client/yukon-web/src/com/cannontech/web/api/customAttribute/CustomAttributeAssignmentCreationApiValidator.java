package com.cannontech.web.api.customAttribute;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;

import com.cannontech.api.error.model.ApiErrorDetails;
import com.cannontech.common.pao.attribute.model.Assignment;
import com.cannontech.common.pao.attribute.model.AttributeAssignment;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.common.pao.attribute.service.AttributeServiceImpl;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonApiValidationUtils;
import com.cannontech.database.data.point.PointType;

public class CustomAttributeAssignmentCreationApiValidator extends SimpleValidator<Assignment> {

    @Autowired private AttributeServiceImpl attributeService;
    @Autowired private YukonApiValidationUtils yukonApiValidationUtils;

    public CustomAttributeAssignmentCreationApiValidator() {
        super(Assignment.class);
    }

    @Override
    protected void doValidation(Assignment assignment, Errors errors) {
        yukonApiValidationUtils.checkIfFieldRequired("attributeId", errors, assignment.getAttributeId(), "AttributeId");
        yukonApiValidationUtils.checkIfFieldRequired("paoType", errors, assignment.getPaoType(), "PaoType");
        yukonApiValidationUtils.checkIfFieldRequired("offset", errors, assignment.getOffset(), "Offset");
        yukonApiValidationUtils.checkIfFieldRequired("pointType", errors, assignment.getPointType(), "PointType");

        yukonApiValidationUtils.checkIsFieldValueGreaterThenTargetValueInt("attributeId", assignment.getAttributeId(), 0, errors);

        int min = assignment.getPointType() == PointType.CalcAnalog || assignment.getPointType() == PointType.CalcStatus ? 0 : 1;
        yukonApiValidationUtils.checkRange(errors, "offset", assignment.getOffset(), min, 99999999, true);
        if (!attributeService.isValidAttributeId(assignment.getAttributeId())) {
            errors.rejectValue("attributeId", ApiErrorDetails.DOES_NOT_EXISTS.getCodeString(), new Object[] { assignment.getAttributeId() }, "");
        }
        Optional<AttributeAssignment> attributeAssignment = AttributeService.customAttributeAssignments.asMap()
                                                                            .values()
                                                                            .stream()
                                                                            .filter(attr -> attr.getAttributeId() == assignment.getAttributeId()
                                                                                    && attr.getPaoType() == assignment.getPaoType())
                                                                            .findFirst();
        if (attributeAssignment.isPresent()) {
            errors.rejectValue("attributeId", ApiErrorDetails.ALREADY_EXISTS.getCodeString(),
                    new Object[] { assignment.getAttributeId() }, "");
        }
    }

}
