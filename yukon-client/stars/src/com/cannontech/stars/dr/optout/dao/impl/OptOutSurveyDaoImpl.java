package com.cannontech.stars.dr.optout.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.common.bulk.filter.RowMapperWithBaseQuery;
import com.cannontech.common.util.ChunkingMappedSqlTemplate;
import com.cannontech.common.util.SqlFragmentGenerator;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.IntegerRowMapper;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.incrementer.NextValueHelper;
import com.cannontech.stars.dr.optout.dao.OptOutSurveyDao;
import com.cannontech.stars.dr.optout.model.OptOutSurvey;
import com.google.common.base.Function;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;

public class OptOutSurveyDaoImpl implements OptOutSurveyDao {
    private YukonJdbcTemplate yukonJdbcTemplate;
    private NextValueHelper nextValueHelper;

    private ParameterizedRowMapper<Map.Entry<Integer, Integer>> optOutGroupRowMapper =
        new ParameterizedRowMapper<Map.Entry<Integer, Integer>>(){
        @Override
        public Entry<Integer, Integer> mapRow(ResultSet rs, int rowNum)
                throws SQLException {
            int optOutSurveyId = rs.getInt("optOutSurveyId");
            int loginGroupId = rs.getInt("loginGroupId");
            return Maps.immutableEntry(optOutSurveyId, loginGroupId);
        }};

    private final static RowMapperWithBaseQuery<OptOutSurvey> optOutSurveyRowMapper =
        new OptOutSurveyRowMapper();

        @Override
        @Transactional(readOnly = true)
        public OptOutSurvey getOptOutSurveyById(int optOutSurveyId) {
            SqlStatementBuilder sql = new SqlStatementBuilder();
            sql.append(optOutSurveyRowMapper.getBaseQuery());
            sql.append("WHERE optOutSurveyId").eq(optOutSurveyId);
            OptOutSurvey retVal =
                yukonJdbcTemplate.queryForObject(sql, optOutSurveyRowMapper);

            sql = new SqlStatementBuilder();
            sql.append("SELECT groupId FROM optOutSurveyGroup");
            sql.append("WHERE optOutSurveyId").eq(optOutSurveyId);
            List<Integer> loginGroupIds =
                yukonJdbcTemplate.query(sql, new IntegerRowMapper());
            retVal.setLoginGroupIds(loginGroupIds);

            return retVal;
        }

    @Override
    public Multimap<OptOutSurvey, Integer> getLoginGroupsForOptOutSurveys(
            Iterable<OptOutSurvey> optOutSurveys) {
        SqlFragmentGenerator<Integer> sqlGenerator = new SqlFragmentGenerator<Integer>() {
            @Override
            public SqlFragmentSource generate(List<Integer> optOutSurveyIds) {
                SqlStatementBuilder sql = new SqlStatementBuilder();
                sql.append("SELECT oos.optOutSurveyId, g.groupId AS loginGroupId");
                sql.append("FROM optOutSurveyGroup g");
                sql.append("JOIN optOutSurvey oos ON oos.optOutSurveyId = g.optOutSurveyId");
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
            template.multimappedQuery(sqlGenerator, optOutSurveys, optOutGroupRowMapper, typeMapper);
        return retVal;
    }

    @Override
    @Transactional
    public void saveOptOutSurvey(OptOutSurvey optOutSurvey) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        int optOutSurveyId = optOutSurvey.getOptOutSurveyId();
        if (optOutSurveyId == 0) {
            optOutSurveyId = nextValueHelper.getNextValue("optOutSurvey");

            sql.append("INSERT INTO optOutSurvey (");
            sql.append(    "optOutSurveyId, surveyId, startDate, stopDate");
            sql.append(")").values(optOutSurveyId,
                                   optOutSurvey.getSurveyId(),
                                   optOutSurvey.getStartDate(),
                                   optOutSurvey.getStopDate());
        } else {
            sql.append("UPDATE optOutSurvey");
            sql.append(    "SET surveyId = ").appendArgument(optOutSurvey.getSurveyId());
            sql.append(    ", startDate = ").appendArgument(optOutSurvey.getStartDate());
            sql.append(    ", stopDate = ").appendArgument(optOutSurvey.getStopDate());
            sql.append("WHERE optOutSurveyId").eq(optOutSurveyId);
        }
        yukonJdbcTemplate.update(sql);

        if (optOutSurveyId != 0) {
            sql = new SqlStatementBuilder();
            sql.append("DELETE FROM optOutSurveyGroup");
            sql.append("WHERE optOutSurveyId").eq(optOutSurveyId);
            yukonJdbcTemplate.update(sql);
        }

        for (int loginGroupId : optOutSurvey.getLoginGroupIds()) {
            sql = new SqlStatementBuilder();
            sql.append("INSERT INTO optOutSurveyGroup");
            sql.append("(optOutSurveyId, groupId)");
            sql.values(optOutSurveyId, loginGroupId);
            yukonJdbcTemplate.update(sql);
        }

        optOutSurvey.setSurveyId(optOutSurveyId);
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


    @Autowired
    public void setYukonJdbcTemplate(YukonJdbcTemplate yukonJdbcTemplate) {
        this.yukonJdbcTemplate = yukonJdbcTemplate;
    }

    @Autowired
    public void setNextValueHelper(NextValueHelper nextValueHelper) {
        this.nextValueHelper = nextValueHelper;
    }
}
