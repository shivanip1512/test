package com.cannontech.dr.loadprogram.service;

import java.util.List;

import com.cannontech.common.dr.program.setup.model.LoadProgram;
import com.cannontech.common.dr.program.setup.model.LoadProgramCopy;
import com.cannontech.common.dr.program.setup.model.NotificationGroup;
import com.cannontech.common.dr.program.setup.model.ProgramDirectMemberControl;
import com.cannontech.common.dr.program.setup.model.ProgramGroup;
import com.cannontech.common.dr.setup.ProgramDetails;
import com.cannontech.common.pao.PaoType;
import com.cannontech.dr.setup.service.LMSetupService;

public interface LoadProgramSetupService extends LMSetupService<LoadProgram, LoadProgramCopy> {

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
    
    /**
     * Return all the Load Groups associated with programType.
     */
    List<ProgramGroup> getAllProgramLoadGroups(PaoType programType);
}
