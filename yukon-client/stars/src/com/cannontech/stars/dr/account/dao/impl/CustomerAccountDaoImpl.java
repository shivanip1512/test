package com.cannontech.stars.dr.account.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.database.incrementer.NextValueHelper;
import com.cannontech.stars.dr.account.dao.CustomerAccountDao;
import com.cannontech.stars.dr.account.model.CustomerAccount;

public class CustomerAccountDaoImpl implements CustomerAccountDao {
    private static final String insertSql;
    private static final String removeSql;
    private static final String updateSql;
    private static final String selectSql;
    private static final String selectByIdSql;
    private static final String selectByAccountNumberSql;
    private static final ParameterizedRowMapper<CustomerAccount> rowMapper;
    
    private SimpleJdbcTemplate simpleJdbcTemplate;
    private NextValueHelper nextValueHelper;
    
    static {
        
        insertSql = "INSERT INTO CustomerAccount (AccountID,AccountSiteID,AccountNumber,CustomerID,BillingAddressID,AccountNotes) VALUES (?,?,?,?,?,?)";
        
        removeSql = "DELETE FROM CustomerAccount WHERE AccountID = ?";
        
        updateSql = "UPDATE CustomerAccount SET AccountSiteID = ?, AccountNumber = ?, CustomerID = ?, BillingAddressID = ?, AccountNotes = ? WHERE AccountID = ?";
        
        selectSql = "SELECT AccountID,AccountSiteID,AccountNumber,CustomerID,BillingAddressID,AccountNotes FROM CustomerAccount";
        
        selectByIdSql = selectSql + " WHERE AccountID = ?";
        
        selectByAccountNumberSql = selectSql + " WHERE AccountNumber = ?";
        
        rowMapper = CustomerAccountDaoImpl.createRowMapper();
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public boolean add(final CustomerAccount account) {
        final int nextId = nextValueHelper.getNextValue("CustomerAccount");
        account.setAccountId(nextId);

        int rowsAffected = simpleJdbcTemplate.update(insertSql, 
                                                     account.getAccountId(),
                                                     account.getAccountSiteId(),
                                                     account.getAccountNumber(),
                                                     account.getCustomerId(),
                                                     account.getBillingAddressId(),
                                                     account.getAccountNotes());
        boolean result = (rowsAffected == 1);
        return result;
    }
    
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public boolean remove(final CustomerAccount account) {
        int rowsAffected = simpleJdbcTemplate.update(removeSql, account.getAccountId());
        boolean result = (rowsAffected == 1);
        return result;
    }
    
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public boolean update(final CustomerAccount account) {
        int rowsAffected = simpleJdbcTemplate.update(updateSql, 
                                                     account.getAccountSiteId(),
                                                     account.getAccountNumber(),
                                                     account.getCustomerId(),
                                                     account.getBillingAddressId(),
                                                     account.getAccountNotes(),
                                                     account.getAccountId());
        boolean result = (rowsAffected == 1);
        return result;
    }
    
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public CustomerAccount getById(final int accountId) throws DataAccessException {
        CustomerAccount account = simpleJdbcTemplate.queryForObject(selectByIdSql, rowMapper, accountId);
        return account;
    }
    
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public CustomerAccount getByAccountNumber(final String accountNumber) throws DataAccessException {
        CustomerAccount account = simpleJdbcTemplate.queryForObject(selectByAccountNumberSql, rowMapper, accountNumber);
        return account;
    }
    
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<CustomerAccount> getAll() {
        try {
            List<CustomerAccount> list = simpleJdbcTemplate.query(selectSql, rowMapper, new Object[]{});
            return list;
        } catch (DataAccessException e) {
            return Collections.emptyList();
        }
    }

    public void setSimpleJdbcTemplate(final SimpleJdbcTemplate simpleJdbcTemplate) {
        this.simpleJdbcTemplate = simpleJdbcTemplate;
    }
    
    public void setNextValueHelper(final NextValueHelper nextValueHelper) {
        this.nextValueHelper = nextValueHelper;
    }
    
    private static final ParameterizedRowMapper<CustomerAccount> createRowMapper() {
        final ParameterizedRowMapper<CustomerAccount> rowMapper = new ParameterizedRowMapper<CustomerAccount>() {
            public CustomerAccount mapRow(ResultSet rs, int rowNum) throws SQLException {
                final CustomerAccount account = new CustomerAccount();
                account.setAccountId(rs.getInt("AccountID"));
                account.setAccountNotes(rs.getString("AccountNotes"));
                account.setAccountNumber(rs.getString("AccountNumber"));
                account.setAccountSiteId(rs.getInt("AccountSiteID"));
                account.setBillingAddressId(rs.getInt("BillingAddressID"));
                account.setCustomerId(rs.getInt("CustomerID"));
                return account;
            }
        };
        return rowMapper;
    }
    
}
