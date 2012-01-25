package com.cannontech.common.survey.dao.impl;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.common.bulk.filter.RowMapperWithBaseQuery;
import com.cannontech.common.survey.dao.SurveyDao;
import com.cannontech.common.survey.model.Answer;
import com.cannontech.common.survey.model.Question;
import com.cannontech.common.survey.model.Result;
import com.cannontech.common.survey.model.ResultAnswer;
import com.cannontech.common.survey.model.Survey;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.DuplicateException;
import com.cannontech.database.FieldMapper;
import com.cannontech.database.SimpleTableAccessTemplate;
import com.cannontech.database.StringRowMapper;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.incrementer.NextValueHelper;
import com.google.common.collect.Maps;

public class SurveyDaoImpl implements SurveyDao {
    private YukonJdbcTemplate yukonJdbcTemplate;
    private NextValueHelper nextValueHelper;

    private SimpleTableAccessTemplate<Survey> dbTemplate;
    private final static RowMapperWithBaseQuery<Survey> surveyRowMapper =
        new SurveyRowMapper();
    private final static FieldMapper<Survey> surveyFieldMapper = new FieldMapper<Survey>() {
        @Override
        public void setPrimaryKey(Survey survey, int surveyId) {
            survey.setSurveyId(surveyId);
        }

        @Override
        public Number getPrimaryKey(Survey survey) {
            return survey.getSurveyId();
        }

        @Override
        public void extractValues(MapSqlParameterSource parameterHolder,
                Survey survey) {
            parameterHolder.addValue("energyCompanyId", survey.getEnergyCompanyId());
            parameterHolder.addValue("surveyName", survey.getSurveyName());
            parameterHolder.addValue("surveyKey", survey.getSurveyKey());
        }
    };
    private final static RowMapperWithBaseQuery<Question> questionRowMapper =
        new QuestionRowMapper();
    private final static RowMapperWithBaseQuery<Answer> answerRowMapper =
        new AnswerRowMapper();

    @Override
    public Survey getSurveyById(int surveyId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append(surveyRowMapper.getBaseQuery());
        sql.append("WHERE surveyId").eq(surveyId);
        return yukonJdbcTemplate.queryForObject(sql, surveyRowMapper);
    }


    @Override
    @Transactional
    public Question getQuestionById(int surveyQuestionId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append(questionRowMapper.getBaseQuery());
        sql.append("WHERE surveyQuestionId").eq(surveyQuestionId);
        Question question =
            yukonJdbcTemplate.queryForObject(sql, questionRowMapper);

        sql = new SqlStatementBuilder();
        sql.append(answerRowMapper.getBaseQuery());
        sql.append("WHERE surveyQuestionId").eq(surveyQuestionId);
        sql.append("ORDER BY displayOrder");
        List<Answer> answers = yukonJdbcTemplate.query(sql, answerRowMapper);
        question.setAnswers(answers);

        return question;
    }

    @Override
    @Transactional
    public List<Question> getQuestionsBySurveyId(int surveyId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append(questionRowMapper.getBaseQuery());
        sql.append("WHERE surveyId").eq(surveyId);
        sql.append("ORDER BY displayOrder");
        List<Question> questions = yukonJdbcTemplate.query(sql, questionRowMapper);
        Map<Integer, Question> questionsById = Maps.newHashMap();
        for (Question question : questions) {
            questionsById.put(question.getSurveyQuestionId(), question);
        }

        sql = new SqlStatementBuilder();
        sql.append(answerRowMapper.getBaseQuery());
        sql.append("WHERE surveyQuestionId IN");
        sql.append(    "(SELECT surveyQuestionId FROM surveyQuestion");
        sql.append(    "WHERE surveyId").eq(surveyId);
        sql.append(")");
        sql.append("ORDER BY surveyQuestionId, displayOrder");
        List<Answer> answers = yukonJdbcTemplate.query(sql, answerRowMapper);
        for (Answer answer : answers) {
            Question question = questionsById.get(answer.getSurveyQuestionId());
            question.addAnswer(answer);
        }

        return questions;
    }

