package com.cannontech.common.survey.dao.impl;

import com.cannontech.common.bulk.filter.SqlFilter;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;

public class EnergyCompanyFilter implements SqlFilter {
    private int energyCompanyId;

    public EnergyCompanyFilter(int energyCompanyId) {
        this.energyCompanyId = energyCompanyId;
    }

    @Override
    public SqlFragmentSource getWhereClauseFragment() {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("energyCompanyId").eq(energyCompanyId);
        return sql;
    }
}
