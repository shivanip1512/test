package com.cannontech.dr.nest.dao;

import java.util.List;

import com.cannontech.dr.nest.model.NestControlHistory;
import com.cannontech.dr.nest.model.NestSync;
import com.cannontech.dr.nest.model.NestSyncDetail;

public interface NestDao {

    /**
     * Saves sync details
     */
    void saveSyncDetails(List<NestSyncDetail> details);

    /**
     * Saves sync info
     */
    void saveSyncInfo(NestSync sync);

    /**
     * Updated LMNestControlHistory cancel request time with "now"
     */
    void updateCancelRequestTime(int id);

    /**
     * Updates LMNestControlHistory with Nest response to cancellation request
     */
    void updateNestResponse(int id, String response);

    /**
     * Returns recent control history for a group that can be used to cancel control
     */
    NestControlHistory getRecentHistoryForGroup(String group);

    /**
     * Creates control history
     */
    void createControlHistory(NestControlHistory history);
}
