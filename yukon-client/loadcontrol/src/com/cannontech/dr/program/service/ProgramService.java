package com.cannontech.dr.program.service;

import com.cannontech.common.pao.DisplayablePao;
import com.cannontech.common.util.DatedObject;
import com.cannontech.common.util.ObjectMapper;
import com.cannontech.loadcontrol.data.LMProgramBase;

public interface ProgramService extends ObjectMapper<DisplayablePao, LMProgramBase> {
    public ObjectMapper<DisplayablePao, LMProgramBase> getMapper();

    public DatedObject<LMProgramBase> findDatedProgram(int programId);
    public DisplayablePao getProgram(int programId);
}
