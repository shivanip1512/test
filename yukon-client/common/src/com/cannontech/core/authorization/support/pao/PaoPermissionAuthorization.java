package com.cannontech.core.authorization.support.pao;

import com.cannontech.core.authorization.service.PaoPermissionService;
import com.cannontech.core.authorization.support.AuthorizationResponse;
import com.cannontech.core.authorization.support.Permission;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.LiteYukonUser;

public class PaoPermissionAuthorization implements PaoAuthorization {

    private PaoPermissionService paoPermissionService = null;

    public void setPaoPermissionService(PaoPermissionService paoPermissionService) {
        this.paoPermissionService = paoPermissionService;
    }

    public AuthorizationResponse isAuthorized(LiteYukonUser user, Permission permission,
            LiteYukonPAObject pao) {
        return paoPermissionService.hasPermission(user, pao, permission);

    }

}
