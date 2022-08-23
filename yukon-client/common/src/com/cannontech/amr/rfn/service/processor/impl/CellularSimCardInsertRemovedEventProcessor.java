package com.cannontech.amr.rfn.service.processor.impl;

import java.util.List;

import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.rfn.message.event.RfnConditionDataType;
import com.cannontech.amr.rfn.message.event.RfnConditionType;
import com.cannontech.amr.rfn.message.event.RfnEvent;
import com.cannontech.amr.rfn.service.RfnMeterEventService;
import com.cannontech.amr.rfn.service.processor.RfnArchiveRequestProcessor;
import com.cannontech.amr.rfn.service.processor.RfnEventConditionDataProcessorHelper;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.rfn.model.RfnDevice;
import com.cannontech.database.db.point.stategroup.InsertedRemoved;
import com.cannontech.message.dispatch.message.PointData;

public class CellularSimCardInsertRemovedEventProcessor extends RfnEventConditionDataProcessorHelper implements RfnArchiveRequestProcessor {

    @Autowired RfnMeterEventService rfnMeterEventService;
    
    @Override
    public void process(RfnDevice device, RfnEvent event, List<? super PointData> pointDatas, Instant now) {
        var insertedRemovedValue = InsertedRemoved.getForAnalogValue((Short) getEventDataWithType(event, RfnConditionDataType.SIM_CARD_STATUS)); // Ensure that this value is the expected value from NM
        rfnMeterEventService.processAttributePointData(device, pointDatas, BuiltInAttribute.CELLULAR_SIM_CARD_INSERTED_REMOVED, new Instant(event.getTimeStamp()), insertedRemovedValue.getRawState(), now);
    }

    @Override
    public RfnConditionType getRfnConditionType() {
        return RfnConditionType.CELLULAR_SIM_CARD_INSERTED_REMOVED;
    }

}
