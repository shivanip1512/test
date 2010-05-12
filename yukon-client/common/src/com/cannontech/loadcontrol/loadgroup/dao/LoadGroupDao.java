package com.cannontech.loadcontrol.loadgroup.dao;

import java.util.Collection;
import java.util.List;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.loadcontrol.loadgroup.model.LoadGroup;
import com.google.common.collect.SetMultimap;


public interface LoadGroupDao {

    public LoadGroup getById(int loadGroupId);

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

}