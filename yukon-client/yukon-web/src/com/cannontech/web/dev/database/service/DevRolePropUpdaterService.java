package com.cannontech.web.dev.database.service;

import java.util.Map;

import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.web.dev.database.objects.DevRoleProperties;

public interface DevRolePropUpdaterService {
    boolean isRunning();
    Map<YukonRole, Boolean> executeSetup(DevRoleProperties devRoleProperties);

}
