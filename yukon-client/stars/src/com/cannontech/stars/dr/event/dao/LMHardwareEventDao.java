package com.cannontech.stars.dr.event.dao;

import java.util.List;

import org.springframework.dao.DataRetrievalFailureException;

import com.cannontech.database.data.lite.stars.LiteLMHardwareEvent;

public interface LMHardwareEventDao {

    LiteLMHardwareEvent getById(int id) throws DataRetrievalFailureException;
    
    List<LiteLMHardwareEvent> getByInventoryId(int inventoryId);
    
}
