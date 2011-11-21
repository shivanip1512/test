package com.cannontech.amr.rfn.service.processor;

import java.util.List;

import com.cannontech.amr.rfn.message.event.RfnConditionType;
import com.cannontech.amr.rfn.message.event.RfnEvent;
import com.cannontech.amr.rfn.model.RfnMeter;
import com.cannontech.message.dispatch.message.PointData;

public interface RfnArchiveRequestProcessorBase {

    /**
     * Process the Rfn Event/Alarm. New PointData objects added to this pointDatas list 
     * will be sent to Dispatch for archiving.
     */
    public <T extends RfnEvent> void process(RfnMeter meter, T event, List<? super PointData> pointDatas);
    public RfnConditionType getRfnConditionType();

}
