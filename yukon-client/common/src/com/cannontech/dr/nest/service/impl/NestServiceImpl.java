package com.cannontech.dr.nest.service.impl;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Strings;
import org.joda.time.Instant;
import org.joda.time.Period;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.pao.PaoType;
import com.cannontech.core.dao.CustomerDao;
import com.cannontech.core.dao.DBPersistentDao;
import com.cannontech.database.data.device.lm.NestCriticalCycleGear;
import com.cannontech.database.data.device.lm.NestStandardCycleGear;
import com.cannontech.database.data.lite.LiteCustomer;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.dr.nest.dao.NestDao;
import com.cannontech.dr.nest.model.NestControlEvent;
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
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.program.dao.ProgramDao;
import com.cannontech.yukon.IDatabaseCache;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class NestServiceImpl implements NestService {

    @Autowired private NestCommunicationService nestCommunicationService;
    @Autowired private NestDao nestDao;
    @Autowired private ProgramDao programDao;
    @Autowired private IDatabaseCache dbCache;
    @Autowired private CustomerDao customerDao;
    @Autowired private DBPersistentDao dbPersistentDao;
    
    //Nest timestamp in RFC3339 UTC "Zulu" format
    public static DateTimeFormatter formatter = DateTimeFormatter
            .ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
            .withZone(ZoneId.of("UTC"));
    
    private static final Logger log = YukonLogManager.getLogger(NestServiceImpl.class);

    @Override
    public Optional<SchedulabilityError> scheduleControl(int programId, int gearId, Date startTime, Date stopTime) {
        
        List<Integer> groupIds = programDao.getDistinctGroupIdsByYukonProgramIds(Sets.newHashSet(programId));
        String group = getNestGroupForProgram(groupIds, programId);
        Period period = new Period(new Instant(startTime), new Instant(stopTime)); 

        try {
            // retrieve standard gear
            NestStandardCycleGear standard = new NestStandardCycleGear();
            standard.setGearID(gearId);
            standard = (NestStandardCycleGear) dbPersistentDao.retrieveDBPersistent(standard);
            PeakLoadShape peak = standard.getPeakLoadShape();
            PrepLoadShape prep = standard.getPrepLoadShape();
            PostLoadShape post = standard.getPostLoadShape();
            LoadShapingOptions loadShaping = new LoadShapingOptions(prep, peak, post);
            ControlEvent event = new ControlEvent(formatter.format(startTime.toInstant()), "PT30M",
                Lists.newArrayList(group), loadShaping);
            event.setStart(new Instant(startTime));
            event.setStop(new Instant(stopTime));
            return nestCommunicationService.sendEvent(event, RushHourEventType.STANDARD);
        } catch (Exception | Error e) {
            // retrieve critical gear
            NestCriticalCycleGear critical = new NestCriticalCycleGear();
            critical.setGearID(gearId);
            critical = (NestCriticalCycleGear) dbPersistentDao.retrieveDBPersistent(critical);
            ControlEvent event =
                new ControlEvent(formatter.format(startTime.toInstant()), "PT30M", Lists.newArrayList(group));
            event.setStart(new Instant(startTime));
            event.setStop(new Instant(stopTime));
            return nestCommunicationService.sendEvent(event, RushHourEventType.CRITICAL);
        }
    }
    
    @Override
    public String stopControl(int programId) {
        List<Integer> groupIds = programDao.getDistinctGroupIdsByYukonProgramIds(Sets.newHashSet(programId));
        String group = getNestGroupForProgram(groupIds, programId);
        NestControlEvent event = nestDao.getCancelableEvent(group);
        if (event == null) {
            throw new NestException("Nest Control History for programId " + programId + " group " + group
                + " is not found or this event is already canceled. Unable to cancel control.");
        }
        Optional<String> result;
        if (event.getStartTime().isAfterNow()) {
            // control not started
            result = nestCommunicationService.cancelEvent(event);
        } else {
            // control started
            result = nestCommunicationService.stopEvent(event);
        }
        return result.get();
    }

    /**
     * Returns program name
     */
    private String getProgramName(int programId) {
        return dbCache.getAllLMPrograms().stream().filter(p -> p.getPaoIdentifier().getPaoId() == programId)
            .findFirst()
            .get()
            .getPaoName();
    }
     
    @Override
    public Optional<String> dissolveAccountWithNest(CustomerAccount account) {
        log.info("Removing accountNumber {} from Nest", account.getAccountNumber());
        CustomerEnrollment enrollment =
            createEnrollement(customerDao.getLiteCustomer(account.getAccountId()), account.getAccountNumber());
        enrollment.setEnrollmentState(EnrollmentState.DISSOLVED);
        return nestCommunicationService.updateEnrollment(enrollment);
    }
    
    @Override
    public Optional<String> updateGroup(int customerId, String accountNumber, String newGroup) {
        log.info("Changing group for accountNumber {} to new group {}", accountNumber, newGroup);
        CustomerEnrollment enrollment = createEnrollement(customerDao.getLiteCustomer(customerId), accountNumber);
        enrollment.setGroupId(newGroup);
        enrollment.setEnrollmentState(EnrollmentState.ACCEPTED);
        return nestCommunicationService.updateEnrollment(enrollment);
    }
    
    @Override
    public boolean isNestProgram(int programId) {
        boolean isNestProgram = dbCache.getAllLMPrograms().stream()
                .anyMatch(p -> p.getPaoType() == PaoType.LM_NEST_PROGRAM && p.getLiteID() == programId);
        return isNestProgram;
    }
    
    //UNIT TEST
    /**
     * Creates Nest enrollment object from yukon account
     */
    private CustomerEnrollment createEnrollement(LiteCustomer customer, String accountNumber) {
        CustomerEnrollment enrollment = new CustomerEnrollment();
        if (Strings.isEmpty(customer.getAltTrackingNumber()) || customer.getAltTrackingNumber().equals("(none)")) {
            throw new NestException(
                "Alt tracking number is required for this operation, it is missing from the account " + accountNumber);
        }
        enrollment.setCustomerId(customer.getAltTrackingNumber());
        return enrollment;
    }
    
    //UNIT TEST
    /**
     * Attempts to find a group by programId
     * @throws
     * NestException - if more the one group is linked to a program
     *                 if no nest groups are associated with the program
     * 
     */
    private String getNestGroupForProgram(List<Integer> groupIds, int programId){
        if (groupIds.size() > 1) {
            String programName = getProgramName(programId);
            throw new NestException(programName + " has " + groupIds.size()
                + " groups. Nest program must have one one group. Please remove additional groups.");
        }
        return dbCache.getAllLMGroups().stream()
            .filter(g -> g.getPaoType() == PaoType.LM_GROUP_NEST && groupIds.contains(g.getLiteID()))
            .map(LiteYukonPAObject::getPaoName)
            .findFirst()
            .orElseThrow(() -> new NestException("Nest Group is not found for program id "+ programId));
    }
}
