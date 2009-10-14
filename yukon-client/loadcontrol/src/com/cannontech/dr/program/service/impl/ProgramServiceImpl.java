package com.cannontech.dr.program.service.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

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
import com.cannontech.common.search.SearchResult;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.DatedObject;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.dr.program.dao.ProgramDao;
import com.cannontech.dr.program.filter.ForLoadGroupFilter;
import com.cannontech.dr.program.model.ProgramNameField;
import com.cannontech.dr.program.service.ConstraintViolations;
import com.cannontech.dr.program.service.ProgramService;
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
import com.google.common.collect.Ordering;

public class ProgramServiceImpl implements ProgramService {
    private ProgramDao programDao = null;
    private LoadControlClientConnection loadControlClientConnection = null;
    private FilterService filterService;
    private DemandResponseEventLogService demandResponseEventLogService;
    private ProgramNameField programNameField;

    private static RowMapperWithBaseQuery<DisplayablePao> rowMapper =
        new AbstractRowMapperWithBaseQuery<DisplayablePao>() {

            @Override
            public SqlFragmentSource getBaseQuery() {
                SqlStatementBuilder retVal = new SqlStatementBuilder();
                retVal.append("SELECT paObjectId, paoName FROM yukonPAObject"
                    + " WHERE type = ");
                retVal.appendArgument(PaoType.LM_DIRECT_PROGRAM.getDbString());
                return retVal;
            }

            @Override
            public DisplayablePao mapRow(ResultSet rs, int rowNum)
                    throws SQLException {
                PaoIdentifier paoId = new PaoIdentifier(rs.getInt("paObjectId"),
                                                        PaoType.LM_DIRECT_PROGRAM);
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

        Comparator<DisplayablePao> defaultSorter = programNameField.getSorter(false, userContext);
        if (sorter == null) {
            sorter = defaultSorter;
        } else {
            sorter = Ordering.from(sorter).compound(defaultSorter);
        }
        SearchResult<DisplayablePao> searchResult =
            filterService.filter(filter, sorter, startIndex, count, rowMapper);
        return searchResult;
    }

    @Override
    public ConstraintViolations getConstraintViolationForStartProgram(
            int programId, int gearNumber, Date startDate, Date stopDate) {
        LMManualControlRequest controlRequest =
            getManualControlMessage(programId, gearNumber, startDate, stopDate,
                                    LMManualControlRequest.CONSTRAINTS_FLAG_CHECK);

        // better be a program for this Request Message!
        LMProgramBase program = loadControlClientConnection.getProgram(programId);

        if (program instanceof LMProgramDirect) {
            String additionalInfo = ((LMProgramDirect) program).getAddtionalInfo();
            if (additionalInfo != null) {
                // When two d's just aren't enough...
                controlRequest.setAddditionalInfo(additionalInfo);
            }
        }

        return makeServerRequest(controlRequest);
    }

    @Override
    public void startProgram(int programId, int gearNumber, Date startDate, boolean stopScheduled, 
                             Date stopDate, boolean overrideConstraints, String additionalInfo) {

        int constraintId = overrideConstraints
            ? LMManualControlRequest.CONSTRAINTS_FLAG_OVERRIDE
            : LMManualControlRequest.CONSTRAINTS_FLAG_USE;
        LMManualControlRequest controlRequest =
            getManualControlMessage(programId, gearNumber, startDate, stopDate, constraintId);

        LMProgramBase program = loadControlClientConnection.getProgram(programId);
        String gearName = getGearNameForProgram(program, gearNumber);
        if (additionalInfo != null) {
            ((LMProgramDirect) program).setAddtionalInfo(additionalInfo);
        }

        // TODO:  Do we need to check the response from the server here?
        // (The old 3-tier didn't so what the response means exactly needs to
        // be researched.)
        ServerRequest serverRequest = new ServerRequestImpl();
        serverRequest.makeServerRequest(loadControlClientConnection, controlRequest);

        demandResponseEventLogService.programScheduled(program.getYukonName(),
                                                       overrideConstraints,
                                                       gearName, startDate,
                                                       stopScheduled, stopDate);
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
    public void scheduleProgramStop(int programId, Date stopDate) {
        LMProgramBase program = loadControlClientConnection.getProgram(programId);
        Date startDate = CtiUtilities.get1990GregCalendar().getTime();
        LMManualControlRequest controlRequest =
            program.createScheduledStopMsg(startDate, stopDate, 1, null);
        loadControlClientConnection.write(controlRequest);

        demandResponseEventLogService.programStopScheduled(program.getYukonName(),
                                                           stopDate, null);
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

        if (serverResponse.getStatus() == ServerResponseMsg.STATUS_OK) {
            return null;
        }

        List<String> violations = new ArrayList<String>();
        LMManualControlResponse lmResp =
            (LMManualControlResponse) serverResponse.getPayload();
        if (lmResp != null) {
            for (Object violation : lmResp.getConstraintViolations()) {
                violations.add(violation.toString());
            }
        } else {
            // use the message from the response for our violation
            violations.add(serverResponse.getMessage());
        }
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
    public void setProgramNameField(ProgramNameField programNameField) {
        this.programNameField = programNameField;
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
