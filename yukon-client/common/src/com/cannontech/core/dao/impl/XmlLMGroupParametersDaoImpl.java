package com.cannontech.core.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.core.dao.XmlLMGroupParametersDao;
import com.cannontech.database.data.device.lm.LmXmlParameter;

public class XmlLMGroupParametersDaoImpl implements XmlLMGroupParametersDao {
    
    private static final String insertSql;
    private static final String removeSql;
    private static final String selectByGroupIdSql;
    
    private static final ParameterizedRowMapper<LmXmlParameter> rowMapper;
    private SimpleJdbcTemplate simpleJdbcTemplate;
    
    static{
        insertSql = "INSERT INTO XmlLMGroupParameters " +
                    "( lmgroupid, parametername, parametervalue ) " +
                    "VALUES (?,?,?)";

        removeSql = "DELETE FROM XmlLMGroupParameters ";
        
        selectByGroupIdSql = "SELECT lmgroupid, parametername, parametervalue  FROM XmlLMGroupParameters" 
                    + " WHERE lmgroupid = ?";
        
        rowMapper = new ParameterizedRowMapper<LmXmlParameter>(){
            public LmXmlParameter mapRow(ResultSet rs, int rowNum) throws SQLException {
                
                LmXmlParameter param = new LmXmlParameter();
                
                param.setLmGroupId(rs.getInt("lmgroupid"));
                param.setParameterName(rs.getString("parametername"));
                param.setParameterValue(rs.getString("parametervalue"));
                
                return param;
            }
        };
    }
    
    @Transactional(readOnly = false)
    public boolean add(LmXmlParameter param) {
        
        int rowsAffected = simpleJdbcTemplate.update(insertSql,param.getLmGroupId(),param.getParameterName(),param.getParameterValue());
        
        boolean success = (rowsAffected == 1);
        return success;
    }
    
    @Transactional(readOnly = true)
    public List<LmXmlParameter> getParametersForGroup(int groupId) {        
        
        List<LmXmlParameter> paramList = simpleJdbcTemplate.query(selectByGroupIdSql, rowMapper, groupId );

        return paramList;
    }

    @Transactional(readOnly = false)
    public boolean removeAllByGroupId(int groupId) {
        String sql = removeSql + " WHERE lmgroupid = ?";
        int rowsAffected = simpleJdbcTemplate.update(sql, groupId);
        
        boolean success = rowsAffected >= 1;
        return success;
    }

    @Autowired
    public void setSimpleJdbcTemplate(SimpleJdbcTemplate simpleJdbcTemplate) {
        this.simpleJdbcTemplate = simpleJdbcTemplate;
    }
    
}
