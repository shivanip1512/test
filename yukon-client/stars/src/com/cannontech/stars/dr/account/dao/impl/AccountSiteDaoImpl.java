package com.cannontech.stars.dr.account.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.lang.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.AddressDao;
import com.cannontech.database.SqlUtils;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.incrementer.NextValueHelper;
import com.cannontech.stars.dr.account.dao.AccountSiteDao;
import com.cannontech.stars.dr.account.model.AccountSite;

public class AccountSiteDaoImpl implements AccountSiteDao {
    private static final String selectSql;
    private static final ParameterizedRowMapper<AccountSite> rowMapper;
    
    @Autowired private YukonJdbcTemplate yukonJdbcTemplate;
    @Autowired private NextValueHelper nextValueHelper;
    @Autowired private AddressDao addressDao;
    
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
        
        /* SiteNumber column in AccountSite does not allow nulls */
        String siteNumber = accountSite.getSiteNumber();
        if(siteNumber == null) {
            siteNumber = "";
        }
        
        int rows = yukonJdbcTemplate.update(sql, accountSiteId,
                                                  accountSite.getSiteInformationId(),
                                                  SqlUtils.convertStringToDbValue(siteNumber),
                                                  accountSite.getStreetAddressId(),
                                                  SqlUtils.convertStringToDbValue(accountSite.getPropertyNotes()),
                                                  SqlUtils.convertStringToDbValue(accountSite.getCustomerStatus()),
                                                  SqlUtils.convertStringToDbValue(accountSite.getCustAtHome()));
        boolean result = (rows == 1);
        return result;
    }
    
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public boolean update(final AccountSite accountSite) {
        validateNotNull(accountSite);
        
        String sql = "UPDATE AccountSite SET SiteInformationID = ?, SiteNumber = ?, StreetAddressID = ?, PropertyNotes = ?, CustomerStatus = ?, CustAtHome = ? WHERE AccountSiteID = ?";
        
        int rows = yukonJdbcTemplate.update(sql, accountSite.getSiteInformationId(),
        										  SqlUtils.convertStringToDbValue(accountSite.getSiteNumber()),
                                                  accountSite.getStreetAddressId(),
                                                  SqlUtils.convertStringToDbValue(accountSite.getPropertyNotes()),
                                                  SqlUtils.convertStringToDbValue(accountSite.getCustomerStatus()),
                                                  SqlUtils.convertStringToDbValue(accountSite.getCustAtHome()),
                                                  accountSite.getAccountSiteId());
        
        boolean result = (rows == 1);
        return result;
    }

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public boolean remove(final AccountSite accountSite) {
        validateNotNull(accountSite);
        
        SqlStatementBuilder deleteCustomerResidence = new SqlStatementBuilder();
        deleteCustomerResidence.append("DELETE FROM CustomerResidence");
        deleteCustomerResidence.append("WHERE AccountSiteId").eq(accountSite.getAccountSiteId());
        yukonJdbcTemplate.update(deleteCustomerResidence);
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("DELETE FROM AccountSite");
        sql.append("WHERE AccountSiteId").eq(accountSite.getAccountSiteId());
        int rows = yukonJdbcTemplate.update(sql);
        
        addressDao.remove(accountSite.getStreetAddressId());

        boolean result = (rows == 1);
        return result;
    }

    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public AccountSite getByAccountSiteId(int accountSiteId) {
        String sql = selectSql + " WHERE AccountSiteID = ?";
        AccountSite accountSite = yukonJdbcTemplate.queryForObject(sql, rowMapper, accountSiteId);
        return accountSite;
    }

    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public AccountSite getByAddressId(int addressId) {
        String sql = selectSql + " WHERE StreetAddressID = ?";
        AccountSite accountSite = yukonJdbcTemplate.queryForObject(sql, rowMapper, addressId);
        return accountSite;
    }

    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<AccountSite> getAll() {
        List<AccountSite> list = yukonJdbcTemplate.query(selectSql, rowMapper);
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
                accountSite.setCustAtHome(SqlUtils.convertDbValueToString(rs, "CustAtHome"));
                accountSite.setCustomerStatus(SqlUtils.convertDbValueToString(rs, "CustomerStatus"));
                accountSite.setPropertyNotes(SqlUtils.convertDbValueToString(rs, "PropertyNotes"));
                accountSite.setSiteInformationId(rs.getInt("SiteInformationID"));
                accountSite.setSiteNumber(SqlUtils.convertDbValueToString(rs, "SiteNumber"));
                accountSite.setStreetAddressId(rs.getInt("StreetAddressID"));
                return accountSite;
            }
        };
        return mapper;
    }
}