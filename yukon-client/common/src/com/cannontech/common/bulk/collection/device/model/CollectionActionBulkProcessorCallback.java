package com.cannontech.common.bulk.collection.device.model;

import org.apache.log4j.Logger;

import com.cannontech.common.bulk.callbackResult.BulkProcessorCallback;
import com.cannontech.common.bulk.collection.device.service.CollectionActionService;
import com.cannontech.common.bulk.processor.ProcessorCallbackException;
import com.cannontech.common.device.commands.CommandRequestExecutionStatus;
import com.cannontech.common.device.model.SimpleDevice;

public class CollectionActionBulkProcessorCallback implements BulkProcessorCallback<SimpleDevice, SimpleDevice>{

    private CollectionActionResult result;
    private CollectionActionService collectionActionService;

    public CollectionActionBulkProcessorCallback(CollectionActionResult result,
            CollectionActionService collectionActionService) {
        this.result = result;
        this.collectionActionService = collectionActionService;
    }

    @Override
    public void receivedProcessingException(int rowNumber, SimpleDevice device,
            ProcessorCallbackException e) {
        CollectionActionLogDetail log =
            new CollectionActionLogDetail(device, CollectionActionDetail.FAILURE);
        log.setDeviceErrorText(e.getMessage());
        result.addDeviceToGroup(CollectionActionDetail.FAILURE, device, log);
    }

    @Override
    public void processedObject(int rowNumber, SimpleDevice device) {
        result.addDeviceToGroup(CollectionActionDetail.SUCCESS, device,
            new CollectionActionLogDetail(device, CollectionActionDetail.SUCCESS));
    }

    @Override
    public void processingSucceeded() {
        collectionActionService.updateResult(result, CommandRequestExecutionStatus.COMPLETE);
        result.log();
    }

    @Override
    public void processingFailed(Exception e) {
        collectionActionService.updateResult(result, CommandRequestExecutionStatus.FAILED);
        CollectionActionLogDetail detailLog =
            new CollectionActionLogDetail(null, CollectionActionDetail.FAILURE);
        detailLog.setExecutionExceptionText("Failed");
        result.setExecutionExceptionText("Failed", detailLog);
        result.log();
    }
}
