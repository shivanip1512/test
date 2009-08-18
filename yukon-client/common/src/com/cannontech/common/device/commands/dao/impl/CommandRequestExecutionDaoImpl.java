package com.cannontech.common.device.commands.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.common.device.commands.CommandRequestExecutionType;
import com.cannontech.common.device.commands.CommandRequestType;
import com.cannontech.common.device.commands.dao.CommandRequestExecutionDao;
import com.cannontech.common.device.commands.dao.model.CommandRequestExecution;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.DateRowMapper;
import com.cannontech.database.FieldMapper;
import com.cannontech.database.ListRowCallbackHandler;
import com.cannontech.database.MaxRowCalbackHandlerRse;
import com.cannontech.database.SimpleTableAccessTemplate;
import com.cannontech.database.SqlUtils;
import com.cannontech.database.incrementer.NextValueHelper;

public class CommandRequestExecutionDaoImpl implements CommandRequestExecutionDao, InitializingBean {

	private static final ParameterizedRowMapper<CommandRequestExecution> rowMapper;
    private SimpleJdbcTemplate simpleJdbcTemplate;
    private NextValueHelper nextValueHelper;
    private SimpleTableAccessTemplate<CommandRequestExecution> template;
    
    private static final String selectById;
    private static final String TABLE_NAME = "CommandRequestExecution";
    
    static {
        
    	selectById = "SELECT CRE.* FROM " + TABLE_NAME + " CRE WHERE CRE.CommandRequestExecutionId = ?";
		rowMapper = CommandRequestExecutionDaoImpl.createRowMapper();
    }
    
    // SAVE OR UPDATE
    public void saveOrUpdate(CommandRequestExecution commandRequestExecution) {
    	template.save(commandRequestExecution);
    }
    
    // GET BY ID
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public CommandRequestExecution getById(final int commandRequestExecutionId) {
    	CommandRequestExecution commandRequestExecution = simpleJdbcTemplate.queryForObject(selectById, rowMapper, commandRequestExecutionId);
        return commandRequestExecution;
    }
    
    // GET LATEST
    public CommandRequestExecution getLatest(Date cutoff) {
    	
    	SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT CRE.* FROM " + TABLE_NAME + " CRE");
        
        List<Object> args = new ArrayList<Object>();
        
        if (cutoff != null) {
        	sql.append("AND CRE.StartTime <= ?");
        	args.add(cutoff);
        }
        
        sql.append("ORDER BY CRE.StartTime DESC");
        
        List<CommandRequestExecution> cres = new ArrayList<CommandRequestExecution>();
        ListRowCallbackHandler lrcHandler = new ListRowCallbackHandler(cres, rowMapper);
        simpleJdbcTemplate.getJdbcOperations().query(sql.getSql(), args.toArray(), new MaxRowCalbackHandlerRse(lrcHandler, 1));
        
        if (cres.size() > 0) {
        	return cres.get(0);
        } else {
        	return null;
        }
    }

    // BY RANGE
    public List<CommandRequestExecution> getByRange(int commandRequestExecutionId, Date startTime, Date stopTime, CommandRequestExecutionType type, boolean ascending) {
    	
    	SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT CRE.* FROM " + TABLE_NAME + " CRE");
        
        List<Object> args = new ArrayList<Object>();
        
        if (commandRequestExecutionId > 0) {
    		sql.append("WHERE CRE.CommandRequestExecutionId = ?");
    		args.add(commandRequestExecutionId);
        } else {
        	sql.append("WHERE CRE.CommandRequestExecutionId > 0");
        }
        
        if (startTime != null) {
        	sql.append("AND CRE.StartTime >= ?");
        	args.add(startTime);
        }
        
        if (stopTime != null) {
        	sql.append("AND CRE.StartTime <= ?");
        	args.add(stopTime);
        }
        
        if (type != null) {
        	sql.append("AND CRE.Type = ?");
        	args.add(type.name());
        }
        
        if (ascending) {
        	sql.append("ORDER BY CRE.StartTime");
        } else {
        	sql.append("ORDER BY CRE.StartTime DESC");
        }
        
        return simpleJdbcTemplate.query(sql.getSql(), rowMapper, args.toArray());
    	
    }
    
