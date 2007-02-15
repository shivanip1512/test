package com.cannontech.core.authorization.service.impl;

import com.cannontech.core.authorization.service.LMCommandAuthorizationService;
import com.cannontech.core.authorization.support.AuthorizationService;
import com.cannontech.core.authorization.support.CommandPermissionConverter;
import com.cannontech.core.authorization.support.Permission;
import com.cannontech.database.data.lite.LiteYukonUser;

/**
 * LMCommandAuthorizationService implementation
 */
public class LMCommandAuthorizationServiceImpl implements LMCommandAuthorizationService {

    private AuthorizationService<String> authorizationService = null;
    private CommandPermissionConverter converter = null;

    public void setConverter(CommandPermissionConverter converter) {
        this.converter = converter;
    }

    public void setAuthorizationService(AuthorizationService<String> authorizationService) {
        this.authorizationService = authorizationService;
    }

    public boolean isAuthorized(LiteYukonUser user, String command, String lmString) {
        Permission permission = converter.getPermission(command);
        boolean authorized = authorizationService.isAuthorized(user, permission, lmString);
        return authorized;
    }

}
