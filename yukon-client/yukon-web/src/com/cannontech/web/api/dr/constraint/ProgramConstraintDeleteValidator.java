package com.cannontech.web.api.dr.constraint;

import java.sql.SQLException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;

import com.cannontech.common.dr.setup.LMDelete;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.core.dao.DBDeleteResult;
import com.cannontech.core.dao.DBDeletionDao;
import com.cannontech.database.data.lite.LiteFactory;
import com.cannontech.database.data.lite.LiteLMConstraint;
import com.cannontech.database.db.device.lm.LMProgramConstraint;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.web.api.dr.setup.LMValidatorHelper;
import com.cannontech.yukon.IDatabaseCache;

public class ProgramConstraintDeleteValidator extends SimpleValidator<LMDelete>{

    @Autowired DBDeletionDao dbDeletionDao;
    @Autowired private IDatabaseCache dbCache;
    @Autowired private LMValidatorHelper lmValidatorHelper;

    public ProgramConstraintDeleteValidator() {
        super(LMDelete.class);
    }

    @Override
    protected void doValidation(LMDelete lmDelete, Errors errors) {
        
        String constraintId = ServletUtils.getPathVariable("id");
        Optional<LiteLMConstraint> liteLMConstraint = 
                dbCache.getAllLMProgramConstraints().stream()
                .filter(constraint -> constraint.getConstraintID() == Integer.parseInt(constraintId))
                .findFirst();
        if (liteLMConstraint.isEmpty()) {
            errors.reject("Constarint Id not found");
        }
        LMProgramConstraint constraint = (LMProgramConstraint) LiteFactory.createDBPersistent(liteLMConstraint.get());
        DBDeleteResult dbDeleteResult = dbDeletionDao.getDeleteInfo(constraint, lmDelete.getName());
        try {
            if (LMProgramConstraint.inUseByProgram(dbDeleteResult.getItemID(), CtiUtilities.getDatabaseAlias())) {
                if (dbDeleteResult.isDeletable()) {
                    errors.reject(dbDeleteResult.getUnableDelMsg().toString().concat(" because it is in use by a Program."));
                }
            }
        } catch (SQLException e) {
            errors.reject("Unable to delete Constraint");
        }

        lmValidatorHelper.checkIfFieldRequired("name", errors, lmDelete.getName(), "Constraint Name");
    }


}
