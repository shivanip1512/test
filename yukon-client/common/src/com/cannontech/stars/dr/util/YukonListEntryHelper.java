package com.cannontech.stars.dr.util;

import java.util.List;

import com.cannontech.common.constants.YukonListEntry;
import com.cannontech.common.constants.YukonSelectionList;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.stars.database.data.lite.LiteStarsEnergyCompany;
import com.cannontech.stars.dr.hardware.model.ListEntryEnum;
import com.cannontech.stars.dr.selectionList.service.SelectionListService;
import com.cannontech.stars.energyCompany.model.YukonEnergyCompany;

/**
 * Helper class for dealing with the YukonListEntry table
 */
public class YukonListEntryHelper {

    /**
     * Helper method to get a list entry id for a given energy company based on
     * a list name and definition id
     * @param energyCompany - Current energy company
     * @param listName - Name of list the entry is in
     * @param definitionId - Yukon definition id of the entry
     * @return The entry id
     */
    public static int getListEntryId(LiteStarsEnergyCompany energyCompany,
            String listName, int definitionId) {

        YukonSelectionList selectionList = 
                YukonSpringHook.getBean(SelectionListService.class).getSelectionList(energyCompany, listName);

        List<YukonListEntry> entries = selectionList.getYukonListEntries();
        for (YukonListEntry entry : entries) {
            int yukonDefId = entry.getYukonDefID();
            if (definitionId == yukonDefId) {
                int entryId = entry.getEntryID();
                return entryId;
            }
        }

        throw new NotFoundException("Could not find entry id for list: " + listName + " YukonDefinitionId: " + definitionId);
    }

    /**
     * Helper method to get a yukon definition id for a given energy company
     * based on a list name and entry id
     * @param energyCompany - Current energy company
     * @param listName - Name of list the entry is in
     * @param listEntryId - Entry id of the entry
     * @return The yukon definition id
     */
    public static int getYukonDefinitionId(YukonEnergyCompany energyCompany, String listName, int listEntryId) {
        YukonSelectionList selectionList = 
                YukonSpringHook.getBean(SelectionListService.class).getSelectionList(energyCompany, listName);

        List<YukonListEntry> entries = selectionList.getYukonListEntries();
        for (YukonListEntry entry : entries) {
            int entryId = entry.getEntryID();
            if (listEntryId == entryId) {
                int definitionId = entry.getYukonDefID();
                return definitionId;
            }
        }

        throw new NotFoundException("Could not find yukon definition id for list: " + listName + " EntryId: " + listEntryId);
    }

    /**
     * Helper method to get a yukon entry id for a given energy company based on
     * a list name and entry text
     * @param entryText - Entry text of the entry
     * @param listName - Name of list the entry is in
     * @param lsec - Current energy company
     * @return The yukon entry id
     */
    public static YukonListEntry getEntryForEntryText(String entryText, String listName, YukonEnergyCompany yec) {

        YukonListEntry entry = null;
        YukonSelectionList selectionList = 
                YukonSpringHook.getBean(SelectionListService.class).getSelectionList(yec, listName);

        for (YukonListEntry item : selectionList.getYukonListEntries()) {
            if (item.getEntryText().equalsIgnoreCase(entryText)) {
                entry = item;
                break;
            }
        }
        if (entry == null) {
            throw new NotFoundException("Could not find yukon entry for list: " + listName + " EntryText: " + entryText);
        }
        return entry;
    }
    
    /**
     * Helper method to get a yukon definition id for a given energy company
     * based on a list entry enum
     * @param energyCompany - Current energy company
     * @param listEnum - Enum value to get entry id for
     * @return The entry id
     */
    public static int getListEntryId(LiteStarsEnergyCompany energyCompany,
            ListEntryEnum listEnum) {

        String listName = listEnum.getListName();
        int definitionId = listEnum.getDefinitionId();

        return YukonListEntryHelper.getListEntryId(energyCompany,
                                                   listName,
                                                   definitionId);
    }

}
