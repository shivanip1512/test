package com.cannontech.amr.deviceread.dao.impl;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;

import com.cannontech.amr.deviceread.dao.DeviceAttributeReadCallback;
import com.cannontech.amr.errors.dao.DeviceError;
import com.cannontech.amr.errors.dao.DeviceErrorTranslatorDao;
import com.cannontech.amr.errors.model.DeviceErrorDescription;
import com.cannontech.amr.errors.model.SpecificDeviceErrorDescription;
import com.cannontech.amr.meter.dao.MeterDao;
import com.cannontech.amr.rfn.dao.RfnDeviceDao;
import com.cannontech.amr.rfn.message.disconnect.RfnMeterDisconnectConfirmationReplyType;
import com.cannontech.amr.rfn.message.disconnect.RfnMeterDisconnectState;
import com.cannontech.amr.rfn.message.disconnect.RfnMeterDisconnectStatusType;
import com.cannontech.amr.rfn.message.read.RfnMeterReadingDataReplyType;
import com.cannontech.amr.rfn.message.read.RfnMeterReadingReplyType;
import com.cannontech.amr.rfn.model.RfnMeter;
import com.cannontech.amr.rfn.service.RfnDeviceReadCompletionCallback;
import com.cannontech.amr.rfn.service.RfnMeterDisconnectCallback;
import com.cannontech.amr.rfn.service.RfnMeterDisconnectService;
import com.cannontech.amr.rfn.service.RfnMeterReadService;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.bulk.collection.device.model.CollectionActionCancellationCallback;
import com.cannontech.common.bulk.collection.device.model.CollectionActionResult;
import com.cannontech.common.bulk.collection.device.model.StrategyType;
import com.cannontech.common.device.commands.dao.CommandRequestExecutionResultDao;
import com.cannontech.common.device.commands.dao.model.CommandRequestExecution;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.PaoUtils;
import com.cannontech.common.pao.YukonDevice;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDao;
import com.cannontech.common.pao.definition.model.PaoMultiPointIdentifier;
import com.cannontech.common.pao.definition.model.PaoTag;
import com.cannontech.common.rfn.model.RfnDevice;
import com.cannontech.common.util.IterableUtils;
import com.cannontech.core.dao.PointDao;
import com.cannontech.core.dynamic.AsyncDynamicDataSource;
import com.cannontech.core.dynamic.PointDataListener;
import com.cannontech.core.dynamic.PointValueHolder;
import com.cannontech.core.dynamic.PointValueQualityHolder;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.dr.rfn.message.unicast.RfnExpressComUnicastDataReplyType;
import com.cannontech.dr.rfn.message.unicast.RfnExpressComUnicastReplyType;
import com.cannontech.dr.rfn.service.RfnExpressComMessageService;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

public class DeviceAttributeReadRfnServiceImpl implements DeviceAttributeReadStrategy {
    
    private final static Logger log = YukonLogManager.getLogger(DeviceAttributeReadRfnServiceImpl.class);
    @Autowired private AsyncDynamicDataSource asyncDynamicDataSource;
    @Autowired private PaoDefinitionDao paoDefinitionDao;
    @Autowired private RfnMeterReadService rfnMeterReadService;
    @Autowired private RfnMeterDisconnectService rfnMeterDisconnectService;
    @Autowired private AttributeService attributeService;
    @Autowired private MeterDao meterDao;
    @Autowired private RfnDeviceDao rfnDeviceDao;
    @Autowired private RfnExpressComMessageService rfnExpressComMessageService;
    @Autowired private PointDao pointDao;
    @Autowired private CommandRequestExecutionResultDao commandRequestExecutionResultDao;
    @Autowired private DeviceErrorTranslatorDao deviceErrorTranslatorDao;
    
    @Override
    public StrategyType getStrategy() {
        return StrategyType.NM;
    }
    
    @Override
    public boolean canRead(PaoType paoType) {
        return paoType.isRfn() && paoDefinitionDao.isTagSupported(paoType, PaoTag.NETWORK_MANAGER_ATTRIBUTE_READS);
    }
    
    @Override
    public boolean isReadable(Iterable<PaoMultiPointIdentifier> devices) {
        // There are only two commands we can send for an Eka meter - one for just the disconnect status and one for everything,
        // so we'll just check if they specified at least one attribute.
        return !Iterables.isEmpty(devices);
    }

