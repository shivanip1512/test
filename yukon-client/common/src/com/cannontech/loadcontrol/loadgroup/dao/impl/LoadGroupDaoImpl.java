package com.cannontech.loadcontrol.loadgroup.dao.impl;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.common.pao.PaoCategory;
import com.cannontech.common.pao.PaoClass;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.PaoUtils;
import com.cannontech.common.util.ChunkingMappedSqlTemplate;
import com.cannontech.common.util.ChunkingSqlTemplate;
import com.cannontech.common.util.SqlFragmentGenerator;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.database.RowMapper;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.database.db.macro.MacroTypes;
import com.cannontech.loadcontrol.loadgroup.dao.LoadGroupDao;
import com.cannontech.loadcontrol.loadgroup.model.LoadGroup;
import com.google.common.base.Function;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.SetMultimap;

public class LoadGroupDaoImpl implements LoadGroupDao {
    @Autowired private YukonJdbcTemplate jdbcTemplate;
    private LoadingCache<String, LoadGroup> cachedValue=CacheBuilder.newBuilder().expireAfterAccess(5, TimeUnit.MINUTES).build(new CacheLoader<String, LoadGroup>(){

        @Override
        public LoadGroup load(String arg0) throws Exception {
            return null;
        }
        
    });

    @Override
    @Transactional(readOnly = true)
    public LoadGroup getById(int loadGroupId){
        // Get all the load group information for the supplied load group name except for the program ids.
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT PAO.paobjectId AS loadGroupId, PAO.paoName AS loadGroupName, PAO.type");
        sql.append("FROM YukonPAObject PAO");
        sql.append("WHERE PAO.paobjectId").eq(loadGroupId);
        sql.append("AND PAO.paoClass").eq(PaoClass.GROUP);
        sql.append("AND PAO.category").eq(PaoCategory.DEVICE);
        
        LoadGroup loadGroup = null;
        try {
            loadGroup = jdbcTemplate.queryForObject(sql, loadGroupRowMapper);
        } catch (EmptyResultDataAccessException ex) {
            throw new NotFoundException("The load group id supplied does not exist.");
        }

        loadGroup.setProgramIds(getLoadGroupProgramIds(loadGroup));
        return loadGroup;
    }

    @Override
    public List<LoadGroup> getByIds(Iterable<Integer> loadGroupIds) {
        ChunkingSqlTemplate template = new ChunkingSqlTemplate(jdbcTemplate);
        SqlFragmentGenerator<Integer> sqlGenerator = new SqlFragmentGenerator<Integer>() {
            @Override
            public SqlFragmentSource generate(List<Integer> subList) {
                SqlStatementBuilder sql = new SqlStatementBuilder();
                sql.append("SELECT PAO.paobjectId AS loadGroupId, PAO.paoName AS loadGroupName, PAO.type");
                sql.append("FROM YukonPAObject PAO");
                sql.append("WHERE PAO.paobjectId").in(subList);
                sql.append("AND PAO.paoClass").eq(PaoClass.GROUP);
                sql.append("AND PAO.category").eq(PaoCategory.DEVICE);
                return sql;
            }};
        List<LoadGroup> loadGroups = template.query(sqlGenerator, loadGroupIds, loadGroupRowMapper);
        return loadGroups;
    }

    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public LoadGroup getByLoadGroupName(String loadGroupName) {
        LoadGroup cachedLoadGroup = cachedValue.getIfPresent(loadGroupName);

        if (cachedLoadGroup != null) {
            return cachedLoadGroup;
        } else {
            // Gets all the load group information for the supplied load group
            // name except for the program ids.
            System.out.println("getbyLoadgroup name" + loadGroupName);
            SqlStatementBuilder sql = new SqlStatementBuilder();
            sql.append("SELECT PAO.paobjectId AS loadGroupId, PAO.paoName AS loadGroupName, PAO.type");
            sql.append("FROM YukonPAObject PAO");
            sql.append("WHERE PAO.paoClass").eq(PaoClass.GROUP);
            sql.append("AND PAO.category").eq(PaoCategory.DEVICE);
            sql.append("AND PAO.paoName").eq(loadGroupName);

            LoadGroup loadGroup = null;
            try {
                loadGroup = jdbcTemplate.queryForObject(sql, loadGroupRowMapper);
            } catch (EmptyResultDataAccessException ex) {
                throw new NotFoundException("The load group name supplied does not exist.");
            } catch (IncorrectResultSizeDataAccessException ex) {
                throw new IllegalArgumentException("The load group name supplied returned too many results");
            }

            loadGroup.setProgramIds(getLoadGroupProgramIds(loadGroup));
            cachedValue.put(loadGroupName, loadGroup);
            return loadGroup;
        }
    }
    
