package com.cannontech.core.authorization.service;

import com.cannontech.common.pao.YukonPao;
import com.cannontech.database.data.lite.LiteYukonUser;

/**
 * Service for determining command authorization for paos.
 */
public interface PaoCommandAuthorizationService {

    /**
     * Method to determine if a user is authorized to execute a given command
     * for a yukonDevice.
     * @param user - User to execute command
     * @param command - Command to execute on the YukonDevice
     * @param device - YukonDevice for command
     * @return True if the user is authorized to execute the command on the YukonDevice
     */
    public boolean isAuthorized(LiteYukonUser user, String command, YukonPao pao);

    public boolean isAuthorized(LiteYukonUser user, String command);
}