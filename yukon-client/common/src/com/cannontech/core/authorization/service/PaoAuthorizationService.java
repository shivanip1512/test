package com.cannontech.core.authorization.service;

import com.cannontech.core.authorization.support.AuthorizationService;
import com.cannontech.database.data.lite.LiteYukonPAObject;

/**
 * Service for determining authorization for paos.
 */
public interface PaoAuthorizationService extends AuthorizationService<LiteYukonPAObject> {
}