    @Override
    public Map<Integer, Question> getQuestionMapBySurveyId(int surveyId) {
        List<Question> questions = getQuestionsBySurveyId(surveyId);
        Map<Integer, Question> retVal = Maps.newHashMap();
        for (Question question : questions) {
            retVal.put(question.getSurveyQuestionId(), question);
        }
        return retVal;
    }

    @Override
    @Transactional
    public String getNextSurveyKey() {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT surveyKey FROM survey");
        sql.append("WHERE surveyKey").startsWith("survey");
        return getNextNamedKey(sql, "survey");
    }

    @Override
    @Transactional
    public String getNextQuestionKey(int surveyId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT questionKey FROM surveyQuestion");
        sql.append("WHERE questionKey").startsWith("question");
        sql.append("AND surveyId").eq(surveyId);
        return getNextNamedKey(sql, "question");
    }

    private String getNextNamedKey(SqlFragmentSource sql, String base) {
        List<String> currentKeys =
            yukonJdbcTemplate.query(sql, new StringRowMapper());
        int maxKeyNumber = 0;
        Pattern keyPattern = Pattern.compile("^" + base + "(\\d+)$");

        for (String key : currentKeys) {
            Matcher matcher = keyPattern.matcher(key);
            if (matcher.matches()) {
                int keyNumber = Integer.parseInt(matcher.group(1));
                if (keyNumber > maxKeyNumber) maxKeyNumber = keyNumber;
            }
        }

        return base + (maxKeyNumber + 1);
    }

    @Override
    @Transactional(readOnly=true)
    public boolean usedByOptOutSurvey(int surveyId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT COUNT(*) FROM optOutSurvey");
        sql.append("WHERE surveyId").eq(surveyId);
        int numUses = yukonJdbcTemplate.queryForInt(sql);
        return numUses > 0;
    }

    @Override
    @Transactional(readOnly=true)
    public boolean hasBeenTaken(int surveyId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT COUNT(*) FROM surveyResult");
        sql.append("WHERE surveyId").eq(surveyId);
        int numUses = yukonJdbcTemplate.queryForInt(sql);
        return numUses > 0;
    }
    
    @Override
    @Transactional
    public void saveSurvey(Survey survey) {
        if (hasBeenTaken(survey.getSurveyId())) {
            throw new RuntimeException("can't modify survey which has been taken");
        }
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT COUNT(*) FROM survey");
        sql.append("WHERE energyCompanyId").eq(survey.getEnergyCompanyId());
        sql.append("AND surveyKey").eq(survey.getSurveyKey());
        if (survey.getSurveyId() != 0) {
            sql.append("AND surveyId").neq(survey.getSurveyId());
        }
        int numDuplicates = yukonJdbcTemplate.queryForInt(sql);
        if (numDuplicates > 0) {
            throw new DuplicateException("surveyKey");
        }

        sql = new SqlStatementBuilder();
        sql.append("SELECT COUNT(*) FROM survey");
        sql.append("WHERE energyCompanyId").eq(survey.getEnergyCompanyId());
        sql.append("AND surveyName").eq(survey.getSurveyName());
        if (survey.getSurveyId() != 0) {
            sql.append("AND surveyId").neq(survey.getSurveyId());
        }
        numDuplicates = yukonJdbcTemplate.queryForInt(sql);
        if (numDuplicates > 0) {
            throw new DuplicateException("surveyName");
        }

        dbTemplate.save(survey);
    }

