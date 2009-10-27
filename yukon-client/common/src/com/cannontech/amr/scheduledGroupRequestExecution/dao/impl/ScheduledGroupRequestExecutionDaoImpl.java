package com.cannontech.amr.scheduledGroupRequestExecution.dao.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

import com.cannontech.amr.scheduledGroupRequestExecution.dao.ScheduleGroupRequestExecutionDaoEnabledFilter;
import com.cannontech.amr.scheduledGroupRequestExecution.dao.ScheduleGroupRequestExecutionDaoPendingFilter;
import com.cannontech.amr.scheduledGroupRequestExecution.dao.ScheduledGroupRequestExecutionDao;
import com.cannontech.amr.scheduledGroupRequestExecution.dao.model.ScheduledGroupRequestExecutionPair;
import com.cannontech.amr.scheduledGroupRequestExecution.tasks.ScheduledGroupRequestExecutionTask;
import com.cannontech.common.device.commands.CommandRequestExecutionContextId;
import com.cannontech.common.device.commands.CommandRequestExecutionType;
import com.cannontech.common.device.commands.dao.CommandRequestExecutionDao;
import com.cannontech.common.device.commands.dao.CommandRequestExecutionResultDao;
import com.cannontech.common.device.commands.dao.impl.CommandRequestExecutionRowAndFieldMapper;
import com.cannontech.common.device.commands.dao.model.CommandRequestExecution;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.FieldMapper;
import com.cannontech.database.IntegerRowMapper;
import com.cannontech.database.SimpleTableAccessTemplate;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.incrementer.NextValueHelper;
import com.cannontech.jobs.dao.ScheduledRepeatingJobDao;
import com.cannontech.jobs.dao.impl.JobDisabledStatus;
import com.cannontech.jobs.model.ScheduledRepeatingJob;
import com.cannontech.jobs.support.YukonJobDefinition;

public class ScheduledGroupRequestExecutionDaoImpl implements ScheduledGroupRequestExecutionDao, InitializingBean {

	private ScheduledRepeatingJobDao scheduledRepeatingJobDao;
	private CommandRequestExecutionDao commandRequestExecutionDao;
	private CommandRequestExecutionResultDao commandRequestExecutionResultDao;
	
    private SimpleJdbcTemplate simpleJdbcTemplate;
    private YukonJdbcTemplate yukonJdbcTemplate;
    private NextValueHelper nextValueHelper;
    private SimpleTableAccessTemplate<ScheduledGroupRequestExecutionPair> template;
    private YukonJobDefinition<ScheduledGroupRequestExecutionTask> scheduledGroupRequestExecutionJobDefinition;
    
    // INSERT
    public void insert(ScheduledGroupRequestExecutionPair pair) {
        template.insert(pair);
    }
    
    
    
    // LATEST CRE FOR JOB ID
    public CommandRequestExecution findLatestCommandRequestExecutionForJobId(int jobId, Date cutoff) {
    	
    	SqlStatementBuilder sql = new SqlStatementBuilder();
    	sql.append("SELECT CRE.* FROM ScheduledGrpCommandRequest SGCR");
    	sql.append("JOIN CommandRequestExec CRE ON (SGCR.CommandRequestExecContextId = CRE.CommandRequestExecContextId)");
    	sql.append("WHERE SGCR.JobID = ").appendArgument(jobId);
    	
    	if (cutoff != null) {
        	sql.append("AND CRE.StartTime <= ").appendArgument(cutoff);
        }
    	
    	sql.append("ORDER BY CRE.StartTime DESC");
    	
    	List<CommandRequestExecution> cres = yukonJdbcTemplate.queryForLimitedResults(sql, new CommandRequestExecutionRowAndFieldMapper(), 1);
    	
    	if (cres.size() > 0) {
    		return cres.get(0);
    	} else {
    		return null;
    	}
    }
    
