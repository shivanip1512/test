package com.cannontech.amr.deviceread.dao.impl;

import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;

import com.cannontech.amr.deviceread.dao.DeviceAttributeReadCallback;
import com.cannontech.amr.deviceread.dao.DeviceAttributeReadError;
import com.cannontech.amr.deviceread.dao.DeviceAttributeReadErrorType;
import com.cannontech.amr.rfn.message.read.RfnMeterReadingDataReplyType;
import com.cannontech.amr.rfn.message.read.RfnMeterReadingReplyType;
import com.cannontech.amr.rfn.model.RfnMeter;
import com.cannontech.amr.rfn.service.RfnMeterReadCompletionCallback;
import com.cannontech.amr.rfn.service.RfnMeterReadService;
import com.cannontech.amr.rfn.dao.RfnMeterDao;
import com.cannontech.common.device.DeviceRequestType;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDao;
import com.cannontech.common.pao.definition.model.PaoTag;
import com.cannontech.common.util.IterableUtils;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dynamic.PointValueHolder;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class DeviceAttributeReadRfnStrategy implements DeviceAttributeReadStrategy {
    private AttributeService attributeService;
    private PaoDefinitionDao paoDefinitionDao;
    private RfnMeterReadService rfnMeterReadService;
    private RfnMeterDao rfnMeterDao;
    
    @Override
    public DeviceAttributeReadStrategyType getType() {
        return DeviceAttributeReadStrategyType.RF;
    }
    
    @Override
    public boolean canRead(PaoType paoType) {
        boolean result = paoDefinitionDao.isTagSupported(paoType, PaoTag.NETWORK_MANAGER_ATTRIBUTE_READS);
        return result;
    }
    
    @Override
    public boolean isReadable(Iterable<? extends YukonPao> devices, Set<Attribute> attributes, LiteYukonUser user) {
        // There's only one command we can send for an Eka meter and it will return everything,
        // so we'll just check if at least one of the attributes requested exists.
        
        // TODO consider just returning true under the assumption that this was already checked prior to calling
        
        for (YukonPao device : devices) {
            Set<Attribute> allExistingAttributes = attributeService.getAllExistingAttributes(device);
            boolean noMatch = Sets.intersection(attributes, allExistingAttributes).isEmpty();
            if (!noMatch) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void initiateRead(Iterable<? extends YukonPao> devices,
            Set<? extends Attribute> attributes, final DeviceAttributeReadCallback delegateCallback,
            DeviceRequestType type, LiteYukonUser user) {
        
        List<RfnMeter> rfnMeters = Lists.newArrayListWithExpectedSize(IterableUtils.guessSize(devices));
        for (YukonPao yukonPao : devices) {
            RfnMeter rfnMeter;
            try {
                rfnMeter = rfnMeterDao.getMeter(yukonPao);
                rfnMeters.add(rfnMeter);
            } catch (NotFoundException e) {
                MessageSourceResolvable summary = YukonMessageSourceResolvable.createSingleCodeWithArguments("yukon.common.device.attributeRead.rfn.notKnown", e.getMessage());
                DeviceAttributeReadError lookupError = new DeviceAttributeReadError(DeviceAttributeReadErrorType.UNKNOWN, summary);
                delegateCallback.receivedError(yukonPao.getPaoIdentifier(), lookupError);
            }
        }
        
        if (rfnMeters.isEmpty()) {
            delegateCallback.complete();
            return;
        }
        
        final AtomicInteger pendingRequests = new AtomicInteger(rfnMeters.size());

        for (final RfnMeter rfnMeter : rfnMeters) {

            RfnMeterReadCompletionCallback callback = new RfnMeterReadCompletionCallback() {

                @Override
                public void receivedData(PointValueHolder value) {
                    delegateCallback.receivedValue(rfnMeter.getPaoIdentifier(), value);
                }
                
                @Override
                public void receivedDataError(RfnMeterReadingDataReplyType replyType) {
                    MessageSourceResolvable summary = YukonMessageSourceResolvable.createSingleCodeWithArguments("yukon.common.device.attributeRead.rfn.dataError", replyType.toString());
                    DeviceAttributeReadError dataError = new DeviceAttributeReadError(DeviceAttributeReadErrorType.COMMUNICATION, summary);
                    delegateCallback.receivedError(rfnMeter.getPaoIdentifier(), dataError);
                }
                
                @Override
                public void receivedStatusError(RfnMeterReadingReplyType replyType) {
                    MessageSourceResolvable summary = YukonMessageSourceResolvable.createSingleCodeWithArguments("yukon.common.device.attributeRead.rfn.statusError", replyType.toString());
                    DeviceAttributeReadError dataError = new DeviceAttributeReadError(DeviceAttributeReadErrorType.UNKNOWN, summary);
                    delegateCallback.receivedError(rfnMeter.getPaoIdentifier(), dataError);
                }
                
                @Override
                public void receivedStatus(RfnMeterReadingReplyType status) {
                }
                
                @Override
                public void processingExceptionOccured(String message) {
                    MessageSourceResolvable summary = YukonMessageSourceResolvable.createSingleCodeWithArguments("yukon.common.device.attributeRead.rfn.exception", message);
                    DeviceAttributeReadError exception = new DeviceAttributeReadError(DeviceAttributeReadErrorType.EXCEPTION, summary);
                    delegateCallback.receivedException(exception);
                }

                @Override
                public void complete() {
                    int remaining = pendingRequests.decrementAndGet();
                    if (remaining == 0) {
                        delegateCallback.complete();
                    }
                }
            };

            rfnMeterReadService.send(rfnMeter.getMeterIdentifier(), callback);
        }
    }
    
    @Autowired
    public void setPaoDefinitionDao(PaoDefinitionDao paoDefinitionDao) {
        this.paoDefinitionDao = paoDefinitionDao;
    }
    
    @Autowired
    public void setRfnMeterDao(RfnMeterDao rfnMeterDao) {
        this.rfnMeterDao = rfnMeterDao;
    }
    
    @Autowired
    public void setRfnMeterReadService(RfnMeterReadService rfnMeterReadService) {
        this.rfnMeterReadService = rfnMeterReadService;
    }
    
    @Autowired
    public void setAttributeService(AttributeService attributeService) {
        this.attributeService = attributeService;
    }

}