    @Override
    @Transactional
    public void saveQuestion(Question question) {
        int surveyId = question.getSurveyId();
        if (hasBeenTaken(surveyId)) {
            throw new RuntimeException("can't modify survey which has been taken");
        }

        int surveyQuestionId = question.getSurveyQuestionId();
        if (surveyQuestionId != 0) {
            deleteQuestion(surveyQuestionId, surveyId);
        }

        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT COUNT(*) FROM surveyQuestion");
        sql.append("WHERE surveyId").eq(question.getSurveyId());
        sql.append("AND questionKey").eq(question.getQuestionKey());
        if (surveyQuestionId != 0) {
            sql.append("AND surveyQuestionId").neq(surveyQuestionId);
        }
        int numDuplicates = yukonJdbcTemplate.queryForInt(sql);
        if (numDuplicates > 0) {
            throw new DuplicateException("questionKey");
        }

        int displayOrder = question.getDisplayOrder();
        if (surveyQuestionId == 0) {
            surveyQuestionId = nextValueHelper.getNextValue("surveyQuestion");
            sql = new SqlStatementBuilder();
            sql.append("SELECT MAX(displayOrder) FROM surveyQuestion");
            sql.append("WHERE surveyId").eq(question.getSurveyId());
            displayOrder = 1;
            try {
                displayOrder = yukonJdbcTemplate.queryForInt(sql) + 1;
            } catch (IncorrectResultSizeDataAccessException dae) {
                // This just means there wasn't a row in the database yet.
            }
        }

        sql = new SqlStatementBuilder();
        sql.append("INSERT INTO surveyQuestion (");
        sql.append("surveyQuestionId, surveyId, questionKey,");
        sql.append("answerRequired, questionType, textAnswerAllowed,");
        sql.append("displayOrder");
        sql.append(") VALUES (");
        sql.appendArgument(surveyQuestionId);
        sql.append(",").appendArgument(question.getSurveyId());
        sql.append(",").appendArgument(question.getQuestionKey());
        sql.append(",").appendArgument(question.isAnswerRequired() ? "Y" : "N");
        sql.append(",").appendArgument(question.getQuestionType().toString());
        sql.append(",").appendArgument(question.isTextAnswerAllowed() ? "Y" : "N");
        sql.append(",").appendArgument(displayOrder);
        sql.append(")");
        yukonJdbcTemplate.update(sql);

        for (Answer answer : question.getAnswers()) {
            sql = new SqlStatementBuilder();
            int answerId = nextValueHelper.getNextValue("surveyQuestionAnswer");
            sql.append("INSERT INTO surveyQuestionAnswer (");
            sql.append("surveyQuestionAnswerId, surveyQuestionId,");
            sql.append("displayOrder, answerKey");
            sql.append(") VALUES (");
            sql.appendArgument(answerId);
            sql.append(",").appendArgument(surveyQuestionId);
            sql.append(",").appendArgument(answer.getDisplayOrder());
            sql.append(",").appendArgument(answer.getAnswerKey());
            sql.append(")");
            answer.setSurveyQuestionAnswerId(answerId);
            yukonJdbcTemplate.update(sql);
        }

        question.setSurveyQuestionId(surveyQuestionId);
        question.setDisplayOrder(displayOrder);
    }

    @Override
    @Transactional
    public void moveQuestionUp(Question question) {
        swapQuestion(question, question.getDisplayOrder() - 1);
    }

    @Override
    @Transactional
    public void moveQuestionDown(Question question) {
        swapQuestion(question, question.getDisplayOrder() + 1);
    }

    /**
     * Swap the passed in question with whatever question has the new display
     * order.
     * @param question The question which is to be swapped.
     * @param newDisplayOrder The display order we want this question to have.
     *            The question which currently has this display order will get
     *            updated with question's display order.
     */
    private void swapQuestion(Question question, int newDisplayOrder) {
        if (hasBeenTaken(question.getSurveyId())) {
            throw new RuntimeException("can't modify survey which has been taken");
        }

        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("UPDATE surveyQuestion");
        sql.append("SET displayOrder = ").appendArgument(0);
        sql.append("WHERE surveyId").eq(question.getSurveyId());
        sql.append("AND displayOrder").eq(newDisplayOrder);
        yukonJdbcTemplate.update(sql);

        sql = new SqlStatementBuilder();
        sql.append("UPDATE surveyQuestion");
        sql.append("SET displayOrder = ").appendArgument(newDisplayOrder);
        sql.append("WHERE surveyId").eq(question.getSurveyId());
        sql.append("AND surveyQuestionId").eq(question.getSurveyQuestionId());
        yukonJdbcTemplate.update(sql);

        sql = new SqlStatementBuilder();
        sql.append("UPDATE surveyQuestion");
        sql.append("SET displayOrder = ").appendArgument(question.getDisplayOrder());
        sql.append("WHERE surveyId").eq(question.getSurveyId());
        sql.append("AND displayOrder").eq(0);
        yukonJdbcTemplate.update(sql);

        question.setDisplayOrder(newDisplayOrder);
    }

