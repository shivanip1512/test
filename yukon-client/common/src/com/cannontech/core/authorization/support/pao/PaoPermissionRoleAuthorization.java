package com.cannontech.core.authorization.support.pao;

import com.cannontech.common.pao.YukonPao;
import com.cannontech.core.authorization.support.PermissionRoleAuthorizationBase;

/**
 * Place holder class which extends the generic PermissionRoleAuthorizationBase
 * class with a type. This class makes it easier to instantiate a
 * PaoAuthorization for LiteYukonPAObject using spring.
 */
public class PaoPermissionRoleAuthorization extends
        PermissionRoleAuthorizationBase<YukonPao> implements PaoAuthorization {
}
