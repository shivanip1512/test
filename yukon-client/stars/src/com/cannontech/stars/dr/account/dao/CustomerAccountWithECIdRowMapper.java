package com.cannontech.stars.dr.account.dao;

import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;

public class CustomerAccountWithECIdRowMapper extends CustomerAccountRowMapper {

    public CustomerAccountWithECIdRowMapper() {}

    @Override
    public SqlFragmentSource getBaseQuery() {
        SqlStatementBuilder retVal = new SqlStatementBuilder();
        retVal.appendFragment(super.getBaseQuery());
        retVal.append("JOIN ECToAccountMapping ECAM ON ECAM.AccountId = CA.AccountId");
        
        return retVal;
    }
}
