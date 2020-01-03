package com.cannontech.dr.program.service.impl;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.bulk.filter.AbstractRowMapperWithBaseQuery;
import com.cannontech.common.bulk.filter.RowMapperWithBaseQuery;
import com.cannontech.common.bulk.filter.UiFilter;
import com.cannontech.common.bulk.filter.service.FilterDao;
import com.cannontech.common.events.loggers.DemandResponseEventLogService;
import com.cannontech.common.exception.NotAuthorizedException;
import com.cannontech.common.pao.DisplayablePao;
import com.cannontech.common.pao.DisplayablePaoBase;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDao;
import com.cannontech.common.pao.definition.model.PaoTag;
import com.cannontech.common.search.result.SearchResults;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.DatedObject;
import com.cannontech.common.util.ScheduledExecutor;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.authorization.service.PaoAuthorizationService;
import com.cannontech.core.authorization.support.Permission;
import com.cannontech.core.dao.GearNotFoundException;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.roleproperties.UserNotInRoleException;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.dr.model.ProgramOriginSource;
import com.cannontech.dr.program.filter.ForLoadGroupFilter;
import com.cannontech.dr.program.model.GearAdjustment;
import com.cannontech.dr.program.service.ConstraintContainer;
import com.cannontech.dr.program.service.ConstraintViolations;
import com.cannontech.dr.program.service.ProgramService;
import com.cannontech.dr.scenario.dao.ScenarioDao;
import com.cannontech.dr.scenario.model.ScenarioProgram;
import com.cannontech.loadcontrol.LCUtils;
import com.cannontech.loadcontrol.LoadControlClientConnection;
import com.cannontech.loadcontrol.ProgramUtils;
import com.cannontech.loadcontrol.dao.LmProgramGearHistory;
import com.cannontech.loadcontrol.dao.LmProgramGearHistory.GearAction;
import com.cannontech.loadcontrol.dao.LoadControlProgramDao;
import com.cannontech.loadcontrol.data.IGearProgram;
import com.cannontech.loadcontrol.data.LMProgramBase;
import com.cannontech.loadcontrol.data.LMProgramDirect;
import com.cannontech.loadcontrol.data.LMProgramDirectGear;
import com.cannontech.loadcontrol.messages.LMCommand;
import com.cannontech.loadcontrol.messages.LMManualControlRequest;
import com.cannontech.loadcontrol.messages.LMManualControlResponse;
import com.cannontech.loadcontrol.service.LoadControlCommandService;
import com.cannontech.loadcontrol.service.ProgramChangeBlocker;
import com.cannontech.loadcontrol.service.data.ProgramStatus;
import com.cannontech.loadcontrol.service.data.ProgramStatusType;
import com.cannontech.message.server.ServerResponseMsg;
import com.cannontech.message.util.BadServerResponseException;
import com.cannontech.message.util.ConnectionException;
import com.cannontech.message.util.Message;
import com.cannontech.message.util.ServerRequest;
import com.cannontech.message.util.ServerRequestImpl;
import com.cannontech.message.util.TimeoutException;
import com.cannontech.stars.dr.jms.message.DrJmsMessageType;
import com.cannontech.stars.dr.jms.message.DrProgramStatusJmsMessage;
import com.cannontech.stars.dr.jms.service.DrJmsMessagingService;
import com.cannontech.stars.dr.program.dao.ProgramDao;
import com.cannontech.user.YukonUserContext;
import com.google.common.collect.Lists;

public class ProgramServiceImpl implements ProgramService {
    private final Logger log = YukonLogManager.getLogger(ProgramServiceImpl.class);

    @Autowired private final ProgramDao programDao = null;
    @Autowired private final LoadControlClientConnection loadControlClientConnection = null;
    @Autowired private FilterDao filterService;
    @Autowired private DemandResponseEventLogService demandResponseEventLogService;
    @Autowired private PaoDefinitionDao paoDefinitionDao;
    @Autowired private LoadControlProgramDao loadControlProgramDao;
    @Autowired private PaoAuthorizationService paoAuthorizationService;
    @Autowired private PaoDao paoDao;
    @Autowired private LoadControlCommandService loadControlCommandService;
    @Autowired private RolePropertyDao rolePropertyDao;
    @Autowired private ScenarioDao scenarioDao;
    @Autowired private DateFormattingService dateFormattingService;
    @Autowired private DrJmsMessagingService drJmsMessagingService;
    @Autowired @Qualifier("main") private ScheduledExecutor scheduledExecutor;

    private static final long PROGRAM_CHANGE_TIMEOUT_MS = 5000;
    private static DateTime lastRuntime = null;

    @PostConstruct
    public void init() {
        scheduledExecutor.scheduleAtFixedRate(this::sendProgramStatus, 5, 5, TimeUnit.MINUTES);
        log.info("Initialized executor for Sending Program Status with frequency of 5 minutes.");
    }

