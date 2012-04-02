package com.cannontech.dr.program.service.impl;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Duration;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.bulk.filter.AbstractRowMapperWithBaseQuery;
import com.cannontech.common.bulk.filter.RowMapperWithBaseQuery;
import com.cannontech.common.bulk.filter.UiFilter;
import com.cannontech.common.bulk.filter.service.FilterService;
import com.cannontech.common.events.loggers.DemandResponseEventLogService;
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
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.core.service.SystemDateFormattingService;
import com.cannontech.database.YukonResultSet;
import com.cannontech.dr.program.dao.ProgramDao;
import com.cannontech.dr.program.filter.ForLoadGroupFilter;
import com.cannontech.dr.program.model.GearAdjustment;
import com.cannontech.dr.program.service.ConstraintContainer;
import com.cannontech.dr.program.service.ConstraintViolations;
import com.cannontech.dr.program.service.ProgramService;
import com.cannontech.dr.scenario.model.ScenarioProgram;
import com.cannontech.loadcontrol.LCUtils;
import com.cannontech.loadcontrol.LoadControlClientConnection;
import com.cannontech.loadcontrol.data.IGearProgram;
import com.cannontech.loadcontrol.data.LMProgramBase;
import com.cannontech.loadcontrol.data.LMProgramDirect;
import com.cannontech.loadcontrol.data.LMProgramDirectGear;
import com.cannontech.loadcontrol.messages.LMCommand;
import com.cannontech.loadcontrol.messages.LMManualControlRequest;
import com.cannontech.loadcontrol.messages.LMManualControlResponse;
import com.cannontech.message.server.ServerResponseMsg;
import com.cannontech.message.util.Message;
import com.cannontech.message.util.ServerRequest;
import com.cannontech.message.util.ServerRequestImpl;
import com.cannontech.user.YukonUserContext;
import com.google.common.collect.Lists;

public class ProgramServiceImpl implements ProgramService {
    private Logger log = YukonLogManager.getLogger(ProgramServiceImpl.class);
    private ProgramDao programDao = null;
    private LoadControlClientConnection loadControlClientConnection = null;
    private FilterService filterService;
    private DemandResponseEventLogService demandResponseEventLogService;
    private SystemDateFormattingService systemDateFormattingService;
    private DateFormattingService dateFormattingService;
    private PaoDefinitionDao paoDefinitionDao;
    
