package com.cannontech.cc.dao;

import java.util.List;

import com.cannontech.cc.model.Program;
import com.cannontech.cc.model.ProgramParameter;

public interface ProgramParameterDao extends StandardDaoOperations<com.cannontech.cc.model.ProgramParameter> {

    ProgramParameter getFor(String parameterKey, Program program) throws UnknownParameterException;
    public List<ProgramParameter> getAllForProgram(Program program);
}
