package com.cannontech.core.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;

import com.cannontech.common.model.PaoProperty;
import com.cannontech.common.model.PaoPropertyName;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.PaoPropertyDao;
import com.cannontech.database.YukonJdbcTemplate;

public class PaoPropertyDaoImpl implements PaoPropertyDao {
    
    @Autowired private YukonJdbcTemplate jdbcTemplate;
    private static final RowMapper<PaoProperty> rowMapper;
   
    static { 
        rowMapper = PaoPropertyDaoImpl.createRowMapper();
    }
    
    private static final RowMapper<PaoProperty> createRowMapper() {
        RowMapper<PaoProperty> rowMapper = new RowMapper<PaoProperty>() {
            @Override
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
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("INSERT INTO PaoProperty (PaObjectID, PropertyName, PropertyValue) VALUES (?,?,?)");
        int rowsAffected = jdbcTemplate.update(sql.toString(),
                                                     property.getPaoIdentifier().getPaoId(),
                                                     property.getPropertyName().name(),
                                                     property.getPropertyValue());
        boolean result = (rowsAffected == 1);
        
        return result;
    }
    
    @Override
    public PaoProperty getByIdAndName(int id, PaoPropertyName propertyName) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT pp.PaObjectID,pp.PropertyName,pp.PropertyValue,yp.Type FROM PaoProperty pp,YukonPaObject yp");
        sql.append("WHERE pp.PaObjectID = yp.PAObjectID AND pp.PaObjectID = ? AND PropertyName = ?");
        
        PaoProperty property = jdbcTemplate.queryForObject(sql.toString(), rowMapper, id, propertyName.name());
        
        return property;
    }
    
    @Override
    public boolean removeAll(int id) {
        jdbcTemplate.update("DELETE FROM PaoProperty WHERE PaObjectID = ?",id);
        
        return true;
    }
    
    @Override
    public boolean remove(PaoProperty property) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("DELETE FROM PaoProperty WHERE PaObjectID = ? AND PropertyName = ?");
        int rows = jdbcTemplate.update(sql.toString(),property.getPaoIdentifier().getPaoId(),property.getPropertyName().name());
        
        boolean ret = (rows == 1);
        return ret;
    }
    
    @Override
    public boolean update(PaoProperty property) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("UPDATE PaoProperty SET PropertyValue = ? WHERE PaObjectID = ? AND PropertyName = ?");
        
        int rowsAffected = jdbcTemplate.update(sql.toString(),
                                                     property.getPropertyValue(),
                                                     property.getPaoIdentifier().getPaoId(),
                                                     property.getPropertyName().name());
        boolean result = (rowsAffected == 1);
        
        return result;
    }
}
