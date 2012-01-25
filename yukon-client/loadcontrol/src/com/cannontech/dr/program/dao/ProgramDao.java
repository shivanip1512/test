package com.cannontech.dr.program.dao;

import java.util.List;

import com.cannontech.common.pao.DisplayablePao;
import com.cannontech.common.pao.PaoIdentifier;

public interface ProgramDao {
    public DisplayablePao getProgram(int programId);
    public List<PaoIdentifier> getAllProgramPaoIdentifiers();
}
