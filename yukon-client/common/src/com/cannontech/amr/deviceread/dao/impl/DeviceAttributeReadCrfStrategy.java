package com.cannontech.amr.deviceread.dao.impl;

import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;

import com.cannontech.amr.crf.dao.CrfMeterDao;
import com.cannontech.amr.crf.message.CrfMeterReadingDataReplyType;
import com.cannontech.amr.crf.message.CrfMeterReadingReplyType;
import com.cannontech.amr.crf.model.CrfMeter;
import com.cannontech.amr.crf.service.CrfMeterReadCompletionCallback;
import com.cannontech.amr.crf.service.CrfMeterReadService;
import com.cannontech.amr.deviceread.dao.DeviceAttributeReadCallback;
import com.cannontech.amr.deviceread.dao.DeviceAttributeReadError;
import com.cannontech.amr.deviceread.dao.DeviceAttributeReadErrorType;
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

public class DeviceAttributeReadCrfStrategy implements DeviceAttributeReadStrategy {
    private AttributeService attributeService;
    private PaoDefinitionDao paoDefinitionDao;
    private CrfMeterReadService crfMeterReadService;
    private CrfMeterDao crfMeterDao;
    
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
        
        List<CrfMeter> crfMeters = Lists.newArrayListWithExpectedSize(IterableUtils.guessSize(devices));
        for (YukonPao yukonPao : devices) {
            CrfMeter crfMeter;
            try {
                crfMeter = crfMeterDao.getMeter(yukonPao);
                crfMeters.add(crfMeter);
            } catch (NotFoundException e) {
                MessageSourceResolvable summary = YukonMessageSourceResolvable.createSingleCodeWithArguments("yukon.common.device.attributeRead.crf.notKnown", e.getMessage());
                DeviceAttributeReadError lookupError = new DeviceAttributeReadError(DeviceAttributeReadErrorType.UNKNOWN, summary);
                delegateCallback.receivedError(yukonPao.getPaoIdentifier(), lookupError);
            }
        }
        
        if (crfMeters.isEmpty()) {
            delegateCallback.complete();
            return;
        }
        
        final AtomicInteger pendingRequests = new AtomicInteger(crfMeters.size());

        for (final CrfMeter crfMeter : crfMeters) {

            CrfMeterReadCompletionCallback callback = new CrfMeterReadCompletionCallback() {

                @Override
                public void receivedData(PointValueHolder value) {
                    delegateCallback.receivedValue(crfMeter.getPaoIdentifier(), value);
                }
                
                @Override
                public void receivedDataError(CrfMeterReadingDataReplyType replyType) {
                    MessageSourceResolvable summary = YukonMessageSourceResolvable.createSingleCodeWithArguments("yukon.common.device.attributeRead.crf.dataError", replyType.toString());
                    DeviceAttributeReadError dataError = new DeviceAttributeReadError(DeviceAttributeReadErrorType.COMMUNICATION, summary);
                    delegateCallback.receivedError(crfMeter.getPaoIdentifier(), dataError);
                }
                
                @Override
                public void receivedStatusError(CrfMeterReadingReplyType replyType) {
                    MessageSourceResolvable summary = YukonMessageSourceResolvable.createSingleCodeWithArguments("yukon.common.device.attributeRead.crf.statusError", replyType.toString());
                    DeviceAttributeReadError dataError = new DeviceAttributeReadError(DeviceAttributeReadErrorType.UNKNOWN, summary);
                    delegateCallback.receivedError(crfMeter.getPaoIdentifier(), dataError);
                }
                
                @Override
                public void receivedStatus(CrfMeterReadingReplyType status) {
                }
                
                @Override
                public void processingExceptionOccured(String message) {
                    MessageSourceResolvable summary = YukonMessageSourceResolvable.createSingleCodeWithArguments("yukon.common.device.attributeRead.crf.exception", message);
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

            crfMeterReadService.send(crfMeter.getMeterIdentifier(), callback);
        }
    }
    
    @Autowired
    public void setPaoDefinitionDao(PaoDefinitionDao paoDefinitionDao) {
        this.paoDefinitionDao = paoDefinitionDao;
    }
    
    @Autowired
    public void setCrfMeterDao(CrfMeterDao crfMeterDao) {
        this.crfMeterDao = crfMeterDao;
    }
    
    @Autowired
    public void setCrfMeterReadService(CrfMeterReadService crfMeterReadService) {
        this.crfMeterReadService = crfMeterReadService;
    }
    
    @Autowired
    public void setAttributeService(AttributeService attributeService) {
        this.attributeService = attributeService;
    }

}
