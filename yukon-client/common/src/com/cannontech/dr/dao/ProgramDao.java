package com.cannontech.dr.dao;

import java.util.List;

import com.cannontech.common.pao.DisplayablePao;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.definition.model.PaoTag;

public interface ProgramDao {
    public DisplayablePao getProgram(int programId);
    public List<PaoIdentifier> getAllProgramPaoIdentifiers();
}
