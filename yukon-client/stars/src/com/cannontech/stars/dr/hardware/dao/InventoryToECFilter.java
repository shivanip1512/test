package com.cannontech.stars.dr.hardware.dao;

import java.util.List;

import com.cannontech.common.bulk.filter.SqlFilter;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;

public class InventoryToECFilter implements SqlFilter {
    private List<Integer> energyCompanyIds;

    public InventoryToECFilter(List<Integer> energyCompanyIds) {
        this.energyCompanyIds = energyCompanyIds;
    }

    @Override
    public SqlFragmentSource getWhereClauseFragment() {
        SqlStatementBuilder retVal = new SqlStatementBuilder();
        retVal.append("ECIM.EnergyCompanyId").in(energyCompanyIds);
        return retVal;
    }
}
