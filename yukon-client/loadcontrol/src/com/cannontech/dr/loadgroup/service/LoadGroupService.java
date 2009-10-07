package com.cannontech.dr.loadgroup.service;

import java.util.Comparator;

import com.cannontech.common.bulk.filter.UiFilter;
import com.cannontech.common.pao.DisplayablePao;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.search.SearchResult;
import com.cannontech.common.util.DatedObject;
import com.cannontech.loadcontrol.data.LMDirectGroupBase;
import com.cannontech.loadcontrol.data.LMGroupBase;
import com.cannontech.user.YukonUserContext;

public interface LoadGroupService {

    public DisplayablePao getLoadGroup(int loadGroupId);
    public LMDirectGroupBase getGroupForPao(YukonPao from);
    
    public DatedObject<? extends LMGroupBase> findDatedGroup(int loadGroupId);

    public SearchResult<DisplayablePao> filterGroups(
            YukonUserContext userContext, UiFilter<DisplayablePao> filter,
            Comparator<DisplayablePao> sorter, int startIndex, int count);

    public void sendShed(int loadGroupId, int durationInSeconds);
    public void sendRestore(int loadGroupId);
    public void setEnabled(int loadGroupId, boolean isEnabled);
    
    public boolean isEnabled(int loadGroupId);
}
