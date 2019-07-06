package com.cannontech.web.api.dr.setup;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;

import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.PaoUtils;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.stars.util.ServletUtils;

/**
 * Helper class for LM validation
 */
public class LMValidatorHelper {
    private final static String key = "yukon.web.modules.dr.setup.error.";
    @Autowired private PaoDao paoDao;

    /**
     * Checks whether the Name is empty or not
     */
    public void checkIfEmptyOrWhitespaceName(String field, Errors errors, String fieldName) {
        YukonValidationUtils.rejectIfEmptyOrWhitespace(errors, field, key + "required", new Object[] {fieldName});
    }

    // Type
    public void checkIfEmptyPaoType(Errors errors) {
        YukonValidationUtils.rejectIfEmptyOrWhitespace(errors, "type", key + "required", new Object[] { "Type" });
    }

    public void validateName(String paoName, Errors errors, String fieldName) {
        checkIfEmptyOrWhitespaceName("name", errors, fieldName);
        if (!errors.hasFieldErrors("name")) {
            YukonValidationUtils.checkExceedsMaxLength(errors, "name", paoName, 60);
            if (!PaoUtils.isValidPaoName(paoName)) {
                errors.rejectValue("name", "yukon.web.error.paoName.containsIllegalChars");
            }
        }
    }

    public void validateNewPaoName(String paoName, PaoType type, Errors errors, String fieldName) {
        validateName(paoName, errors, fieldName);
        if (!errors.hasFieldErrors("name")) {
            String paoId = ServletUtils.getPathVariable("id");
            // Check if pao name already exists
            if (type != null && (paoId == null || !(StringUtils.equals(paoDao.getYukonPAOName(Integer.valueOf(paoId)), paoName)))) {
                validateUniquePaoName(paoName, type, errors, fieldName);
            }
        }
    }

    public void validateCopyPaoName(String paoName, Errors errors, String fieldName) {
        validateName(paoName, errors, fieldName);

        if (!errors.hasFieldErrors("name")) {
            Integer paoId = Integer.valueOf(ServletUtils.getPathVariable("id"));
            if (paoId != null) {
                validateUniquePaoName(paoName, paoDao.getLiteYukonPAO(paoId).getPaoType(), errors, fieldName);
            }
        }
    }

    /**
     * Checks whether the Pao name is unique or not
     */
    private void validateUniquePaoName(String paoName, PaoType type, Errors errors, String fieldName) {
        LiteYukonPAObject unique = paoDao.findUnique(paoName, type);
        if (unique != null) {
            errors.rejectValue("name", key + "unique", new Object[] {fieldName}, "");
        }
    }
}
