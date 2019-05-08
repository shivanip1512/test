package com.cannontech.amr.monitors.impl;

import static org.joda.time.DateTime.now;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.jms.ConnectionFactory;

import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;

import com.cannontech.amr.monitors.message.OutageJmsMessage;
import com.cannontech.amr.statusPointMonitoring.dao.StatusPointMonitorDao;
import com.cannontech.amr.statusPointMonitoring.model.StatusPointMonitor;
import com.cannontech.amr.statusPointMonitoring.model.StatusPointMonitorProcessor;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.device.groups.service.DeviceGroupService;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.pao.PaoCategory;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.Range;
import com.cannontech.core.dao.PointDao;
import com.cannontech.core.dao.RawPointHistoryDao;
import com.cannontech.core.dao.RawPointHistoryDao.Order;
import com.cannontech.core.dynamic.PointValueHolder;
import com.cannontech.core.dynamic.RichPointData;
import com.cannontech.core.dynamic.RichPointDataListener;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.point.PointType;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Iterables;

public class StatusPointMonitorProcessorFactory extends MonitorProcessorFactoryBase<StatusPointMonitor> {

    private static final Logger log = YukonLogManager.getLogger(StatusPointMonitorProcessorFactory.class);
    @Autowired private AttributeService attributeService;
    @Autowired private StatusPointMonitorDao statusPointMonitorDao;
    @Autowired private RawPointHistoryDao rawPointHistoryDao;
    @Autowired private PointDao pointDao;
    @Autowired private DeviceGroupService deviceGroupService;
    @Autowired private GlobalSettingDao globalSettingDao;
    private JmsTemplate jmsTemplate;
    private PointDataTrackingLogger trackingLogger = new PointDataTrackingLogger(log);
    private Cache<Integer, PointValueHolder> recentStatusPoints = CacheBuilder.newBuilder()
                       .expireAfterWrite(30, TimeUnit.SECONDS)
                       .build();

    @Override
    protected List<StatusPointMonitor> getAllMonitors() {
        return statusPointMonitorDao.getAllStatusPointMonitors();
    }

    private boolean isMonitoredData(final StatusPointMonitor statusPointMonitor, RichPointData richPointData) {
        int daysToIgnore = globalSettingDao.getInteger(GlobalSettingType.STATUS_POINT_MONITOR_NOTIFICATION_LIMIT);
        //Ignores date check if value is set to 0
        if (daysToIgnore > 0) {
            DateTime xDaysAgo = now().withTimeAtStartOfDay().minusDays(daysToIgnore);
            if (new DateTime(richPointData.getPointValue().getPointDataTimeStamp()).isBefore(xDaysAgo)) {
                // ignore the data that is older the X number of days
                return false;
            }
        }
      
        PaoIdentifier paoIdentifier = richPointData.getPaoPointIdentifier().getPaoIdentifier();
        if (paoIdentifier.getPaoType().getPaoCategory() != PaoCategory.DEVICE) {
            // non devices can't be in groups
            return false;
        }
        
        //check to make sure this point is a status point
        PointType pointType = richPointData.getPaoPointIdentifier().getPointIdentifier().getPointType(); 
        if (!pointType.isStatus()) {
            return false;
        }
        
        DeviceGroup groupToMonitor = deviceGroupService.findGroupName(statusPointMonitor.getGroupName());
        if (groupToMonitor == null) {
            // group does not exist, have nothing to monitor
            return false;
        }

        SimpleDevice simpleDevice = new SimpleDevice(paoIdentifier);
        boolean deviceInGroup = deviceGroupService.isDeviceInGroup(groupToMonitor, simpleDevice);
        if (!deviceInGroup) {
            return false;
        }

        // RichPointData matches the attribute we're looking for?
        if (!attributeService.isPointAttribute(richPointData.getPaoPointIdentifier(), statusPointMonitor.getAttribute())) {
            return false;
        }
        
        LitePoint litePoint = pointDao.getLitePoint(richPointData.getPointValue().getId());
        
        // point's StateGroup matches the StateGroup we are monitoring?
        if (litePoint.getStateGroupID() != statusPointMonitor.getStateGroup().getStateGroupID()) {
            return false;
        }
        
        return true;
    }

    @Override
    protected RichPointDataListener createPointListener(final StatusPointMonitor statusPointMonitor) {
        
        return richPointData -> {
            if (!isMonitoredData(statusPointMonitor, richPointData)) {
                trackingLogger.rejectId(richPointData);
                return;
            }
            
            trackingLogger.acceptId(richPointData);

            PointValueHolder nextValue = richPointData.getPointValue();
            PointValueHolder previousValue = null; // store this outside the loop because it is valid for every processor 
            
            PointValueHolder cachedValue = recentStatusPoints.getIfPresent(richPointData.getPointValue().getId());
            if (cachedValue == null || richPointData.getPointValue().getPointDataTimeStamp().after(
                cachedValue.getPointDataTimeStamp())) {
                // if this is the most recent value, cache it
                recentStatusPoints.put(nextValue.getId(), nextValue);
            }
            
            if (log.isDebugEnabled()) {
                log.debug("Point %s caught by Status Point Monitor: %s with value: %s", richPointData.getPaoPointIdentifier(), statusPointMonitor, nextValue);
            }
            
            for (StatusPointMonitorProcessor statusPointMonitorProcessor : statusPointMonitor.getProcessors()) {
                
                boolean needPreviousValue = previousValue == null && needPreviousValue(statusPointMonitorProcessor); 
                if (needPreviousValue) {
                    previousValue = getPreviousValueForPoint(nextValue);
                }
                
                boolean shouldSendMessage = shouldSendMessage(statusPointMonitorProcessor, nextValue, previousValue);

                if (shouldSendMessage) {                  
                    OutageJmsMessage outageJmsMessage = new OutageJmsMessage();
                    outageJmsMessage.setSource(statusPointMonitor.getName());
                    outageJmsMessage.setActionType(statusPointMonitorProcessor.getActionTypeEnum());
                    outageJmsMessage.setPaoIdentifier(richPointData.getPaoPointIdentifier().getPaoIdentifier());
                    outageJmsMessage.setPointValueQualityHolder(richPointData.getPointValue());
                    
                    log.debug("Outage message pushed to jms queue: " + outageJmsMessage);
                    jmsTemplate.convertAndSend("yukon.notif.obj.amr.OutageJmsMessage", outageJmsMessage);
                    break; // once we've found a match, stop evaluating processors
                }
            }
        };
    }
    
