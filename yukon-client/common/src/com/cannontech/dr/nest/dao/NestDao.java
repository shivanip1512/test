package com.cannontech.dr.nest.dao;

import java.util.List;

import com.cannontech.common.model.Direction;
import com.cannontech.common.model.PagingParameters;
import com.cannontech.common.search.result.SearchResults;
import com.cannontech.dr.nest.model.NestControlHistory;
import com.cannontech.dr.nest.model.NestSync;
import com.cannontech.dr.nest.model.NestSyncDetail;
import com.cannontech.dr.nest.model.NestSyncType;

public interface NestDao {

    public enum SortBy {
        SYNCTYPE;
    }
    
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

    /**
     * Returns list of previous sync sorted by most recent
     */
    List<NestSync> getNestSyncs();
    
    /**
     * return searchResults with NestSync discrepancies information
     */
    SearchResults<NestSyncDetail> getNestSyncDetail(int syncId, PagingParameters paging, SortBy sortBy, Direction direction, List<NestSyncType> syncTypes);

    /**
     * returns the NestSync by Sync Id
     */
    NestSync getNestSyncById(int syncId);

}
