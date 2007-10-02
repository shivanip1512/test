package com.cannontech.web.input;

import com.cannontech.database.data.lite.LiteYukonUser;

/**
 * Interface for determining security settings for an input
 */
public interface InputSecurity {

    /**
     * Method used to determine if the given user has permission to edit
     * @param user - User for permission
     * @return True if user can edit
     */
    public boolean isEditable(LiteYukonUser user);

}
