package com.cannontech.common.device.commands.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.common.device.commands.dao.CommandRequestExecutionResultsDao;
import com.cannontech.common.device.commands.dao.CommandRequestExecutionResultsFilterType;
import com.cannontech.common.device.commands.dao.model.CommandRequestExecutionResult;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.FieldMapper;
import com.cannontech.database.IntegerRowMapper;
import com.cannontech.database.SimpleTableAccessTemplate;
import com.cannontech.database.incrementer.NextValueHelper;

public class CommandRequestExecutionResultsDaoImpl implements CommandRequestExecutionResultsDao, InitializingBean {

	private static final ParameterizedRowMapper<CommandRequestExecutionResult> rowMapper;
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
    
    private static final String TABLE_NAME = "CommandRequestExecutionResults";
    
    static {
    	
		selectResultIdsById = "SELECT CRER.CommandRequestExecutionResultsId FROM " + TABLE_NAME + " CRER WHERE CommandRequestExecutionId = ? ORDER BY CompleteTime";
		
		selectCountById = "SELECT COUNT(CRER.CommandRequestExecutionResultsId) AS CrerCount FROM " + TABLE_NAME + " CRER WHERE CommandRequestExecutionId = ?";
		selectSuccessCountById = selectCountById + " AND CRER.ErrorCode = 0";
		selectFailCountById = selectCountById + " AND CRER.ErrorCode > 0";
		
		selectDeviceIdsById = "SELECT DISTINCT CRER.DeviceId FROM " + TABLE_NAME + " CRER WHERE CommandRequestExecutionId = ?";
		selectSuccessDeviceIdsById = selectDeviceIdsById + " AND CRER.ErrorCode = 0";
		selectFailDeviceIdsById = selectDeviceIdsById + " AND CRER.ErrorCode > 0";
		
		rowMapper = CommandRequestExecutionResultsDaoImpl.createRowMapper();
    }
    
    // SAVE OR UPDATE
    public void saveOrUpdate(CommandRequestExecutionResult commandRequestExecutionResult) {
    	template.save(commandRequestExecutionResult);
    }
    
    // GET RESULTS BY CRE ID
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<CommandRequestExecutionResult> getResultsByExecutionId(int commandRequestExecutionId, CommandRequestExecutionResultsFilterType reportFilterType) {
		    	
    	SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT * FROM " + TABLE_NAME);
        sql.append("WHERE CommandRequestExecutionId = ?");
        
        String sqlCondition = reportFilterType.getSqlCondition();
        if (sqlCondition != null) {
        	sql.append("AND " + sqlCondition);
        }
        
        sql.append("ORDER BY CompleteTime");
        
        List<CommandRequestExecutionResult> list = simpleJdbcTemplate.query(sql.getSql(), rowMapper, commandRequestExecutionId);
        return list;
	}
    
    // GET RESULT IDS BY CRE ID
    public List<Integer> getResultsIdsByExecutionId(int commandRequestExecutionId) {
    	
    	ParameterizedRowMapper<Integer> mapper = new ParameterizedRowMapper<Integer>() {
	        public Integer mapRow(ResultSet rs, int num) throws SQLException{
	        	return rs.getInt("CommandRequestExecutionResultsId");
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
    
    public static final ParameterizedRowMapper<CommandRequestExecutionResult> createRowMapper() {
        final ParameterizedRowMapper<CommandRequestExecutionResult> rowMapper = new ParameterizedRowMapper<CommandRequestExecutionResult>() {
            public CommandRequestExecutionResult mapRow(ResultSet rs, int rowNum) throws SQLException {
            	
            	CommandRequestExecutionResult commandRequestExecutionResult = new CommandRequestExecutionResult();
            	
            	commandRequestExecutionResult.setId(rs.getInt("CommandRequestExecutionResultsId"));
            	commandRequestExecutionResult.setCommandRequestExecutionId(rs.getInt("CommandRequestExecutionId"));
            	commandRequestExecutionResult.setCommand(rs.getString("Command"));
            	commandRequestExecutionResult.setErrorCode(rs.getInt("ErrorCode"));
            	commandRequestExecutionResult.setCompleteTime(rs.getTimestamp("CompleteTime"));
            	commandRequestExecutionResult.setDeviceId(rs.getInt("DeviceId"));
            	commandRequestExecutionResult.setRouteId(rs.getInt("RouteId"));
                
            	return commandRequestExecutionResult;
            }
        };
        return rowMapper;
    }
    
    private FieldMapper<CommandRequestExecutionResult> fieldMapper = new FieldMapper<CommandRequestExecutionResult>() {
        public void extractValues(MapSqlParameterSource p, CommandRequestExecutionResult commandRequestExecutionResult) {
            p.addValue("CommandRequestExecutionResultsId", commandRequestExecutionResult.getId());
            p.addValue("CommandRequestExecutionId", commandRequestExecutionResult.getCommandRequestExecutionId());
            p.addValue("Command", commandRequestExecutionResult.getCommand());
            p.addValue("ErrorCode", commandRequestExecutionResult.getErrorCode());
            p.addValue("CompleteTime", commandRequestExecutionResult.getCompleteTime());
            p.addValue("DeviceId", commandRequestExecutionResult.getDeviceId());
            p.addValue("RouteId", commandRequestExecutionResult.getRouteId());
            
        }
        public Number getPrimaryKey(CommandRequestExecutionResult commandRequestExecutionResult) {
            return commandRequestExecutionResult.getId();
        }
        public void setPrimaryKey(CommandRequestExecutionResult commandRequestExecutionResult, int value) {
        	commandRequestExecutionResult.setId(value);
        }
    };
    
    public void afterPropertiesSet() throws Exception {
    	template = new SimpleTableAccessTemplate<CommandRequestExecutionResult>(simpleJdbcTemplate, nextValueHelper);
    	template.withTableName(TABLE_NAME);
    	template.withPrimaryKeyField("CommandRequestExecutionResultsId");
    	template.withFieldMapper(fieldMapper); 
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
