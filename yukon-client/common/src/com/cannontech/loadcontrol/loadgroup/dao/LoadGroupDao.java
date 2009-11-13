package com.cannontech.loadcontrol.loadgroup.dao;

import java.util.List;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.loadcontrol.loadgroup.model.LoadGroup;


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
     * @param pao
     * @return
     */
    public List<PaoIdentifier> getParentMacroGroups(YukonPao pao);

}