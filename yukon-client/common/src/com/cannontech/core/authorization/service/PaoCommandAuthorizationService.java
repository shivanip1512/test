package com.cannontech.core.authorization.service;

import com.cannontech.common.device.YukonDevice;
import com.cannontech.core.authorization.exception.PaoAuthorizationException;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.LiteYukonUser;

/**
 * Service for determining command authorization for paos.
 */
public interface PaoCommandAuthorizationService {

    /**
     * Method to determine if a user is authorized to execute a given command
     * for a pao.
     * @param user - User to execute command
     * @param command - Command to execute on the pao
     * @param pao - Pao for command
     * @return True if the user is authorized to execute the command on the pao
     */
    public boolean isAuthorized(LiteYukonUser user, String command, LiteYukonPAObject pao);

    public void verifyAuthorized(LiteYukonUser user, String command, LiteYukonPAObject pao) throws PaoAuthorizationException;

    /**
     * Method to determine if a user is authorized to execute a given command
     * for a yukonDevice.
     * @param user - User to execute command
     * @param command - Command to execute on the YukonDevice
     * @param device - YukonDevice for command
     * @return True if the user is authorized to execute the command on the YukonDevice
     */
    public boolean isAuthorized(LiteYukonUser user, String command, YukonDevice device);
    
    public boolean isAuthorized(LiteYukonUser user, String command);

    public void verifyAuthorized(LiteYukonUser user, String command, YukonDevice device) throws PaoAuthorizationException;
}