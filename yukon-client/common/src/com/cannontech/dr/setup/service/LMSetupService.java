package com.cannontech.dr.setup.service;

import com.cannontech.database.data.lite.LiteYukonUser;

public interface LMSetupService<T1, T2> {

    /**
     * Creates the LM object.
     */
    T1 create(T1 lmObject, LiteYukonUser liteYukonUser);
    
    /**
     * Update the LM object.
     */
    T1 update(int id, T1 lmObject, LiteYukonUser liteYukonUser);

    /**
     * Retrieve LM objects based on id.
     */
    T1 retrieve(int id, LiteYukonUser liteYukonUser);
    
    /**
     * Delete the LM object.
     */
    int delete(int id, LiteYukonUser liteYukonUser);

    /**
     * Copy the LM object.
     */
    T1 copy(int id, T2 lmCopy, LiteYukonUser liteYukonUser);
       
   
    
}
