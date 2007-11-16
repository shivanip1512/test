package com.cannontech.web.debug;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.cannontech.jobs.dao.JobStatusDao;
import com.cannontech.jobs.dao.ScheduledOneTimeJobDao;
import com.cannontech.jobs.dao.ScheduledRepeatingJobDao;
import com.cannontech.jobs.dao.YukonJobDao;
import com.cannontech.jobs.model.JobStatus;
import com.cannontech.jobs.model.ScheduledOneTimeJob;
import com.cannontech.jobs.model.ScheduledRepeatingJob;
import com.cannontech.jobs.model.YukonJob;
import com.cannontech.jobs.service.JobManager;

public class SchedulerDebugController extends MultiActionController {
    private JobManager jobManager;
    private YukonJobDao yukonJobDao;
    private JobStatusDao jobStatusDao;
    private ScheduledRepeatingJobDao scheduledRepeatingJobDao;
    private ScheduledOneTimeJobDao scheduledOneTimeJobDao;

    public ModelAndView active(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView mav = new ModelAndView("scheduler/active.jsp");

        Collection<YukonJob> scheduledJobs = jobManager.getCurrentlyExecuting();
        mav.addObject("activeJobs", scheduledJobs);
        
        return mav;
    }

    public ModelAndView status(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView mav = new ModelAndView("scheduler/status.jsp");
        
        Date now = new Date();
        Date oneWeekAgo = DateUtils.addWeeks(now, -7);
        
        List<JobStatus<YukonJob>> allStatus = jobStatusDao.getAllStatus(now, oneWeekAgo);
        mav.addObject("jobStatusList", allStatus);
        
        return mav;
    }
    
    public ModelAndView jobs(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView mav = new ModelAndView("scheduler/jobs.jsp");
        
        Set<ScheduledOneTimeJob> allOneTime = scheduledOneTimeJobDao.getAll();
        mav.addObject("allOneTime", allOneTime);
        
        Set<ScheduledRepeatingJob> allRepeating = scheduledRepeatingJobDao.getAll();
        mav.addObject("allRepeating", allRepeating);
        
        return mav;
    }
    
    public ModelAndView enableJob(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        int jobId = ServletRequestUtils.getRequiredIntParameter(request, "jobId");
        YukonJob job = yukonJobDao.getById(jobId);
        jobManager.enableJob(job);
        
        return new ModelAndView("redirect:jobs");
    }
    
    public ModelAndView disableJob(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        int jobId = ServletRequestUtils.getRequiredIntParameter(request, "jobId");
        YukonJob job = yukonJobDao.getById(jobId);
        jobManager.disableJob(job);
        
        return new ModelAndView("redirect:jobs");
    }
    
    public ModelAndView abortJob(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        int jobId = ServletRequestUtils.getRequiredIntParameter(request, "jobId");
        YukonJob job = yukonJobDao.getById(jobId);
        jobManager.abortJob(job);
        
        return new ModelAndView("redirect:active");
    }
    
    @Required
    public void setJobManager(JobManager jobManager) {
        this.jobManager = jobManager;
    }
    
    @Required
    public void setJobStatusDao(JobStatusDao jobStatusDao) {
        this.jobStatusDao = jobStatusDao;
    }
    
    @Required
    public void setScheduledOneTimeJobDao(ScheduledOneTimeJobDao scheduledOneTimeJobDao) {
        this.scheduledOneTimeJobDao = scheduledOneTimeJobDao;
    }
    
    @Required
    public void setScheduledRepeatingJobDao(ScheduledRepeatingJobDao scheduledRepeatingJobDao) {
        this.scheduledRepeatingJobDao = scheduledRepeatingJobDao;
    }
    
    @Required
    public void setYukonJobDao(YukonJobDao yukonJobDao) {
        this.yukonJobDao = yukonJobDao;
    }

}
