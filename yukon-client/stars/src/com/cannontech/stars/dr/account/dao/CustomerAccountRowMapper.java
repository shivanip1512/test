package com.cannontech.stars.dr.account.dao;

import java.sql.SQLException;

import com.cannontech.common.bulk.filter.AbstractRowMapperWithBaseQuery;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.YukonResultSet;
import com.cannontech.stars.dr.account.model.CustomerAccount;

public class CustomerAccountRowMapper extends AbstractRowMapperWithBaseQuery<CustomerAccount> {

    public CustomerAccountRowMapper() {}

    @Override
    public SqlFragmentSource getBaseQuery() {
        SqlStatementBuilder retVal = new SqlStatementBuilder();
        retVal.append("SELECT CA.AccountId, CA.AccountSiteId, CA.AccountNumber, CA.CustomerId,");
        retVal.append(       "CA.BillingAddressId, CA.AccountNotes");
        retVal.append("FROM CustomerAccount CA");
        return retVal;
    }

    @Override
    public boolean needsWhere() {
        return true;
    };
    
    @Override
    public SqlFragmentSource getOrderBy() {
        SqlStatementBuilder retVal = new SqlStatementBuilder();
        retVal.append("ORDER BY UPPER(CA.AccountNumber)");
        return retVal;
    }

    @Override
    public CustomerAccount mapRow(YukonResultSet rs) throws SQLException {
        final CustomerAccount account = new CustomerAccount();
        account.setAccountId(rs.getInt("AccountId"));
        account.setAccountNotes(rs.getStringSafe("AccountNotes"));
        account.setAccountNumber(rs.getStringSafe("AccountNumber"));
        account.setAccountSiteId(rs.getInt("AccountSiteId"));
        account.setBillingAddressId(rs.getInt("BillingAddressId"));
        account.setCustomerId(rs.getInt("CustomerId"));
        return account;
    }
}
