package com.cannontech.web.api.dr.setup;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;

import com.cannontech.api.error.model.ApiErrorDetails;
import com.cannontech.common.dr.setup.LMCopy;
import com.cannontech.common.dr.setup.LoadGroupCopy;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.PaoUtils;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.common.validator.YukonValidationUtilsCommon;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.yukon.IDatabaseCache;

/**
 * Helper class for LM validation
 */
public class LMValidatorHelper {
    private final static String key = "yukon.web.modules.dr.setup.error.";
    @Autowired private PaoDao paoDao;
    @Autowired private IDatabaseCache serverDatabaseCache;
    public void checkIfFieldRequired(String field, Errors errors, Object fieldValue, String fieldName) {
        if (YukonValidationUtilsCommon.checkIfFieldRequired(fieldValue)) {
            errors.rejectValue(field, key + "required", new Object[] { fieldName }, "");
        }
    }

    // Type
    public void checkIfEmptyPaoType(Errors errors) {
        YukonValidationUtils.rejectIfEmptyOrWhitespace(errors, "type", key + "required", new Object[] { "Type" });
    }

    public void validateName(String paoName, Errors errors, String fieldName) {
        checkIfFieldRequired("name", errors, paoName, fieldName);
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

    /**
     * Checks whether the Pao name is unique or not
     */
    private void validateUniquePaoName(String paoName, PaoType type, Errors errors, String fieldName) {
        LiteYukonPAObject unique = paoDao.findUnique(paoName, type);
        if (unique != null) {
            errors.rejectValue("name", ApiErrorDetails.ALREADY_EXISTS.getCodeString() , new Object[] {fieldName}, "");
        }
    }

    public void validateRoute(Errors errors, Integer routeId) {
        checkIfFieldRequired("routeId", errors, routeId, "Route Id");
        if (!errors.hasFieldErrors("routeId")) {
            LiteYukonPAObject liteRoute = serverDatabaseCache.getAllRoutesMap().get(routeId);
            if (liteRoute == null) {
                errors.rejectValue("routeId", key + "routeId.doesNotExist");
            }
        }
    }

    /**
     * Find duplicate entries from list and returns set of entries which are duplicate.
     */
    public Set<Integer> findDuplicates(List<Integer> list) {
        return list.stream().filter(e -> Collections.frequency(list, e) >1).collect(Collectors.toSet());
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
