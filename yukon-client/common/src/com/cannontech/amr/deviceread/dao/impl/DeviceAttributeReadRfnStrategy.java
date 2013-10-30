package com.cannontech.amr.deviceread.dao.impl;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;

import com.cannontech.amr.device.StrategyType;
import com.cannontech.amr.deviceread.dao.DeviceAttributeReadError;
import com.cannontech.amr.deviceread.dao.DeviceAttributeReadErrorType;
import com.cannontech.amr.errors.model.DeviceErrorDescription;
import com.cannontech.amr.errors.model.SpecificDeviceErrorDescription;
import com.cannontech.amr.meter.dao.MeterDao;
import com.cannontech.amr.rfn.dao.RfnDeviceDao;
import com.cannontech.amr.rfn.message.read.RfnMeterReadingDataReplyType;
import com.cannontech.amr.rfn.message.read.RfnMeterReadingReplyType;
import com.cannontech.amr.rfn.model.RfnMeter;
import com.cannontech.amr.rfn.service.RfnDeviceReadCompletionCallback;
import com.cannontech.amr.rfn.service.RfnMeterReadService;
import com.cannontech.common.device.DeviceRequestType;
import com.cannontech.common.device.commands.CommandRequestDevice;
import com.cannontech.common.device.commands.GroupCommandCompletionCallback;
import com.cannontech.common.device.commands.dao.CommandRequestExecutionResultDao;
import com.cannontech.common.device.commands.dao.model.CommandRequestExecution;
import com.cannontech.common.device.commands.dao.model.CommandRequestExecutionResult;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.PaoUtils;
import com.cannontech.common.pao.YukonDevice;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDao;
import com.cannontech.common.pao.definition.model.PaoMultiPointIdentifier;
import com.cannontech.common.pao.definition.model.PaoTag;
import com.cannontech.common.rfn.model.RfnDevice;
import com.cannontech.common.util.IterableUtils;
import com.cannontech.core.dao.NotFoundException;
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
import com.cannontech.user.YukonUserContext;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

public class DeviceAttributeReadRfnStrategy implements DeviceAttributeReadStrategy {
    @Autowired private AsyncDynamicDataSource asyncDynamicDataSource;
    @Autowired private PaoDefinitionDao paoDefinitionDao;
    @Autowired private RfnMeterReadService rfnMeterReadService;
    @Autowired private MeterDao meterDao;
    @Autowired private RfnDeviceDao rfnDeviceDao;
    @Autowired private RfnExpressComMessageService rfnExpressComMessageService;
    @Autowired private PointDao pointDao;
    @Autowired private CommandRequestExecutionResultDao commandRequestExecutionResultDao;
    @Autowired private YukonUserContextMessageSourceResolver resolver;
    
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
    public void initiateRead(Iterable<PaoMultiPointIdentifier> devices,
            final DeviceAttributeReadStrategyCallback delegateCallback,
            DeviceRequestType type, LiteYukonUser user) {
        
        Iterable<PaoIdentifier> paoIdentifiers = PaoUtils.convertPaoMultisToPaoIdentifiers(devices);
        final List<RfnMeter> rfnMeters = Lists.newArrayListWithCapacity(IterableUtils.guessSize(devices));
        final List<RfnDevice> rfnDevices = Lists.newArrayListWithCapacity(IterableUtils.guessSize(devices));
        
        for (PaoIdentifier paoIdentifier : paoIdentifiers) {
            try {
                if (paoIdentifier.getPaoType().isMeter()) {
                    RfnMeter rfnMeter;
                    rfnMeter = meterDao.getRfnMeterForId(paoIdentifier.getPaoId()); // TODO create bulk loader
                    rfnMeters.add(rfnMeter);
                } else {
                    RfnDevice rfnDevice;
                    rfnDevice = rfnDeviceDao.getDevice(paoIdentifier); // TODO create bulk loader
                    rfnDevices.add(rfnDevice);
                }
            } catch (NotFoundException e) {
                MessageSourceResolvable summary = YukonMessageSourceResolvable.createSingleCodeWithArguments("yukon.common.device.attributeRead.rfn.notKnown", e.getMessage());
                DeviceAttributeReadError lookupError = new DeviceAttributeReadError(DeviceAttributeReadErrorType.UNKNOWN, summary);
                delegateCallback.receivedError(paoIdentifier, lookupError);
            }
        }
        
        if (rfnMeters.isEmpty() && rfnDevices.isEmpty()) {
            delegateCallback.complete();
            return;
        }
        
        sendMeterRequests(rfnMeters, delegateCallback);
        
        Iterable<Integer> devicePaoIds = Iterables.transform(rfnDevices, PaoUtils.getYukonPaoToPaoIdFunction());
        Multimap<Integer, Integer> devicePointIds = pointDao.getPaoPointMultimap(devicePaoIds);
        sendDeviceRequests(rfnDevices, devicePointIds, delegateCallback);
    }
    
