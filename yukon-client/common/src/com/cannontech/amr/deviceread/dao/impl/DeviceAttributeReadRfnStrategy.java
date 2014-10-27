package com.cannontech.amr.deviceread.dao.impl;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;

import com.cannontech.amr.device.StrategyType;
import com.cannontech.amr.deviceread.dao.DeviceAttributeReadError;
import com.cannontech.amr.deviceread.dao.DeviceAttributeReadErrorType;
import com.cannontech.amr.deviceread.service.RetryParameters;
import com.cannontech.amr.errors.dao.DeviceErrorTranslatorDao;
import com.cannontech.amr.errors.model.DeviceErrorDescription;
import com.cannontech.amr.errors.model.SpecificDeviceErrorDescription;
import com.cannontech.amr.meter.dao.MeterDao;
import com.cannontech.amr.rfn.dao.RfnDeviceDao;
import com.cannontech.amr.rfn.message.read.RfnMeterReadingDataReplyType;
import com.cannontech.amr.rfn.message.read.RfnMeterReadingReplyType;
import com.cannontech.amr.rfn.model.RfnMeter;
import com.cannontech.amr.rfn.service.RfnDeviceReadCompletionCallback;
import com.cannontech.amr.rfn.service.RfnMeterReadService;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.DeviceRequestType;
import com.cannontech.common.device.commands.CommandRequestDevice;
import com.cannontech.common.device.commands.CommandRequestExecutionObjects;
import com.cannontech.common.device.commands.GroupCommandCompletionCallback;
import com.cannontech.common.device.commands.dao.CommandRequestExecutionResultDao;
import com.cannontech.common.device.commands.dao.model.CommandRequestExecution;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.device.service.CommandCompletionCallbackAdapter;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.PaoUtils;
import com.cannontech.common.pao.YukonDevice;
import com.cannontech.common.pao.attribute.model.Attribute;
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
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

public class DeviceAttributeReadRfnStrategy implements DeviceAttributeReadStrategy {
    
    private final static Logger log = YukonLogManager.getLogger(DeviceAttributeReadPlcStrategy.class);
    @Autowired private AsyncDynamicDataSource asyncDynamicDataSource;
    @Autowired private PaoDefinitionDao paoDefinitionDao;
    @Autowired private RfnMeterReadService rfnMeterReadService;
    @Autowired private MeterDao meterDao;
    @Autowired private RfnDeviceDao rfnDeviceDao;
    @Autowired private RfnExpressComMessageService rfnExpressComMessageService;
    @Autowired private PointDao pointDao;
    @Autowired private CommandRequestExecutionResultDao commandRequestExecutionResultDao;
    @Autowired private YukonUserContextMessageSourceResolver resolver;
    @Autowired private DeviceErrorTranslatorDao deviceErrorTranslatorDao;
    
    @Override
    public StrategyType getType() {
        return StrategyType.RF;
    }
    
    @Override
    public boolean canRead(PaoType paoType) {
        boolean result = false;
        if(paoType.isRfn()){
            result = paoDefinitionDao.isTagSupported(paoType, PaoTag.NETWORK_MANAGER_ATTRIBUTE_READS);
        }
        return result;
    }
    
    @Override
    public boolean isReadable(Iterable<PaoMultiPointIdentifier> devices, LiteYukonUser user) {
        // There's only one command we can send for an Eka meter and it will return everything,
        // so we'll just check if at least one of the attributes requested exists.
        return !Iterables.isEmpty(devices);
    }

    @Override
    public void initiateRead(Iterable<PaoMultiPointIdentifier> points,
            final DeviceAttributeReadStrategyCallback delegateCallback,
            DeviceRequestType type, final CommandRequestExecution execution, LiteYukonUser user) {
          
        RfnStrategyCallback strategyCallback = new RfnStrategyCallback() {
            
            Set<DeviceAttributeReadError> errors = new HashSet<>();
            AtomicInteger completionCounter = new AtomicInteger(1);
            
            @Override
            public void complete() {
                int count = completionCounter.decrementAndGet();
                if (count == 0) {
                    delegateCallback.complete();
                }
            }
            
            @Override
            public void receivedError(PaoIdentifier pao, DeviceAttributeReadError error) {
                delegateCallback.receivedError(pao, error);
                errors.add(error);
                commandRequestExecutionResultDao.saveCommandRequestExecutionResult(execution, pao.getPaoId(),
                    error.getType().getErrorCode());
            }
            
            @Override
            public void receivedValue(PaoIdentifier pao, PointValueHolder value) {
                delegateCallback.receivedValue(pao, value);
            }
                 
            @Override
            public void receivedException(DeviceAttributeReadError error) {
                delegateCallback.receivedException(error);
            }

            @Override
            public void receivedLastValue(PaoIdentifier pao, String value) {
                //ignore the last value if this device had an error
                if (!errors.contains(pao)) {
                    commandRequestExecutionResultDao.saveCommandRequestExecutionResult(execution,  pao.getPaoId(),  0);
                }
                delegateCallback.receivedLastValue(pao, value);
            }
            
            @Override
            public void setCompletionCounter(int completionCounter) {
                this.completionCounter = new AtomicInteger(completionCounter);
            } 
            
        };
        
        initiateRead(points, strategyCallback);
    }
        
