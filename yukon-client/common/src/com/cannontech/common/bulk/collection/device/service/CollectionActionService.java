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
     * Returns result from cache. This method should be used if we do not want the result to be re-created from the database.
     * Example would be cancellations, we only can cancel cached executions.
     */
    CollectionActionResult getCachedResult(int key);
    
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
    
}
