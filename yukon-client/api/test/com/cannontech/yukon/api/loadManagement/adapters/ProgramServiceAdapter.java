package com.cannontech.yukon.api.loadManagement.adapters;

import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.joda.time.Duration;

import com.cannontech.common.bulk.filter.UiFilter;
import com.cannontech.common.exception.NotAuthorizedException;
import com.cannontech.common.pao.DisplayablePao;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.program.widget.model.ProgramData;
import com.cannontech.common.search.result.SearchResults;
import com.cannontech.common.util.DatedObject;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.database.data.lite.LiteYukonUser;
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
                                                                             Date stopDate) {
        throw new UnsupportedOperationException("Not Implemented");
    }
    
    @Override
    public List<ConstraintViolations> getConstraintViolationForStopScenario(int scenarioId,
                                                                            Date stopDate) {
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
                                                                      List<GearAdjustment> gearAdjustments) {
        throw new UnsupportedOperationException("Not Implemented");
    }

    @Override
    public void startProgram(int programId, int gearNumber, Date startDate, Duration startOffset,
                                      boolean stopScheduled, Date stopDate, Duration stopOffset,
                                      boolean overrideConstraints, List<GearAdjustment> gearAdjustments) {
        throw new UnsupportedOperationException("Not Implemented");
    }

    @Override
    public void stopProgram(int programId, Date stopDate, Duration stopOffset) {
        throw new UnsupportedOperationException("Not Implemented");
    }

    @Override
    public void stopProgram(int programId) {
        throw new UnsupportedOperationException("Not Implemented");
    }

    @Override
    public ConstraintViolations getConstraintViolationsForStopProgram(int programId,
                                                                      int gearNumber, Date stopDate) {
        throw new UnsupportedOperationException("Not Implemented");
    }

    @Override
    public void stopProgramWithGear(int programId, int gearNumber, Date stopDate,
                                    boolean overrideConstraints) {
        throw new UnsupportedOperationException("Not Implemented");
    }

    @Override
    public void changeGear(int programId, int gearNumber) {
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
                             boolean overrideConstraints, List<GearAdjustment> gearAdjustments) throws TimeoutException {
        throw new UnsupportedOperationException("Not Implemented");
    }

    @Override
    public ProgramStatus stopProgramBlocking(int programId, Date stopDate,
                                                     Duration stopOffset) throws TimeoutException {
        throw new UnsupportedOperationException("Not Implemented");
    }

    @Override
    public ProgramStatus stopProgram(int programid, Date stopTime,
                                                          boolean force, boolean observeConstraints)
            throws TimeoutException {
        throw new UnsupportedOperationException("Not Implemented");
    }

    @Override
    public ProgramStatus startProgram(int programId, Date startTime, Date stopTime,
                                            String gearName, boolean force,
                                            boolean observeConstraints, LiteYukonUser liteYukonUser)
            throws NotAuthorizedException, NotFoundException, TimeoutException,
            BadServerResponseException {
        throw new UnsupportedOperationException("Not Implemented");
    }

    @Override
    public List<ProgramStatus> startScenarioBlocking(int scenarioId, Date startTime,
                                                      Date stopTime, boolean forceStart,
                                                      boolean observeConstraintsAndExecute,
                                                      LiteYukonUser user) throws NotFoundException,
            TimeoutException, NotAuthorizedException, BadServerResponseException,
            ConnectionException {
        throw new UnsupportedOperationException("Not Implemented");
    }

    @Override
    public void startScenario(int scenarioId, Date startTime, Date stopTime,
                                          boolean overrideConstraints, boolean observeConstraints,
                                          LiteYukonUser user) throws NotFoundException,
            TimeoutException, NotAuthorizedException, BadServerResponseException,
            ConnectionException {
        throw new UnsupportedOperationException("Not Implemented");
    }

    @Override
    public void stopScenario(int scenarioId, Date stopTime, boolean forceStop,
                                         boolean observeConstraintsAndExecute, LiteYukonUser user)
            throws NotFoundException, NotAuthorizedException {
        throw new UnsupportedOperationException("Not Implemented");
    }

    @Override
    public List<ProgramStatus> stopScenarioBlocking(int scenarioId, Date stopTime,
                                                     boolean forceStop,
                                                     boolean observeConstraintsAndExecute,
                                                     LiteYukonUser user) throws NotFoundException,
            TimeoutException, NotAuthorizedException, BadServerResponseException {
        throw new UnsupportedOperationException("Not Implemented");
    }

    @Override
    public Map<String, List<ProgramData>> buildProgramWidgetData(YukonUserContext userContext) {
        throw new UnsupportedOperationException("Not Implemented");
    }

    @Override
    public Map<String, List<ProgramData>> getProgramsHistoryDetail(DateTime from, DateTime to, YukonUserContext userContext) {
        throw new UnsupportedOperationException("Not Implemented");
    }

    public Map<String, List<ProgramData>> buildProgramDetailsData(YukonUserContext userContext) {
        throw new UnsupportedOperationException("Not Implemented");
    }
}
