package com.cannontech.web.dr;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.joda.time.LocalTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.jobs.dao.ScheduledRepeatingJobDao;
import com.cannontech.jobs.model.ScheduledRepeatingJob;
import com.cannontech.jobs.service.JobManager;
import com.cannontech.jobs.support.YukonJobDefinition;
import com.cannontech.jobs.support.YukonTask;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.dr.model.RfPerformanceSettings;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.google.common.collect.Iterables;

@Controller
@CheckRoleProperty(YukonRoleProperty.DEMAND_RESPONSE)
public class RfPerformanceController {

    private static final String KEY_BASE = "yukon.web.modules.dr.home.rfPerformance.configure.";
    
    private static final Logger log = YukonLogManager.getLogger(RfPerformanceController.class);
    
    @Autowired private ScheduledRepeatingJobDao jobDao;
    @Autowired private JobManager jobManager;
    
    @Autowired @Qualifier("rfnPerformanceVerification")
        private YukonJobDefinition<RfnPerformanceVerificationTask> rfnVerificationJobDef;
    @Autowired @Qualifier("rfnPerformanceVerificationEmail")
        private YukonJobDefinition<RfnPerformanceVerificationEmailTask> rfnEmailJobDef;
    
    /**
     * Update settings for rf performance test command and email generation
     */
    @RequestMapping(value="/rf/performance", method=RequestMethod.POST)
    public String saveSettings(@ModelAttribute("settings") RfPerformanceSettings settings, YukonUserContext userContext, FlashScope flash) {
        
        LocalTime time = LocalTime.MIDNIGHT.plusMinutes(settings.getTime());
        
        StringBuilder cron = new StringBuilder("0 ");
        cron.append(time.getMinuteOfHour() + " ");
        cron.append(time.getHourOfDay() + " * * ?");
        
        log.debug("Cron update: '" + cron.toString() + "'");
        
        ScheduledRepeatingJob testCommandJob = getJob(rfnVerificationJobDef);
        testCommandJob.setSystemUser(true);  // important!
        
        ScheduledRepeatingJob emailJob = getJob(rfnEmailJobDef);
        emailJob.setSystemUser(true); // important!
        emailJob.getJobProperties().put("notificationGroups", StringUtils.join(settings.getNotifGroupIds(), ","));
//        emailJob.getJobProperties().put("additionalEmails", "");
        
        try {
            if (!cron.toString().equals(testCommandJob.getCronString())) {
                jobManager.replaceScheduledJob(testCommandJob.getId(), 
                                               rfnVerificationJobDef, 
                                               testCommandJob.getJobDefinition().createBean(), 
                                               cron.toString(), 
                                               null); // system user!
            }
            
            if (settings.isEmail()) {
                jobManager.enableJob(emailJob);
            } else {
                jobManager.disableJob(emailJob);
            }
            
            flash.setConfirm(new YukonMessageSourceResolvable(KEY_BASE + "success"));
        } catch (Exception e) {
            flash.setError(new YukonMessageSourceResolvable(KEY_BASE + "failed"));
            log.error("Failed to update RF Broadcast Performance Email Job", e);
        }
        
        return "redirect:/dr/home";
    }
    
    @RequestMapping(value="/rf/details", method=RequestMethod.GET)
    public String details(ModelMap model, long from, long to) {
        return "dr/rf/details.jsp";
    }
    
    private ScheduledRepeatingJob getJob(YukonJobDefinition<? extends YukonTask> jobDefinition) {
        List<ScheduledRepeatingJob> activeJobs = jobManager.getNotDeletedRepeatingJobsByDefinition(jobDefinition);
        return Iterables.getOnlyElement(activeJobs);
    }
}