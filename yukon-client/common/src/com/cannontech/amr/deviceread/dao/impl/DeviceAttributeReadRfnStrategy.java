package com.cannontech.amr.deviceread.dao.impl;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;

import com.cannontech.amr.device.StrategyType;
import com.cannontech.amr.deviceread.dao.DeviceAttributeReadError;
import com.cannontech.amr.deviceread.dao.DeviceAttributeReadErrorType;
import com.cannontech.amr.rfn.dao.RfnDeviceDao;
import com.cannontech.amr.rfn.message.read.RfnMeterReadingDataReplyType;
import com.cannontech.amr.rfn.message.read.RfnMeterReadingReplyType;
import com.cannontech.amr.rfn.model.RfnMeter;
import com.cannontech.amr.rfn.service.RfnDeviceReadCompletionCallback;
import com.cannontech.amr.rfn.service.RfnMeterReadService;
import com.cannontech.common.device.DeviceRequestType;
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
import com.cannontech.core.dynamic.PointValueHolder;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.dr.rfn.message.unicast.RfnExpressComUnicastDataReplyType;
import com.cannontech.dr.rfn.message.unicast.RfnExpressComUnicastReplyType;
import com.cannontech.dr.rfn.service.RfnExpressComMessageService;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

public class DeviceAttributeReadRfnStrategy implements DeviceAttributeReadStrategy {
    
    @Autowired private PaoDefinitionDao paoDefinitionDao;
    @Autowired private RfnMeterReadService rfnMeterReadService;
    @Autowired private RfnDeviceDao rfnDeviceDao;
    @Autowired private RfnExpressComMessageService rfnExpressComMessageService;
    
    @Override
    public StrategyType getType() {
        return StrategyType.RF;
    }
    
    @Override
    public boolean canRead(PaoType paoType) {
        boolean result = paoDefinitionDao.isTagSupported(paoType, PaoTag.NETWORK_MANAGER_ATTRIBUTE_READS);
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
                    rfnMeter = rfnDeviceDao.getMeter(paoIdentifier); // TODO create bulk loader
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
        
        sendRequests(rfnMeters, delegateCallback);
        sendRequests(rfnDevices, delegateCallback);
    }
    
    private void sendRequests(List<? extends YukonDevice> devices, final DeviceAttributeReadStrategyCallback delegateCallback) {
        final AtomicInteger pendingRequests = new AtomicInteger(devices.size());

        for (final YukonDevice device : devices) {
            
            if (device.getPaoIdentifier().getPaoType().isMeter()) {
                RfnDeviceReadCompletionCallback<RfnMeterReadingReplyType, RfnMeterReadingDataReplyType> meterCallback = getCallback(device, delegateCallback, pendingRequests);
                rfnMeterReadService.send((RfnMeter) device, meterCallback);
            } else {
                RfnDeviceReadCompletionCallback<RfnExpressComUnicastReplyType, RfnExpressComUnicastDataReplyType> deviceCallback = getCallback(device, delegateCallback, pendingRequests);
                rfnExpressComMessageService.readDevice((RfnDevice) device, deviceCallback);
            }
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
    
}