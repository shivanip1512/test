package com.cannontech.stars.dr.optout.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.PostConstruct;

import org.apache.commons.lang.StringUtils;
import org.joda.time.ReadableInstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.common.bulk.filter.RowMapperWithBaseQuery;
import com.cannontech.common.util.ChunkingMappedSqlTemplate;
import com.cannontech.common.util.SqlFragmentCollection;
import com.cannontech.common.util.SqlFragmentGenerator;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.FieldMapper;
import com.cannontech.database.IntegerRowMapper;
import com.cannontech.database.SimpleTableAccessTemplate;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.database.incrementer.NextValueHelper;
import com.cannontech.stars.dr.optout.dao.OptOutSurveyDao;
import com.cannontech.stars.dr.optout.model.OptOutAction;
import com.cannontech.stars.dr.optout.model.OptOutSurvey;
import com.cannontech.stars.dr.optout.model.SurveyResult;
import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;

public class OptOutSurveyDaoImpl implements OptOutSurveyDao {
    private YukonJdbcTemplate yukonJdbcTemplate;
    private NextValueHelper nextValueHelper;
    private SimpleTableAccessTemplate<OptOutSurvey> dbTemplate;

    private final static ParameterizedRowMapper<Map.Entry<Integer, Integer>> optOutProgramRowMapper =
        new ParameterizedRowMapper<Map.Entry<Integer, Integer>>(){
        @Override
        public Entry<Integer, Integer> mapRow(ResultSet rs, int rowNum)
                throws SQLException {
            int optOutSurveyId = rs.getInt("optOutSurveyId");
            int programId = rs.getInt("programId");
            return Maps.immutableEntry(optOutSurveyId, programId);
        }};

    private final static RowMapperWithBaseQuery<OptOutSurvey> optOutSurveyRowMapper =
        new OptOutSurveyRowMapper();
    private final static FieldMapper<OptOutSurvey> optOutSurveyFieldMapper = new FieldMapper<OptOutSurvey>() {
        @Override
        public void setPrimaryKey(OptOutSurvey optOutSurvey, int optOutSurveyId) {
            optOutSurvey.setOptOutSurveyId(optOutSurveyId);
        }

        @Override
        public Number getPrimaryKey(OptOutSurvey optOutSurvey) {
            return optOutSurvey.getOptOutSurveyId();
        }

        @Override
        public void extractValues(MapSqlParameterSource parameterHolder,
                OptOutSurvey optOutSurvey) {
            parameterHolder.addValue("surveyId", optOutSurvey.getSurveyId());
            parameterHolder.addValue("startDate", optOutSurvey.getStartDate());
            parameterHolder.addValue("stopDate", optOutSurvey.getStopDate());
        }
    };

    @Override
    @Transactional(readOnly = true)
    public OptOutSurvey getOptOutSurveyById(int optOutSurveyId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append(optOutSurveyRowMapper.getBaseQuery());
        sql.append("WHERE optOutSurveyId").eq(optOutSurveyId);
        OptOutSurvey retVal =
            yukonJdbcTemplate.queryForObject(sql, optOutSurveyRowMapper);

        sql = new SqlStatementBuilder();
        sql.append("SELECT deviceId AS programId FROM optOutSurveyProgram");
        sql.append("WHERE optOutSurveyId").eq(optOutSurveyId);
        List<Integer> programIds =
            yukonJdbcTemplate.query(sql, new IntegerRowMapper());
        retVal.setProgramIds(programIds);

        return retVal;
    }

    @Override
    @Transactional(readOnly=true)
    public Multimap<OptOutSurvey, Integer> getProgramsForOptOutSurveys(
            Iterable<OptOutSurvey> optOutSurveys) {
        SqlFragmentGenerator<Integer> sqlGenerator = new SqlFragmentGenerator<Integer>() {
            @Override
            public SqlFragmentSource generate(List<Integer> optOutSurveyIds) {
                SqlStatementBuilder sql = new SqlStatementBuilder();
                sql.append("SELECT oos.optOutSurveyId, p.deviceId AS programId");
                sql.append("FROM optOutSurveyProgram p");
                sql.append("JOIN optOutSurvey oos ON oos.optOutSurveyId = p.optOutSurveyId");
                sql.append("JOIN survey s ON oos.surveyId = s.surveyId");
                sql.append("WHERE oos.optOutSurveyId").in(optOutSurveyIds);
                return sql;
            }
        };

        Function<OptOutSurvey, Integer> typeMapper = new Function<OptOutSurvey, Integer>() {
            @Override
            public Integer apply(OptOutSurvey from) {
                return from.getOptOutSurveyId();
            }
        };

        ChunkingMappedSqlTemplate template =
            new ChunkingMappedSqlTemplate(yukonJdbcTemplate);

        Multimap<OptOutSurvey, Integer> retVal =
            template.multimappedQuery(sqlGenerator, optOutSurveys, optOutProgramRowMapper, typeMapper);
        return retVal;
    }

