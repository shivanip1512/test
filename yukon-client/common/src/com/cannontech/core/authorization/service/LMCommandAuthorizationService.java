package com.cannontech.core.authorization.service;

import com.cannontech.database.data.lite.LiteYukonUser;

/**
 * Service for determining user command authorization for Load Management
 * objects.
 */
public interface LMCommandAuthorizationService {

    /**
     * Method to determine if a user is authorized to execute a given command
     * for a Load Management object represented by the lmString.
     * @param user - User to execute command
     * @param command - Command to execute on the LM object
     * @param lmString - String identifying an LM object
     * @return True if user is authorized to execute the command on the LM
     *         object
     */
    public boolean isAuthorized(LiteYukonUser user, String command, String lmString);

}