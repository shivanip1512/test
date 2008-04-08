package com.cannontech.stars.dr.program.dao;

import java.util.List;

import com.cannontech.stars.dr.appliance.model.Appliance;
import com.cannontech.stars.dr.program.model.Program;

public interface ProgramDao {

    public List<Program> getByAppliances(List<Appliance> applianceList); 
    
    public List<Program> getByProgramIds(List<Integer> programIdList);
    
    public List<Integer> getGroupIdsByProgramId(int programId);
    
}
