package com.cannontech.cc.dao;

import java.util.List;

import com.cannontech.cc.model.AvailableProgramGroup;
import com.cannontech.cc.model.Program;
import com.cannontech.database.cache.functions.StandardDaoOperations;

public interface AvailableProgramGroupDao  extends StandardDaoOperations<AvailableProgramGroup> {
    public List<AvailableProgramGroup> getAllForProgram(Program program);

}
