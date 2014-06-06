package com.cannontech.web.dr.ecobee;

import java.beans.PropertyEditorSupport;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.DateTimeFieldType;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.joda.time.LocalTime;
import org.joda.time.YearMonth;
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

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.i18n.DisplayableEnum;
import com.cannontech.common.util.MonthYear;
import com.cannontech.common.util.Range;
import com.cannontech.common.util.RecentResultsCache;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.core.service.DateFormattingService.DateFormatEnum;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.dr.assetavailability.dao.DRGroupDeviceMappingDao;
import com.cannontech.dr.ecobee.dao.EcobeeQueryCountDao;
import com.cannontech.dr.ecobee.dao.EcobeeQueryType;
import com.cannontech.dr.ecobee.model.EcobeeDiscrepancyCategory;
import com.cannontech.dr.ecobee.model.EcobeeQueryStatistics;
import com.cannontech.dr.ecobee.model.EcobeeReadResult;
import com.cannontech.dr.ecobee.model.EcobeeReconciliationReport;
import com.cannontech.dr.ecobee.service.EcobeeCommunicationService;
import com.cannontech.dr.ecobee.service.EcobeeReconciliationService;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.jobs.dao.ScheduledRepeatingJobDao;
import com.cannontech.jobs.model.ScheduledRepeatingJob;
import com.cannontech.jobs.service.JobManager;
import com.cannontech.jobs.support.ScheduleException;
import com.cannontech.jobs.support.YukonJobDefinition;
import com.cannontech.jobs.support.YukonTask;
import com.cannontech.stars.core.dao.EnergyCompanyDao;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.dr.ecobee.service.DataDownloadService;
import com.cannontech.web.dr.model.EcobeeQueryStats;
import com.cannontech.web.dr.model.EcobeeSettings;
import com.cannontech.web.input.DatePropertyEditorFactory;
import com.cannontech.web.loadcontrol.tasks.RepeatingWeatherDataTask;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.cannontech.web.util.WebFileUtils;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

@Controller
@CheckRoleProperty(YukonRoleProperty.SHOW_ECOBEE)
public class EcobeeController {

    private static final Logger log = YukonLogManager.getLogger(EcobeeController.class);
    private static DateTimeFormatter dateTimeFormatter;
    private static final String homeKey = "yukon.web.modules.dr.home.ecobee.configure.";

    @Autowired private EcobeeCommunicationService ecobeeCommunicationService;
    @Autowired private EnergyCompanyDao ecDao;
    
    @Autowired private EcobeeQueryCountDao ecobeeQueryCountDao;
    @Autowired private DatePropertyEditorFactory datePropertyEditorFactory;
    @Autowired private ScheduledRepeatingJobDao scheduledRepeatingJobDao;
    @Autowired private JobManager jobManager;
    @Autowired @Qualifier("ecobeeReconciliationReport")
        private YukonJobDefinition<RepeatingWeatherDataTask> ecobeeReconciliationReportJobDef;
    @Autowired @Qualifier("ecobeePointUpdate")
        private YukonJobDefinition<RepeatingWeatherDataTask> ecobeePointUpdateJobDef;
    @Autowired private DateFormattingService dateFormattingService;
    @Autowired @Qualifier("ecobeeReads") RecentResultsCache<EcobeeReadResult> readResultsCache;
    @Autowired private EcobeeReconciliationService ecobeeReconciliation;
    @Autowired private DataDownloadService dataDownloadService;
    @Autowired private DRGroupDeviceMappingDao drGroupDeviceMappingDao;

