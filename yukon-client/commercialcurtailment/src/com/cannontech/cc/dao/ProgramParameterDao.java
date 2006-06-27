package com.cannontech.cc.dao;

import java.util.List;

import com.cannontech.cc.model.Program;
import com.cannontech.cc.model.ProgramParameter;
import com.cannontech.cc.model.ProgramParameterKey;

public interface ProgramParameterDao extends StandardDaoOperations<com.cannontech.cc.model.ProgramParameter> {

    public List<ProgramParameter> getAllForProgram(Program program);
    void deleteAllForProgram(Program program);

    public ProgramParameter getFor(Program program, ProgramParameterKey parameterKey) throws UnknownParameterException;
    public String getParameterValue(Program program, ProgramParameterKey key);
    public int getParameterValueInt(Program program, ProgramParameterKey key);
    public float getParameterValueFloat(Program program, ProgramParameterKey key);

}
