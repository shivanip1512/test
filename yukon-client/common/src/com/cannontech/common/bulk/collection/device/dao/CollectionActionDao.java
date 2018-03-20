package com.cannontech.common.bulk.collection.device.dao;

import java.util.Date;
import java.util.List;

import com.cannontech.common.bulk.collection.device.model.CollectionAction;
import com.cannontech.common.bulk.collection.device.model.CollectionActionFilter;
import com.cannontech.common.bulk.collection.device.model.CollectionActionFilteredResult;
import com.cannontech.common.bulk.collection.device.model.CollectionActionResult;
import com.cannontech.common.device.commands.CommandRequestExecutionStatus;
import com.cannontech.common.model.Direction;
import com.cannontech.common.model.PagingParameters;
import com.cannontech.common.search.result.SearchResults;
import com.cannontech.database.data.lite.LiteYukonUser;

public interface CollectionActionDao {

    public enum SortBy{
        ACTION("ca.Action"),
        START_TIME("ca.StartTime"),
        SUCCESS("c.SuccessCount"),
        FAILURE("c.FailedCount"),
        NOT_ATTEMPTED("c.NotAttemptedCount"),
        STATUS("ca.Status"),
        USER_NAME("ca.UserName");
        
        private SortBy(String dbString) {
            this.dbString = dbString;
        }
        
        private final String dbString;

        public String getDbString() {
            return dbString;
        }
    }
    
    void createCollectionAction(CollectionActionResult result, LiteYukonUser user);

    CollectionActionResult loadResultFromDb(int key);

    List<CollectionAction> getHistoricalActions();

    void updateDbRequestStatus(int collectionActionId, int deviceId, CommandRequestExecutionStatus newStatus);

    void updateCollectionActionStatus(int collectionActionId, CommandRequestExecutionStatus newStatus, Date stopTime);

    SearchResults<CollectionActionFilteredResult> getCollectionActionFilteredResults(CollectionActionFilter filter,
            PagingParameters paging, SortBy sortBy, Direction direction);

    int getCollectionActionIdFromCreId(int creId);

    void updateCollectionActionRequest(int cacheKey, int deviceId, CommandRequestExecutionStatus status);
}
