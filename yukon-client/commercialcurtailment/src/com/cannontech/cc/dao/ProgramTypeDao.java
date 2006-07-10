package com.cannontech.cc.dao;

import java.util.List;

import com.cannontech.cc.model.ProgramType;
import com.cannontech.database.cache.functions.StandardDaoOperations;

public interface ProgramTypeDao extends StandardDaoOperations<ProgramType> {
    public List<ProgramType> getAllProgramTypes(Integer energyCompanyId);
}
