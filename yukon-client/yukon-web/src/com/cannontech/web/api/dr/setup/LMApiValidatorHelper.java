package com.cannontech.web.api.dr.setup;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;

import com.cannontech.api.error.model.ApiErrorDetails;
import com.cannontech.common.dr.setup.LMCopy;
import com.cannontech.common.dr.setup.LoadGroupCopy;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.yukon.IDatabaseCache;

/**
 * Helper class for LM API validation
 */
public class LMApiValidatorHelper {
    @Autowired private PaoDao paoDao;
    @Autowired private IDatabaseCache serverDatabaseCache;
    @Autowired private LMValidatorHelperCommon lmValidatorHelperCommon;

    public void checkIfFieldRequired(String field, Errors errors, Object fieldValue, String fieldName) {
        if (lmValidatorHelperCommon.checkIfFieldRequired(fieldValue)) {
            errors.rejectValue(field, ApiErrorDetails.FIELD_REQUIRED.getCodeString(), new Object[] { fieldName }, "");
        }
    }

    public void validateName(String paoName, Errors errors, String fieldName) {
        checkIfFieldRequired("name", errors, paoName, fieldName);
        if (!errors.hasFieldErrors("name")) {
            YukonValidationUtils.checkExceedsMaxLength(errors, "name", paoName, 60);
            if (!lmValidatorHelperCommon.isValidPaoName(paoName)) {
                errors.rejectValue("name", ApiErrorDetails.ILLEGAL_CHARACTERS.getCodeString(), new Object[] { fieldName }, "");
            }
        }
    }

    /**
     * Checks whether the Pao name is unique or not
     */
    private void validateUniquePaoName(String paoName, PaoType type, Errors errors, String fieldName) {
        if (lmValidatorHelperCommon.validateUniquePaoName(paoName, type)) {
            errors.rejectValue("name", ApiErrorDetails.ALREADY_EXISTS.getCodeString(), new Object[] { fieldName },
                    "");
        }
    }

    public void validateNewPaoName(String paoName, PaoType type, Errors errors, String fieldName) {
        validateName(paoName, errors, fieldName);
        if (!errors.hasFieldErrors("name")) {
            String paoId = ServletUtils.getPathVariable("id");
            // Check if pao name already exists
            if (type != null && (paoId == null || !(paoDao.getYukonPAOName(Integer.valueOf(paoId)).equalsIgnoreCase(paoName)))) {
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

    public void validateRoute(Errors errors, Integer routeId) {
        checkIfFieldRequired("routeId", errors, routeId, "Route Id");
        if (!errors.hasFieldErrors("routeId")) {
            if (lmValidatorHelperCommon.validateRoute(routeId)) {
                errors.rejectValue("routeId", ApiErrorDetails.DOES_NOT_EXISTS.getCodeString(), new Object[] { routeId }, "");
            }
        }
    }

    /**
     * Validates route id if load group supports route id
     */
    public void validateRouteId(LMCopy lmCopy, Errors errors, String field) {
        Integer paoId = Integer.valueOf(ServletUtils.getPathVariable("id"));
        if (paoId != null) {
            PaoType type = serverDatabaseCache.getAllPaosMap().get(paoId).getPaoType();
            if (lmCopy instanceof LoadGroupCopy && type.isLoadGroupSupportRoute()) {
                Integer routeId = ((LoadGroupCopy) lmCopy).getRouteId();
                if (routeId != null) {
                    validateRoute(errors, routeId);
                }
            }
        }
    }

}
