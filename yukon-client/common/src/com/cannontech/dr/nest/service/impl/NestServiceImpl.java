package com.cannontech.dr.nest.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Strings;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.Hours;
import org.joda.time.Instant;
import org.joda.time.Seconds;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.pao.DisplayablePao;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.util.Iso8601DateUtil;
import com.cannontech.core.dao.CustomerDao;
import com.cannontech.database.data.lite.LiteCustomer;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.dr.loadgroup.service.LoadGroupService;
import com.cannontech.dr.nest.dao.NestDao;
import com.cannontech.dr.nest.model.NestControlEvent;
import com.cannontech.dr.nest.model.NestException;
import com.cannontech.dr.nest.model.NestStopEventResult;
import com.cannontech.dr.nest.model.v3.ControlEvent;
import com.cannontech.dr.nest.model.v3.CustomerEnrollment;
import com.cannontech.dr.nest.model.v3.EnrollmentState;
import com.cannontech.dr.nest.model.v3.LoadShapingOptions;
import com.cannontech.dr.nest.model.v3.RushHourEventType;
import com.cannontech.dr.nest.model.v3.SchedulabilityError;
import com.cannontech.dr.nest.service.NestCommunicationService;
import com.cannontech.dr.nest.service.NestService;
import com.cannontech.dr.program.service.ProgramService;
import com.cannontech.loadcontrol.data.LMDirectGroupBase;
import com.cannontech.loadcontrol.data.LMProgramBase;
import com.cannontech.stars.dr.program.dao.ProgramDao;
import com.cannontech.yukon.IDatabaseCache;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class NestServiceImpl implements NestService {

    @Autowired private CustomerDao customerDao;
    @Autowired private IDatabaseCache dbCache;
    @Autowired private LoadGroupService loadGroupService;
    @Autowired private NestCommunicationService nestCommunicationService;
    @Autowired private NestDao nestDao;
    @Autowired private ProgramDao programDao;
    @Autowired private ProgramService programService;
    
    DateTimeFormatter debugFormatter = DateTimeFormat.forPattern("MMM dd YYYY HH:mm:ss");
    
    private static Hours NEST_MAX_CONTROL_HOURS = Hours.FOUR;
    
    private static final Logger log = YukonLogManager.getLogger(NestServiceImpl.class);

    @Override
    public Optional<SchedulabilityError> scheduleControl(int programId, int gearId, Date startTime, Date stopTime,
            boolean adjustStopTime) {

        List<Integer> groupIds = programDao.getDistinctGroupIdsByYukonProgramIds(Sets.newHashSet(programId));
        String group = getNestGroupForProgram(groupIds, programId).getPaoName();
        
        if(adjustStopTime) {
            stopTime = adjustStopTime(startTime, stopTime);
        }
        
        if (stopTime == null) {
            // currently stop time can't be null as UI defaults stop time to 2023 if the time is not selected
            throw new NestException("stopTime can't be null if adjustStopTime is false");
        }
        
        String startTimeStr = Iso8601DateUtil.formatIso8601Date(startTime, true);
        String durationStr = createDurationStr(startTime, stopTime);
        ControlEvent event = new ControlEvent();
        event.setStart(new Instant(startTime));
        event.setStop(new Instant(stopTime)); 
        event.setDuration(durationStr);
        event.setGroupIds(Lists.newArrayList(group));
        event.setStartTime(startTimeStr);
        LoadShapingOptions loadShaping = nestDao.findNestLoadShapingOptions(gearId);
        if (loadShaping != null) { // Standard Cycle
            event.setLoadShapingOptions(loadShaping);
            return nestCommunicationService.sendEvent(event, RushHourEventType.STANDARD);
        } else { // Critical Cycle
            return nestCommunicationService.sendEvent(event, RushHourEventType.CRITICAL);
        }
    }
    
    @Override
    public String createDurationStr(Date startTime, Date stopTime) {
        //A duration in seconds with up to nine fractional digits, terminated by 's'. Example: "3.5s"
        //Duration
        //A Duration represents a signed, fixed-length span of time represented as a count of seconds and fractions of 
        //seconds at nanosecond resolution. It is independent of any calendar and concepts like "day" or "month". 
        //It is related to Timestamp in that the difference between two Timestamp values is a Duration and it can be added or subtracted from a Timestamp. 
        //Range is approximately +-10,000 years.

        String seconds = Seconds.secondsBetween(new DateTime(startTime), new DateTime(stopTime)).toString();
        return seconds + "s";
    }

    //UNIT TEST
    /**
     * Adjusts stop time if it is null or duration between start and stop time greater then 4 hours
     */
    private Date adjustStopTime(Date startTime, Date stopTime) {
        if (stopTime == null) {
            stopTime = new DateTime(startTime).plusHours(NEST_MAX_CONTROL_HOURS.getHours()).toDate();
        } else {
            Duration duration = new Duration(new Instant(startTime), new Instant(stopTime));
            if (duration.isLongerThan(NEST_MAX_CONTROL_HOURS.toStandardDuration())) {
                stopTime = new DateTime(startTime).plusHours(NEST_MAX_CONTROL_HOURS.getHours()).toDate();
            }
        }
        log.debug("Control start time {} stop time {}", new DateTime(startTime).toString(debugFormatter),
            new DateTime(stopTime).toString(debugFormatter));
        return stopTime;
    }

    @Override
    public NestStopEventResult stopControlForProgram(int programId) {
        List<Integer> groupIds = programDao.getDistinctGroupIdsByYukonProgramIds(Sets.newHashSet(programId));
        String group = getNestGroupForProgram(groupIds, programId).getPaoName();
        return stopControlForGroup(group);
    }

    @Override
    public NestStopEventResult stopControlForGroup(String group) {
        NestStopEventResult result = new NestStopEventResult();
        NestControlEvent event = nestDao.getCancelableEvent(group);
        if (event == null) {
            log.info(
                "Nest Control History for group {} is not found or this event is already canceled. Unable to cancel control with Nest.",
                group);
            return result;
        }
        result.setStopPossible(true);
        Optional<String> nestResult;
        if (event.getStartTime().isAfterNow()) {
            // control not started
            nestResult = nestCommunicationService.cancelEvent(event);
        } else {
            // control started
            nestResult = nestCommunicationService.stopEvent(event);
        }
        if(nestResult.isPresent()) {
            result.setNestResponse(nestResult.get());
        } else {
            result.setSuccess(true);
        }
        return result;
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
    public Optional<String> dissolveAccountWithNest(LiteCustomer account, String accountNumber) {
        log.info("Removing accountNumber {} from Nest", account.getLiteID());
        CustomerEnrollment enrollment = createEnrollement(account, accountNumber);
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
    public boolean isEnabledNestProgramWithEnabledGroup(int programId) {
        return dbCache.getAllLMPrograms().stream()
                .anyMatch(p -> p.getPaoType() == PaoType.LM_NEST_PROGRAM 
                            && p.getLiteID() == programId 
                            && isProgramEnabled(programId) 
                            && isGroupEnabled(programId));
    }

    /**
     * Returns true if group is enabled
     */
    private boolean isGroupEnabled(int programId) {
        List<Integer> groupIds = programDao.getDistinctGroupIdsByYukonProgramIds(Sets.newHashSet(programId));
        LiteYukonPAObject groupPao = getNestGroupForProgram(groupIds, programId);
        LMDirectGroupBase group = loadGroupService.getGroupForPao(groupPao);
        return !group.getDisableFlag();
    }

    /**
     * Returns true if group is enabled
     */
    private boolean isProgramEnabled(int programId) {
        DisplayablePao pao = programService.getProgram(programId);
        LMProgramBase program = programService.getProgramForPao(pao);
        return !program.getDisableFlag();
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
    private LiteYukonPAObject getNestGroupForProgram(List<Integer> groupIds, int programId){
        if (groupIds.size() > 1) {
            String programName = getProgramName(programId);
            throw new NestException(programName + " has " + groupIds.size()
                + " groups. Nest program must have one group. Please remove additional groups.");
        }
        return dbCache.getAllLMGroups().stream()
            .filter(g -> g.getPaoType() == PaoType.LM_GROUP_NEST && groupIds.contains(g.getLiteID()))
            .findFirst()
            .orElseThrow(() -> new NestException("Nest Group is not found for program id "+ programId));
    }
}
