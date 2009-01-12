package com.cannontech.cc.dao;

import java.util.List;

import com.cannontech.cc.model.Program;
import com.cannontech.cc.model.ProgramType;
import com.cannontech.core.dao.support.StandardDaoOperations;

public interface ProgramDao extends StandardDaoOperations<Program> {
    public List<Program> getProgramsForEnergyCompany(Integer energyCompanyId);
    public Program getForId(Integer id);
    public List<Program> getProgramsForType(ProgramType programType);
    public Integer incrementAndReturnIdentifier(Program program);
}
