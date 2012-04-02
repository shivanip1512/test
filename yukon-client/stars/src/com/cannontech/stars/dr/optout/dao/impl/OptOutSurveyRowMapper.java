package com.cannontech.stars.dr.optout.dao.impl;

import java.sql.SQLException;

import com.cannontech.common.bulk.filter.AbstractRowMapperWithBaseQuery;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.YukonResultSet;
import com.cannontech.stars.dr.optout.model.OptOutSurvey;

public class OptOutSurveyRowMapper extends
        AbstractRowMapperWithBaseQuery<OptOutSurvey> {
    final static SqlFragmentSource baseQuery;
    final static SqlFragmentSource orderBy;
    static {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT oos.optOutSurveyId, oos.surveyId, oos.startDate,");
        sql.append(    "oos.stopDate, s.energyCompanyId, s.surveyName");
        sql.append("FROM optOutSurvey oos");
        sql.append("JOIN survey s ON oos.surveyId = s.surveyId");
        baseQuery = sql;

        sql = new SqlStatementBuilder();
        sql.append("ORDER BY startDate DESC");
        orderBy = sql;
    }

    @Override
    public OptOutSurvey mapRow(YukonResultSet rs) throws SQLException {
        OptOutSurvey retVal = new OptOutSurvey();
        retVal.setOptOutSurveyId(rs.getInt("optOutSurveyId"));
        retVal.setSurveyId(rs.getInt("surveyId"));
        retVal.setSurveyName(rs.getString("surveyName"));
        retVal.setStartDate(rs.getResultSet().getTimestamp("startDate"));
        retVal.setStopDate(rs.getResultSet().getTimestamp("stopDate"));
        retVal.setEnergyCompanyId(rs.getInt("energyCompanyId"));

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
