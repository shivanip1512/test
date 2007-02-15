package com.cannontech.core.authorization.support.pao;

import com.cannontech.core.authorization.support.PermissionRoleAuthorizationBase;
import com.cannontech.database.data.lite.LiteYukonPAObject;

/**
 * Place holder class which extends the generic PermissionRoleAuthorizationBase
 * class with a type. This class makes it easier to instantiate a
 * PaoAuthorization for LiteYukonPAObject using spring.
 */
public class PaoPermissionRoleAuthorization extends
        PermissionRoleAuthorizationBase<LiteYukonPAObject> implements PaoAuthorization {
}
