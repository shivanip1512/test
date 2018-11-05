package com.cannontech.dr.nest.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Strings;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.pao.PaoType;
import com.cannontech.dr.nest.dao.NestDao;
import com.cannontech.dr.nest.model.DissolveReason;
import com.cannontech.dr.nest.model.LoadShaping;
import com.cannontech.dr.nest.model.LoadShapingPeak;
import com.cannontech.dr.nest.model.LoadShapingPost;
import com.cannontech.dr.nest.model.LoadShapingPreparation;
import com.cannontech.dr.nest.model.NestControlHistory;
import com.cannontech.dr.nest.model.NestError;
import com.cannontech.dr.nest.model.NestException;
import com.cannontech.dr.nest.model.NestExisting;
import com.cannontech.dr.nest.model.NestUploadInfo;
import com.cannontech.dr.nest.model.StandardEvent;
import com.cannontech.dr.nest.service.NestCommunicationService;
import com.cannontech.dr.nest.service.NestService;
import com.cannontech.dr.nest.service.NestSyncService;
import com.cannontech.dr.service.ControlHistoryService;
import com.cannontech.stars.dr.program.dao.ProgramDao;
import com.cannontech.yukon.IDatabaseCache;
import com.google.common.collect.Sets;

public class NestServiceImpl implements NestService {

    @Autowired private NestCommunicationService nestCommunicationService;
    @Autowired private NestSyncService nestSyncService;
    @Autowired private NestDao nestDao;
    @Autowired private ControlHistoryService controlHistoryService;
    @Autowired private ProgramDao programDao;
    @Autowired private IDatabaseCache dbCache;
    
    private static final Logger log = YukonLogManager.getLogger(NestServiceImpl.class);

    @Override
    public Optional<NestError> control(int programId, int gearId, Instant startTime, Instant stopTime) {
        
        List<String> groupNames = getNestGroupNames(programId);        
        /**
         * CREATE TABLE LMNestLoadShapingGear (
    GearId               NUMERIC              NOT NULL,
    PreparationOption    VARCHAR(20)          NOT NULL,
    PeakOption           VARCHAR(20)          NOT NULL,
    PostPeakOption       VARCHAR(20)          NOT NULL,
    CONSTRAINT PK_LMNestLoadShapingGear PRIMARY KEY (GearId)
);*/
        LoadShaping loadShaping =
            new LoadShaping(LoadShapingPreparation.STANDARD, LoadShapingPeak.STANDARD, LoadShapingPost.STANDARD);
        StandardEvent standardEvent = new StandardEvent("2018-09-14T00:00:00.000Z", "PT30M",  groupNames, loadShaping);
        standardEvent.setStart(startTime);
        standardEvent.setStop(stopTime);
        
        return nestCommunicationService.sendStandardEvent(standardEvent);
    }
    
    @Override
    public void stopControl(int programId) {
        List<String> groupNames = getNestGroupNames(programId);
        groupNames.forEach(group -> {
            NestControlHistory history = nestDao.getRecentHistoryForGroup(group);
            if (history == null) {
                log.error("Nest Control History for programId {} group {} is not found. Unable to cancel control.",
                    programId, group);
            } else {
                // NEST - add event log
                boolean success = nestCommunicationService.cancelEvent(history);
            }
        });
    }

    private List<String> getNestGroupNames(int programId){
        List<Integer> groupId = programDao.getDistinctGroupIdsByProgramIds(Sets.newHashSet(programId));
        
        return dbCache.getAllLMGroups().stream()
            .filter(g -> g.getPaoType() == PaoType.LM_GROUP_NEST && groupId.contains(g.getLiteID()))
            .map(g -> g.getPaoName())
            .collect(Collectors.toList());   
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
                .orElseThrow(() -> new NestException("Account " + accountNumber + " is not found in the Nest file"));
    }
}