    @PostConstruct
    public void init() {
        String defaultCron = "0 0 0 * * ?";// every day at 12am

        List<ScheduledRepeatingJob> reconciliationReportJobs = 
                jobManager.getNotDeletedRepeatingJobsByDefinition(ecobeeReconciliationReportJobDef);
        if (reconciliationReportJobs == null || reconciliationReportJobs.isEmpty()) {
            log.info("ecobeeReconciliationReport job doesn't exist. Creating job with default values.");
            ScheduledRepeatingJob job = new ScheduledRepeatingJob();
            job.setBeanName(ecobeeReconciliationReportJobDef.getName());
            job.setCronString(defaultCron);
            job.setDisabled(true);
            job.setUserContext(null);
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
            job.setJobDefinition(ecobeePointUpdateJobDef);
            job.setJobProperties(Collections.<String, String>emptyMap());
            scheduledRepeatingJobDao.save(job);
            jobManager.instantiateTask(job);
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
                                 String ecobeeEndReportDate) throws IOException {

        Instant startDate = new Instant(dateTimeFormatter.parseMillis(ecobeeStartReportDate));
        Instant endDate = new Instant(dateTimeFormatter.parseMillis(ecobeeEndReportDate));
        
        Duration specifiedDuration = new Duration(startDate, endDate);
        
        if (specifiedDuration.isLongerThan(Duration.standardDays(7))) {
            log.debug("bogus date range: " + specifiedDuration);
            response.setStatus(400);
            DateTime now = new DateTime();
            model.addAttribute("now", now);
            model.addAttribute("oneDayAgo", new DateTime(now.minusDays(1)));
            
            return "dr/ecobee/download.jsp";
        }
        
        if (loadGroupIds == null) {
            // Load groups are required.
            response.setStatus(400);
            DateTime now = new DateTime();
            model.addAttribute("now", now);
            model.addAttribute("oneDayAgo", new DateTime(now.minusDays(1)));
            
            return "dr/ecobee/download.jsp";
        }
        List<String> serialNumbers = drGroupDeviceMappingDao.getSerialNumbersForLoadGroups(Lists.newArrayList(loadGroupIds));
        
        String resultKey = dataDownloadService.start(serialNumbers, Range.inclusive(startDate, endDate));
        
        model.addAttribute("key", resultKey);
        EcobeeReadResult result = readResultsCache.getResult(resultKey);
        
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
            WebFileUtils.writeToCSV(response, result.getFile(), "ecobee_data_" + Instant.now().getMillis() + ".csv");
        }
    }
    
    @RequestMapping("/ecobee/download/settings")
    public String downloadSettings(ModelMap model) {
        
        DateTime now = new DateTime();
        model.addAttribute("now", now);
        model.addAttribute("oneDayAgo", new DateTime(now.minusDays(1)));
        
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
        } catch (ScheduleException e) {
        ecobeeSettings.setCheckErrorsTime(LocalTime.MIDNIGHT);
        ecobeeSettings.setDataCollectionTime(LocalTime.MIDNIGHT);
            log.error("Unable to retrieve ecobee job schedules. ", e);
        }
        model.addAttribute("ecobeeSettings", ecobeeSettings);

        EcobeeQueryStatistics currentMonthStats = ecobeeQueryCountDao.getCountsForMonth(MonthYear.now());
        EcobeeQueryStats queryStats = new EcobeeQueryStats(currentMonthStats);
        model.addAttribute("ecobeeStats", queryStats);

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

        //get stats across a range of months
        MonthYear currentMonth = MonthYear.now();
        MonthYear yearAgoMonth = currentMonth.minus(0, 1);
        Range<MonthYear> range = Range.inclusive(yearAgoMonth, currentMonth);

        List<EcobeeQueryStatistics> rangeOfStatsList = ecobeeQueryCountDao.getCountsForRange(range);
        List<EcobeeQueryStats> queryStatsList = new ArrayList<>();
        for (EcobeeQueryStatistics stats : rangeOfStatsList) {
            int statsMonth = stats.getMonth();
            int statsYear = stats.getYear();
            YearMonth month = new YearMonth().withYear(statsYear).withMonthOfYear(statsMonth);
            int demandResponseCount = stats.getQueryCountByType(EcobeeQueryType.DEMAND_RESPONSE);
            int dataCollectionCount = stats.getQueryCountByType(EcobeeQueryType.DATA_COLLECTION);
            int systemCount = stats.getQueryCountByType(EcobeeQueryType.SYSTEM);
            EcobeeQueryStats queryStats =
                new EcobeeQueryStats(month, demandResponseCount, dataCollectionCount, systemCount);
            queryStatsList.add(queryStats);
        }
        // begin unit test
        if (rangeOfStatsList.isEmpty()) {
            // fake data for testing
            Random rand = new Random();
            DateTime dateTime = new DateTime();
            int maxTestVal = 100000;
            for (int i = 0; i < 12; i += 1) {
                int demandResponseCount = rand.nextInt(maxTestVal);
                int dataCollectionCount = rand.nextInt(maxTestVal - demandResponseCount);
                int systemCount = rand.nextInt(maxTestVal - demandResponseCount - dataCollectionCount);
                int month = dateTime.minusMonths(i).getMonthOfYear();
                int year = dateTime.minusMonths(i).getYear();
                YearMonth yearMonth = new YearMonth().withYear(year).withMonthOfYear(month);
                EcobeeQueryStats queryStats =
                    new EcobeeQueryStats(yearMonth, demandResponseCount, dataCollectionCount, systemCount);
                queryStatsList.add(queryStats);
            }
        }
        model.addAttribute("statsList", queryStatsList);
        // TODO: fetch data download info
        List<String> pendingKeys = readResultsCache.getPendingKeys();
        List<String> completedKeys = readResultsCache.getCompletedKeys();
        Map<String, EcobeeReadResult> downloads = new HashMap<>();
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
        DateTime now = new DateTime();
        DateTime oneDayAgo = new DateTime(now.minusDays(1));
        model.addAttribute("now", now);
        model.addAttribute("oneDayAgo", oneDayAgo);
        model.addAttribute("downloads", downloads);

