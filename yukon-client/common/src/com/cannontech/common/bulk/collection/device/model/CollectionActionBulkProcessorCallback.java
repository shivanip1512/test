package com.cannontech.common.bulk.collection.device.model;

import com.cannontech.common.bulk.callbackResult.BulkProcessorCallback;
import com.cannontech.common.bulk.collection.device.dao.CollectionActionDao;
import com.cannontech.common.bulk.collection.device.service.CollectionActionService;
import com.cannontech.common.bulk.processor.ProcessorCallbackException;
import com.cannontech.common.device.commands.CommandRequestExecutionStatus;
import com.cannontech.common.device.model.SimpleDevice;

/**
 * Callback for non-CRE collection actions
 */
public class CollectionActionBulkProcessorCallback implements BulkProcessorCallback<SimpleDevice, SimpleDevice>{

    protected CollectionActionResult result;
    private CollectionActionService collectionActionService;
    private CollectionActionDao collectionActionDao;

    public CollectionActionBulkProcessorCallback(CollectionActionResult result,
            CollectionActionService collectionActionService, CollectionActionDao collectionActionDao) {
        this.result = result;
        this.collectionActionService = collectionActionService;
        this.collectionActionDao = collectionActionDao;
    }

    @Override
    public void receivedProcessingException(int rowNumber, SimpleDevice device, ProcessorCallbackException e) {
        CollectionActionLogDetail log = new CollectionActionLogDetail(device, e.getDetail());
        log.setDeviceErrorText(e.getMessage());
        result.addDeviceToGroup(e.getDetail(), device, log);
        collectionActionDao.updateCollectionActionRequest(result.getCacheKey(), device.getDeviceId(),
            CommandRequestExecutionStatus.FAILED);
    }

    @Override
    public void processedObject(int rowNumber, SimpleDevice device) {
        result.addDeviceToGroup(CollectionActionDetail.SUCCESS, device,
            new CollectionActionLogDetail(device, CollectionActionDetail.SUCCESS));
        collectionActionDao.updateCollectionActionRequest(result.getCacheKey(), device.getDeviceId(),
            CommandRequestExecutionStatus.COMPLETE);
    }

    @Override
    public void processingSucceeded() {
        collectionActionService.updateResult(result, CommandRequestExecutionStatus.COMPLETE);
    }

    @Override
    public void processingFailed(Exception e) {
        result.setExecutionExceptionText("There was an error processing the command");
        collectionActionService.updateResult(result, CommandRequestExecutionStatus.FAILED);
    }
}
