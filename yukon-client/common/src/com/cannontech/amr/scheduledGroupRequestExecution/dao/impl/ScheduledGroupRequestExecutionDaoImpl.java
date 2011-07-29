package com.cannontech.amr.scheduledGroupRequestExecution.dao.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import com.cannontech.amr.scheduledGroupRequestExecution.dao.ScheduleGroupRequestExecutionDaoEnabledFilter;
import com.cannontech.amr.scheduledGroupRequestExecution.dao.ScheduleGroupRequestExecutionDaoOnetimeFilter;
import com.cannontech.amr.scheduledGroupRequestExecution.dao.ScheduleGroupRequestExecutionDaoPendingFilter;
import com.cannontech.amr.scheduledGroupRequestExecution.dao.ScheduledGroupRequestExecutionDao;
import com.cannontech.amr.scheduledGroupRequestExecution.dao.ScheduledGroupRequestExecutionStatus;
import com.cannontech.amr.scheduledGroupRequestExecution.dao.model.ScheduledGroupRequestExecutionPair;
import com.cannontech.amr.scheduledGroupRequestExecution.tasks.ScheduledGroupRequestExecutionTask;
import com.cannontech.common.device.DeviceRequestType;
import com.cannontech.common.device.commands.CommandRequestExecutionContextId;
import com.cannontech.common.device.commands.CommandRequestExecutionStatus;
import com.cannontech.common.device.commands.dao.impl.CommandRequestExecutionRowAndFieldMapper;
import com.cannontech.common.device.commands.dao.model.CommandRequestExecution;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.FieldMapper;
import com.cannontech.database.SimpleTableAccessTemplate;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.incrementer.NextValueHelper;
import com.cannontech.jobs.dao.ScheduledRepeatingJobDao;
import com.cannontech.jobs.dao.impl.JobDisabledStatus;
import com.cannontech.jobs.model.ScheduledRepeatingJob;
import com.cannontech.jobs.service.JobManager;
import com.cannontech.jobs.support.ScheduleException;
import com.cannontech.jobs.support.YukonJobDefinition;
import com.google.common.collect.Lists;

public class ScheduledGroupRequestExecutionDaoImpl implements ScheduledGroupRequestExecutionDao, InitializingBean {

