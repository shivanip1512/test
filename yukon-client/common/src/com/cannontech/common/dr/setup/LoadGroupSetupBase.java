package com.cannontech.common.dr.setup;

import com.cannontech.database.data.device.lm.LMGroup;

public interface LoadGroupSetupBase {
    
    /**
     * Build model object from the passed db persistent load group.
     */
    void buildModel(LMGroup loadGroup);
    
    /**
     * Builds db persistent object for a load group.
     */
    void buildDBPersistent(LMGroup group);

}
