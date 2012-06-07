package com.cannontech.stars.dr.hardware.service;

import com.cannontech.database.data.lite.LiteYukonUser;

public interface DeviceVerificationService {

    public boolean verify(LiteYukonUser user, String serialNumber);
    
}
