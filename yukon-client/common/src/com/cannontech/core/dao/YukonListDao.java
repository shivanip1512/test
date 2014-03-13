package com.cannontech.core.dao;

import java.util.List;

import com.cannontech.common.constants.YukonListEntry;
import com.cannontech.common.constants.YukonSelectionList;
import com.cannontech.stars.energyCompany.model.YukonEnergyCompany;

/**
 * This class controls the database access to the YukonSelectionList and YukonListEntry tables.
 * It also holds the cached maps for YukonSelectionList and YukonListEntry that help boost performance
 * to this frequently accessed table.
 */
public interface YukonListDao {

    public boolean areSameInYukon(int entryID1, int entryID2);

    public YukonListEntry getYukonListEntry(YukonSelectionList list, String entryText);
    
    public YukonListEntry getYukonListEntry(int listEntryID);

    public YukonSelectionList getYukonSelectionList(int listID);
    
    /**
     * This method returns all the YukonSelectionList objects for a given energy company.
     */
    public List<YukonSelectionList> getSelectionListsByEnergyCompanyId(int energyCompanyId);

    /**
     * This method returns the YukonSelectionList associated with the energyCompanyId and listName.
     * If there is not a YukonSelectionList associated with these parameters the method will return null.
     */
    public YukonSelectionList findSelectionListByEnergyCompanyIdAndListName(int energyCompanyId, String listName);

    /**
     * Returns the list of YukonListEntry for the given yukon definition id and energy company.
     */
    public List<YukonListEntry> getYukonListEntry(int yukonDefinitionId, YukonEnergyCompany ec);
}