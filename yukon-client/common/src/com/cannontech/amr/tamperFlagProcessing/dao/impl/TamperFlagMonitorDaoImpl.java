package com.cannontech.amr.tamperFlagProcessing.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.amr.MonitorEvaluatorStatus;
import com.cannontech.amr.tamperFlagProcessing.TamperFlagMonitor;
import com.cannontech.amr.tamperFlagProcessing.dao.TamperFlagMonitorDao;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.DuplicateException;
import com.cannontech.core.dao.TamperFlagMonitorNotFoundException;
import com.cannontech.database.FieldMapper;
import com.cannontech.database.SimpleTableAccessTemplate;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.incrementer.NextValueHelper;

public class TamperFlagMonitorDaoImpl implements TamperFlagMonitorDao  {

    private static final RowMapper<TamperFlagMonitor> rowMapper;
    private YukonJdbcTemplate yukonJdbcTemplate;
    private NextValueHelper nextValueHelper;
    private SimpleTableAccessTemplate<TamperFlagMonitor> template;
    
    private static final String selectAllSql;
    private static final String selectById;
    private static final String selectCountByName;
    private static final String deleteById;
    
    static {
        
    	selectAllSql = "SELECT * FROM TamperFlagMonitor";

		selectById = selectAllSql + " WHERE TamperFlagMonitorId = ?";
        selectCountByName = "SELECT COUNT(*) FROM TamperFlagMonitor WHERE TamperFlagMonitorName";
		
		deleteById = "DELETE FROM TamperFlagMonitor WHERE TamperFlagMonitorId = ?";
		
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
    		tamperFlagMonitor = 
    		    yukonJdbcTemplate.queryForObject(selectById, rowMapper, tamperFlagMonitorId);
    	} catch (EmptyResultDataAccessException e) {
    		throw new TamperFlagMonitorNotFoundException();
    	}
    	
    	return tamperFlagMonitor;
    }
    
    public boolean processorExistsWithName(String name) {
        final SqlStatementBuilder sql = new SqlStatementBuilder(selectCountByName);
        sql.eq(name);
        int c = yukonJdbcTemplate.queryForInt(sql);

        return c > 0;
    }
    
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<TamperFlagMonitor> getAll() {
        return yukonJdbcTemplate.query(selectAllSql, rowMapper, new Object[]{});
    }
    
    public boolean delete(int tamperFlagMonitorId) {
    	
    	return yukonJdbcTemplate.update(deleteById, tamperFlagMonitorId) > 0;
    }
    
    private static final RowMapper<TamperFlagMonitor> createRowMapper() {
        final RowMapper<TamperFlagMonitor> rowMapper = new RowMapper<TamperFlagMonitor>() {
            public TamperFlagMonitor mapRow(ResultSet rs, int rowNum) throws SQLException {
            	TamperFlagMonitor tamperFlagMonitor = new TamperFlagMonitor();
            	tamperFlagMonitor.setTamperFlagMonitorId(rs.getInt("TamperFlagMonitorId"));
            	tamperFlagMonitor.setTamperFlagMonitorName(rs.getString("TamperFlagMonitorName"));
            	tamperFlagMonitor.setGroupName(rs.getString("GroupName"));
            	tamperFlagMonitor.setEvaluatorStatus(MonitorEvaluatorStatus.valueOf(rs.getString("EvaluatorStatus")));
                return tamperFlagMonitor;
            }
        };
        return rowMapper;
    }
    
    private FieldMapper<TamperFlagMonitor> tamperFlagMonitorFieldMapper = new FieldMapper<TamperFlagMonitor>() {
        public void extractValues(MapSqlParameterSource p, TamperFlagMonitor tamperFlagMonitor) {
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
    
    @PostConstruct
    public void init() throws Exception {
        template = 
           new SimpleTableAccessTemplate<TamperFlagMonitor>(yukonJdbcTemplate, nextValueHelper);
        template.setTableName("TamperFlagMonitor");
        template.setPrimaryKeyField("TamperFlagMonitorId");
        template.setFieldMapper(tamperFlagMonitorFieldMapper); 
    }
    
    @Autowired
    public void setYukonJdbcTemplate(YukonJdbcTemplate yukonJdbcTemplate) {
		this.yukonJdbcTemplate = yukonJdbcTemplate;
	}
    @Autowired
    public void setNextValueHelper(NextValueHelper nextValueHelper) {
		this.nextValueHelper = nextValueHelper;
	}
}
