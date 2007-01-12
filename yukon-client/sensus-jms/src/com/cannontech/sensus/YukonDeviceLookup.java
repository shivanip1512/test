package com.cannontech.sensus;

import com.cannontech.database.data.lite.LiteYukonPAObject;

public interface YukonDeviceLookup {
    /**
     * If a Yukon Device has been configured for this repId, return its PAObject.
     * Otherwise return null.
     * @param fnid the FlexNetID
     * @return a Yukon PAO 
     */
    public LiteYukonPAObject getDeviceForRepId(int repId);
    
    public boolean isDeviceConfigured(int repId);

}
