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
import com.cannontech.core.dao.CustomerDao;
import com.cannontech.database.data.lite.LiteCustomer;
import com.cannontech.dr.nest.dao.NestDao;
import com.cannontech.dr.nest.model.NestControlHistory;
import com.cannontech.dr.nest.model.NestException;
import com.cannontech.dr.nest.model.v3.ControlEvent;
import com.cannontech.dr.nest.model.v3.CustomerEnrollment;
import com.cannontech.dr.nest.model.v3.EnrollmentState;
import com.cannontech.dr.nest.model.v3.LoadShapingOptions;
import com.cannontech.dr.nest.model.v3.PeakLoadShape;
import com.cannontech.dr.nest.model.v3.PostLoadShape;
import com.cannontech.dr.nest.model.v3.PrepLoadShape;
import com.cannontech.dr.nest.model.v3.RushHourEventType;
import com.cannontech.dr.nest.model.v3.SchedulabilityError;
import com.cannontech.dr.nest.service.NestCommunicationService;
import com.cannontech.dr.nest.service.NestService;
import com.cannontech.dr.nest.service.NestSyncService;
import com.cannontech.dr.service.ControlHistoryService;
import com.cannontech.stars.dr.account.model.CustomerAccount;
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
    @Autowired private CustomerDao customerDao;
    
    
    private static final Logger log = YukonLogManager.getLogger(NestServiceImpl.class);

    @Override
    public Optional<SchedulabilityError> control(int programId, int gearId, Instant startTime, Instant stopTime) {
        
        List<String> groupNames = getNestGroupNames(programId);        
        /**
         * CREATE TABLE LMNestLoadShapingGear (
    GearId               NUMERIC              NOT NULL,
    PreparationOption    VARCHAR(20)          NOT NULL,
    PeakOption           VARCHAR(20)          NOT NULL,
    PostPeakOption       VARCHAR(20)          NOT NULL,
    CONSTRAINT PK_LMNestLoadShapingGear PRIMARY KEY (GearId)
);*/
        LoadShapingOptions loadShaping =
            new LoadShapingOptions(PrepLoadShape.PREP_STANDARD, PeakLoadShape.PEAK_STANDARD, PostLoadShape.POST_STANDARD);
        ControlEvent event = new ControlEvent("2018-09-14T00:00:00.000Z", "PT30M", groupNames, loadShaping);
        event.setStart(startTime);
        event.setStop(stopTime);
        
        return nestCommunicationService.sendEvent(event, RushHourEventType.STANDARD);
    }
    
    @Override
    public String stopControl(int programId) {
        List<String> groupNames = getNestGroupNames(programId);
        groupNames.forEach(group -> {
            NestControlHistory history = nestDao.getRecentHistoryForGroup(group);
            if (history == null) {
                log.error("Nest Control History for programId {} group {} is not found. Unable to cancel control.",
                    programId, group);
            } else {
                // NEST - add event log
                Optional<String> result = nestCommunicationService.cancelEvent(history);
            }
        });
        return "";
    }

    private List<String> getNestGroupNames(int programId){
        List<Integer> groupId = programDao.getDistinctGroupIdsByProgramIds(Sets.newHashSet(programId));
        
        return dbCache.getAllLMGroups().stream()
            .filter(g -> g.getPaoType() == PaoType.LM_GROUP_NEST && groupId.contains(g.getLiteID()))
            .map(g -> g.getPaoName())
            .collect(Collectors.toList());   
    }
     
    @Override
    public Optional<String> dissolveAccountWithNest(CustomerAccount account) {
        log.info("Removing accountNumber {} from Nest", account.getAccountNumber());
        CustomerEnrollment enrollment = createEnrollement(account.getCustomerId(), account.getAccountNumber());
        enrollment.setEnrollmentState(EnrollmentState.DISSOLVED);
        return nestCommunicationService.updateEnrollment(enrollment);
    }
    
    @Override
    public Optional<String> updateGroup(int customerId, String accountNumber, String newGroup) {
        log.info("Changing group for accountNumber {} to new group {}", accountNumber, newGroup);
        CustomerEnrollment enrollment = createEnrollement(customerId, accountNumber);
        enrollment.setGroupId(newGroup);
        enrollment.setEnrollmentState(EnrollmentState.ACCEPTED);
        return nestCommunicationService.updateEnrollment(enrollment);
    }
    
    /**
     * Creates Nest enrollment object from yukon account
     */
    private CustomerEnrollment createEnrollement(int customerId, String accountNumber) {
        CustomerEnrollment enrollment = new CustomerEnrollment();
        LiteCustomer customer = customerDao.getLiteCustomer(customerId);
        if (Strings.isEmpty(customer.getAltTrackingNumber()) || customer.getAltTrackingNumber().equals("(none)")) {
            throw new NestException(
                "Alt tracking number is required for this operation, it is missing from the account " + accountNumber);
        }
        enrollment.setCustomerId(customer.getAltTrackingNumber());
        return enrollment;
    }
}
