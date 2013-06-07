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
import java.util.concurrent.Executor;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.bulk.filter.AbstractRowMapperWithBaseQuery;
import com.cannontech.common.bulk.filter.RowMapperWithBaseQuery;
import com.cannontech.common.bulk.filter.UiFilter;
import com.cannontech.common.bulk.filter.service.FilterService;
import com.cannontech.common.events.loggers.DemandResponseEventLogService;
import com.cannontech.common.exception.NotAuthorizedException;
import com.cannontech.common.pao.DisplayablePao;
import com.cannontech.common.pao.DisplayablePaoBase;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDao;
import com.cannontech.common.pao.definition.model.PaoTag;
import com.cannontech.common.search.SearchResult;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.DatedObject;
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
import com.cannontech.dr.program.dao.ProgramDao;
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
import com.cannontech.loadcontrol.dao.LoadControlProgramDao;
import com.cannontech.messaging.message.BaseMessage;
import com.cannontech.messaging.message.loadcontrol.CommandMessage;
import com.cannontech.messaging.message.loadcontrol.ManualControlRequestMessage;
import com.cannontech.messaging.message.loadcontrol.ManualControlResponseMessage;
import com.cannontech.messaging.message.loadcontrol.data.GearProgram;
import com.cannontech.messaging.message.loadcontrol.data.Program;
import com.cannontech.messaging.message.loadcontrol.data.ProgramDirect;
import com.cannontech.loadcontrol.service.LoadControlCommandService;
import com.cannontech.loadcontrol.service.ProgramChangeBlocker;
import com.cannontech.loadcontrol.service.data.ProgramStatus;
import com.cannontech.messaging.message.loadcontrol.data.ProgramDirectGear;
import com.cannontech.messaging.util.BadServerResponseException;
import com.cannontech.messaging.util.ServerRequest;
import com.cannontech.messaging.util.ServerRequestImpl;
import com.cannontech.messaging.util.TimeoutException;
import com.cannontech.message.util.ConnectionException;
import com.cannontech.user.YukonUserContext;
import com.google.common.collect.Lists;

public class ProgramServiceImpl implements ProgramService {
    private final Logger log = YukonLogManager.getLogger(ProgramServiceImpl.class);

    @Autowired private final ProgramDao programDao = null;
    @Autowired private final LoadControlClientConnection loadControlClientConnection = null;
    @Autowired private FilterService filterService;
    @Autowired private DemandResponseEventLogService demandResponseEventLogService;
    @Autowired private PaoDefinitionDao paoDefinitionDao;
    @Autowired private LoadControlProgramDao loadControlProgramDao;
    @Autowired private PaoAuthorizationService paoAuthorizationService;
    @Autowired private PaoDao paoDao;
    @Autowired private LoadControlCommandService loadControlCommandService;
    @Autowired private RolePropertyDao rolePropertyDao;
    @Autowired private ScenarioDao scenarioDao;
    @Autowired private DateFormattingService dateFormattingService;
    @Autowired @Qualifier("main") private Executor executor;

