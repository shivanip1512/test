package com.cannontech.loadcontrol.loadgroup.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.PaoUtils;
import com.cannontech.common.util.ChunkingMappedSqlTemplate;
import com.cannontech.common.util.SqlFragmentGenerator;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.impl.YukonPaoRowMapper;
import com.cannontech.database.IntegerRowMapper;
import com.cannontech.database.data.pao.DeviceClasses;
import com.cannontech.database.data.pao.PAOGroups;
import com.cannontech.database.db.macro.MacroTypes;
import com.cannontech.loadcontrol.loadgroup.dao.LoadGroupDao;
import com.cannontech.loadcontrol.loadgroup.model.LoadGroup;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.SetMultimap;

public class LoadGroupDaoImpl implements LoadGroupDao {
    
    private SimpleJdbcTemplate simpleJdbcTemplate;
    
    /** These strings are to help with the row mapper.  If you use both of these
     *  strings you will have the right table connections and data returned.
     */
    private final String loadGroupSQLHeader = "SELECT PAO.paobjectId as loadGroupId, PAO.paoName as loadGroupName, PAO.type Type " + 
                                              "FROM YukonPAObject PAO ";
   
    @Override
    @Transactional(readOnly = true)
    public LoadGroup getById(int loadGroupId){

        LoadGroup loadGroup = null;

        /*
         * This query gets all the load group information for the supplied load group name, 
         * except for the program ids.
         */
        final SqlStatementBuilder loadGroupQuery = new SqlStatementBuilder();
        loadGroupQuery.append(loadGroupSQLHeader);
        loadGroupQuery.append("WHERE PAO.paobjectId = ?");
        loadGroupQuery.append("AND PAO.paoClass = ?");
        loadGroupQuery.append("AND PAO.category = ?");
        
        try {
            loadGroup = simpleJdbcTemplate.queryForObject(loadGroupQuery.toString(),
                                                          loadGroupDatabaseResultRowMapper(),
                                                          loadGroupId,
                                                          DeviceClasses.STRING_CLASS_GROUP,
                                                          PAOGroups.STRING_CAT_DEVICE);
        } catch (EmptyResultDataAccessException ex) {
            throw new NotFoundException("The load group id supplied does not exist.");
        }

        loadGroup.setProgramIds(getLoadGroupProgramIds(loadGroup));
        return loadGroup;

    }

    
    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public LoadGroup getByLoadGroupName(String loadGroupName){

        LoadGroup loadGroup = null;

        /*
         * This query gets all the load group information for the supplied load group name, 
         * except for the program ids.
         */
        final SqlStatementBuilder loadGroupQuery = new SqlStatementBuilder();
        loadGroupQuery.append(loadGroupSQLHeader);
        loadGroupQuery.append("WHERE PAO.paoClass = 'GROUP'");
        loadGroupQuery.append("AND PAO.category = 'DEVICE'");
        loadGroupQuery.append("AND PAO.paoName = ?");
        
        try {
            loadGroup = simpleJdbcTemplate.queryForObject(loadGroupQuery.toString(),
                                                          loadGroupDatabaseResultRowMapper(),
                                                          loadGroupName);
        } catch (EmptyResultDataAccessException ex) {
            throw new NotFoundException("The load group name supplied does not exist.");

        } catch(IncorrectResultSizeDataAccessException ex){
            throw new IllegalArgumentException("The load group name supplied returned too many results");
        }
        
        loadGroup.setProgramIds(getLoadGroupProgramIds(loadGroup));
        return loadGroup;
    }
    
    /*
     * This method checks to see if the load group supplied load group is be used in an active enrollment
     */
    public boolean isLoadGroupInUse(int loadGroupId){
        boolean isUsed = false;

        /*
         * This query gets all the load group information for the supplied load group name, 
         * except for the program ids.
         */
        final SqlStatementBuilder loadGroupInUseQuery = new SqlStatementBuilder();
        loadGroupInUseQuery.append("SELECT COUNT(*) ");
        loadGroupInUseQuery.append("FROM LMHardwareControlGroup LMHCG");
        loadGroupInUseQuery.append("WHERE LMHCG.LMGroupId = ?");
        loadGroupInUseQuery.append("AND LMHCG.Type = ?");
        loadGroupInUseQuery.append("AND LMHCG.GroupEnrollStop IS NULL");
        
        int enrollmentCount = simpleJdbcTemplate.queryForInt(loadGroupInUseQuery.toString(),
                                                             loadGroupId,
                                                             1);
                                                             
        if(enrollmentCount > 0)
            isUsed = true;
            
        return isUsed;
    }
    
    public List<LoadGroup> getByStarsProgramId(int programId) {
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append(loadGroupSQLHeader);
        sql.append("JOIN LMProgramDirectGroup LMPDG ON LMPDG.LMGroupDeviceId = PAO.PAObjectId");
        sql.append("JOIN LMProgramWebPublishing LMPWP ON LMPDG.DeviceId = LMPWP.DeviceId");
        sql.append("WHERE LMPWP.ProgramId = ?");
        
        List<LoadGroup> loadGroupList = simpleJdbcTemplate.query(sql.getSql(), loadGroupDatabaseResultRowMapper(), programId);
        return loadGroupList;
    }
    
