package com.cannontech.core.dao;

import java.util.List;

import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.LiteYukonUser;

public abstract class MspRawPointHistoryBaseDao {

    /**
     * Returns a list of paObjects for PaoTag.LM_PROGRAM that user has permission to access.
     * @return
     */
    public abstract List<LiteYukonPAObject> getAuthorizedProgramsList(LiteYukonUser user);
}
