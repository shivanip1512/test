package com.cannontech.common.survey.dao.impl;

import java.sql.SQLException;

import com.cannontech.common.bulk.filter.AbstractRowMapperWithBaseQuery;
import com.cannontech.common.survey.model.Answer;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.YukonResultSet;

public class AnswerRowMapper extends
        AbstractRowMapperWithBaseQuery<Answer> {
    final static SqlFragmentSource baseQuery;
    static {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT surveyQuestionAnswerId, surveyQuestionId,");
        sql.append("displayOrder, answerKey");
        sql.append("FROM surveyQuestionAnswer");
        baseQuery = sql;
    }

    @Override
    public Answer mapRow(YukonResultSet rs) throws SQLException {
        Answer retVal = new Answer();

        retVal.setSurveyQuestionAnswerId(rs.getInt("surveyQuestionAnswerId"));
        retVal.setSurveyQuestionId(rs.getInt("surveyQuestionId"));
        retVal.setDisplayOrder(rs.getInt("displayOrder"));
        retVal.setAnswerKey(rs.getString("answerKey"));

        return retVal;
    }

    @Override
    public SqlFragmentSource getBaseQuery() {
        return baseQuery;
    }

    @Override
    public boolean needsWhere() {
        return true;
    }
}
