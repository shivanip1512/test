package com.cannontech.stars.dr.event.dao;

import java.util.List;

public interface EventAccountDao {
    
    public List<Integer> getAllEventsForAccount(Integer accountId);

    public void deleteAllEventsForAccount(Integer accountId);

}
