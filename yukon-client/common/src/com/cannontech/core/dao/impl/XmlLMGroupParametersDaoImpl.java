package com.cannontech.core.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.core.dao.XmlLMGroupParametersDao;
import com.cannontech.database.data.device.lm.LMxmlParameter;

public class XmlLMGroupParametersDaoImpl implements XmlLMGroupParametersDao {
    
    private static final String insertSql;
    private static final String removeSql;
    private static final String selectByGroupIdSql;
    
    private static final ParameterizedRowMapper<LMxmlParameter> rowMapper;
    private SimpleJdbcTemplate simpleJdbcTemplate;
    
    static{
        insertSql = "INSERT INTO XmlLMGroupParameters " +
                    "( lmgroupid, parametername, parametervalue ) " +
                    "VALUES (?,?,?)";

        removeSql = "DELETE FROM XmlLMGroupParameters ";
        
        selectByGroupIdSql = "SELECT lmgroupid, parametername, parametervalue  FROM XmlLMGroupParameters" 
                    + " WHERE lmgroupid = ?";
        
        rowMapper = new ParameterizedRowMapper<LMxmlParameter>(){
            public LMxmlParameter mapRow(ResultSet rs, int rowNum) throws SQLException {
                
                LMxmlParameter param = new LMxmlParameter();
                
                param.setLmGroupId(rs.getInt("lmgroupid"));
                param.setParameterName(rs.getString("parametername"));
                param.setParameterValue(rs.getString("parametervalue"));
                
                return param;
            }
        };
    }
    
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public boolean add(LMxmlParameter param) {
        
        int rowsAffected = simpleJdbcTemplate.update(insertSql,param.getLmGroupId(),param.getParameterName(),param.getParameterValue());
        
        boolean success = (rowsAffected == 1);
        return success;
    }
    
    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public List<LMxmlParameter> getParametersForGroup(int groupId) {        
        
        List<LMxmlParameter> paramList = simpleJdbcTemplate.query(selectByGroupIdSql, rowMapper, groupId );

        return paramList;
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public boolean removeAllByGroupId(int groupId) {
        String sql = removeSql + " WHERE lmgroupid = ?";
        int rowsAffected = simpleJdbcTemplate.update(sql, groupId);
        
        boolean success = rowsAffected >= 1;
        return success;
    }

    public void setSimpleJdbcTemplate(SimpleJdbcTemplate simpleJdbcTemplate) {
        this.simpleJdbcTemplate = simpleJdbcTemplate;
    }
    
}