    private final RowMapperWithBaseQuery<DisplayablePao> rowMapper =
        new AbstractRowMapperWithBaseQuery<DisplayablePao>() {

            @Override
            public SqlFragmentSource getBaseQuery() {
            	Set<PaoType> paoTypes = paoDefinitionDao.getPaoTypesThatSupportTag(PaoTag.LM_PROGRAM);
                SqlStatementBuilder retVal = new SqlStatementBuilder();
                retVal.append("SELECT paObjectId, paoName, type");
                retVal.append("FROM yukonPAObject");
                retVal.append("WHERE type").in(paoTypes);

                return retVal;
            }

            @Override
            public DisplayablePao mapRow(YukonResultSet rs) throws SQLException {
                PaoIdentifier paoId = rs.getPaoIdentifier("paobjectId", "type");
                DisplayablePao retVal = new DisplayablePaoBase(paoId, rs.getString("paoName"));
                return retVal;
            }
        };

    @Override
    public LMProgramBase getProgramForPao(YukonPao from) {
        DatedObject<LMProgramBase> datedProgram =
            loadControlClientConnection.getDatedProgram(from.getPaoIdentifier().getPaoId());
        return datedProgram == null ? null : datedProgram.getObject();
    }
    
    @Override
    public LMProgramBase getProgramForPaoSafe(YukonPao from) {
            return loadControlClientConnection.getProgramSafe(from.getPaoIdentifier().getPaoId());
    }

    @Override
    public DatedObject<LMProgramBase> findDatedProgram(int programId) {
        return loadControlClientConnection.getDatedProgram(programId);
    }

    @Override
    public List<DisplayablePao> findProgramsForLoadGroup(int loadGroupId, 
                                                         YukonUserContext userContext) {
        UiFilter<DisplayablePao> filter = new ForLoadGroupFilter(loadGroupId);

        SearchResults<DisplayablePao> searchResult =
            filterPrograms(filter, null, 0, Integer.MAX_VALUE, userContext);

        return searchResult.getResultList();
    }

    @Override
    public SearchResults<DisplayablePao> filterPrograms(UiFilter<DisplayablePao> filter,
                                                       Comparator<DisplayablePao> sorter, 
                                                       int startIndex, int count,
                                                       YukonUserContext userContext) {

        SearchResults<DisplayablePao> searchResult =
            filterService.filter(filter, sorter, startIndex, count, rowMapper);
        return searchResult;
    }

    @Override
    public List<GearAdjustment> getDefaultAdjustments(Date startDate,
            Date stopDate, Collection<ScenarioProgram> scenarioPrograms,
            YukonUserContext userContext) {

        DateTime firstStartDate = new DateTime(startDate);
        DateTime lastStopDate = new DateTime(stopDate);
        if (scenarioPrograms != null) {
            for (ScenarioProgram scenarioProgram : scenarioPrograms) {
                Date programStartDate = datePlusOffset(startDate,
                                                       scenarioProgram.getStartOffset());
                if (firstStartDate.isAfter(programStartDate.getTime())) {
                    firstStartDate = new DateTime(programStartDate);
                }
                Date programStopDate = datePlusOffset(stopDate,
                                                      scenarioProgram.getStopOffset());
                if (lastStopDate.isBefore(programStopDate.getTime())) {
                    lastStopDate = new DateTime(programStopDate);
                }
            }
        }

        List<GearAdjustment> retVal = Lists.newArrayList();

        Calendar timeSlotStartCal = dateFormattingService.getCalendar(userContext);
        timeSlotStartCal.setTime(startDate);
        timeSlotStartCal.set(Calendar.MINUTE, 0);
        timeSlotStartCal.set(Calendar.SECOND, 0);
        timeSlotStartCal.set(Calendar.MILLISECOND, 0);
        DateTime gearAdjustmentBegin = new DateTime(timeSlotStartCal);

        while (lastStopDate.compareTo(gearAdjustmentBegin) > 0
                && retVal.size() < 24) {
            retVal.add(new GearAdjustment(gearAdjustmentBegin.toDate()));
            gearAdjustmentBegin = gearAdjustmentBegin.plusHours(1);
        }

        return retVal;
    }
    
