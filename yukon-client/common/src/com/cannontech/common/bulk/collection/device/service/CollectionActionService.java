package com.cannontech.common.bulk.collection.device.service;

import java.util.LinkedHashMap;
import java.util.List;

import com.cannontech.common.bulk.collection.device.model.CollectionAction;
import com.cannontech.common.bulk.collection.device.model.CollectionActionDetail;
import com.cannontech.common.bulk.collection.device.model.CollectionActionResult;
import com.cannontech.common.bulk.collection.device.model.DeviceCollection;
import com.cannontech.common.device.DeviceRequestType;
import com.cannontech.common.device.commands.CommandRequestExecutionStatus;
import com.cannontech.common.device.commands.CommandRequestType;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.user.YukonUserContext;

public interface CollectionActionService {

    /**
     * Returns result from cache, if result is not found re-created result from the database.
     */
    CollectionActionResult getResult(int key);
        
    /**
     * Clears cache. Used for testing only.
     */
    void clearCache();
    
    /**
     * Returns list of results from cache.
     */
    List<CollectionActionResult> getCachedResults(List<Integer> cacheKeys);

    /**
     * Creates result for CRE collection action types.
     */
    CollectionActionResult createResult(CollectionAction action, LinkedHashMap<String, String> inputs,
            DeviceCollection collection, CommandRequestType commandRequestType, DeviceRequestType deviceRequestType,
            YukonUserContext context);

    /**
     * Creates result for none-CRE collection action types.
     */
    CollectionActionResult createResult(CollectionAction action, LinkedHashMap<String, String> inputs,
            DeviceCollection collection, YukonUserContext context);
    /**
     * Attempts to cancel execution.
     */
    void cancel(int key, LiteYukonUser user);
    
    /**
     * Updates result's status.
     */
    void updateResult(CollectionActionResult result, CommandRequestExecutionStatus status);
    
    /**
     * Adds unsupported devices to the list.
     */
    void addUnsupportedToResult(CollectionActionDetail detail, CollectionActionResult result,
            List<? extends YukonPao> unsupportedDevices);

    /**
     * Adds unsupported devices to the list.
     */
    void addUnsupportedToResult(CollectionActionDetail detail, CollectionActionResult result, int execId,
            List<? extends YukonPao> devices);
}
