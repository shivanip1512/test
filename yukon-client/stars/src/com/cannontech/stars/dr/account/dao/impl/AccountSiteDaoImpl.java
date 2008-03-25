package com.cannontech.stars.dr.account.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.lang.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.database.incrementer.NextValueHelper;
import com.cannontech.stars.dr.account.dao.AccountSiteDao;
import com.cannontech.stars.dr.account.model.AccountSite;

public class AccountSiteDaoImpl implements AccountSiteDao {
    private static final String selectSql;
    private static final ParameterizedRowMapper<AccountSite> rowMapper;
    private SimpleJdbcTemplate simpleJdbcTemplate;
    private NextValueHelper nextValueHelper;
    
    static {
        
        selectSql = "SELECT AccountSiteID,SiteInformationID,SiteNumber,StreetAddressID,PropertyNotes,CustomerStatus,CustAtHome FROM AccountSite";
        
        rowMapper = createRowMapper();
    }
    
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public boolean add(final AccountSite accountSite) {
        validateNotNull(accountSite);
        
        String sql = "INSERT INTO AccountSite (AccountSiteID,SiteInformationID,SiteNumber,StreetAddressID,PropertyNotes,CustomerStatus,CustAtHome) VALUES (?,?,?,?,?,?,?)";
        
        int accountSiteId = nextValueHelper.getNextValue("AccountSite");
        accountSite.setAccountSiteId(accountSiteId);
        
        int rows = simpleJdbcTemplate.update(sql, accountSiteId,
                                                  accountSite.getSiteInformationId(),
                                                  accountSite.getSiteNumber(),
                                                  accountSite.getStreetAddressId(),
                                                  accountSite.getPropertyNotes(),
                                                  accountSite.getCustomerStatus(),
                                                  accountSite.getCustAtHome());
        boolean result = (rows == 1);
        return result;
    }
    
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public boolean update(final AccountSite accountSite) {
        validateNotNull(accountSite);
        
        String sql = "UPDATE AccountSite SET SiteInformationID = ?, SiteNumber = ?, StreetAddressID = ?, PropertyNotes = ?, CustomerStatus = ?, CustAtHome = ? WHERE AccountSiteID = ?";
        
        int rows = simpleJdbcTemplate.update(sql, accountSite.getSiteInformationId(),
                                                  accountSite.getSiteNumber(),
                                                  accountSite.getStreetAddressId(),
                                                  accountSite.getPropertyNotes(),
                                                  accountSite.getCustomerStatus(),
                                                  accountSite.getCustAtHome(),
                                                  accountSite.getAccountSiteId());
        
        boolean result = (rows == 1);
        return result;
    }

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public boolean remove(final AccountSite accountSite) {
        validateNotNull(accountSite);
        
        String sql = "DELETE FROM AccountSite WHERE AccountSiteID = ?";
        
        int rows = simpleJdbcTemplate.update(sql, accountSite.getAccountSiteId());
        boolean result = (rows == 1);
        return result;
    }

    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public AccountSite getByAccountSiteId(int accountSiteId) {
        String sql = selectSql + " WHERE AccountSiteID = ?";
        AccountSite accountSite = simpleJdbcTemplate.queryForObject(sql, rowMapper, accountSiteId);
        return accountSite;
    }

    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public AccountSite getByAddressId(int addressId) {
        String sql = selectSql + " WHERE StreetAddressID = ?";
        AccountSite accountSite = simpleJdbcTemplate.queryForObject(sql, rowMapper, addressId);
        return accountSite;
    }

    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<AccountSite> getAll() {
        List<AccountSite> list = simpleJdbcTemplate.query(selectSql, rowMapper);
        return list;
    }
    
    private void validateNotNull(final AccountSite accountSite) {
        Validate.notNull(accountSite, "accountSite parameter cannot be null.");
    }
    
    private static ParameterizedRowMapper<AccountSite> createRowMapper() {
        ParameterizedRowMapper<AccountSite> mapper = new ParameterizedRowMapper<AccountSite>() {
            @Override
            public AccountSite mapRow(ResultSet rs, int rowNum) throws SQLException {
                AccountSite accountSite = new AccountSite();
                accountSite.setAccountSiteId(rs.getInt("AccountSiteID"));
                accountSite.setCustAtHome(rs.getString("CustAtHome"));
                accountSite.setCustomerStatus(rs.getString("CustomerStatus"));
                accountSite.setPropertyNotes(rs.getString("PropertyNotes"));
                accountSite.setSiteInformationId(rs.getInt("SiteInformationID"));
                accountSite.setSiteNumber(rs.getString("SiteNumber"));
                accountSite.setStreetAddressId(rs.getInt("StreetAddressID"));
                return accountSite;
            }
        };
        return mapper;
    }

    @Autowired
    public void setSimpleJdbcTemplate(SimpleJdbcTemplate simpleJdbcTemplate) {
        this.simpleJdbcTemplate = simpleJdbcTemplate;
    }
    
    @Autowired
    public void setNextValueHelper(NextValueHelper nextValueHelper) {
        this.nextValueHelper = nextValueHelper;
    }

}
