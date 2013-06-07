package com.cannontech.amr.rfn.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.rfn.message.event.RfnConditionDataType;
import com.cannontech.amr.rfn.message.event.RfnConditionType;
import com.cannontech.amr.rfn.message.event.RfnEvent;
import com.cannontech.amr.rfn.service.processor.RfnArchiveRequestProcessor;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.common.point.PointQuality;
import com.cannontech.common.rfn.model.RfnDevice;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.db.point.stategroup.EventStatus;
import com.cannontech.messaging.message.dispatch.PointDataMessage;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

public class RfnMeterEventService {

    private static final Logger log = YukonLogManager.getLogger(RfnMeterEventService.class);

    @Autowired private AttributeService attributeService;
    @Autowired private List<RfnArchiveRequestProcessor> processors;

    private Map<RfnConditionType, RfnArchiveRequestProcessor> processorsMap;
    private Map<Boolean, Integer> clearedStateMap = ImmutableMap.of(Boolean.TRUE, EventStatus.CLEARED.getRawState(),
                                                                    Boolean.FALSE, EventStatus.ACTIVE.getRawState());
    
    @PostConstruct
    public void initialize() {
        // Build up our map of processors
        processorsMap = Maps.newHashMap();
        for (RfnArchiveRequestProcessor processor : processors) {
            processorsMap.put(processor.getRfnConditionType(), processor);
        }
    }
    
    /**
     * Process our event/alarm by first adding pointdata for our event type (i.e. the corresponding "Event Status"
     * status point), then continue on to our more specific processor
     */
    public <T extends RfnEvent> void processEvent(RfnDevice device, T event, List<? super PointDataMessage> pointDatas) {
        log.debug("Event Recieved - event: " + event + " Meter: " + device);

        boolean handledStatusEvent = handleRfnEventStatusEvents(device, event, pointDatas);
        RfnArchiveRequestProcessor processor = processorsMap.get(event.getType());
        if (processor != null) {
            processor.process(device, event, pointDatas);
        } else if (handledStatusEvent == false){
            String className = event.getClass().getSimpleName();
            log.debug(className + " of type " + event.getType() + " is not currently supported");
        }
    }
    
    private <T extends RfnEvent> boolean handleRfnEventStatusEvents(RfnDevice meter, T event,
                                                                 List<? super PointDataMessage> pointDatas) {
        try {
            BuiltInAttribute eventAttr = BuiltInAttribute.valueOf(event.getType().name());
            if (eventAttr.isRfnEventStatusType()) {
                int rawEventStatusState = getRawClearedStateForEvent(event);
                processAttributePointData(meter, pointDatas, eventAttr, event.getTimeStamp(), rawEventStatusState);
                return true;
            }
        } catch (IllegalArgumentException e) {
            // event type is OUTAGE or RESTORE, which don't map to Attributes. That's fine.
        }
        return false;
    }
    
    public <T extends RfnEvent> int getRawClearedStateForEvent(T event) {
        Map<RfnConditionDataType, Object> eventData = event.getEventData();
        Boolean cleared = false;
        if (eventData != null) {
            Object thing = eventData.get(RfnConditionDataType.CLEARED);
            if (thing != null) cleared = (Boolean)thing;
        }
        int rawEventStatusState = clearedStateMap.get(cleared);
        return rawEventStatusState;
    }
    
    /**
     * Creates a point for the passed in Attribute if one doesn't exist, then gets that point
     * so we can properly build up a pointData object, which then gets assigned to our
     * passed in List of pointDatas. This method uses a PointQuality of Normal
     */
    public void processAttributePointData(RfnDevice rfnDevice,
                                          List<? super PointDataMessage> pointDatas,
                                          BuiltInAttribute attr,
                                          long timestamp,
                                          double pointValue) {
        processAttributePointData(rfnDevice, pointDatas, attr, timestamp, pointValue, PointQuality.Normal);
    }

    /**
     * Creates a point for the passed in Attribute if one doesn't exist, then gets that point
     * so we can properly build up a pointData object, which then gets assigned to our
     * passed in List of pointDatas.
     */
    public void processAttributePointData(RfnDevice rfnDevice,
                                          List<? super PointDataMessage> pointDatas,
                                          BuiltInAttribute attr,
                                          long timestamp,
                                          double pointValue,
                                          PointQuality quality) {
        // create our attribute point if it doesn't exist yet
        attributeService.createPointForAttribute(rfnDevice, attr);

        LitePoint litePoint = attributeService.getPointForAttribute(rfnDevice, attr);

        PointDataMessage pointData = new PointDataMessage();
        pointData.setId(litePoint.getPointID());
        pointData.setTime(new Date(timestamp));
        pointData.setPointQuality(quality);
        pointData.setType(litePoint.getPointTypeEnum().getPointTypeId());
        pointData.setValue(pointValue);
        pointData.setTagsPointMustArchive(true);
        pointDatas.add(pointData);
    }

}