package com.cannontech.web.dr.ecobee;

import java.beans.PropertyEditorSupport;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
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
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.events.loggers.EcobeeEventLogService;
import com.cannontech.common.events.model.EventSource;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.util.JsonUtils;
import com.cannontech.common.util.Range;
import com.cannontech.common.util.RecentResultsCache;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.core.service.DateFormattingService.DateFormatEnum;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.incrementer.NextValueHelper;
import com.cannontech.dr.assetavailability.dao.DRGroupDeviceMappingDao;
import com.cannontech.dr.ecobee.model.EcobeeDiscrepancyCategory;
import com.cannontech.dr.ecobee.model.EcobeeReadResult;
import com.cannontech.dr.ecobee.model.EcobeeReconciliationReport;
import com.cannontech.dr.ecobee.model.EcobeeReconciliationResult;
import com.cannontech.dr.ecobee.model.discrepancy.EcobeeDiscrepancy;
import com.cannontech.dr.ecobee.service.EcobeeReconciliationService;
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
import com.cannontech.web.dr.ecobee.service.DataDownloadService;
import com.cannontech.web.dr.model.EcobeeSettings;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.cannontech.web.util.WebFileUtils;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

@Controller
@CheckRoleProperty(YukonRoleProperty.SHOW_ECOBEE)
public class EcobeeController {

    private static final Logger log = YukonLogManager.getLogger(EcobeeController.class);
    private static DateTimeFormatter dateTimeFormatter;
    private static final int runtimeReportMaxDateRange = 7;
    private static final String homeKey = "yukon.web.modules.dr.home.ecobee.configure.";
    private static final String fixIssueKey = "yukon.web.modules.dr.ecobee.details.issues.";

    @Autowired private DataDownloadService dataDownloadService;
    @Autowired private DateFormattingService dateFormattingService;
    @Autowired private DRGroupDeviceMappingDao drGroupDeviceMappingDao;

    @Autowired private EcobeeEventLogService ecobeeEventLogService;
    @Autowired private EcobeeReconciliationService ecobeeReconciliation;
    @Autowired private JobManager jobManager;
    @Autowired private NextValueHelper nextValueHelper;
    @Autowired private ScheduledRepeatingJobDao scheduledRepeatingJobDao;
    @Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;
    
    @Autowired @Qualifier("ecobeeReconciliationReport")
        private YukonJobDefinition<EcobeeReconciliationReportTask> ecobeeReconciliationReportJobDef;
    @Autowired @Qualifier("ecobeePointUpdate")
        private YukonJobDefinition<EcobeePointUpdateTask> ecobeePointUpdateJobDef;
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
       
        List<ScheduledRepeatingJob> ecobeePointUpdateJobs  = 
                jobManager.getNotDeletedRepeatingJobsByDefinition(ecobeePointUpdateJobDef);
        if (ecobeePointUpdateJobs == null || ecobeePointUpdateJobs.isEmpty()) {
            log.info("ecobeePointUpdate job doesn't exist. Creating job with default values.");
            ScheduledRepeatingJob job = new ScheduledRepeatingJob();
            job.setBeanName(ecobeePointUpdateJobDef.getName());
            job.setCronString(defaultCron);
            job.setDisabled(true);
            job.setUserContext(null);
            job.setJobGroupId(nextValueHelper.getNextValue("Job"));
            job.setJobDefinition(ecobeePointUpdateJobDef);
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
        ScheduledRepeatingJob ecobeePointUpdateJob = getJob(ecobeePointUpdateJobDef);
        ScheduledRepeatingJob reconciliationReportJob = getJob(ecobeeReconciliationReportJobDef);

        LocalTime pointUpdateTime = ecobeeSettings.getDataCollectionTime();
        String pointUpdateCron = "0 " + pointUpdateTime.getMinuteOfHour() + " " 
                + pointUpdateTime.getHourOfDay() + " * * ?";

        if (!ecobeePointUpdateJob.getCronString().equals(pointUpdateCron)) {
            jobManager.replaceScheduledJob(ecobeePointUpdateJob.getId(), ecobeePointUpdateJobDef, 
                    ecobeePointUpdateJob.getJobDefinition().createBean(), pointUpdateCron, null, 
                    ecobeePointUpdateJob.getJobProperties());
            ecobeePointUpdateJob = getJob(ecobeePointUpdateJobDef);
        }

        LocalTime errorCheckTime = ecobeeSettings.getCheckErrorsTime();
        String errorsCron = "0 " + errorCheckTime.getMinuteOfHour() + " " + errorCheckTime.getHourOfDay() + " * * ?";

        if (!reconciliationReportJob.getCronString().equals(errorsCron)) {
            jobManager.replaceScheduledJob(reconciliationReportJob.getId(), ecobeeReconciliationReportJobDef, 
                    reconciliationReportJob.getJobDefinition().createBean(), errorsCron, null, 
                    reconciliationReportJob.getJobProperties());
            reconciliationReportJob = getJob(ecobeeReconciliationReportJobDef);
        }

        if (ecobeeSettings.isDataCollection() && ecobeePointUpdateJob.isDisabled()) {
            jobManager.enableJob(ecobeePointUpdateJob);
        } else if (!ecobeeSettings.isDataCollection() && !ecobeePointUpdateJob.isDisabled()) {
            jobManager.disableJob(ecobeePointUpdateJob);
        }
        
        if (ecobeeSettings.isCheckErrors() && reconciliationReportJob.isDisabled()) {
            jobManager.enableJob(reconciliationReportJob);
        } else if (!ecobeeSettings.isCheckErrors() && !reconciliationReportJob.isDisabled()) {
            jobManager.disableJob(reconciliationReportJob);
        }
        flash.setConfirm(new YukonMessageSourceResolvable(homeKey + "successful"));
        return "redirect:/dr/home";
    }
    
