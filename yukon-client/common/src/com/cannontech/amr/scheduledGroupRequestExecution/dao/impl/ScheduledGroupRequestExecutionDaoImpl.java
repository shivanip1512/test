package com.cannontech.amr.scheduledGroupRequestExecution.dao.impl;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import com.cannontech.amr.scheduledGroupRequestExecution.dao.ScheduleGroupRequestExecutionDaoEnabledFilter;
import com.cannontech.amr.scheduledGroupRequestExecution.dao.ScheduleGroupRequestExecutionDaoPendingFilter;
import com.cannontech.amr.scheduledGroupRequestExecution.dao.ScheduledGroupRequestExecutionDao;
import com.cannontech.amr.scheduledGroupRequestExecution.dao.ScheduledGroupRequestExecutionStatus;
import com.cannontech.amr.scheduledGroupRequestExecution.dao.model.ScheduledGroupExecutionCounts;
import com.cannontech.amr.scheduledGroupRequestExecution.dao.model.ScheduledGroupRequestExecutionPair;
import com.cannontech.amr.scheduledGroupRequestExecution.tasks.ScheduledGroupRequestExecutionTask;
import com.cannontech.common.device.DeviceRequestType;
import com.cannontech.common.device.commands.CommandRequestExecutionContextId;
import com.cannontech.common.device.commands.CommandRequestExecutionStatus;
import com.cannontech.common.device.commands.dao.impl.CommandRequestExecutionRowAndFieldMapper;
import com.cannontech.common.device.commands.dao.model.CommandRequestExecution;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.database.FieldMapper;
import com.cannontech.database.SimpleTableAccessTemplate;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.database.incrementer.NextValueHelper;
import com.cannontech.jobs.dao.ScheduledRepeatingJobDao;
import com.cannontech.jobs.dao.impl.JobDisabledStatus;
import com.cannontech.jobs.model.JobStatus;
import com.cannontech.jobs.model.ScheduledRepeatingJob;
import com.cannontech.jobs.model.YukonJob;
import com.cannontech.jobs.service.JobManager;
import com.cannontech.jobs.support.YukonJobDefinition;

public class ScheduledGroupRequestExecutionDaoImpl implements ScheduledGroupRequestExecutionDao {

	private ScheduledRepeatingJobDao scheduledRepeatingJobDao;
	private JobManager jobManager;
	
    private YukonJdbcTemplate yukonJdbcTemplate;
    private NextValueHelper nextValueHelper;
    private SimpleTableAccessTemplate<ScheduledGroupRequestExecutionPair> template;
    private YukonJobDefinition<ScheduledGroupRequestExecutionTask> scheduledGroupRequestExecutionJobDefinition;
    
    private static YukonRowMapper<ScheduledGroupExecutionCounts> executionRowMapper = 
        new YukonRowMapper<ScheduledGroupExecutionCounts>() {
        @Override
        public ScheduledGroupExecutionCounts mapRow(YukonResultSet rs) throws SQLException {
            int failureCount = rs.getInt("failureCount");
            int successCount = rs.getInt("successCount");
            int totalCount = rs.getInt("totalCount");

            ScheduledGroupExecutionCounts exec =
                new ScheduledGroupExecutionCounts(failureCount, successCount, totalCount);
            return exec;
        }
    };
    
    // INSERT
    @Override
    public void insert(ScheduledGroupRequestExecutionPair pair) {
        template.insert(pair);
    }
    
    // LATEST CRE FOR JOB ID
    @Override
    public CommandRequestExecution findLatestCommandRequestExecutionForJobId(int jobId, Date cutoff) {
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        
        sql.append("SELECT CRE2.*");
        sql.append("FROM (");
        sql.append("    SELECT"); 
        sql.append("        SGCR.JobId,");
        sql.append("        CRE.CommandRequestExecId,");
        sql.append("        ROW_NUMBER() OVER (ORDER BY CRE.StartTime DESC) RN"); 
        sql.append("    FROM ScheduledGrpCommandRequest SGCR"); 
        sql.append("    JOIN CommandRequestExec CRE"); 
        sql.append("        ON SGCR.CommandRequestExecContextId = CRE.CommandRequestExecContextId");
        sql.append("    WHERE SGCR.JobID = ").appendArgument(jobId);
        if (cutoff != null) {
            sql.append("AND CRE.StartTime <= ").appendArgument(cutoff);
        }
        sql.append(") LatestCre"); 
        sql.append("JOIN CommandRequestExec CRE2"); 
        sql.append("    ON LatestCre.CommandRequestExecId = CRE2.CommandRequestExecId"); 
        sql.append("WHERE LatestCre.RN = 1");
        
        List<CommandRequestExecution> cres = yukonJdbcTemplate.queryForLimitedResults(sql, new CommandRequestExecutionRowAndFieldMapper(), 1);
        
        if (cres.size() > 0) {
            return cres.get(0);
        }

        return null;
    }
    
