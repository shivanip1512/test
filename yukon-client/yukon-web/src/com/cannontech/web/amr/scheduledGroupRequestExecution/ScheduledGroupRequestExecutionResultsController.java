package com.cannontech.web.amr.scheduledGroupRequestExecution;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.cannontech.amr.scheduledGroupRequestExecution.dao.ScheduleGroupRequestExecutionDaoEnabledFilter;
import com.cannontech.amr.scheduledGroupRequestExecution.dao.ScheduleGroupRequestExecutionDaoPendingFilter;
import com.cannontech.amr.scheduledGroupRequestExecution.dao.ScheduledGroupRequestExecutionDao;
import com.cannontech.amr.scheduledGroupRequestExecution.tasks.ScheduledGroupRequestExecutionTask;
import com.cannontech.common.bulk.mapper.ObjectMappingException;
import com.cannontech.common.device.attribute.model.Attribute;
import com.cannontech.common.device.commands.CommandRequestExecutionType;
import com.cannontech.common.device.commands.dao.model.CommandRequestExecution;
import com.cannontech.common.util.MappingList;
import com.cannontech.common.util.ObjectMapper;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.core.service.DateFormattingService.DateOnlyMode;
import com.cannontech.jobs.dao.JobStatusDao;
import com.cannontech.jobs.dao.ScheduledRepeatingJobDao;
import com.cannontech.jobs.model.ScheduledRepeatingJob;
import com.cannontech.jobs.service.JobManager;
import com.cannontech.jobs.support.ScheduleException;
import com.cannontech.jobs.support.YukonJobDefinition;
import com.cannontech.servlet.YukonUserContextUtils;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.amr.util.cronExpressionTag.CronExpressionTagUtils;
import com.cannontech.web.input.InputRoot;
import com.cannontech.web.input.InputUtil;

public class ScheduledGroupRequestExecutionResultsController extends MultiActionController {
	
	private ScheduledGroupRequestExecutionDao scheduledGroupRequestExecutionDao;
	private ScheduledRepeatingJobDao scheduledRepeatingJobDao;
	private JobStatusDao jobStatusDao;
	private JobManager jobManager;
	private DateFormattingService dateFormattingService;
	private YukonJobDefinition<ScheduledGroupRequestExecutionTask> scheduledGroupRequestExecutionJobDefinition;
	
	// JOBS
	public ModelAndView jobs(HttpServletRequest request, HttpServletResponse response) throws ServletException {
		
		ModelAndView mav = new ModelAndView("scheduledGroupRequestExecution/results/jobs.jsp");
		YukonUserContext userContext = YukonUserContextUtils.getYukonUserContext(request);
		String error = null;
		
		int jobId = ServletRequestUtils.getIntParameter(request, "jobId", 0);
		
		// DEFAULT FILTERS
		Date toDate = new Date();
		Date fromDate = DateUtils.addMonths(toDate, -1);
		CommandRequestExecutionType typeFilter = null;
		
		// FILTERS
		
		// from date
		String fromDateFilterStr = ServletRequestUtils.getStringParameter(request, "fromDateFilter", null);
		if (fromDateFilterStr != null) {
			try {
				fromDate = dateFormattingService.flexibleDateParser(fromDateFilterStr, DateOnlyMode.START_OF_DAY, userContext);
			} catch (ParseException e) {
				error = "Invalid From Date: " + fromDateFilterStr;
			}
		}
		
		// to date
		String toDateFilterStr = ServletRequestUtils.getStringParameter(request, "toDateFilter", null);
		if (toDateFilterStr != null) {
			try {
				toDate = dateFormattingService.flexibleDateParser(toDateFilterStr, DateOnlyMode.END_OF_DAY, userContext);
			} catch (ParseException e) {
				error = "Invalid To Date: " + toDateFilterStr;
			}
		}
		
		// status 
		String statusFilterStr = ServletRequestUtils.getStringParameter(request, "statusFilter", ScheduleGroupRequestExecutionDaoEnabledFilter.ANY.name());
		ScheduleGroupRequestExecutionDaoEnabledFilter statusFilter = ScheduleGroupRequestExecutionDaoEnabledFilter.valueOf(statusFilterStr);
		
		// include pending
		boolean excludePendingFilterBool = ServletRequestUtils.getBooleanParameter(request, "excludePendingFilter", false);
		ScheduleGroupRequestExecutionDaoPendingFilter excludePendingFilter;
		if (excludePendingFilterBool) {
			excludePendingFilter = ScheduleGroupRequestExecutionDaoPendingFilter.EXECUTED_ONLY;
		} else {
			excludePendingFilter = ScheduleGroupRequestExecutionDaoPendingFilter.ANY;
		}
		
		// type
		String typeFilterStr = ServletRequestUtils.getStringParameter(request, "typeFilter", null);
		if (typeFilterStr != null && !typeFilterStr.equals("ANY") && !typeFilterStr.equals("ANY")) {
			typeFilter = CommandRequestExecutionType.valueOf(typeFilterStr);
		}
		
		
		
		// PARAMS
		mav.addObject("jobId", jobId);
		mav.addObject("fromDate", fromDate);
		mav.addObject("toDate", DateUtils.addMilliseconds(toDate, -1));
		mav.addObject("statusFilter", statusFilter);
		mav.addObject("excludePendingFilter", excludePendingFilter);
		mav.addObject("typeFilter", typeFilter);
		mav.addObject("error", error);
		
		// TYPES
		CommandRequestExecutionType[] commandRequestExecutionTypes = CommandRequestExecutionType.values();
		List<CommandRequestExecutionType> scheduledCommandRequestExecutionTypes = new ArrayList<CommandRequestExecutionType>();
		for (CommandRequestExecutionType commandRequestExecutionType : commandRequestExecutionTypes) {
			if (commandRequestExecutionType.isScheduled()) {
				scheduledCommandRequestExecutionTypes.add(commandRequestExecutionType);
			}
		}
		mav.addObject("scheduledCommandRequestExecutionTypes", scheduledCommandRequestExecutionTypes);
		
		// JOBS
		List<ScheduledRepeatingJob> jobs = scheduledGroupRequestExecutionDao.getJobs(jobId, fromDate, toDate, typeFilter, statusFilter, excludePendingFilter, false);
		
		final Date startTime = fromDate;
		final Date stopTime = toDate;
		ObjectMapper<ScheduledRepeatingJob, JobWrapper> mapper = new ObjectMapper<ScheduledRepeatingJob, JobWrapper>() {
			public JobWrapper map(ScheduledRepeatingJob from) throws ObjectMappingException {
                return new JobWrapper(from, startTime, stopTime);
            }
		};
		
		MappingList<ScheduledRepeatingJob, JobWrapper> jobWrappers = new MappingList<ScheduledRepeatingJob, JobWrapper>(jobs, mapper);
		mav.addObject("jobWrappers", jobWrappers);
		
		return mav;
	}
	
