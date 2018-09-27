package com.cannontech.dr.nest.service.impl;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.dr.nest.model.DissolveReason;
import com.cannontech.dr.nest.model.LoadShaping;
import com.cannontech.dr.nest.model.LoadShapingPeak;
import com.cannontech.dr.nest.model.LoadShapingPost;
import com.cannontech.dr.nest.model.LoadShapingPreparation;
import com.cannontech.dr.nest.model.NestEventId;
import com.cannontech.dr.nest.model.NestException;
import com.cannontech.dr.nest.model.NestExisting;
import com.cannontech.dr.nest.model.NestUploadInfo;
import com.cannontech.dr.nest.model.StandardEvent;
import com.cannontech.dr.nest.service.NestCommunicationService;
import com.cannontech.dr.nest.service.NestService;
import com.cannontech.dr.nest.service.NestSyncService;
import com.cannontech.dr.service.ControlHistoryService;
import com.google.common.collect.Lists;

public class NestServiceImpl implements NestService {

    @Autowired private NestCommunicationService nestCommunicationService;
    @Autowired private NestSyncService nestSyncService;
    @Autowired private ControlHistoryService controlHistoryService;
    
    private ConcurrentHashMap<String, String> groupsToEventIds = new ConcurrentHashMap<>();
    private static final Logger log = YukonLogManager.getLogger(NestServiceImpl.class);

    @Override
    public void control() {

        //yukon.notif.stream.dr.NestCriticalCyclingControlMessage and yukon.notif.stream.dr.NestStandardCyclingControlMessage
        
        List<String> groups = Lists.newArrayList("Test");

        // CriticalEvent criticalEvent = new CriticalEvent("2018-09-14T00:00:00.000Z", "PT30M", groups);
        // String eventId = nestCommunicationService.createCriticalEvent(criticalEvent);
        // groupsToEventIds.put("Group1", eventId);

        LoadShaping loadShaping =
            new LoadShaping(LoadShapingPreparation.STANDARD, LoadShapingPeak.STANDARD, LoadShapingPost.STANDARD);
        StandardEvent standardEvent = new StandardEvent("2018-09-14T00:00:00.000Z", "PT30M", groups, loadShaping);
        NestEventId eventId = nestCommunicationService.createStandardEvent(standardEvent);
        
    
        
        if(eventId == null) {
            //got an error - log?
            //don't send message controlHistoryService?
           // controlHistoryService.sendControlHistoryShedMessage(groupId, startTime, controlType, associationId, controlDurationSeconds, reductionRatio);sendControlHistoryRestoreMessage(groupId, time);
        } else{
            groupsToEventIds.put("Test", eventId.getId());
            //send message controlHistoryService?
        }
    }
    
    
    @Override
    public NestUploadInfo dissolveAccountWithNest(String accountNumber) {
        //NEST - add event log
        log.info("Dissolving account with Nest {}", accountNumber);
        List<NestExisting> existing = nestCommunicationService.downloadExisting();
        NestExisting row = getRowToModifyAndRemoveAllOtherAccounts(existing, accountNumber);
        row.setDissolve("Y");
        row.setDissolveReason(DissolveReason.CUSTOMER_UNENROLLED.name());
        return nestCommunicationService.uploadExisting(existing);
    }
    
    @Override
    public NestUploadInfo updateGroup(String accountNumber, String newGroup) {
        //NEST - add event log
        log.info("Changing group for accountNumber {} to new group {}", accountNumber, newGroup);
        List<NestExisting> existing = nestCommunicationService.downloadExisting();
        NestExisting row = getRowToModifyAndRemoveAllOtherAccounts(existing, accountNumber);
        log.debug("Existing group {}", row.getGroup());
        row.setAssignGroup("Y");
        row.setGroup(newGroup);
        return nestCommunicationService.uploadExisting(existing);
    }
    
    /**
     * Removes all the accounts from the existing list that are not account we are looking for.
     * Returns the row of the account found
     * 
     * @throws NestException if account is not found
     */
    
    private NestExisting getRowToModifyAndRemoveAllOtherAccounts(List<NestExisting> existing, String accountNumber) {
        //NEST - add unit test
        existing.removeIf(
            row -> Strings.isNotEmpty(row.getAccountNumber()) && !row.getAccountNumber().equals(accountNumber)
                && !row.getAccountNumber().equals(NestSyncServiceImpl.EMPTY_ROW));

        return existing.stream()
                .filter(row -> Strings.isNotBlank(row.getAccountNumber()) && row.getAccountNumber().equals(accountNumber))
                .findFirst()
                .orElseThrow(() -> new NestException("Account " + accountNumber + " is not fund in the Nest file"));
    }
}
