package com.cannontech.core.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;


import com.cannontech.common.model.PaoProperty;
import com.cannontech.common.model.PaoPropertyName;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.core.dao.PaoPropertyDao;

public class PaoPropertyDaoImpl implements PaoPropertyDao {
    
    private static final ParameterizedRowMapper<PaoProperty> rowMapper;
    private SimpleJdbcTemplate simpleJdbcTemplate;
    
    static { 
        rowMapper = PaoPropertyDaoImpl.createRowMapper();
    }
    
    private static final ParameterizedRowMapper<PaoProperty> createRowMapper() {
        ParameterizedRowMapper<PaoProperty> rowMapper = new ParameterizedRowMapper<PaoProperty>() {
            public PaoProperty mapRow(ResultSet rs, int rowNum) throws SQLException {
                PaoProperty property = new PaoProperty();
                
                int paoId = rs.getInt("PaObjectID");
                String paoTypeString = rs.getString("Type"); 
                PaoType paoType = PaoType.getForDbString(paoTypeString);
                property.setPaoIdentifier(new PaoIdentifier(paoId,paoType));
                
                String propertyName = rs.getString("PropertyName");
                PaoPropertyName paoPropertyName =  PaoPropertyName.valueOf(propertyName);
                property.setPropertyName(paoPropertyName);
                
                property.setPropertyValue(rs.getString("PropertyValue"));

                return property;
            }
        };
        return rowMapper;
    }

    @Override
    public boolean add(PaoProperty property) {
        String sql = "INSERT INTO PaoProperty (PaObjectID, PropertyName, PropertyValue) VALUES (?,?,?)";
        int rowsAffected = simpleJdbcTemplate.update(sql,
                                                     property.getPaoIdentifier().getPaoId(),
                                                     property.getPropertyName().name(),
                                                     property.getPropertyValue());
        boolean result = (rowsAffected == 1);
        
        return result;
    }
    
    @Override
    public PaoProperty getByIdAndName(int id, PaoPropertyName propertyName) {
        String sql = "SELECT pp.PaObjectID,pp.PropertyName,pp.PropertyValue,yp.Type FROM PaoProperty pp, " + 
                     "YukonPaObject yp WHERE pp.PaObjectID = ? AND yp.PAObjectID = ? AND PropertyName = ?";
        
        PaoProperty property = simpleJdbcTemplate.queryForObject(sql, rowMapper, id, id, propertyName.name());
        
        return property;
    }
    
    @Override
    public boolean removeAll(int id) {
        simpleJdbcTemplate.update("DELETE FROM PaoProperty WHERE PaObjectID = ?",id);
        
        return true;
    }
    
    @Override
    public boolean remove(PaoProperty property) {
        String sql = "DELETE FROM PaoProperty WHERE PaObjectID = ? AND PropertyName = ?";
        int rows = simpleJdbcTemplate.update(sql,property.getPaoIdentifier().getPaoId(),property.getPropertyName().name());
        
        boolean ret = (rows == 1);
        return ret;
    }
    
    @Override
    public boolean update(PaoProperty property) {
        String sql = "UPDATE PaoProperty SET PropertyValue = ? WHERE PaObjectID = ? AND PropertyName = ?";
        int rowsAffected = simpleJdbcTemplate.update(sql,
                                                     property.getPropertyValue(),
                                                     property.getPropertyName().name(),
                                                     property.getPaoIdentifier().getPaoId());
        boolean result = (rowsAffected == 1);
        
        return result;
    }

    @Autowired
    public void setSimpleJdbcTemplate(final SimpleJdbcTemplate simpleJdbcTemplate) {
        this.simpleJdbcTemplate = simpleJdbcTemplate;
    }
}
