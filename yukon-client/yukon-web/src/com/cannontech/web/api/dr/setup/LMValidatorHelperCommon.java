package com.cannontech.web.api.dr.setup;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.PaoUtils;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.yukon.IDatabaseCache;

/**
 * Common validation class for LM validation
 */
public class LMValidatorHelperCommon {
    @Autowired private PaoDao paoDao;
    @Autowired private IDatabaseCache serverDatabaseCache;

    public boolean checkIfFieldRequired(Object fieldValue) {
        return (fieldValue == null || !StringUtils.hasText(fieldValue.toString())) ? true : false; 
    }

    public boolean isValidPaoName(String paoName) {
        return PaoUtils.isValidPaoName(paoName) ? true : false;
    }

    /**
     * Checks whether the Pao name is unique or not
     */
    public boolean validateUniquePaoName(String paoName, PaoType type) {
        LiteYukonPAObject unique = paoDao.findUnique(paoName, type);
        return (unique != null) ? true : false;
    }

    public boolean validateRoute(Integer routeId) {
        LiteYukonPAObject liteRoute = serverDatabaseCache.getAllRoutesMap().get(routeId);
        return (liteRoute == null) ? true : false;
    }

}