    @RequestMapping(value="/ecobee/download/start", method=RequestMethod.POST)
    public String ecobeeDataReport(HttpServletResponse response, 
                                 ModelMap model,
                                 Integer[] loadGroupIds,
                                 String ecobeeStartReportDate, 
                                 String ecobeeEndReportDate,
                                 YukonUserContext userContext) throws IOException {

        Map<String, String> errResponse = Maps.newHashMap();
        boolean isValidationError = false;
        LocalDate startDate = null;
        try {
            startDate = dateFormattingService.parseLocalDate(ecobeeStartReportDate, userContext);
        } catch (ParseException e) {
            log.error(e);
            errResponse.put("startDateError", "true");
            isValidationError = true;
        }

        LocalDate endDate = null;
        try {
            endDate = dateFormattingService.parseLocalDate(ecobeeEndReportDate, userContext);
        } catch (ParseException e) {
            log.error(e);
            errResponse.put("endDateError", "true");
            isValidationError = true;
        }

        if (isValidationError) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            JsonUtils.getWriter().writeValue(response.getOutputStream(), errResponse);
            return null;
        }

        int durationIndays = Days.daysBetween(startDate, endDate).getDays();
        if (runtimeReportMaxDateRange <= durationIndays || startDate.isAfter(endDate)) {
            errResponse.put("dateRangeError", "true");
            isValidationError = true;
            response.setStatus(HttpStatus.BAD_REQUEST.value());
        }

        List<String> serialNumbers = null;
        if (loadGroupIds == null) {
            // Load groups are required.
            errResponse.put("loadgroupsUnspecified", "true");
            isValidationError = true;
            response.setStatus(HttpStatus.BAD_REQUEST.value());
        } else {
            serialNumbers = drGroupDeviceMappingDao.getSerialNumbersForLoadGroups(Lists.newArrayList(loadGroupIds));
            if (serialNumbers.isEmpty()) {
                // If list of serialNumbers is empty, tell client.
                errResponse.put("loadgroupsMissingSerialNumbers", "true");
                isValidationError = true;
                response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            }
        }
        if (isValidationError == true) {
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            JsonUtils.getWriter().writeValue(response.getOutputStream(), errResponse);
            return null;
        }
        
        String resultKey = dataDownloadService.start(serialNumbers, Range.inclusive(startDate, endDate), userContext);

        ecobeeEventLogService.dataDownloaded(userContext.getYukonUser(), startDate, endDate.plusDays(1),
            Arrays.toString(loadGroupIds), EventSource.OPERATOR);
        
        model.addAttribute("key", resultKey);
        EcobeeReadResult result = readResultsCache.getResult(resultKey);

        DateTimeFormatter formatter =
            dateFormattingService.getDateTimeFormatter(DateFormatEnum.DATE_YYYYMMdd, YukonUserContext.system);
        log.info("startDate: " + result.getStartDateRange().toString(formatter) + " endDate: "
            + result.getEndDateRange().toString(formatter));
        model.addAttribute("download", result);
        model.addAttribute("hideRow", true);

