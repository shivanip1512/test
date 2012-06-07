package com.cannontech.stars.dr.account.dao;

import java.util.List;

import com.cannontech.common.bulk.filter.SqlFilter;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;

public class CustomerAccountToECFilter implements SqlFilter {
    private List<Integer> energyCompanyIds;

    public CustomerAccountToECFilter(List<Integer> energyCompanyIds) {
        this.energyCompanyIds = energyCompanyIds;
    }

    @Override
    public SqlFragmentSource getWhereClauseFragment() {
        SqlStatementBuilder retVal = new SqlStatementBuilder();
        retVal.append("ECAM.EnergyCompanyId").in(energyCompanyIds);
        return retVal;
    }
}
