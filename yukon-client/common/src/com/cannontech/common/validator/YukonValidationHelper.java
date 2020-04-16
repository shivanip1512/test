package com.cannontech.common.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;

import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.PaoUtils;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.stars.util.ServletUtils;

public class YukonValidationHelper {

    @Autowired private PaoDao paoDao;

    public void validatePaoName(String paoName, PaoType type, Errors errors, String fieldName, String pathVariable) {
        YukonValidationUtils.checkExceedsMaxLength(errors, "name", paoName, 60);
        if (!PaoUtils.isValidPaoName(paoName)) {
            errors.rejectValue("name", "yukon.web.error.paoName.containsIllegalChars");
        }

        if (!errors.hasFieldErrors("name")) {
            String paoId = ServletUtils.getPathVariable(pathVariable);
            // Check if pao name already exists
            if (type != null && (paoId == null || !(paoDao.getYukonPAOName(Integer.valueOf(paoId)).equalsIgnoreCase(paoName)))) {
                validateUniquePaoName(paoName, type, errors, fieldName);
            }
        }
    }

    /**
     * Checks whether the Pao name is unique or not
     */
    private void validateUniquePaoName(String paoName, PaoType type, Errors errors, String fieldName) {
        LiteYukonPAObject unique = paoDao.findUnique(paoName, type);
        if (unique != null) {
            errors.rejectValue("name", "yukon.web.api.error.unique", new Object[] { fieldName }, "");
        }
    }

}
