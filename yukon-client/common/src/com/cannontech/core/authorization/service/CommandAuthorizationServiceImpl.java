package com.cannontech.core.authorization.service;

import com.cannontech.core.authorization.support.CommandPermissionConverter;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.LiteYukonUser;

/**
 * Service class for determining command authorization for a given user and
 * device
 */
public class CommandAuthorizationServiceImpl implements CommandAuthorizationService {

    private PaoAuthorizationService deviceAuthService = null;
    private CommandPermissionConverter converter = null;

    public void setConverter(CommandPermissionConverter converter) {
        this.converter = converter;
    }

    public void setDeviceAuthService(PaoAuthorizationService deviceAuthService) {
        this.deviceAuthService = deviceAuthService;
    }

    public boolean isAuthorized(LiteYukonUser user, String command, LiteYukonPAObject device) {
        return deviceAuthService.isAuthorized(user, converter.getPermission(command), device);
    }

}