    @Override
    public List<ConstraintViolations> getConstraintViolationForStartScenario(int scenarioId, Date startDate, Date stopDate, ProgramOriginSource programOriginSource) {
        List<Integer> programIds = loadControlProgramDao.getProgramIdsByScenarioId(scenarioId);
        List<LMProgramBase> programs = loadControlClientConnection.getProgramsForProgramIds(programIds);
        Map<Integer, ScenarioProgram> scenarioPrograms =  scenarioDao.findScenarioProgramsForScenario(scenarioId);
        List<ConstraintViolations> programViolations = new ArrayList<>();

        for (LMProgramBase program : programs) {
            final int programId = program.getPaoIdentifier().getPaoId();
            final int startingGearNumber = loadControlProgramDao.getStartingGearForScenarioAndProgram(ProgramUtils.getProgramId(program), scenarioId);
            final ScenarioProgram scenarioProgram = scenarioPrograms.get(programId);

            ConstraintViolations violations = 
                getConstraintViolationForStartProgram(
                    programId, 
                    startingGearNumber, 
                    startDate, 
                    scenarioProgram.getStartOffset(), 
                    stopDate, 
                    scenarioProgram.getStopOffset(), 
                    null,
                    programOriginSource);
            
            programViolations.add(violations);
        }
        
        return programViolations;
    }
    
    @Override
    public List<ConstraintViolations> getConstraintViolationForStopScenario(int scenarioId, Date stopDate, ProgramOriginSource programOriginSource) {
        List<Integer> programIds = loadControlProgramDao.getProgramIdsByScenarioId(scenarioId);
        List<LMProgramBase> programs = loadControlClientConnection.getProgramsForProgramIds(programIds);
        List<ConstraintViolations> programViolations = new ArrayList<>();

        for (LMProgramBase program : programs) {
            final int programId = program.getPaoIdentifier().getPaoId();
            final int startingGearNumber = loadControlProgramDao.getStartingGearForScenarioAndProgram(ProgramUtils.getProgramId(program), scenarioId);

            ConstraintViolations violations = 
                getConstraintViolationsForStopProgram(programId, startingGearNumber, stopDate, programOriginSource);

            programViolations.add(violations);
        }
        
        return programViolations;
    }

    @Override
    public ConstraintViolations getConstraintViolationForStartProgram(int programId, int gearNumber, Date startDate,
                                                                      Duration startOffset, Date stopDate, Duration stopOffset, 
                                                                      List<GearAdjustment> gearAdjustments, ProgramOriginSource programOriginSource) {
        Date programStartDate = datePlusOffset(startDate, startOffset);
        Date programStopDate = datePlusOffset(stopDate, stopOffset);

        LMManualControlRequest controlRequest =
            getManualControlMessage(programId, gearNumber,
                                    programStartDate, programStopDate,
                                    LMManualControlRequest.CONSTRAINTS_FLAG_CHECK,
                                    programOriginSource
                                    );

        // better be a program for this Request Message!
        LMProgramBase program = loadControlClientConnection.getProgram(programId);

        String additionalInfo = null;
        if (program instanceof LMProgramDirect) {
            additionalInfo =
                additionalInfoFromGearAdjustments(gearAdjustments,
                                                  programStartDate,
                                                  programStopDate);
        }

        if (additionalInfo != null) {
            ((LMProgramDirect) program).setAddtionalInfo(additionalInfo);
            controlRequest.setAddditionalInfo(additionalInfo);
        }

        log.debug("checking constraints for program " + programId + " using gear " + gearNumber +
                  " at " + startDate + " + " + startOffset + " = " + programStartDate +
                  "; stopping at " + stopDate + " + " + stopOffset + " = " + programStopDate +
                  "; additionalInfo = " + additionalInfo);

        return makeServerRequest(controlRequest);
    } 

    @Override
    public ProgramStatus startProgram(int programId, Date startTime, Date stopTime, String gearName,
            boolean overrideConstraints, boolean observeConstraints, LiteYukonUser liteYukonUser, ProgramOriginSource programOriginSource)
                    throws NotFoundException, TimeoutException, UserNotInRoleException, ConnectionException {

        boolean stopScheduled = stopTime != null && rolePropertyDao.getPropertyBooleanValue(YukonRoleProperty.SCHEDULE_STOP_CHECKED_BY_DEFAULT, liteYukonUser);

        LMProgramBase program = loadControlClientConnection.getProgramSafe(programId);
        ProgramStatus programStatus = new ProgramStatus(program);

        int gearNumber;
        if (gearName == null) {
            gearNumber = ((LMProgramDirect)program).getCurrentGearNumber();
        } else {
            try {
                gearNumber = loadControlProgramDao.getGearNumberForGearName(programId, gearName);
            } catch (NotFoundException e) {
                throw new GearNotFoundException(e.getMessage(), e);
            }
        }

        if (!overrideConstraints) {
            ConstraintViolations checkViolations = getConstraintViolationForStartProgram(programId, gearNumber, startTime, Duration.ZERO, stopTime, Duration.ZERO, null, programOriginSource);
            if (checkViolations.isViolated()) {
                for (ConstraintContainer violation : checkViolations.getConstraintContainers()) {
                    log.info("Constraint Violation: " + violation.toString() + " for request");
                }
                programStatus.setConstraintViolations(checkViolations.getConstraintContainers());
            } 
        }

        if (observeConstraints || overrideConstraints) {
            log.info("No constraint violations for request.");
            programStatus = startProgramBlocking(programId, gearNumber, startTime, Duration.ZERO, stopScheduled, stopTime, Duration.ZERO, observeConstraints, null, programOriginSource);
        }
        return programStatus;
    }

