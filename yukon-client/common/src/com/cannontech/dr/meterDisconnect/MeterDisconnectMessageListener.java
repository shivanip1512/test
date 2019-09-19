package com.cannontech.dr.meterDisconnect;

import static com.cannontech.amr.disconnect.model.DrDisconnectStatusCallback.ControlOperation.*;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.jms.Message;
import javax.jms.StreamMessage;

import org.apache.logging.log4j.Logger;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;

import com.cannontech.amr.disconnect.model.DisconnectCommand;
import com.cannontech.amr.disconnect.model.DrDisconnectStatusCallback;
import com.cannontech.amr.disconnect.service.DisconnectService;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.bulk.collection.device.model.CollectionActionResult;
import com.cannontech.common.bulk.collection.device.model.DeviceCollection;
import com.cannontech.common.bulk.collection.device.model.DeviceCollectionType;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.smartNotification.service.SmartNotificationEventCreationService;
import com.cannontech.common.util.SimpleCallback;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.dr.meterDisconnect.service.DrMeterDisconnectStatusService;
import com.cannontech.dr.service.ControlHistoryService;
import com.cannontech.dr.service.ControlType;
import com.cannontech.loadcontrol.loadgroup.dao.LoadGroupDao;
import com.cannontech.stars.core.dao.InventoryBaseDao;
import com.cannontech.stars.database.data.lite.LiteLmHardwareBase;
import com.cannontech.stars.dr.enrollment.dao.EnrollmentDao;
import com.cannontech.stars.dr.optout.dao.OptOutEventDao;
import com.cannontech.user.YukonUserContext;
import com.cannontech.yukon.IDatabaseCache;
import com.google.common.collect.Lists;

public class MeterDisconnectMessageListener {
    private static final Logger log = YukonLogManager.getLogger(MeterDisconnectMessageListener.class);
    
    @Autowired private ControlHistoryService controlHistoryService;
    @Autowired private IDatabaseCache dbCache;
    @Autowired private DisconnectService disconnectService;
    @Autowired private OptOutEventDao optOutEventDao;
    @Autowired private InventoryBaseDao inventoryBaseDao;
    @Autowired private DrMeterDisconnectStatusService drStatusService;
    @Autowired private LoadGroupDao loadGroupDao;
    @Autowired private EnrollmentDao enrollmentDao;
    @Autowired private SmartNotificationEventCreationService smartNotificationEventCreationService;
    
    // Disconnect "Control"
    public void handleCyclingControlMessage(Message message) {
        if (message instanceof StreamMessage) {
            // Process control message
            MeterDisconnectControlMessage controlMessage = new MeterDisconnectControlMessage((StreamMessage) message);
            
            // Turn GroupId into a collection for passing to methods
            Collection<Integer> groupIdCollection = Arrays.asList(controlMessage.getGroupId());
            
            // Create duration for control
            int controlDurationSeconds = new Duration(controlMessage.getStartTime(), controlMessage.getEndTime()).toStandardSeconds().getSeconds();
            
            // Checks that the control duration received is positive.
            if(controlDurationSeconds <= 0) {
                return;
            }
            
            // Find all the opted out devices that are in the groupId
            Set<Integer> optOutInventory = optOutEventDao.getOptedOutInventoryByLoadGroups(groupIdCollection);
            // Find inventory and remove OptOut
            Set<SimpleDevice> meters = findInventoryAndRemoveOptOut(groupIdCollection, optOutInventory);
            
            // Find the loadGroup's programId
            int programId = loadGroupDao.getProgramIdByGroupId(controlMessage.getGroupId());
            
            // Initialize the event for the status report (or add the meters to an existing event)
            int eventId = drStatusService.initializeEvent(controlMessage.getStartTime(), controlMessage.getEndTime(), programId, meters);
            // Log the meter status of the Opt Out and Controlled devices
            logMeterStatusFromControlMessage(optOutInventory, meters, eventId);
            
            // Create a MeterCollection of the devices to be controlled
            MeterCollection collection = new MeterCollection(meters);
            
            // Generate the Status Callback for logging and send disconnect to devices
            sendDisconnectAndLogStatusCallback(programId, eventId, collection);
            
            // Log the controlHistoryShed message and its duration
            controlHistoryService.sendControlHistoryShedMessage(controlMessage.getGroupId(), controlMessage.getStartTime(), ControlType.METER_DISCONNECT, null,
                controlDurationSeconds, 100);
        }
    }

    // Connect "End Control"
    public void handleRestoreMessage(Message message) {
        if (message instanceof StreamMessage) {
            // Process restore message
            MeterDisconnectRestoreMessage restoreMessage = new MeterDisconnectRestoreMessage((StreamMessage) message);
            
            // Turn GroupId into a collection for passing to methods
            Collection<Integer> groupIdCollection = Arrays.asList(restoreMessage.getGroupId());
            
            // Checks that there is a LMGroup associated with the groupId
            boolean hasGroup = checkForLmGroup(restoreMessage);
            if (!hasGroup) {
                return;
            }
            int programId = loadGroupDao.getProgramIdByGroupId(restoreMessage.getGroupId());
            // Gets the eventId for the active programId
            Optional<Integer> eventId = drStatusService.findActiveEventForProgram(programId);
            // Logs the restore event
            logRestoreEvent(restoreMessage, programId, eventId);
            
            // Find all the opted out devices that are in the groupId
            Set<Integer> optOutInventory = optOutEventDao.getOptedOutInventoryByLoadGroups(groupIdCollection);
            // Find inventory and remove OptOut
            Set<SimpleDevice> meters = findInventoryAndRemoveOptOut(groupIdCollection, optOutInventory);
            
            // Create a MeterCollection of the devices to be controlled
            MeterCollection collection = new MeterCollection(meters);
            
            // Generate the Status Callback for logging and send connect to devices
            sendConnectAndLogStatusCallback(programId, eventId, collection);
            
            // Log the controlHistoryRestore message and the restore time (now)
            controlHistoryService.sendControlHistoryRestoreMessage(restoreMessage.getGroupId(), restoreMessage.getRestoreTime());         
        }
    }

