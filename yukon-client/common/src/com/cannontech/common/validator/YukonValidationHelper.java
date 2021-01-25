package com.cannontech.common.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;

import com.cannontech.api.error.model.ApiErrorDetails;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.PaoUtils;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.yukon.IDatabaseCache;

public class YukonValidationHelper {

    @Autowired private IDatabaseCache serverDatabaseCache;
    @Autowired private YukonUserContextMessageSourceResolver messageResolver;
    @Autowired private YukonValidationHelperCommon yukonValidationHelperCommon;
    private final static String key = "yukon.web.error.";
    
    public void validatePaoName(String paoName, PaoType type, Errors errors, String fieldName, String paoId) {
        if (StringUtils.hasText(paoName)) {
            String paoNameWithoutSpace = paoName.trim();
            YukonValidationUtils.checkExceedsMaxLength(errors, "name", paoNameWithoutSpace, 60);
            if (!PaoUtils.isValidPaoName(paoNameWithoutSpace)) {
                errors.rejectValue("name", ApiErrorDetails.ILLEGAL_CHARACTERS.getCodeString(), new Object[] { fieldName }, "");
            }

            if (!errors.hasFieldErrors("name") && yukonValidationHelperCommon.isPaoNameConflict(paoName, type, errors, paoId)) {
                errors.rejectValue("name", ApiErrorDetails.ALREADY_EXISTS.getCodeString(), new Object[] { fieldName }, "");
            }
        } else {
            errors.rejectValue("name", ApiErrorDetails.FIELD_REQUIRED.getCodeString(), new Object[] { "Name" }, "");
        }
    }

    /**
     * Check if paoType is matched with the poaObject present in the cache for paoId.
     */
    public void checkIfPaoTypeChanged(Errors errors, PaoType paoType, int paoId) {
        LiteYukonPAObject litePao = serverDatabaseCache.getAllPaosMap().get(paoId);
        if (yukonValidationHelperCommon.checkIfPaoTypeChanged(paoType, paoId)) {
            errors.rejectValue("type", ApiErrorDetails.POINT_TYPE_MISMATCH.getCodeString(),
                    new Object[] { paoType, litePao.getPaoType(), String.valueOf(paoId) }, "");
        }
    }

    public String getMessage(String key) {
        MessageSourceAccessor messageSourceAccessor = messageResolver.getMessageSourceAccessor(YukonUserContext.system);
        String message = messageSourceAccessor.getMessage(key);
        return message;
    }
}