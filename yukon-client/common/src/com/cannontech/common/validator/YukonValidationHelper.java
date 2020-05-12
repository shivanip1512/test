package com.cannontech.common.validator;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;

import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.PaoUtils;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.yukon.IDatabaseCache;

public class YukonValidationHelper {

    @Autowired private IDatabaseCache serverDatabaseCache;
    private final static String key = "yukon.web.error.";

    public void validatePaoName(String paoName, PaoType type, Errors errors, String fieldName, String paoIdPathVariable) {
        if (!YukonValidationUtils.checkIsBlank(errors, "name", paoName, false)) {
            YukonValidationUtils.checkExceedsMaxLength(errors, "name", paoName, 60);
            if (!PaoUtils.isValidPaoName(paoName)) {
                errors.rejectValue("name", key + "paoName.containsIllegalChars");
            }

            if (!errors.hasFieldErrors("name")) {
                // Check if pao name already exists for paoClass and paoCategory
                String paoId = ServletUtils.getPathVariable(paoIdPathVariable);
                Optional<LiteYukonPAObject> litePao = serverDatabaseCache.getAllYukonPAObjects()
                                                                         .stream()
                                                                         .filter(pao -> pao.getPaoName().equalsIgnoreCase(paoName) 
                                                                                  && pao.getPaoType().getPaoClass() == type.getPaoClass()
                                                                                  && pao.getPaoType().getPaoCategory() == type.getPaoCategory())
                                                                         .findFirst();

                if (!litePao.isEmpty()) {
                    if (paoId == null || (litePao.get().getLiteID() != Integer.valueOf(paoId))) {
                        errors.rejectValue("name", key + "nameConflict", new Object[] { fieldName }, "");
                    }
                }
            }
        }
    }

    /**
     * Check if paoType is matched with the poaObject present in the cache for paoId.
    */
    public void checkIfPaoTypeChanged(Errors errors, PaoType paoType, int paoId) {
        LiteYukonPAObject litePao = serverDatabaseCache.getAllPaosMap().get(paoId);
        if (litePao != null && litePao.getPaoType() != paoType) {
            errors.rejectValue("type", key + "paoTypeMismatch", new Object[] { paoType, litePao.getPaoType(), paoId }, "");
        }
    }
    
    /** 
     * Check if the field is required
     */
    public void checkIfFieldRequired(String field, Errors errors, Object fieldValue, String fieldName) {
        if (fieldValue == null || !StringUtils.hasText(fieldValue.toString())) {
            errors.rejectValue(field, key + "field.required", new Object[] { fieldName }, "");
        }
    }

}
