package com.cannontech.core.authorization.support.pao;

import com.cannontech.common.pao.YukonPao;
import com.cannontech.core.authorization.service.PaoPermissionService;
import com.cannontech.core.authorization.support.AuthorizationResponse;
import com.cannontech.core.authorization.support.Permission;
import com.cannontech.database.data.lite.LiteYukonUser;

public class PaoPermissionAuthorization implements PaoAuthorization {

    private PaoPermissionService paoPermissionService = null;

    public void setPaoPermissionService(PaoPermissionService paoPermissionService) {
        this.paoPermissionService = paoPermissionService;
    }

    public AuthorizationResponse isAuthorized(LiteYukonUser user, Permission permission,
            YukonPao pao) {
        return paoPermissionService.hasPermission(user, pao, permission);

    }
    
    @Override
    public String toString() {
        return "pao permission service authorization";
    }

}
