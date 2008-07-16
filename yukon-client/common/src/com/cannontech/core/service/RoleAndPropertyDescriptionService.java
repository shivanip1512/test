package com.cannontech.core.service;

import com.cannontech.database.data.lite.LiteYukonUser;

public interface RoleAndPropertyDescriptionService {

    public boolean checkIfAtLeaseOneExists(String rolePropDescription, LiteYukonUser user);
    
}
