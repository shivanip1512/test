package com.cannontech.web.api.dr.setup;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;

import com.cannontech.common.dr.setup.LMCopy;
import com.cannontech.common.pao.PaoUtils;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.stars.util.ServletUtils;

public class LMCopyValidator extends SimpleValidator<LMCopy>{
    private final static String key = "yukon.web.modules.dr.setup.loadGroup.error.";
    @Autowired private PaoDao paoDao;

    public LMCopyValidator() {
        super(LMCopy.class);
    }

    @Override
    protected void doValidation(LMCopy loadGroup, Errors errors) {

        // Group Name
        YukonValidationUtils.rejectIfEmptyOrWhitespace(errors, "name", key + "required", new Object[] { "Group Name" });
        if (!errors.hasFieldErrors("name")) {
            YukonValidationUtils.checkExceedsMaxLength(errors, "name", loadGroup.getName(), 60);
            if (!PaoUtils.isValidPaoName(loadGroup.getName())) {
                errors.rejectValue("name", "yukon.web.error.paoName.containsIllegalChars");
            }
        }

        if (!errors.hasFieldErrors("name")) {
            Integer paoId = Integer.valueOf(ServletUtils.getPathVariable("id"));

            // Check if a load group with this name already exists
            if (StringUtils.equals(paoDao.getYukonPAOName(paoId), loadGroup.getName())) {
                LiteYukonPAObject unique =
                    paoDao.findUnique(loadGroup.getName(), paoDao.getLiteYukonPAO(paoId).getPaoType());
                if (unique != null) {
                    errors.rejectValue("name", key + "groupName.unique");
                }
            }
        }
    }
}
