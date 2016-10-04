package com.cannontech.loadcontrol.service;

import java.util.Date;
import java.util.List;
import java.util.Set;

import com.cannontech.common.exception.NotAuthorizedException;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.loadcontrol.service.data.ProgramControlHistory;
import com.cannontech.loadcontrol.service.data.ProgramStatus;
import com.cannontech.loadcontrol.service.data.ProgramStatusType;
import com.cannontech.loadcontrol.service.data.ScenarioProgramStartingGears;

public interface LoadControlService {

    /**
     * Find program id by name in database them retrieve program from
     * connection cache. If the cache contains no program for id then return
     * null.
     * @param programName
     * @param user will be checked against user/group pao permission tables to validate access to program,
     * a program is visible to the user either because it is directly visible, or belongs to a control area that is visible.
     * @return A ProgramStatus object based on the Program
     * @throws NotFoundException if no program exists in database for given programName
     * @throws NotAuthorizedException if neither the user (nor any groups user belongs to) have neither the program (nor any of
     * the control areas the program belongs to) made visible to them.
     */
    public ProgramStatus getProgramStatusByProgramName(String programName, LiteYukonUser user) throws NotFoundException, NotAuthorizedException;

    /**
     * Returns a list of ProgramStatus where the program is active, and the program is visible to the user
     * either because it is directly visible, or belongs to a control area that is visible.
     * @return
     */
    public List<ProgramStatus> getAllCurrentlyActivePrograms(LiteYukonUser user);

    /**
     * Returns a list of ScenarioProgramStartingGears.
     * The ScenarioProgramStartingGears object contains the name of the scenario, and a list of 
     * ProgramStartingGear. Each ProgramStartingGear contains the name of the program, and the name
     * and id of its starting gear.
     * Only those scenarios that the user has visibility to are included.
     * @param user
     * @return
     */
    public List<ScenarioProgramStartingGears> getAllScenarioProgramStartingGears(LiteYukonUser user);
    
    /**
     * Returns ScenarioProgramStartingGears for the given scenarioName.
     * The ScenarioProgramStartingGears object contains the name of the scenario, and a list of 
     * ProgramStartingGear. Each ProgramStartingGear contains the name of the program, and the name
     * and id of its starting gear.
     * @param scenarioName
     * @param user will be checked against user/group pao permission tables to validate visibility of a scenario.
     * @return
     * @throws NotFoundException if no scenario exists for given scenarioName.
     * @throws NotAuthorizedException if neither the user (nor any groups user belongs to) have the scenario made visible to them.
     */
    public ScenarioProgramStartingGears getScenarioProgramStartingGearsByScenarioName(String scenarioName, LiteYukonUser user) throws NotFoundException, NotAuthorizedException;
    
    /**
     * Returns a list of ProgramControlHistory objects for the given programName, within the given
     * time period.
     * @param programName
     * @param fromTime inclusive. required
     * @param throughTime inclusive. optional (use null for "now")
     * @param user will be checked against user/group pao permission tables to validate access to program,
     * a program is visible to the user either because it is directly visible, or belongs to a control area that is visible.
     * @return
     * @throws NotFoundException if no program exists for given programName
     * @throws NotAuthorizedException for any of of the programs in the scenario - if neither the user (nor any groups user belongs to) have neither the program (nor any of
     * the control areas the program belongs to) made visible to them.
     */
    public List<ProgramControlHistory> getControlHistoryByProgramName(
			String programName, Date fromTime, Date throughTime,
			LiteYukonUser user) throws NotFoundException, NotAuthorizedException;
    
    /**
     * Returns a list of ProgramControlHistory objects  within the given time period.
     * Only programs visible to the user are included.
     * @param fromTime. required
     * @param throughTime. optional (use null for "now") 
     * @param user
     * @return
     */
    public List<ProgramControlHistory> getAllControlHistory(Date fromTime, Date throughTime, LiteYukonUser user);

    /**
     * Returns a list of all ProgramStatus (programName, currentStatus, start/stop dateTimes and gear
     * information.) if programStatusTypes (Active,Inactive, Scheduled) is empty otherwise it will return list of ProgramStatus that are
     * belongs to programStatusTypes and the programs will be visible
     * to the user either because it is directly visible, or belongs to a control area that is visible.
     * @param user
     * @param programStatusTypes
     * @return
     */
    List<ProgramStatus> getAllProgramStatus(LiteYukonUser user,  Set<ProgramStatusType> programStatusTypes);
}
