package com.cannontech.common.point.alarm.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.common.point.alarm.dao.PointPropertyValueDao;
import com.cannontech.common.point.alarm.model.PointPropertyValue;

public class PointPropertyValueDaoImpl implements PointPropertyValueDao{
    private static final String insertSql;
    private static final String removeSql;
    private static final String removeByPointIdSql;
    private static final String updateSql;
    private static final String selectAllSql;
    private static final String selectByIdSql;
    private static final String selectByIdAndAttributeSql;
    
    private static final ParameterizedRowMapper<PointPropertyValue> rowMapper;
    private SimpleJdbcTemplate simpleJdbcTemplate;
    
    static {
            insertSql = "INSERT INTO PointPropertyValue (pointid," + 
            "pointpropertyid,floatvalue) VALUES (?,?,?)";
            
            removeSql = "DELETE FROM PointPropertyValue WHERE pointid = ? AND pointpropertyid = ?";
            
            removeByPointIdSql = "DELETE FROM PointPropertyValue WHERE pointid = ?";
            
            updateSql = "UPDATE PointProperty SET floatvalue = ?" + 
            " WHERE pointid = ? AND pointpropertyid = ?";
            
            selectAllSql = "SELECT pointid,pointpropertyid,floatvalue FROM PointPropertyValue";
            
            selectByIdSql = selectAllSql + " WHERE pointid = ?";
            
            selectByIdAndAttributeSql = selectAllSql + " WHERE pointid = ? AND pointpropertyid = ?";
            
            rowMapper = new ParameterizedRowMapper<PointPropertyValue>() {
            public PointPropertyValue mapRow(ResultSet rs, int rowNum) throws SQLException {
            	PointPropertyValue attribute = new PointPropertyValue();
            	attribute.setPointId(rs.getInt("PointID"));
            	attribute.setPropertyId(rs.getInt("pointpropertyid"));
            	attribute.setFloatValue(rs.getFloat("FloatValue"));
                return attribute;
            }
        };
    
    }
    
    public void setSimpleJdbcTemplate(final SimpleJdbcTemplate simpleJdbcTemplate) {
        this.simpleJdbcTemplate = simpleJdbcTemplate;
    }
	
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public boolean add(PointPropertyValue attrib) {
		int rowsAffected = simpleJdbcTemplate.update(insertSql, attrib.getPointId(),
																attrib.getPropertyId(),
																attrib.getFloatValue());
		return (rowsAffected == 1);
	}

    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
	public List<PointPropertyValue> getById(int id) {
		return simpleJdbcTemplate.query(selectByIdSql, rowMapper, id);
	}

    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
	public PointPropertyValue getByIdAndPropertyId(int id, int attributeId) {
    	return simpleJdbcTemplate.queryForObject(selectByIdAndAttributeSql, rowMapper, new Object[] {id, attributeId});
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public boolean remove(PointPropertyValue attrib) {
        int rowsAffected = simpleJdbcTemplate.update(removeSql,attrib.getPointId(),attrib.getPropertyId() );

        return (rowsAffected > 0);
	}
    
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public boolean removeByPointId(int id) {
        int rowsAffected = simpleJdbcTemplate.update(removeByPointIdSql,id );

        return (rowsAffected > 0);
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public boolean update(PointPropertyValue attrib) {
		int rowsAffected = simpleJdbcTemplate.update(updateSql,
				attrib.getFloatValue(),
				attrib.getPointId(),
				attrib.getPropertyId());
		return (rowsAffected == 1);
	}
}