    // GET JOBS BY RANGE
	public List<ScheduledRepeatingJob> getJobs(int jobId, 
												Date startTime, 
												Date stopTime, 
												List<CommandRequestExecutionType> types, 
												ScheduleGroupRequestExecutionDaoEnabledFilter enabled, 
												ScheduleGroupRequestExecutionDaoPendingFilter pending,
												boolean acsending) {
		
		SqlStatementBuilder sql = new SqlStatementBuilder();
    	sql.append("SELECT DISTINCT Job.*, JSR.* FROM Job");
        sql.append("JOIN JobScheduledRepeating JSR ON (Job.JobId = JSR.JobId)");
        sql.append("LEFT JOIN JobStatus JS ON (Job.JobId = JS.JobId)");
        if (types != null) {
        	sql.append("INNER JOIN JOBPROPERTY JP ON (Job.JobId = JP.JobId)");
        }
        
        sql.append("WHERE Job.BeanName = ").appendArgument(scheduledGroupRequestExecutionJobDefinition.getName());
        
        // single job
        if (jobId > 0) {
    		sql.append("AND Job.JobId = ").appendArgument(jobId);
        }
        
        // start stop / pending (start time will get ignored if PENDING_ONLY is set)
        if (startTime != null) {
        	
        	if (pending.equals(ScheduleGroupRequestExecutionDaoPendingFilter.ANY)) {
        		
        		sql.append("AND (JS.StartTime >= ").appendArgument(startTime).append(" OR JS.StartTime IS NULL)");
        	
        	} else if (pending.equals(ScheduleGroupRequestExecutionDaoPendingFilter.PENDING_ONLY)) {
        		
        		sql.append("AND JS.StartTime IS NULL");
        	
        	} else if (pending.equals(ScheduleGroupRequestExecutionDaoPendingFilter.EXECUTED_ONLY)) {
        		
        		sql.append("AND JS.StartTime >= ").appendArgument(startTime);
        	}
        	
        } else {
        	
        	if (pending.equals(ScheduleGroupRequestExecutionDaoPendingFilter.PENDING_ONLY)) {
        		
        		sql.append("AND JS.StartTime IS NULL");
        	
        	} else if (pending.equals(ScheduleGroupRequestExecutionDaoPendingFilter.EXECUTED_ONLY)) {
        		
        		sql.append("AND JS.StartTime IS NOT NULL");
        	}
        }
        
        // stop time (stop time will get ignored if PEDNING_ONLY is set)
        if (stopTime != null) {
        	
        	if (pending.equals(ScheduleGroupRequestExecutionDaoPendingFilter.ANY)) {
        		
        		sql.append("AND (JS.StopTime < ").appendArgument(stopTime).append(" OR JS.StopTime IS NULL)");
        	
        	} else if (pending.equals(ScheduleGroupRequestExecutionDaoPendingFilter.PENDING_ONLY)) {
        		
        		sql.append("AND JS.StopTime IS NULL");
        	
        	} else if (pending.equals(ScheduleGroupRequestExecutionDaoPendingFilter.EXECUTED_ONLY)) {
        		
        		sql.append("AND JS.StopTime < ").appendArgument(stopTime);
        	}

        } else {
        	
        	// stop time can be whatever unless PENDING_ONLY is set, we know it can only have a null value
        	// (although the start time check should have already excluded the record)
        	if (pending.equals(ScheduleGroupRequestExecutionDaoPendingFilter.PENDING_ONLY)) {
        		
        		sql.append("AND JS.StopTime IS NULL");
        	
        	}
        }
        
        // enabled disabled status
        sql.append("AND Job.Disabled != ").appendArgument(JobDisabledStatus.D.name());
        if (enabled.equals(ScheduleGroupRequestExecutionDaoEnabledFilter.ENABLED_ONLY)) {
        	sql.append("AND Job.Disabled = ").appendArgument(JobDisabledStatus.N.name());
        }
        else if (enabled.equals(ScheduleGroupRequestExecutionDaoEnabledFilter.DISABLED_ONLY)) {
        	sql.append("AND Job.Disabled = ").appendArgument(JobDisabledStatus.Y.name());
        }
        
        // type
        if (types != null) {
        	
        	if (types.size() == 1) {
        		
        		sql.append("AND JP.Value = ").appendArgument(types.get(0).name());
        	
        	} else if (types.size() > 1) {
        		
        		sql.append("AND JP.Value IN (");
        		
        		List<String> typeStrs = new ArrayList<String>();
        		for (CommandRequestExecutionType type : types) {
        			typeStrs.add(type.name());
        		}
        		
            	sql.appendArgumentList(typeStrs);
            	
            	sql.append(")");
        		
        	}
        	
        }
        
        // order
        if (acsending) {
        	sql.append("ORDER BY Job.JobID");
        } else {
        	sql.append("ORDER BY Job.JobID DESC");
        }
	    
    	List<ScheduledRepeatingJob> jobs = simpleJdbcTemplate.query(sql.getSql(), scheduledRepeatingJobDao.getJobRowMapper(), sql.getArguments());
    	return jobs;
	}
	
    
    // GET CRES BY JOB ID
    public List<CommandRequestExecution> getCommandRequestExecutionsByJobId(int jobId, Date startTime, Date stopTime, boolean acsending) {
    	
    	SqlStatementBuilder sql = new SqlStatementBuilder();
    	sql.append("SELECT CRE.* FROM ScheduledGrpCommandRequest SGCR");
        sql.append("JOIN CommandRequestExec CRE ON (SGCR.CommandRequestExecContextId = CRE.CommandRequestExecContextId)");
        
        if (jobId > 0) {
    		sql.append("WHERE SGCR.JobId = ").appendArgument(jobId);
        } else {
        	sql.append("WHERE SGCR.JobId > 0");
        }
    	
        if (startTime != null) {
        	sql.append("AND CRE.StartTime >= ").appendArgument(startTime);
        }
        
        if (stopTime != null) {
        	sql.append("AND CRE.StartTime <= ").appendArgument(stopTime);
        }
    	
        if (acsending) {
        	sql.append("ORDER BY CRE.StartTime");
        } else {
        	sql.append("ORDER BY CRE.StartTime DESC");
        }
        
        List<CommandRequestExecution> cres = simpleJdbcTemplate.query(sql.getSql(), new CommandRequestExecutionRowAndFieldMapper(), sql.getArguments());
    	return cres;
    }
    