    // REQUEST COUNT
    public int getRequestCountById(int commandRequestExecutionId) {
    	
    	String sql = "SELECT CRE.RequestCount FROM " + TABLE_NAME + " CRE WHERE CRE.CommandRequestExecutionId = ?";
    	return simpleJdbcTemplate.queryForInt(sql, commandRequestExecutionId);
    }
    
    // IS COMPLETE
    public boolean isComplete(int commandRequestExecutionId) {
    	
    	Date stopTime = queryForStopTime(commandRequestExecutionId);
    	if (stopTime == null) {
    		return false;
    	}
    	
    	return true;
    }
    
    // STOP TIME
    public Date getStopTime(int commandRequestExecutionId) {
    	
    	return queryForStopTime(commandRequestExecutionId);
    }
    
    private Date queryForStopTime(int commandRequestExecutionId) {
    	
    	String sql = "SELECT CRE.StopTime FROM " + TABLE_NAME + " CRE WHERE CRE.CommandRequestExecutionId = ?";
    	return simpleJdbcTemplate.queryForObject(sql, new DateRowMapper(), commandRequestExecutionId);
    }
    
    public static final ParameterizedRowMapper<CommandRequestExecution> createRowMapper() {
        final ParameterizedRowMapper<CommandRequestExecution> rowMapper = new ParameterizedRowMapper<CommandRequestExecution>() {
            public CommandRequestExecution mapRow(ResultSet rs, int rowNum) throws SQLException {
            	
            	CommandRequestExecution commandRequestExecution = new CommandRequestExecution();
            	
            	commandRequestExecution.setId(rs.getInt("CommandRequestExecutionId"));
            	commandRequestExecution.setStartTime(rs.getTimestamp("StartTime"));
            	commandRequestExecution.setStopTime(rs.getTimestamp("StopTime"));
            	commandRequestExecution.setRequestCount(rs.getInt("RequestCount"));
            	commandRequestExecution.setCommandRequestExecutionType(CommandRequestExecutionType.valueOf(rs.getString("CommandRequestExecutionType")));
            	Integer userId = SqlUtils.getNullableInt(rs, "UserId");
            	commandRequestExecution.setUserId(userId);
            	commandRequestExecution.setCommandRequestType(CommandRequestType.valueOf(rs.getString("CommandRequestType")));
                
            	return commandRequestExecution;
            }
        };
        return rowMapper;
    }
    
    private FieldMapper<CommandRequestExecution> fieldMapper = new FieldMapper<CommandRequestExecution>() {
        public void extractValues(MapSqlParameterSource p, CommandRequestExecution commandRequestExecution) {
            p.addValue("CommandRequestExecutionId", commandRequestExecution.getId());
            p.addValue("StartTime", commandRequestExecution.getStartTime());
            p.addValue("StopTime", commandRequestExecution.getStopTime());
            p.addValue("RequestCount", commandRequestExecution.getRequestCount());
            p.addValue("CommandRequestExecutionType", commandRequestExecution.getCommandRequestExecutionType().name());
            p.addValue("UserID", commandRequestExecution.getUserId());
            p.addValue("CommandRequestType", commandRequestExecution.getCommandRequestType().name());
            
        }
        public Number getPrimaryKey(CommandRequestExecution commandRequestExecution) {
            return commandRequestExecution.getId();
        }
        public void setPrimaryKey(CommandRequestExecution commandRequestExecution, int value) {
        	commandRequestExecution.setId(value);
        }
    };
    
    public void afterPropertiesSet() throws Exception {
    	template = new SimpleTableAccessTemplate<CommandRequestExecution>(simpleJdbcTemplate, nextValueHelper);
    	template.withTableName(TABLE_NAME);
    	template.withPrimaryKeyField("CommandRequestExecutionId");
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
