package com.cannontech.web.debug;

import java.util.SortedSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.cannontech.jobs.service.JobManager;
import com.cannontech.jobs.service.JobManager.ScheduledJobInfo;

public class SchedulerDebugController extends MultiActionController {
    private JobManager jobManager;

    public ModelAndView active(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView mav = new ModelAndView("scheduler/active.jsp");

        SortedSet<ScheduledJobInfo> scheduledJobInfo = jobManager.getScheduledJobInfo();

        mav.addObject("scheduledJobInfo", scheduledJobInfo);

        return mav;
    }

    public void setJobManager(JobManager jobManager) {
        this.jobManager = jobManager;
    }

}
