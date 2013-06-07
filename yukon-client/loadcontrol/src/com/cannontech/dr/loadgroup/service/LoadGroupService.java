package com.cannontech.dr.loadgroup.service;

import java.util.Comparator;
import java.util.List;

import com.cannontech.common.bulk.filter.UiFilter;
import com.cannontech.common.pao.DisplayablePao;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.search.SearchResult;
import com.cannontech.common.util.DatedObject;
import com.cannontech.messaging.message.loadcontrol.data.DirectGroupBase;
import com.cannontech.messaging.message.loadcontrol.data.GroupBase;
import com.cannontech.user.YukonUserContext;

public interface LoadGroupService {

    public DisplayablePao getLoadGroup(int loadGroupId);
    public DirectGroupBase getGroupForPao(YukonPao from);
    
    public DatedObject<? extends GroupBase> findDatedGroup(int loadGroupId);

    public List<DisplayablePao> findLoadGroupsForMacroLoadGroup(
            int loadGroupId, YukonUserContext userContext);

    public SearchResult<DisplayablePao> filterGroups(
            UiFilter<DisplayablePao> filter, Comparator<DisplayablePao> sorter,
            int startIndex, int count, YukonUserContext userContext);

    public void sendShed(int loadGroupId, int durationInSeconds);
    public void sendRestore(int loadGroupId);
    public void setEnabled(int loadGroupId, boolean isEnabled);
    
    public boolean isEnabled(int loadGroupId);
}
