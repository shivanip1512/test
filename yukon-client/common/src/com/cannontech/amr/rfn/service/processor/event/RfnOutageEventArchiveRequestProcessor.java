package com.cannontech.amr.rfn.service.processor.event;

import java.util.List;

import com.cannontech.amr.rfn.message.event.RfnConditionDataType;
import com.cannontech.amr.rfn.message.event.RfnConditionType;
import com.cannontech.amr.rfn.message.event.RfnEvent;
import com.cannontech.amr.rfn.model.RfnMeter;
import com.cannontech.amr.rfn.service.processor.RfnArchiveRequestProcessorBase;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.message.dispatch.message.PointData;

public class RfnOutageEventArchiveRequestProcessor extends RfnEventConditionDataProcessorHelper
        implements RfnArchiveRequestProcessorBase {
    
    @Override
    public <T extends RfnEvent> void process(RfnMeter meter, T event, List<? super PointData> pointDatas) {
        rfnMeterEventService.processAttributePointData(meter, pointDatas, BuiltInAttribute.OUTAGE_STATUS, 2); // 2 = bad (outage)
        
        Long count = (Long) getEventDataWithType(event, RfnConditionDataType.COUNT);
        rfnMeterEventService.processAttributePointData(meter, pointDatas, BuiltInAttribute.RFN_OUTAGE_COUNT, (Long) count);
    }
    
    @Override
    public RfnConditionType getRfnConditionType() {
        return RfnConditionType.OUTAGE;
    }
    
}
