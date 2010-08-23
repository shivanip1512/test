package com.cannontech.stars.dr.optout.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.common.bulk.filter.RowMapperWithBaseQuery;
import com.cannontech.common.util.ChunkingMappedSqlTemplate;
import com.cannontech.common.util.SqlFragmentGenerator;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.FieldMapper;
import com.cannontech.database.IntegerRowMapper;
import com.cannontech.database.SimpleTableAccessTemplate;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.incrementer.NextValueHelper;
import com.cannontech.stars.dr.optout.dao.OptOutSurveyDao;
import com.cannontech.stars.dr.optout.model.OptOutSurvey;
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
        sql.append("SELECT programId FROM optOutSurveyProgram");
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
                sql.append("SELECT oos.optOutSurveyId, p.programId");
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
                sql.append("SELECT programId, surveyId");
                sql.append("FROM optOutSurvey oos");
                sql.append("JOIN optOutSurveyProgram oosp");
                sql.append(    "ON oos.optOutSurveyId = oosp.optOutSurveyId");
                sql.append("WHERE startDate").lt(now);
                sql.append("AND (stopDate IS NULL");
                sql.append(    "OR stopDate").gt(now).append(")");
                sql.append("AND programId").in(subList);
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
            sql.append("(optOutSurveyId, programId)");
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


    @PostConstruct
    public void init() {
        dbTemplate = new SimpleTableAccessTemplate<OptOutSurvey>(yukonJdbcTemplate, nextValueHelper);
        dbTemplate.withTableName("optOutSurvey");
        dbTemplate.withFieldMapper(optOutSurveyFieldMapper);
        dbTemplate.withPrimaryKeyField("optOutSurveyId");
        dbTemplate.withPrimaryKeyValidOver(0);
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
