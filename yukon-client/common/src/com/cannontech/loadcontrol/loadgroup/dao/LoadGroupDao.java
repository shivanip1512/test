package com.cannontech.loadcontrol.loadgroup.dao;

import java.util.Collection;
import java.util.List;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.loadcontrol.loadgroup.model.LoadGroup;
import com.google.common.collect.SetMultimap;


public interface LoadGroupDao {

    public LoadGroup getById(int loadGroupId);

    public List<LoadGroup> getByIds(Iterable<Integer> loadGroupIds);

    public LoadGroup getByLoadGroupName(String loadGroupName);
    
    /**
     * This method returns false when the supplied load group
     * is apart of an active enrollment.  
     * 
     * @param loadGroupId
     * @return
     */
    public boolean isLoadGroupInUse(int loadGroupId);

    /**
     * Returns a list of YukonPao's which are the macro groups
     * the provide load group is in.
     * 
     * @param pao
     * @return
     */
    public List<PaoIdentifier> getParentMacroGroups(PaoIdentifier pao);

    /**
     * This method returns a list of loadGroups that are attached to 
     * a stars program.
     * 
     */
    public List<LoadGroup> getByStarsProgramId(int programId);
    
    /**
     * Method to get a mapping of macro group to groups
     * @param groups - Collection of groups to get macro group mappings for
     * @return Macro group to groups mapping
     */
    public SetMultimap<PaoIdentifier, PaoIdentifier> getMacroGroupToGroupMappings(Collection<PaoIdentifier> groups);

    /**
     * Method to return a list of LoadGroups for a LoadManagement programId (NOT A STARS program).
     * @param programId
     * @return
     */
    public List<LoadGroup> getByProgramId(int programId);
    
    /**
     * @return the ID of the program that contains the specified group.
     */
    public int getProgramIdByGroupId(int lmGroupId);
}