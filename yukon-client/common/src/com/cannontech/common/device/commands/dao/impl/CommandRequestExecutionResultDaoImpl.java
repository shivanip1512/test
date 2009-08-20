package com.cannontech.common.device.commands.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.common.device.commands.dao.CommandRequestExecutionResultDao;
import com.cannontech.common.device.commands.dao.CommandRequestExecutionResultsFilterType;
import com.cannontech.common.device.commands.dao.model.CommandRequestExecutionResult;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.IntegerRowMapper;
import com.cannontech.database.RowAndFieldMapper;
import com.cannontech.database.SimpleTableAccessTemplate;
import com.cannontech.database.incrementer.NextValueHelper;

public class CommandRequestExecutionResultDaoImpl implements CommandRequestExecutionResultDao, InitializingBean {

	private static final RowAndFieldMapper<CommandRequestExecutionResult> rowAndFieldMapper;
    private SimpleJdbcTemplate simpleJdbcTemplate;
    private NextValueHelper nextValueHelper;
    private SimpleTableAccessTemplate<CommandRequestExecutionResult> template;
    
    private static final String selectResultIdsById;
    
    private static final String selectCountById;
    private static final String selectSuccessCountById;
    private static final String selectFailCountById;
    
    private static final String selectDeviceIdsById;
    private static final String selectSuccessDeviceIdsById;
    private static final String selectFailDeviceIdsById;
    
    static {
    	
		selectResultIdsById = "SELECT CRER.CommandRequestExecResultId FROM CommandRequestExecResult CRER WHERE CommandRequestExecId = ? ORDER BY CompleteTime";
		
		selectCountById = "SELECT COUNT(CRER.CommandRequestExecResultId) AS CrerCount FROM CommandRequestExecResult CRER WHERE CRER.CommandRequestExecId = ?";
		selectSuccessCountById = selectCountById + " AND CRER.ErrorCode = 0";
		selectFailCountById = selectCountById + " AND CRER.ErrorCode > 0";
		
		selectDeviceIdsById = "SELECT DISTINCT CRER.DeviceId FROM CommandRequestExecResult CRER WHERE CommandRequestExecId = ?";
		selectSuccessDeviceIdsById = selectDeviceIdsById + " AND CRER.ErrorCode = 0";
		selectFailDeviceIdsById = selectDeviceIdsById + " AND CRER.ErrorCode > 0";
		
		rowAndFieldMapper = new CommandRequestExecutionResultsRowAndFieldMapper();
    }
    
    // SAVE OR UPDATE
    public void saveOrUpdate(CommandRequestExecutionResult commandRequestExecutionResult) {
    	template.save(commandRequestExecutionResult);
    }
    
    // GET RESULTS BY CRE ID
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<CommandRequestExecutionResult> getResultsByExecutionId(int commandRequestExecutionId, CommandRequestExecutionResultsFilterType reportFilterType) {
		    	
    	SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT * FROM CommandRequestExecResult");
        sql.append("WHERE CommandRequestExecId = ?");
        
        String sqlCondition = reportFilterType.getSqlCondition();
        if (sqlCondition != null) {
        	sql.append("AND " + sqlCondition);
        }
        
        sql.append("ORDER BY CompleteTime");
        
        List<CommandRequestExecutionResult> list = simpleJdbcTemplate.query(sql.getSql(), rowAndFieldMapper, commandRequestExecutionId);
        return list;
	}
    
    // GET RESULT IDS BY CRE ID
    public List<Integer> getResultsIdsByExecutionId(int commandRequestExecutionId) {
    	
    	ParameterizedRowMapper<Integer> mapper = new ParameterizedRowMapper<Integer>() {
	        public Integer mapRow(ResultSet rs, int num) throws SQLException{
	        	return rs.getInt("CommandRequestExecResultId");
	        }
	    };
	    
    	List<Integer> resultIds = simpleJdbcTemplate.query(selectResultIdsById, mapper, commandRequestExecutionId);
    	return resultIds;
    }
    
    // GET RESULT COUNT BY CRE ID
    public int getCountByExecutionId(int commandRequestExecutionId) {
    	return simpleJdbcTemplate.queryForInt(selectCountById, commandRequestExecutionId);
    }
    
    public int getSucessCountByExecutionId(int commandRequestExecutionId) {
    	return simpleJdbcTemplate.queryForInt(selectSuccessCountById, commandRequestExecutionId);
    }
    
	public int getFailCountByExecutionId(int commandRequestExecutionId) {
		return simpleJdbcTemplate.queryForInt(selectFailCountById, commandRequestExecutionId);
	}
	
	// GET DEVICE IDS BY CRE ID
	public List<Integer> getDeviceIdsByExecutionId(int commandRequestExecutionId) {
		return simpleJdbcTemplate.query(selectDeviceIdsById, new IntegerRowMapper(), commandRequestExecutionId);
	}
	public List<Integer> getSucessDeviceIdsByExecutionId(int commandRequestExecutionId) {
		return simpleJdbcTemplate.query(selectSuccessDeviceIdsById, new IntegerRowMapper(), commandRequestExecutionId);
	}
	public List<Integer> getFailDeviceIdsByExecutionId(int commandRequestExecutionId) {
		return simpleJdbcTemplate.query(selectFailDeviceIdsById, new IntegerRowMapper(), commandRequestExecutionId);
	}
    
    
    public void afterPropertiesSet() throws Exception {
    	template = new SimpleTableAccessTemplate<CommandRequestExecutionResult>(simpleJdbcTemplate, nextValueHelper);
    	template.withTableName("CommandRequestExecResult");
    	template.withPrimaryKeyField("CommandRequestExecResultId");
    	template.withFieldMapper(rowAndFieldMapper); 
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
