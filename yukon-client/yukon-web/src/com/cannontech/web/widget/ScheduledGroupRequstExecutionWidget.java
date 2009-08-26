package com.cannontech.web.widget;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;

import com.cannontech.amr.scheduledGroupRequestExecution.dao.ScheduleGroupRequestExecutionDaoEnabledFilter;
import com.cannontech.amr.scheduledGroupRequestExecution.dao.ScheduleGroupRequestExecutionDaoPendingFilter;
import com.cannontech.amr.scheduledGroupRequestExecution.dao.ScheduledGroupRequestExecutionDao;
import com.cannontech.common.bulk.mapper.ObjectMappingException;
import com.cannontech.common.device.commands.CommandRequestExecutionType;
import com.cannontech.common.util.MappingList;
import com.cannontech.common.util.ObjectMapper;
import com.cannontech.jobs.model.ScheduledRepeatingJob;
import com.cannontech.jobs.model.YukonJob;
import com.cannontech.jobs.service.JobManager;
import com.cannontech.servlet.YukonUserContextUtils;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.scheduledGroupRequestExecution.ScheduledGroupRequestExecutionJobWrapperFactory;
import com.cannontech.web.common.scheduledGroupRequestExecution.ScheduledGroupRequestExecutionJobWrapperFactory.ScheduledGroupRequestExecutionJobWrapper;
import com.cannontech.web.widget.support.WidgetControllerBase;
import com.cannontech.web.widget.support.WidgetParameterHelper;

public class ScheduledGroupRequstExecutionWidget extends WidgetControllerBase {

	private ScheduledGroupRequestExecutionDao scheduledGroupRequestExecutionDao;
	private ScheduledGroupRequestExecutionJobWrapperFactory scheduledGroupRequestExecutionJobWrapperFactory;
	private JobManager jobManager;
	
	@Override
	public ModelAndView render(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		ModelAndView mav = new ModelAndView("scheduledGroupRequestExecution/render.jsp");
		final YukonUserContext userContext = YukonUserContextUtils.getYukonUserContext(request);
		
		List<CommandRequestExecutionType> types = new ArrayList<CommandRequestExecutionType>();
		for (CommandRequestExecutionType type : CommandRequestExecutionType.values()) {
			if (type.isScheduled()) {
				types.add(type);
			}
		}
		
		List<ScheduledRepeatingJob> jobs = scheduledGroupRequestExecutionDao.getJobs(0, null, null, types, ScheduleGroupRequestExecutionDaoEnabledFilter.ANY, ScheduleGroupRequestExecutionDaoPendingFilter.ANY, false);
		
		ObjectMapper<ScheduledRepeatingJob, ScheduledGroupRequestExecutionJobWrapper> mapper = new ObjectMapper<ScheduledRepeatingJob, ScheduledGroupRequestExecutionJobWrapper>() {
			public ScheduledGroupRequestExecutionJobWrapper map(ScheduledRepeatingJob from) throws ObjectMappingException {
                return scheduledGroupRequestExecutionJobWrapperFactory.createJobWrapper(from, null, null, userContext);
            }
		};
		
		MappingList<ScheduledRepeatingJob, ScheduledGroupRequestExecutionJobWrapper> jobWrappers = new MappingList<ScheduledRepeatingJob, ScheduledGroupRequestExecutionJobWrapper>(jobs, mapper);
		mav.addObject("jobWrappers", jobWrappers);
		
		return mav;
	}
	
	public ModelAndView toggleEnabled(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		int jobId = ServletRequestUtils.getRequiredIntParameter(request, "toggleEnabledJobId");
		
		YukonJob job = jobManager.getJob(jobId);
		if (job.isDisabled()) {
			jobManager.enableJob(job);
		} else {
			jobManager.disableJob(job);
		}
	
		ModelAndView mav = render(request, response);
        return mav;
	}
	
	public ModelAndView delete(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		int jobId = WidgetParameterHelper.getRequiredIntParameter(request, "jobId");
		
		YukonJob job = jobManager.getJob(jobId);
		jobManager.deleteJob(job);
	
		ModelAndView mav = render(request, response);
        return mav;
	}
	
	
	@Autowired
	public void setScheduledGroupRequestExecutionDao(ScheduledGroupRequestExecutionDao scheduledGroupRequestExecutionDao) {
		this.scheduledGroupRequestExecutionDao = scheduledGroupRequestExecutionDao;
	}
	
	@Autowired
	public void setScheduledGroupRequestExecutionJobWrapperFactory(ScheduledGroupRequestExecutionJobWrapperFactory scheduledGroupRequestExecutionJobWrapperFactory) {
		this.scheduledGroupRequestExecutionJobWrapperFactory = scheduledGroupRequestExecutionJobWrapperFactory;
	}
	
	@Resource(name="jobManager")
	public void setJobManager(JobManager jobManager) {
		this.jobManager = jobManager;
	}
}
