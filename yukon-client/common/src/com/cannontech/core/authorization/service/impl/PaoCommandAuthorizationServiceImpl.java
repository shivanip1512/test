package com.cannontech.core.authorization.service.impl;

import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.core.authorization.service.PaoAuthorizationService;
import com.cannontech.core.authorization.service.PaoCommandAuthorizationService;
import com.cannontech.core.authorization.support.CommandPermissionConverter;
import com.cannontech.core.authorization.support.Permission;
import com.cannontech.database.data.lite.LiteYukonUser;

/**
 * PaoCommandAuthorizationService implementation
 */
public class PaoCommandAuthorizationServiceImpl implements PaoCommandAuthorizationService {

    private PaoAuthorizationService authorizationService = null;
    private CommandPermissionConverter converter = null;

    public void setConverter(CommandPermissionConverter converter) {
        this.converter = converter;
    }

    public void setAuthorizationService(PaoAuthorizationService authorizationService) {
        this.authorizationService = authorizationService;
    }

    @Override
    public boolean isAuthorized(LiteYukonUser user, String command, YukonPao device) {
    	Permission permission = converter.getPermission(command);
        return authorizationService.isAuthorized(user, permission, device);
    }

    @Override
    @Deprecated
    public boolean isAuthorized(LiteYukonUser user, String command) {
        Permission permission = converter.getPermission(command);
        boolean authorized = authorizationService.isAuthorized(user, permission, new SimpleDevice(-1, PaoType.MCT410IL));
        return authorized;
    }
}
