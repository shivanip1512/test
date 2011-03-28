package com.cannontech.stars.dr.selectionList.service;

import com.cannontech.common.constants.DisplayableSelectionList;
import com.cannontech.common.constants.SelectionListCategory;
import com.cannontech.common.constants.YukonSelectionList;
import com.cannontech.user.YukonUserContext;
import com.google.common.collect.SortedSetMultimap;

/**
 * This service is meant to hide the fact that these lists are cached and dealt with by
 * StarsDatabaseCache.
 */
public interface SelectionListService {
    /**
     * Get a list of user editable lists for the given energy company and user context.
     * @param ecId The energy company id for the lists being edited. This does not need to be the
     *            same energy company associated with the user, as long as the user is allowed
     *            to view the energy company.
     */
    public SortedSetMultimap<SelectionListCategory, DisplayableSelectionList>
        getUserEditableLists(int ecId, YukonUserContext context);

    /**
     * Returns true if the given list is inherited from a parent of energyCompany rather than
     * being directly associated with it.
     */
    public boolean isListInherited(int ecId, YukonSelectionList list);
}
