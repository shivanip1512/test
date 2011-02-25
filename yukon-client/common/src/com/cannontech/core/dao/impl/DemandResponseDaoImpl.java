package com.cannontech.core.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.PaoUtils;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.util.ChunkingMappedSqlTemplate;
import com.cannontech.common.util.SqlFragmentGenerator;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.DemandResponseDao;
import com.cannontech.database.YukonJdbcTemplate;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.SetMultimap;

public class DemandResponseDaoImpl implements DemandResponseDao {

    private YukonJdbcTemplate yukonJdbcTemplate;
    
    @Override
    public List<YukonPao> getControlAreasAndScenariosForProgram(YukonPao program) {
        
        int programId = program.getPaoIdentifier().getPaoId();

        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT y.PAObjectID, y.Category, y.Type"); 
        sql.append("FROM yukonpaobject y");
        sql.append("WHERE paObjectId IN (");
        sql.append("    SELECT deviceId FROM lmControlAreaProgram WHERE lmProgramDeviceId =");
        sql.appendArgument(programId);
        sql.append("    )");
        sql.append("    OR paObjectId IN (");
        sql.append("    SELECT scenarioId FROM lmControlScenarioProgram WHERE programId=");
        sql.appendArgument(programId);
        sql.append("    )");
        
        List<YukonPao> parentList = new ArrayList<YukonPao>();
        yukonJdbcTemplate.query(sql, new YukonPaoRowMapper(), parentList);
        
        return parentList;
    }

    @Override
    public List<YukonPao> getProgramsForGroup(YukonPao group) {

        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT y.PAObjectID, y.Category, y.Type"); 
        sql.append("FROM yukonpaobject y");
        sql.append("WHERE paObjectId IN (");
        sql.append("    SELECT deviceId ");
        sql.append("    FROM lmProgramDirectGroup ");
        sql.append("    WHERE lmGroupDeviceId").eq(group.getPaoIdentifier().getPaoId());
        sql.append("    )");
        
        List<YukonPao> programList = new ArrayList<YukonPao>();
        yukonJdbcTemplate.query(sql, new YukonPaoRowMapper(), programList);
        
        return programList;
    }
    
    @Override
    public SetMultimap<PaoIdentifier, PaoIdentifier> getProgramToGroupMappingForGroups(
                                                               Collection<PaoIdentifier> groups) {
        
        SqlFragmentGenerator<Integer> sqlGenerator = new SqlFragmentGenerator<Integer>() {
            public SqlFragmentSource generate(List<Integer> subList) {

                SqlStatementBuilder sql = new SqlStatementBuilder();
                sql.append("SELECT deviceId, lmGroupDeviceId, type");
                sql.append("FROM lmProgramDirectGroup");
                sql.append("	JOIN YukonPaobject ON paObjectId = deviceId");
                sql.append("WHERE lmGroupDeviceId").in(subList);
                return sql;
            }
        };
        ParameterizedRowMapper<Map.Entry<Integer, PaoIdentifier>> rowMapper = 
            new ParameterizedRowMapper<Map.Entry<Integer, PaoIdentifier>>() {
            public Entry<Integer, PaoIdentifier> mapRow(ResultSet rs, int rowNum) throws SQLException {
                Integer programId = rs.getInt("deviceId");
                String paoTypeStr = rs.getString("type");
                PaoType paoType = PaoType.getForDbString(paoTypeStr);                
                PaoIdentifier program = new PaoIdentifier(programId, paoType);
                
                Integer groupId = rs.getInt("lmGroupDeviceId");
                return Maps.immutableEntry(groupId, program);
            }
        };
        Function<PaoIdentifier, Integer> typeMapper = PaoUtils.getPaoIdFunction();

        ChunkingMappedSqlTemplate sqlTemplate = new ChunkingMappedSqlTemplate(yukonJdbcTemplate);
        return sqlTemplate.reverseMultimappedQuery(sqlGenerator, 
                                                   Lists.newArrayList(groups), 
                                                   rowMapper, 
                                                   typeMapper);
        
    }
    
