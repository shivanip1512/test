package com.cannontech.common.constants;

/**
 * @author rneuharth
 *
 * Common functions for ListEntry values
 */
public final class YukonListEntryFuncs
{

    /**
     * Checks for a valid phone number entry
     */
    public static boolean isPhoneNumber( int listEntryID )
    {
        return(
             listEntryID == YukonListEntryTypes.YUK_ENTRY_ID_PHONE
             || listEntryID == YukonListEntryTypes.YUK_ENTRY_ID_HOME_PHONE
             || listEntryID == YukonListEntryTypes.YUK_ENTRY_ID_WORK_PHONE );
    }

}
