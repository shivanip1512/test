package com.cannontech.amr.scheduledGroupRequestExecution.dao.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

import com.cannontech.amr.scheduledGroupRequestExecution.dao.ScheduleGroupRequestExecutionDaoEnabledFilter;
import com.cannontech.amr.scheduledGroupRequestExecution.dao.ScheduleGroupRequestExecutionDaoPendingFilter;
import com.cannontech.amr.scheduledGroupRequestExecution.dao.ScheduledGroupRequestExecutionDao;
import com.cannontech.amr.scheduledGroupRequestExecution.dao.model.ScheduledGroupRequestExecutionPair;
import com.cannontech.common.device.commands.CommandRequestExecutionType;
import com.cannontech.common.device.commands.dao.CommandRequestExecutionDao;
import com.cannontech.common.device.commands.dao.CommandRequestExecutionResultsDao;
import com.cannontech.common.device.commands.dao.impl.CommandRequestExecutionDaoImpl;
import com.cannontech.common.device.commands.dao.model.CommandRequestExecution;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.FieldMapper;
import com.cannontech.database.MaxListResultSetExtractor;
import com.cannontech.database.SimpleTableAccessTemplate;
import com.cannontech.database.incrementer.NextValueHelper;
import com.cannontech.jobs.dao.ScheduledRepeatingJobDao;
import com.cannontech.jobs.model.ScheduledRepeatingJob;

public class ScheduledGroupRequestExecutionDaoImpl implements ScheduledGroupRequestExecutionDao, InitializingBean {

	private ScheduledRepeatingJobDao scheduledRepeatingJobDao;
	private CommandRequestExecutionDao commandRequestExecutionDao;
	private CommandRequestExecutionResultsDao commandRequestExecutionResultsDao;
	
    private SimpleJdbcTemplate simpleJdbcTemplate;
    private NextValueHelper nextValueHelper;
    private SimpleTableAccessTemplate<ScheduledGroupRequestExecutionPair> template;
    
    private static final String TABLE_NAME = "ScheduledGroupCommandRequests";
    
    // INSERT
    public void insert(ScheduledGroupRequestExecutionPair pair) {
        template.insert(pair);
    }
    
    
    
    // LATEST CRE FOR JOB ID
    public CommandRequestExecution getLatestCommandRequestExecutionForJobId(int jobId, Date cutoff) {
    	
    	List<Object> args = new ArrayList<Object>();
    	
    	SqlStatementBuilder sql = new SqlStatementBuilder();
    	sql.append("SELECT CRE.* FROM " + TABLE_NAME + " SGCR");
    	sql.append("INNER JOIN CommandRequestExecution CRE ON (SGCR.CommandRequestExecutionId = CRE.CommandRequestExecutionId)");
    	sql.append("WHERE SGCR.JobID = ?");
    	
    	args.add(jobId);
    	
    	if (cutoff != null) {
        	sql.append("AND CRE.StartTime <= ?");
        	args.add(cutoff);
        }
    	
    	sql.append("ORDER BY CRE.StartTime DESC");
    	
    	MaxListResultSetExtractor<CommandRequestExecution> rse = new MaxListResultSetExtractor<CommandRequestExecution>(CommandRequestExecutionDaoImpl.createRowMapper(), 1);
    	JdbcOperations jdbcOperations = simpleJdbcTemplate.getJdbcOperations();
    	jdbcOperations.query(sql.getSql(), args.toArray(), rse);
    	
    	List<CommandRequestExecution> result = rse.getResult();
    	if (result.size() > 0) {
    		return result.get(0);
    	} else {
    		return null;
    	}
    }
    
    // LASTEST COUNTS FOR JOB ID
    public int getLatestRequestCountForJobId(int jobId) {
    	
    	int count = 0;
    	CommandRequestExecution latestCre = getLatestCommandRequestExecutionForJobId(jobId, null);
    	
    	if (latestCre != null) {
    		count = commandRequestExecutionDao.getRequestCountById(latestCre.getId());
    	}
    	return count;
    }
	public int getLatestSuccessCountForJobId(int jobId) {
		
		int count = 0;
    	CommandRequestExecution latestCre = getLatestCommandRequestExecutionForJobId(jobId, null);
    	
    	if (latestCre != null) {
    		count = commandRequestExecutionResultsDao.getSucessCountByExecutionId(latestCre.getId());
    	}
    	return count;
	}
	public int getLatestFailCountForJobId(int jobId) {
		
		int count = 0;
    	CommandRequestExecution latestCre = getLatestCommandRequestExecutionForJobId(jobId, null);
    	
    	if (latestCre != null) {
    		count = commandRequestExecutionResultsDao.getFailCountByExecutionId(latestCre.getId());
    	}
    	return count;
	}
    
