package com.cannontech.common.validator;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;

import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.PaoUtils;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.yukon.IDatabaseCache;

public class YukonValidationHelper {

    @Autowired private IDatabaseCache serverDatabaseCache;

    public void validatePaoName(String paoName, PaoType type, Errors errors, String fieldName, String pathVariable) {
        if (!YukonValidationUtils.checkIsBlank(errors, "name", paoName, false)) {
            YukonValidationUtils.checkExceedsMaxLength(errors, "name", paoName, 60);
            if (!PaoUtils.isValidPaoName(paoName)) {
                errors.rejectValue("name", "yukon.web.error.paoName.containsIllegalChars");
            }

            if (!errors.hasFieldErrors("name")) {
                // Check if pao name already exists for paoClass and paoCategory
                String paoId = ServletUtils.getPathVariable(pathVariable);
                Optional<LiteYukonPAObject> litePao = serverDatabaseCache.getAllYukonPAObjects()
                        .stream()
                        .filter(pao -> pao.getPaoName().equalsIgnoreCase(paoName)
                                && pao.getPaoType().getPaoClass() == type.getPaoClass()
                                && pao.getPaoType().getPaoCategory() == type.getPaoCategory())
                        .findFirst();

                if (!litePao.isEmpty()) {
                    if (paoId == null || (litePao.get().getLiteID() != Integer.valueOf(paoId))) {
                        errors.rejectValue("name", "yukon.web.api.error.unique", new Object[] { fieldName }, "");
                    }
                }
            }
        }
    }
}
