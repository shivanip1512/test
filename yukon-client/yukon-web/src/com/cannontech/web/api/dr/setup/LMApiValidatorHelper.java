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
import com.cannontech.common.validator.YukonApiValidationUtils;
import com.cannontech.common.validator.YukonValidationUtilsCommon;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.yukon.IDatabaseCache;

/**
 * Helper class for LM API validation
 */
public class LMApiValidatorHelper {
    @Autowired private IDatabaseCache serverDatabaseCache;

    /**
     * Checks whether the Pao name is unique or not
     */

    public void checkIfFieldRequired(String field, Errors errors, Object fieldValue, String fieldName) {
        if (YukonValidationUtilsCommon.checkIfFieldRequired(fieldValue)) {
            errors.rejectValue(field, ApiErrorDetails.FIELD_REQUIRED.getCodeString(), new Object[] { fieldName }, "");
        }
    }

    public void validateName(String paoName, Errors errors, String fieldName) {
        checkIfFieldRequired("name", errors, paoName, fieldName);
        if (!errors.hasFieldErrors("name")) {
            YukonApiValidationUtils.checkExceedsMaxLength(errors, "name", paoName, 60);
            if (!PaoUtils.isValidPaoName(paoName)) {
                errors.rejectValue("name", ApiErrorDetails.ILLEGAL_CHARACTERS.getCodeString(), new Object[] { fieldName }, "");
            }
        }
    }

    public void validateRoute(Errors errors, Integer routeId) {
        YukonApiValidationUtils.checkIfFieldRequired("routeId", errors, routeId, "Route Id");
        if (!errors.hasFieldErrors("routeId")) {
            LiteYukonPAObject liteRoute = serverDatabaseCache.getAllRoutesMap().get(routeId);
            if (liteRoute == null) {
                errors.rejectValue("routeId", ApiErrorDetails.DOES_NOT_EXISTS.getCodeString(), new Object[] { "Route Id" }, "");
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
