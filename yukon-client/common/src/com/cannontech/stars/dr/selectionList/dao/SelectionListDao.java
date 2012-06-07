package com.cannontech.stars.dr.selectionList.dao;

import java.util.List;

import com.cannontech.common.constants.YukonSelectionList;

public interface SelectionListDao {
    public void saveList(YukonSelectionList list, List<Integer> entryIdsToDelete);
}
