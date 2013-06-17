package com.cannontech.amr.rfn.service.processor.impl;

import java.util.List;

import com.cannontech.amr.rfn.message.event.RfnConditionDataType;
import com.cannontech.amr.rfn.message.event.RfnConditionType;
import com.cannontech.amr.rfn.message.event.RfnEvent;
import com.cannontech.amr.rfn.model.RfnInvalidValues;
import com.cannontech.amr.rfn.service.processor.RfnArchiveRequestProcessor;
import com.cannontech.amr.rfn.service.processor.RfnEventConditionDataProcessorHelper;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.point.PointQuality;
import com.cannontech.common.rfn.model.InvalidEventMessageException;
import com.cannontech.common.rfn.model.RfnDevice;
import com.cannontech.database.db.point.stategroup.OutageStatus;
import com.cannontech.message.dispatch.message.PointData;

public class RfnRestoreEventArchiveRequestProcessor extends RfnEventConditionDataProcessorHelper
        implements RfnArchiveRequestProcessor {
    
    @Override
    public <T extends RfnEvent> void process(RfnDevice device, T event, List<? super PointData> pointDatas) {
        rfnMeterEventService.processAttributePointData(device, pointDatas, BuiltInAttribute.OUTAGE_STATUS, event.getTimeStamp(), OutageStatus.GOOD.getRawState());
        
        Long durationInSeconds = RfnInvalidValues.OUTAGE_DURATION.getValue();
        PointQuality pointQuality = PointQuality.Unknown;
        try {
            Long start = (Long) getEventDataWithType(event, RfnConditionDataType.EVENT_START_TIME);
            Long end = event.getTimeStamp();
            durationInSeconds = (end - start) / 1000;
            pointQuality = PointQuality.Normal;
        } catch (InvalidEventMessageException e) {
            // Old firmware doesn't include the EVENT_START_TIME meta-data, so just use the invalid value set above for the duration if we get here
        }
        rfnMeterEventService.processAttributePointData(device, pointDatas, BuiltInAttribute.OUTAGE_LOG, event.getTimeStamp(), durationInSeconds, pointQuality);

        Long count = (Long) getEventDataWithType(event, RfnConditionDataType.COUNT);
        rfnMeterEventService.processAttributePointData(device, pointDatas, BuiltInAttribute.RFN_OUTAGE_RESTORE_COUNT, event.getTimeStamp(), count);
    }
    
    @Override
    public RfnConditionType getRfnConditionType() {
        return RfnConditionType.RESTORE;
    }
}
