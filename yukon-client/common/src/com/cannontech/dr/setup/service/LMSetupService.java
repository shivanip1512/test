package com.cannontech.dr.setup.service;

public interface LMSetupService<T1, T2> {

    /**
     * Creates the LM object.
     */
    int create(T1 lmObject);
    
    /**
     * Update the LM object.
     */
    int update(int id, T1 lmObject);

    /**
     * Retrieve LM objects based on id.
     */
    T1 retrieve(int id);

    /**
     * Delete the LM object.
     */
    int delete(int id, String name);

    /**
     * Copy the LM object.
     */
    int copy(int id, T2 lmCopy);
}
