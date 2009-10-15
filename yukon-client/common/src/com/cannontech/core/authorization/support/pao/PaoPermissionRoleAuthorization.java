package com.cannontech.core.authorization.support.pao;

import com.cannontech.common.pao.YukonPao;
import com.cannontech.core.authorization.support.PermissionRoleAuthorizationBase;

/**
 * PermissionRoleAuthorization class which can return Authorized, Unauthorized or Unknown
 */
public class PaoPermissionRoleAuthorization extends
        PermissionRoleAuthorizationBase<YukonPao> implements PaoAuthorization {
}