    @Override
    public void initiateRead(Iterable<PaoMultiPointIdentifier> points,
                             final GroupCommandCompletionCallback groupCallback, DeviceRequestType type,
                             LiteYukonUser user) {

        RfnStrategyCallback strategyCallback = new RfnStrategyCallback() {
            
            AtomicInteger completionCounter = new AtomicInteger(1);
            
            @Override
            public void complete() {
                int count = completionCounter.decrementAndGet();
                if (count == 0) {
                    groupCallback.complete();
                }
            }
            
            @Override
            public void receivedError(PaoIdentifier pao, DeviceAttributeReadError error) {
                CommandRequestDevice command = new CommandRequestDevice();
                command.setDevice(new SimpleDevice(pao));
                SpecificDeviceErrorDescription errorDescription = getError(error.getType().getErrorCode());
                groupCallback.receivedLastError(command, errorDescription);
                commandRequestExecutionResultDao.saveCommandRequestExecutionResult(groupCallback.getExecution(),
                    pao.getPaoId(), error.getType().getErrorCode());
            }
            
            @Override
            public void receivedValue(PaoIdentifier pao, PointValueHolder value) {
                CommandRequestDevice command = new CommandRequestDevice();
                command.setDevice(new SimpleDevice(pao));
                groupCallback.receivedValue(command, value);
            }
                 
            @Override
            public void receivedException(DeviceAttributeReadError error) {
                SpecificDeviceErrorDescription errorDescription = getError(error.getType().getErrorCode());
                CommandRequestDevice command = new CommandRequestDevice();
                groupCallback.receivedLastError(command, errorDescription);
            }

            @Override
            public void receivedLastValue(PaoIdentifier pao, String value) {
                //ignore the last value if this device had an error
                if (groupCallback.getErrors().get(new SimpleDevice(pao)) == null) {
                    CommandRequestDevice command = new CommandRequestDevice();
                    command.setDevice(new SimpleDevice(pao));
                    groupCallback.receivedLastResultString(command, value);
                    commandRequestExecutionResultDao.saveCommandRequestExecutionResult(groupCallback.getExecution(),
                        pao.getPaoId(), 0);
                }
            }

            @Override
            public void setCompletionCounter(int completionCounter) {
                this.completionCounter = new AtomicInteger(completionCounter);
            }            
        };
        
        initiateRead(points, strategyCallback);
    }
    
    private interface RfnStrategyCallback extends DeviceAttributeReadStrategyCallback{
       
        public void setCompletionCounter(int completionCounter);
    }
    
    @Override
    public int getRequestCount(Collection<PaoMultiPointIdentifier> devicesForThisStrategy){
        return devicesForThisStrategy.size();
    }
    
    //Use for RFN Meters
    private void sendMeterRequests(List<RfnMeter> meters, final DeviceAttributeReadStrategyCallback delegateCallback) {
        final AtomicInteger pendingRequests = new AtomicInteger(meters.size());
        for (final RfnMeter meter : meters) {
            RfnDeviceReadCompletionCallback<RfnMeterReadingReplyType, RfnMeterReadingDataReplyType> meterCallback = getCallback(meter, delegateCallback, pendingRequests);
            rfnMeterReadService.send(meter, meterCallback);
        }
    }
    