    // CRE COUNT BY JOB ID
    public int getDistinctCreCountByJobId(int jobId, Date startTime, Date stopTime) {
    	
    	SqlStatementBuilder sql = new SqlStatementBuilder();
    	sql.append("SELECT COUNT(DISTINCT CRE.CommandRequestExecContextId) AS distinctCreCount");
    	sql.append("FROM ScheduledGrpCommandRequest SGCR");
        sql.append("JOIN CommandRequestExec CRE ON (SGCR.CommandRequestExecContextId = CRE.CommandRequestExecContextId)");
        
        if (jobId > 0) {
    		sql.append("WHERE SGCR.JobId = ").appendArgument(jobId);
        } else {
        	sql.append("WHERE SGCR.JobId > 0");
        }
    	
        if (startTime != null) {
        	sql.append("AND CRE.StartTime >= ").appendArgument(startTime);
        }
        
        if (stopTime != null) {
        	sql.append("AND CRE.StartTime <= ").appendArgument(stopTime);
        }
        
        int creCount = simpleJdbcTemplate.queryForInt(sql.getSql(), sql.getArguments());
    	return creCount;
    }
	
    public int getLatestFailCountByJobId(int jobId) {
    	
    	CommandRequestExecution latestCre = findLatestCommandRequestExecutionForJobId(jobId, null);
    	if (latestCre == null) {
    		return 0;
    	}
    	
    	// if the context has only a single cre look to it for fail count
    	List<CommandRequestExecution> cres = commandRequestExecutionDao.getCresByContextId(latestCre.getContextId());
    	if(cres.size() == 1) {
    		return commandRequestExecutionResultDao.getFailCountByExecutionId(cres.get(0).getId());
    	}
    	
    	// get fail count of most recent cre for context (lastest retry)
    	SqlStatementBuilder sql = new SqlStatementBuilder();
    	sql.append("SELECT COUNT(CRER.CommandRequestExecResultId) AS failCount");
		sql.append("FROM CommandRequestExec CRE");
		sql.append("JOIN CommandRequestExecResult CRER ON (CRE.CommandRequestExecId = CRER.CommandRequestExecId)");
		sql.append("WHERE CRE.CommandRequestExecContextId =").appendArgument(latestCre.getContextId());
		sql.append("AND CRE.StopTime IS NOT NULL");
		sql.append("AND CRER.ErrorCode > 0");
		sql.append("GROUP BY CRE.CommandRequestExecId, CRER.CommandRequestExecId");
		sql.append("ORDER BY CRE.CommandRequestExecId DESC");
    	
    	List<Integer> results = yukonJdbcTemplate.queryForLimitedResults(sql, new IntegerRowMapper(), 1);
    	if (results.size() == 0) {
    		return 0;
    	}
    	
    	return results.get(0);
    }
    
