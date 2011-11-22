package com.cannontech.amr.monitors.impl;

import java.util.Date;
import java.util.List;

import javax.jms.ConnectionFactory;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;

import com.cannontech.amr.monitors.message.OutageJmsMessage;
import com.cannontech.amr.statusPointMonitoring.dao.StatusPointMonitorDao;
import com.cannontech.amr.statusPointMonitoring.model.StatusPointMonitor;
import com.cannontech.amr.statusPointMonitoring.model.StatusPointMonitorProcessor;
import com.cannontech.clientutils.LogHelper;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.device.groups.service.DeviceGroupService;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.pao.PaoCategory;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.core.dao.PointDao;
import com.cannontech.core.dao.RawPointHistoryDao;
import com.cannontech.core.dao.RawPointHistoryDao.Clusivity;
import com.cannontech.core.dao.RawPointHistoryDao.Order;
import com.cannontech.core.dynamic.PointValueHolder;
import com.cannontech.core.dynamic.RichPointData;
import com.cannontech.core.dynamic.RichPointDataListener;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.point.PointType;

public class StatusPointMonitorProcessorFactory extends MonitorProcessorFactoryBase<StatusPointMonitor> {

    private static final Logger log = YukonLogManager.getLogger(StatusPointMonitorProcessorFactory.class);
    private AttributeService attributeService;
    private StatusPointMonitorDao statusPointMonitorDao;
    private RawPointHistoryDao rawPointHistoryDao;
    private PointDao pointDao;
    private JmsTemplate jmsTemplate;
    private DeviceGroupService deviceGroupService;

    @Override
    protected List<StatusPointMonitor> getAllMonitors() {
        return statusPointMonitorDao.getAllStatusPointMonitors();
    }

    protected RichPointDataListener createPointListener(final StatusPointMonitor statusPointMonitor) {
        
        RichPointDataListener richPointDataListener = new RichPointDataListener() {

            @Override
            public void pointDataReceived(RichPointData richPointData) {

                PaoIdentifier paoIdentifier = richPointData.getPaoPointIdentifier().getPaoIdentifier();
                if (paoIdentifier.getPaoType().getPaoCategory() != PaoCategory.DEVICE) {
                    // non devices can't be in groups
                    return;
                }
                
                //check to make sure this point is a status point
                PointType pointType = richPointData.getPaoPointIdentifier().getPointIdentifier().getPointType(); 
                if (!pointType.isStatus()) {
                    return;
                }
                
                DeviceGroup groupToMonitor = deviceGroupService.findGroupName(statusPointMonitor.getGroupName());
                if (groupToMonitor == null) {
                	// group does not exist, have nothing to monitor
                	return;
                }

                SimpleDevice simpleDevice = new SimpleDevice(paoIdentifier);
                boolean deviceInGroup = deviceGroupService.isDeviceInGroup(groupToMonitor, simpleDevice);
                if (!deviceInGroup) {
                    return;
                }

                // RichPointData matches the attribute we're looking for?
                if (!attributeService.isPointAttribute(richPointData.getPaoPointIdentifier(), statusPointMonitor.getAttribute())) {
                    return;
                }
                
                LitePoint litePoint = pointDao.getLitePoint(richPointData.getPointValue().getId());
                
                // point's StateGroup matches the StateGroup we are monitoring?
                if (!(litePoint.getStateGroupID() == statusPointMonitor.getStateGroup().getStateGroupID())) {
                    return;
                }
                
                PointValueHolder nextValue = richPointData.getPointValue();
                PointValueHolder previousValue = null; // store this outside the loop because it is valid for every processor 
                
                LogHelper.debug(log, "Point %s caught by Status Point Monitor: %s with value: %s", richPointData.getPaoPointIdentifier(), statusPointMonitor, nextValue);
                
                for (StatusPointMonitorProcessor statusPointMonitorProcessor : statusPointMonitor.getProcessors()) {
                    
                    boolean needPreviousValue = previousValue == null && needPreviousValue(statusPointMonitorProcessor); 
                    if (needPreviousValue) {
                        previousValue = getPreviousValueForPoint(nextValue);
                    }
                    
                    boolean shouldSendMessage = shouldSendMessage(statusPointMonitorProcessor, nextValue, previousValue);

                    if (shouldSendMessage) {
                        OutageJmsMessage outageJmsMessage = new OutageJmsMessage();
                        outageJmsMessage.setSource(statusPointMonitor.getStatusPointMonitorName());
                        outageJmsMessage.setActionType(statusPointMonitorProcessor.getActionTypeEnum());
                        outageJmsMessage.setPaoIdentifier(richPointData.getPaoPointIdentifier().getPaoIdentifier());
                        outageJmsMessage.setPointValueQualityHolder(richPointData.getPointValue());
                        
                        log.debug("Outage message pushed to jms queue: " + outageJmsMessage);
                        jmsTemplate.convertAndSend("yukon.notif.obj.amr.OutageJmsMessage", outageJmsMessage);
                        break; // once we've found a match, stop evaluating processors
                    }
                }
            }
        };

        return richPointDataListener;
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
            return true;
        }
        return nextPointValue.getValue() != prevPointValue.getValue();
    }
    
    public static boolean isExactMatch(int processorPointValue, PointValueHolder pointValue) {
        
        //Safety check for points that don't have a previous value yet in the database
        if (pointValue == null) {
            return false;
        }
        int pointValueAsInt = (int)pointValue.getValue();
        return processorPointValue == pointValueAsInt;
    }

    private PointValueHolder getPreviousValueForPoint(PointValueHolder pointValueQualityHolder) {
        Date nextTimeStamp = pointValueQualityHolder.getPointDataTimeStamp();
        int pointId = pointValueQualityHolder.getId();
        List<PointValueHolder> pointPrevValueList = rawPointHistoryDao.getLimitedPointData(pointId, null, nextTimeStamp, Clusivity.INCLUSIVE_EXCLUSIVE, Order.REVERSE, 1);
        
        if (pointPrevValueList.size() > 0) { 
            PointValueHolder pointValuePrev = pointPrevValueList.get(0);
            return pointValuePrev;
        } else {
            return null;
        }
    }

    @Autowired
    public void setStatusPointMonitorDao(StatusPointMonitorDao statusPointMonitorDao) {
        this.statusPointMonitorDao = statusPointMonitorDao;
    }
    
    @Autowired
    public void setAttributeService(AttributeService attributeService) {
        this.attributeService = attributeService;
    }
    
    @Autowired
    public void setRawPointHistoryDao(RawPointHistoryDao rawPointHistoryDao) {
        this.rawPointHistoryDao = rawPointHistoryDao;
    }
    
    @Autowired
    public void setPointDao(PointDao pointDao) {
        this.pointDao = pointDao;
    }
    
    @Autowired
    public void setConnectionFactory(ConnectionFactory connectionFactory) {
        jmsTemplate = new JmsTemplate(connectionFactory);
        jmsTemplate.setPubSubDomain(true);
    }
    
    @Autowired
    public void setDeviceGroupService(DeviceGroupService deviceGroupService) {
        this.deviceGroupService = deviceGroupService;
    }
}