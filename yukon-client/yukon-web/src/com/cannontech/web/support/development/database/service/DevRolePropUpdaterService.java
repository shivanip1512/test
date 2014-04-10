package com.cannontech.web.support.development.database.service;

import java.util.Map;

import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.web.support.development.database.objects.DevRoleProperties;

public interface DevRolePropUpdaterService {
    boolean isRunning();
    Map<YukonRole, Boolean> executeSetup(DevRoleProperties devRoleProperties);

}
