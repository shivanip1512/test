package com.cannontech.amr.rfn.service.processor;

import java.util.List;

import org.joda.time.Instant;

import com.cannontech.amr.rfn.message.event.RfnConditionType;
import com.cannontech.amr.rfn.message.event.RfnEvent;
import com.cannontech.common.rfn.model.RfnDevice;
import com.cannontech.message.dispatch.message.PointData;

public interface RfnArchiveRequestProcessor {

    /**
     * Process the Rfn Event/Alarm. New PointData objects added to this pointDatas list 
     * will be sent to Dispatch for archiving.
     */
    public void process(RfnDevice device, RfnEvent event, List<? super PointData> pointDatas, Instant now);
    public RfnConditionType getRfnConditionType();
}
