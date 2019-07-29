package com.cannontech.multispeak.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.exception.NotAuthorizedException;
import com.cannontech.common.point.PointQuality;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.ProgramNotFoundException;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.dr.model.ProgramOriginSource;
import com.cannontech.dr.program.service.ProgramService;
import com.cannontech.loadcontrol.dao.LoadControlProgramDao;
import com.cannontech.loadcontrol.data.LMProgramBase;
import com.cannontech.loadcontrol.service.data.ProgramStatus;
import com.cannontech.loadcontrol.service.data.ScenarioStatus;
import com.cannontech.message.util.BadServerResponseException;
import com.cannontech.message.util.TimeoutException;

public abstract class MultispeakLMServiceBase {

    @Autowired protected ProgramService programService;
    @Autowired protected LoadControlProgramDao loadControlProgramDao;

    public String buildFdrMultispeakLMTranslation(String objectId) {
        String objectIdStr = "ObjectId:" + objectId + ";";
        String pointTypeStr = "POINTTYPE:Analog;";

        return objectIdStr + pointTypeStr;
    }

    public ProgramStatus startControlByProgramName(String programName, Date startTime, Date stopTime,
            LiteYukonUser liteYukonUser) throws NotAuthorizedException, NotFoundException, TimeoutException,
            BadServerResponseException {
        int programId;
        try {
            programId = loadControlProgramDao.getProgramIdByProgramName(programName);
        } catch (NotFoundException e) {
            throw new ProgramNotFoundException(e.getMessage(), e);
        }
        return programService.startProgram(programId, startTime, stopTime, null, false, true, liteYukonUser, ProgramOriginSource.MULTISPEAK);
    }

    public ProgramStatus stopControlByProgramName(String programName, Date stopTime, LiteYukonUser liteYukonUser)
            throws NotAuthorizedException, NotFoundException, TimeoutException, BadServerResponseException {
        int programId;
        try {
            programId = loadControlProgramDao.getProgramIdByProgramName(programName);
        } catch (NotFoundException e) {
            throw new ProgramNotFoundException(e.getMessage(), e);
        }
        return programService.stopProgram(programId, stopTime, false, true, ProgramOriginSource.MULTISPEAK);
    }

    public ScenarioStatus startControlByControlScenario(String scenarioName, Date startTime, Date stopTime,
            LiteYukonUser liteYukonUser) throws NotAuthorizedException, NotFoundException, TimeoutException,
            BadServerResponseException {
        int scenarioId = loadControlProgramDao.getScenarioIdForScenarioName(scenarioName);
        List<ProgramStatus> programStatuses =
            programService.startScenarioBlocking(scenarioId, startTime, stopTime, false, true, liteYukonUser, ProgramOriginSource.MULTISPEAK);
        ScenarioStatus scenarioStatus = new ScenarioStatus(scenarioName, programStatuses);

        return scenarioStatus;
    }

    public ScenarioStatus stopControlByControlScenario(String scenarioName, Date stopTime, LiteYukonUser liteYukonUser)
            throws NotAuthorizedException, NotFoundException, TimeoutException, BadServerResponseException {
        int scenarioId = loadControlProgramDao.getScenarioIdForScenarioName(scenarioName);
        List<ProgramStatus> programStatuses =
            programService.stopScenarioBlocking(scenarioId, stopTime, false, true, liteYukonUser, ProgramOriginSource.MULTISPEAK);
        ScenarioStatus scenarioStatus = new ScenarioStatus(scenarioName, programStatuses);

        return scenarioStatus;
    }

    /**
     * Helper method to return the total count of devices in lmProgramBases
     * that are active (enrolled on an account) but are not currently opted out.
     * If a program is null, a count of 0 is used
     * 
     * @param lmProgramBases
     * @param programCounts
     * @return count of all the programs in scenerio
     */
    protected Integer getActiveCount(List<LMProgramBase> lmProgramBases, Map<Integer, Integer> programCounts) {
        Integer count = 0;
        for (LMProgramBase program : lmProgramBases) {
            if (program != null) {
                // Combine counts for all programs in the scenario
                Integer programCount = programCounts.get(program.getYukonID());
                count += (programCount != null ? programCount : 0);
            }
        }
        return count;
    }

    /**
     * Helper method to return the total count of devices in lmProgramBases
     * that are active (enrolled on an account) but are not currently opted out.
     * Additionally, the lmProgramBase must be active for the count to be included.
     * If a program is null, a count of 0 is used
     * 
     * @param lmProgramBases
     * @param programCounts
     * @return controlledCount
     */
    protected Integer getActiveControlledCount(List<LMProgramBase> lmProgramBases, Map<Integer, Integer> programCounts) {
        Integer controlledCount = 0;
        for (LMProgramBase program : lmProgramBases) {
            if (program != null) {
                // Combine counts for all programs in the scenario
                Integer programCount = programCounts.get(program.getYukonID());
                controlledCount += (program.isActive() && programCount != null ? programCount : 0); // controlled items is 0 if not active program
            }
        }
        return controlledCount;
    }

    /**
     * Used to specially format a PointQuality for Power Supplier Loads (see valueQuality.tag) 
     */
    public static String getPointQualityLetter(PointQuality pointQuality) {

        if (PointQuality.NonUpdated.equals(pointQuality)) {
            return "F";
        } else if (PointQuality.Manual.equals(pointQuality)) {
            return "M";
        } else {
            return " ";
        }
    }
}
