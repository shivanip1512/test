package com.cannontech.dr.loadprogram.service;

import java.util.List;

import com.cannontech.common.dr.gear.setup.model.ProgramGear;
import com.cannontech.common.dr.program.setup.model.LoadProgram;
import com.cannontech.common.dr.program.setup.model.LoadProgramCopy;
import com.cannontech.common.dr.program.setup.model.ProgramGroup;
import com.cannontech.common.dr.setup.ProgramDetails;
import com.cannontech.common.pao.PaoType;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.LiteGear;
import com.cannontech.dr.setup.service.LMSetupService;

public interface LoadProgramSetupService extends LMSetupService<LoadProgram, LoadProgramCopy> {
  
    /**
     * Get all program Load Groups associated with paoType and groups.
     */

    List<ProgramGroup> getAllProgramLoadGroups(PaoType programType, List<LiteYukonPAObject> groups);

    /**
     * Return all the Programs which belong to Control Area.
     */

    List<ProgramDetails> getAvailablePrograms();
    
    /**
     * Return all the Load Groups associated with programType.
     */
    List<ProgramGroup> getAllProgramLoadGroups(PaoType programType);

    /**
     * Return all the Gears associated with programId.
     */
    List<LiteGear> getGearsForProgram(int programId);

    /**
     * Return ProgramGear corresponding to gearId.
     */
    ProgramGear getProgramGear(Integer gearId);
}
