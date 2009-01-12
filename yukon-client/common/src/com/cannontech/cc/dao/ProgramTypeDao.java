package com.cannontech.cc.dao;

import java.util.List;

import com.cannontech.cc.model.ProgramType;
import com.cannontech.core.dao.support.StandardDaoOperations;

public interface ProgramTypeDao extends StandardDaoOperations<ProgramType> {
    public List<ProgramType> getAllProgramTypes(Integer energyCompanyId);
}
