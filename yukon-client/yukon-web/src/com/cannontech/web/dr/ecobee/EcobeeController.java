package com.cannontech.web.dr.ecobee;

import java.beans.PropertyEditorSupport;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.DateTimeFieldType;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestClientException;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.util.RecentResultsCache;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.core.service.DateFormattingService.DateFormatEnum;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.incrementer.NextValueHelper;
import com.cannontech.dr.ecobee.EcobeeAuthenticationException;
import com.cannontech.dr.ecobee.EcobeeCommunicationException;
import com.cannontech.dr.ecobee.model.EcobeeReadResult;
import com.cannontech.dr.ecobee.model.EcobeeZeusDiscrepancyCategory;
import com.cannontech.dr.ecobee.model.EcobeeZeusReconciliationReport;
import com.cannontech.dr.ecobee.model.EcobeeZeusReconciliationResult;
import com.cannontech.dr.ecobee.model.discrepancy.EcobeeZeusDiscrepancy;
import com.cannontech.dr.ecobee.service.EcobeeZeusReconciliationService;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.jobs.dao.ScheduledRepeatingJobDao;
import com.cannontech.jobs.model.ScheduledRepeatingJob;
import com.cannontech.jobs.service.JobManager;
import com.cannontech.jobs.support.JobManagerException;
import com.cannontech.jobs.support.ScheduleException;
import com.cannontech.jobs.support.YukonJobDefinition;
import com.cannontech.jobs.support.YukonTask;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.dr.model.EcobeeSettings;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.google.common.collect.Iterables;

@Controller
@CheckRoleProperty(YukonRoleProperty.SHOW_ECOBEE)
public class EcobeeController {

    private static final Logger log = YukonLogManager.getLogger(EcobeeController.class);
    private static DateTimeFormatter dateTimeFormatter;
    private static final String homeKey = "yukon.web.modules.dr.home.ecobee.configure.";
    private static final String fixIssueKey = "yukon.web.modules.dr.ecobee.details.issues.";

    @Autowired private DateFormattingService dateFormattingService;
    @Autowired private EcobeeZeusReconciliationService ecobeeZeusReconciliationService;
    @Autowired private JobManager jobManager;
    @Autowired private NextValueHelper nextValueHelper;
    @Autowired private ScheduledRepeatingJobDao scheduledRepeatingJobDao;
    @Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;

    
    @Autowired @Qualifier("ecobeeReconciliationReport")
        private YukonJobDefinition<EcobeeReconciliationReportTask> ecobeeReconciliationReportJobDef;
    @Autowired @Qualifier("ecobeeReads") RecentResultsCache<EcobeeReadResult> readResultsCache;

    private static final Comparator<EcobeeReadResult> readResultStartDateComparator = new Comparator<EcobeeReadResult>() {
        @Override
        public int compare(EcobeeReadResult result1, EcobeeReadResult result2) {
            int value = result2.getStartDate().compareTo(result1.getStartDate());
            return value;
        }
    };
    
    @PostConstruct
    public void init() {
        String defaultCron = "0 0 0 * * ?";// every day at 12am

        try {
            List<ScheduledRepeatingJob> reconciliationReportJobs =
                jobManager.getNotDeletedRepeatingJobsByDefinition(ecobeeReconciliationReportJobDef);
       
        if (reconciliationReportJobs == null || reconciliationReportJobs.isEmpty()) {
            log.info("ecobeeReconciliationReport job doesn't exist. Creating job with default values.");
            ScheduledRepeatingJob job = new ScheduledRepeatingJob();
            job.setBeanName(ecobeeReconciliationReportJobDef.getName());
            job.setCronString(defaultCron);
            job.setDisabled(true);
            job.setUserContext(null);
            job.setJobGroupId(nextValueHelper.getNextValue("Job"));
            job.setJobDefinition(ecobeeReconciliationReportJobDef);
            job.setJobProperties(Collections.<String, String>emptyMap());
            scheduledRepeatingJobDao.save(job);
            jobManager.instantiateTask(job);
            }
        } catch (JobManagerException e) {
            log.warn(e.getMessage());
        }

        dateTimeFormatter = dateFormattingService.getDateTimeFormatter(DateFormatEnum.DATEHM, YukonUserContext.system);
    }

