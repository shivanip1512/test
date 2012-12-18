package com.cannontech.common.validation.service;

import java.util.Date;
import java.util.Set;

import com.cannontech.common.validation.model.RphTag;
import com.cannontech.database.data.lite.LiteYukonUser;

public interface ValidationHelperService {
    
    
    public void acceptAllMatchingRows(Set<RphTag> tags, LiteYukonUser liteYukonUser);
    
    public void deleteAllMatchingRows(Set<RphTag> tags, LiteYukonUser liteYukonUser);
    
    /**
     * @param since pass null to reset since the beginning (changeId 1)
     */
    public void resetValidationEngine(Date since);

    public void acceptRawPointHistoryRow(long changeId, LiteYukonUser user);

    public void deleteRawPointHistoryRow(long changeId, LiteYukonUser user);
}
