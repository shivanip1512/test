package com.cannontech.dr.rfn.service;

import java.util.List;

import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.dr.rfn.model.PqrConfig;
import com.cannontech.stars.database.data.lite.LiteLmHardwareBase;

/**
 * TODO: javadocs
 */
public interface PqrConfigService {
    
    public String sendConfigs(List<LiteLmHardwareBase> hardware, PqrConfig config, LiteYukonUser user);
    
}
