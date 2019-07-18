package com.cannontech.common.dr.setup;

public interface LoadGroupSetupBase<T> {
    
    /**
     * Build model object from the passed db persistent load group.
     */
    void buildModel(T loadGroup);
    
    /**
     * Builds db persistent object for a load group.
     */
    void buildDBPersistent(T loadGroup);

}
