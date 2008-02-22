package com.cannontech.common.point.alarm.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.common.point.alarm.dao.PointPropertyDao;
import com.cannontech.common.point.alarm.model.PointProperty;

public class PointPropertyDaoImpl implements PointPropertyDao{
    private static final String insertSql;
    private static final String removeSql;
    private static final String removeByPointIdSql;
    private static final String updateSql;
    private static final String selectAllSql;
    private static final String selectByIdSql;
    private static final String selectByIdAndAttributeSql;
    
    private static final ParameterizedRowMapper<PointProperty> rowMapper;
    private SimpleJdbcTemplate simpleJdbcTemplate;
    
    static {
            insertSql = "INSERT INTO PointProperty (pointid," + 
            "propertyid,floatvalue) VALUES (?,?,?)";
            
            removeSql = "DELETE FROM PointProperty WHERE pointid = ? AND propertyid = ?";
            
            removeByPointIdSql = "DELETE FROM PointProperty WHERE pointid = ?";
            
            updateSql = "UPDATE PointProperty SET floatvalue = ?" + 
            " WHERE pointid = ? AND propertyid = ?";
            
            selectAllSql = "SELECT pointid,propertyid,floatvalue FROM PointProperty";
            
            selectByIdSql = selectAllSql + " WHERE pointid = ?";
            
            selectByIdAndAttributeSql = selectAllSql + " WHERE pointid = ? AND propertyid = ?";
            
            rowMapper = new ParameterizedRowMapper<PointProperty>() {
            public PointProperty mapRow(ResultSet rs, int rowNum) throws SQLException {
            	PointProperty attribute = new PointProperty();
            	attribute.setPointId(rs.getInt("PointID"));
            	attribute.setPropertyId(rs.getInt("propertyid"));
            	attribute.setFloatValue(rs.getFloat("FloatValue"));
                return attribute;
            }
        };
    
    }
    
    public void setSimpleJdbcTemplate(final SimpleJdbcTemplate simpleJdbcTemplate) {
        this.simpleJdbcTemplate = simpleJdbcTemplate;
    }
	
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public boolean add(PointProperty attrib) {
		int rowsAffected = simpleJdbcTemplate.update(insertSql, attrib.getPointId(),
																attrib.getPropertyId(),
																attrib.getFloatValue());
		return (rowsAffected == 1);
	}

    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
	public List<PointProperty> getById(int id) {
		return simpleJdbcTemplate.query(selectByIdSql, rowMapper, id);
	}

    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
	public PointProperty getByIdAndPropertyId(int id, int attributeId) {
    	return simpleJdbcTemplate.queryForObject(selectByIdAndAttributeSql, rowMapper, new Object[] {id, attributeId});
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public boolean remove(PointProperty attrib) {
        int rowsAffected = simpleJdbcTemplate.update(removeSql,attrib.getPointId(),attrib.getPropertyId() );

        return (rowsAffected > 0);
	}
    
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public boolean removeByPointId(int id) {
        int rowsAffected = simpleJdbcTemplate.update(removeByPointIdSql,id );

        return (rowsAffected > 0);
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public boolean update(PointProperty attrib) {
		int rowsAffected = simpleJdbcTemplate.update(updateSql,
				attrib.getFloatValue(),
				attrib.getPointId(),
				attrib.getPropertyId());
		return (rowsAffected == 1);
	}
}
