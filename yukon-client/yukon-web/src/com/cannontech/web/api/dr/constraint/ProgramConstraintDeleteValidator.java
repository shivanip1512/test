package com.cannontech.web.api.dr.constraint;

import java.sql.SQLException;
import java.util.Optional;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;

import com.cannontech.clientutils.YukonLogManager;
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
    private static final Logger log = YukonLogManager.getLogger(ProgramConstraintDeleteValidator.class);

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
            errors.reject("Constraint Id not found");
        }
        LMProgramConstraint constraint = (LMProgramConstraint) LiteFactory.createDBPersistent(liteLMConstraint.get());
        DBDeleteResult dbDeleteResult = dbDeletionDao.getDeleteInfo(constraint, constraint.getConstraintName());
        try {
            if (LMProgramConstraint.inUseByProgram(dbDeleteResult.getItemID(), CtiUtilities.getDatabaseAlias())) {
                if (dbDeleteResult.isDeletable()) {
                    errors.reject(dbDeleteResult.getUnableDelMsg().toString().concat(" because it is in use by a Program."));
                }
            }
        } catch (SQLException e) {
            errors.reject("Unable to delete Constraint");
            log.error("Unable to delete Constraint with naame : " + constraint.getConstraintName() + e);
        }

        lmValidatorHelper.checkIfFieldRequired("name", errors, lmDelete.getName(), "Constraint Name");
    }


}