        EcobeeReconciliationReport report = ecobeeReconciliation.findReconciliationReport();
        model.addAttribute("report", report);

        return "dr/ecobee/details.jsp";
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

    // TODO: replace with EcobeeDiscrepancy
    public class EcobeeSyncIssue {

        private EcobeeSyncIssueType type;
        private String serialNumber;
        private String loadGroupName;
        private String issueName;

        public EcobeeSyncIssueType getType() {
            return type;
        }
        public void setType(EcobeeSyncIssueType type) {
            this.type = type;
        }
        public String getSerialNumber() {
            return serialNumber;
        }
        public void setSerialNumber(String serialNumber) {
            this.serialNumber = serialNumber;
        }
        public String getLoadGroupName() {
            return loadGroupName;
        }
        public void setLoadGroupName(String loadGroupName) {
            this.loadGroupName = loadGroupName;
        }
        public String getIssueName() {
            return issueName;
        }
        public void setIssueName(String issueName) {
            this.issueName = issueName;
        }
    }

    // TODO: replace with EcobeeDiscrepancyType
    public enum EcobeeSyncIssueType implements DisplayableEnum {

        DEVICE_NOT_IN_ECOBEE(false),
        DEVICE_NOT_IN_YUKON(false),
        LOAD_GROUP_NOT_IN_ECOBEE(true),  // User should be able to create load group in ecobee system and auto populate it with devices from matching yukon load group.
        ECOBEE_ENROLLMENT_INCORRECT(true), // User should be able to move devices to correct ecobee load group in ecobee system.
        ECOBEE_SET_DOES_NOT_MATCH(false),
        ECOBEE_SET_IN_INCORRECT_LOCATION(true);

        private final boolean fixable;
        private EcobeeSyncIssueType (boolean fixable) {
            this.fixable = fixable;
        }

        public boolean isFixable() {
            return fixable;
        }

        public boolean isDeviceIssue() {
            return this == DEVICE_NOT_IN_ECOBEE || this == DEVICE_NOT_IN_YUKON;
        }

        @Override
        public String getFormatKey() {
            return "yukon.web.modules.dr.ecobee.details." + name();
        }
    }

    // TODO: remove, now using real data
    public class EcobeeDataDownload {
        private DateTime startDate;
        private DateTime endDate;
        private boolean downloadFinished = false;
        
        public EcobeeDataDownload(DateTime startDate, DateTime endDate,
                boolean downloadFinished) {
            super();
            this.startDate = startDate;
            this.endDate = endDate;
            this.downloadFinished = downloadFinished;
        }
        public EcobeeDataDownload(DateTime startDate, boolean downloadFinished) {
            super();
            this.startDate = startDate;
            this.endDate = null;
            this.downloadFinished = downloadFinished;
        }
        public DateTime getStartDate() {
            return startDate;
        }
        public void setStartDate(DateTime startDate) {
            this.startDate = startDate;
        }
        public DateTime getEndDate() {
            return endDate;
        }
        public void setEndDate(DateTime endDate) {
            this.endDate = endDate;
        }
        public boolean isDownloadFinished() {
            return downloadFinished;
        }
        public void setDownloadFinished(boolean downloadFinished) {
            this.downloadFinished = downloadFinished;
        }
    }
    
    private ScheduledRepeatingJob getJob(YukonJobDefinition<? extends YukonTask> jobDefinition) {
        List<ScheduledRepeatingJob> activeJobs = jobManager.getNotDeletedRepeatingJobsByDefinition(jobDefinition);
        return Iterables.getOnlyElement(activeJobs);
    }
}
