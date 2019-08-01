package com.cannontech.common.device.commands.dao.impl;

import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;

import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.common.device.DeviceRequestType;
import com.cannontech.common.device.commands.CommandRequestExecutionContextId;
import com.cannontech.common.device.commands.CommandRequestExecutionStatus;
import com.cannontech.common.device.commands.CommandRequestType;
import com.cannontech.common.device.commands.dao.CommandRequestExecutionDao;
import com.cannontech.common.device.commands.dao.model.CommandRequestExecution;
import com.cannontech.common.model.PagingParameters;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.DateRowMapper;
import com.cannontech.database.RowAndFieldMapper;
import com.cannontech.database.SimpleTableAccessTemplate;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.incrementer.NextValueHelper;

public class CommandRequestExecutionDaoImpl implements CommandRequestExecutionDao {

    private static final RowAndFieldMapper<CommandRequestExecution> rowAndFieldMapper;
    @Autowired YukonJdbcTemplate yukonJdbcTemplate;
    @Autowired NextValueHelper nextValueHelper;
    private SimpleTableAccessTemplate<CommandRequestExecution> template;
    
    static {
        rowAndFieldMapper = new CommandRequestExecutionRowAndFieldMapper();
    }
    
    // SAVE OR UPDATE
    @Override
    public void saveOrUpdate(CommandRequestExecution commandRequestExecution) {
    	template.save(commandRequestExecution);
    }
    
    // GET BY ID
    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public CommandRequestExecution getById(final int commandRequestExecutionId) {
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT CRE.* FROM CommandRequestExec CRE WHERE CommandRequestExecId = ").appendArgument(commandRequestExecutionId);
        
        CommandRequestExecution commandRequestExecution = yukonJdbcTemplate.queryForObject(sql, rowAndFieldMapper);
        return commandRequestExecution;
    }
    
    // BY CONTEXTID
    @Override
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
    
    @Override
    public List<CommandRequestExecution> findByRange(PagingParameters paging, int jobId, Instant from, Instant to,
                                                     DeviceRequestType type) {
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT CommandRequestExecId, StartTime, StopTime, RequestCount, CommandRequestExecType, Username,");
        sql.append("CommandRequestExecContextId, ExecutionStatus, CommandRequestType");
        sql.append("FROM (SELECT cre.CommandRequestExecId, cre.StartTime, cre.StopTime,  cre.RequestCount, cre.CommandRequestExecType, ");
        sql.append("             cre.Username, cre.CommandRequestExecContextId, cre.ExecutionStatus, cre.CommandRequestType, ");
        sql.append("             ROW_NUMBER() OVER (ORDER BY cre.StartTime DESC) AS counter");
        sql.append("      FROM CommandRequestExec cre");
        if (jobId > 0) {
            sql.append(" JOIN ScheduledGrpCommandRequest sgcr ON sgcr.CommandRequestExecContextId = cre.CommandRequestExecContextId");
        }
        sql.append("      WHERE cre.StartTime").gte(from);
        sql.append("        AND cre.StartTime").lte(to);
        if (jobId > 0) {
            sql.append("    AND sgcr.JobId IN ");
            sql.append("(SELECT jobId FROM Job WHERE JobGroupId = (SELECT JobGroupId FROM Job WHERE jobId").eq(jobId);
            sql.append("))");
        }
        if (type != null) {
            sql.append("    AND cre.CommandRequestExecType").eq_k(type);
        }
        sql.append(") tbl");
        sql.append("WHERE tbl.counter between").append(paging.getOneBasedStartIndex());
        sql.append("AND").append(paging.getOneBasedEndIndex());
              
        List<CommandRequestExecution> executions = yukonJdbcTemplate.query(sql, rowAndFieldMapper);
                
        return executions;
    }
    
    @Override
    public int getByRangeCount(int jobId, Instant from, Instant to, DeviceRequestType type) {

        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT COUNT(*)");
        sql.append("FROM CommandRequestExec cre");
        if (jobId > 0) {
            sql.append("JOIN ScheduledGrpCommandRequest sgcr ON sgcr.CommandRequestExecContextId = cre.CommandRequestExecContextId");
        }
        sql.append("  WHERE cre.StartTime").gte(from);
        sql.append("    AND cre.StartTime").lte(to);
        if (jobId > 0) {
            sql.append("    AND sgcr.JobId IN ");
            sql.append("(SELECT jobId FROM Job WHERE JobGroupId = (SELECT JobGroupId FROM Job WHERE jobId").eq(jobId);
            sql.append("))");
        }
        if (type != null) {
            sql.append("AND cre.CommandRequestExecType").eq_k(type);
        }

        return yukonJdbcTemplate.queryForInt(sql);
    }
    
    // REQUEST COUNT
    @Override
    public int getRequestCountByCreId(int commandRequestExecutionId) {

        final SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT CRE.RequestCount");
        sql.append("FROM CommandRequestExec CRE");
        sql.append("WHERE CRE.CommandRequestExecId").eq(
            commandRequestExecutionId);
        Integer requestCount = yukonJdbcTemplate.queryForInt(sql);

        return requestCount;
    }
    
    // IS COMPLETE
    @Override
    public boolean isComplete(int commandRequestExecutionId) {
        Date stopTime = queryForStopTime(commandRequestExecutionId);
        if (stopTime == null) {
            return false;
        }
        return true;
    }
    
    // STOP TIME
    @Override
    public Date getStopTime(int commandRequestExecutionId) {
        return queryForStopTime(commandRequestExecutionId);
    }
    
    private Date queryForStopTime(int commandRequestExecutionId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT CRE.StopTime FROM CommandRequestExec CRE WHERE CRE.CommandRequestExecId").eq(commandRequestExecutionId);
        return yukonJdbcTemplate.queryForObject(sql, new DateRowMapper());
    }
    
    @PostConstruct
    public void init() throws Exception {
        template = new SimpleTableAccessTemplate<>(yukonJdbcTemplate, nextValueHelper);
        template.setTableName("CommandRequestExec");
        template.setPrimaryKeyField("CommandRequestExecId");
        template.setFieldMapper(rowAndFieldMapper);
    }
    
    @Override
    public CommandRequestExecution createStartedExecution(CommandRequestType commandType, DeviceRequestType deviceType,
            int requestCount, LiteYukonUser user) {
        CommandRequestExecutionContextId contextId =
            new CommandRequestExecutionContextId(nextValueHelper.getNextValue("CommandRequestExec"));
        return createStartedExecution(commandType, deviceType, contextId.getId(), requestCount, user);
    }

    @Override
    public CommandRequestExecution createStartedExecution(CommandRequestType commandType, DeviceRequestType deviceType,
            int contextId, int requestCount, LiteYukonUser user) {
        CommandRequestExecution execution = new CommandRequestExecution();
        execution.setContextId(contextId);
        execution.setStartTime(new Date());
        execution.setRequestCount(requestCount);
        execution.setCommandRequestExecutionType(deviceType);
        execution.setUserName(user.getUsername());
        execution.setCommandRequestType(commandType);
        execution.setCommandRequestExecutionStatus(CommandRequestExecutionStatus.STARTED);
        // TODO YUK-20330 Investigate "connect" message causing table locking
        // If the DeviceRequestType is the new METER_DR_CONNECT_DISCONNECT_COMMAND used only by the MeterCommandStradegy opt out connect and unenrollment connect, don't do this, it locks the db tables.
        if(deviceType != DeviceRequestType.METER_DR_CONNECT_DISCONNECT_COMMAND) {
            saveOrUpdate(execution);
        }
        return execution;
    }
}
