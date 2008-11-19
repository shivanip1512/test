package com.cannontech.stars.dr.loadgroup.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.database.IntegerRowMapper;
import com.cannontech.database.data.pao.DeviceClasses;
import com.cannontech.database.data.pao.PAOGroups;
import com.cannontech.stars.dr.loadgroup.dao.LoadGroupDao;
import com.cannontech.stars.dr.loadgroup.model.LoadGroup;

public class LoadGroupDaoImpl implements LoadGroupDao {
    
    private SimpleJdbcTemplate simpleJdbcTemplate;
    
    /** These strings are to help with the row mapper.  If you use both of these
     *  strings you will have the right table connections and data returned.
     */
    private final String loadGroupSQLHeader = "SELECT PAO.paobjectId as loadGroupId, PAO.paoName as loadGroupName " + 
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
        }
        
        loadGroup.setProgramIds(getLoadGroupProgramIds(loadGroup));
        return loadGroup;
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
    
        try {
            return simpleJdbcTemplate.query(loadGroupProgramIdsQuery.toString(),
                                            new IntegerRowMapper(),
                                            loadGroup.getLoadGroupId());
        } catch (EmptyResultDataAccessException ex) {
            return Collections.emptyList();
        }
    }
    
    
    // rowMappers
    private ParameterizedRowMapper<LoadGroup> loadGroupDatabaseResultRowMapper() {
        final ParameterizedRowMapper<LoadGroup> mapper = new ParameterizedRowMapper<LoadGroup>() {
            @Override
            public LoadGroup mapRow(ResultSet rs, int rowNum) throws SQLException {
                
                int loadGroupId = rs.getInt("loadGroupId"); 
                String loadGroupName = rs.getString("loadGroupName");
                
                return new LoadGroup(loadGroupId, loadGroupName, null);
            }
        };
        return mapper;
    }

    @Autowired
    public void setSimpleJdbcTemplate(SimpleJdbcTemplate simpleJdbcTemplate) {
        this.simpleJdbcTemplate = simpleJdbcTemplate;
    }

}