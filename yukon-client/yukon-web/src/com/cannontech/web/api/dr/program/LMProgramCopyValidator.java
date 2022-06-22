package com.cannontech.web.api.dr.program;

import java.util.Set;
import java.util.stream.Collectors;

import org.apache.logging.log4j.core.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;

import com.cannontech.api.error.model.ApiErrorDetails;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.dr.program.setup.model.LoadProgramCopy;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonApiValidationUtils;
import com.cannontech.development.service.impl.DemandResponseSetupServiceImpl;
import com.cannontech.yukon.IDatabaseCache;

public class LMProgramCopyValidator extends SimpleValidator<LoadProgramCopy> {

    private static final Logger log = YukonLogManager.getLogger(LMProgramCopyValidator.class);

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
                log.info("Program constraint ID : {} Cached constraint IDs:{}", constraintId, constraintIds);
                if (!constraintIds.contains(constraintId)) {
                    errors.rejectValue("constraintId", ApiErrorDetails.DOES_NOT_EXISTS.getCodeString(),
                            new Object[] { constraintId }, "");
                    log.info("constraint ID not in cached constraint IDs");
                }
            }
        }
        log.info(errors);
    }
}