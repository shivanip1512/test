package com.cannontech.common.validator;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;

import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.PaoUtils;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.yukon.IDatabaseCache;

public class YukonValidationHelper {

    @Autowired private IDatabaseCache serverDatabaseCache;
    private final static String key = "yukon.web.error.";
    
    public void validatePaoName(String paoName, PaoType type, Errors errors, String fieldName, String paoId) {
        if (StringUtils.hasText(paoName)) {
            String paoNameWithoutSpace = paoName.trim();
            YukonValidationUtils.checkExceedsMaxLength(errors, "name", paoNameWithoutSpace, 60);
            if (!PaoUtils.isValidPaoName(paoNameWithoutSpace)) {
                errors.rejectValue("name", key + "paoName.containsIllegalChars");
            }

            if (!errors.hasFieldErrors("name")) {
                // Check if pao name already exists for paoClass and paoCategory
                PaoType paoType = (type == null && paoId != null) ? serverDatabaseCache.getAllPaosMap().get(Integer.valueOf(paoId)).getPaoType() : type;
                Optional<LiteYukonPAObject> litePao = serverDatabaseCache.getAllYukonPAObjects()
                                                                         .stream()
                                                                         .filter(pao -> pao.getPaoName().equalsIgnoreCase(paoNameWithoutSpace) 
                                                                                  && pao.getPaoType().getPaoClass() == paoType.getPaoClass()
                                                                                  && pao.getPaoType().getPaoCategory() == paoType.getPaoCategory())
                                                                         .findFirst();

                if (!litePao.isEmpty()) {
                    if (paoId == null || (litePao.get().getLiteID() != Integer.valueOf(paoId))) {
                        errors.rejectValue("name", key + "nameConflict", new Object[] { fieldName }, "");
                    }
                }
            }
        } else {
            errors.rejectValue("name", key + "fieldrequired", new Object[] { "Name" }, "");
        }
    }

    /**
     * Check if paoType is matched with the poaObject present in the cache for paoId.
     */
    public void checkIfPaoTypeChanged(Errors errors, PaoType paoType, int paoId) {
        LiteYukonPAObject litePao = serverDatabaseCache.getAllPaosMap().get(paoId);
        if (litePao != null && litePao.getPaoType() != paoType) {
            errors.rejectValue("type", key + "paoTypeMismatch",
                    new Object[] { paoType, litePao.getPaoType(), String.valueOf(paoId) }, "");
        }
    }
}