    /**
     * Checks to see if the load group supplied load group is be used in an active enrollment
     */
    @Override
    public boolean isLoadGroupInUse(int loadGroupId){
        boolean isUsed = false;

        // Get all the load group information for the supplied load group name except for the program ids.
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT COUNT(*)");
        sql.append("FROM LMHardwareControlGroup LMHCG");
        sql.append("WHERE LMHCG.LMGroupId").eq(loadGroupId);
        sql.append("AND LMHCG.Type").eq(1);
        sql.append("AND LMHCG.GroupEnrollStop IS NULL");
        
        int enrollmentCount = jdbcTemplate.queryForInt(sql);

        if (enrollmentCount > 0) {
            isUsed = true;
        }

        return isUsed;
    }
    
    @Override
    public List<LoadGroup> getByStarsProgramId(int programId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT PAO.paobjectId AS loadGroupId, PAO.paoName AS loadGroupName, PAO.type");
        sql.append("FROM YukonPAObject PAO");
        sql.append("JOIN LMProgramDirectGroup LMPDG ON LMPDG.LMGroupDeviceId = PAO.PAObjectId");
        sql.append("JOIN LMProgramWebPublishing LMPWP ON LMPDG.DeviceId = LMPWP.DeviceId");
        sql.append("WHERE LMPWP.ProgramId").eq(programId);
        
        List<LoadGroup> loadGroupList = jdbcTemplate.query(sql, loadGroupRowMapper);
        return loadGroupList;
    }

    /**
     * Retrieves all the program ids that are associated with the loadGroup provided.
     */
    private List<Integer> getLoadGroupProgramIds(LoadGroup loadGroup){
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT LMPWP.programId");
        sql.append("FROM LMProgramDirectGroup LMPDG, LMProgramWebPublishing LMPWP"); 
        sql.append("WHERE LMPDG.LMGroupDeviceId").eq(loadGroup.getLoadGroupId());
        sql.append("AND LMPDG.deviceId = LMPWP.deviceId");
    
        return jdbcTemplate.query(sql, RowMapper.INTEGER);
    }
    
    @Override
    public List<PaoIdentifier> getParentMacroGroups(PaoIdentifier group) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT gm.OwnerID PAObjectID, pao.type Type");
        sql.append("FROM GenericMacro gm");
        sql.append("JOIN YukonPAObject pao ON pao.PAObjectID = gm.OwnerID ");
        sql.append("WHERE gm.MacroType").eq(MacroTypes.GROUP);
        sql.append(    "AND gm.ChildID").eq(group.getPaoIdentifier().getPaoId());

        return jdbcTemplate.query(sql, RowMapper.PAO_IDENTIFIER);
    }
    
    @Override
    public SetMultimap<PaoIdentifier, PaoIdentifier> getMacroGroupToGroupMappings(Collection<PaoIdentifier> groups) {
        SqlFragmentGenerator<Integer> sqlGenerator = new SqlFragmentGenerator<Integer>() {
            @Override
            public SqlFragmentSource generate(List<Integer> subList) {
                SqlStatementBuilder sql = new SqlStatementBuilder();
                sql.append("SELECT OwnerID, ChildID");
                sql.append("FROM GenericMacro");
                sql.append("WHERE MacroType").eq(MacroTypes.GROUP);
                sql.append(    "AND ChildID").in(subList);
                return sql;
            }
        };
        YukonRowMapper<Map.Entry<Integer, PaoIdentifier>> rowMapper =
            new YukonRowMapper<Map.Entry<Integer, PaoIdentifier>>() {
                @Override
                public Entry<Integer, PaoIdentifier> mapRow(YukonResultSet rs) throws SQLException {
                    Integer macroGroupId = rs.getInt("OwnerID");
                    PaoIdentifier macroGroup = new PaoIdentifier(macroGroupId, PaoType.MACRO_GROUP);

                    Integer groupId = rs.getInt("ChildID");

                    return Maps.immutableEntry(groupId, macroGroup);
                }
            };
        Function<PaoIdentifier, Integer> typeMapper = PaoUtils.getPaoIdFunction();

        ChunkingMappedSqlTemplate sqlTemplate = new ChunkingMappedSqlTemplate(jdbcTemplate);
        return sqlTemplate.reverseMultimappedQuery(sqlGenerator, 
                                                   Lists.newArrayList(groups), 
                                                   rowMapper, 
                                                   typeMapper);
    }
    
    @Override
    public List<LoadGroup> getByProgramId(int programId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT PAO.paobjectId AS loadGroupId, PAO.paoName AS loadGroupName, PAO.type");
        sql.append("FROM YukonPAObject PAO");
        sql.append("WHERE paobjectId IN (");
        sql.append("SELECT lmGroupDeviceId FROM lmProgramDirectGroup");
        sql.append("WHERE deviceId").eq(programId).append(")");

        return jdbcTemplate.query(sql, loadGroupRowMapper);
    }

    private final YukonRowMapper<LoadGroup> loadGroupRowMapper = new YukonRowMapper<LoadGroup>() {
        @Override
        public LoadGroup mapRow(YukonResultSet rs) throws SQLException {
            String loadGroupName = rs.getString("loadGroupName");
            PaoIdentifier paoIdentifier = rs.getPaoIdentifier("loadGroupId", "type");
            return new LoadGroup(paoIdentifier, loadGroupName, null);
        }
    };
}
