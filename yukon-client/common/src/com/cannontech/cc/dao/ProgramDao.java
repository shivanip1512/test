package com.cannontech.cc.dao;

import java.util.List;

import com.cannontech.cc.model.Program;
import com.cannontech.cc.model.ProgramType;
import com.cannontech.core.dao.support.IdentifiableObjectProvider;

public interface ProgramDao extends IdentifiableObjectProvider<Program> {
    public List<Program> getProgramsForEnergyCompany(Integer energyCompanyId);
    public Program getForId(Integer id);
    public List<Program> getProgramsForType(ProgramType programType);
    public Integer incrementAndReturnIdentifier(Program program);
    public void save(Program object);
    public void delete(Program object);
    public String findGearName(int programId, int gearNumber);
    
}
