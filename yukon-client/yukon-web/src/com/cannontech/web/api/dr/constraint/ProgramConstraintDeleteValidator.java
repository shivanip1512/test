package com.cannontech.web.api.dr.constraint;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;

import com.cannontech.common.dr.setup.LMDelete;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.core.dao.DBDeletionDao;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.database.data.lite.LiteLMConstraint;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.web.api.dr.setup.LMValidatorHelper;
import com.cannontech.yukon.IDatabaseCache;

public class ProgramConstraintDeleteValidator extends SimpleValidator<LMDelete> {

    @Autowired DBDeletionDao dbDeletionDao;
    @Autowired private IDatabaseCache dbCache;
    @Autowired private LMValidatorHelper lmValidatorHelper;

    public ProgramConstraintDeleteValidator() {
        super(LMDelete.class);
    }

    @Override
    protected void doValidation(LMDelete lmDelete, Errors errors) {

        String constraintId = ServletUtils.getPathVariable("id");
        Optional<LiteLMConstraint> liteLMConstraint = dbCache.getAllLMProgramConstraints()
                .stream()
                .filter(constraint -> constraint.getConstraintID() == Integer.parseInt(constraintId))
                .findFirst();
        if (liteLMConstraint.isEmpty()) {
            throw new NotFoundException("Constraint Id not found");
        }
        lmValidatorHelper.checkIfFieldRequired("name", errors, lmDelete.getName(), "Constraint Name");
    }

}
