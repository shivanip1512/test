package com.cannontech.amr.rfn.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.Logger;
import org.joda.time.Instant;
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
import com.cannontech.message.dispatch.message.PointData;
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
        processorsMap = Maps.uniqueIndex(processors, RfnArchiveRequestProcessor::getRfnConditionType);
    }
    
    /**
     * Process our event/alarm by first adding pointdata for our event type (i.e. the corresponding "Event Status"
     * status point), then continue on to our more specific processor
     */
    public void processEvent(RfnDevice device, RfnEvent event, List<? super PointData> pointDatas) {
        log.debug("Event Received - event: " + event + " Meter: " + device);

        Instant now = Instant.now();
        
        boolean handledStatusEvent = handleRfnEventStatusEvents(device, event, pointDatas, now);
        RfnArchiveRequestProcessor processor = processorsMap.get(event.getType());
        if (processor != null) {
            processor.process(device, event, pointDatas, now);
        } else if (handledStatusEvent == false){
            String className = event.getClass().getSimpleName();
            log.debug(className + " of type " + event.getType() + " is not currently supported");
        }
    }
    
    private boolean handleRfnEventStatusEvents(RfnDevice meter, RfnEvent event,
                                               List<? super PointData> pointDatas, Instant now) {
        try {
            BuiltInAttribute eventAttr = BuiltInAttribute.valueOf(event.getType().name());
            if (eventAttr.isStatusType()) {
                int rawEventStatusState = getRawClearedStateForEvent(event);
                processAttributePointData(meter, pointDatas, eventAttr, new Instant(event.getTimeStamp()), rawEventStatusState, now);
                return true;
            }
        } catch (IllegalArgumentException e) {
            // event type is OUTAGE or RESTORE, which don't map to Attributes. That's fine.
        }
        return false;
    }
    
    public int getRawClearedStateForEvent(RfnEvent event) {
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
                                          List<? super PointData> pointDatas,
                                          BuiltInAttribute attr,
                                          Instant timestamp,
                                          double pointValue,
                                          Instant now) {
        processAttributePointData(rfnDevice, pointDatas, attr, timestamp, pointValue, PointQuality.Normal, now);
    }

    /**
     * Creates a point for the passed in Attribute if one doesn't exist, then gets that point
     * so we can properly build up a pointData object, which then gets assigned to our
     * passed in List of pointDatas.
     */
    public void processAttributePointData(RfnDevice rfnDevice,
                                          List<? super PointData> pointDatas,
                                          BuiltInAttribute attr,
                                          Instant timestamp,
                                          double pointValue,
                                          PointQuality quality,
                                          Instant now) {
        // create our attribute point if it doesn't exist yet
        attributeService.createPointForAttribute(rfnDevice, attr);

        LitePoint litePoint = attributeService.getPointForAttribute(rfnDevice, attr);

        PointData pointData = new PointData();
        pointData.setId(litePoint.getPointID());
        pointData.setTime(new Date(timestamp.getMillis()));
        pointData.setPointQuality(quality);
        pointData.setType(litePoint.getPointTypeEnum().getPointTypeId());
        pointData.setValue(pointValue);
        pointData.setTagsPointMustArchive(true);

        if (RfnDataValidator.isTimestampRecent(timestamp, now)) {
            pointDatas.add(pointData);
        } else {
            log.warn("Timestamp invalid or old, discarding pointdata for " + rfnDevice + " " + attr + ": " + pointData);
        }        
    }
}