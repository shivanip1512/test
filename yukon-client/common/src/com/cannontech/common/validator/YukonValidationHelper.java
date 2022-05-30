package com.cannontech.common.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;

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
    
    public void validatePaoName(String paoName, PaoType type, Errors errors, String fieldName, String paoId, String modelObjectFieldName) {
        if (StringUtils.hasText(paoName)) {
            String paoNameWithoutSpace = paoName.trim();
            YukonValidationUtils.checkExceedsMaxLength(errors, modelObjectFieldName, paoNameWithoutSpace, 60);
            if (!PaoUtils.isValidPaoName(paoNameWithoutSpace)) {
                errors.rejectValue(modelObjectFieldName, key + "paoName.containsIllegalChars");
            }

            if (!errors.hasFieldErrors(modelObjectFieldName) && yukonValidationHelperCommon.isPaoNameConflict(paoName, type, errors, paoId)) {
                errors.rejectValue(modelObjectFieldName, key + "nameConflict", new Object[] { fieldName }, "");
            }
        } else {
            errors.rejectValue(modelObjectFieldName, key + "fieldrequired", new Object[] { "Name" }, "");
        }
    }

    /**
     * Check if paoType is matched with the poaObject present in the cache for paoId.
     */
    public void checkIfPaoTypeChanged(Errors errors, PaoType paoType, int paoId) {
        LiteYukonPAObject litePao = serverDatabaseCache.getAllPaosMap().get(paoId);
        if (yukonValidationHelperCommon.checkIfPaoTypeChanged(paoType, paoId)) {
            errors.rejectValue("deviceType", key + "paoTypeMismatch",
                    new Object[] { paoType, litePao.getPaoType(), String.valueOf(paoId) }, "");
        }
    }

    public String getMessage(String key) {
        MessageSourceAccessor messageSourceAccessor = messageResolver.getMessageSourceAccessor(YukonUserContext.system);
        String message = messageSourceAccessor.getMessage(key);
        return message;
    }
    
    public void checkIfFieldRequired(String field, Errors errors, Object fieldValue, String fieldName) {
        if (YukonValidationUtilsCommon.checkIfFieldRequired(fieldValue)) {
            errors.rejectValue(field, key + "fieldrequired", new Object[] { fieldName }, "");
        }
    }
    
    public void checkExceedsMaxLength(String field, Errors errors, String fieldValue, String fieldName, int maxLength) {
        if (YukonValidationUtilsCommon.checkExceedsMaxLength(fieldValue, maxLength)) {
            errors.rejectValue(field, key + "fieldrequired", new Object[] { fieldName }, String.valueOf(maxLength));
        }
    }
}