    // GET JOBS BY RANGE
    @Override
	public List<ScheduledRepeatingJob> getJobs(int jobId, Date startTime, Date stopTime, List<DeviceRequestType> types, 
    		ScheduleGroupRequestExecutionDaoEnabledFilter enabled, ScheduleGroupRequestExecutionDaoPendingFilter pending,
    		boolean ascendingJobIds) {

		SqlStatementBuilder sql = new SqlStatementBuilder();
    	sql.append("SELECT DISTINCT Job.*, JSR.* FROM Job");
        sql.append("JOIN JobScheduledRepeating JSR ON (Job.JobId = JSR.JobId)");
        sql.append("LEFT JOIN JobStatus JS ON (Job.JobId = JS.JobId)");
        if (types != null) {
        	sql.append("INNER JOIN JOBPROPERTY JP ON (Job.JobId = JP.JobId)");
        }

        sql.append("WHERE Job.BeanName").eq(scheduledGroupRequestExecutionJobDefinition.getName());
        
        // single job
        if (jobId > 0) {
    		sql.append("AND Job.JobId").eq(jobId);
        }
        
        // start stop / pending (start time will get ignored if PENDING_ONLY is set)
        if (startTime != null) {
        	if (pending.equals(ScheduleGroupRequestExecutionDaoPendingFilter.ANY)) {
        		sql.append("AND (JS.StartTime").gte(startTime).append(" OR JS.StartTime IS NULL)");
        	} else if (pending.equals(ScheduleGroupRequestExecutionDaoPendingFilter.PENDING_ONLY)) {
        		sql.append("AND JS.StartTime IS NULL");
        	} else if (pending.equals(ScheduleGroupRequestExecutionDaoPendingFilter.EXECUTED_ONLY)) {
        		sql.append("AND JS.StartTime").gte(startTime);
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
        		sql.append("AND (JS.StopTime").lt(stopTime).append(" OR JS.StopTime IS NULL)");
        	} else if (pending.equals(ScheduleGroupRequestExecutionDaoPendingFilter.PENDING_ONLY)) {
        		sql.append("AND JS.StopTime IS NULL");
        	} else if (pending.equals(ScheduleGroupRequestExecutionDaoPendingFilter.EXECUTED_ONLY)) {
        		sql.append("AND JS.StopTime").lt(stopTime);
        	}
        } else {
        	// stop time can be whatever unless PENDING_ONLY is set, we know it can only have a null value
        	// (although the start time check should have already excluded the record)
        	if (pending.equals(ScheduleGroupRequestExecutionDaoPendingFilter.PENDING_ONLY)) {
        		sql.append("AND JS.StopTime IS NULL");
        	}
        }
        
        // enabled disabled status
        sql.append("AND Job.Disabled").neq(JobDisabledStatus.D.name());
        if (enabled.equals(ScheduleGroupRequestExecutionDaoEnabledFilter.ENABLED_ONLY)) {
        	sql.append("AND Job.Disabled").eq(JobDisabledStatus.N.name());
        }
        else if (enabled.equals(ScheduleGroupRequestExecutionDaoEnabledFilter.DISABLED_ONLY)) {
        	sql.append("AND Job.Disabled").eq(JobDisabledStatus.Y.name());
        }
        
        // type
        if (types != null) {
        	if (types.size() == 1) {
        		sql.append("AND JP.Value").eq(types.get(0).name());
        	} else if (types.size() > 1) {
        		
        		sql.append("AND JP.Value IN (");
        		
        		List<String> typeStrs = new ArrayList<>();
        		for (DeviceRequestType type : types) {
        			typeStrs.add(type.name());
        		}
            	sql.appendArgumentList(typeStrs);
            	sql.append(")");
        	}
        }
        
        // order
        if (ascendingJobIds) {
        	sql.append("ORDER BY Job.JobID");
        } else {
        	sql.append("ORDER BY Job.JobID DESC");
        }

    	List<ScheduledRepeatingJob> jobs = yukonJdbcTemplate.query(sql, scheduledRepeatingJobDao.getJobRowMapper());
    	
    	return jobs;
	}