        return "dr/ecobee/download.row.jsp";
    }
    
    @RequestMapping("/ecobee/download")
    public void download(HttpServletResponse response, String key) throws IOException {
        
        EcobeeReadResult result = readResultsCache.getResult(key);

        if (!result.isComplete()) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
        } else {
            DateTimeFormatter formatter =
                dateFormattingService.getDateTimeFormatter(DateFormatEnum.DATE_YYYYMMdd, YukonUserContext.system);
            String startDate = result.getStartDateRange().toString(formatter);
            String endDate = result.getEndDateRange().toString(formatter);
            log.info("startDate: " + startDate + " endDate: " + endDate);
            WebFileUtils.writeToCSV(response, result.getFile(), "ecobee_data_" + startDate + "_" + endDate + ".csv");
        }
    }
    
    @RequestMapping("/ecobee/download/settings")
    public String downloadSettings(ModelMap model) {

        Date now = new Date();
        model.addAttribute("now", now);
        model.addAttribute("oneDayAgo", new DateTime(now).minusDays(1).toDate());
        return "dr/ecobee/download.jsp";
    }
    
    @RequestMapping(value="/ecobee/statistics", method=RequestMethod.GET)
    public String statistics(ModelMap model, LiteYukonUser user) {
        
        ScheduledRepeatingJob reconciliationReportJob = getJob(ecobeeReconciliationReportJobDef);
        ScheduledRepeatingJob ecobeePointUpdateJob = getJob(ecobeePointUpdateJobDef);

        EcobeeSettings ecobeeSettings = new EcobeeSettings();
        ecobeeSettings.setCheckErrors(!reconciliationReportJob.isDisabled());
        ecobeeSettings.setDataCollection(!ecobeePointUpdateJob.isDisabled());
        try {
            Date now = new Date();
            Date nextErrorCheck = jobManager.getNextRuntime(reconciliationReportJob, now);
            ecobeeSettings.setCheckErrorsTime(LocalTime.fromDateFields(nextErrorCheck));
            Date nextPointUpdate = jobManager.getNextRuntime(ecobeePointUpdateJob, now);
            ecobeeSettings.setDataCollectionTime(LocalTime.fromDateFields(nextPointUpdate));
        } catch (IllegalArgumentException | ScheduleException e) {
            ecobeeSettings.setCheckErrorsTime(LocalTime.MIDNIGHT);
            ecobeeSettings.setDataCollectionTime(LocalTime.MIDNIGHT);
            log.error("Unable to retrieve ecobee job schedules. ", e);
        }
        model.addAttribute("ecobeeSettings", ecobeeSettings);

        int deviceIssues = 0;
        int groupIssues = 0;
        EcobeeReconciliationReport report = ecobeeReconciliation.findReconciliationReport();
        if (report != null) {
            deviceIssues = report.getErrorNumberByCategory(EcobeeDiscrepancyCategory.DEVICE);
            groupIssues = report.getErrorNumberByCategory(EcobeeDiscrepancyCategory.GROUP);
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
        
        EcobeeReconciliationReport report = ecobeeReconciliation.findReconciliationReport();
        model.addAttribute("report", report);

        return "dr/ecobee/details.jsp";
    }
    
    @RequestMapping(value="/ecobee/runReport")
    public String runReport() {
        ecobeeReconciliation.runReconciliationReport();
        return "";
    }

    @RequestMapping(value="/ecobee/fixIssue", method=RequestMethod.POST)
    public @ResponseBody Map<String, Object> fixIssue(
            HttpServletResponse response,
            YukonUserContext userContext,
            Integer reportId,
            Integer errorId
            ) throws IllegalArgumentException {
        
        EcobeeReconciliationResult result = null;
        Map<String, Object> json = new HashMap<>();
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        try {
            result = ecobeeReconciliation.fixDiscrepancy(reportId, errorId);
            ecobeeEventLogService.syncIssueFixed(userContext.getYukonUser(), 
                                                 result.getOriginalDiscrepancy().getErrorType().toString(), 
                                                 EventSource.OPERATOR);
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

    @RequestMapping(value="/ecobee/fix-all", method=RequestMethod.GET)
    public @ResponseBody List<Map<String, Object>> fixAllIssues(
            YukonUserContext userContext,
            Integer reportId,
            FlashScope flash) {
        
        List<Map<String, Object>> fixResponse = new ArrayList<>();
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        try {
            List<EcobeeReconciliationResult> results = ecobeeReconciliation.fixAllDiscrepancies(reportId);
            for (EcobeeReconciliationResult result: results) {
                EcobeeDiscrepancy originalError = result.getOriginalDiscrepancy();
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