    @RequestMapping(value="/ecobee/settings", method=RequestMethod.POST)
    public String saveSettings(EcobeeSettings ecobeeSettings, FlashScope flash) {
        ScheduledRepeatingJob reconciliationReportJob = getJob(ecobeeReconciliationReportJobDef);

        LocalTime errorCheckTime = ecobeeSettings.getCheckErrorsTime();
        String errorsCron = "0 " + errorCheckTime.getMinuteOfHour() + " " + errorCheckTime.getHourOfDay() + " * * ?";

        if (!reconciliationReportJob.getCronString().equals(errorsCron)) {
            jobManager.replaceScheduledJob(reconciliationReportJob.getId(), ecobeeReconciliationReportJobDef, 
                    reconciliationReportJob.getJobDefinition().createBean(), errorsCron, null, 
                    reconciliationReportJob.getJobProperties());
            reconciliationReportJob = getJob(ecobeeReconciliationReportJobDef);
        }
        
        if (ecobeeSettings.isCheckErrors() && reconciliationReportJob.isDisabled()) {
            jobManager.enableJob(reconciliationReportJob);
        } else if (!ecobeeSettings.isCheckErrors() && !reconciliationReportJob.isDisabled()) {
            jobManager.disableJob(reconciliationReportJob);
        }
        flash.setConfirm(new YukonMessageSourceResolvable(homeKey + "successful"));
        return "redirect:/dr/home";
    }

    @RequestMapping(value="/ecobee/statistics", method=RequestMethod.GET)
    public String statistics(ModelMap model, LiteYukonUser user) {
        
        ScheduledRepeatingJob reconciliationReportJob = getJob(ecobeeReconciliationReportJobDef);

        EcobeeSettings ecobeeSettings = new EcobeeSettings();
        ecobeeSettings.setCheckErrors(!reconciliationReportJob.isDisabled());
        try {
            Date now = new Date();
            Date nextErrorCheck = jobManager.getNextRuntime(reconciliationReportJob, now);
            ecobeeSettings.setCheckErrorsTime(LocalTime.fromDateFields(nextErrorCheck));
        } catch (IllegalArgumentException | ScheduleException e) {
            ecobeeSettings.setCheckErrorsTime(LocalTime.MIDNIGHT);
            ecobeeSettings.setDataCollectionTime(LocalTime.MIDNIGHT);
            log.error("Unable to retrieve ecobee job schedules. ", e);
        }
        model.addAttribute("ecobeeSettings", ecobeeSettings);

        int deviceIssues = 0;
        int groupIssues = 0;

        EcobeeZeusReconciliationReport report = ecobeeZeusReconciliationService.findReconciliationReport();
        if (report != null) {
            deviceIssues = report.getErrorNumberByCategory(EcobeeZeusDiscrepancyCategory.DEVICE);
            groupIssues = report.getErrorNumberByCategory(EcobeeZeusDiscrepancyCategory.GROUP);
        }

        model.addAttribute("deviceIssues", deviceIssues);
        model.addAttribute("groupIssues", groupIssues);

        return "dr/ecobee/statistics.jsp";
    }

    @RequestMapping(value="/ecobee", method=RequestMethod.GET)
    public String details(ModelMap model, YukonUserContext userContext) {

        List<String> pendingKeys = readResultsCache.getSortedPendingKeys(readResultStartDateComparator);
        List<String> completedKeys = readResultsCache.getSortedCompletedKeys(readResultStartDateComparator);
        //Use LinkedHashMap to maintain sorted ordering
        Map<String, EcobeeReadResult> downloads = new LinkedHashMap<>();
        int pendingIndex;
        for (pendingIndex = 0; pendingIndex < 5 && pendingIndex < pendingKeys.size(); pendingIndex++) {
            String key = pendingKeys.get(pendingIndex);
            EcobeeReadResult result = readResultsCache.getResult(key);
            downloads.put(key, result);
        }
        int completedIndex;
        int completedsToAdd = 5 - pendingIndex;
        for (completedIndex = 0; completedIndex < completedsToAdd && completedIndex < completedKeys.size(); completedIndex++) {
            String key = completedKeys.get(completedIndex);
            EcobeeReadResult result = readResultsCache.getResult(key);
            downloads.put(key, result);
        }
        Date now = new Date();
        Date oneDayAgo = new DateTime(now).minusDays(1).toDate();
        model.addAttribute("now", now);
        model.addAttribute("oneDayAgo", oneDayAgo);
        model.addAttribute("downloads", downloads);
        
        EcobeeZeusReconciliationReport report = ecobeeZeusReconciliationService.findReconciliationReport();
        model.addAttribute("report", report);

        return "dr/ecobee/details.jsp";
    }
    
