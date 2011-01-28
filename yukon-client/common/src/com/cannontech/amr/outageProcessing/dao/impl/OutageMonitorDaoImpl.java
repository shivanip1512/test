package com.cannontech.amr.outageProcessing.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.amr.MonitorEvaluatorStatus;
import com.cannontech.amr.outageProcessing.OutageMonitor;
import com.cannontech.amr.outageProcessing.dao.OutageMonitorDao;
import com.cannontech.core.dao.DuplicateException;
import com.cannontech.core.dao.OutageMonitorNotFoundException;
import com.cannontech.database.FieldMapper;
import com.cannontech.database.SimpleTableAccessTemplate;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.incrementer.NextValueHelper;

public class OutageMonitorDaoImpl implements OutageMonitorDao, InitializingBean  {

	private static final ParameterizedRowMapper<OutageMonitor> rowMapper;
    private YukonJdbcTemplate yukonJdbcTemplate;
    private NextValueHelper nextValueHelper;
    private SimpleTableAccessTemplate<OutageMonitor> template;
    
    private static final String selectAllSql;
    private static final String selectById;
    private static final String selectCountByName;
    private static final String deleteById;
    private static final String TABLE_NAME = "OutageMonitor";
    
    static {
        
    	selectAllSql = "SELECT * FROM " + TABLE_NAME;

		selectById = selectAllSql + " WHERE OutageMonitorId = ?";
		selectCountByName = "SELECT COUNT(*) FROM " + TABLE_NAME + " WHERE OutageMonitorName = ?";
		
		deleteById = "DELETE FROM " + TABLE_NAME + " WHERE OutageMonitorId = ?";
		
        rowMapper = OutageMonitorDaoImpl.createRowMapper();
    }
    
    public void saveOrUpdate(OutageMonitor outageMonitor) {
    	try {
    		template.save(outageMonitor);
    	} catch (DataIntegrityViolationException e) {
    		throw new DuplicateException("Unable to save outage processor.", e);
    	}
    }
    
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public OutageMonitor getById(final int outageMonitorId) throws OutageMonitorNotFoundException {
    	
    	OutageMonitor outageMonitor = null;
    	
    	try {
    		outageMonitor = yukonJdbcTemplate.queryForObject(selectById, rowMapper, outageMonitorId);
    	} catch (EmptyResultDataAccessException e) {
    		throw new OutageMonitorNotFoundException();
    	}
    	
    	return outageMonitor;
    }
    
    public boolean processorExistsWithName(String name) {

    	int c = yukonJdbcTemplate.queryForInt(selectCountByName, name);
    	
    	return c > 0;
    }
    
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<OutageMonitor> getAll() {
        return yukonJdbcTemplate.query(selectAllSql, rowMapper);
    }
    
    public boolean delete(int outageMonitorId) {
    	
    	return yukonJdbcTemplate.update(deleteById, outageMonitorId) > 0;
    }
    
    private static final ParameterizedRowMapper<OutageMonitor> createRowMapper() {
        final ParameterizedRowMapper<OutageMonitor> rowMapper = new ParameterizedRowMapper<OutageMonitor>() {
            public OutageMonitor mapRow(ResultSet rs, int rowNum) throws SQLException {
            	OutageMonitor outageMonitor = new OutageMonitor();
            	
            	outageMonitor.setOutageMonitorId(rs.getInt("OutageMonitorId"));
            	outageMonitor.setOutageMonitorName(rs.getString("OutageMonitorName"));
            	outageMonitor.setGroupName(rs.getString("GroupName"));
            	outageMonitor.setTimePeriodDays(rs.getInt("TimePeriod"));
            	outageMonitor.setNumberOfOutages(rs.getInt("NumberOfOutages"));
            	outageMonitor.setEvaluatorStatus(MonitorEvaluatorStatus.valueOf(rs.getString("EvaluatorStatus")));
                return outageMonitor;
            }
        };
        return rowMapper;
    }
    
    private FieldMapper<OutageMonitor> outageMonitorFieldMapper = new FieldMapper<OutageMonitor>() {
        public void extractValues(MapSqlParameterSource p, OutageMonitor outageMonitor) {
            p.addValue("OutageMonitorName", outageMonitor.getOutageMonitorName());
            p.addValue("GroupName", outageMonitor.getGroupName());
            p.addValue("TimePeriod", outageMonitor.getTimePeriodDays());
            p.addValue("NumberOfOutages", outageMonitor.getNumberOfOutages());
            p.addValue("EvaluatorStatus", outageMonitor.getEvaluatorStatus().name());
            
        }
        public Number getPrimaryKey(OutageMonitor outageMonitor) {
            return outageMonitor.getOutageMonitorId();
        }
        public void setPrimaryKey(OutageMonitor outageMonitor, int value) {
        	outageMonitor.setOutageMonitorId(value);
        }
    };
    
    public void afterPropertiesSet() throws Exception {
        template = new SimpleTableAccessTemplate<OutageMonitor>(yukonJdbcTemplate, nextValueHelper);
        template.setTableName(TABLE_NAME);
        template.setPrimaryKeyField("OutageMonitorId");
        template.setFieldMapper(outageMonitorFieldMapper); 
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