    // GET JOBS BY RANGE
	public List<ScheduledRepeatingJob> getJobs(int jobId, 
												Date startTime, 
												Date stopTime, 
												CommandRequestExecutionType type, 
												ScheduleGroupRequestExecutionDaoEnabledFilter enabled, 
												ScheduleGroupRequestExecutionDaoPendingFilter pending,
												boolean acsending) {
		
		SqlStatementBuilder sql = new SqlStatementBuilder();
    	sql.append("SELECT DISTINCT Job.*, JSR.* FROM Job");
        sql.append("INNER JOIN JobScheduledRepeating JSR ON (Job.JobId = JSR.JobId)");
        sql.append("LEFT JOIN JobStatus JS ON (Job.JobId = JS.JobId)");
        if (type != null) {
        	sql.append("INNER JOIN JOBPROPERTY JP ON (Job.JobId = JP.JobId)");
        }
        
        List<Object> args = new ArrayList<Object>();
        
        // single job
        if (jobId > 0) {
    		sql.append("WHERE Job.JobId = ?");
    		args.add(jobId);
        } else {
        	sql.append("WHERE Job.JobId > 0");
        }
        
        // start stop / pending (start time will get ignored if PENDING_ONLY is set)
        if (startTime != null) {
        	
        	if (pending.equals(ScheduleGroupRequestExecutionDaoPendingFilter.ANY)) {
        		
        		sql.append("AND (JS.StartTime >= ? OR JS.StartTime IS NULL)");
            	args.add(startTime);
        	
        	} else if (pending.equals(ScheduleGroupRequestExecutionDaoPendingFilter.PENDING_ONLY)) {
        		
        		sql.append("AND JS.StartTime IS NULL");
        	
        	} else if (pending.equals(ScheduleGroupRequestExecutionDaoPendingFilter.EXECUTED_ONLY)) {
        		
        		sql.append("AND JS.StartTime >= ?");
            	args.add(startTime);
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
        		
        		sql.append("AND (JS.StopTime < ? OR JS.StopTime IS NULL)");
            	args.add(stopTime);
        	
        	} else if (pending.equals(ScheduleGroupRequestExecutionDaoPendingFilter.PENDING_ONLY)) {
        		
        		sql.append("AND JS.StopTime IS NULL");
        	
        	} else if (pending.equals(ScheduleGroupRequestExecutionDaoPendingFilter.EXECUTED_ONLY)) {
        		
        		sql.append("AND JS.StopTime < ?");
            	args.add(stopTime);
        	}

        } else {
        	
        	// stop time can be whatever unless PENDING_ONLY is set, we know it can only have a null value
        	// (although the start time check should have already excluded the record)
        	if (pending.equals(ScheduleGroupRequestExecutionDaoPendingFilter.PENDING_ONLY)) {
        		
        		sql.append("AND JS.StopTime IS NULL");
        	
        	}
        }
        
        // enabled disabled status
        if (enabled.equals(ScheduleGroupRequestExecutionDaoEnabledFilter.ENABLED_ONLY)) {
        	sql.append("AND Job.Disabled = ?");
        	args.add("N");
        }
        else if (enabled.equals(ScheduleGroupRequestExecutionDaoEnabledFilter.DISABLED_ONLY)) {
        	sql.append("AND Job.Disabled = ?");
        	args.add("Y");
        }
        
        // type
        if (type != null) {
        	sql.append("AND JP.Value = ?");
        	args.add(type.name());
        }
        
        // order
        if (acsending) {
        	sql.append("ORDER BY Job.JobID");
        } else {
        	sql.append("ORDER BY Job.JobID DESC");
        }
	    
    	List<ScheduledRepeatingJob> jobs = simpleJdbcTemplate.query(sql.getSql(), scheduledRepeatingJobDao.getJobRowMapper(), args.toArray());
    	return jobs;
	}
	
    
    // GET CRES BY JOB ID
    public List<CommandRequestExecution> getCommandRequestExecutionsByJobId(int jobId, Date startTime, Date stopTime, boolean acsending) {
    	
    	SqlStatementBuilder sql = new SqlStatementBuilder();
    	sql.append("SELECT CRE.* FROM " + TABLE_NAME + " SGCR");
        sql.append("INNER JOIN CommandRequestExecution CRE ON (SGCR.CommandRequestExecutionId = CRE.CommandRequestExecutionId)");
        
        List<Object> args = new ArrayList<Object>();
        
        if (jobId > 0) {
    		sql.append("WHERE SGCR.JobId = ?");
    		args.add(jobId);
        } else {
        	sql.append("WHERE SGCR.JobId > 0");
        }
    	
        if (startTime != null) {
        	sql.append("AND CRE.StartTime >= ?");
        	args.add(startTime);
        }
        
        if (stopTime != null) {
        	sql.append("AND CRE.StartTime <= ?");
        	args.add(stopTime);
        }
    	
        if (acsending) {
        	sql.append("ORDER BY CRE.StartTime");
        } else {
        	sql.append("ORDER BY CRE.StartTime DESC");
        }
        
        List<CommandRequestExecution> cres = simpleJdbcTemplate.query(sql.getSql(), CommandRequestExecutionDaoImpl.createRowMapper(), args.toArray());
    	return cres;
    }
    
