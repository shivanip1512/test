package com.cannontech.amr.tamperFlagProcessing.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.amr.tamperFlagProcessing.TamperFlagMonitor;
import com.cannontech.amr.tamperFlagProcessing.TamperFlagMonitorEvaluatorStatus;
import com.cannontech.amr.tamperFlagProcessing.dao.TamperFlagMonitorDao;
import com.cannontech.core.dao.DuplicateException;
import com.cannontech.core.dao.TamperFlagMonitorNotFoundException;
import com.cannontech.database.FieldMapper;
import com.cannontech.database.SimpleTableAccessTemplate;
import com.cannontech.database.incrementer.NextValueHelper;

public class TamperFlagMonitorDaoImpl implements TamperFlagMonitorDao, InitializingBean  {

	private static final ParameterizedRowMapper<TamperFlagMonitor> rowMapper;
    private SimpleJdbcTemplate simpleJdbcTemplate;
    private NextValueHelper nextValueHelper;
    private SimpleTableAccessTemplate<TamperFlagMonitor> template;
    
    private static final String selectAllSql;
    private static final String selectById;
    private static final String selectCountByName;
    private static final String deleteById;
    private static final String TABLE_NAME = "TamperFlagMonitor";
    
    static {
        
    	selectAllSql = "SELECT * FROM " + TABLE_NAME;

		selectById = selectAllSql + " WHERE TamperFlagMonitorId = ?";
		selectCountByName = "SELECT COUNT(*) FROM " + TABLE_NAME + " WHERE TamperFlagMonitorName = ?";
		
		deleteById = "DELETE FROM " + TABLE_NAME + " WHERE TamperFlagMonitorId = ?";
		
        rowMapper = TamperFlagMonitorDaoImpl.createRowMapper();
    }
    
    public void saveOrUpdate(TamperFlagMonitor tamperFlagMonitor) {
    	try {
    		template.save(tamperFlagMonitor);
    	} catch (DataIntegrityViolationException e) {
    		throw new DuplicateException("Unable to save tamper flag monitor.", e);
    	}
    }
    
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public TamperFlagMonitor getById(final int tamperFlagMonitorId) throws TamperFlagMonitorNotFoundException {
    	
    	TamperFlagMonitor tamperFlagMonitor = null;
    	
    	try {
    		tamperFlagMonitor = simpleJdbcTemplate.queryForObject(selectById, rowMapper, tamperFlagMonitorId);
    	} catch (EmptyResultDataAccessException e) {
    		throw new TamperFlagMonitorNotFoundException();
    	}
    	
    	return tamperFlagMonitor;
    }
    
    public boolean processorExistsWithName(String name) {

    	int c = simpleJdbcTemplate.queryForInt(selectCountByName, name);
    	
    	return c > 0;
    }
    
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<TamperFlagMonitor> getAll() {
        try {
            List<TamperFlagMonitor> list = simpleJdbcTemplate.query(selectAllSql, rowMapper, new Object[]{});
            return list;
        } catch (DataAccessException e) {
            return Collections.emptyList();
        } 
    }
    
    public int delete(int tamperFlagMonitorId) {
    	
    	return simpleJdbcTemplate.update(deleteById, tamperFlagMonitorId);
    }
    
    private static final ParameterizedRowMapper<TamperFlagMonitor> createRowMapper() {
        final ParameterizedRowMapper<TamperFlagMonitor> rowMapper = new ParameterizedRowMapper<TamperFlagMonitor>() {
            public TamperFlagMonitor mapRow(ResultSet rs, int rowNum) throws SQLException {
            	TamperFlagMonitor tamperFlagMonitor = new TamperFlagMonitor();
            	tamperFlagMonitor.setTamperFlagMonitorId(rs.getInt("TamperFlagMonitorId"));
            	tamperFlagMonitor.setTamperFlagMonitorName(rs.getString("TamperFlagMonitorName"));
            	tamperFlagMonitor.setGroupName(rs.getString("GroupName"));
            	tamperFlagMonitor.setEvaluatorStatus(TamperFlagMonitorEvaluatorStatus.valueOf(rs.getString("EvaluatorStatus")));
                return tamperFlagMonitor;
            }
        };
        return rowMapper;
    }
    
    private FieldMapper<TamperFlagMonitor> tamperFlagMonitorFieldMapper = new FieldMapper<TamperFlagMonitor>() {
        public void extractValues(MapSqlParameterSource p, TamperFlagMonitor tamperFlagMonitor) {
            p.addValue("TamperFlagMonitorId", tamperFlagMonitor.getTamperFlagMonitorId());
            p.addValue("TamperFlagMonitorName", tamperFlagMonitor.getTamperFlagMonitorName());
            p.addValue("GroupName", tamperFlagMonitor.getGroupName());
            p.addValue("EvaluatorStatus", tamperFlagMonitor.getEvaluatorStatus().name());
            
        }
        public Number getPrimaryKey(TamperFlagMonitor tamperFlagMonitor) {
            return tamperFlagMonitor.getTamperFlagMonitorId();
        }
        public void setPrimaryKey(TamperFlagMonitor tamperFlagMonitor, int value) {
        	tamperFlagMonitor.setTamperFlagMonitorId(value);
        }
    };
    
    public void afterPropertiesSet() throws Exception {
        template = new SimpleTableAccessTemplate<TamperFlagMonitor>(simpleJdbcTemplate, nextValueHelper);
        template.withTableName(TABLE_NAME);
        template.withPrimaryKeyField("TamperFlagMonitorId");
        template.withFieldMapper(tamperFlagMonitorFieldMapper); 
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
