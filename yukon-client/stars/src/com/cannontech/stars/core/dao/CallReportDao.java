package com.cannontech.stars.core.dao;

import java.util.List;

public interface CallReportDao {
    
    /**
     * Method to delete callreports for an account
     * @param accountId
     */
    public void deleteByAccount(int accountId);

    /**
     * Method to get call reports for an account
     * @param accountId
     * @return
     */
    public List<Integer> getByAccount(int accountId);

}
