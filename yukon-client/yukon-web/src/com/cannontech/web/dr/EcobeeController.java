package com.cannontech.web.dr;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.joda.time.YearMonth;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.i18n.DisplayableEnum;
import com.cannontech.common.util.MonthYear;
import com.cannontech.common.util.Range;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.dr.assetavailability.dao.DRGroupDeviceMappingDao;
import com.cannontech.dr.ecobee.dao.EcobeeQueryCountDao;
import com.cannontech.dr.ecobee.dao.EcobeeQueryType;
import com.cannontech.dr.ecobee.model.EcobeeDeviceReading;
import com.cannontech.dr.ecobee.model.EcobeeDeviceReadings;
import com.cannontech.dr.ecobee.model.EcobeeQueryStatistics;
import com.cannontech.dr.ecobee.service.EcobeeCommunicationService;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.stars.core.dao.EnergyCompanyDao;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.dr.model.EcobeeQueryStats;
import com.cannontech.web.dr.model.EcobeeSettings;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.google.common.collect.Lists;

@Controller
@CheckRoleProperty(YukonRoleProperty.SHOW_ECOBEE)
public class EcobeeController {

    private static final Logger log = YukonLogManager.getLogger(EcobeeController.class);
    private static final String homeKey = "yukon.web.modules.dr.home.ecobee.configure.";

    @Autowired private EcobeeCommunicationService ecobeeCommunicationService;
    @Autowired private EnergyCompanyDao ecDao;
    @Autowired private DRGroupDeviceMappingDao drGroupDeviceMappingDao;
    @Autowired private EcobeeQueryCountDao ecobeeQueryCountDao;

    @RequestMapping(value="/ecobee/settings", method=RequestMethod.POST)
    public String saveSettings(@ModelAttribute("ecobeeSettings") EcobeeSettings settings, FlashScope flash) {
        log.info(settings);
        //flash.setConfirm(new YukonMessageSourceResolvable(homeKey + "successful"));
        flash.setError(new YukonMessageSourceResolvable(homeKey + "failed"));
        return "redirect:/dr/home";
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
        EcobeeSettings ecobeeSettings = new EcobeeSettings();
        ecobeeSettings.setCheckErrors(true);
        ecobeeSettings.setDataCollection(true);
        ecobeeSettings.setErrorCheckTime(42);
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
        model.addAttribute("deviceIssues", 3);
        model.addAttribute("groupIssues", 6);

        log.debug(queryStats);
        return "dr/ecobee/statistics.jsp";
    }

    @RequestMapping(value="/ecobee", method=RequestMethod.GET)
    public String details(ModelMap model, YukonUserContext userContext) {

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

        return "dr/ecobee/details.jsp";
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
}
