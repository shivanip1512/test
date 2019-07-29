package com.cannontech.yukon.api.loadManagement.adapters;

import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.joda.time.Duration;

import com.cannontech.common.bulk.filter.UiFilter;
import com.cannontech.common.exception.NotAuthorizedException;
import com.cannontech.common.pao.DisplayablePao;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.search.result.SearchResults;
import com.cannontech.common.util.DatedObject;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.dr.model.ProgramOriginSource;
import com.cannontech.dr.program.model.GearAdjustment;
import com.cannontech.dr.program.service.ConstraintViolations;
import com.cannontech.dr.program.service.ProgramService;
import com.cannontech.dr.scenario.model.ScenarioProgram;
import com.cannontech.loadcontrol.data.LMProgramBase;
import com.cannontech.loadcontrol.service.data.ProgramStatus;
import com.cannontech.message.util.BadServerResponseException;
import com.cannontech.message.util.ConnectionException;
import com.cannontech.message.util.TimeoutException;
import com.cannontech.user.YukonUserContext;

public class ProgramServiceAdapter implements ProgramService {

    @Override
    public DisplayablePao getProgram(int programId) {
        throw new UnsupportedOperationException("Not Implemented");
    }

    @Override
    public LMProgramBase getProgramForPao(YukonPao from) {
        throw new UnsupportedOperationException("Not Implemented");
    }

    @Override
    public LMProgramBase getProgramForPaoSafe(YukonPao from) {
        throw new UnsupportedOperationException("Not Implemented");
    }

    @Override
    public DatedObject<LMProgramBase> findDatedProgram(int programId) {
        throw new UnsupportedOperationException("Not Implemented");
    }

    @Override
    public List<DisplayablePao> findProgramsForLoadGroup(int loadGroupId,
                                                         YukonUserContext userContext) {
        throw new UnsupportedOperationException("Not Implemented");
    }
    
    @Override
    public List<ConstraintViolations> getConstraintViolationForStartScenario(int scenarioId,
                                                                             Date startDate,
                                                                             Date stopDate,
                                                                             ProgramOriginSource programOriginSource) {
        throw new UnsupportedOperationException("Not Implemented");
    }
    
    @Override
    public List<ConstraintViolations> getConstraintViolationForStopScenario(int scenarioId,
                                                                            Date stopDate,
                                                                            ProgramOriginSource programOriginSource) {
        throw new UnsupportedOperationException("Not Implemented");
    }

    @Override
    public SearchResults<DisplayablePao> filterPrograms(UiFilter<DisplayablePao> filter,
                                                       Comparator<DisplayablePao> sorter,
                                                       int startIndex, int count,
                                                       YukonUserContext userContext) {
        throw new UnsupportedOperationException("Not Implemented");
    }

    @Override
    public List<GearAdjustment> getDefaultAdjustments(Date startDate, Date stopDate,
                                                      Collection<ScenarioProgram> scenarioPrograms,
                                                      YukonUserContext userContext) {
        throw new UnsupportedOperationException("Not Implemented");
    }

    @Override
    public ConstraintViolations getConstraintViolationForStartProgram(int programId,
                                                                      int gearNumber,
                                                                      Date startDate,
                                                                      Duration startOffset,
                                                                      Date stopDate,
                                                                      Duration stopOffset,
                                                                      List<GearAdjustment> gearAdjustments,
                                                                      ProgramOriginSource programOriginSource) {
        throw new UnsupportedOperationException("Not Implemented");
    }

    @Override
    public void startProgram(int programId, int gearNumber, Date startDate, Duration startOffset,
                                      boolean stopScheduled, Date stopDate, Duration stopOffset,
                                      boolean overrideConstraints, List<GearAdjustment> gearAdjustments,
                                      ProgramOriginSource programOriginSource) {
        throw new UnsupportedOperationException("Not Implemented");
    }

    @Override
    public void stopProgram(int programId, Date stopDate, Duration stopOffset, ProgramOriginSource programOriginSource) {
        throw new UnsupportedOperationException("Not Implemented");
    }