    @Override
    @Transactional(readOnly=true)
    public Multimap<Integer, Integer> getCurrentSurveysByProgramId(
            Iterable<Integer> programIds) {
        ChunkingMappedSqlTemplate template =
            new ChunkingMappedSqlTemplate(yukonJdbcTemplate);

        final Date now = new Date();
        SqlFragmentGenerator<Integer> sqlGenerator = new SqlFragmentGenerator<Integer>() {
            public SqlFragmentSource generate(List<Integer> subList) {
                SqlStatementBuilder sql = new SqlStatementBuilder();
                sql.append("SELECT oosp.deviceId AS programId, oos.surveyId");
                sql.append("FROM optOutSurvey oos");
                sql.append("JOIN optOutSurveyProgram oosp");
                sql.append(    "ON oos.optOutSurveyId = oosp.optOutSurveyId");
                sql.append("WHERE startDate").lt(now);
                sql.append("AND (stopDate IS NULL");
                sql.append(    "OR stopDate").gt(now).append(")");
                sql.append("AND oosp.deviceId").in(subList);
                return sql;
            }
        };

        Function<Integer, Integer> typeMapper = Functions.identity();
        ParameterizedRowMapper<Map.Entry<Integer, Integer>> rowMapper = new ParameterizedRowMapper<Entry<Integer,Integer>>() {
            @Override
            public Entry<Integer, Integer> mapRow(ResultSet rs, int rowNum)
                    throws SQLException {
                Integer programId = rs.getInt("programId");
                Integer surveyId = rs.getInt("surveyId");
                return Maps.immutableEntry(programId, surveyId);
            }
        };

        Multimap<Integer, Integer> retVal =
            template.multimappedQuery(sqlGenerator, programIds, rowMapper, typeMapper);

        return retVal;
    }

    @Override
    @Transactional
    public void saveOptOutSurvey(OptOutSurvey optOutSurvey) {
        dbTemplate.save(optOutSurvey);

        int optOutSurveyId = optOutSurvey.getOptOutSurveyId();
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("DELETE FROM optOutSurveyProgram");
        sql.append("WHERE optOutSurveyId").eq(optOutSurveyId);
        yukonJdbcTemplate.update(sql);

        for (int programId : optOutSurvey.getProgramIds()) {
            sql = new SqlStatementBuilder();
            sql.append("INSERT INTO optOutSurveyProgram");
            sql.append("(optOutSurveyId, deviceId)");
            sql.values(optOutSurveyId, programId);
            yukonJdbcTemplate.update(sql);
        }
    }

    @Override
    @Transactional
    public void deleteOptOutSurvey(int optOutSurveyId) {
        // TODO:  make sure this hasn't been taken
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("DELETE FROM optOutSurvey");
        sql.append("WHERE optOutSurveyId").eq(optOutSurveyId);
        yukonJdbcTemplate.update(sql);
    }

    @Override
    public void saveResult(int surveyResultId, int optOutEventLogId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("INSERT INTO optOutSurveyResult");
        sql.append("(surveyResultId, optOutEventLogId)");
        sql.values(surveyResultId, optOutEventLogId);
        yukonJdbcTemplate.update(sql);
    }

