package com.cannontech.core.dao;

import java.util.Map;

import com.cannontech.common.constants.YukonListEntry;
import com.cannontech.common.constants.YukonSelectionList;

public interface YukonListDao {

    public boolean isListEntryValid(int entryID_, String entry_);

    public boolean areSameInYukon(int entryID1, int entryID2);

    /**
     * Checks for a valid phone number entry
     */
    public boolean isPhoneNumber(int listEntryID);

    /**
     * Checks for an email entry
     */
    public boolean isEmail(int listEntryID);

    /**
     * Checks for an short email (SMS) entry
     */
    public boolean isShortEmail(int listEntryID);

    /**
     * Checks for a PIN entry
     */
    public boolean isPIN(int listEntryID);

    /**
     * Checks for a fax entry
     */
    public boolean isFax(int listEntryID);

    /**
     * Checks for a pager entry
     */
    public boolean isPager(int listEntryID);

    /**
     * Get the selection list name based on the specified list entry yukon definition ID 
     */
    public String getYukonListName(int yukonDefID);
    
    public void releaseAllConstants();

    
    public YukonListEntry getYukonListEntry(YukonSelectionList list,
            String entryText);
    
    public YukonListEntry getYukonListEntry(int listEntryID);

    public YukonSelectionList getYukonSelectionList(int listID);
    
    public Map<Integer,YukonSelectionList> getYukonSelectionLists();
    
    public Map<Integer,YukonListEntry> getYukonListEntries();
}