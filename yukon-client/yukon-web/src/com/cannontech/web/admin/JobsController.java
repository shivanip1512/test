package com.cannontech.web.admin;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletException;

import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.jobs.dao.JobStatusDao;
import com.cannontech.jobs.dao.ScheduledOneTimeJobDao;
import com.cannontech.jobs.dao.ScheduledRepeatingJobDao;
import com.cannontech.jobs.dao.YukonJobDao;
import com.cannontech.jobs.model.JobStatus;
import com.cannontech.jobs.model.ScheduledOneTimeJob;
import com.cannontech.jobs.model.ScheduledRepeatingJob;
import com.cannontech.jobs.model.YukonJob;
import com.cannontech.jobs.service.JobManager;

@Controller
@RequestMapping("/jobsscheduler/*")
public class JobsController {
    @Autowired private JobManager jobManager;
    @Autowired private YukonJobDao yukonJobDao;
    @Autowired private JobStatusDao jobStatusDao;
    @Autowired private ScheduledRepeatingJobDao scheduledRepeatingJobDao;
    @Autowired private ScheduledOneTimeJobDao scheduledOneTimeJobDao;

    @RequestMapping
    public String active(ModelMap model) {
        Collection<YukonJob> scheduledJobs = jobManager.getCurrentlyExecuting();
        model.addAttribute("activeJobs", scheduledJobs);
        
        return "jobs/active.jsp";
    }
    
    @RequestMapping
    public String status(ModelMap model) {
        Date now = new Date();
        Date oneWeekAgo = DateUtils.addWeeks(now, -7);
        
        List<JobStatus<YukonJob>> allStatus = jobStatusDao.getAllStatus(now, oneWeekAgo);
        model.addAttribute("jobStatusList", allStatus);
        
        return "jobs/status.jsp";
    }
    
    @RequestMapping
    public String all(ModelMap model) {

        Set<ScheduledOneTimeJob> allOneTime = scheduledOneTimeJobDao.getAll();
        model.addAttribute("allOneTime", allOneTime);
        
        Set<ScheduledRepeatingJob> allRepeating = scheduledRepeatingJobDao.getAll();
        model.addAttribute("allRepeating", allRepeating);
        
        return "jobs/all.jsp";
    }
    
    @RequestMapping
    public String enableJob(int jobId) throws ServletException {
        YukonJob job = yukonJobDao.getById(jobId);
        jobManager.enableJob(job);
        
        return "redirect:all";
    }
    
    @RequestMapping
    public String disableJob( int jobId ) throws ServletException {
        YukonJob job = yukonJobDao.getById(jobId);
        jobManager.disableJob(job);
        
        return "redirect:all";
    }
    
    @RequestMapping
    public String abortJob(int jobId) throws ServletException {
        YukonJob job = yukonJobDao.getById(jobId);
        jobManager.abortJob(job);
        
        return "redirect:active";
    }
}
