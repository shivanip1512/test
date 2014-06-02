package com.cannontech.web.dr.ecobee;

import java.beans.PropertyEditorSupport;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.DateTimeFieldType;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.joda.time.LocalTime;
import org.joda.time.YearMonth;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
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
import com.cannontech.dr.ecobee.EcobeeCommunicationException;
import com.cannontech.dr.ecobee.dao.EcobeeQueryCountDao;
import com.cannontech.dr.ecobee.dao.EcobeeQueryType;
import com.cannontech.dr.ecobee.model.EcobeeDeviceReading;
import com.cannontech.dr.ecobee.model.EcobeeDeviceReadings;
import com.cannontech.dr.ecobee.model.EcobeeDiscrepancyType;
import com.cannontech.dr.ecobee.model.EcobeeQueryStatistics;
import com.cannontech.dr.ecobee.model.EcobeeReadResult;
import com.cannontech.dr.ecobee.model.EcobeeReconciliationReport;
import com.cannontech.dr.ecobee.model.discrepancy.EcobeeDiscrepancy;
import com.cannontech.dr.ecobee.service.EcobeeCommunicationService;
import com.cannontech.dr.ecobee.service.EcobeeReconciliationService;
import com.cannontech.jobs.dao.ScheduledRepeatingJobDao;
import com.cannontech.jobs.model.ScheduledRepeatingJob;
import com.cannontech.jobs.service.JobManager;
import com.cannontech.jobs.support.ScheduleException;
import com.cannontech.jobs.support.YukonJobDefinition;
import com.cannontech.jobs.support.YukonTask;
import com.cannontech.stars.core.dao.EnergyCompanyDao;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.dr.model.EcobeeQueryStats;
import com.cannontech.web.dr.model.EcobeeSettings;
import com.cannontech.web.input.DatePropertyEditorFactory;
import com.cannontech.web.loadcontrol.tasks.RepeatingWeatherDataTask;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;

@Controller
@CheckRoleProperty(YukonRoleProperty.SHOW_ECOBEE)
public class EcobeeController {

    private static final Logger log = YukonLogManager.getLogger(EcobeeController.class);
//    private static final String baseKey = "yukon.web.modules.dr.home.ecobee.configure.";
    private static final String homeKey = "yukon.web.modules.dr.home.ecobee.configure.";
    private static final String detailsKey = "yukon.web.modules.dr.home.ecobee.details.";
    private static DateTimeFormatter dateTimeFormatter;

