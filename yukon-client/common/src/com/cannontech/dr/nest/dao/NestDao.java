package com.cannontech.dr.nest.dao;

import java.util.List;

import com.cannontech.common.model.Direction;
import com.cannontech.common.model.PagingParameters;
import com.cannontech.common.search.result.SearchResults;
import com.cannontech.dr.nest.model.NestControlEvent;
import com.cannontech.dr.nest.model.NestSync;
import com.cannontech.dr.nest.model.NestSyncDetail;
import com.cannontech.dr.nest.model.NestSyncType;
import com.cannontech.dr.nest.model.v3.LoadShapingOptions;

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
     * Returns recent control event for a group that can be stopped or canceled
     */
    NestControlEvent getCancelableEvent(String group);;

    /**
     * Saves control history event
     */
    void saveControlEvent(NestControlEvent event);

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
    
    /**
     * Returns LMNestLoadShapingGear for gearId. If not found, returns null.
     */
    public LoadShapingOptions findNestLoadShapingOptions(int gearId);
}
