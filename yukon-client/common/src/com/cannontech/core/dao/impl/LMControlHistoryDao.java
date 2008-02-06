package com.cannontech.core.dao.impl;

import java.util.Date;
import java.util.List;
import org.springframework.dao.DataAccessException;

import com.cannontech.database.db.device.lm.LMControlHistory;

public interface LMControlHistoryDao {

    public LMControlHistory getById(int controlEntryId) throws DataAccessException;
    
    public List<LMControlHistory> getByPAObjectId(final int paObjectId);
    
    public List<LMControlHistory> getByPAObjectIdAndStartDateRange(final int paObjectId, Date firstDate, Date secondDate);
    
    public List<LMControlHistory> getByStartDateRange(Date firstDate, Date secondDate);
    
    public List<LMControlHistory> getAll();
}
