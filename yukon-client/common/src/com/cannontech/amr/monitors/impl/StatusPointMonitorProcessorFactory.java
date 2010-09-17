package com.cannontech.amr.monitors.impl;

import java.util.Date;
import java.util.List;

import javax.jms.ConnectionFactory;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;

import com.cannontech.amr.monitors.message.OutageJmsMessage;
import com.cannontech.amr.statusPointProcessing.dao.StatusPointMonitorDao;
import com.cannontech.amr.statusPointProcessing.model.StatusPointMonitor;
import com.cannontech.amr.statusPointProcessing.model.StatusPointMonitorMessageProcessor;
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
                SimpleDevice simpleDevice = new SimpleDevice(paoIdentifier);
                
                DeviceGroup groupToMonitor = deviceGroupService.resolveGroupName(statusPointMonitor.getGroupName());
                boolean deviceInGroup = deviceGroupService.isDeviceInGroup(groupToMonitor, simpleDevice);
                if (!deviceInGroup) {
                    return;
                }
                
                //check to make sure this point is a status point
                PointType pointType = richPointData.getPaoPointIdentifier().getPointIdentifier().getPointType(); 
                if (!pointType.isStatus()) {
                    return;
                }

                // RichPointData matches the attribute we're looking for?
                if (attributeService.isPointAttribute(richPointData.getPaoPointIdentifier(), statusPointMonitor.getAttribute())) {
                    PointValueHolder currentValue = richPointData.getPointValue();
                    
                    LitePoint litePoint = pointDao.getLitePoint(richPointData.getPointValue().getId());
                    
                    // point's StateGroup matches the StateGroup we are monitoring?
                    if (litePoint.getStateGroupID() == statusPointMonitor.getStateGroup().getStateGroupID()) {
                        
                        log.debug("Point " + richPointData.getPaoPointIdentifier() + " caught by Status Point Monitor: " + statusPointMonitor + " with value: " + currentValue);
                        
                        for (StatusPointMonitorMessageProcessor statusPointMonitorMessageProcessor : statusPointMonitor.getStatusPointMonitorMessageProcessors()) {
                            
                            boolean needPreviousValue = needPreviousValue(statusPointMonitorMessageProcessor);
                            PointValueHolder previousValue = null;
                            if (needPreviousValue) {
                                previousValue = getPreviousValueForPoint(currentValue);
                                log.debug("Previous value of point:" + previousValue.getId() + " is " + previousValue.getValue());
                            }
                            
                            boolean shouldSendMessage = shouldSendMessage(statusPointMonitorMessageProcessor, currentValue, previousValue);

                            if (shouldSendMessage) {
                                OutageJmsMessage outageJmsMessage = new OutageJmsMessage();
                                outageJmsMessage.setSource(statusPointMonitor.getStatusPointMonitorName());
                                outageJmsMessage.setActionType(statusPointMonitorMessageProcessor.getActionTypeEnum());
                                outageJmsMessage.setPaoIdentifier(richPointData.getPaoPointIdentifier().getPaoIdentifier());
                                outageJmsMessage.setPointValueQualityHolder(richPointData.getPointValue());
                                
                                log.debug("Outage message pushed to jms queue: " + outageJmsMessage);
                                jmsTemplate.convertAndSend("yukon.notif.obj.amr.OutageJmsMessage", outageJmsMessage);
                            }
                        }
                    }
                }
            }
        };

        return richPointDataListener;
    }
    
    public static boolean needPreviousValue(StatusPointMonitorMessageProcessor processor) {
        boolean prevDontCare = processor.getPrevStateType().isDontCare();
        boolean nextDontCare = processor.getNextStateType().isDontCare();
        boolean nextExact = processor.getNextStateType().isExact();
        
        //Don't hit the database for last value if we don't have to
        if (prevDontCare && (nextDontCare || nextExact)) {
            return false;
        }
        return true;
    }
    
    public static boolean shouldSendMessage(StatusPointMonitorMessageProcessor processor, 
                                             PointValueHolder currentPointValue, PointValueHolder prevPointValue) {
        log.debug(processor);
        boolean shouldSendMessage = false;
        
        //prev states
        boolean prevDontCare = processor.getPrevStateType().isDontCare();
        boolean prevDiff = processor.getPrevStateType().isDifference();
        boolean prevExact = processor.getPrevStateType().isExact();
        
        //next states
        boolean currentDontCare = processor.getNextStateType().isDontCare();
        boolean currentDiff = processor.getNextStateType().isDifference();
        boolean currentExact = processor.getNextStateType().isExact();
        
        try {
        //Send logic
        if (prevDontCare) {
            if (currentDontCare) {
                shouldSendMessage = true;
                log.debug("Send logic met: Previous State: " + processor.getPrevState() + " Next State: " + processor.getNextState());
            } else if (currentDiff) {
                shouldSendMessage = isDifference(currentPointValue, prevPointValue);
                log.debug("Send logic met: Previous State: " + processor.getPrevState() + " Next State: " + processor.getNextState());
            } else if (currentExact){
                //TODO:need to throw all of this in a try/catch or something
                shouldSendMessage = isExactMatch(processor.getNextStateInt(), currentPointValue);
                log.debug("Send logic met: Previous State: " + processor.getPrevState() + " Next State: " + processor.getNextState());
            }
        } else if (prevDiff) {
            if (currentDontCare) {
                shouldSendMessage = isDifference(currentPointValue, prevPointValue);
                log.debug("Send logic met: Previous State: " + processor.getPrevState() + " Next State: " + processor.getNextState());
            } else if (currentDiff) {
                shouldSendMessage = isDifference(currentPointValue, prevPointValue);
                log.debug("Send logic met: Previous State: " + processor.getPrevState() + " Next State: " + processor.getNextState());
            } else if (currentExact) {
                shouldSendMessage = (isDifference(currentPointValue, prevPointValue) && (isExactMatch(processor.getNextStateInt(), currentPointValue)));
                log.debug("Send logic met: Previous State: " + processor.getPrevState() + " Next State: " + processor.getNextState());
            }
        }
        else if (prevExact) {
            if (currentDontCare) {
                shouldSendMessage = isExactMatch(processor.getPrevStateInt(), prevPointValue);
                log.debug("Send logic met: Previous State: " + processor.getPrevState() + " Next State: " + processor.getNextState());
            } else if (currentDiff) {
                shouldSendMessage = (isDifference(currentPointValue, prevPointValue) && isExactMatch(processor.getPrevStateInt(), prevPointValue));
                log.debug("Send logic met: Previous State: " + processor.getPrevState() + " Next State: " + processor.getNextState());
            } else if (currentExact) {
                shouldSendMessage = (isExactMatch(processor.getPrevStateInt(), prevPointValue) && isExactMatch(processor.getNextStateInt(), currentPointValue));
                log.debug("Send logic met: Previous State: " + processor.getPrevState() + " Next State: " + processor.getNextState());
            }
        }
        } catch(NumberFormatException ignore) {
            //Caught this exception when trying to convert a non-int state type to an int. Just ignore this.
        }
        
        return shouldSendMessage;
    }
    
    public static boolean isDifference(PointValueHolder currentPointValue, PointValueHolder prevPointValue) {
        if (prevPointValue == null) {
            return true;
        }
        return currentPointValue.getValue() != prevPointValue.getValue();
    }
    
    public static boolean isExactMatch(int processorPointValue, PointValueHolder pointValue) {
        if (pointValue == null) {
            return false;
        }
        return processorPointValue == (int)pointValue.getValue();
    }

    private PointValueHolder getPreviousValueForPoint(PointValueHolder pointValueQualityHolder) {
        Date currentTimeStamp = pointValueQualityHolder.getPointDataTimeStamp();
        int pointId = pointValueQualityHolder.getId();
        List<PointValueHolder> pointPrevValueList = rawPointHistoryDao.getLimitedPointData(pointId, null, currentTimeStamp, Clusivity.INCLUSIVE_EXCLUSIVE, Order.REVERSE, 1);
        
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