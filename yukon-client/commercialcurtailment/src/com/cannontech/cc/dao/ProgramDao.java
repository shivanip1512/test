package com.cannontech.cc.dao;

import java.util.List;

import com.cannontech.cc.model.Program;
import com.cannontech.cc.model.ProgramType;
import com.cannontech.database.cache.functions.StandardDaoOperations;

public interface ProgramDao extends StandardDaoOperations<Program> {
    public List<Program> getProgramsForEnergyCompany(Integer energyCompanyId);
    public Program getForId(Integer id);
    public List<Program> getProgramsForType(ProgramType programType);

}