    //Use for RFN LCRs
    private void sendDeviceRequests(List<RfnDevice> devices, Multimap<Integer, Integer> devicePointIds, final DeviceAttributeReadStrategyCallback delegateCallback) {
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
    
    private <T1, T2> RfnDeviceReadCompletionCallback<T1, T2> getCallback(final YukonDevice device, final DeviceAttributeReadStrategyCallback delegateCallback, final AtomicInteger pendingRequests) {
        RfnDeviceReadCompletionCallback<T1, T2> callback = new RfnDeviceReadCompletionCallback<T1, T2>() {

            @Override
            public void receivedData(PointValueHolder value) {
                delegateCallback.receivedValue(device.getPaoIdentifier(), value);
            }
            
            @Override
            public void receivedDataError(T2 replyType) {
                MessageSourceResolvable summary = YukonMessageSourceResolvable.createSingleCodeWithArguments("yukon.common.device.attributeRead.rfn.dataError", replyType);
                DeviceAttributeReadError dataError = new DeviceAttributeReadError(DeviceAttributeReadErrorType.COMMUNICATION, summary);
                delegateCallback.receivedError(device.getPaoIdentifier(), dataError);
            }
            
            @Override
            public void receivedStatusError(T1 replyType) {
                MessageSourceResolvable summary = YukonMessageSourceResolvable.createSingleCodeWithArguments("yukon.common.device.attributeRead.rfn.statusError", replyType);
                DeviceAttributeReadError dataError = new DeviceAttributeReadError(DeviceAttributeReadErrorType.UNKNOWN, summary);
                delegateCallback.receivedError(device.getPaoIdentifier(), dataError);
            }
            
            @Override
            public void receivedStatus(T1 status) {
            }
            
            @Override
            public void processingExceptionOccured(MessageSourceResolvable message) {
                DeviceAttributeReadError exception = new DeviceAttributeReadError(DeviceAttributeReadErrorType.EXCEPTION, message);
                delegateCallback.receivedException(exception);
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
    
    /**
     * Callback that listens for point data updates.
     */
    private class DataListeningReadCompletionCallback<T1, T2> implements RfnDeviceReadCompletionCallback<T1, T2>, PointDataListener {
        YukonDevice device;
        DeviceAttributeReadStrategyCallback delegateCallback;
        AtomicInteger pendingRequests;
        boolean isComplete = false;
        
        public DataListeningReadCompletionCallback(YukonDevice device, DeviceAttributeReadStrategyCallback delegateCallback, AtomicInteger pendingRequests) {
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
            if(!isComplete) {
                MessageSourceResolvable summary = YukonMessageSourceResolvable.createSingleCodeWithArguments("yukon.common.device.attributeRead.rfn.dataError", replyType);
                DeviceAttributeReadError dataError = new DeviceAttributeReadError(DeviceAttributeReadErrorType.COMMUNICATION, summary);
                delegateCallback.receivedError(device.getPaoIdentifier(), dataError);
            }
        }
        
        @Override
        public void receivedStatusError(T1 replyType) {
            if(!isComplete) {
                MessageSourceResolvable summary = YukonMessageSourceResolvable.createSingleCodeWithArguments("yukon.common.device.attributeRead.rfn.statusError", replyType);
                DeviceAttributeReadError dataError = new DeviceAttributeReadError(DeviceAttributeReadErrorType.UNKNOWN, summary);
                delegateCallback.receivedError(device.getPaoIdentifier(), dataError);
            }
        }
        
        @Override
        public void receivedStatus(T1 status) {
            //This only means the request was sent
        }
        
        @Override
        public void processingExceptionOccured(MessageSourceResolvable message) {
            if(!isComplete) {
                DeviceAttributeReadError exception = new DeviceAttributeReadError(DeviceAttributeReadErrorType.EXCEPTION, message);
                delegateCallback.receivedException(exception);
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
        List<RfnDevice> rfnDevices = Lists.newArrayListWithCapacity(IterableUtils.guessSize(points));
        
        for (PaoMultiPointIdentifier pointIdentifier: points) {
            if (pointIdentifier.getPao().getPaoType().isMeter()) {
                RfnMeter rfnMeter = meterDao.getRfnMeterForId(pointIdentifier.getPao().getPaoId());
                rfnMeters.add(rfnMeter);
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
            log.debug(getType() + " Strategy initiateRead");
            log.debug("rfnMeter:" + rfnMeters);
            log.debug("rfnDevices:" + rfnDevices);
            log.debug("completionCounter:" + completionCounter);
        }
                
        strategyCallback.setCompletionCounter(completionCounter);
        sendMeterRequests(rfnMeters, strategyCallback);        
        sendDeviceRequests(rfnDevices, devicePointIds, strategyCallback);
    }
            
    @Override
    public CommandRequestExecutionObjects<CommandRequestDevice> initiateRead(Set<SimpleDevice> devices,
            Set<? extends Attribute> attributes, String command, DeviceRequestType type, LiteYukonUser user,
            RetryParameters retryParameters, CommandCompletionCallbackAdapter<CommandRequestDevice> callback) {
        
        throw new UnsupportedOperationException(getType() + " Strategy does not support read with retries");
        
    }

    private SpecificDeviceErrorDescription getError(Integer errorCode) {
        DeviceErrorDescription errorDescription = deviceErrorTranslatorDao.translateErrorCode(errorCode);
        SpecificDeviceErrorDescription deviceErrorDescription =
            new SpecificDeviceErrorDescription(errorDescription, null);

        return deviceErrorDescription;
    }
}