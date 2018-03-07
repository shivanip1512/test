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
     * Returns true if log file exist.
     */
    boolean hasLog(int cacheKey);
}
