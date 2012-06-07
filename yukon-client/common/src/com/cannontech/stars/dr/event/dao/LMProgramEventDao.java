package com.cannontech.stars.dr.event.dao;

import java.util.List;

public interface LMProgramEventDao {

    /**
     * Method to delete program events for an account
     * @param accountId
     */
    public void deleteProgramEventsForAccount(int accountId);

    /**
     * Method to get all program event ids for an account
     * @param accountId
     * @return
     */
    public List<Integer> getProgramEventIdsForAccount(int accountId);
}
