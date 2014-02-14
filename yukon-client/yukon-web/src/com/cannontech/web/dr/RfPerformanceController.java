package com.cannontech.web.dr;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.search.result.SearchResults;
import com.cannontech.common.util.Range;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.dr.model.PerformanceVerificationEventMessageStats;
import com.cannontech.dr.rfn.dao.PerformanceVerificationDao;
import com.cannontech.dr.rfn.model.UnknownDevice;
import com.cannontech.dr.rfn.model.UnknownDevices;
import com.cannontech.dr.rfn.model.UnknownStatus;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
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
import com.cannontech.web.util.JsonUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;

@Controller
@CheckRoleProperty(YukonRoleProperty.DEMAND_RESPONSE)
public class RfPerformanceController {

    private static final String baseKey = "yukon.web.modules.dr.home.rfPerformance.";
    private static final Logger log = YukonLogManager.getLogger(RfPerformanceController.class);
    
    @Autowired private PaoDao paoDao;
    @Autowired private JobManager jobManager;
    @Autowired private ScheduledRepeatingJobDao jobDao;
    @Autowired private PerformanceVerificationDao rfPerformanceDao;
    @Autowired private DatePropertyEditorFactory datePropertyEditorFactory;
    @Autowired private YukonUserContextMessageSourceResolver resolver;
    
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
            
            flash.setConfirm(new YukonMessageSourceResolvable(baseKey + "configure.success"));
        } catch (Exception e) {
            flash.setError(new YukonMessageSourceResolvable(baseKey + "configure.failed"));
            log.error("Failed to update RF Broadcast Performance Email Job", e);
        }
        
        return "redirect:/dr/home";
    }
    
    @RequestMapping(value="/rf/details", method=RequestMethod.GET)
    public String details(ModelMap model, @RequestParam(required=false) Instant from, @RequestParam(required=false) Instant to) {
        
        if (from == null) from = new Instant().minus(Duration.standardDays(7));
        if (to == null) to = new Instant();
        to = to.plus(Duration.standardDays(1)).toDateTime().toDateMidnight().toInstant();
        
        model.addAttribute("from", from);
        model.addAttribute("to", to);
        
        List<PerformanceVerificationEventMessageStats> tests = rfPerformanceDao.getReports(Range.inclusiveExclusive(from, to));
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
    
    @RequestMapping(value="/rf/details/unknown/{test}", method=RequestMethod.GET)
    public String unknown(ModelMap model, HttpServletResponse resp,
            YukonUserContext userContext,
            @PathVariable long test, 
            @RequestParam(defaultValue="10") Integer itemsPerPage, 
            @RequestParam(defaultValue="1") Integer page) throws JsonProcessingException {
        
        MessageSourceAccessor accessor = resolver.getMessageSourceAccessor(userContext);
        
        UnknownDevices unknownDevices = rfPerformanceDao.getDevicesWithUnknownStatus(test);
        SearchResults<UnknownDevice> result = 
                SearchResults.pageBasedForWholeList(page, itemsPerPage, unknownDevices.getUnknownDevices());
        
        model.addAttribute("result", result);
        
        List<Map<String, Object>> data = new ArrayList<>();
        
        Map<String, Object> stat = Maps.newHashMapWithExpectedSize(3);
        stat.put("label", accessor.getMessage(UnknownStatus.ACTIVE));
        stat.put("data", unknownDevices.getNumActive());
        stat.put("color", "#009933");
        data.add(stat);
        
        stat = Maps.newHashMapWithExpectedSize(3);
        stat.put("label", accessor.getMessage(UnknownStatus.INACTIVE));
        stat.put("data", unknownDevices.getNumInactive());
        stat.put("color", "#888888");
        data.add(stat);
        
        stat = Maps.newHashMapWithExpectedSize(3);
        stat.put("label", accessor.getMessage(UnknownStatus.UNAVAILABLE));
        stat.put("data", unknownDevices.getNumUnavailable());
        stat.put("color", "#fb8521");
        data.add(stat);
        
        stat = Maps.newHashMapWithExpectedSize(3);
        stat.put("label", accessor.getMessage(UnknownStatus.UNREPORTED_NEW));
        stat.put("data", unknownDevices.getNumUnreportedNew());
        stat.put("color", "#4d90fe");
        data.add(stat);
        
        stat = Maps.newHashMapWithExpectedSize(3);
        stat.put("label", accessor.getMessage(UnknownStatus.UNREPORTED_OLD));
        stat.put("data", unknownDevices.getNumUnreportedOld());
        stat.put("color", "#D14836");
        data.add(stat);
        
        resp.addHeader("X-JSON", JsonUtils.toJson(data));
        model.addAttribute("test", test);
        model.addAttribute("unknownDevices", unknownDevices);

        return "dr/rf/unknown.jsp";
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