    @Override
    public void initiateRead(Iterable<PaoMultiPointIdentifier> points,
                             final GroupCommandCompletionCallback groupCallback, DeviceRequestType type,
                             final YukonUserContext userContext) {

        DeviceAttributeReadStrategyCallback strategyCallback = new DeviceAttributeReadStrategyCallback() {
            MessageSourceAccessor messageSourceAccessor = resolver.getMessageSourceAccessor(userContext);
            @Override
            public void complete() {
                groupCallback.complete();
            }
            
            @Override
            public void receivedError(PaoIdentifier pao, DeviceAttributeReadError error) {
                CommandRequestDevice command = new CommandRequestDevice();
                command.setDevice(new SimpleDevice(pao));
                SpecificDeviceErrorDescription errorDescription = getErrorDescription(error);
                groupCallback.receivedLastError(command, errorDescription);
                saveCommandRequestExecutionResult(groupCallback.getExecution(), pao.getPaoId(), error.getType().getErrorCode());
            }
            
            @Override
            public void receivedValue(PaoIdentifier pao, PointValueHolder value) {
                CommandRequestDevice command = new CommandRequestDevice();
                command.setDevice(new SimpleDevice(pao));
                groupCallback.receivedValue(command, value);
            }
                 
            @Override
            public void receivedException(DeviceAttributeReadError exception) {
                SpecificDeviceErrorDescription errorDescription = getErrorDescription(exception);
                CommandRequestDevice command = new CommandRequestDevice();
                groupCallback.receivedLastError(command, errorDescription);
            }

            @Override
            public void receivedLastValue(PaoIdentifier pao) {
                //ignore the last value if this device had an error
                if (groupCallback.getErrors().get(new SimpleDevice(pao)) == null) {
                    CommandRequestDevice command = new CommandRequestDevice();
                    command.setDevice(new SimpleDevice(pao));
                    groupCallback.receivedLastResultString(command, "");
                    saveCommandRequestExecutionResult(groupCallback.getExecution(),  pao.getPaoId(),  0);
                }
            }
            
            private SpecificDeviceErrorDescription getErrorDescription(DeviceAttributeReadError error) {
                String summary = "";
                String detail = "";
                if (error.getSummary() != null) {
                    summary = messageSourceAccessor.getMessage(error.getSummary());
                }
                if (error.getDetail() != null) {
                    detail = messageSourceAccessor.getMessage(error.getDetail());
                }
                DeviceErrorDescription desc = new DeviceErrorDescription(error.getType().getErrorCode(), "", "", summary, detail);
                SpecificDeviceErrorDescription errorDescription = new SpecificDeviceErrorDescription(desc,  detail);
                return errorDescription;
            }
        };
        
        initiateRead(points, strategyCallback, type, userContext.getYukonUser());
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
                delegateCallback.receivedLastValue(device.getPaoIdentifier());
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
                delegateCallback.receivedLastValue(device.getPaoIdentifier());
                int remaining = pendingRequests.decrementAndGet();
                if (remaining == 0) {
                    delegateCallback.complete();
                }
            }
        }
    }
    
    private void saveCommandRequestExecutionResult(CommandRequestExecution execution,
                                                   int deviceId, int errorCode) {

        CommandRequestExecutionResult result = new CommandRequestExecutionResult();
        result.setCommandRequestExecutionId(execution.getId());
        result.setCommand(execution.getCommandRequestExecutionType().getShortName());
        result.setCompleteTime(new Date());
        result.setDeviceId(deviceId);
        result.setErrorCode(errorCode);
        commandRequestExecutionResultDao.saveOrUpdate(result);
    }
}