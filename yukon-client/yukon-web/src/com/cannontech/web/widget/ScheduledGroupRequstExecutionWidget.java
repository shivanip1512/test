package com.cannontech.web.widget;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;

import com.cannontech.amr.scheduledGroupRequestExecution.dao.ScheduleGroupRequestExecutionDaoEnabledFilter;
import com.cannontech.amr.scheduledGroupRequestExecution.dao.ScheduleGroupRequestExecutionDaoPendingFilter;
import com.cannontech.amr.scheduledGroupRequestExecution.dao.ScheduledGroupRequestExecutionDao;
import com.cannontech.common.device.commands.CommandRequestExecutionType;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.jobs.model.ScheduledRepeatingJob;
import com.cannontech.jobs.model.YukonJob;
import com.cannontech.jobs.service.JobManager;
import com.cannontech.servlet.YukonUserContextUtils;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.scheduledGroupRequestExecution.ScheduledGroupRequestExecutionJobWrapperFactory;
import com.cannontech.web.common.scheduledGroupRequestExecution.ScheduledGroupRequestExecutionJobWrapperFactory.ScheduledGroupRequestExecutionJobWrapper;
import com.cannontech.web.security.annotation.CheckRole;
import com.cannontech.web.widget.support.WidgetControllerBase;
import com.cannontech.web.widget.support.WidgetParameterHelper;

@CheckRole(YukonRole.SCHEDULER)
public class ScheduledGroupRequstExecutionWidget extends WidgetControllerBase {

	private ScheduledGroupRequestExecutionDao scheduledGroupRequestExecutionDao;
	private ScheduledGroupRequestExecutionJobWrapperFactory scheduledGroupRequestExecutionJobWrapperFactory;
	private JobManager jobManager;
	private RolePropertyDao rolePropertyDao;
	
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
		List<ScheduledGroupRequestExecutionJobWrapper> jobWrappers = new ArrayList<ScheduledGroupRequestExecutionJobWrapper>();
		
		for (ScheduledRepeatingJob job : jobs) {
			jobWrappers.add(scheduledGroupRequestExecutionJobWrapperFactory.createJobWrapper(job, null, null, userContext));
		}
		Collections.sort(jobWrappers);
		mav.addObject("jobWrappers", jobWrappers);
		
		boolean canManage = rolePropertyDao.checkProperty(YukonRoleProperty.MANAGE_SCHEDULES, userContext.getYukonUser());
		mav.addObject("canManage", canManage);
		
		return mav;
	}
	
	public ModelAndView delete(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		YukonUserContext userContext = YukonUserContextUtils.getYukonUserContext(request);
		rolePropertyDao.verifyProperty(YukonRoleProperty.MANAGE_SCHEDULES, userContext.getYukonUser());
		
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
	
	@Autowired
	public void setRolePropertyDao(RolePropertyDao rolePropertyDao) {
		this.rolePropertyDao = rolePropertyDao;
	}
}