	// JOB DETAIL
	public ModelAndView detail(HttpServletRequest request, HttpServletResponse response) throws ServletException {
		
		ModelAndView mav = new ModelAndView("scheduledGroupRequestExecution/results/jobDetail.jsp");
		
		int jobId = ServletRequestUtils.getRequiredIntParameter(request, "jobId");
        
        ScheduledRepeatingJob job = scheduledRepeatingJobDao.getById(jobId);
        
        JobWrapper jobWrapper = new JobWrapper(job, null, null);
        mav.addObject("jobWrapper", jobWrapper);
        
        CommandRequestExecution lastCre = scheduledGroupRequestExecutionDao.getLatestCommandRequestExecutionForJobId(jobId, null);
        mav.addObject("lastCre", lastCre);
        
        return mav;
		
	}
	
	// TOGGLE JOB ENABLED
	public ModelAndView toggleJobEnabled(HttpServletRequest request, HttpServletResponse response) throws ServletException {
		
		ModelAndView mav = new ModelAndView("redirect:detail");
		
		int jobId = ServletRequestUtils.getRequiredIntParameter(request, "jobId");
        
        ScheduledRepeatingJob job = scheduledRepeatingJobDao.getById(jobId);
        
        if (job.isDisabled()) {
        	jobManager.enableJob(job);
        } else {
        	jobManager.disableJob(job);
        }
        
        mav.addObject("jobId", jobId);
        
        return mav;
		
	}
	
	// JOB WRAPPER
	public class JobWrapper {
		
		private ScheduledRepeatingJob job;
		private ScheduledGroupRequestExecutionTask task;
		private int creCount;
		
		public JobWrapper(ScheduledRepeatingJob job, Date startTime, Date stopTime) {
			this.job = job;
			
			this.task = new ScheduledGroupRequestExecutionTask();
			InputRoot inputRoot = scheduledGroupRequestExecutionJobDefinition.getInputs();
	        InputUtil.applyProperties(inputRoot, this.task, getJob().getJobProperties());
	        
	        this.creCount = scheduledGroupRequestExecutionDao.getCreCountByJobId(this.job.getId(), startTime, stopTime);
		}
		
		public ScheduledRepeatingJob getJob() {
			return job;
		}
		
		public String getCommandRequestTypeShortName() {
			return this.task.getCommandRequestExecutionType().getShortName();
		}
		
		public Date getLastRun() {
			return jobStatusDao.getJobLastSuccessfulRunDate(this.job.getId());
		}
		
		public Date getNextRun() {
			
			Date nextRun = null;
			try {
				nextRun = jobManager.getNextRuntime(job, new Date());
			} catch (ScheduleException e) {
			}
			
			return nextRun;
		}
		
		public String getCommand() {
			return this.task.getCommand();
		}
		
		public Attribute getAttribute() {
			return this.task.getAttribute();
		}
		
		public String getDeviceGroupName() {
			return this.task.getGroupName();
		}
		
		public int getCreCount() {
			return creCount;
		}
		
		public String getScheduleDescription() {
			return CronExpressionTagUtils.getDescription(this.job.getCronString());
		}
	}

	@Autowired
	public void setScheduledGroupRequestExecutionDao(
			ScheduledGroupRequestExecutionDao scheduledGroupRequestExecutionDao) {
		this.scheduledGroupRequestExecutionDao = scheduledGroupRequestExecutionDao;
	}
	
	@Autowired
	public void setScheduledRepeatingJobDao(
			ScheduledRepeatingJobDao scheduledRepeatingJobDao) {
		this.scheduledRepeatingJobDao = scheduledRepeatingJobDao;
	}
	
	@Autowired
	public void setJobStatusDao(JobStatusDao jobStatusDao) {
		this.jobStatusDao = jobStatusDao;
	}
	
	@Resource(name="jobManager")
	public void setJobManager(JobManager jobManager) {
		this.jobManager = jobManager;
	}
	
	@Autowired
	public void setDateFormattingService(DateFormattingService dateFormattingService) {
		this.dateFormattingService = dateFormattingService;
	}
	
	@Resource(name="scheduledGroupRequestExecutionJobDefinition")
	public void setScheduledGroupRequestExecutionJobDefinition(
			YukonJobDefinition<ScheduledGroupRequestExecutionTask> scheduledGroupRequestExecutionJobDefinition) {
		this.scheduledGroupRequestExecutionJobDefinition = scheduledGroupRequestExecutionJobDefinition;
	}
}