    @Override
    @Transactional
    public void deleteSurvey(int surveyId) {
        if (usedByOptOutSurvey(surveyId) || hasBeenTaken(surveyId)) {
            throw new RuntimeException("can't delete survey which has been " +
                "taken or is used by an opt out survey");
        }

        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("DELETE FROM surveyQuestionAnswer");
        sql.append("WHERE surveyQuestionId IN(");
        sql.append(    "SELECT surveyQuestionId FROM surveyQuestion");
        sql.append(    "WHERE surveyId").eq(surveyId);
        sql.append(")");
        yukonJdbcTemplate.update(sql);

        sql = new SqlStatementBuilder();
        sql.append("DELETE FROM surveyQuestion WHERE surveyId").eq(surveyId);
        yukonJdbcTemplate.update(sql);

        sql = new SqlStatementBuilder();
        sql.append("DELETE FROM survey WHERE surveyId").eq(surveyId);
        yukonJdbcTemplate.update(sql);
    }

    private void deleteQuestion(int surveyQuestionId, int surveyId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("DELETE FROM surveyQuestion");
        sql.append("WHERE surveyQuestionId").eq(surveyQuestionId);
        yukonJdbcTemplate.update(sql);
    }

    @Override
    @Transactional
    public void deleteQuestion(int surveyQuestionId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT surveyId FROM surveyQuestion");
        sql.append("WHERE surveyQuestionId").eq(surveyQuestionId);
        int surveyId = yukonJdbcTemplate.queryForInt(sql);
        if (hasBeenTaken(surveyId)) {
            throw new RuntimeException("can't modify survey which has been taken");
        }

        deleteQuestion(surveyQuestionId, surveyId);
    }

    @Override
    @Transactional
    public void saveResult(Result result) {
        int surveyResultId = nextValueHelper.getNextValue("surveyResult");

        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("INSERT INTO surveyResult (surveyResultId,");
        sql.append("surveyId, accountId, accountNumber, whenTaken)");
        sql.values(surveyResultId, result.getSurveyId(), result.getAccountId(),
                   result.getAccountNumber(), result.getWhenTaken());
        yukonJdbcTemplate.update(sql);

        for (ResultAnswer answer : result.getResultAnswers()) {
            int surveyResultAnswerId = nextValueHelper.getNextValue("surveyResultAnswer");
            answer.setSurveyResultAnswerId(surveyResultAnswerId);
            answer.setSurveyResultId(surveyResultId);

            sql = new SqlStatementBuilder();
            sql.append("INSERT INTO surveyResultAnswer (surveyResultAnswerId,");
            sql.append("surveyResultId, surveyQuestionId,");
            sql.append("surveyQuestionAnswerId, textAnswer)");
            String textAnswer = answer.getTextAnswer();
            if (StringUtils.isBlank(textAnswer)) {
                textAnswer = null;
            }
            sql.values(surveyResultAnswerId, surveyResultId,
                       answer.getSurveyQuestionId(),
                       answer.getSurveyQuestionAnswerId(),
                       textAnswer);
            yukonJdbcTemplate.update(sql);
        }

        result.setSurveyResultId(surveyResultId);
    }

    @PostConstruct
    public void init() {
        dbTemplate = new SimpleTableAccessTemplate<Survey>(yukonJdbcTemplate, nextValueHelper);
        dbTemplate.setTableName("survey");
        dbTemplate.setFieldMapper(surveyFieldMapper);
        dbTemplate.setPrimaryKeyField("surveyId");
        dbTemplate.setPrimaryKeyValidOver(0);
    }

    @Autowired
    public void setYukonJdbcTemplate(YukonJdbcTemplate yukonJdbcTemplate) {
        this.yukonJdbcTemplate = yukonJdbcTemplate;
    }

    @Autowired
    public void setNextValueHelper(NextValueHelper nextValueHelper) {
        this.nextValueHelper = nextValueHelper;
    }
}