    @Override
    public ProgramStatus startProgramBlocking(int programId, int gearNumber, Date startDate, Duration startOffset,
                                              boolean stopScheduled, Date stopDate, Duration stopOffset, boolean overrideConstraints,
                                              List<GearAdjustment> gearAdjustments, ProgramOriginSource programOriginSource) throws TimeoutException {
        return startProgramBlocking(programId, gearNumber, startDate, startOffset, stopScheduled, stopDate, stopOffset,
                                    overrideConstraints, gearAdjustments, true, programOriginSource);
    }

    @Override
    public void startProgram(int programId, int gearNumber, Date startDate,
                             Duration startOffset, boolean stopScheduled, Date stopDate,
                             Duration stopOffset, boolean overrideConstraints,
                             List<GearAdjustment> gearAdjustments, ProgramOriginSource programOriginSource) {
        try {
            startProgramBlocking(programId, gearNumber, startDate, startOffset, 
                                 stopScheduled, stopDate, stopOffset, overrideConstraints, 
                                 gearAdjustments, false, programOriginSource);
        } catch (TimeoutException e) {
            // This isn't actually thrown since we're passing false for block.
        }
    }

    private ProgramStatus startProgramBlocking(int programId, int gearNumber, Date startDate,
                                               Duration startOffset, boolean stopScheduled, Date stopDate,
                                               Duration stopOffset, boolean overrideConstraints,
                                               List<GearAdjustment> gearAdjustments, boolean block, ProgramOriginSource programOriginSource) throws TimeoutException {

        Date programStartDate = datePlusOffset(startDate, startOffset);
        Date programStopDate = stopScheduled ? datePlusOffset(stopDate, stopOffset)
                                             : CtiUtilities.get2035GregCalendar().getTime();

        if (log.isDebugEnabled()) {
            log.debug("starting program " + programId + " using gear " + gearNumber +
                      " at " + startDate + " + " + startOffset + " = " + programStartDate +
                      "; stopping at " + stopDate + " + " + stopOffset + " = " + programStopDate);
        }

        LMManualControlRequest controlRequest = getStartProgramRequest(programId, gearNumber, programStartDate,
                                                                       stopScheduled, programStopDate, overrideConstraints, gearAdjustments, programOriginSource);

        LMProgramBase program = loadControlClientConnection.getProgram(programId);
        ProgramStatus programStatus = null;

        controlRequest.setConstraintFlag(LMManualControlRequest.CONSTRAINTS_FLAG_USE);
        if (overrideConstraints) {
            controlRequest.setConstraintFlag(LMManualControlRequest.CONSTRAINTS_FLAG_OVERRIDE);
        }

        if (block) {
            programStatus = new ProgramStatus(program);
            ProgramChangeBlocker programChangeBlocker = new ProgramChangeBlocker(controlRequest, programStatus,
                                                                                 loadControlCommandService, loadControlClientConnection, PROGRAM_CHANGE_TIMEOUT_MS);
            programChangeBlocker.updateProgramStatus();
        } else {
            ServerRequest serverRequest = new ServerRequestImpl();
            serverRequest.makeServerRequest(loadControlClientConnection, controlRequest);
        }

        String gearName = getGearNameForProgram(program, gearNumber);
        demandResponseEventLogService.programScheduled(program.getYukonName(), overrideConstraints,
                                                       gearName, programStartDate,
                                                       stopScheduled, programStopDate);

        return programStatus;
    }
    
