package com.cannontech.web.api.dr.program;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;

import com.cannontech.common.dr.program.setup.model.LoadProgram;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.web.api.dr.setup.LMValidatorHelper;
import com.cannontech.yukon.IDatabaseCache;

public class LMProgramValidator extends SimpleValidator<LoadProgram> {

    private final static String key = "yukon.web.modules.dr.setup.loadProgram.error.";

    @Autowired private LMValidatorHelper lmValidatorHelper;
    @Autowired private IDatabaseCache serverDatabaseCache;

    public LMProgramValidator() {
        super(LoadProgram.class);
    }

    @Override
    protected void doValidation(LoadProgram loadProgram, Errors errors) {
        lmValidatorHelper.validateCopyPaoName(loadProgram.getName(), errors, "Program Name");
        //Type
        lmValidatorHelper.checkIfFieldRequired("type", errors, loadProgram.getType(), "Type");
        lmValidatorHelper.checkIfFieldRequired("operationalState", errors, loadProgram.getOperationalState(), "Operational State");
        
        lmValidatorHelper.checkIfFieldRequired("constraint", errors, loadProgram.getConstraint(), "Program Constraint");

        if (!errors.hasFieldErrors("constraint")) {
            Integer constraintId = loadProgram.getConstraint().getConstraintId();
            lmValidatorHelper.checkIfFieldRequired("constraintId", errors, constraintId, "ConstraintId");
            if (!errors.hasFieldErrors("constraintId")) {
                Set<Integer> constraintIds = serverDatabaseCache.getAllLMProgramConstraints().stream()
                                                                                             .map(lmConstraint -> lmConstraint.getConstraintID())
                                                                                             .collect(Collectors.toSet());
                if (!constraintIds.contains(constraintId)) {
                    errors.rejectValue("constraintId", key + "constraintId.doesNotExist");
                }
            }
        }
        
        loadProgram.getRestoreOffset();
        
        

    }

}
