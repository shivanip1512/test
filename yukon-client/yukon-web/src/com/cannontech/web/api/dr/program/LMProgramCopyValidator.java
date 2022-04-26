package com.cannontech.web.api.dr.program;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;

import com.cannontech.api.error.model.ApiErrorDetails;
import com.cannontech.common.dr.program.setup.model.LoadProgramCopy;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonApiValidationUtils;
import com.cannontech.yukon.IDatabaseCache;

public class LMProgramCopyValidator extends SimpleValidator<LoadProgramCopy> {

    @Autowired private IDatabaseCache serverDatabaseCache;
    @Autowired private YukonApiValidationUtils yukonApiValidationUtils;

    public LMProgramCopyValidator() {
        super(LoadProgramCopy.class);
    }

    @Override
    protected void doValidation(LoadProgramCopy loadProgramCopy, Errors errors) {
        // Name validation
        yukonApiValidationUtils.validateCopyPaoName(loadProgramCopy.getName(), errors, "Name");

        yukonApiValidationUtils.checkIfFieldRequired("operationalState", errors, loadProgramCopy.getOperationalState(),
                "Operational State");

        yukonApiValidationUtils.checkIfFieldRequired("constraint", errors, loadProgramCopy.getConstraint(), "Program Constraint");

        if (!errors.hasFieldErrors("constraint")) {
            Integer constraintId = loadProgramCopy.getConstraint().getConstraintId();
            yukonApiValidationUtils.checkIfFieldRequired("constraint.constraintId", errors, constraintId, "Constraint");
            if (!errors.hasFieldErrors("constraint.constraintId")) {
                Set<Integer> constraintIds = serverDatabaseCache.getAllLMProgramConstraints().stream()
                                                                                             .map(lmConstraint -> lmConstraint.getConstraintID())
                                                                                             .collect(Collectors.toSet());
                if (!constraintIds.contains(constraintId)) {
                    errors.rejectValue("constraintId", ApiErrorDetails.DOES_NOT_EXISTS.getCodeString(),
                            new Object[] { constraintId }, "");
                }
            }
        }

    }
}