package com.cannontech.stars.core.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

import com.cannontech.database.data.lite.stars.LiteStarsCustAccountInformation;
import com.cannontech.stars.core.dao.ECSearchDao;

public class ECSearchDaoImpl implements ECSearchDao {
    private static final String searchByLocationAddress1Sql;
    private static final ParameterizedRowMapper<Integer> accountSiteRowMapper;
    private SimpleJdbcTemplate simpleJdbcTemplate;
    
    static {
        
        searchByLocationAddress1Sql = "SELECT AccountSiteID FROM AccountSite,Address " +
                                      "WHERE AccountSite.StreetAddressID = Address.AddressID " +
                                      "AND Address.LocationAddress1 like ?";
        
        accountSiteRowMapper = ECSearchDaoImpl.createAccountSiteRowMapper();
        
    }
    
    public List<LiteStarsCustAccountInformation> searchAddressByLocationAddress1(final String locationAddress1, final List<LiteStarsCustAccountInformation> accountInfoList) {
        final List<LiteStarsCustAccountInformation> matchedAccountInfoList = new ArrayList<LiteStarsCustAccountInformation>();
        
        try {
            String address = locationAddress1 + "%";
            List<Integer> accountSiteIdList = simpleJdbcTemplate.query(searchByLocationAddress1Sql, accountSiteRowMapper, address);
            for (final LiteStarsCustAccountInformation account : accountInfoList) {
                Integer accountSiteId = Integer.valueOf(account.getAccountSite().getAccountSiteID());
                if (accountSiteIdList.contains(accountSiteId)) matchedAccountInfoList.add(account);
            }
        } catch (DataAccessException ignore) { }
        
        return matchedAccountInfoList;
    }
    
    private static final ParameterizedRowMapper<Integer> createAccountSiteRowMapper() {
        ParameterizedRowMapper<Integer> rowMapper = new ParameterizedRowMapper<Integer>() {
            public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
                int id = rs.getInt("AccountSiteID");
                return Integer.valueOf(id);
            }
        };
        return rowMapper;
    }

    public void setSimpleJdbcTemplate(SimpleJdbcTemplate simpleJdbcTemplate) {
        this.simpleJdbcTemplate = simpleJdbcTemplate;
    }
    
}
