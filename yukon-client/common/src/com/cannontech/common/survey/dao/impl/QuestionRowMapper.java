package com.cannontech.common.survey.dao.impl;

import java.sql.SQLException;

import com.cannontech.common.bulk.filter.AbstractRowMapperWithBaseQuery;
import com.cannontech.common.survey.model.Question;
import com.cannontech.common.survey.model.QuestionType;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.YukonResultSet;

public class QuestionRowMapper extends
        AbstractRowMapperWithBaseQuery<Question> {
    final static SqlFragmentSource baseQuery;
    static {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT surveyQuestionId, surveyId, questionKey,");
        sql.append("answerRequired, questionType, textAnswerAllowed,");
        sql.append("displayOrder");
        sql.append("FROM surveyQuestion");
        baseQuery = sql;
    }

    @Override
    public Question mapRow(YukonResultSet rs) throws SQLException {
        Question retVal = new Question();

        retVal.setSurveyQuestionId(rs.getInt("surveyQuestionId"));
        retVal.setSurveyId(rs.getInt("surveyId"));
        retVal.setQuestionKey(rs.getString("questionKey"));
        boolean answerRequired =
            rs.getString("answerRequired").equalsIgnoreCase("y");
        retVal.setAnswerRequired(answerRequired);
        QuestionType questionType =
            QuestionType.valueOf(rs.getString("questionType"));
        retVal.setQuestionType(questionType);
        boolean textAnswerAllowed =
            rs.getString("textAnswerAllowed").equalsIgnoreCase("y");
        retVal.setTextAnswerAllowed(textAnswerAllowed);
        retVal.setDisplayOrder(rs.getInt("displayOrder"));

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
