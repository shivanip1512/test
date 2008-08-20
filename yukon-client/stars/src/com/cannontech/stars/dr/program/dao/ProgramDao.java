package com.cannontech.stars.dr.program.dao;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.cannontech.stars.dr.appliance.model.Appliance;
import com.cannontech.stars.dr.appliance.model.ApplianceCategory;
import com.cannontech.stars.dr.program.model.Program;

public interface ProgramDao {

    public Program getByProgramId(int programId);
    
    public Map<ApplianceCategory, List<Program>> getByApplianceCategories(List<ApplianceCategory> applianceCategories);
    
    public List<Program> getByAppliances(List<Appliance> applianceList); 
    
    public List<Program> getByProgramIds(List<Integer> programIdList);
    
    public List<Integer> getDistinctGroupIdsByYukonProgramIds(final Set<Integer> programIds);
    
    public List<Integer> getGroupIdsByProgramId(int programId);
    
}
