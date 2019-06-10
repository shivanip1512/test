package com.cannontech.dr.meterDisconnect;

import java.util.Collections;
import java.util.Set;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.StreamMessage;

import org.apache.logging.log4j.Logger;
import org.joda.time.DateTimeZone;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;

import com.cannontech.amr.meter.dao.MeterDao;
import com.cannontech.amr.meter.model.YukonMeter;
import com.cannontech.amr.rfn.message.disconnect.RfnMeterDisconnectConfirmationReplyType;
import com.cannontech.amr.rfn.message.disconnect.RfnMeterDisconnectState;
import com.cannontech.amr.rfn.message.disconnect.RfnMeterDisconnectStatusType;
import com.cannontech.amr.rfn.model.RfnMeter;
import com.cannontech.amr.rfn.service.RfnMeterDisconnectCallback;
import com.cannontech.amr.rfn.service.RfnMeterDisconnectService;
import com.cannontech.cc.service.GroupService;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.device.groups.service.DeviceGroupService;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.core.dynamic.PointValueQualityHolder;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.dr.service.ControlHistoryService;
import com.cannontech.dr.service.ControlType;
import com.cannontech.yukon.IDatabaseCache;

public class MeterDisconnectMessageListener {
    private static final Logger log = YukonLogManager.getLogger(MeterDisconnectMessageListener.class);
    
    @Autowired private ControlHistoryService controlHistoryService;
    @Autowired private RfnMeterDisconnectService rfnMeterDisconnectService;
    @Autowired private IDatabaseCache dbCache;
    @Autowired private DeviceGroupService deviceGroupService;
    @Autowired private GroupService groupService;
    @Autowired private MeterDao meterDao;

    public class MeterDisconnectCallback implements RfnMeterDisconnectCallback {

        @Override
        public void processingExceptionOccurred(MessageSourceResolvable message) {
            log.debug("RFN exception (RfnMeterDisconnectCallback)");
        }

        @Override
        public void complete() {
            log.debug("RFN (RfnMeterDisconnectCallback) completed");
        }

        @Override
        public void receivedSuccess(RfnMeterDisconnectState state,
                                    PointValueQualityHolder pointData) {
            log.debug("RFN receivedSuccess");
        }

        @Override
        public void receivedError(MessageSourceResolvable message, RfnMeterDisconnectState state,
                                  RfnMeterDisconnectConfirmationReplyType replyType) {
            log.debug("RFN receivedError");

        }

        
    }
    
    public void handleCyclingControlMessage(Message message) {
        if (message instanceof StreamMessage) {
            try {
                log.debug("Received message on yukon.notif.stream.dr.MeterDisconnectControlMessage queue.");
                StreamMessage msg = (StreamMessage) message;
                int groupId = msg.readInt();
                long utcStartTimeSeconds = msg.readLong();
                long utcEndTimeSeconds = msg.readLong();
                Instant startTime = new Instant(utcStartTimeSeconds * 1000);
                Instant endTime = new Instant(utcEndTimeSeconds * 1000);
                Duration controlDuration = new Duration(startTime, endTime);
                int controlDurationSeconds = controlDuration.toStandardSeconds().getSeconds();
                Instant startTimeUtc = new Instant(DateTimeZone.getDefault().convertLocalToUTC(startTime.getMillis(), false));
                
                
                log.debug("Parsed message - Group Id: " + groupId + ", startTime: " + startTime + ", endTime: " + endTime);
                                
                String groupName = groupService.getGroup(groupId).getName();
                DeviceGroup deviceGroup = deviceGroupService.findGroupName(groupName);
                if (deviceGroup != null) {
                    Set<SimpleDevice> meters = deviceGroupService.getDevices(Collections.singleton(deviceGroup));
                    Iterable<YukonMeter> yukonMeters = meterDao.getMetersForYukonPaos(meters);
                    yukonMeters.forEach(meter -> {
                        MeterDisconnectCallback callback = new MeterDisconnectCallback();
                        
                        rfnMeterDisconnectService.send((RfnMeter) meter, RfnMeterDisconnectStatusType.RESUME, callback);
                    });
                }
                

                controlHistoryService.sendControlHistoryShedMessage(groupId, startTimeUtc, ControlType.METER_DISCONNECT, null,
                    controlDurationSeconds, -9780);
            } catch (JMSException e) {
                log.error("Error parsing Meter Disconnect control message from LM", e);
            }
        }
    }
    
    public void handleRestoreMessage(Message message) {
        if (message instanceof StreamMessage) {
            try {
                log.debug("Received message on yukon.notif.stream.dr.MeterDisconnectRestoreMessage queue.");
                StreamMessage msg = (StreamMessage) message;
                int groupId = msg.readInt();
                long restoreTime = msg.readLong();
                
                log.debug("Parsed: Group Id: " + groupId + ", Restore Time: " + restoreTime);
                
                LiteYukonPAObject group = dbCache.getAllLMGroups().stream()
                        .filter(g -> g.getLiteID() == groupId).findAny().orElse(null);
                if (group == null) {
                    log.error("Group with id " + groupId + " is not found");
                    return;
                }

                String groupName = groupService.getGroup(groupId).getName();
                DeviceGroup deviceGroup = deviceGroupService.findGroupName(groupName);
                if (deviceGroup != null) {
                    Set<SimpleDevice> meters = deviceGroupService.getDevices(Collections.singleton(deviceGroup));
                    Iterable<YukonMeter> yukonMeters = meterDao.getMetersForYukonPaos(meters);
                    yukonMeters.forEach(meter -> {
                        MeterDisconnectCallback callback = new MeterDisconnectCallback();
                        
                        rfnMeterDisconnectService.send((RfnMeter) meter, RfnMeterDisconnectStatusType.TERMINATE, callback);
                    });
                }
                controlHistoryService.sendControlHistoryRestoreMessage(groupId, Instant.now());
            } catch (JMSException e) {
                log.error("Error parsing Meter Disconnect restore message from LM", e);
                return;
            }
        }
    }

}
