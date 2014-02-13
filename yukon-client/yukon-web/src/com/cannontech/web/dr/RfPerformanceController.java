package com.cannontech.web.dr;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.joda.time.LocalTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.util.Range;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.dr.model.PerformanceVerificationEventMessageStats;
import com.cannontech.dr.rfn.dao.PerformanceVerificationDao;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.jobs.dao.ScheduledRepeatingJobDao;
import com.cannontech.jobs.model.ScheduledRepeatingJob;
import com.cannontech.jobs.service.JobManager;
import com.cannontech.jobs.support.YukonJobDefinition;
import com.cannontech.jobs.support.YukonTask;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.dr.model.RfPerformanceSettings;
import com.cannontech.web.input.DatePropertyEditorFactory;
import com.cannontech.web.input.DatePropertyEditorFactory.BlankMode;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.google.common.collect.Iterables;

@Controller
@CheckRoleProperty(YukonRoleProperty.DEMAND_RESPONSE)
public class RfPerformanceController {

    private static final String KEY_BASE = "yukon.web.modules.dr.home.rfPerformance.configure.";
    
    private static final Logger log = YukonLogManager.getLogger(RfPerformanceController.class);
    
    @Autowired private ScheduledRepeatingJobDao jobDao;
    @Autowired private JobManager jobManager;
    @Autowired private PerformanceVerificationDao rfPerformanceDao;
    @Autowired private DatePropertyEditorFactory datePropertyEditorFactory;
    
    @Autowired @Qualifier("rfnPerformanceVerification")
        private YukonJobDefinition<RfnPerformanceVerificationTask> rfnVerificationJobDef;
    @Autowired @Qualifier("rfnPerformanceVerificationEmail")
        private YukonJobDefinition<RfnPerformanceVerificationEmailTask> rfnEmailJobDef;
    
    /**
     * Update settings for rf performance test command and email generation
     */
    @RequestMapping(value="/rf/performance", method=RequestMethod.POST)
    public String saveSettings(@ModelAttribute("settings") RfPerformanceSettings settings, YukonUserContext userContext, FlashScope flash) {
        
        LocalTime commandTime = LocalTime.MIDNIGHT.plusMinutes(settings.getTime());
        LocalTime emailTime = LocalTime.MIDNIGHT.plusMinutes(settings.getEmailTime());
        
        StringBuilder commandCron = new StringBuilder("0 ");
        commandCron.append(commandTime.getMinuteOfHour() + " ");
        commandCron.append(commandTime.getHourOfDay() + " * * ?");
        
        StringBuilder emailCron = new StringBuilder("0 ");
        emailCron.append(emailTime.getMinuteOfHour() + " ");
        emailCron.append(emailTime.getHourOfDay() + " * * ?");
        
        ScheduledRepeatingJob commandJob = getJob(rfnVerificationJobDef);
        commandJob.setSystemUser(true);  // important!
        
        try {
            if (!commandCron.toString().equals(commandJob.getCronString())) {
                jobManager.replaceScheduledJob(commandJob.getId(), 
                                               rfnVerificationJobDef, 
                                               commandJob.getJobDefinition().createBean(), 
                                               commandCron.toString(), 
                                               null); // system user!
            }
            
            ScheduledRepeatingJob emailJob = getJob(rfnEmailJobDef);
            emailJob.getJobProperties().put("notificationGroups", StringUtils.join(settings.getNotifGroupIds(), ","));
//            emailJob.getJobProperties().put("additionalEmails", "");
            
            if (settings.isEmail()) {
                // Email setting is enabled. We might need to update the cron string.
                if (!emailCron.toString().equals(emailJob.getCronString())) {
                    jobManager.replaceScheduledJob(emailJob.getId(), 
                            rfnEmailJobDef, 
                            emailJob.getJobDefinition().createBean(), 
                            emailCron.toString(), 
                            null,
                            emailJob.getJobProperties()); 
                    
                    emailJob = getJob(rfnEmailJobDef);
                }
                
                if (emailJob.isDisabled()) {
                    jobManager.enableJob(emailJob);
                }
            } else if (!emailJob.isDisabled()) {
                jobManager.disableJob(emailJob);
            }
            
            // Always call one of these since we may need to update
            // job properties and these two methods end up saving 
            // everything about the job (hacky)
            
            flash.setConfirm(new YukonMessageSourceResolvable(KEY_BASE + "success"));
        } catch (Exception e) {
            flash.setError(new YukonMessageSourceResolvable(KEY_BASE + "failed"));
            log.error("Failed to update RF Broadcast Performance Email Job", e);
        }
        
        return "redirect:/dr/home";
    }
    
    @RequestMapping(value="/rf/details", method=RequestMethod.GET)
    public String details(ModelMap model, @RequestParam(required=false) Instant from, @RequestParam(required=false) Instant to) {
        
        if (from == null) from =  new Instant().minus(Duration.standardDays(7));
        if (to == null) to =  new Instant(); 
        
        model.addAttribute("from", from);
        model.addAttribute("to", to);
        
        List<PerformanceVerificationEventMessageStats> tests = rfPerformanceDao.getReports(Range.inclusive(from, to));
        model.addAttribute("tests", tests);
        
        int totalSuccess = 0;
        int totalFailed = 0;
        int totalUnknown = 0;
        for (PerformanceVerificationEventMessageStats stats : tests) {
            totalSuccess += stats.getNumSuccesses();
            totalFailed += stats.getNumFailures();
            totalUnknown += stats.getNumUnknowns();
        }
        
        model.addAttribute("totalSuccess", totalSuccess);
        model.addAttribute("totalFailed", totalFailed);
        model.addAttribute("totalUnknown", totalUnknown);
        
        return "dr/rf/details.jsp";
    }
    
    private ScheduledRepeatingJob getJob(YukonJobDefinition<? extends YukonTask> jobDefinition) {
        List<ScheduledRepeatingJob> activeJobs = jobManager.getNotDeletedRepeatingJobsByDefinition(jobDefinition);
        return Iterables.getOnlyElement(activeJobs);
    }
    
    @InitBinder
    public void initBinder(WebDataBinder binder, final YukonUserContext userContext) {
        datePropertyEditorFactory.setupInstantPropertyEditor(binder, userContext, BlankMode.CURRENT);
    }
    
}