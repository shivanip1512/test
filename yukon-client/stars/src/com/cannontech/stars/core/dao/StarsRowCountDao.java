package com.cannontech.stars.core.dao;

import org.springframework.dao.DataAccessException;

public interface StarsRowCountDao {

    public int getCustAccountsRowCount(int energyCompanyId) throws DataAccessException;
    
    public int getWorkOrdersRowCount(int energyCompanyId) throws DataAccessException;
    
    public int getInventoryRowCount(int energyCompanyId) throws DataAccessException;
    
}