    @Override
    public void stopProgram(int programId, ProgramOriginSource programOriginSource) {
        throw new UnsupportedOperationException("Not Implemented");
    }

    @Override
    public ConstraintViolations getConstraintViolationsForStopProgram(int programId,
                                                                      int gearNumber, Date stopDate,
                                                                      ProgramOriginSource programOriginSource) {
        throw new UnsupportedOperationException("Not Implemented");
    }

    @Override
    public void stopProgramWithGear(int programId, int gearNumber, Date stopDate,
                                    boolean overrideConstraints, ProgramOriginSource programOriginSource) {
        throw new UnsupportedOperationException("Not Implemented");
    }

    @Override
    public void changeGear(int programId, int gearNumber, ProgramOriginSource programOriginSource) {
        throw new UnsupportedOperationException("Not Implemented");
    }

    @Override
    public void setEnabled(int programId, boolean isEnabled) {
        throw new UnsupportedOperationException("Not Implemented");
    }

    @Override
    public void disableAndSupressRestoration(int programId) {
        throw new UnsupportedOperationException("Not Implemented");
    }

    @Override
    public ProgramStatus startProgramBlocking(int programId, int gearNumber, Date startDate, Duration startOffset,
                             boolean stopScheduled, Date stopDate, Duration stopOffset,
                             boolean overrideConstraints, List<GearAdjustment> gearAdjustments,
                             ProgramOriginSource programOriginSource) throws TimeoutException {
        throw new UnsupportedOperationException("Not Implemented");
    }

    @Override
    public ProgramStatus stopProgramBlocking(int programId, Date stopDate,
                                                     Duration stopOffset, ProgramOriginSource programOriginSource) throws TimeoutException {
        throw new UnsupportedOperationException("Not Implemented");
    }

    @Override
    public ProgramStatus stopProgram(int programid, Date stopTime,
                                                          boolean force, boolean observeConstraints,
                                                          ProgramOriginSource programOriginSource)
            throws TimeoutException {
        throw new UnsupportedOperationException("Not Implemented");
    }

    @Override
    public ProgramStatus startProgram(int programId, Date startTime, Date stopTime,
                                            String gearName, boolean force,
                                            boolean observeConstraints, LiteYukonUser liteYukonUser,
                                            ProgramOriginSource programOriginSource)
            throws NotAuthorizedException, NotFoundException, TimeoutException,
            BadServerResponseException {
        throw new UnsupportedOperationException("Not Implemented");
    }

    @Override
    public List<ProgramStatus> startScenarioBlocking(int scenarioId, Date startTime,
                                                      Date stopTime, boolean forceStart,
                                                      boolean observeConstraintsAndExecute,
                                                      LiteYukonUser user, ProgramOriginSource programOriginSource) throws NotFoundException,
            TimeoutException, NotAuthorizedException, BadServerResponseException,
            ConnectionException {
        throw new UnsupportedOperationException("Not Implemented");
    }

    @Override
    public void startScenario(int scenarioId, Date startTime, Date stopTime,
                                          boolean overrideConstraints, boolean observeConstraints,
                                          LiteYukonUser user, ProgramOriginSource programOriginSource) throws NotFoundException,
            TimeoutException, NotAuthorizedException, BadServerResponseException,
            ConnectionException {
        throw new UnsupportedOperationException("Not Implemented");
    }

    @Override
    public void stopScenario(int scenarioId, Date stopTime, boolean forceStop,
                                         boolean observeConstraintsAndExecute, LiteYukonUser user,
                                         ProgramOriginSource programOriginSource)
            throws NotFoundException, NotAuthorizedException {
        throw new UnsupportedOperationException("Not Implemented");
    }

    @Override
    public List<ProgramStatus> stopScenarioBlocking(int scenarioId, Date stopTime,
                                                     boolean forceStop,
                                                     boolean observeConstraintsAndExecute,
                                                     LiteYukonUser user, ProgramOriginSource programOriginSource) throws NotFoundException,
            TimeoutException, NotAuthorizedException, BadServerResponseException {
        throw new UnsupportedOperationException("Not Implemented");
    }
}
