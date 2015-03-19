package com.cannontech.stars.dr.appliance.dao.impl;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.util.ChunkingMappedSqlTemplate;
import com.cannontech.common.util.ChunkingSqlTemplate;
import com.cannontech.common.util.SqlFragmentGenerator;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.stars.dr.appliance.dao.AssignedProgramDao;
import com.cannontech.stars.dr.appliance.dao.AssignedProgramRowMapper;
import com.cannontech.stars.dr.appliance.dao.AssignedProgramRowMapper.SortBy;
import com.cannontech.stars.dr.appliance.model.AssignedProgram;
import com.cannontech.stars.webconfiguration.dao.WebConfigurationDao;
import com.cannontech.stars.webconfiguration.model.WebConfiguration;
import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.collect.Maps;

public class AssignedProgramDaoImpl implements AssignedProgramDao {
    @Autowired private YukonJdbcTemplate jdbcTemplate;
    @Autowired private WebConfigurationDao webConfigurationDao;

    @Override
    public AssignedProgram getById(int assignedProgramId) {
        AssignedProgramRowMapper rowMapper = new AssignedProgramRowMapper();
        final SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append(rowMapper.getBaseQuery());
        sql.append("AND p.programId").eq(assignedProgramId);

        AssignedProgram assignedProgram = jdbcTemplate.queryForObject(sql, rowMapper);
        WebConfiguration webConfiguration = webConfigurationDao.getForAssignedProgram(assignedProgramId);
        assignedProgram.setWebConfiguration(webConfiguration);

        return assignedProgram;
    }

    @Override
    public List<AssignedProgram> getByIds(Collection<Integer> assignedProgramIds) {
        Map<Integer, WebConfiguration> webConfigurations =
            webConfigurationDao.getForAssignedPrograms(assignedProgramIds);

        ChunkingSqlTemplate template = new ChunkingSqlTemplate(jdbcTemplate);

        final AssignedProgramRowMapper rowMapper =
            new AssignedProgramRowMapper(SortBy.PROGRAM_NAME, false, -1, webConfigurations);
        SqlFragmentGenerator<Integer> sqlGenerator = new SqlFragmentGenerator<Integer>() {
            @Override
            public SqlFragmentSource generate(List<Integer> subList) {
                SqlStatementBuilder sql = new SqlStatementBuilder();
                sql.append(rowMapper.getBaseQuery());
                sql.append("AND p.programId IN (").appendList(subList).append(")");
                return sql;
            }
        };

        List<AssignedProgram> retVal = template.query(sqlGenerator, assignedProgramIds, rowMapper);

        return retVal;
    }

    @Override
    public Map<Integer, Integer> getProgramIdsByAssignedProgramIds(Iterable<Integer> assignedProgramIds) {
        ChunkingMappedSqlTemplate template = new ChunkingMappedSqlTemplate(jdbcTemplate);

        SqlFragmentGenerator<Integer> sqlGenerator = new SqlFragmentGenerator<Integer>() {
            @Override
            public SqlFragmentSource generate(List<Integer> subList) {
                SqlStatementBuilder sql = new SqlStatementBuilder();
                sql.append("SELECT deviceId, programId");
                sql.append("FROM lmProgramWebPublishing");
                sql.append("WHERE programId").in(subList);
                return sql;
            }
        };

        Function<Integer, Integer> typeMapper = Functions.identity();
        YukonRowMapper<Map.Entry<Integer, Integer>> rowMapper = new YukonRowMapper<Entry<Integer, Integer>>() {
            @Override
            public Entry<Integer, Integer> mapRow(YukonResultSet rs) throws SQLException {
                Integer programId = rs.getInt("deviceId");
                Integer assignedProgramId = rs.getInt("programId");
                return Maps.immutableEntry(assignedProgramId, programId);
            }
        };

        Map<Integer, Integer> retVal = template.mappedQuery(sqlGenerator, assignedProgramIds, rowMapper, typeMapper);

        return retVal;
    }

    @Override
    public int getHighestProgramOrder(int applianceCategoryId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT MAX(programOrder) FROM lmProgramWebPublishing");
        sql.append("WHERE applianceCategoryId").eq(applianceCategoryId);
        return jdbcTemplate.queryForInt(sql);
    }
}
