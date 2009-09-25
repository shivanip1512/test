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
import com.cannontech.common.bulk.mapper.ObjectMappingException;
import com.cannontech.common.pao.DisplayablePao;
import com.cannontech.common.pao.DisplayablePaoBase;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.search.SearchResult;
import com.cannontech.common.util.DatedObject;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.dr.program.dao.ProgramDao;
import com.cannontech.dr.program.filter.ForLoadGroupFilter;
import com.cannontech.dr.program.model.ProgramDisplayField;
import com.cannontech.dr.program.service.ConstraintViolations;
import com.cannontech.dr.program.service.ProgramService;
import com.cannontech.loadcontrol.LoadControlClientConnection;
import com.cannontech.loadcontrol.data.LMProgramBase;
import com.cannontech.loadcontrol.data.LMProgramDirect;
import com.cannontech.loadcontrol.messages.LMManualControlRequest;
import com.cannontech.loadcontrol.messages.LMManualControlResponse;
import com.cannontech.message.server.ServerResponseMsg;
import com.cannontech.message.util.ServerRequest;
import com.cannontech.message.util.ServerRequestImpl;
import com.cannontech.user.YukonUserContext;
import com.google.common.collect.Ordering;

public class ProgramServiceImpl implements ProgramService {
    private ProgramDao programDao = null;
    private LoadControlClientConnection loadControlClientConnection = null;
    private FilterService filterService;

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
    public LMProgramBase map(DisplayablePao from) throws ObjectMappingException {
        DatedObject<LMProgramBase> datedProgram =
            loadControlClientConnection.getDatedProgram(from.getPaoIdentifier().getPaoId());
        return datedProgram == null ? null : datedProgram.getObject();
    }

    @Override
    public DatedObject<LMProgramBase> findDatedProgram(int programId) {
        return loadControlClientConnection.getDatedProgram(programId);
    }

    @Override
    public List<DisplayablePao> findProgramsForLoadGroup(
            YukonUserContext userContext, int loadGroupId) {
        UiFilter<DisplayablePao> filter = new ForLoadGroupFilter(loadGroupId);

        SearchResult<DisplayablePao> searchResult =
            filterPrograms(userContext, filter, null, 0, Integer.MAX_VALUE);

        return searchResult.getResultList();
    }

    @Override
    public SearchResult<DisplayablePao> filterPrograms(
            YukonUserContext userContext, UiFilter<DisplayablePao> filter,
            Comparator<DisplayablePao> sorter, int startIndex, int count) {

        Comparator<DisplayablePao> defaultSorter = ProgramDisplayField.NAME.getSorter(this, userContext, false);
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
            YukonUserContext userContext, int programId, int gearNumber,
            Date startDate, Date stopDate) {
        LMManualControlRequest controlRequest =
            getManualControlMessage(userContext, programId, gearNumber,
                                    startDate, stopDate,
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
    public void startProgram(YukonUserContext userContext, int programId,
            int gearNumber, Date startDate, Date stopDate,
            boolean overrideConstraints) {

        int constraintId = overrideConstraints
            ? LMManualControlRequest.CONSTRAINTS_FLAG_OVERRIDE
            : LMManualControlRequest.CONSTRAINTS_FLAG_USE;
        LMManualControlRequest controlRequest =
            getManualControlMessage(userContext, programId, gearNumber,
                                    startDate, stopDate, constraintId);

        // TODO:  Do we need to check the response from the server here?
        // (The old 3-tier didn't so what the response means exactly needs to
        // be researched.)
        ServerRequest serverRequest = new ServerRequestImpl();
        serverRequest.makeServerRequest(loadControlClientConnection, controlRequest);
    }

    @Override
    public DisplayablePao getProgram(int programId) {
        return programDao.getProgram(programId);
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

    private LMManualControlRequest getManualControlMessage(
            YukonUserContext userContext, int programId, int gearNumber,
            Date startDate, Date stopDate, int constraintId) {
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