    private RowMapperWithBaseQuery<DisplayablePao> rowMapper =
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
    public ConstraintViolations getConstraintViolationForStartProgram(
            int programId, int gearNumber, Date startDate,
            Duration startOffset, Date stopDate, Duration stopOffset,
            List<GearAdjustment> gearAdjustments) {
        Date programStartDate = datePlusOffset(startDate, startOffset);
        Date programStopDate = datePlusOffset(stopDate, stopOffset);

        LMManualControlRequest controlRequest =
            getManualControlMessage(programId, gearNumber,
                                    programStartDate, programStopDate,
                                    LMManualControlRequest.CONSTRAINTS_FLAG_CHECK);

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
    public void startProgram(int programId, int gearNumber, Date startDate,
            Duration startOffset, boolean stopScheduled, Date stopDate,
            Duration stopOffset, boolean overrideConstraints,
            List<GearAdjustment> gearAdjustments) {

        Date programStartDate = datePlusOffset(startDate, startOffset);
        Date programStopDate = datePlusOffset(stopDate, stopOffset);
        int constraintId = overrideConstraints
            ? LMManualControlRequest.CONSTRAINTS_FLAG_OVERRIDE
            : LMManualControlRequest.CONSTRAINTS_FLAG_USE;
        LMManualControlRequest controlRequest =
            getManualControlMessage(programId, gearNumber, programStartDate,
                                    programStopDate, constraintId);

        LMProgramBase program = loadControlClientConnection.getProgram(programId);
        String gearName = getGearNameForProgram(program, gearNumber);
        String additionalInfo =
            additionalInfoFromGearAdjustments(gearAdjustments, programStartDate,
                                              programStopDate);
        if (additionalInfo != null) {
            ((LMProgramDirect) program).setAddtionalInfo(additionalInfo);
            controlRequest.setAddditionalInfo(additionalInfo);
        }
        log.debug("starting program " + programId + " using gear " + gearNumber +
                  " at " + startDate + " + " + startOffset + " = " + programStartDate +
                  "; stopping at " + stopDate + " + " + stopOffset + " = " + programStopDate +
                  "; additionalInfo = " + additionalInfo);

        // TODO:  Do we need to check the response from the server here?
        // (The old 3-tier didn't so what the response means exactly needs to
        // be researched.)
        ServerRequest serverRequest = new ServerRequestImpl();
        serverRequest.makeServerRequest(loadControlClientConnection, controlRequest);

        demandResponseEventLogService.programScheduled(program.getYukonName(),
                                                       overrideConstraints,
                                                       gearName,
                                                       programStartDate,
                                                       stopScheduled,
                                                       programStopDate);
    }

    protected Date datePlusOffset(Date date, Duration offset) {
        TimeZone timeZone = systemDateFormattingService.getSystemTimeZone();
        DateTimeZone dateTimeZone = DateTimeZone.forTimeZone(timeZone);
        DateTime dateTime = new DateTime(date, dateTimeZone);
        return dateTime.plus(offset).toDate();
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
    public void scheduleProgramStop(int programId, Date stopDate,
            Duration stopOffset) {
        LMProgramBase program = loadControlClientConnection.getProgram(programId);
        Date programStopDate = datePlusOffset(stopDate, stopOffset);
        Date startDate = CtiUtilities.get1990GregCalendar().getTime();
        LMManualControlRequest controlRequest =
            program.createScheduledStopMsg(startDate, programStopDate, 1, null);
        loadControlClientConnection.write(controlRequest);

        demandResponseEventLogService.programStopScheduled(program.getYukonName(),
                                                           programStopDate, null);
    }

    @Override
    public void stopProgram(int programId) {
        LMProgramBase program = loadControlClientConnection.getProgram(programId);
        Date stopDate = new Date();
        LMManualControlRequest controlRequest =
            program.createStartStopNowMsg(stopDate, 1, null, false,
                                          LMManualControlRequest.CONSTRAINTS_FLAG_USE);
        loadControlClientConnection.write(controlRequest);

        demandResponseEventLogService.programStopped(program.getYukonName(),
                                                     stopDate, null);
    }

    @Override
    public ConstraintViolations getConstraintViolationsForStopProgram(int programId, int gearNumber, 
                                                                      Date stopDate) {
        LMProgramBase program = loadControlClientConnection.getProgram(programId);
        Date startDate = CtiUtilities.get1990GregCalendar().getTime();
        LMManualControlRequest changeGearRequest = 
            program.createScheduledStopMsg(
                    startDate, 
                    stopDate,
                    gearNumber, null);
        changeGearRequest.setConstraintFlag(LMManualControlRequest.CONSTRAINTS_FLAG_CHECK);
        changeGearRequest.setCommand(LMManualControlRequest.CHANGE_GEAR);

        return makeServerRequest(changeGearRequest);
    }

    @Override
    public void stopProgramWithGear(int programId, int gearNumber, Date stopDate, 
                                    boolean overrideConstraints) {
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
                    gearNumber, null);
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
    public void changeGear(int programId, int gearNumber) {
        
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
                                          LMManualControlRequest.CONSTRAINTS_FLAG_USE) ;
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
    
    @Autowired
    public void setProgramDao(ProgramDao programDao) {
        this.programDao = programDao;
    }

    @Autowired
    public void setLoadControlClientConnection(
            LoadControlClientConnection loadControlClientConnection) {
        this.loadControlClientConnection = loadControlClientConnection;
    }

    @Autowired
    public void setFilterService(FilterService filterService) {
        this.filterService = filterService;
    }

    @Autowired
    public void setDemandResponseEventLogService(
            DemandResponseEventLogService demandResponseEventLogService) {
        this.demandResponseEventLogService = demandResponseEventLogService;
    }

    @Autowired
    public void setSystemDateFormattingService(
            SystemDateFormattingService systemDateFormattingService) {
        this.systemDateFormattingService = systemDateFormattingService;
    }

    @Autowired
    public void setDateFormattingService(DateFormattingService dateFormattingService) {
        this.dateFormattingService = dateFormattingService;
    }
    
    @Autowired
    public void setPaoDefinitionDao(PaoDefinitionDao paoDefinitionDao) {
		this.paoDefinitionDao = paoDefinitionDao;
	}

    private LMManualControlRequest getManualControlMessage(
            int programId, int gearNumber, Date startDate, Date stopDate, int constraintId) {
        if (stopDate == null) {
            throw new IllegalArgumentException("startDate cannot be null");
        }

        LMProgramBase program = loadControlClientConnection.getProgram(programId);
        String additionalInfo = program instanceof LMProgramDirect
            ? ((LMProgramDirect) program).getAddtionalInfo() : null;
        if (startDate == null) {
            return program.createStartStopNowMsg(stopDate, gearNumber,
                                                 additionalInfo, true,
                                                 constraintId);
        }

        return program.createScheduledStartMsg(startDate, stopDate, gearNumber,
                                               null, additionalInfo, constraintId);
    }
}
