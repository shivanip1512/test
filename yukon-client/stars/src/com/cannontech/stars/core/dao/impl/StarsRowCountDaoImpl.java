package com.cannontech.stars.core.dao.impl;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.stars.core.dao.StarsRowCountDao;

public class StarsRowCountDaoImpl implements StarsRowCountDao {
    private static final String selectInventorySql;
    private static final String selectWorkOrdersSql;
    private static final String selectCustAccountsSql;
    private SimpleJdbcTemplate simpleJdbcTemplate;
    
    static {
        
        selectInventorySql = "SELECT COUNT(*) " +
                             "FROM InventoryBase,ECToInventoryMapping " +
                             "WHERE EnergyCompanyID = ? " +
                             "AND InventoryBase.InventoryID = ECToInventoryMapping.InventoryID";
        
        selectWorkOrdersSql = "SELECT COUNT(*) " +
                              "FROM WorkOrderBase,ECToWorkOrderMapping " +
                              "WHERE EnergyCompanyID = ? " +
                              "AND WorkOrderBase.OrderID = ECToWorkOrderMapping.WorkOrderID";
        
        selectCustAccountsSql = "SELECT COUNT(*) " +
                                "FROM CustomerAccount,ECToAccountMapping " +
                                "WHERE EnergyCompanyID = ? " +
                                "AND CustomerAccount.AccountID = ECToAccountMapping.AccountID";
        
    }
    
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public int getCustAccountsRowCount(final int energyCompanyId) throws DataAccessException {
        int result = simpleJdbcTemplate.queryForInt(selectCustAccountsSql, energyCompanyId);
        return result;
    }

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public int getInventoryRowCount(final int energyCompanyId) throws DataAccessException {
        int result = simpleJdbcTemplate.queryForInt(selectInventorySql, energyCompanyId);
        return result;
    }

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public int getWorkOrdersRowCount(final int energyCompanyId) throws DataAccessException {
        int result = simpleJdbcTemplate.queryForInt(selectWorkOrdersSql, energyCompanyId);
        return result;
    }

    public void setSimpleJdbcTemplate(final SimpleJdbcTemplate simpleJdbcTemplate) {
        this.simpleJdbcTemplate = simpleJdbcTemplate;
    }
    
}
