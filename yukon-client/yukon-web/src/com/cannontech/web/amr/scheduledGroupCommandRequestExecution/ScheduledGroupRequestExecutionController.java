package com.cannontech.web.amr.scheduledGroupCommandRequestExecution;

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

import com.cannontech.amr.scheduledGroupRequestExecution.dao.ScheduledGroupCommandRequestExecutionDao;
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
import com.cannontech.web.input.InputRoot;
import com.cannontech.web.input.InputUtil;

public class ScheduledGroupRequestExecutionController extends MultiActionController {
	
	private ScheduledGroupCommandRequestExecutionDao scheduledGroupCommandRequestExecutionDao;
	private ScheduledRepeatingJobDao scheduledRepeatingJobDao;
	private JobStatusDao jobStatusDao;
	private JobManager jobManager;
	private DateFormattingService dateFormattingService;
	private YukonJobDefinition<ScheduledGroupRequestExecutionTask> scheduledGroupRequestExecutionJobDefinition;
	
	// LIST
	public ModelAndView list(HttpServletRequest request, HttpServletResponse response) throws ServletException {
		
		ModelAndView mav = new ModelAndView("scheduledGroupCommandRequestExecution/jobs.jsp");
		YukonUserContext userContext = YukonUserContextUtils.getYukonUserContext(request);
		String error = null;
		
		int jobId = ServletRequestUtils.getIntParameter(request, "jobId", 0);
		
		// DEFAULT FILTERS
		Date toDate = new Date();
		Date fromDate = DateUtils.addMonths(toDate, -1);
		CommandRequestExecutionType typeFilter = null;
		
		// FILTERS
		String fromDateStr = ServletRequestUtils.getStringParameter(request, "fromDate", null);
		if (fromDateStr != null) {
			try {
				fromDate = dateFormattingService.flexibleDateParser(fromDateStr, DateOnlyMode.START_OF_DAY, userContext);
			} catch (ParseException e) {
				error = "Invalid From Date: " + fromDateStr;
			}
		}
		
		String toDateStr = ServletRequestUtils.getStringParameter(request, "toDate", null);
		if (toDateStr != null) {
			try {
				toDate = dateFormattingService.flexibleDateParser(toDateStr, DateOnlyMode.END_OF_DAY, userContext);
			} catch (ParseException e) {
				error = "Invalid To Date: " + toDateStr;
			}
		}
		
		String typeFilterStr = ServletRequestUtils.getStringParameter(request, "typeFilter", null);
		if (typeFilterStr != null && !typeFilterStr.equals("ANY")) {
			typeFilter = CommandRequestExecutionType.valueOf(typeFilterStr);
		}
		
		// PARAMS
		mav.addObject("jobId", jobId);
		mav.addObject("fromDate", fromDate);
		mav.addObject("toDate", toDate);
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
		List<ScheduledRepeatingJob> jobs = scheduledGroupCommandRequestExecutionDao.getJobs(jobId, fromDate, toDate, typeFilter, false);
		
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
	
	// DETAIL
	public ModelAndView detail(HttpServletRequest request, HttpServletResponse response) throws ServletException {
		
		ModelAndView mav = new ModelAndView("scheduledGroupCommandRequestExecution/jobDetail.jsp");
		
		int jobId = ServletRequestUtils.getRequiredIntParameter(request, "jobId");
        
        ScheduledRepeatingJob job = scheduledRepeatingJobDao.getById(jobId);
        
        JobWrapper jobWrapper = new JobWrapper(job, null, null);
        mav.addObject("jobWrapper", jobWrapper);
        
        CommandRequestExecution lastCre = scheduledGroupCommandRequestExecutionDao.getLatestCommandRequestExecutionForJobId(jobId, null);
        mav.addObject("lastCre", lastCre);
        
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
	        
	        this.creCount = scheduledGroupCommandRequestExecutionDao.getCreCountByJobId(this.job.getId(), startTime, stopTime);
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
	}

	@Autowired
	public void setScheduledGroupCommandRequestExecutionDao(
			ScheduledGroupCommandRequestExecutionDao scheduledGroupCommandRequestExecutionDao) {
		this.scheduledGroupCommandRequestExecutionDao = scheduledGroupCommandRequestExecutionDao;
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