    @Override
    public void initiateRead(Iterable<PaoMultiPointIdentifier> points, DeviceAttributeReadCallback callback,
            CommandRequestExecution execution, LiteYukonUser user) {
        if(callback.getResult() != null) {
            callback.getResult().addCancellationCallback(new CollectionActionCancellationCallback(getStrategy(), callback));
        }
        initiateRead(points, new RfnStrategyCallback(callback, execution));
    }

    private class RfnStrategyCallback implements DeviceAttributeReadCallback{
       
        Set<PaoIdentifier> errors = Sets.newConcurrentHashSet();
        AtomicInteger completionCounter = new AtomicInteger(1);
        DeviceAttributeReadCallback callback;
        CommandRequestExecution execution;
        public RfnStrategyCallback(DeviceAttributeReadCallback callback, CommandRequestExecution execution) {
            this.callback = callback;
            this.execution = execution;
        }

        @Override
        public void complete() {
            int count = completionCounter.decrementAndGet();
            if (count == 0) {
                callback.complete(getStrategy());
            }
        }

        @Override
        public void receivedError(PaoIdentifier pao, SpecificDeviceErrorDescription error) {
            callback.receivedError(pao, error);
            errors.add(pao);
            commandRequestExecutionResultDao.saveCommandRequestExecutionResult(execution, pao.getPaoId(),
                error.getErrorCode());
        }
        
        @Override
        public void receivedValue(PaoIdentifier pao, PointValueHolder value) {
            callback.receivedValue(pao, value);
        }
             
        @Override
        public void receivedException(SpecificDeviceErrorDescription error) {
            callback.receivedException(error);
        }

        @Override
        public void receivedLastValue(PaoIdentifier pao, String value) {
            //ignore the last value if this device had an error
            if (!errors.contains(pao)) {
                commandRequestExecutionResultDao.saveCommandRequestExecutionResult(execution,  pao.getPaoId(),  0);
                callback.receivedLastValue(pao, value);
            }
        }
        
        public void setCompletionCounter(int completionCounter) {
            this.completionCounter = new AtomicInteger(completionCounter);
        }
    }
    
    @Override
    public int getRequestCount(Collection<PaoMultiPointIdentifier> devicesForThisStrategy){
        return devicesForThisStrategy.size();
    }
    
    //Use for RFN Meters
    private void sendMeterRequests(List<RfnMeter> meters, final DeviceAttributeReadCallback delegateCallback) {
        final AtomicInteger pendingRequests = new AtomicInteger(meters.size());
        for (final RfnMeter meter : meters) {
            RfnDeviceReadCompletionCallback<RfnMeterReadingReplyType, RfnMeterReadingDataReplyType> meterCallback = getCallback(meter, delegateCallback, pendingRequests);
            rfnMeterReadService.send(meter, meterCallback);
        }
    }
    
    //Use for RFN disconnect statuses
    private void sendMeterDisconnectQueries(List<RfnMeter> meters, final DeviceAttributeReadCallback delegateCallback) {
        final AtomicInteger pendingRequests = new AtomicInteger(meters.size());
        for (final RfnMeter meter : meters) {
            var disconnectCallback = getDisconnectCallback(meter, delegateCallback, pendingRequests);
            rfnMeterDisconnectService.send(meter, RfnMeterDisconnectStatusType.QUERY, disconnectCallback);
        }
    }

    //Use for RFN LCRs
    private void sendDeviceRequests(List<RfnDevice> devices, Multimap<Integer, Integer> devicePointIds, final DeviceAttributeReadCallback delegateCallback) {
        final AtomicInteger pendingRequests = new AtomicInteger(devices.size());
        for (final RfnDevice device : devices) {
            DataListeningReadCompletionCallback<RfnExpressComUnicastReplyType, RfnExpressComUnicastDataReplyType> deviceCallback = 
                    new DataListeningReadCompletionCallback<>(device, delegateCallback, pendingRequests);
            //register callback as listener for device points (since request won't return data)
            int paoIdentifier = device.getPaoIdentifier().getPaoId();
            Set<Integer> pointIds = Sets.newHashSet(devicePointIds.get(paoIdentifier));
            asyncDynamicDataSource.registerForPointData(deviceCallback, pointIds);
            rfnExpressComMessageService.readDevice(device, deviceCallback);
        }
    }
    