    @Override
    public SetMultimap<PaoIdentifier, PaoIdentifier> getControlAreaToProgramMappingForPrograms(
                                                               Collection<PaoIdentifier> programs) {
        
        SqlFragmentGenerator<Integer> sqlGenerator = new SqlFragmentGenerator<Integer>() {
            public SqlFragmentSource generate(List<Integer> subList) {

                SqlStatementBuilder sql = new SqlStatementBuilder();
                sql.append("SELECT deviceId, lmProgramDeviceId");
                sql.append("FROM lmControlAreaProgram");
                sql.append("WHERE lmProgramDeviceId").in(subList);
                return sql;
            }
        };
        ParameterizedRowMapper<Map.Entry<Integer, PaoIdentifier>> rowMapper = 
            new ParameterizedRowMapper<Map.Entry<Integer, PaoIdentifier>>() {
            public Entry<Integer, PaoIdentifier> mapRow(ResultSet rs, int rowNum) throws SQLException {
                Integer controlAreaId = rs.getInt("deviceId");
                PaoIdentifier controlArea = new PaoIdentifier(controlAreaId, PaoType.LM_CONTROL_AREA);
                
                Integer programId = rs.getInt("lmProgramDeviceId");
                
                return Maps.immutableEntry(programId, controlArea);
            }
        };
        Function<PaoIdentifier, Integer> typeMapper = new Function<PaoIdentifier, Integer>() {
           public Integer apply(PaoIdentifier from) {
               return from.getPaoId();
           }; 
        };

        ChunkingMappedSqlTemplate sqlTemplate = new ChunkingMappedSqlTemplate(yukonJdbcTemplate);
        return sqlTemplate.reverseMultimappedQuery(sqlGenerator, Lists.newArrayList(programs), rowMapper, typeMapper);

    }
    
    @Override
    public SetMultimap<PaoIdentifier, PaoIdentifier> getScenarioToProgramMappingForPrograms(
                                                                    Collection<PaoIdentifier> programs) {

        SqlFragmentGenerator<Integer> sqlGenerator = new SqlFragmentGenerator<Integer>() {
            public SqlFragmentSource generate(List<Integer> subList) {

                SqlStatementBuilder sql = new SqlStatementBuilder();
                sql.append("SELECT scenarioId, programId");
                sql.append("FROM lmControlScenarioProgram");
                sql.append("WHERE programId").in(subList);
                return sql;
            }
        };
        ParameterizedRowMapper<Map.Entry<Integer, PaoIdentifier>> rowMapper = 
            new ParameterizedRowMapper<Map.Entry<Integer, PaoIdentifier>>() {
            public Entry<Integer, PaoIdentifier> mapRow(ResultSet rs, int rowNum) throws SQLException {
                Integer scenarioId = rs.getInt("scenarioId");
                PaoIdentifier scenario = new PaoIdentifier(scenarioId, PaoType.LM_CONTROL_AREA);
                
                Integer programId = rs.getInt("programId");
                return Maps.immutableEntry(programId, scenario);
            }
        };
        Function<PaoIdentifier, Integer> typeMapper = new Function<PaoIdentifier, Integer>() {
           public Integer apply(PaoIdentifier from) {
               return from.getPaoId();
           }; 
        };

        ChunkingMappedSqlTemplate sqlTemplate = new ChunkingMappedSqlTemplate(yukonJdbcTemplate);
        return sqlTemplate.reverseMultimappedQuery(sqlGenerator, Lists.newArrayList(programs), rowMapper, typeMapper);
        
    }
    
    public void setYukonJdbcTemplate(YukonJdbcTemplate yukonJdbcTemplate) {
        this.yukonJdbcTemplate = yukonJdbcTemplate;
    }

}