    private String getProgramName(int programId) {
        // Find and returns the program name as a String
        String programName = dbCache.getAllLMPrograms().stream()
                    .filter(p -> p.getLiteID() ==  programId)
                    .map(LiteYukonPAObject::getPaoName)
                    .findFirst()
                    .get();
        return programName;
    }
    
    private Set<SimpleDevice> findInventoryAndRemoveOptOut(Collection<Integer> groupIdCollection,
                                                           Set<Integer> optOutInventory) {
        // Find all the inventory in the group
        Set<Integer> inventory = enrollmentDao.getActiveEnrolledInventoryIdsForGroupIds(groupIdCollection);
        // Remove any meters that are opted out from the list of meters that will be sent control
        inventory.removeAll(optOutInventory);
        // Turn all the inventory to SimpleDevices
        Set <SimpleDevice> meters  = inventoryBaseDao.getLMHardwareForIds(inventory).stream()
                                                                                    .map(LiteLmHardwareBase::getDeviceID)
                                                                                    .map(paoId -> dbCache.getAllPaosMap().get(paoId))
                                                                                    .map(SimpleDevice::new)
                                                                                    .collect(Collectors.toSet());
        return meters;
    }
    
    private boolean checkForLmGroup(MeterDisconnectRestoreMessage restoreMessage) {
        // Get all the LMGroups from the dbCache
        List<LiteYukonPAObject> allLMGroups = dbCache.getAllLMGroups();
        // See if there is a group that matches the GroupID from the message
        LiteYukonPAObject group = allLMGroups.stream()
                                             .filter(g -> g.getLiteID() == restoreMessage.getGroupId())
                                             .findAny()
                                             .orElse(null);
        // If no group is found, return false
        if(group == null) {
            log.error("Group with id " + restoreMessage.getGroupId() + " is not found");
            return false;
        }
        else {
            return true;
        }
    }
       
    private void logRestoreEvent(MeterDisconnectRestoreMessage restoreMessage, int programId,
                                 Optional<Integer> eventId) {
        log.debug("Event " + eventId + " found during lookup");
        // Log the eventID for the drStatusService if it exists
        eventId.ifPresentOrElse(id -> drStatusService.restoreSent(restoreMessage.getRestoreTime(), id),
                                () -> log.error("No active dr disconnect event found for program ID " + programId));
    }
    
    private void logMeterStatusFromControlMessage(Set<Integer> optOutInventory,
                                                  Set<SimpleDevice> meters, int eventId) {
        // Generate instant for logging
        Instant now = Instant.now();
        // Get the OptOut deviceIds
        Set<Integer> optOutDeviceIds = inventoryBaseDao.getLMHardwareForIds(optOutInventory).stream()
                                                                                            .map(LiteLmHardwareBase::getDeviceID)
                                                                                            .collect(Collectors.toSet());        
        // Get the meterIds   
        List<Integer> meterIds = meters.stream()
                                       .map(SimpleDevice::getDeviceId)
                                       .collect(Collectors.toList());
        // Log the status of meters that are opted out
        drStatusService.updateControlStatus(eventId, DrMeterControlStatus.NO_CONTROL_OPTED_OUT, now,
                                            optOutDeviceIds);
        // Log the status of meters that will be controlled
        drStatusService.updateControlStatus(eventId, DrMeterControlStatus.CONTROL_SENT, now, meterIds);
    }
   
    private void sendDisconnectAndLogStatusCallback(int programId, int eventId,
                                                        MeterCollection collection) {
        SimpleCallback<CollectionActionResult> doNothingCallback = result -> {};
        // statusCallback used for logging with the disconnectService
        DrDisconnectStatusCallback statusCallback = new DrDisconnectStatusCallback(CONTROL, eventId,
            drStatusService, smartNotificationEventCreationService, getProgramName(programId));
        log.debug("Sending Disconnect Command to collection " + collection);
        // Send the disconnect
        disconnectService.execute(DisconnectCommand.DISCONNECT, collection, doNothingCallback,
                                  statusCallback, YukonUserContext.system);
    }
    
    private void sendConnectAndLogStatusCallback(int programId, Optional<Integer> eventId,
                                                 MeterCollection collection) {
        SimpleCallback<CollectionActionResult> doNothingCallback = result -> {};
        DrDisconnectStatusCallback statusCallback = null;
        // If an evenId was found, create a statusCallback for logging the disconnect status
        // else it will just use a null callback
        if (eventId.isPresent()) {
            statusCallback = new DrDisconnectStatusCallback(RESTORE, eventId.get(), drStatusService,
                    smartNotificationEventCreationService, getProgramName(programId));
        }
        log.debug("Sending Connect Command to collection " + collection);
        // Send the connect command
        disconnectService.execute(DisconnectCommand.CONNECT, collection, doNothingCallback,
                                  statusCallback, YukonUserContext.system);
    }
   
    public static class MeterCollection implements DeviceCollection {
        List<SimpleDevice> collection;
        public MeterCollection(Set<SimpleDevice> meters) {
            this.collection = Lists.newArrayList(meters);
        }
        
        public MeterCollection(List<SimpleDevice> collection) {
            this.collection = collection;
        }
        
        public MeterCollection(SimpleDevice meter) {
            this.collection = Lists.newArrayList(meter);
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
}
