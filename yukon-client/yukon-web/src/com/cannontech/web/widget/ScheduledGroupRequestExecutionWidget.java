package com.cannontech.web.widget;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.cannontech.amr.scheduledGroupRequestExecution.dao.ScheduleGroupRequestExecutionDaoEnabledFilter;
import com.cannontech.amr.scheduledGroupRequestExecution.dao.ScheduleGroupRequestExecutionDaoOnetimeFilter;
import com.cannontech.amr.scheduledGroupRequestExecution.dao.ScheduleGroupRequestExecutionDaoPendingFilter;
import com.cannontech.amr.scheduledGroupRequestExecution.dao.ScheduledGroupRequestExecutionDao;
import com.cannontech.common.device.DeviceRequestType;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.jobs.model.ScheduledRepeatingJob;
import com.cannontech.jobs.model.YukonJob;
import com.cannontech.jobs.service.JobManager;
import com.cannontech.jobs.support.ScheduleException;
import com.cannontech.servlet.YukonUserContextUtils;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.scheduledGroupRequestExecution.ScheduledGroupRequestExecutionJobWrapperFactory;
import com.cannontech.web.common.scheduledGroupRequestExecution.ScheduledGroupRequestExecutionJobWrapperFactory.ScheduledGroupRequestExecutionJobWrapper;
import com.cannontech.web.security.annotation.CheckRole;
import com.cannontech.web.widget.support.WidgetControllerBase;
import com.cannontech.web.widget.support.WidgetParameterHelper;
import com.google.common.base.Function;
import com.google.common.collect.Ordering;

@Controller
@RequestMapping("/scheduledGroupRequestExecutionWidget")
@CheckRole(YukonRole.SCHEDULER)
public class ScheduledGroupRequestExecutionWidget extends WidgetControllerBase {

    @Autowired private ScheduledGroupRequestExecutionDao scheduledGroupRequestExecutionDao;
	@Autowired private ScheduledGroupRequestExecutionJobWrapperFactory scheduledGroupRequestExecutionJobWrapperFactory;
	private JobManager jobManager;
	@Autowired private RolePropertyDao rolePropertyDao;
	
	public ScheduledGroupRequestExecutionWidget() {
	    this.setIdentityPath("common/deviceIdentity.jsp");
	}
	
    @Override
    @RequestMapping("render")
	public ModelAndView render(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		ModelAndView mav = new ModelAndView("scheduledGroupRequestExecution/render.jsp");
		final YukonUserContext userContext = YukonUserContextUtils.getYukonUserContext(request);
		
		List<DeviceRequestType> types = new ArrayList<DeviceRequestType>();
		for (DeviceRequestType type : DeviceRequestType.values()) {
			if (type.isScheduled()) {
				types.add(type);
			}
		}
		
		List<ScheduledRepeatingJob> jobs = scheduledGroupRequestExecutionDao.getJobs(
				0, null, null, types,
				ScheduleGroupRequestExecutionDaoEnabledFilter.ANY,
				ScheduleGroupRequestExecutionDaoPendingFilter.ANY,
				ScheduleGroupRequestExecutionDaoOnetimeFilter.EXCLUDE_ONETIME,
				false);
		Collections.sort(jobs, getNextRunComparator());
		
		// Just get the top 20 jobs
		int numJobsToShow = 20;
		Integer numAdditionalJobs = null;
		if (jobs.size() > numJobsToShow) {
		    numAdditionalJobs = jobs.size() - numJobsToShow;
		    jobs = jobs.subList(0, numJobsToShow);
		}
		
		List<ScheduledGroupRequestExecutionJobWrapper> jobWrappers = new ArrayList<ScheduledGroupRequestExecutionJobWrapper>();
		for (ScheduledRepeatingJob job : jobs) {
			jobWrappers.add(scheduledGroupRequestExecutionJobWrapperFactory.createJobWrapper(job, null, null, userContext));
		}
		mav.addObject("jobWrappers", jobWrappers);
		
		boolean canManage = rolePropertyDao.checkProperty(YukonRoleProperty.MANAGE_SCHEDULES, userContext.getYukonUser());
		mav.addObject("canManage", canManage);
		mav.addObject("numAdditionalJobs", numAdditionalJobs);
		return mav;
	}
	
	// TOGGLE JOB ENABLED
    @RequestMapping("toggleEnabled")
    public ModelAndView toggleEnabled(HttpServletRequest request, HttpServletResponse response) throws Exception {
        
        YukonUserContext userContext = YukonUserContextUtils.getYukonUserContext(request);
        rolePropertyDao.verifyProperty(YukonRoleProperty.MANAGE_SCHEDULES,
                                       userContext.getYukonUser());

        int jobId = WidgetParameterHelper.getRequiredIntParameter(request, "jobId");
        YukonJob job = jobManager.getJob(jobId);
        
        if (job.isDisabled()) {
            jobManager.enableJob(job);
        } else {
            jobManager.disableJob(job);
        }
        
        ModelAndView mav = render(request, response);
        return mav;
    }

    private Comparator<ScheduledRepeatingJob> getNextRunComparator() {
        Ordering<Date> dateComparer = Ordering.natural().nullsLast();
        Ordering<ScheduledRepeatingJob> jobNextRunOrdering = dateComparer
            .onResultOf(new Function<ScheduledRepeatingJob, Date>() {
                public Date apply(ScheduledRepeatingJob from) {
                    return getNextRun(from);
                }
            });
        return jobNextRunOrdering;
    }

    private Date getNextRun(ScheduledRepeatingJob job) {
        Date nextRun = null;
        try {
            nextRun = jobManager.getNextRuntime(job, new Date());
        } catch (ScheduleException e) {}
        return nextRun;
    }

    @Resource(name = "jobManager")
    public void setJobManager(JobManager jobManager) {
        this.jobManager = jobManager;
    }

    @Override
    public String getTitleKey() {
        return WidgetControllerBase.keyPrefix + "schedules";
    }
}
