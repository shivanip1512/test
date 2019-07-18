package com.cannontech.common.dr.setup;

import com.cannontech.database.data.device.lm.LMGroup;

public interface LoadGroupSetupBase<T extends LMGroup> {
    
    /**
     * Build model object from the passed db persistent load group.
     */
    void buildModel(T loadGroup);
    
    /**
     * Builds db persistent object for a load group.
     */
    void buildDBPersistent(T loadGroup);

}
