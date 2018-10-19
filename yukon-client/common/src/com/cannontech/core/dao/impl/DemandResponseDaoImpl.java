package com.cannontech.core.dao.impl;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.PaoUtils;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.util.ChunkingMappedSqlTemplate;
import com.cannontech.common.util.SqlFragmentGenerator;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.DemandResponseDao;
import com.cannontech.database.TypeRowMapper;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.SetMultimap;

public class DemandResponseDaoImpl implements DemandResponseDao {
    @Autowired private YukonJdbcTemplate jdbcTemplate;

    @Override
    public List<PaoIdentifier> getControlAreasAndScenariosForProgram(YukonPao program) {
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

        List<PaoIdentifier> parentList = jdbcTemplate.query(sql, TypeRowMapper.PAO_IDENTIFIER);

        return parentList;
    }

    @Override
    public List<PaoIdentifier> getProgramsForGroup(YukonPao group) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT y.PAObjectID, y.Category, y.Type"); 
        sql.append("FROM yukonpaobject y");
        sql.append("WHERE paObjectId IN (");
        sql.append("    SELECT deviceId ");
        sql.append("    FROM lmProgramDirectGroup ");
        sql.append("    WHERE lmGroupDeviceId").eq(group.getPaoIdentifier().getPaoId());
        sql.append("    )");
        
        List<PaoIdentifier> programList = jdbcTemplate.query(sql, TypeRowMapper.PAO_IDENTIFIER);
        
        return programList;
    }
    
    @Override
    public SetMultimap<PaoIdentifier, PaoIdentifier> getProgramToGroupMappingForGroups(Collection<PaoIdentifier> groups) {
        SqlFragmentGenerator<Integer> sqlGenerator = new SqlFragmentGenerator<Integer>() {
            @Override
            public SqlFragmentSource generate(List<Integer> subList) {
                SqlStatementBuilder sql = new SqlStatementBuilder();
                sql.append("SELECT deviceId, lmGroupDeviceId, type");
                sql.append("FROM lmProgramDirectGroup");
                sql.append("	JOIN YukonPaobject ON paObjectId = deviceId");
                sql.append("WHERE lmGroupDeviceId").in(subList);
                return sql;
            }
        };
        YukonRowMapper<Map.Entry<Integer, PaoIdentifier>> rowMapper =
            new YukonRowMapper<Map.Entry<Integer, PaoIdentifier>>() {
            @Override
            public Entry<Integer, PaoIdentifier> mapRow(YukonResultSet rs) throws SQLException {
                PaoIdentifier program = rs.getPaoIdentifier("deviceId",  "type");

                Integer groupId = rs.getInt("lmGroupDeviceId");
                return Maps.immutableEntry(groupId, program);
            }
        };

        ChunkingMappedSqlTemplate sqlTemplate = new ChunkingMappedSqlTemplate(jdbcTemplate);
        return sqlTemplate.reverseMultimappedQuery(sqlGenerator, Lists.newArrayList(groups), rowMapper,
            PaoUtils.getPaoIdFunction());
    }
    
    @Override
    public Multimap<PaoIdentifier, PaoIdentifier> getGroupsToPrograms(List<LiteYukonPAObject> groups) {
        SetMultimap<PaoIdentifier, PaoIdentifier> programToGroupMap = getProgramToGroupMappingForGroups(
            groups.stream().map(LiteYukonPAObject::getPaoIdentifier).collect(Collectors.toList()));
        Multimap<PaoIdentifier, PaoIdentifier> groupsToPrograms =
            Multimaps.invertFrom(programToGroupMap, ArrayListMultimap.<PaoIdentifier, PaoIdentifier> create());
        return groupsToPrograms;
    }

    @Override
    public Multimap<PaoIdentifier, PaoIdentifier> getProgramsToAreas(Collection<PaoIdentifier> programs) {
        SetMultimap<PaoIdentifier, PaoIdentifier> areasToProgram = getControlAreaToProgramMappingForPrograms(programs);
        Multimap<PaoIdentifier, PaoIdentifier> programsToAreas =
            Multimaps.invertFrom(areasToProgram, ArrayListMultimap.<PaoIdentifier, PaoIdentifier> create());
        return programsToAreas;
    }
    
    @Override
    public SetMultimap<PaoIdentifier, PaoIdentifier> getControlAreaToProgramMappingForPrograms(
                                                               Collection<PaoIdentifier> programs) {
        SqlFragmentGenerator<Integer> sqlGenerator = new SqlFragmentGenerator<Integer>() {
            @Override
            public SqlFragmentSource generate(List<Integer> subList) {

                SqlStatementBuilder sql = new SqlStatementBuilder();
                sql.append("SELECT deviceId, lmProgramDeviceId");
                sql.append("FROM lmControlAreaProgram");
                sql.append("WHERE lmProgramDeviceId").in(subList);
                return sql;
            }
        };
        YukonRowMapper<Map.Entry<Integer, PaoIdentifier>> rowMapper =
            new YukonRowMapper<Map.Entry<Integer, PaoIdentifier>>() {
                @Override
                public Entry<Integer, PaoIdentifier> mapRow(YukonResultSet rs) throws SQLException {
                    Integer controlAreaId = rs.getInt("deviceId");
                    PaoIdentifier controlArea = new PaoIdentifier(controlAreaId, PaoType.LM_CONTROL_AREA);

                    Integer programId = rs.getInt("lmProgramDeviceId");

                    return Maps.immutableEntry(programId, controlArea);
                }
            };

        ChunkingMappedSqlTemplate sqlTemplate = new ChunkingMappedSqlTemplate(jdbcTemplate);
        return sqlTemplate.reverseMultimappedQuery(sqlGenerator, Lists.newArrayList(programs), rowMapper,
            PaoUtils.getPaoIdFunction());
    }
    
    @Override
    public SetMultimap<PaoIdentifier, PaoIdentifier> getScenarioToProgramMappingForPrograms(
                                                                    Collection<PaoIdentifier> programs) {
        SqlFragmentGenerator<Integer> sqlGenerator = new SqlFragmentGenerator<Integer>() {
            @Override
            public SqlFragmentSource generate(List<Integer> subList) {
                SqlStatementBuilder sql = new SqlStatementBuilder();
                sql.append("SELECT scenarioId, programId");
                sql.append("FROM lmControlScenarioProgram");
                sql.append("WHERE programId").in(subList);
                return sql;
            }
        };
        YukonRowMapper<Map.Entry<Integer, PaoIdentifier>> rowMapper =
            new YukonRowMapper<Map.Entry<Integer, PaoIdentifier>>() {
                @Override
                public Entry<Integer, PaoIdentifier> mapRow(YukonResultSet rs) throws SQLException {
                    Integer scenarioId = rs.getInt("scenarioId");
                    PaoIdentifier scenario = new PaoIdentifier(scenarioId, PaoType.LM_CONTROL_AREA);

                    Integer programId = rs.getInt("programId");
                    return Maps.immutableEntry(programId, scenario);
                }
            };

        ChunkingMappedSqlTemplate sqlTemplate = new ChunkingMappedSqlTemplate(jdbcTemplate);
        return sqlTemplate.reverseMultimappedQuery(sqlGenerator, Lists.newArrayList(programs), rowMapper,
            PaoUtils.getPaoIdFunction());
    }
}
