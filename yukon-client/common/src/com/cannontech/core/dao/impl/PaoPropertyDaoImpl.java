package com.cannontech.core.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;


import com.cannontech.common.model.PaoProperty;
import com.cannontech.core.dao.PaoPropertyDao;

public class PaoPropertyDaoImpl implements PaoPropertyDao {
    private static final String insertSql;
    private static final String removeSql;
    private static final String updateSql;
    private static final String selectAllSql;
    private static final String selectByIdSql;
    
    private static final ParameterizedRowMapper<PaoProperty> rowMapper;
    
    private SimpleJdbcTemplate simpleJdbcTemplate;
    
    static {
        insertSql = "INSERT INTO PaoProperty (PaoID, PropertyName, PropertyValue) VALUES (?,?,?)";
        
        removeSql = "DELETE FROM PaoProperty WHERE PaoID = ?";
        
        updateSql = "UPDATE PaoProperty SET PropertyName = ?," + 
        "PropertyValue = ? WHERE PaoID = ?";
        
        selectAllSql = "SELECT PaoID,PropertyName,PropertyValue FROM PaoProperty";
        
        selectByIdSql = selectAllSql + " WHERE PaoID = ?";
        
        rowMapper = PaoPropertyDaoImpl.createRowMapper();
    }
    
    private static final ParameterizedRowMapper<PaoProperty> createRowMapper() {
        ParameterizedRowMapper<PaoProperty> rowMapper = new ParameterizedRowMapper<PaoProperty>() {
            public PaoProperty mapRow(ResultSet rs, int rowNum) throws SQLException {
                PaoProperty property = new PaoProperty();
                property.setPaoId(rs.getInt("PaoID"));
                property.setPropertyName(rs.getString("PropertyName"));
                property.setPropertyValue(rs.getString("PropertyValue"));

                return property;
            }
        };
        return rowMapper;
    }

    @Override
    public boolean add(PaoProperty property) {
        int rowsAffected = simpleJdbcTemplate.update(insertSql,
                                                     property.getPaoId(),
                                                     property.getPropertyName(),
                                                     property.getPropertyValue());
        boolean result = (rowsAffected == 1);
        
        return result;
    }

    @Override
    public PaoProperty getById(int id) {
        PaoProperty property = simpleJdbcTemplate.queryForObject(selectByIdSql, rowMapper, id);
        
        return property;
    }
    
    @Override
    public PaoProperty getByIdAndName(int id, String name) {
        PaoProperty property = simpleJdbcTemplate.queryForObject(selectByIdSql + " AND PropertyName = ?", rowMapper, id, name);
        
        return property;
    }
    
    @Override
    public boolean remove(PaoProperty property) {
        simpleJdbcTemplate.update(removeSql,property.getPaoId());
        
        return true;
    }
    
    @Override
    public boolean update(PaoProperty property) {
        int rowsAffected = simpleJdbcTemplate.update(updateSql,
                                                     property.getPropertyName(),
                                                     property.getPropertyValue(),
                                                     property.getPaoId());
        boolean result = (rowsAffected == 1);
        
        return result;
    }

    @Autowired
    public void setSimpleJdbcTemplate(final SimpleJdbcTemplate simpleJdbcTemplate) {
        this.simpleJdbcTemplate = simpleJdbcTemplate;
    }
}
