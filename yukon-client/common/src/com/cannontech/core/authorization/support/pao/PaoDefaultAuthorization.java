package com.cannontech.core.authorization.support.pao;

import com.cannontech.core.authorization.support.AuthorizationResponse;
import com.cannontech.core.authorization.support.Permission;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.LiteYukonUser;

public class PaoDefaultAuthorization implements PaoAuthorization {

    public AuthorizationResponse isAuthorized(LiteYukonUser user,
            Permission permission, LiteYukonPAObject object) {
        if( permission.getNotInTableResponse() )
            return AuthorizationResponse.AUTHORIZED;
        else
            return AuthorizationResponse.UNAUTHORIZED;
    }

}