    @Override
    public List<ProgramStatus> startScenarioBlocking(int scenarioId, Date startTime, Date stopTime, 
            boolean overrideConstraints, boolean observeConstraints, LiteYukonUser user, ProgramOriginSource programOriginSource)
            throws NotFoundException, TimeoutException, NotAuthorizedException, ConnectionException {

        if (!loadControlClientConnection.isValid()) {
            throw new ConnectionException("The Load Management server connection is not valid.");
        }

        if (!paoAuthorizationService.isAuthorized(user, Permission.LM_VISIBLE, paoDao.getLiteYukonPAO(scenarioId))) {
            throw new NotAuthorizedException("Scenario is not visible to user id=" + scenarioId + "");
        }

        List<Integer> programIds = loadControlProgramDao.getProgramIdsByScenarioId(scenarioId);
        List<LMProgramBase> programs = loadControlClientConnection.getProgramsForProgramIds(programIds);
        Map<Integer, ScenarioProgram> scenarioPrograms =  scenarioDao.findScenarioProgramsForScenario(scenarioId);
        List<ProgramStatus> programStatuses = new ArrayList<>();

        for (LMProgramBase program : programs) {
            int startingGearNumber = loadControlProgramDao.getStartingGearForScenarioAndProgram(ProgramUtils.getProgramId(program), scenarioId);
            ScenarioProgram scenarioProgram = scenarioPrograms.get(program.getYukonID());

            Date programStartDate = datePlusOffset(startTime, scenarioProgram.getStartOffset());
            Date programStopDate = stopTime == null ? null : datePlusOffset(stopTime, scenarioProgram.getStopOffset());

            String gearName = getGearNameForProgram(program, startingGearNumber);
            ProgramStatus programStatus = startProgram(program.getYukonID(), programStartDate, programStopDate, gearName,
                          overrideConstraints, observeConstraints, user, programOriginSource);
            
            programStatuses.add(programStatus);
        }

        return programStatuses;   
    }

