package com.cannontech.web.api.dr.setup;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;

import com.cannontech.common.dr.setup.LMCopy;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.stars.util.ServletUtils;

public class LMCopyValidator extends SimpleValidator<LMCopy>{
    @Autowired private LoadGroupValidatorHelper loadGroupValidatorHelper;
    @Autowired private PaoDao paoDao;

    public LMCopyValidator() {
        super(LMCopy.class);
    }

    @Override
    protected void doValidation(LMCopy loadGroup, Errors errors) {

        // Group Name
        loadGroupValidatorHelper.validateGroupName(loadGroup.getName(), errors);

        if (!errors.hasFieldErrors("name")) {
            Integer paoId = Integer.valueOf(ServletUtils.getPathVariable("id"));

            // Check if a load group with this name already exists
            if (paoId != null) {
                loadGroupValidatorHelper.validateUniqueGroupName(loadGroup.getName(),
                    paoDao.getLiteYukonPAO(paoId).getPaoType(), errors);
            }
        }
    }
}
