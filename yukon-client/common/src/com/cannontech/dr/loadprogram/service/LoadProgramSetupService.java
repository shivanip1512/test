package com.cannontech.dr.loadprogram.service;

import java.util.List;

import com.cannontech.common.dr.program.setup.model.LoadProgram;
import com.cannontech.common.dr.program.setup.model.LoadProgramCopy;
import com.cannontech.common.dr.program.setup.model.NotificationGroup;
import com.cannontech.common.dr.program.setup.model.ProgramDirectMemberControl;
import com.cannontech.common.dr.program.setup.model.ProgramGroup;
import com.cannontech.common.dr.setup.ProgramDetails;
import com.cannontech.common.pao.PaoType;

public interface LoadProgramSetupService {

    /**
     * Create the load program.
     */
    int create(LoadProgram loadProgram);

    /**
     * Retrieve load Program for the programId.
     */
    LoadProgram retrieve(int programId);

    /**
     * Delete the load program.
     */

    int delete(int programId, String programName);

    /**
     * Copy the load program.
     */

    int copy(int programId, LoadProgramCopy loadProgramCopy);

    /**
     * Update the load program.
     */

    int update(int programId, LoadProgram loadProgram);

    /**
     * Get all program Notification Groups.
     */
    List<NotificationGroup> getAllAvailableProgramNotificationGroups();

    /**
     * Get all program Member control Groups.
     */
    List<ProgramDirectMemberControl> getAllAvailableDirectMemberControls();

    /**
     * Get program Load Groups associated with paoType and programId.
     */
    List<ProgramGroup> getAvailableProgramLoadGroups(int programId);

    /**
     * Get program Notification Groups associated with programId.
     */
    List<NotificationGroup> getAvailableProgramNotificationGroups(int programId);

    /**
     * Get program Member control Groups associated with programId.
     */

    List<ProgramDirectMemberControl> getAvailableDirectMemberControls(int programId);

    /**
     * Get all program Load Groups associated with paoType.
     */

    List<ProgramGroup> getAllAvailableProgramLoadGroups(PaoType programType);

    /**
     * Return all the Programs which belong to Control Area.
     */

    List<ProgramDetails> getAvailablePrograms();

}
