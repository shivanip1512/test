package com.cannontech.common.survey.dao.impl;

import java.sql.SQLException;

import com.cannontech.common.bulk.filter.AbstractRowMapperWithBaseQuery;
import com.cannontech.common.survey.model.Survey;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.YukonResultSet;

public class SurveyRowMapper extends AbstractRowMapperWithBaseQuery<Survey> {
    final static SqlFragmentSource baseQuery;
    final static SqlFragmentSource orderBy;
    static {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT surveyId, energyCompanyId, surveyName, surveyKey");
        sql.append("FROM survey");
        baseQuery = sql;

        sql = new SqlStatementBuilder();
        sql.append("ORDER BY surveyName");
        orderBy = sql;
    }

    @Override
    public Survey mapRow(YukonResultSet rs) throws SQLException {
        Survey retVal = new Survey();
        retVal.setSurveyId(rs.getInt("surveyId"));
        retVal.setEnergyCompanyId(rs.getInt("energyCompanyId"));
        retVal.setSurveyName(rs.getString("surveyName"));
        retVal.setSurveyKey(rs.getString("surveyKey"));
        return retVal;
    }

    @Override
    public SqlFragmentSource getBaseQuery() {
        return baseQuery;
    }

    @Override
    public SqlFragmentSource getOrderBy() {
        return orderBy;
    }

    @Override
    public boolean needsWhere() {
        return true;
    }
}
