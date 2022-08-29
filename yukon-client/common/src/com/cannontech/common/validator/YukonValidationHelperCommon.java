package com.cannontech.common.validator;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;

import com.cannontech.common.pao.PaoType;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.yukon.IDatabaseCache;

public class YukonValidationHelperCommon {

    @Autowired private IDatabaseCache serverDatabaseCache;

    public boolean isPaoNameConflict(String paoName, PaoType type, Errors errors, String paoId) {
        String paoNameWithoutSpace = paoName.trim();
        if (!errors.hasFieldErrors("deviceName")) {
            // Check if pao name already exists for paoClass and paoCategory
            PaoType paoType = (type == null && paoId != null) ? serverDatabaseCache.getAllPaosMap()
                    .get(Integer.valueOf(paoId)).getPaoType() : type;
            if (paoType != null) {
                Optional<LiteYukonPAObject> litePao = serverDatabaseCache.getAllYukonPAObjects()
                        .stream()
                        .filter(pao -> pao.getPaoName().equalsIgnoreCase(paoNameWithoutSpace)
                                && pao.getPaoType().getPaoClass() == paoType.getPaoClass()
                                && pao.getPaoType().getPaoCategory() == paoType.getPaoCategory())
                        .findFirst();

                if (!litePao.isEmpty()) {
                    if (paoId == null || (litePao.get().getLiteID() != Integer.valueOf(paoId))) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Check if paoType is matched with the poaObject present in the cache for paoId.
     */
    public boolean checkIfPaoTypeChanged(PaoType paoType, int paoId) {
        LiteYukonPAObject litePao = serverDatabaseCache.getAllPaosMap().get(paoId);
        return (litePao != null && litePao.getPaoType() != paoType) ? true : false;
    }
}