    @Override
    public void startScenario(final int scenarioId,final  Date startTime,final  Date stopTime,
                                          final boolean overrideConstraints,final boolean observeConstraints,final  LiteYukonUser user,
                                          ProgramOriginSource programOriginSource)
                                                  throws NotFoundException, TimeoutException, NotAuthorizedException, BadServerResponseException,
                                                  ConnectionException {
        scheduledExecutor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    startScenarioBlocking(scenarioId, startTime, stopTime, overrideConstraints, observeConstraints, user, programOriginSource);
                } catch (Exception e) {
                    log.debug("Error while running scenario start asynchronously. scenarioId = " + scenarioId, e);
                }
            }
        });
    }

    private LMManualControlRequest getStartProgramRequest(int programId, int gearNumber, Date programStartDate,
                                                          boolean stopScheduled, Date programStopDate, boolean overrideConstraints,
                                                          List<GearAdjustment> gearAdjustments, ProgramOriginSource programOriginSource) {

        int constraintId = overrideConstraints
                ? LMManualControlRequest.CONSTRAINTS_FLAG_OVERRIDE
                        : LMManualControlRequest.CONSTRAINTS_FLAG_USE;
        LMManualControlRequest controlRequest =
                getManualControlMessage(programId, gearNumber, programStartDate,
                                        programStopDate, constraintId, programOriginSource);
        LMProgramBase program = loadControlClientConnection.getProgram(programId);
        String additionalInfo =
                additionalInfoFromGearAdjustments(gearAdjustments, programStartDate,
                                                  programStopDate);
        if (additionalInfo != null) {
            ((LMProgramDirect) program).setAddtionalInfo(additionalInfo);
            controlRequest.setAddditionalInfo(additionalInfo);
        }

        if (log.isDebugEnabled()) {
            log.debug("starting program " + programId + " using gear " + gearNumber +
                      "; additionalInfo = " + additionalInfo + "; non-blocking");
        }

        return controlRequest;
    }

    /**
     * If date is null, uses now
     */
    protected Date datePlusOffset(Date date, Duration offset) {
        return new Instant(date).plus(offset).toDate();
    }

    /**
     * Combine gear adjustments into a string to use for the "additional info".
     * This method ignores gear adjustments which are not valid for the
     * program's start and stop times. It assumes the gear adjustments are in
     * order by start/end time.
     */
    protected String additionalInfoFromGearAdjustments(
            List<GearAdjustment> gearAdjustments, Date startDate, Date stopDate) {
        if (gearAdjustments == null) {
            return null;
        }

        StringBuilder retVal = new StringBuilder("adjustments");
        for (GearAdjustment gearAdjustment : gearAdjustments) {
            Date gearBegin = gearAdjustment.getBeginTime();
            Date gearEnd = gearAdjustment.getEndTime();
            if (startDate.before(gearBegin) && !stopDate.before(gearBegin)
                    || !startDate.before(gearBegin) && !startDate.after(gearEnd)) {
                retVal.append(' ');
                retVal.append(gearAdjustment.getAdjustmentValue());
            }
        }

        return retVal.toString();
    }

    private String getGearNameForProgram(LMProgramBase program, int gearNumber) {
        if (program instanceof IGearProgram) {
            LMProgramDirectGear gear =
                ((IGearProgram) program).getDirectGearVector().get(gearNumber - 1);
            return gear.getGearName();
        }
        return null;
    }

    @Override
    public void stopProgram(int programId, ProgramOriginSource programOriginSource) {
        LMProgramBase program = loadControlClientConnection.getProgram(programId);
        Date stopDate = new Date();
        LMManualControlRequest controlRequest =
            program.createStartStopNowMsg(stopDate, 1, null, false,
                                          LMManualControlRequest.CONSTRAINTS_FLAG_USE, programOriginSource);
        loadControlClientConnection.write(controlRequest);

        demandResponseEventLogService.programStopped(program.getYukonName(),
                                                     stopDate, null);
    }

    @Override
    public ProgramStatus stopProgramBlocking(int programId, Date stopDate, Duration stopOffset,
                                                ProgramOriginSource programOriginSource) 
            throws TimeoutException, BadServerResponseException {
        return stopProgramBlocking(programId, stopDate, stopOffset, true, programOriginSource);
    }

    @Override
    public void stopProgram(int programId, Date stopDate, Duration stopOffset, ProgramOriginSource programOriginSource) throws BadServerResponseException {
        try {
            stopProgramBlocking(programId, stopDate, stopOffset, false, programOriginSource);
        } catch (TimeoutException e) {
            // This isn't actually thrown since we're passing false for block.
        }
    }

    @Override
    public ProgramStatus stopProgram(int programId, Date stopTime, boolean force, boolean observeConstraints,
                                        ProgramOriginSource programOriginSource)
            throws TimeoutException, BadServerResponseException {
        
        if (stopTime == null) {
            stopTime = CtiUtilities.get1990GregCalendar().getTime();
        }

        LMProgramBase program = loadControlClientConnection.getProgramSafe(programId);
        int gearNumber = ((LMProgramDirect)program).getCurrentGearNumber();

        ProgramStatus programStatus = new ProgramStatus(program);

        ConstraintViolations checkViolations = null;

        if (!force) {
            checkViolations = getConstraintViolationsForStopProgram(programId, gearNumber, stopTime, programOriginSource);

            if (checkViolations.isViolated()) {
                for (ConstraintContainer violation : checkViolations.getConstraintContainers()) {
                    log.info("Constraint Violation: " + violation.toString() + " for request");
                }
                programStatus.setConstraintViolations(checkViolations.getConstraintContainers());
            }

        } else {
            log.info("No constraint violations for request");
        }

        if (force || observeConstraints) {
            programStatus = stopProgramBlocking(programId, stopTime, Duration.ZERO, programOriginSource);
        }
        return programStatus;
    }

    private ProgramStatus stopProgramBlocking(int programId, Date stopDate, Duration stopOffset, boolean block, ProgramOriginSource programOriginSource)
            throws TimeoutException, BadServerResponseException {
        
        LMProgramBase program = loadControlClientConnection.getProgram(programId);
        Date programStopDate = datePlusOffset(stopDate, stopOffset);
        Date startDate = CtiUtilities.get1990GregCalendar().getTime();
        LMManualControlRequest controlRequest = program.createScheduledStopMsg(startDate, programStopDate, 1, null, programOriginSource);
        
        ProgramStatus programStatus = null;
        if (block) {
            programStatus = new ProgramStatus(program);

            ProgramChangeBlocker programChangeBlocker = new ProgramChangeBlocker(controlRequest,
                                                                                 programStatus,
                                                                                 this.loadControlCommandService,
                                                                                 this.loadControlClientConnection,
                                                                                 PROGRAM_CHANGE_TIMEOUT_MS);
            programChangeBlocker.updateProgramStatus();
        } else {
            loadControlClientConnection.write(controlRequest);
        }

        demandResponseEventLogService.programStopScheduled(program.getYukonName(),
                                                           programStopDate, null);

        return programStatus;
    }

    @Override
    public void stopScenario(final int scenarioId, final  Date stopTime,
                                         final boolean overrideConstraints,
                                         final boolean observeConstraints,
                                         final LiteYukonUser user,
                                         final ProgramOriginSource programOriginSource) throws NotFoundException, NotAuthorizedException {
        scheduledExecutor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    stopScenarioBlocking(scenarioId,  stopTime, overrideConstraints, observeConstraints, user, programOriginSource);
                } catch (Exception e) {
                    log.debug("Error while running scenario start asynchronously. scenarioId = " + scenarioId, e);
                }
            }
        });
    }

    @Override
    public List<ProgramStatus> stopScenarioBlocking(int scenarioId,  Date stopTime, boolean overrideConstraints,
                                                     boolean observeConstraints, LiteYukonUser user,
                                                     ProgramOriginSource programOriginSource)
                         throws NotFoundException, TimeoutException, NotAuthorizedException, ConnectionException, BadServerResponseException {

        if (!loadControlClientConnection.isValid()) {
            throw new ConnectionException("The Load Management server connection is not valid.");
        }

        if (!paoAuthorizationService.isAuthorized(user, Permission.LM_VISIBLE, paoDao.getLiteYukonPAO(scenarioId))) {
            throw new NotAuthorizedException("Scenario is not visible to user id=" + scenarioId + "");
        }

        List<Integer> programIds = loadControlProgramDao.getProgramIdsByScenarioId(scenarioId);
        List<LMProgramBase> programs = loadControlClientConnection.getProgramsForProgramIds(programIds);
        Map<Integer, ScenarioProgram> scenarioPrograms =  scenarioDao.findScenarioProgramsForScenario(scenarioId);
        List<ProgramStatus> programStatuses = new ArrayList<>();

        for (LMProgramBase program : programs) {
            ScenarioProgram scenarioProgram = scenarioPrograms.get(program.getYukonID());

            Date programStopDate = datePlusOffset(stopTime, scenarioProgram.getStopOffset());
            ProgramStatus programStatus = stopProgram(program.getYukonID(), programStopDate, overrideConstraints, observeConstraints, programOriginSource);

            programStatuses.add(programStatus);
        }

        return programStatuses;   
    }

    @Override
    public ConstraintViolations getConstraintViolationsForStopProgram(int programId, int gearNumber, 
                                                                      Date stopDate, ProgramOriginSource programOriginSource) {
        LMProgramBase program = loadControlClientConnection.getProgram(programId);
        Date startDate = CtiUtilities.get1990GregCalendar().getTime();
        LMManualControlRequest changeGearRequest = 
            program.createScheduledStopMsg(
                    startDate, 
                    stopDate,
                    gearNumber, null, programOriginSource);
        changeGearRequest.setConstraintFlag(LMManualControlRequest.CONSTRAINTS_FLAG_CHECK);
        changeGearRequest.setCommand(LMManualControlRequest.CHANGE_GEAR);

        return makeServerRequest(changeGearRequest);
    }

    @Override
    public void stopProgramWithGear(int programId, int gearNumber, Date stopDate, 
                                    boolean overrideConstraints, ProgramOriginSource programOriginSource) {
        int constraintId = overrideConstraints
            ? LMManualControlRequest.CONSTRAINTS_FLAG_OVERRIDE
            : LMManualControlRequest.CONSTRAINTS_FLAG_USE;
        LMProgramBase program = loadControlClientConnection.getProgram(programId);
        String gearName = getGearNameForProgram(program, gearNumber);
        Date startDate = CtiUtilities.get1990GregCalendar().getTime();
        LMManualControlRequest changeGearRequest = 
            program.createScheduledStopMsg(
                    startDate, 
                    stopDate,
                    gearNumber, null, programOriginSource);
        changeGearRequest.setConstraintFlag(constraintId);
        changeGearRequest.setCommand(LMManualControlRequest.CHANGE_GEAR);
        ServerRequest serverRequest = new ServerRequestImpl();
        serverRequest.makeServerRequest(loadControlClientConnection, changeGearRequest);

        demandResponseEventLogService.programStopped(program.getYukonName(),
                                                     stopDate, gearName);
    }

    private ConstraintViolations makeServerRequest(LMManualControlRequest controlRequest) {
        ServerRequest serverRequest = new ServerRequestImpl();
        ServerResponseMsg serverResponse =
            serverRequest.makeServerRequest(loadControlClientConnection, controlRequest);

        List<ConstraintContainer> violations = Lists.newArrayList();
        LMManualControlResponse lmResp = (LMManualControlResponse) serverResponse.getPayload();
        
        violations = LCUtils.convertViolationsToContainers(lmResp.getConstraintViolations());

        return new ConstraintViolations(violations);
    }

    @Override
    public DisplayablePao getProgram(int programId) {
        return programDao.getProgram(programId);
    }
    
    @Override
    public void changeGear(int programId, int gearNumber, ProgramOriginSource programOriginSource) {
        
        LMProgramBase program = loadControlClientConnection.getProgram(programId);
        Date stopDate = program.getStopTime().getTime();
        
        // Change gears is only available for programs in the Manual Active state
        if(LMProgramBase.STATUS_MANUAL_ACTIVE != program.getProgramStatus()) {
            throw new IllegalStateException("The program " + 
                                            program.getYukonName() + 
                                            " must be in the Manual Active state to change gears.");
        }
        
        LMManualControlRequest changeGearRequest = 
            program.createStartStopNowMsg(stopDate, 
                                          gearNumber, 
                                          null, 
                                          true, 
                                          LMManualControlRequest.CONSTRAINTS_FLAG_USE,
                                          programOriginSource) ;
        changeGearRequest.setCommand(LMManualControlRequest.CHANGE_GEAR);
        changeGearRequest.setStartTime(program.getStartTime());
        ServerRequest serverRequest = new ServerRequestImpl();
        serverRequest.makeServerRequest(loadControlClientConnection, changeGearRequest);
        
        String gearName = getGearNameForProgram(program, gearNumber);
        demandResponseEventLogService.programChangeGear(program.getYukonName(), gearName);
    }

    @Override
    public void setEnabled(int programId, boolean isEnabled) {

        int loadControlCommand = isEnabled ? LMCommand.ENABLE_PROGRAM
                : LMCommand.DISABLE_PROGRAM;
        Message msg = new LMCommand(loadControlCommand, programId, 0, 0.0);
        loadControlClientConnection.write(msg);
        
        DisplayablePao program = this.getProgram(programId);
        if(isEnabled) {
            demandResponseEventLogService.programEnabled(program.getName());
        } else {
            demandResponseEventLogService.programDisabled(program.getName());
        }
        
    }
    
    @Override
    public void disableAndSupressRestoration(int programId) {
        Message msg = new LMCommand(LMCommand.EMERGENCY_DISABLE_PROGRAM, programId, 0, 0.0);
        loadControlClientConnection.write(msg);
        
        DisplayablePao program = getProgram(programId);
        demandResponseEventLogService.programDisabled(program.getName());
    }
    
    private LMManualControlRequest getManualControlMessage(
            int programId, int gearNumber, Date startDate, Date stopDate, int constraintId, ProgramOriginSource programOriginSource) {
        if (stopDate == null) {
            throw new IllegalArgumentException("stopDate cannot be null");
        }

        LMProgramBase program = loadControlClientConnection.getProgram(programId);
        String additionalInfo = program instanceof LMProgramDirect
            ? ((LMProgramDirect) program).getAddtionalInfo() : null;
        if (startDate == null) {
            return program.createStartStopNowMsg(stopDate, gearNumber,
                                                 additionalInfo, true,
                                                 constraintId, programOriginSource);
        }

        return program.createScheduledStartMsg(startDate, stopDate, gearNumber,
                                               null, additionalInfo, constraintId, programOriginSource);
    }

    public void sendProgramStatus() {
        DateTime from = null;
        if (lastRuntime == null) {
            lastRuntime = DateTime.now();
            from = lastRuntime.minusMinutes(5);
        } else {
            from = lastRuntime;
            lastRuntime = DateTime.now();
        }
        List<LmProgramGearHistory> lmProgramGearHistories = loadControlProgramDao.getProgramHistoryDetails(from, lastRuntime);

        List<DrProgramStatusJmsMessage> programStatusMessages = new ArrayList<>();

        if (CollectionUtils.isNotEmpty(lmProgramGearHistories)) {

            for (LmProgramGearHistory programHistory : lmProgramGearHistories) {
                DrProgramStatusJmsMessage message = buildProgramStatusJmsMessage(programHistory);
                programStatusMessages.add(message);
            }

            sendProgramStatus(programStatusMessages);
        } else {
            log.debug("Not sending any data as there is no load program change history");
        }

    }

    /**
     * Build Program Status messages that include start/stop time, gear change time, status, program/gear name,
     * correlationId(programGearHistoryId).
     */
    private DrProgramStatusJmsMessage buildProgramStatusJmsMessage(LmProgramGearHistory programHistory) {

        DrProgramStatusJmsMessage message = new DrProgramStatusJmsMessage();
        // correlationId
        message.setProgramGearHistId(programHistory.getProgramGearHistoryId());
        message.setProgramName(programHistory.getProgramName());
        message.setGearName(programHistory.getGearName());
        message.setMessageType(DrJmsMessageType.PROGRAMSTATUS);

        if (GearAction.START == programHistory.getAction() || GearAction.UPDATE == programHistory.getAction()) {
            message.setStartDateTime(programHistory.getEventTime());
            message.setProgramStatusType(ProgramStatusType.ACTIVE);
        } else if (GearAction.GEAR_CHANGE == programHistory.getAction()) {
            message.setGearChangeTime(programHistory.getEventTime());
            message.setProgramStatusType(ProgramStatusType.ACTIVE);
        } else if (GearAction.STOP == programHistory.getAction()) {
            message.setStopDateTime(programHistory.getEventTime());
            Date startedTime = getProgramStartedDateTime(programHistory.getProgramHistoryId());
            if (startedTime != null) {
                message.setStartDateTime(startedTime);
            }
            message.setProgramStatusType(ProgramStatusType.INACTIVE);
        }
        return message;
    }

    /**
     * Publish program status messages to queue.
     */
    private void sendProgramStatus(List<DrProgramStatusJmsMessage> programStatusMessages) {
        for (DrProgramStatusJmsMessage message : programStatusMessages) {
            drJmsMessagingService.publishProgramStatusNotice(message);
        }
    }

    /**
     * Get Program Start time from database corresponding to programHistoryId
     */

    private Date getProgramStartedDateTime(Integer programHistoryId) {

        LmProgramGearHistory startedProgramGearHistory = loadControlProgramDao.getProgramHistoryDetail(programHistoryId, GearAction.START);
        return startedProgramGearHistory.getEventTime();

    }

}
