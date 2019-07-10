package com.cannontech.dr.macro.service;

import com.cannontech.common.dr.setup.LMCopy;
import com.cannontech.common.dr.setup.MacroLoadGroup;

public interface MacroLoadGroupSetupService {

    /**
     * Retrieve Macro load group for the MacroloadGroupId.
     */
    MacroLoadGroup retrieve(int loadGroupId);

    /**
     * Create the Macro load group.
     */
    int create(MacroLoadGroup loadGroup);

    /**
     * Update the Macro load group.
     */
    int update(int loadGroupId, MacroLoadGroup loadGroup);

    /**
     * Copy the Macro load group.
     */
    int copy(int loadGroupId, LMCopy lmCopy);

    /**
     * Delete the Macro load group.
     */
    int delete(int loadGroupId, String loadGroupName);
}
