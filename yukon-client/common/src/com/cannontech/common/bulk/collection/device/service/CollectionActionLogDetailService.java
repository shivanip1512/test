package com.cannontech.common.bulk.collection.device.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

import com.cannontech.common.bulk.collection.device.model.CollectionActionDetail;
import com.cannontech.common.bulk.collection.device.model.CollectionActionLogDetail;
import com.cannontech.common.bulk.collection.device.model.CollectionActionResult;
import com.cannontech.common.pao.YukonPao;

public interface CollectionActionLogDetailService {

    /**
     * Helper method to build log details.
     */
    List<CollectionActionLogDetail> buildLogDetails(List<? extends YukonPao> paos, CollectionActionDetail detail);

    /**
     * Appends log detail to a log file.
     */
    void appendToLog(CollectionActionResult result, CollectionActionLogDetail detail);

    /**
     * Appends log details to a log file.
     */
    void appendToLog(CollectionActionResult result, List<CollectionActionLogDetail> details);

    /**
     * Returns log file.
     */
    File getLog(int cacheKey) throws FileNotFoundException;

    /**
     * Returns true if log file exist. It is possible for the result to be created and file not to exist if
     * devices haven't responded yet.
     */
    boolean hasLog(int cacheKey);

    /**
     * Log details are cached until execution/action is done (Completed, Canceled etc) to prevent duplicate
     * entries in the log file for the same devices. When action is done this method is called to clear cache.
     */
    void clearCache(int cacheKey);
}
