package com.cannontech.web.api.dr.program;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;

import com.cannontech.common.dr.program.setup.model.LoadProgramCopy;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.web.api.dr.setup.LMValidatorHelper;
import com.cannontech.yukon.IDatabaseCache;

public class LMProgramCopyValidator extends SimpleValidator<LoadProgramCopy> {

    private final static String key = "yukon.web.modules.dr.setup.loadProgram.error.";

    @Autowired private LMValidatorHelper lmValidatorHelper;
    @Autowired private IDatabaseCache serverDatabaseCache;

    public LMProgramCopyValidator() {
        super(LoadProgramCopy.class);
    }

    @Override
    protected void doValidation(LoadProgramCopy loadProgramCopy, Errors errors) {
        //Name validation
        lmValidatorHelper.validateCopyPaoName(loadProgramCopy.getName(), errors, "Program Name");
        //Type
        lmValidatorHelper.checkIfFieldRequired("type", errors, loadProgramCopy.getType(), "Type");
        lmValidatorHelper.checkIfFieldRequired("operationalState", errors, loadProgramCopy.getOperationalState(), "Operational State");
        
        lmValidatorHelper.checkIfFieldRequired("constraint", errors, loadProgramCopy.getConstraint(), "Program Constraint");

        if (!errors.hasFieldErrors("constraint")) {
            Integer constraintId = loadProgramCopy.getConstraint().getConstraintId();
            lmValidatorHelper.checkIfFieldRequired("constraint.constraintId", errors, constraintId, "Constraint Id");
            if (!errors.hasFieldErrors("constraint.constraintId")) {
                Set<Integer> constraintIds = serverDatabaseCache.getAllLMProgramConstraints().stream()
                                                                                             .map(lmConstraint -> lmConstraint.getConstraintID())
                                                                                             .collect(Collectors.toSet());
                if (!constraintIds.contains(constraintId)) {
                    errors.rejectValue("constraint.constraintId", key + "constraintId.doesNotExist");
                }
            }
        }
        
    }
}