	private ScheduledRepeatingJobDao scheduledRepeatingJobDao;
	private JobManager jobManager;
	
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
    @Override
	public List<ScheduledRepeatingJob> getJobs(int jobId, 
												Date startTime, 
												Date stopTime, 
												List<DeviceRequestType> types, 
												ScheduleGroupRequestExecutionDaoEnabledFilter enabled, 
												ScheduleGroupRequestExecutionDaoPendingFilter pending,
												ScheduleGroupRequestExecutionDaoOnetimeFilter onetime,
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
        		for (DeviceRequestType type : types) {
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
	    
    	List<ScheduledRepeatingJob> potentialJobs = yukonJdbcTemplate.query(sql, scheduledRepeatingJobDao.getJobRowMapper());
    	List<ScheduledRepeatingJob> jobs = Lists.newArrayListWithExpectedSize(potentialJobs.size());
    	
    	for (ScheduledRepeatingJob job : potentialJobs) {
    		
    		Date nextRun = null;
			try {
				nextRun = this.jobManager.getNextRuntime(job, new Date());
			} catch (ScheduleException e) {
				// has no next run time, leave as null
			}
			
			if (onetime.equals(ScheduleGroupRequestExecutionDaoOnetimeFilter.INCLUDE_ONETIME) || nextRun != null) {
				jobs.add(job);
			}
    	}
    	
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
        
        List<CommandRequestExecution> cres = yukonJdbcTemplate.query(sql, new CommandRequestExecutionRowAndFieldMapper());
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
        
        int creCount = yukonJdbcTemplate.queryForInt(sql);
    	return creCount;
    }
	
    public int getLatestFailCountByJobId(int jobId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT INSIDER.failCount FROM (");
        sql.append(     "SELECT COUNT(CRER.CommandRequestExecResultId) AS failCount, ROW_NUMBER() OVER (ORDER BY CRE.CommandRequestExecId DESC) RN");
        sql.append(     "FROM CommandRequestExec CRE");
        sql.append(     "JOIN ScheduledGrpCommandRequest SGCR ON (SGCR.CommandRequestExecContextId = CRE.CommandRequestExecContextId)");
        sql.append(     "JOIN CommandRequestExecResult CRER ON (CRE.CommandRequestExecId = CRER.CommandRequestExecId)");
        sql.append(     "WHERE CRE.CommandRequestExecContextId = (");
        sql.append(         "SELECT INSIDER2.CommandRequestExecContextId FROM (");
        sql.append(             "SELECT CRE2.CommandRequestExecContextId, ROW_NUMBER() OVER (ORDER BY CRE2.StartTime DESC) RN2");
        sql.append(             "FROM ScheduledGrpCommandRequest SGCR2");
        sql.append(             "JOIN CommandRequestExec CRE2 ON (SGCR2.CommandRequestExecContextId = CRE2.CommandRequestExecContextId)");
        sql.append(             "WHERE SGCR2.JobID").eq(jobId);
        sql.append(         ") INSIDER2");
        sql.append(         "WHERE INSIDER2.RN2 = 1)");
        sql.append(     "AND CRER.ErrorCode > 0");
        sql.append(     "GROUP BY CRE.CommandRequestExecId, CRER.CommandRequestExecId");
        sql.append(") INSIDER");
        sql.append("WHERE INSIDER.RN = 1");
        
        int result = 0;
        try {
            result = yukonJdbcTemplate.queryForInt(sql);
        } catch (EmptyResultDataAccessException e){}
        return result;
    }
    
    public int getLatestSuccessCountByJobId(int jobId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT COUNT(CRER.CommandRequestExecResultId) AS successCount");
        sql.append("FROM CommandRequestExec CRE");
        sql.append("JOIN ScheduledGrpCommandRequest SGCR ON (SGCR.CommandRequestExecContextId = CRE.CommandRequestExecContextId)");
        sql.append("JOIN CommandRequestExecResult CRER ON (CRE.CommandRequestExecId = CRER.CommandRequestExecId)");
        sql.append("WHERE CRE.CommandRequestExecContextId = (");
        sql.append(     "SELECT INSIDER.CommandRequestExecContextId FROM (");
        sql.append(         "SELECT CRE.CommandRequestExecContextId, ROW_NUMBER() OVER (ORDER BY CRE.StartTime DESC) RN");
        sql.append(         "FROM ScheduledGrpCommandRequest SGCR");
        sql.append(         "JOIN CommandRequestExec CRE ON (SGCR.CommandRequestExecContextId = CRE.CommandRequestExecContextId)");
        sql.append(         "WHERE SGCR.JobID").eq(jobId);
        sql.append(     ") INSIDER");
        sql.append(     "WHERE INSIDER.RN = 1)");
        sql.append("AND CRER.ErrorCode = 0");
        
        int result = 0;
        try {
            result = yukonJdbcTemplate.queryForInt(sql);
        } catch (EmptyResultDataAccessException e) {}
        return result;
    }
    
    public int getLatestRequestCountByJobId(int jobId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT MAX(CRE.RequestCount)");
        sql.append("FROM CommandRequestExec CRE");
        sql.append("WHERE CRE.CommandRequestExecContextId = (");
        sql.append(     "SELECT INSIDER.CommandRequestExecContextId FROM (");
        sql.append(         "SELECT CRE.CommandRequestExecContextId, ROW_NUMBER() OVER (ORDER BY CRE.StartTime DESC) RN");
        sql.append(         "FROM ScheduledGrpCommandRequest SGCR");
        sql.append(         "JOIN CommandRequestExec CRE ON (SGCR.CommandRequestExecContextId = CRE.CommandRequestExecContextId)");
        sql.append(         "WHERE SGCR.JobID").eq(jobId);
        sql.append(     ") INSIDER");
        sql.append(     "WHERE INSIDER.RN = 1)");
        
        int result = 0;
        try {
            result = yukonJdbcTemplate.queryForInt(sql);
        } catch (EmptyResultDataAccessException e) {}
        return result;
    }
    
    public ScheduledGroupRequestExecutionStatus getStatusByJobId(int jobId) {
    	JobDisabledStatus jobDisabledStatus = jobManager.getJobDisabledStatus(jobId);
		
		if (jobDisabledStatus == JobDisabledStatus.D) {
			return ScheduledGroupRequestExecutionStatus.DELETED;
		} else if (jobDisabledStatus == JobDisabledStatus.Y) {
			return ScheduledGroupRequestExecutionStatus.DISABLED;
		} else {
			
			CommandRequestExecution latestCre = findLatestCommandRequestExecutionForJobId(jobId, null);
			if (latestCre != null) {
				
				Date now = new Date();
				Date startTime = latestCre.getStartTime();
				Date stopTime = latestCre.getStopTime();
				CommandRequestExecutionStatus commandRequestExecutionStatus = latestCre.getCommandRequestExecutionStatus();
				
				if (startTime != null && now.compareTo(startTime) >= 0 && (stopTime == null || now.compareTo(stopTime) <= 0) && commandRequestExecutionStatus != CommandRequestExecutionStatus.FAILED) {
					return ScheduledGroupRequestExecutionStatus.RUNNING;
				}
			}
		}
		
		return ScheduledGroupRequestExecutionStatus.ENABLED;
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
        template = 
            new SimpleTableAccessTemplate<ScheduledGroupRequestExecutionPair>(yukonJdbcTemplate, 
                                                                               nextValueHelper);
        template.setTableName("ScheduledGrpCommandRequest");
        template.setPrimaryKeyField("CommandRequestExecContextId");
        template.setFieldMapper(fieldMapper); 
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
	@Resource(name="jobManager")
	public void setJobManager(JobManager jobManager) {
		this.jobManager = jobManager;
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
}
