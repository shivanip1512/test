package com.cannontech.core.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.core.dao.LmXmlParameterDao;
import com.cannontech.database.data.device.lm.LmXmlParameter;
import com.cannontech.database.data.device.lm.LMGroupXML.XmlType;
import com.cannontech.database.incrementer.NextValueHelper;

public class LmXmlParameterDaoImpl implements LmXmlParameterDao {
    
    private static final String insertSql;
    private static final String updateByParamId;
    private static final String removeSql;
    private static final String selectByGroupIdSql;
    
    private static final ParameterizedRowMapper<LmXmlParameter> rowMapper;
    
    private SimpleJdbcTemplate simpleJdbcTemplate;
    private NextValueHelper nextValueHelper = null;
    
    static{
        insertSql = "INSERT INTO LMGroupXMLParameter " +
                    "( xmlparamId, lmgroupid, parametername, parametervalue ) " +
                    "VALUES (?,?,?,?)";

        updateByParamId = "UPDATE LMGroupXMLParameter SET lmgroupid = ?, parametername = ?, parametervalue = ? WHERE xmlparamId = ?";
        
        removeSql = "DELETE FROM LMGroupXMLParameter ";
        
        selectByGroupIdSql = "SELECT xmlparamId, lmgroupid, parametername, parametervalue  FROM LMGroupXMLParameter" 
                    + " WHERE lmgroupid = ?";
        
        rowMapper = new ParameterizedRowMapper<LmXmlParameter>(){
            public LmXmlParameter mapRow(ResultSet rs, int rowNum) throws SQLException {
                
                LmXmlParameter param = new LmXmlParameter();
                
                param.setXmlParamId(rs.getInt("xmlparamId"));
                param.setLmGroupId(rs.getInt("lmgroupid"));
                param.setParameterName(rs.getString("parametername"));
                param.setParameterValue(rs.getString("parametervalue"));
                
                return param;
            }
        };
    }
    
    @Transactional(readOnly = false)
    public boolean add(LmXmlParameter param) {
        
    	int id = nextValueHelper.getNextValue("LMGroupXMLParameter");
        int rowsAffected = simpleJdbcTemplate.update(insertSql,id,param.getLmGroupId(),param.getParameterName(),param.getParameterValue());
        
        boolean success = (rowsAffected == 1);
        return success;
    }
   
    @Transactional(readOnly = false)
    public boolean update(LmXmlParameter param) {
        
        int rowsAffected = simpleJdbcTemplate.update(updateByParamId,param.getLmGroupId(),param.getParameterName(),param.getParameterValue(), param.getXmlParamId());
        
        boolean success = (rowsAffected == 1);
        return success;
    }
    
    @Transactional(readOnly = true)
    public List<LmXmlParameter> getParametersForGroup(int groupId, XmlType type) {        
        
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
    
    @Autowired
    public void setNextValueHelper(NextValueHelper nextValueHelper) {
        this.nextValueHelper = nextValueHelper;
    }
    
}