    public int getLatestSuccessCountByJobId(int jobId) {
    	
    	CommandRequestExecution latestCre = findLatestCommandRequestExecutionForJobId(jobId, null);
    	if (latestCre == null) {
    		return 0;
    	}
    	
    	SqlStatementBuilder sql = new SqlStatementBuilder();
    	sql.append("SELECT COUNT(CRER.CommandRequestExecResultId) AS successCount");
		sql.append("FROM CommandRequestExec CRE");
		sql.append("JOIN CommandRequestExecResult CRER ON (CRE.CommandRequestExecId = CRER.CommandRequestExecId)");
		sql.append("WHERE CRE.CommandRequestExecContextId =").appendArgument(latestCre.getContextId());
		sql.append("AND CRER.ErrorCode = 0");
		
		int successCount = simpleJdbcTemplate.queryForInt(sql.getSql(), sql.getArguments());
    	return successCount;
    }
    
    public int getLatestRequestCountByJobId(int jobId) {
    	
    	CommandRequestExecution latestCre = findLatestCommandRequestExecutionForJobId(jobId, null);
        if (latestCre == null) {
        	return 0;
        }
        
    	String sql = "SELECT MAX(CRE.RequestCount) FROM CommandRequestExec CRE WHERE CRE.CommandRequestExecContextId = ?";
    	return yukonJdbcTemplate.queryForInt(sql, latestCre.getContextId());
    }

	private FieldMapper<ScheduledGroupRequestExecutionPair> fieldMapper = new FieldMapper<ScheduledGroupRequestExecutionPair>() {
        public void extractValues(MapSqlParameterSource p, ScheduledGroupRequestExecutionPair pair) {
            p.addValue("JobId", pair.getJobId());
            
        }
        public Number getPrimaryKey(ScheduledGroupRequestExecutionPair pair) {
            return pair.getCommandRequestExecutionContextId().getId();
        }
        public void setPrimaryKey(ScheduledGroupRequestExecutionPair pair, int value) {
        	pair.setCommandRequestExecutionContextId(new CommandRequestExecutionContextId(value));
        }
    };
    
	public void afterPropertiesSet() throws Exception {
        template = new SimpleTableAccessTemplate<ScheduledGroupRequestExecutionPair>(simpleJdbcTemplate, nextValueHelper);
        template.withTableName("ScheduledGrpCommandRequest");
        template.withPrimaryKeyField("CommandRequestExecContextId");
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
	@Autowired
	public void setScheduledRepeatingJobDao(
			ScheduledRepeatingJobDao scheduledRepeatingJobDao) {
		this.scheduledRepeatingJobDao = scheduledRepeatingJobDao;
	}
	@Autowired
    public void setYukonJdbcTemplate(YukonJdbcTemplate yukonJdbcTemplate) {
        this.yukonJdbcTemplate = yukonJdbcTemplate;
    }
	@Resource(name="scheduledGroupRequestExecutionJobDefinition")
    public void setScheduledGroupRequestExecutionjobDefinition(
            YukonJobDefinition<ScheduledGroupRequestExecutionTask> scheduledGroupRequestExecutionJobDefinition) {
        this.scheduledGroupRequestExecutionJobDefinition = scheduledGroupRequestExecutionJobDefinition;
    }
	
	@Autowired
	public void setCommandRequestExecutionDao(CommandRequestExecutionDao commandRequestExecutionDao) {
		this.commandRequestExecutionDao = commandRequestExecutionDao;
	}
	
	@Autowired
	public void setCommandRequestExecutionResultDao(CommandRequestExecutionResultDao commandRequestExecutionResultDao) {
		this.commandRequestExecutionResultDao = commandRequestExecutionResultDao;
	}
}
