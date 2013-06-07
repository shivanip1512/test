package com.cannontech.amr.rfn.service.processor;

import java.util.List;

import com.cannontech.amr.rfn.message.event.RfnConditionType;
import com.cannontech.amr.rfn.message.event.RfnEvent;
import com.cannontech.common.rfn.model.RfnDevice;
import com.cannontech.messaging.message.dispatch.PointDataMessage;

public interface RfnArchiveRequestProcessor {

    /**
     * Process the Rfn Event/Alarm. New PointData objects added to this pointDatas list 
     * will be sent to Dispatch for archiving.
     */
    public <T extends RfnEvent> void process(RfnDevice device, T event, List<? super PointDataMessage> pointDatas);
    public RfnConditionType getRfnConditionType();

}
