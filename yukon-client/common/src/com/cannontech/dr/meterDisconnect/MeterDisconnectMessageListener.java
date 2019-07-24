package com.cannontech.dr.meterDisconnect;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.StreamMessage;

import org.apache.logging.log4j.Logger;
import org.joda.time.DateTimeZone;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;

import com.cannontech.amr.disconnect.model.DisconnectCommand;
import com.cannontech.amr.disconnect.service.DisconnectService;
import com.cannontech.cc.service.GroupService;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.bulk.collection.device.model.DeviceCollection;
import com.cannontech.common.bulk.collection.device.model.DeviceCollectionType;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.device.groups.service.DeviceGroupService;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.util.SimpleCallback;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.dr.service.ControlHistoryService;
import com.cannontech.dr.service.ControlType;
import com.cannontech.stars.core.dao.InventoryBaseDao;
import com.cannontech.stars.database.data.lite.LiteLmHardwareBase;
import com.cannontech.stars.dr.optout.dao.OptOutEventDao;
import com.cannontech.user.YukonUserContext;
import com.cannontech.yukon.IDatabaseCache;
import com.google.common.collect.Lists;

public class MeterDisconnectMessageListener {
    private static final Logger log = YukonLogManager.getLogger(MeterDisconnectMessageListener.class);
    
    @Autowired private ControlHistoryService controlHistoryService;
    @Autowired private IDatabaseCache dbCache;
    @Autowired private DeviceGroupService deviceGroupService;
    @Autowired private GroupService groupService;
    @Autowired private DisconnectService disconnectService;
    @Autowired private OptOutEventDao optOutEventDao;
    @Autowired private InventoryBaseDao inventoryBaseDao;

    public class MeterCollection implements DeviceCollection {
        List<SimpleDevice> collection;
        public MeterCollection(List<SimpleDevice> collection) {
            this.collection = collection;
        }
        /**
         * Only method needed to execute disconnect connect command using disconnectService.
         */
        @Override
        public List<SimpleDevice> getDeviceList() {
            return collection;
        }

        @Override
        public int getDeviceCount() {
            return collection.size();
        }

        @Override
        public Iterator<SimpleDevice> iterator() {
            return collection.listIterator();
        }

        @Override
        public List<SimpleDevice> getDevices(int start, int size) {
            int end = start + size;
            return collection.subList(start, Math.min(end, collection.size()));
        }

        @Override
        public Map<String, String> getCollectionParameters() {
            return null;
        }

        @Override
        public MessageSourceResolvable getDescription() {
            return null;
        }

        @Override
        public DeviceCollectionType getCollectionType() {
            return null;
        }

        @Override
        public Set<String> getErrorDevices() {
            return null;
        }

        @Override
        public int getDeviceErrorCount() {
            return 0;
        }

        @Override
        public String getUploadFileName() {
            return null;
        }

        @Override
        public String getHeader() {
            return null;
        }
        
    }
    
    public class Callback implements SimpleCallback {

        @Override
        public void handle(Object item) throws Exception {
        }
        
    }
    
    // Disconnect "Control"
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
                    // Find all the opted out devices that are in the groupId
                    Set<Integer> optOutInventory = optOutEventDao.getOptedOutInventoryByLoadGroups(Arrays.asList(groupId));
                    Set<Integer> optOutDeviceIds = inventoryBaseDao.getLMHardwareForIds(optOutInventory).stream()
                                                                                                        .map(LiteLmHardwareBase::getDeviceID)
                                                                                                        .collect(Collectors.toSet());
                    Set<SimpleDevice> meters = deviceGroupService.getDevices(Collections.singleton(deviceGroup));   
                    // Remove any meter's that are opted out from the list of meters that will be sent control
                    meters = meters.stream()
                                   .filter(meterId -> !optOutDeviceIds.contains(meterId.getPaoIdentifier().getPaoId()))
                                   .collect(Collectors.toSet());
                    
                    MeterCollection collection = new MeterCollection(Lists.newArrayList(meters));
                    Callback callback = new Callback(); //callback isn't really being used.
                    disconnectService.execute(DisconnectCommand.DISCONNECT, collection, callback, YukonUserContext.system);
                }
                

                controlHistoryService.sendControlHistoryShedMessage(groupId, startTimeUtc, ControlType.METER_DISCONNECT, null,
                    controlDurationSeconds, 100);
            } catch (JMSException e) {
                log.error("Error parsing Meter Disconnect control message from LM", e);
            }
        }
    }
    
    // Connect "End Control"
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
                    MeterCollection collection = new MeterCollection(Lists.newArrayList(meters));
                    Callback callback = new Callback();
                    disconnectService.execute(DisconnectCommand.CONNECT, collection, callback, YukonUserContext.system);
                }
                controlHistoryService.sendControlHistoryRestoreMessage(groupId, Instant.now());
            } catch (JMSException e) {
                log.error("Error parsing Meter Disconnect restore message from LM", e);
                return;
            }
        }
    }

}
