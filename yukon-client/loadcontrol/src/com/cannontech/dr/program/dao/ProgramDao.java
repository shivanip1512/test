package com.cannontech.dr.program.dao;

import com.cannontech.dr.model.ControllablePao;

public interface ProgramDao {
    public ControllablePao getProgram(int programId);
}