    //Use for RFN meters
    private <T1, T2> RfnDeviceReadCompletionCallback<T1, T2> getCallback(final YukonDevice device, final DeviceAttributeReadCallback delegateCallback, final AtomicInteger pendingRequests) {
        RfnDeviceReadCompletionCallback<T1, T2> callback = new RfnDeviceReadCompletionCallback<T1, T2>() {

            @Override
            public void receivedData(PointValueHolder value) {
                delegateCallback.receivedValue(device.getPaoIdentifier(), value);
            }
            
            @Override
            public void receivedDataError(T2 replyType) {
                SpecificDeviceErrorDescription error =
                    getError(DeviceError.NM_TIMEOUT, replyType, "yukon.common.device.attributeRead.rfn.dataError");
                delegateCallback.receivedError(device.getPaoIdentifier(), error);
            }

            @Override
            public void receivedStatusError(T1 replyType) {
                SpecificDeviceErrorDescription error =
                    getError(DeviceError.FAILURE, replyType, "yukon.common.device.attributeRead.rfn.statusError");
                delegateCallback.receivedError(device.getPaoIdentifier(), error);
            }
            
            @Override
            public void receivedStatus(T1 status) {
            }
            
            @Override
            public void processingExceptionOccurred(MessageSourceResolvable detail) {
                SpecificDeviceErrorDescription error = getError(DeviceError.FAILURE, detail);
                delegateCallback.receivedException(error);
            }

            @Override
            public void complete() {
                delegateCallback.receivedLastValue(device.getPaoIdentifier(), "");
                int remaining = pendingRequests.decrementAndGet();
                if (remaining == 0) {
                    delegateCallback.complete();
                }
            }
        };
        
        return callback;
    }
    
    //Use for RFN disconnect statuses
    private RfnMeterDisconnectCallback getDisconnectCallback(final RfnMeter meter, final DeviceAttributeReadCallback delegateCallback,
            final AtomicInteger pendingRequests) {
        return new RfnMeterDisconnectCallback() {

            @Override
            public void processingExceptionOccurred(MessageSourceResolvable detail) {
                SpecificDeviceErrorDescription error = getError(DeviceError.FAILURE, detail);
                delegateCallback.receivedException(error);
            }

            @Override
            public void complete() {
                delegateCallback.receivedLastValue(meter.getPaoIdentifier(), "");
                int remaining = pendingRequests.decrementAndGet();
                if (remaining == 0) {
                    delegateCallback.complete();
                }
            }

            @Override
            public void receivedSuccess(RfnMeterDisconnectState state, PointValueQualityHolder pointData) {
                delegateCallback.receivedValue(meter.getPaoIdentifier(), pointData);
            }

            @Override
            public void receivedError(MessageSourceResolvable message, RfnMeterDisconnectState state,
                    RfnMeterDisconnectConfirmationReplyType replyType) {
                SpecificDeviceErrorDescription error =
                        getError(DeviceError.FAILURE, replyType, "yukon.common.device.attributeRead.rfn.statusError");
                delegateCallback.receivedError(meter.getPaoIdentifier(), error);
            }
        };
    }
    
    /**
     * Callback that listens for point data updates.
     */
    private class DataListeningReadCompletionCallback<T1, T2> implements RfnDeviceReadCompletionCallback<T1, T2>, PointDataListener {
        YukonDevice device;
        DeviceAttributeReadCallback delegateCallback;
        AtomicInteger pendingRequests;
        boolean isComplete = false;
        
        public DataListeningReadCompletionCallback(YukonDevice device, DeviceAttributeReadCallback delegateCallback, AtomicInteger pendingRequests) {
            this.device = device;
            this.delegateCallback = delegateCallback;
            this.pendingRequests = pendingRequests;
        }
        
        @Override
        public void pointDataReceived(PointValueQualityHolder pointData) {
            if(!isComplete) {
                receivedData(pointData);
                complete();
            }
        }
        
        @Override
        public void receivedData(PointValueHolder value) {
            if(!isComplete) {
                delegateCallback.receivedValue(device.getPaoIdentifier(), value);
            }
        }
        
        @Override
        public void receivedDataError(T2 replyType) {
            if (!isComplete) {
                SpecificDeviceErrorDescription error =
                    getError(DeviceError.NM_TIMEOUT, replyType, "yukon.common.device.attributeRead.rfn.statusError");
                delegateCallback.receivedError(device.getPaoIdentifier(), error);
            }
        }
        
        @Override
        public void receivedStatusError(T1 replyType) {
            if(!isComplete) {
                SpecificDeviceErrorDescription error =
                    getError(DeviceError.FAILURE, replyType, "yukon.common.device.attributeRead.rfn.statusError");
                delegateCallback.receivedError(device.getPaoIdentifier(), error);
            }
        }
        
