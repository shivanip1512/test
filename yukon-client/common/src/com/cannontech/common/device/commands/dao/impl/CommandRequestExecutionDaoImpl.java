package com.cannontech.common.device.commands.dao.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.common.device.DeviceRequestType;
import com.cannontech.common.device.commands.CommandRequestExecutionStatus;
import com.cannontech.common.device.commands.dao.CommandRequestExecutionDao;
import com.cannontech.common.device.commands.dao.model.CommandRequestExecution;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.DateRowMapper;
import com.cannontech.database.RowAndFieldMapper;
import com.cannontech.database.SimpleTableAccessTemplate;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.incrementer.NextValueHelper;

public class CommandRequestExecutionDaoImpl implements CommandRequestExecutionDao, InitializingBean {

	private static final RowAndFieldMapper<CommandRequestExecution> rowAndFieldMapper;
    private YukonJdbcTemplate yukonJdbcTemplate;
    private NextValueHelper nextValueHelper;
    private SimpleTableAccessTemplate<CommandRequestExecution> template;
    
    static {
		rowAndFieldMapper = new CommandRequestExecutionRowAndFieldMapper();
    }
    
    // SAVE OR UPDATE
    public void saveOrUpdate(CommandRequestExecution commandRequestExecution) {
    	template.save(commandRequestExecution);
    }
    
    // GET BY ID
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public CommandRequestExecution getById(final int commandRequestExecutionId) {
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT CRE.* FROM CommandRequestExec CRE WHERE CommandRequestExecId = ").appendArgument(commandRequestExecutionId);
        
    	CommandRequestExecution commandRequestExecution = yukonJdbcTemplate.queryForObject(sql, rowAndFieldMapper);
        return commandRequestExecution;
    }
    
    // BY CONTEXTID
    public List<CommandRequestExecution> getCresByContextId(int commandRequestExecutionContextId) {
    	
    	SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT CRE.* FROM CommandRequestExec CRE");
        sql.append("WHERE CRE.CommandRequestExecContextId =").append(commandRequestExecutionContextId);
        
        return yukonJdbcTemplate.query(sql.getSql(), rowAndFieldMapper, sql.getArguments());
    }
    
    // IN_PROGRESS CRES
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<CommandRequestExecution> getAllByStatus(CommandRequestExecutionStatus commandRequestExecutionStatus) {
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT CRE.* FROM CommandRequestExec CRE WHERE ExecutionStatus = ").appendArgument(commandRequestExecutionStatus);
        
    	List<CommandRequestExecution> cres = yukonJdbcTemplate.query(sql, rowAndFieldMapper);
        return cres;
    }
    
    // BY RANGE
    public List<CommandRequestExecution> findByRange(int commandRequestExecutionId, Date beginTime, Date endTime, DeviceRequestType type, boolean ascending) {
    	
    	SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT CRE.* FROM CommandRequestExec CRE");
        
        if (commandRequestExecutionId > 0) {
    		sql.append("WHERE CommandRequestExecId = ").appendArgument(commandRequestExecutionId);
        } else {
        	sql.append("WHERE 1 = 1");
        }
        
        if (beginTime != null) {
        	sql.append("AND CRE.StartTime >= ").appendArgument(beginTime);
        }
        
        if (endTime != null) {
        	sql.append("AND CRE.StartTime <= ").appendArgument(endTime);
        }
        
        if (type != null) {
        	sql.append("AND CRE.CommandRequestExecType = ").appendArgument(type.name());
        }
        
        if (ascending) {
        	sql.append("ORDER BY CRE.StartTime");
        } else {
        	sql.append("ORDER BY CRE.StartTime DESC");
        }
        
        return yukonJdbcTemplate.query(sql.getSql(), rowAndFieldMapper, sql.getArguments());
    	
    }
    
    // REQUEST COUNT
    public int getRequestCountByCreId(int commandRequestExecutionId) {
    	
    	String sql = "SELECT CRE.RequestCount FROM CommandRequestExec CRE WHERE CRE.CommandRequestExecId = ?";
    	return yukonJdbcTemplate.queryForInt(sql, commandRequestExecutionId);
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
    	return yukonJdbcTemplate.queryForObject(sql, new DateRowMapper(), commandRequestExecutionId);
    }
    
    public void afterPropertiesSet() throws Exception {
    	template = new SimpleTableAccessTemplate<CommandRequestExecution>(yukonJdbcTemplate, nextValueHelper);
    	template.setTableName("CommandRequestExec");
    	template.setPrimaryKeyField("CommandRequestExecId");
    	template.setFieldMapper(rowAndFieldMapper); 
    }
    
    @Autowired
    public void setNextValueHelper(NextValueHelper nextValueHelper) {
		this.nextValueHelper = nextValueHelper;
	}
    @Autowired
    public void setYukonJdbcTemplate(YukonJdbcTemplate yukonJdbcTemplate) {
        this.yukonJdbcTemplate = yukonJdbcTemplate;
    }
	
}