    @Autowired private EcobeeCommunicationService ecobeeCommunicationService;
    @Autowired private EnergyCompanyDao ecDao;
    @Autowired private DRGroupDeviceMappingDao drGroupDeviceMappingDao;
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
    @Autowired EcobeeReconciliationService ecobeeReconciliation;

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
            job.setJobProperties(new HashMap<String,String>());
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
            job.setJobProperties(new HashMap<String,String>());
            scheduledRepeatingJobDao.save(job);
            jobManager.instantiateTask(job);
        }
        dateTimeFormatter = dateFormattingService.getDateTimeFormatter(DateFormatEnum.DATEHM, YukonUserContext.system);
    }

    @RequestMapping(value="/ecobee/settings", method=RequestMethod.POST)
    public String saveSettings(EcobeeSettings ecobeeSettings) {
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
        
//        if (bindingResult.hasErrors()) {
//            List<MessageSourceResolvable> messages = YukonValidationUtils.errorsForBindingResult(bindingResult);
//            flashScope.setMessage(messages, FlashScopeMessageType.ERROR);
//        } else {
//            flashScope.setConfirm(new YukonMessageSourceResolvable(baseKey + "successful"));
//        }
        return "redirect:/dr/home";
    }
    
    @RequestMapping(value="/ecobee/download", method=RequestMethod.POST)
    public void ecobeeDataReport(HttpServletRequest request, HttpServletResponse response, Integer[] loadGroupIds,
            String ecobeeStartReportDate, String ecobeeEndReportDate) throws IOException {

        Instant startDate = new Instant(dateTimeFormatter.parseMillis(ecobeeStartReportDate));
        Instant endDate = new Instant(dateTimeFormatter.parseMillis(ecobeeEndReportDate));
        log.info("ecobeeStartReportDate: " + ecobeeStartReportDate + " ecobeeEndReportDate: " + ecobeeEndReportDate +
            " loadGroupIds: " + loadGroupIds);
        Range<Instant> dateRange = Range.inclusive(startDate, endDate);
        if (loadGroupIds == null) {
            response.setStatus(200);
            log.info("no loadGroupIds specified");
            return;
        }
        List<Integer> idsList = Lists.newArrayList(loadGroupIds);
        List<String> allSerialNumbers = drGroupDeviceMappingDao.getInventorySerialNumbersForLoadGroups(idsList);

        DateTimeFormatter timeFormatter = DateTimeFormat.forPattern("dd/MM/yyyy HH:mm:ss");
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=\"ecobee_data_report.csv\"");
        BufferedWriter output = new BufferedWriter(new OutputStreamWriter(response.getOutputStream()));
        String headerFormat = "%s,%s,%s,%s,%s,%s,%s,%s\n";
        String dataFormat = "%s,%s,%s,%s,%s,%s,%d,%s\n";
        output.write(String.format(headerFormat, "Serial Number", "Date", "Outdoor Temp",
                "Indoor Temp", "Set Cool Temp", "Set Heat Temp", "Runtime Seconds", "Event Activity"));
        // readDeviceData should only be sent 25 serial numbers at a time
        int errorCount = 0;
        EcobeeReadResult result = new EcobeeReadResult(allSerialNumbers.size());
        for (List<String> serialNumbers : Lists.partition(allSerialNumbers, 25)) {
            List<EcobeeDeviceReadings> allDeviceReadings = new ArrayList<>();
            try {
                allDeviceReadings = ecobeeCommunicationService.readDeviceData(serialNumbers, dateRange);
            } catch (EcobeeCommunicationException e) {
                errorCount += 1;
            }
            for (EcobeeDeviceReadings deviceReadings : allDeviceReadings) {
                String serialNumber = deviceReadings.getSerialNumber();
                for (EcobeeDeviceReading deviceReading : deviceReadings.getReadings()) {
                    String dateStr = timeFormatter.print(deviceReading.getDate());
                    int runtimeSeconds = deviceReading.getRuntimeSeconds();
                    if (0 > runtimeSeconds) {
                        log.info("runtimeSeconds=" + runtimeSeconds + ", converting to absolute value");
                        runtimeSeconds = Math.abs(runtimeSeconds);
                    }
                    String dataRow = String.format(dataFormat,
                        serialNumber,
                        dateStr,
                        formatNullable(deviceReading.getOutdoorTempInF()),
                        formatNullable(deviceReading.getIndoorTempInF()),
                        formatNullable(deviceReading.getSetCoolTempInF()),
                        formatNullable(deviceReading.getSetHeatTempInF()),
                        runtimeSeconds,
                        deviceReading.getEventActivity());
                    output.write(dataRow);
                }
                result.addCompleted(serialNumbers.size());
            }
        }
        readResultsCache.addResult(result);
        log.info("errorCount=" + errorCount);
        output.flush();
    }

    // TODO: Mark: set requestMapping values
    //@RequestMapping(value="/ecobeeCsv", method=RequestMethod.GET)
    public void ecobeeDataReportCsv(HttpServletResponse response, LiteYukonUser user) throws IOException {
        response.setContentType("text/csv");
        // TODO: Mark: figure out good name for file
        response.setHeader("Content-Disposition","filename=\"ecobee_data_report.csv\"");

        // TODO: get date range from request
        Range<Instant> dateRange = Range.inclusive(Instant.now().minus(Duration.standardDays(7)), Instant.now());
        // TODO: Mark: get loadGroupIds from request
        List<Integer> loadGroupIds = new ArrayList<>();
        List<String> allSerialNumbers = drGroupDeviceMappingDao.getInventorySerialNumbersForLoadGroups(loadGroupIds);

        String headerFormat = "%s,%s,%s,%s,%s,%s,%s,%s\n";
        String dataFormat = "%s,%s,%s,%s,%s,%s,%d,%s\n";

        BufferedWriter output = new BufferedWriter(new OutputStreamWriter(response.getOutputStream()));
        output.write(String.format(headerFormat, "Serial Number", "Date", "Outdoor Temp",
                         "Indoor Temp", "Set Cool Temp", "Set Heat Temp", "Runtime Seconds", "Event Activity"));
        DateTimeFormatter timeFormatter = DateTimeFormat.forPattern("dd/MM/yyyy HH:mm:ss");

        // readDeviceData should only be sent 25 serial numbers at a time
        for (List<String> serialNumbers : Lists.partition(allSerialNumbers, 25)) {
            List<EcobeeDeviceReadings> allDeviceReadings =
                    ecobeeCommunicationService.readDeviceData(serialNumbers, dateRange);
            for (EcobeeDeviceReadings deviceReadings : allDeviceReadings) {
                String serialNumber = deviceReadings.getSerialNumber();
                for (EcobeeDeviceReading deviceReading : deviceReadings.getReadings()) {
                    String dateStr = timeFormatter.print(deviceReading.getDate());
                    String line = String.format(dataFormat, serialNumber, dateStr,
                            formatNullable(deviceReading.getOutdoorTempInF()),
                            formatNullable(deviceReading.getIndoorTempInF()),
                            formatNullable(deviceReading.getSetCoolTempInF()),
                            formatNullable(deviceReading.getSetHeatTempInF()),
                            deviceReading.getRuntimeSeconds(), deviceReading.getEventActivity());
                    output.write(line);
                }
            }
        }

        output.flush();
    }

    private static String formatNullable(Float num) {
        return num == null ? "" : new DecimalFormat("#.#").format(num);
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
        int statsMonth = currentMonthStats.getMonth();
        int statsYear = currentMonthStats.getYear();
        int currentMonthDataCollectionQueryCount = currentMonthStats.getQueryCountByType(EcobeeQueryType.DATA_COLLECTION);
        int currentMonthDemandResponseQueryCount = currentMonthStats.getQueryCountByType(EcobeeQueryType.DEMAND_RESPONSE);
        int currentMonthSystemQueryCount = currentMonthStats.getQueryCountByType(EcobeeQueryType.SYSTEM);
        EcobeeQueryStats queryStats;
        // begin test
        if(0 == currentMonthDataCollectionQueryCount && 0 == currentMonthDemandResponseQueryCount &&
            0 == currentMonthSystemQueryCount) {
            // generate fake data
            Random rand = new Random();
            int maxTestVal = 10000;
            currentMonthDemandResponseQueryCount = rand.nextInt(maxTestVal);
            currentMonthDataCollectionQueryCount = rand.nextInt(maxTestVal - currentMonthDemandResponseQueryCount);
            currentMonthSystemQueryCount = rand.nextInt(maxTestVal - currentMonthDemandResponseQueryCount -
                currentMonthDataCollectionQueryCount);
            YearMonth month = new YearMonth().withYear(statsYear).withMonthOfYear(statsMonth);
            queryStats =
                new EcobeeQueryStats(month, currentMonthDemandResponseQueryCount, currentMonthDataCollectionQueryCount,
                    currentMonthSystemQueryCount);
        } else {
            queryStats = new EcobeeQueryStats(currentMonthStats);
        }
        // end test
        model.addAttribute("ecobeeStats", queryStats);

        // TODO deviceIssues and groupIssues need real data
        model.addAttribute("deviceIssues", 3);
        model.addAttribute("groupIssues", 6);

        log.debug(queryStats);
        return "dr/ecobee/statistics.jsp";
    }

    @RequestMapping(value="/ecobee", method=RequestMethod.GET)
    public String details(ModelMap model, YukonUserContext userContext) {

        EcobeeReconciliationReport report = ecobeeReconciliation.findReconciliationReport();
        if (report != null) {
            // TODO: add report parsing here
        }
        // dummy data for issues until job created to generate issues
        List<EcobeeSyncIssue> issues = new ArrayList<>();

        EcobeeSyncIssue deviceNotInEcobee = new EcobeeSyncIssue();
        deviceNotInEcobee.setType(EcobeeSyncIssueType.DEVICE_NOT_IN_ECOBEE);
        deviceNotInEcobee.setSerialNumber("123456789");
        issues.add(deviceNotInEcobee);

        EcobeeSyncIssue deviceNotInYukon = new EcobeeSyncIssue();
        deviceNotInYukon.setType(EcobeeSyncIssueType.DEVICE_NOT_IN_YUKON);
        deviceNotInYukon.setSerialNumber("987654321");
        issues.add(deviceNotInYukon);

        EcobeeSyncIssue loadGroupNotInEcobee = new EcobeeSyncIssue();
        loadGroupNotInEcobee.setType(EcobeeSyncIssueType.LOAD_GROUP_NOT_IN_ECOBEE);
        loadGroupNotInEcobee.setLoadGroupName("AC Super Saver 9000");;
        issues.add(loadGroupNotInEcobee);

        EcobeeSyncIssue ecobeeEnrollmentIncorrect = new EcobeeSyncIssue();
        ecobeeEnrollmentIncorrect.setType(EcobeeSyncIssueType.LOAD_GROUP_NOT_IN_ECOBEE);
        ecobeeEnrollmentIncorrect.setLoadGroupName("WH Lite 50%");
        issues.add(ecobeeEnrollmentIncorrect);

        EcobeeSyncIssue ecobeeSetDoesNotMatch = new EcobeeSyncIssue();
        ecobeeSetDoesNotMatch.setType(EcobeeSyncIssueType.ECOBEE_SET_DOES_NOT_MATCH);
        ecobeeSetDoesNotMatch.setLoadGroupName("Com 5M Control");
        issues.add(ecobeeSetDoesNotMatch);

        EcobeeSyncIssue ecobeeIncorrectLocation = new EcobeeSyncIssue();
        ecobeeIncorrectLocation.setType(EcobeeSyncIssueType.ECOBEE_SET_IN_INCORRECT_LOCATION);
        ecobeeIncorrectLocation.setLoadGroupName("RF WH EMERGENCY PROGRAM");
        issues.add(ecobeeIncorrectLocation);

        EcobeeSyncIssue ecobeeBadLoadGroup = new EcobeeSyncIssue();
        ecobeeBadLoadGroup.setType(EcobeeSyncIssueType.LOAD_GROUP_NOT_IN_ECOBEE);
        ecobeeBadLoadGroup.setLoadGroupName("COM_LITE-8");
        issues.add(ecobeeBadLoadGroup);

        EcobeeSyncIssue anotherDeviceNotInYukon = new EcobeeSyncIssue();
        anotherDeviceNotInYukon.setType(EcobeeSyncIssueType.DEVICE_NOT_IN_YUKON);
        anotherDeviceNotInYukon.setSerialNumber("42");
        issues.add(anotherDeviceNotInYukon);

        EcobeeSyncIssue anotherDeviceNotInEcobee = new EcobeeSyncIssue();
        anotherDeviceNotInEcobee.setType(EcobeeSyncIssueType.DEVICE_NOT_IN_ECOBEE);
        anotherDeviceNotInEcobee.setSerialNumber("42");
        issues.add(anotherDeviceNotInEcobee);

        EcobeeSyncIssue anotherSetDoesNotMatch = new EcobeeSyncIssue();
        anotherSetDoesNotMatch.setType(EcobeeSyncIssueType.ECOBEE_SET_DOES_NOT_MATCH);
        anotherSetDoesNotMatch.setLoadGroupName("Serial RFN Group");
        issues.add(anotherSetDoesNotMatch);

        EcobeeSyncIssue anotherIncorrectLocation = new EcobeeSyncIssue();
        anotherIncorrectLocation.setType(EcobeeSyncIssueType.ECOBEE_SET_IN_INCORRECT_LOCATION);
        anotherIncorrectLocation.setLoadGroupName("RF WH EMERGENCY GRP");
        issues.add(anotherIncorrectLocation);

        EcobeeSyncIssue yetAnotherSetDoesNotMatch = new EcobeeSyncIssue();
        yetAnotherSetDoesNotMatch.setType(EcobeeSyncIssueType.ECOBEE_SET_DOES_NOT_MATCH);
        yetAnotherSetDoesNotMatch.setLoadGroupName("SEP Group");
        issues.add(yetAnotherSetDoesNotMatch);

        EcobeeSyncIssue notAnotherIncorrectLocation = new EcobeeSyncIssue();
        notAnotherIncorrectLocation.setType(EcobeeSyncIssueType.ECOBEE_SET_IN_INCORRECT_LOCATION);
        notAnotherIncorrectLocation.setLoadGroupName("SIGNAL TEST");
        issues.add(notAnotherIncorrectLocation);

        model.addAttribute("issues", issues);

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
        log.debug("queryStatsList.size(): " + queryStatsList.size());
        for (EcobeeQueryStats stats : queryStatsList) {
            log.debug(stats);
        }
        // end unit test, debug logging
        model.addAttribute("statsList", queryStatsList);
        // TODO: fetch data download info
        List<String> pendingKeys = readResultsCache.getPendingKeys();
        List<String> completedKeys = readResultsCache.getCompletedKeys();
        List<EcobeeReadResult> downloadsList = new ArrayList<EcobeeReadResult>();
        int pendingIndex;
        for (pendingIndex = 0; pendingIndex < 5 && pendingIndex < pendingKeys.size(); pendingIndex++) {
            EcobeeReadResult result = readResultsCache.getResult(pendingKeys.get(pendingIndex));
            downloadsList.add(result);
        }
        int completedIndex;
        int completedsToAdd = 5 - pendingIndex;
        for (completedIndex = 0; completedIndex < completedsToAdd && completedIndex < completedKeys.size(); completedIndex++) {
            EcobeeReadResult result = readResultsCache.getResult(completedKeys.get(completedIndex));
            downloadsList.add(result);
        }
        // begin unit test for data downloads history and in-progress
        // TODO: this should return a list of historical entries
        DateTime startDate = new DateTime(2014, 5, 2, 21, 45, 00);
        DateTime endDate = new DateTime(2014, 5, 2, 22, 00, 00);
        Boolean downLoadFinished = true;
        DateTime startDownLoad = new DateTime(2014, 5, 16, 22, 00, 00);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        model.addAttribute("downLoadFinished", downLoadFinished);
        model.addAttribute("startDownLoad", startDownLoad);
//      List<EcobeeDataDownload> downloadsList = new ArrayList<EcobeeController.EcobeeDataDownload>();
//      downloadsList.add(new EcobeeDataDownload(new DateTime(2014, 5, 16, 22, 00, 00), null, false));
//      downloadsList.add(new EcobeeDataDownload(new DateTime(2014, 5, 20, 12, 30, 00),
//              new DateTime(2014, 5, 21, 12, 30, 00), true));
//      downloadsList.add(new EcobeeDataDownload(new DateTime(2014, 5, 2, 21, 45, 00),
//          new DateTime(2014, 5, 3, 21, 45, 00), true));
//      downloadsList.add(new EcobeeDataDownload(new DateTime(2014, 5, 19, 12, 30, 00),
//              new DateTime(2014, 5, 20, 12, 30, 00), true));
//      for (EcobeeDataDownload download: downloadsList) {
//          log.info("startDate: " + download.startDate + " endDate: " + download.endDate + " downloadFinished: " + download.downloadFinished);
//      }
      model.addAttribute("downloadsList", downloadsList);
      DateTime now = new DateTime();
      DateTime sevenDaysAgo = new DateTime(now.minusDays(7));
      DateTime oneDayAgo = new DateTime(now.minusDays(1));
      model.addAttribute("now", now);
      model.addAttribute("sevenDaysAgo", sevenDaysAgo);
      model.addAttribute("oneDayAgo", oneDayAgo);

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
    
    public class EcobeeDownload {
        private DateTime startReportDate;
        private DateTime endReportDate;
        private List<Integer> paoIds;
        public DateTime getStartReportDate() {
            return startReportDate;
        }
        public void setStartReportDate(DateTime startReportDate) {
            this.startReportDate = startReportDate;
        }
        public DateTime getEndReportDate() {
            return endReportDate;
        }
        public void setEndReportDate(DateTime endReportDate) {
            this.endReportDate = endReportDate;
        }
        public List<Integer> getPaoIds() {
            return paoIds;
        }
        public void setPaoIds(List<Integer> paoIds) {
            this.paoIds = paoIds;
        }
        
    }
    
    private ScheduledRepeatingJob getJob(YukonJobDefinition<? extends YukonTask> jobDefinition) {
        List<ScheduledRepeatingJob> activeJobs = jobManager.getNotDeletedRepeatingJobsByDefinition(jobDefinition);
        return Iterables.getOnlyElement(activeJobs);
    }
}
