package com.cannontech.core.dao.impl;

import java.util.List;

import com.cannontech.database.db.device.lm.LMControlHistory;

public interface LMControlHistoryDao {
    List<LMControlHistory> getAll();
}