    /*
     * This method retrieves all the program ids that are associated with the 
     * loadGroup provided.
     */
    private List<Integer> getLoadGroupProgramIds(LoadGroup loadGroup){
    
        final SqlStatementBuilder loadGroupProgramIdsQuery = new SqlStatementBuilder();
        loadGroupProgramIdsQuery.append("SELECT LMPWP.programId");
        loadGroupProgramIdsQuery.append("FROM LMProgramDirectGroup LMPDG, LMProgramWebPublishing LMPWP"); 
        loadGroupProgramIdsQuery.append("WHERE LMPDG.LMGroupDeviceId = ?");
        loadGroupProgramIdsQuery.append("AND LMPDG.deviceId = LMPWP.deviceId");
    
        return simpleJdbcTemplate.query(loadGroupProgramIdsQuery.toString(),
                                        new IntegerRowMapper(),
                                        loadGroup.getLoadGroupId());
    }
    
    public List<PaoIdentifier> getParentMacroGroups(PaoIdentifier group) {

        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT gm.OwnerID PAObjectID, pao.type Type");
        sql.append("FROM GenericMacro gm");
        sql.append("JOIN YukonPAObject pao ON pao.PAObjectID = gm.OwnerID ");
        sql.append("WHERE gm.MacroType").eq(MacroTypes.GROUP);
        sql.append("    AND gm.ChildID").eq(group.getPaoIdentifier().getPaoId());
        
        return simpleJdbcTemplate.query(sql.getSql(), new YukonPaoRowMapper(), sql.getArguments());
    }
    
    @Override
    public SetMultimap<PaoIdentifier, PaoIdentifier> getMacroGroupToGroupMappings(Collection<PaoIdentifier> groups) {
        
        SqlFragmentGenerator<Integer> sqlGenerator = new SqlFragmentGenerator<Integer>() {
            public SqlFragmentSource generate(List<Integer> subList) {

                SqlStatementBuilder sql = new SqlStatementBuilder();
                sql.append("SELECT OwnerID, ChildID");
                sql.append("FROM GenericMacro");
                sql.append("WHERE MacroType").eq(MacroTypes.GROUP);
                sql.append("    AND ChildID").in(subList);
                return sql;
            }
        };
        ParameterizedRowMapper<Map.Entry<Integer, PaoIdentifier>> rowMapper = 
            new ParameterizedRowMapper<Map.Entry<Integer, PaoIdentifier>>() {
            public Entry<Integer, PaoIdentifier> mapRow(ResultSet rs, int rowNum) throws SQLException{
                Integer macroGroupId = rs.getInt("OwnerID");
                PaoIdentifier macroGroup = new PaoIdentifier(macroGroupId, PaoType.MACRO_GROUP);

                Integer groupId = rs.getInt("ChildID");

                return Maps.immutableEntry(groupId, macroGroup);
            }
        };
        Function<PaoIdentifier, Integer> typeMapper = PaoUtils.getPaoIdFunction();

        ChunkingMappedSqlTemplate sqlTemplate = new ChunkingMappedSqlTemplate(simpleJdbcTemplate);
        return sqlTemplate.reverseMultimappedQuery(sqlGenerator, 
                                                   Lists.newArrayList(groups), 
                                                   rowMapper, 
                                                   typeMapper);
                
    }
    
    @Override
    public List<LoadGroup> getByProgramId(int programId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append(loadGroupSQLHeader);
        sql.append("WHERE paobjectId IN (");
        sql.append("SELECT lmGroupDeviceId FROM lmProgramDirectGroup");
        sql.append("WHERE deviceId").eq(programId).append(")");

        return simpleJdbcTemplate.query(sql.toString(),
                                          loadGroupDatabaseResultRowMapper(),
                                          programId);
    }
    
    // rowMappers
    private ParameterizedRowMapper<LoadGroup> loadGroupDatabaseResultRowMapper() {
        final ParameterizedRowMapper<LoadGroup> mapper = new ParameterizedRowMapper<LoadGroup>() {
            @Override
            public LoadGroup mapRow(ResultSet rs, int rowNum) throws SQLException {
                
                int loadGroupId = rs.getInt("loadGroupId"); 
                String loadGroupName = rs.getString("loadGroupName");
                String typeStr = rs.getString("type");
                int deviceTypeId = PAOGroups.getPAOType("DEVICE", typeStr);
                PaoType paoType = PaoType.getForId(deviceTypeId);
                PaoIdentifier paoIdentifier = new PaoIdentifier(loadGroupId, paoType);
                return new LoadGroup(paoIdentifier, loadGroupName, null);
            }
        };
        return mapper;
    }
    
    @Autowired
    public void setSimpleJdbcTemplate(SimpleJdbcTemplate simpleJdbcTemplate) {
        this.simpleJdbcTemplate = simpleJdbcTemplate;
    }
    
}