    @RequestMapping(value="/ecobee/runReport")
    public String runReport() throws RestClientException, EcobeeCommunicationException, EcobeeAuthenticationException {
        ecobeeZeusReconciliationService.runReconciliationReport();
        return "";
    }

    @RequestMapping(value="/ecobee/fixIssue", method=RequestMethod.POST)
    public @ResponseBody Map<String, Object> fixIssue(
            HttpServletResponse response,
            YukonUserContext userContext,
            Integer reportId,
            Integer errorId
            ) throws IllegalArgumentException, RestClientException, EcobeeAuthenticationException {
        
        
        Map<String, Object> json = new HashMap<>();
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(userContext);
      
        EcobeeZeusReconciliationResult result = null;
        try {
            result = ecobeeZeusReconciliationService.fixDiscrepancy(reportId, errorId, userContext.getYukonUser());
            if (result.isSuccess()) {
                json.put("success", "true");
            } else {
                json.put("success", "false");
                String message = accessor.getMessage(result.getErrorType());
                json.put("message", message);
                response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            }
        } catch (IllegalArgumentException e) {
            json.put("success", "false");
            String message = accessor.getMessage(result.getErrorType());
            json.put("message", message);
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }

        return json;
    }

    @RequestMapping(value = "/ecobee/fix-all", method = RequestMethod.GET)
    public @ResponseBody List<Map<String, Object>> fixAllIssues(
            YukonUserContext userContext,
            Integer reportId,
            FlashScope flash) throws RestClientException, EcobeeAuthenticationException {

        List<Map<String, Object>> fixResponse = new ArrayList<>();
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(userContext);

        try {
            List<EcobeeZeusReconciliationResult> results = ecobeeZeusReconciliationService.fixAllDiscrepancies(reportId, userContext.getYukonUser());
            for (EcobeeZeusReconciliationResult result : results) {
                EcobeeZeusDiscrepancy originalError = result.getOriginalDiscrepancy();
                Integer originalErrorId = originalError.getErrorId();
                Boolean success = result.isSuccess();
                Map<String, Object> json = new HashMap<>();
                json.put("originalErrorId", originalErrorId);
                json.put("success", success);
                if (!success) {
                    String fixErrorString = accessor.getMessage(result.getErrorType());
                    json.put("fixErrorString", fixErrorString);
                }
                fixResponse.add(json);
            }
        } catch (IllegalArgumentException e) {
            flash.setError(new YukonMessageSourceResolvable(fixIssueKey + "fixFailed"));
        }

        return fixResponse;
    }

    @InitBinder
    public void initialize(WebDataBinder dataBinder) {
        PropertyEditorSupport localDateEditor = new PropertyEditorSupport() {
            @Override
            public String getAsText() {
                LocalTime value = (LocalTime) getValue();
                if (value == null) {
                    value = LocalTime.MIDNIGHT;
                }
                int minutesOfDay = value.get(DateTimeFieldType.minuteOfDay());
                return Integer.toString(minutesOfDay);
            }
            @Override
            public void setAsText(String text) throws IllegalArgumentException {
                try {
                    int minutesSinceMidnight = Integer.parseInt(text);
                    setValue(LocalTime.MIDNIGHT.plusMinutes(minutesSinceMidnight));
                } catch (NumberFormatException e) {
                    setValue(null);
                }
            }
        };
        dataBinder.registerCustomEditor(LocalTime.class, localDateEditor);
    }

    private ScheduledRepeatingJob getJob(YukonJobDefinition<? extends YukonTask> jobDefinition) {
        List<ScheduledRepeatingJob> activeJobs = jobManager.getNotDeletedRepeatingJobsByDefinition(jobDefinition);
        return Iterables.getOnlyElement(activeJobs);
    }
}
