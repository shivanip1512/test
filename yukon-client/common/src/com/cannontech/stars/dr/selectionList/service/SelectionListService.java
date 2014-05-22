package com.cannontech.stars.dr.selectionList.service;

import java.util.List;

import com.cannontech.common.constants.DisplayableSelectionList;
import com.cannontech.common.constants.SelectionListCategory;
import com.cannontech.common.constants.YukonDefinition;
import com.cannontech.common.constants.YukonListEntry;
import com.cannontech.common.constants.YukonSelectionList;
import com.cannontech.common.constants.YukonSelectionListEnum;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.stars.energyCompany.model.YukonEnergyCompany;
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
    SortedSetMultimap<SelectionListCategory, DisplayableSelectionList> getUserEditableLists(int ecId,
        LiteYukonUser user);

    /**
     * Returns true if the given list is inherited from a parent of the given energy company
     * rather than being directly associated with it.
     */
    boolean isListInherited(int ecId, YukonSelectionList list);

    /**
     * Update the entries in the list to match the default list.  This does not modify anything
     * except the list entries.
     */
    void restoreToDefault(YukonSelectionList list);

    /**
     * Get a list of valid definitions to use with the given energy company and list type.
     */
    List<YukonDefinition> getValidDefinitions(int ecId, YukonSelectionListEnum listType);

    /**
     * Returns a selection list.  The user has the option to use inherited or default lists 
     * if the current energy company doesn't have one
     * @param energyCompany The energy company that contains the list.
     * @param listName The name of the list being requested.
     * @param useInherited Should inherited selection lists be returned?
     * @param useDefault Should default selection lists be returned?
     */
    YukonSelectionList getSelectionList(YukonEnergyCompany energyCompany, String listName, boolean useInherited,
        boolean useDefault);

    /**
     * Returns a selection list.  The user has the option to use inherited or default lists 
     * if the current energy company doesn't have one
     * @param energyCompanyId The id of the energy company that contains the list.
     * @param listName The name of the list being requested.
     * @param useInherited Should inherited selection lists be returned?
     * @param useDefault Should default selection lists be returned?
     */
    YukonSelectionList getSelectionList(int energyCompanyId, String listName, boolean useInherited,
            boolean useDefault);

    /**
     * Returns inherited or default lists if the current energy company doesn't have one
     */
    YukonSelectionList getSelectionList(YukonEnergyCompany energyCompany, String listName);

    /**
     * Returns inherited or default lists if the current energy company doesn't have one
     */
    YukonSelectionList getSelectionList(int energyCompanyId, String listName);

    YukonListEntry getListEntry(YukonEnergyCompany energyCompany, int yukonDefId);
}