    private static final long PROGRAM_CHANGE_TIMEOUT_MS = 5000;

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
            public DisplayablePao mapRow(YukonResultSet rs)
                    throws SQLException {
            	int paobjectId = rs.getInt("paObjectId");
            	String paoTypeStr = rs.getString("type");
            	PaoType paoType = PaoType.getForDbString(paoTypeStr);
                PaoIdentifier paoId = new PaoIdentifier(paobjectId, paoType);
                DisplayablePao retVal = new DisplayablePaoBase(paoId,
                                                               rs.getString("paoName"));
                return retVal;
            }
        };

    @Override
    public Program getProgramForPao(YukonPao from) {
        DatedObject<Program> datedProgram =
            loadControlClientConnection.getDatedProgram(from.getPaoIdentifier().getPaoId());
        return datedProgram == null ? null : datedProgram.getObject();
    }
    
    @Override
    public Program getProgramForPaoSafe(YukonPao from) {
            return loadControlClientConnection.getProgramSafe(from.getPaoIdentifier().getPaoId());
    }

    @Override
    public DatedObject<Program> findDatedProgram(int programId) {
        return loadControlClientConnection.getDatedProgram(programId);
    }

    @Override
    public List<DisplayablePao> findProgramsForLoadGroup(int loadGroupId, 
                                                         YukonUserContext userContext) {
        UiFilter<DisplayablePao> filter = new ForLoadGroupFilter(loadGroupId);

        SearchResult<DisplayablePao> searchResult =
            filterPrograms(filter, null, 0, Integer.MAX_VALUE, userContext);

        return searchResult.getResultList();
    }

    @Override
    public SearchResult<DisplayablePao> filterPrograms(UiFilter<DisplayablePao> filter,
                                                       Comparator<DisplayablePao> sorter, 
                                                       int startIndex, int count,
                                                       YukonUserContext userContext) {

        SearchResult<DisplayablePao> searchResult =
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
    public List<ConstraintViolations> getConstraintViolationForStartScenario(int scenarioId, Date startDate, Date stopDate) {
        List<Integer> programIds = loadControlProgramDao.getProgramIdsByScenarioId(scenarioId);
        List<Program> programs = loadControlClientConnection.getProgramsForProgramIds(programIds);
        Map<Integer, ScenarioProgram> scenarioPrograms =  scenarioDao.findScenarioProgramsForScenario(scenarioId);
        List<ConstraintViolations> programViolations = new ArrayList<>();

        for (Program program : programs) {
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
                    null);
            
            programViolations.add(violations);
        }
        
        return programViolations;
    }
    
    @Override
    public List<ConstraintViolations> getConstraintViolationForStopScenario(int scenarioId, Date stopDate) {
        List<Integer> programIds = loadControlProgramDao.getProgramIdsByScenarioId(scenarioId);
        List<Program> programs = loadControlClientConnection.getProgramsForProgramIds(programIds);
        List<ConstraintViolations> programViolations = new ArrayList<>();

        for (Program program : programs) {
            final int programId = program.getPaoIdentifier().getPaoId();
            final int startingGearNumber = loadControlProgramDao.getStartingGearForScenarioAndProgram(ProgramUtils.getProgramId(program), scenarioId);

            ConstraintViolations violations = 
                getConstraintViolationsForStopProgram(programId, startingGearNumber, stopDate);

            programViolations.add(violations);
        }
        
        return programViolations;
    }

    @Override
    public ConstraintViolations getConstraintViolationForStartProgram(int programId, int gearNumber, Date startDate,
                                                                      Duration startOffset, Date stopDate, Duration stopOffset, 
                                                                      List<GearAdjustment> gearAdjustments) {
        Date programStartDate = datePlusOffset(startDate, startOffset);
        Date programStopDate = datePlusOffset(stopDate, stopOffset);

        ManualControlRequestMessage controlRequest =
            getManualControlMessage(programId, gearNumber,
                                    programStartDate, programStopDate,
                                    ManualControlRequestMessage.CONSTRAINTS_FLAG_CHECK);

        // better be a program for this Request Message!
        Program program = loadControlClientConnection.getProgram(programId);

        String additionalInfo = null;
        if (program instanceof ProgramDirect) {
            additionalInfo =
                additionalInfoFromGearAdjustments(gearAdjustments,
                                                  programStartDate,
                                                  programStopDate);
        }

        if (additionalInfo != null) {
            ((ProgramDirect) program).setAddtionalInfo(additionalInfo);
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
            boolean overrideConstraints, boolean observeConstraints, LiteYukonUser liteYukonUser)
                    throws NotFoundException, TimeoutException, UserNotInRoleException, ConnectionException {

        boolean stopScheduled = rolePropertyDao.getPropertyBooleanValue(YukonRoleProperty.SCHEDULE_STOP_CHECKED_BY_DEFAULT, liteYukonUser);

        Program program = loadControlClientConnection.getProgramSafe(programId);
        ProgramStatus programStatus = new ProgramStatus(program);

        int gearNumber;
        if (gearName == null) {
            gearNumber = ((ProgramDirect)program).getCurrentGearNumber();
        } else {
            try {
                gearNumber = loadControlProgramDao.getGearNumberForGearName(programId, gearName);
            } catch (NotFoundException e) {
                throw new GearNotFoundException(e.getMessage(), e);
            }
        }

        if (!overrideConstraints) {
            ConstraintViolations checkViolations = getConstraintViolationForStartProgram(programId, gearNumber, startTime, Duration.ZERO, stopTime, Duration.ZERO, null);
            if (checkViolations.isViolated()) {
                for (ConstraintContainer violation : checkViolations.getConstraintContainers()) {
                    log.info("Constraint Violation: " + violation.toString() + " for request");
                }
                programStatus.setConstraintViolations(checkViolations.getConstraintContainers());
            } 
        }

        if (observeConstraints || overrideConstraints) {
            log.info("No constraint violations for request.");
            programStatus = startProgramBlocking(programId, gearNumber, startTime, Duration.ZERO, stopScheduled, stopTime, Duration.ZERO, observeConstraints, null);
        }
        return programStatus;
    }

    @Override
    public ProgramStatus startProgramBlocking(int programId, int gearNumber, Date startDate, Duration startOffset,
                                              boolean stopScheduled, Date stopDate, Duration stopOffset, boolean overrideConstraints,
                                              List<GearAdjustment> gearAdjustments) throws TimeoutException {
        return startProgramBlocking(programId, gearNumber, startDate, startOffset, stopScheduled, stopDate, stopOffset,
                                    overrideConstraints, gearAdjustments, true);
    }

    @Override
    public void startProgram(int programId, int gearNumber, Date startDate,
                             Duration startOffset, boolean stopScheduled, Date stopDate,
                             Duration stopOffset, boolean overrideConstraints,
                             List<GearAdjustment> gearAdjustments) {
        try {
            startProgramBlocking(programId, gearNumber, startDate, startOffset, 
                                 stopScheduled, stopDate, stopOffset, overrideConstraints, 
                                 gearAdjustments, false);
        } catch (TimeoutException e) {
            // This isn't actually thrown since we're passing false for block.
        }
    }

    private ProgramStatus startProgramBlocking(int programId, int gearNumber, Date startDate,
                                               Duration startOffset, boolean stopScheduled, Date stopDate,
                                               Duration stopOffset, boolean overrideConstraints,
                                               List<GearAdjustment> gearAdjustments, boolean block) throws TimeoutException {

        Date programStartDate = datePlusOffset(startDate, startOffset);
        Date programStopDate = datePlusOffset(stopDate, stopOffset);

        if (log.isDebugEnabled()) {
            log.debug("starting program " + programId + " using gear " + gearNumber +
                      " at " + startDate + " + " + startOffset + " = " + programStartDate +
                      "; stopping at " + stopDate + " + " + stopOffset + " = " + programStopDate);
        }

        ManualControlRequestMessage controlRequest = getStartProgramRequest(programId, gearNumber, programStartDate,
                                                                       stopScheduled, programStopDate, overrideConstraints, gearAdjustments);

        Program program = loadControlClientConnection.getProgram(programId);
        ProgramStatus programStatus = null;

        controlRequest.setConstraintFlag(ManualControlRequestMessage.CONSTRAINTS_FLAG_USE);
        if (overrideConstraints) {
            controlRequest.setConstraintFlag(ManualControlRequestMessage.CONSTRAINTS_FLAG_OVERRIDE);
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
    public List<ProgramStatus> startScenarioBlocking(int scenarioId, Date startTime,
                                             Date stopTime, boolean overrideConstraints, boolean observeConstraints,
                                                         LiteYukonUser user)
                             throws NotFoundException, TimeoutException, NotAuthorizedException,
                                     ConnectionException {

        if (!loadControlClientConnection.isValid()) {
            throw new ConnectionException("The Load Management server connection is not valid.");
        }

        if (!paoAuthorizationService.isAuthorized(user, Permission.LM_VISIBLE, paoDao.getLiteYukonPAO(scenarioId))) {
            throw new NotAuthorizedException("Scenario is not visible to user id=" + scenarioId + "");
        }

        List<Integer> programIds = loadControlProgramDao.getProgramIdsByScenarioId(scenarioId);
        List<Program> programs = loadControlClientConnection.getProgramsForProgramIds(programIds);
        Map<Integer, ScenarioProgram> scenarioPrograms =  scenarioDao.findScenarioProgramsForScenario(scenarioId);
        List<ProgramStatus> programStatuses = new ArrayList<>();

        for (Program program : programs) {
            int startingGearNumber = loadControlProgramDao.getStartingGearForScenarioAndProgram(ProgramUtils.getProgramId(program), scenarioId);
            ScenarioProgram scenarioProgram = scenarioPrograms.get(program.getYukonId());

            Date programStartDate = datePlusOffset(startTime, scenarioProgram.getStartOffset());
            Date programStopDate = datePlusOffset(stopTime, scenarioProgram.getStopOffset());

            String gearName = getGearNameForProgram(program, startingGearNumber);
            ProgramStatus programStatus = startProgram(program.getYukonId(), programStartDate, programStopDate, gearName,
                          overrideConstraints, observeConstraints, user);
            
            programStatuses.add(programStatus);
        }

        return programStatuses;   
    }

    @Override
    public void startScenario(final int scenarioId,final  Date startTime,final  Date stopTime,
                                          final boolean overrideConstraints,final boolean observeConstraints,final  LiteYukonUser user)
                                                  throws NotFoundException, TimeoutException, NotAuthorizedException, BadServerResponseException,
                                                  ConnectionException {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    startScenarioBlocking(scenarioId, startTime, stopTime, overrideConstraints, observeConstraints, user);
                } catch (Exception e) {
                    log.debug("Error while running scenario start asynchronously. scenarioId = " + scenarioId, e);
                }
            }
        });
    }

    private ManualControlRequestMessage getStartProgramRequest(int programId, int gearNumber, Date programStartDate,
                                                          boolean stopScheduled, Date programStopDate, boolean overrideConstraints,
                                                          List<GearAdjustment> gearAdjustments) {

        int constraintId = overrideConstraints
            ? ManualControlRequestMessage.CONSTRAINTS_FLAG_OVERRIDE
            : ManualControlRequestMessage.CONSTRAINTS_FLAG_USE;
        ManualControlRequestMessage controlRequest =
                getManualControlMessage(programId, gearNumber, programStartDate,
                                        programStopDate, constraintId);

        Program program = loadControlClientConnection.getProgram(programId);
        String additionalInfo =
                additionalInfoFromGearAdjustments(gearAdjustments, programStartDate,
                                                  programStopDate);
        if (additionalInfo != null) {
            ((ProgramDirect) program).setAddtionalInfo(additionalInfo);
            controlRequest.setAddditionalInfo(additionalInfo);
        }

        if (log.isDebugEnabled()) {
            log.debug("starting program " + programId + " using gear " + gearNumber +
                      "; additionalInfo = " + additionalInfo + "; non-blocking");
        }

        return controlRequest;
    }

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

    private String getGearNameForProgram(Program program, int gearNumber) {
        if (program instanceof GearProgram) {
            ProgramDirectGear gear =
                ((GearProgram) program).getDirectGearVector().get(gearNumber - 1);
            return gear.getGearName();
        }
        return null;
    }

    @Override
    public void stopProgram(int programId) {
        Program program = loadControlClientConnection.getProgram(programId);
        Date stopDate = new Date();
        ManualControlRequestMessage controlRequest =
            program.createStartStopNowMsg(stopDate, 1, null, false,
                                          ManualControlRequestMessage.CONSTRAINTS_FLAG_USE);
        loadControlClientConnection.write(controlRequest);

        demandResponseEventLogService.programStopped(program.getYukonName(),
                                                     stopDate, null);
    }

    @Override
    public ProgramStatus stopProgramBlocking(int programId, Date stopDate, Duration stopOffset) 
            throws TimeoutException, BadServerResponseException {
        return stopProgramBlocking(programId, stopDate, stopOffset, true);
    }

    @Override
    public void stopProgram(int programId, Date stopDate, Duration stopOffset) throws BadServerResponseException {
        try {
            stopProgramBlocking(programId, stopDate, stopOffset, false);
        } catch (TimeoutException e) {
            // This isn't actually thrown since we're passing false for block.
        }
    }

    @Override
    public ProgramStatus stopProgram(int programId, Date stopTime, boolean force, boolean observeConstraints)
            throws TimeoutException, BadServerResponseException {
        
        if (stopTime == null) {
            stopTime = CtiUtilities.get1990GregCalendar().getTime();
        }

        Program program = loadControlClientConnection.getProgramSafe(programId);
        int gearNumber = ((ProgramDirect)program).getCurrentGearNumber();

        ProgramStatus programStatus = new ProgramStatus(program);

        ConstraintViolations checkViolations = null;

        if (!force) {
            checkViolations = getConstraintViolationsForStopProgram(programId, gearNumber, stopTime);

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
            programStatus = stopProgramBlocking(programId, stopTime, Duration.ZERO);
        }
        return programStatus;
    }

    private ProgramStatus stopProgramBlocking(int programId, Date stopDate, Duration stopOffset, boolean block)
            throws TimeoutException, BadServerResponseException {
        
        Program program = loadControlClientConnection.getProgram(programId);
        Date programStopDate = datePlusOffset(stopDate, stopOffset);
        Date startDate = CtiUtilities.get1990GregCalendar().getTime();
        ManualControlRequestMessage controlRequest = program.createScheduledStopMsg(startDate, programStopDate, 1, null);
        
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
                                         final LiteYukonUser user) throws NotFoundException, NotAuthorizedException {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    stopScenarioBlocking(scenarioId,  stopTime, overrideConstraints, observeConstraints, user);
                } catch (Exception e) {
                    log.debug("Error while running scenario start asynchronously. scenarioId = " + scenarioId, e);
                }
            }
        });
    }

    @Override
    public List<ProgramStatus> stopScenarioBlocking(int scenarioId,  Date stopTime, boolean overrideConstraints,
                                                     boolean observeConstraints, LiteYukonUser user)
                         throws NotFoundException, TimeoutException, NotAuthorizedException, ConnectionException, BadServerResponseException {

        if (!loadControlClientConnection.isValid()) {
            throw new ConnectionException("The Load Management server connection is not valid.");
        }

        if (!paoAuthorizationService.isAuthorized(user, Permission.LM_VISIBLE, paoDao.getLiteYukonPAO(scenarioId))) {
            throw new NotAuthorizedException("Scenario is not visible to user id=" + scenarioId + "");
        }

        List<Integer> programIds = loadControlProgramDao.getProgramIdsByScenarioId(scenarioId);
        List<Program> programs = loadControlClientConnection.getProgramsForProgramIds(programIds);
        Map<Integer, ScenarioProgram> scenarioPrograms =  scenarioDao.findScenarioProgramsForScenario(scenarioId);
        List<ProgramStatus> programStatuses = new ArrayList<>();

        for (Program program : programs) {
            ScenarioProgram scenarioProgram = scenarioPrograms.get(program.getYukonId());

            Date programStopDate = datePlusOffset(stopTime, scenarioProgram.getStopOffset());
            ProgramStatus programStatus = stopProgram(program.getYukonId(), programStopDate, overrideConstraints, observeConstraints);

            programStatuses.add(programStatus);
        }

        return programStatuses;   
    }

    @Override
    public ConstraintViolations getConstraintViolationsForStopProgram(int programId, int gearNumber, 
                                                                      Date stopDate) {
        Program program = loadControlClientConnection.getProgram(programId);
        Date startDate = CtiUtilities.get1990GregCalendar().getTime();
        ManualControlRequestMessage changeGearRequest = 
            program.createScheduledStopMsg(
                    startDate, 
                    stopDate,
                    gearNumber, null);
        changeGearRequest.setConstraintFlag(ManualControlRequestMessage.CONSTRAINTS_FLAG_CHECK);
        changeGearRequest.setCommand(ManualControlRequestMessage.CHANGE_GEAR);

        return makeServerRequest(changeGearRequest);
    }

    @Override
    public void stopProgramWithGear(int programId, int gearNumber, Date stopDate, 
                                    boolean overrideConstraints) {
        int constraintId = overrideConstraints
            ? ManualControlRequestMessage.CONSTRAINTS_FLAG_OVERRIDE
            : ManualControlRequestMessage.CONSTRAINTS_FLAG_USE;
        Program program = loadControlClientConnection.getProgram(programId);
        String gearName = getGearNameForProgram(program, gearNumber);
        Date startDate = CtiUtilities.get1990GregCalendar().getTime();
        ManualControlRequestMessage changeGearRequest = 
            program.createScheduledStopMsg(
                    startDate, 
                    stopDate,
                    gearNumber, null);
        changeGearRequest.setConstraintFlag(constraintId);
        changeGearRequest.setCommand(ManualControlRequestMessage.CHANGE_GEAR);

        ServerRequest serverRequest = new ServerRequestImpl();
        serverRequest.makeServerRequest(loadControlClientConnection, changeGearRequest);

        demandResponseEventLogService.programStopped(program.getYukonName(),
                                                     stopDate, gearName);
    }

    private ConstraintViolations makeServerRequest(ManualControlRequestMessage controlRequest) {
        ServerRequest serverRequest = new ServerRequestImpl();
        com.cannontech.messaging.message.server.ServerResponseMessage serverResponse =
            serverRequest.makeServerRequest(loadControlClientConnection, controlRequest);

        List<ConstraintContainer> violations = Lists.newArrayList();
        ManualControlResponseMessage lmResp = (ManualControlResponseMessage) serverResponse.getPayload();
        
        violations = LCUtils.convertViolationsToContainers(lmResp.getConstraintViolations());

        return new ConstraintViolations(violations);
    }

    @Override
    public DisplayablePao getProgram(int programId) {
        return programDao.getProgram(programId);
    }
    
    @Override
    public void changeGear(int programId, int gearNumber) {
        
        Program program = loadControlClientConnection.getProgram(programId);
        Date stopDate = program.getStopTime().getTime();
        
        // Change gears is only available for programs in the Manual Active state
        if(Program.STATUS_MANUAL_ACTIVE != program.getProgramStatus()) {
            throw new IllegalStateException("The program " + 
                                            program.getYukonName() + 
                                            " must be in the Manual Active state to change gears.");
        }
        
        ManualControlRequestMessage changeGearRequest = 
            program.createStartStopNowMsg(stopDate, 
                                          gearNumber, 
                                          null, 
                                          true, 
                                          ManualControlRequestMessage.CONSTRAINTS_FLAG_USE) ;
        changeGearRequest.setCommand(ManualControlRequestMessage.CHANGE_GEAR);
        changeGearRequest.setStartTime(program.getStartTime());

        ServerRequest serverRequest = new ServerRequestImpl();
        serverRequest.makeServerRequest(loadControlClientConnection, changeGearRequest);
        
        String gearName = getGearNameForProgram(program, gearNumber);
        demandResponseEventLogService.programChangeGear(program.getYukonName(), gearName);
    }

    @Override
    public void setEnabled(int programId, boolean isEnabled) {

        int loadControlCommand = isEnabled ? CommandMessage.ENABLE_PROGRAM
                : CommandMessage.DISABLE_PROGRAM;
        BaseMessage msg = new CommandMessage(loadControlCommand, programId, 0, 0.0);
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
        BaseMessage msg = new CommandMessage(CommandMessage.EMERGENCY_DISABLE_PROGRAM, programId, 0, 0.0);
        loadControlClientConnection.write(msg);
        
        DisplayablePao program = getProgram(programId);
        demandResponseEventLogService.programDisabled(program.getName());
    }
    
    private ManualControlRequestMessage getManualControlMessage(
            int programId, int gearNumber, Date startDate, Date stopDate, int constraintId) {
        if (stopDate == null) {
            throw new IllegalArgumentException("startDate cannot be null");
        }

        Program program = loadControlClientConnection.getProgram(programId);
        String additionalInfo = program instanceof ProgramDirect
            ? ((ProgramDirect) program).getAddtionalInfo() : null;
        if (startDate == null) {
            return program.createStartStopNowMsg(stopDate, gearNumber,
                                                 additionalInfo, true,
                                                 constraintId);
        }

        return program.createScheduledStartMsg(startDate, stopDate, gearNumber,
                                               null, additionalInfo, constraintId);
    }
}
