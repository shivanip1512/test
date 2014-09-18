package com.cannontech.dr.loadgroup.service;

import java.util.Comparator;
import java.util.List;

import com.cannontech.common.bulk.filter.UiFilter;
import com.cannontech.common.pao.DisplayablePao;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.search.result.SearchResults;
import com.cannontech.common.util.DatedObject;
import com.cannontech.loadcontrol.data.LMDirectGroupBase;
import com.cannontech.loadcontrol.data.LMGroupBase;
import com.cannontech.user.YukonUserContext;

public interface LoadGroupService {

    public DisplayablePao getLoadGroup(int loadGroupId);
    public LMDirectGroupBase getGroupForPao(YukonPao from);

    public DatedObject<? extends LMGroupBase> findDatedGroup(int loadGroupId);

    public List<DisplayablePao> findLoadGroupsForMacroLoadGroup(
            int loadGroupId, YukonUserContext userContext);

    public SearchResults<DisplayablePao> filterGroups(
            UiFilter<DisplayablePao> filter, Comparator<DisplayablePao> sorter,
            int startIndex, int count, YukonUserContext userContext);

    public void sendShed(int loadGroupId, int durationInSeconds);
    public void sendRestore(int loadGroupId);
    public void setEnabled(int loadGroupId, boolean isEnabled);
}
