package com.cannontech.web.stars.dr.operator.inventory.service;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import com.cannontech.stars.model.AssetReportDevice;

public interface AssetReportService {
    
    List<AssetReportDevice> getAssetReportDevices(int ecId, List<Integer> assetIds);

    /**
     * Method adds the assets to queue.
     * 
     * @param queue - Queue in which device has to be added
     * @param isCompleted - Flag indicating queuing completion.
     */
    void queueAssetReportDevices(int ecId, List<Integer> assetIds, BlockingQueue<AssetReportDevice> queue,
            AtomicBoolean isCompleted);

}
