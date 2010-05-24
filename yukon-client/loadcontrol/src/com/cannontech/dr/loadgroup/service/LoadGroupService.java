package com.cannontech.dr.loadgroup.service;

import java.util.Comparator;
import java.util.List;

import com.cannontech.common.bulk.filter.UiFilter;
import com.cannontech.common.pao.DisplayablePao;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.search.SearchResult;
import com.cannontech.common.util.DatedObject;
import com.cannontech.dr.model.ControllablePao;
import com.cannontech.loadcontrol.data.LMDirectGroupBase;
import com.cannontech.loadcontrol.data.LMGroupBase;
import com.cannontech.user.YukonUserContext;

public interface LoadGroupService {

    public ControllablePao getLoadGroup(int loadGroupId);
    public LMDirectGroupBase getGroupForPao(YukonPao from);
    
    public DatedObject<? extends LMGroupBase> findDatedGroup(int loadGroupId);

    public List<ControllablePao> findLoadGroupsForMacroLoadGroup(
            int loadGroupId, YukonUserContext userContext);

    public SearchResult<ControllablePao> filterGroups(
            UiFilter<ControllablePao> filter, Comparator<DisplayablePao> sorter,
            int startIndex, int count, YukonUserContext userContext);

    public void sendShed(int loadGroupId, int durationInSeconds);
    public void sendRestore(int loadGroupId);
    public void setEnabled(int loadGroupId, boolean isEnabled);
    
    public boolean isEnabled(int loadGroupId);
}
