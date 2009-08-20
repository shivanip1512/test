package com.cannontech.common.device.commands.dao.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.common.device.commands.CommandRequestExecutionType;
import com.cannontech.common.device.commands.dao.CommandRequestExecutionDao;
import com.cannontech.common.device.commands.dao.model.CommandRequestExecution;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.DateRowMapper;
import com.cannontech.database.ListRowCallbackHandler;
import com.cannontech.database.MaxRowCalbackHandlerRse;
import com.cannontech.database.RowAndFieldMapper;
import com.cannontech.database.SimpleTableAccessTemplate;
import com.cannontech.database.incrementer.NextValueHelper;

public class CommandRequestExecutionDaoImpl implements CommandRequestExecutionDao, InitializingBean {

	private static final RowAndFieldMapper<CommandRequestExecution> rowAndFieldMapper;
    private SimpleJdbcTemplate simpleJdbcTemplate;
    private NextValueHelper nextValueHelper;
    private SimpleTableAccessTemplate<CommandRequestExecution> template;
    
    private static final String selectById;
    
    static {
        
    	selectById = "SELECT CRE.* FROM CommandRequestExec CRE WHERE CommandRequestExecId = ?";
		rowAndFieldMapper = new CommandRequestExecutionRowAndFieldMapper();
    }
    
    // SAVE OR UPDATE
    public void saveOrUpdate(CommandRequestExecution commandRequestExecution) {
    	template.save(commandRequestExecution);
    }
    
    // GET BY ID
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public CommandRequestExecution getById(final int commandRequestExecutionId) {
    	CommandRequestExecution commandRequestExecution = simpleJdbcTemplate.queryForObject(selectById, rowAndFieldMapper, commandRequestExecutionId);
        return commandRequestExecution;
    }
    
    // GET LATEST
    public CommandRequestExecution getLatest(Date cutoff) {
    	
    	SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT CRE.* FROM CommandRequestExec CRE");
        
        List<Object> args = new ArrayList<Object>();
        
        if (cutoff != null) {
        	sql.append("AND CRE.StartTime <= ?");
        	args.add(cutoff);
        }
        
        sql.append("ORDER BY CRE.StartTime DESC");
        
        List<CommandRequestExecution> cres = new ArrayList<CommandRequestExecution>();
        ListRowCallbackHandler lrcHandler = new ListRowCallbackHandler(cres, rowAndFieldMapper);
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
        sql.append("SELECT CRE.* FROM CommandRequestExec CRE");
        
        List<Object> args = new ArrayList<Object>();
        
        if (commandRequestExecutionId > 0) {
    		sql.append("WHERE CommandRequestExecId = ?");
    		args.add(commandRequestExecutionId);
        } else {
        	sql.append("WHERE CommandRequestExecId > 0");
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
        
        return simpleJdbcTemplate.query(sql.getSql(), rowAndFieldMapper, args.toArray());
    	
    }
    
    // REQUEST COUNT
    public int getRequestCountById(int commandRequestExecutionId) {
    	
    	String sql = "SELECT CRE.RequestCount FROM CommandRequestExec CRE WHERE CRE.CommandRequestExecId = ?";
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
    	
    	String sql = "SELECT CRE.StopTime FROM CommandRequestExec CRE WHERE CRE.CommandRequestExecId = ?";
    	return simpleJdbcTemplate.queryForObject(sql, new DateRowMapper(), commandRequestExecutionId);
    }
    
    public void afterPropertiesSet() throws Exception {
    	template = new SimpleTableAccessTemplate<CommandRequestExecution>(simpleJdbcTemplate, nextValueHelper);
    	template.withTableName("CommandRequestExec");
    	template.withPrimaryKeyField("CommandRequestExecId");
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