    public static boolean needPreviousValue(StatusPointMonitorProcessor processor) {
        boolean prevDontCare = processor.getPrevStateType().isDontCare();
        boolean nextDontCare = processor.getNextStateType().isDontCare();
        boolean nextExact = processor.getNextStateType().isExact();
        
        //Don't hit the database for last value if we don't have to
        if (prevDontCare && (nextDontCare || nextExact)) {
            return false;
        }
        return true;
    }
    
    public static boolean shouldSendMessage(StatusPointMonitorProcessor processor, 
                                             PointValueHolder nextPointValue, PointValueHolder prevPointValue) {
        boolean shouldSendMessage = false;
        
        //prev states
        boolean prevDontCare = processor.getPrevStateType().isDontCare();
        boolean prevDiff = processor.getPrevStateType().isDifference();
        boolean prevExact = processor.getPrevStateType().isExact();
        
        //next states
        boolean nextDontCare = processor.getNextStateType().isDontCare();
        boolean nextDiff = processor.getNextStateType().isDifference();
        
        try {
            //Send logic
            if (prevDontCare) {
                if (nextDontCare) {
                    shouldSendMessage = true;
                } else if (nextDiff) {
                    shouldSendMessage = isDifference(nextPointValue, prevPointValue);
                } else {
                    //nextState must be exact
                    shouldSendMessage = isExactMatch(processor.transientGetNextStateInt(), nextPointValue);
                }
            } else if (prevDiff) {
                if (nextDontCare) {
                    shouldSendMessage = isDifference(nextPointValue, prevPointValue);
                } else if (nextDiff) {
                    shouldSendMessage = isDifference(nextPointValue, prevPointValue);
                } else {
                    //nextState must be exact
                    shouldSendMessage = (isDifference(nextPointValue, prevPointValue) && (isExactMatch(processor.transientGetNextStateInt(), nextPointValue)));
                }
            }
            else if (prevExact) {
                if (nextDontCare) {
                    shouldSendMessage = isExactMatch(processor.transientGetPrevStateInt(), prevPointValue);
                } else if (nextDiff) {
                    shouldSendMessage = (isDifference(nextPointValue, prevPointValue) && isExactMatch(processor.transientGetPrevStateInt(), prevPointValue));
                } else {
                    //nextState must be exact
                    shouldSendMessage = (isExactMatch(processor.transientGetPrevStateInt(), prevPointValue) && isExactMatch(processor.transientGetNextStateInt(), nextPointValue));
                }
            }
        } catch(NumberFormatException e) {
            log.error("Caught exception when converting non-int state type to an int", e);
        }
        
        log.debug(processor);
        log.debug("Should Send Message: " + shouldSendMessage);
        log.debug("Next point value: " + nextPointValue.getValue() + " for pointId: " + nextPointValue.getId());
        
        if (prevPointValue != null) {
            log.debug("Previous point value: " + prevPointValue.getValue() + " for pointId: " + prevPointValue.getId());
        } else {
            log.debug("Previous point value was not retrieved because it was not needed.");
        }
        
        return shouldSendMessage;
    }
    
    public static boolean isDifference(PointValueHolder nextPointValue, PointValueHolder prevPointValue) {
        if (prevPointValue == null) {
            return true;    //previous point not existing
        }
        return nextPointValue.getValue() != prevPointValue.getValue();
    }
    
    public static boolean isExactMatch(int processorPointValue, PointValueHolder pointValue) {
        //Safety check for points that don't have a previous value yet in the database
        if (pointValue == null) {
            return false;   //should this change to true? If (prev) pointValue doesn't exist, then return true.
        }
        int pointValueAsInt = (int)pointValue.getValue();
        return processorPointValue == pointValueAsInt;
    }

    private PointValueHolder getPreviousValueForPoint(PointValueHolder nextValue) {
        Date nextTimeStamp = nextValue.getPointDataTimeStamp();
        int pointId = nextValue.getId();
        Range<Date> dateRange = new Range<>(null, true, nextTimeStamp, false);
		List<PointValueHolder> pointPrevValueList = rawPointHistoryDao.getLimitedPointData(pointId,dateRange.translate(CtiUtilities.INSTANT_FROM_DATE), false,
						Order.REVERSE, 1);
        PointValueHolder cachedValue = recentStatusPoints.getIfPresent(pointId);
        PointValueHolder rphValue = Iterables.getFirst(pointPrevValueList, null);
        if (cachedValue != null
            // next value is more recent that the value in cache
            && nextTimeStamp.after(cachedValue.getPointDataTimeStamp())
            // cached value is more recent then value in RPH
            && cachedValue.getPointDataTimeStamp().after(rphValue.getPointDataTimeStamp())) {
            return cachedValue;
        }

        return rphValue;
    }
    
    @Autowired
    public void setConnectionFactory(ConnectionFactory connectionFactory) {
        jmsTemplate = new JmsTemplate(connectionFactory);
        jmsTemplate.setPubSubDomain(true);
    }
}