    // CRE COUNT BY JOB ID
    public int getCreCountByJobId(int jobId, Date startTime, Date stopTime) {
    	
    	SqlStatementBuilder sql = new SqlStatementBuilder();
    	sql.append("SELECT COUNT(CRE.CommandRequestExecutionId) AS creCount ");
    	sql.append("FROM " + TABLE_NAME + " SGCR");
        sql.append("INNER JOIN CommandRequestExecution CRE ON (SGCR.CommandRequestExecutionId = CRE.CommandRequestExecutionId)");
        
        List<Object> args = new ArrayList<Object>();
        
        if (jobId > 0) {
    		sql.append("WHERE SGCR.JobId = ?");
    		args.add(jobId);
        } else {
        	sql.append("WHERE SGCR.JobId > 0");
        }
    	
        if (startTime != null) {
        	sql.append("AND CRE.StartTime >= ?");
        	args.add(startTime);
        }
        
        if (stopTime != null) {
        	sql.append("AND CRE.StartTime <= ?");
        	args.add(stopTime);
        }
    	
        int creCount = simpleJdbcTemplate.queryForInt(sql.getSql(), args.toArray());
    	return creCount;
    }
	
	private FieldMapper<ScheduledGroupRequestExecutionPair> fieldMapper = new FieldMapper<ScheduledGroupRequestExecutionPair>() {
        public void extractValues(MapSqlParameterSource p, ScheduledGroupRequestExecutionPair pair) {
            p.addValue("CommandRequestExecutionId", pair.getCommandRequestExecutionId());
            p.addValue("JobId", pair.getJobId());
            
        }
        public Number getPrimaryKey(ScheduledGroupRequestExecutionPair pair) {
            return pair.getCommandRequestExecutionId();
        }
        public void setPrimaryKey(ScheduledGroupRequestExecutionPair pair, int value) {
        	pair.setCommandRequestExecutionId(value);
        }
    };
    
	public void afterPropertiesSet() throws Exception {
        template = new SimpleTableAccessTemplate<ScheduledGroupRequestExecutionPair>(simpleJdbcTemplate, nextValueHelper);
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
	@Autowired
	public void setScheduledRepeatingJobDao(
			ScheduledRepeatingJobDao scheduledRepeatingJobDao) {
		this.scheduledRepeatingJobDao = scheduledRepeatingJobDao;
	}
	@Autowired
	public void setCommandRequestExecutionDao(
			CommandRequestExecutionDao commandRequestExecutionDao) {
		this.commandRequestExecutionDao = commandRequestExecutionDao;
	}
	@Autowired
	public void setCommandRequestExecutionResultsDao(
			CommandRequestExecutionResultsDao commandRequestExecutionResultsDao) {
		this.commandRequestExecutionResultsDao = commandRequestExecutionResultsDao;
	}
}