    @Override
    public List<SurveyResult> findSurveyResults(int surveyId,
            int questionId, Iterable<Integer> answerIds,
            boolean includeOtherAnswers, boolean includeUnanswered,
            boolean includeScheduledSurveys,
            ReadableInstant begin, ReadableInstant end,
            String accountNumber, String serialNumber) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT r.surveyResultId, r.surveyId, r.whenTaken,");
        sql.append(    "r.accountId, r.accountNumber, ra.surveyResultAnswerId,");
        sql.append(    "ra.surveyQuestionId, ra.surveyQuestionAnswerId, ");
        sql.append(    "sqa.answerKey, ra.textAnswer, OOE.ScheduledDate, ");
        sql.append(    "OOE.StartDate, OOE.StopDate, l.inventoryId, l.customerAccountId");
        sql.append("FROM surveyResult r");
        sql.append(    "JOIN surveyResultAnswer ra ON r.surveyResultId = ra.surveyResultId");
        sql.append(    "JOIN optOutSurveyResult oosr ON oosr.surveyResultId = r.surveyResultId");
        sql.append(    "JOIN optOutEventLog l ON l.optOutEventLogId = oosr.optOutEventLogId");
        sql.append(    "JOIN OptOutEvent OOE ON OOE.OptOutEventId = l.OptOutEventId");
        sql.append(    "LEFT JOIN surveyQuestionAnswer sqa ON ra.surveyQuestionAnswerId = sqa.surveyQuestionAnswerId");
        if (!StringUtils.isBlank(serialNumber)) {
            sql.append("JOIN lmHardwareBase hwb ON hwb.inventoryId = l.inventoryId");
        }
        sql.append("WHERE r.surveyId").eq(surveyId);
        sql.append(    "AND ra.surveyQuestionId").eq(questionId);
        sql.append(    "AND (l.eventAction").eq_k(OptOutAction.START_OPT_OUT);
        if (includeScheduledSurveys) {
            sql.append(         "OR l.eventAction").eq_k(OptOutAction.SCHEDULE);
        }
        sql.append(         ")");
        if (!StringUtils.isBlank(accountNumber)) {
            sql.append("AND r.accountNumber").eq(accountNumber);
        }
        if (!StringUtils.isBlank(serialNumber)) {
            sql.append("AND hwb.manufacturerSerialNumber").eq(serialNumber);
        }

        SqlFragmentCollection answersOr = SqlFragmentCollection.newOrCollection();
        if (answerIds != null && answerIds.iterator().hasNext()) {
            SqlStatementBuilder answerIdsSql = new SqlStatementBuilder();
            answerIdsSql.append("ra.surveyQuestionAnswerId").in(answerIds);
            answersOr.add(answerIdsSql);
        }
        if (includeOtherAnswers) {
            answersOr.addSimpleFragment("ra.surveyQuestionAnswerId IS NULL AND ra.textAnswer IS NOT NULL");
        }
        if (includeUnanswered) {
            answersOr.addSimpleFragment("ra.surveyQuestionAnswerId IS NULL AND ra.textAnswer IS NULL");
        }
        sql.append("AND").append(answersOr);
        if (begin != null) {
            sql.append(    "AND r.whenTaken").gte(begin);
        }
        if (end != null) {
            sql.append(    "AND r.whenTaken").lte(end);
        }

        List<SurveyResult> retVal = yukonJdbcTemplate.query(sql, surveyResultRowMapper);
        return retVal;
    }


    @PostConstruct
    public void init() {
        dbTemplate = new SimpleTableAccessTemplate<OptOutSurvey>(yukonJdbcTemplate, nextValueHelper);
        dbTemplate.setTableName("optOutSurvey");
        dbTemplate.setFieldMapper(optOutSurveyFieldMapper);
        dbTemplate.setPrimaryKeyField("optOutSurveyId");
        dbTemplate.setPrimaryKeyValidOver(0);
    }

    public static YukonRowMapper<SurveyResult> surveyResultRowMapper = new YukonRowMapper<SurveyResult>() {
        @Override
        public SurveyResult mapRow(YukonResultSet rs)
                throws SQLException {
            SurveyResult retVal = new SurveyResult();
            retVal.setSurveyResultId(rs.getInt("surveyResultId"));
            retVal.setSurveyId(rs.getInt("surveyId"));
            retVal.setWhenTaken(rs.getInstant("whenTaken"));
            retVal.setAccountId(rs.getNullableInt("accountId"));
            retVal.setAccountNumber(rs.getString("accountNumber"));
            int surveyQuestionAnswerId = rs.getInt("surveyQuestionAnswerId");
            if (rs.wasNull()) {
                retVal.setSurveyQuestionAnswerId(null);
            } else {
                retVal.setSurveyQuestionAnswerId(surveyQuestionAnswerId);
            }
            retVal.setAnswerKey(rs.getString("answerKey"));
            retVal.setTextAnswer(rs.getString("textAnswer"));
            retVal.setInventoryId(rs.getInt("inventoryId"));
            
            retVal.setScheduledDate(rs.getInstant("ScheduledDate"));
            retVal.setStartDate(rs.getInstant("StartDate"));
            retVal.setStopDate(rs.getInstant("StopDate"));
            
            return retVal;
        }
    };

    // DI Setters
    @Autowired
    public void setYukonJdbcTemplate(YukonJdbcTemplate yukonJdbcTemplate) {
        this.yukonJdbcTemplate = yukonJdbcTemplate;
    }

    @Autowired
    public void setNextValueHelper(NextValueHelper nextValueHelper) {
        this.nextValueHelper = nextValueHelper;
    }
}