    // CRE COUNT BY JOB ID
    @Override
    public int getDistinctCreCountByJobId(int jobId, Date startTime, Date stopTime) {

    	SqlStatementBuilder sql = new SqlStatementBuilder();
    	sql.append("SELECT COUNT(DISTINCT CRE.CommandRequestExecContextId) AS distinctCreCount");
    	sql.append("FROM ScheduledGrpCommandRequest SGCR");
        sql.append("JOIN CommandRequestExec CRE ON (SGCR.CommandRequestExecContextId = CRE.CommandRequestExecContextId)");

        if (jobId > 0) {
        	sql.append("WHERE SGCR.JobId IN ");
    		sql.append("(SELECT jobId FROM Job WHERE JobGroupId = (SELECT JobGroupId FROM Job WHERE JobId").eq(jobId);
            sql.append("))");
        } else {
        	sql.append("WHERE SGCR.JobId > 0");
        }

        if (startTime != null) {
        	sql.append("AND CRE.StartTime").gte(startTime);
        }

        if (stopTime != null) {
        	sql.append("AND CRE.StartTime").lte(stopTime);
        }

        int creCount = yukonJdbcTemplate.queryForInt(sql);
    	return creCount;
    }
    
    @Override
    public ScheduledGroupExecutionCounts getExecutionCountsForJobId(int jobId) {
        JobStatus<YukonJob> latestJob = jobManager.getLatestStatusByJobId(jobId);
        int jobIdToQuery = jobId;
        if(latestJob != null) {
            jobIdToQuery = latestJob.getJob().getId();
        }
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT COUNT(CASE WHEN CRER.ErrorCode !=0 THEN 1 END) failureCount");
        sql.append(", COUNT(CASE WHEN CRER.ErrorCode =0 THEN 1 END) successCount");
        sql.append(", MAX(CRE.RequestCount) totalCount");
        sql.append("FROM CommandRequestExecResult CRER");
        sql.append(     "JOIN CommandRequestExec CRE ON CRER.CommandRequestExecId = CRE.CommandRequestExecId");
        sql.append("WHERE CRER.CommandRequestExecId = (");
        sql.append(     "SELECT INSIDER.CommandRequestExecId FROM (");
        sql.append(         "SELECT CRE2.CommandRequestExecId, ROW_NUMBER() OVER (ORDER BY CRE2.StartTime DESC) RN");
        sql.append(         "FROM ScheduledGrpCommandRequest SGCR");
        sql.append(             "JOIN CommandRequestExec CRE2 ON (SGCR.CommandRequestExecContextId = CRE2.CommandRequestExecContextId)");
        sql.append(         "WHERE SGCR.JobID").eq(jobIdToQuery);
        sql.append(     ") INSIDER");
        sql.append(     "WHERE INSIDER.RN = 1)");
        
        ScheduledGroupExecutionCounts execCounts;
        try {
            execCounts = yukonJdbcTemplate.queryForObject(sql, executionRowMapper);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Job not found.", e);
        }
        return execCounts;
    }
	
    @Override
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
				
				if (startTime != null && now.compareTo(startTime) >= 0 
				        && (stopTime == null || now.compareTo(stopTime) <= 0) 
				        && commandRequestExecutionStatus != CommandRequestExecutionStatus.FAILED) {
					return ScheduledGroupRequestExecutionStatus.RUNNING;
				}
			}
		}
		
		return ScheduledGroupRequestExecutionStatus.ENABLED;
    }

	private final FieldMapper<ScheduledGroupRequestExecutionPair> fieldMapper = new FieldMapper<ScheduledGroupRequestExecutionPair>() {
        @Override
        public void extractValues(MapSqlParameterSource p, ScheduledGroupRequestExecutionPair pair) {
            p.addValue("JobId", pair.getJobId());
            
        }
        @Override
        public Number getPrimaryKey(ScheduledGroupRequestExecutionPair pair) {
            return pair.getCommandRequestExecutionContextId().getId();
        }
        @Override
        public void setPrimaryKey(ScheduledGroupRequestExecutionPair pair, int value) {
        	pair.setCommandRequestExecutionContextId(new CommandRequestExecutionContextId(value));
        }
    };

	@PostConstruct
    public void init() throws Exception {
        template = new SimpleTableAccessTemplate<>(yukonJdbcTemplate,nextValueHelper);
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
