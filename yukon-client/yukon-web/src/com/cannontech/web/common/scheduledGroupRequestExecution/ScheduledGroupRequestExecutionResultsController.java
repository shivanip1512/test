package com.cannontech.web.common.scheduledGroupRequestExecution;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
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
import com.cannontech.common.bulk.mapper.ObjectMappingException;
import com.cannontech.common.device.commands.CommandRequestExecutionType;
import com.cannontech.common.device.commands.dao.model.CommandRequestExecution;
import com.cannontech.common.util.MappingList;
import com.cannontech.common.util.ObjectMapper;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.core.service.DateFormattingService.DateOnlyMode;
import com.cannontech.jobs.dao.ScheduledRepeatingJobDao;
import com.cannontech.jobs.model.ScheduledRepeatingJob;
import com.cannontech.jobs.service.JobManager;
import com.cannontech.servlet.YukonUserContextUtils;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.scheduledGroupRequestExecution.ScheduledGroupRequestExecutionJobWrapperFactory.ScheduledGroupRequestExecutionJobWrapper;

public class ScheduledGroupRequestExecutionResultsController extends MultiActionController {
	
	private ScheduledGroupRequestExecutionDao scheduledGroupRequestExecutionDao;
	private ScheduledRepeatingJobDao scheduledRepeatingJobDao;
	private JobManager jobManager;
	private DateFormattingService dateFormattingService;
	private ScheduledGroupRequestExecutionJobWrapperFactory scheduledGroupRequestExecutionJobWrapperFactory;
	
	// JOBS
	public ModelAndView jobs(HttpServletRequest request, HttpServletResponse response) throws ServletException {
		
		ModelAndView mav = new ModelAndView("scheduledGroupRequestExecution/results/jobs.jsp");
		final YukonUserContext userContext = YukonUserContextUtils.getYukonUserContext(request);
		String error = null;
		
		int jobId = ServletRequestUtils.getIntParameter(request, "jobId", 0);
		
		// DEFAULT FILTERS
		Date toDate = new Date();
		Date fromDate = DateUtils.addMonths(toDate, -1);
		List<CommandRequestExecutionType> typeFilters = null;
		
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
		CommandRequestExecutionType typeFilter = null;
		String typeFilterStr = ServletRequestUtils.getStringParameter(request, "typeFilter", null);
		if (typeFilterStr != null && !typeFilterStr.equals("ANY")) {
			typeFilter = CommandRequestExecutionType.valueOf(typeFilterStr);
			typeFilters = Collections.singletonList(typeFilter);
		} else {
			typeFilters = null;
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
		List<ScheduledRepeatingJob> jobs = scheduledGroupRequestExecutionDao.getJobs(jobId, fromDate, toDate, typeFilters, statusFilter, excludePendingFilter, false);
		
		final Date startTime = fromDate;
		final Date stopTime = toDate;
		ObjectMapper<ScheduledRepeatingJob, ScheduledGroupRequestExecutionJobWrapper> mapper = new ObjectMapper<ScheduledRepeatingJob, ScheduledGroupRequestExecutionJobWrapper>() {
			public ScheduledGroupRequestExecutionJobWrapper map(ScheduledRepeatingJob from) throws ObjectMappingException {
                return scheduledGroupRequestExecutionJobWrapperFactory.createJobWrapper(from, startTime, stopTime, userContext);
            }
		};
		
		MappingList<ScheduledRepeatingJob, ScheduledGroupRequestExecutionJobWrapper> jobWrappers = new MappingList<ScheduledRepeatingJob, ScheduledGroupRequestExecutionJobWrapper>(jobs, mapper);
		mav.addObject("jobWrappers", jobWrappers);
		
		return mav;
	}
	
	// JOB DETAIL
	public ModelAndView detail(HttpServletRequest request, HttpServletResponse response) throws ServletException {
		
		ModelAndView mav = new ModelAndView("scheduledGroupRequestExecution/results/jobDetail.jsp");
		YukonUserContext userContext = YukonUserContextUtils.getYukonUserContext(request);
		
		int jobId = ServletRequestUtils.getRequiredIntParameter(request, "jobId");
        
        ScheduledRepeatingJob job = scheduledRepeatingJobDao.getById(jobId);
        
        ScheduledGroupRequestExecutionJobWrapper jobWrapper = scheduledGroupRequestExecutionJobWrapperFactory.createJobWrapper(job, null, null, userContext);
        mav.addObject("jobWrapper", jobWrapper);
        
        CommandRequestExecution lastCre = scheduledGroupRequestExecutionDao.findLatestCommandRequestExecutionForJobId(jobId, null);
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
	
	@Resource(name="jobManager")
	public void setJobManager(JobManager jobManager) {
		this.jobManager = jobManager;
	}
	
	@Autowired
	public void setDateFormattingService(DateFormattingService dateFormattingService) {
		this.dateFormattingService = dateFormattingService;
	}
	
	@Autowired
	public void setScheduledGroupRequestExecutionJobWrapperFactory(ScheduledGroupRequestExecutionJobWrapperFactory scheduledGroupRequestExecutionJobWrapperFactory) {
		this.scheduledGroupRequestExecutionJobWrapperFactory = scheduledGroupRequestExecutionJobWrapperFactory;
	}
}