        @Override
        public void receivedStatus(T1 status) {
            //This only means the request was sent
        }
        
        @Override
        public void processingExceptionOccurred(MessageSourceResolvable detail) {
            if(!isComplete) {
                SpecificDeviceErrorDescription error = getError(DeviceError.FAILURE, detail);
                delegateCallback.receivedException( error);
            }
        }

        @Override
        public void complete() {
            if(!isComplete) {
                isComplete = true;
                delegateCallback.receivedLastValue(device.getPaoIdentifier(), "");
                int remaining = pendingRequests.decrementAndGet();
                if (remaining == 0) {
                    delegateCallback.complete();
                }
            }
        }
    }
    
    private void initiateRead(Iterable<PaoMultiPointIdentifier> points,  RfnStrategyCallback strategyCallback){
        List<RfnMeter> rfnMeters = Lists.newArrayListWithCapacity(IterableUtils.guessSize(points));
        List<RfnMeter> rfnDisconnectMeters = Lists.newArrayListWithCapacity(IterableUtils.guessSize(points));
        List<RfnDevice> rfnDevices = Lists.newArrayListWithCapacity(IterableUtils.guessSize(points));
        
        for (PaoMultiPointIdentifier pointIdentifier: points) {
            if (pointIdentifier.getPao().getPaoType().isMeter()) {
                RfnMeter rfnMeter = meterDao.getRfnMeterForId(pointIdentifier.getPao().getPaoId());
                if (containsOnlyDisconnectStatus(pointIdentifier)) {
                    rfnDisconnectMeters.add(rfnMeter);
                } else {
                    rfnMeters.add(rfnMeter);
                }
            } else {
                RfnDevice rfnDevice = rfnDeviceDao.getDevice(pointIdentifier.getPao());
                rfnDevices.add(rfnDevice);
            }
        }
        
        Iterable<Integer> devicePaoIds = Iterables.transform(rfnDevices, PaoUtils.getYukonPaoToPaoIdFunction());
        Multimap<Integer, Integer> devicePointIds = pointDao.getPaoPointMultimap(devicePaoIds);
          
        int completionCounter = 0;
        
        if(!rfnMeters.isEmpty()){
            completionCounter ++;
        }
        if(!rfnDevices.isEmpty()){
            completionCounter ++;
        }
        
        if (log.isDebugEnabled()) {
            log.debug(getStrategy() + " Strategy initiateRead");
            log.debug("rfnMeter:" + rfnMeters);
            log.debug("rfnDevices:" + rfnDevices);
            log.debug("completionCounter:" + completionCounter);
        }
                
        strategyCallback.setCompletionCounter(completionCounter);
        sendMeterRequests(rfnMeters, strategyCallback);        
        sendMeterDisconnectQueries(rfnDisconnectMeters, strategyCallback);
        sendDeviceRequests(rfnDevices, devicePointIds, strategyCallback);
    }
    
    private boolean containsOnlyDisconnectStatus(PaoMultiPointIdentifier paoMultiPointIdentifier) {
        var paoPointIdentifiers = paoMultiPointIdentifier.getPaoPointIdentifiers(); 
        if (paoPointIdentifiers.size() == 1) {
            var point = Iterables.getOnlyElement(paoPointIdentifiers);
            return attributeService.findPaoPointIdentifierForAttribute(paoMultiPointIdentifier.getPao(), BuiltInAttribute.DISCONNECT_STATUS)
                    .map(p -> p.equals(point))
                    .orElse(false);
        }
        return false;
    }
    
    private <T> SpecificDeviceErrorDescription getError(DeviceError error, T replyType, String msgCode) {
        DeviceErrorDescription errorDescription = deviceErrorTranslatorDao.translateErrorCode(error);
        MessageSourceResolvable detail = YukonMessageSourceResolvable.createSingleCodeWithArguments(msgCode, replyType);
        return new SpecificDeviceErrorDescription(errorDescription, detail);
    }
    
    private SpecificDeviceErrorDescription getError(DeviceError error, MessageSourceResolvable detail) {
        DeviceErrorDescription errorDescription = deviceErrorTranslatorDao.translateErrorCode(error);
        return new SpecificDeviceErrorDescription(errorDescription, detail);
    }

    @Override
    public void cancel(CollectionActionResult result, LiteYukonUser user) {
        //doesn't support cancellations
        result.getCancellationCallbacks(getStrategy()).forEach(callback -> {
            callback.cancel();
        });
    }
}