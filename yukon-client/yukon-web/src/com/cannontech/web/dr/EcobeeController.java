package com.cannontech.web.dr;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.Instant;
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
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.dr.assetavailability.dao.DRGroupDeviceMappingDao;
import com.cannontech.dr.ecobee.EcobeeException;
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
import com.cannontech.web.dr.model.EcobeeSettings;
import com.cannontech.web.security.annotation.CheckRole;
import com.google.common.collect.Lists;

@Controller
@CheckRole(YukonRole.DEMAND_RESPONSE)
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
    public void ecobeeDataReportCsv(HttpServletResponse response, LiteYukonUser user) 
            throws IOException, EcobeeException {
        response.setContentType("text/csv");
        // TODO: Mark: figure out good name for file
        response.setHeader("Content-Disposition","filename=\"ecobee_data_report.csv\"");

        int ecId = ecDao.getEnergyCompany(user).getId();

        // TODO: get date range from request
        Range<Instant> dateRange = Range.inclusive(Instant.now().minus(Duration.standardDays(7)), Instant.now());
        // TODO: Mark: get loadGroupIds from request
        List<Integer> loadGroupIds = new ArrayList<>();
        List<String> allSerialNumbers = drGroupDeviceMappingDao.getInventorySerialNumbersForLoadGroups(loadGroupIds);

        String headerFormat = "%s,%s,%s,%s,%s,%s,%s,%s\n";
        String dataFormat = "%s,%s,%.1f,%.1f,%.1f,%.1f,%d,%s\n";

        BufferedWriter output = new BufferedWriter(new OutputStreamWriter(response.getOutputStream()));
        output.write(String.format(headerFormat, "Serial Number", "Date", "Outdoor Temp",
                         "Indoor Temp", "Set Cool Temp", "Set Heat Temp", "Runtime Seconds", "Event Activity"));
        DateTimeFormatter timeFormatter = DateTimeFormat.forPattern("dd/MM/yyyy HH:mm:ss");

        // readDeviceData should only be sent 25 serial numbers at a time
        for (List<String> serialNumbers : Lists.partition(allSerialNumbers, 25)) {
            List<EcobeeDeviceReadings> allDeviceReadings = 
                    ecobeeCommunicationService.readDeviceData(serialNumbers, dateRange, ecId);
            for (EcobeeDeviceReadings deviceReadings : allDeviceReadings) {
                String serialNumber = deviceReadings.getSerialNumber();
                for (EcobeeDeviceReading deviceReading : deviceReadings.getReadings()) {
                    String dateStr = timeFormatter.print(deviceReading.getDate());
                    String line = String.format(dataFormat, serialNumber, dateStr,
                        deviceReading.getOutdoorTempInF(), deviceReading.getIndoorTempInF(), 
                        deviceReading.getSetCoolTempInF(), deviceReading.getSetHeatTempInF(),
                        deviceReading.getRuntimeSeconds(), deviceReading.getEventActivity());
                    output.write(line);
                }
            }
        }

        output.flush();
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
        
        LiteYukonUser user = userContext.getYukonUser();
        int energyCoId = ecDao.getEnergyCompany(user).getId();

        //get stats across a range of months
        MonthYear currentMonth = MonthYear.now();
        MonthYear yearAgoMonth = currentMonth.minus(0, 1);
        Range<MonthYear> range = Range.inclusive(yearAgoMonth, currentMonth);

        List<EcobeeQueryStatistics> rangeOfStatsList = ecobeeQueryCountDao.getCountsForRange(range, energyCoId);
        List<EcobeeQueryStats> queryStatsList = new ArrayList<>();
        for(EcobeeQueryStatistics stats : rangeOfStatsList) {
            int statsMonth = stats.getMonth();
            int statsYear = stats.getYear();
            int demandResponseCount = stats.getQueryCountByType(EcobeeQueryType.DEMAND_RESPONSE);
            int dataCollectionCount = stats.getQueryCountByType(EcobeeQueryType.DATA_COLLECTION);
            int systemCount = stats.getQueryCountByType(EcobeeQueryType.SYSTEM);
            EcobeeQueryStats queryStats =
                new EcobeeQueryStats(statsMonth, statsYear, demandResponseCount, dataCollectionCount, systemCount);
            queryStatsList.add(queryStats);
        }
        // begin unit test
        if (0 == rangeOfStatsList.size()) {
            // fake data for testing
            Random rand = new Random();
            DateTime dateTime = new DateTime();
            int maxTestVal = 100000;
            for(int i = 0; i < 11; i += 1) {
                int demandResponseCount = rand.nextInt(maxTestVal);
                int dataCollectionCount = rand.nextInt(maxTestVal-demandResponseCount);
                int systemCount = rand.nextInt(maxTestVal-demandResponseCount-dataCollectionCount);
                EcobeeQueryStats queryStats =
                    new EcobeeQueryStats(dateTime.minusMonths(i).getMonthOfYear(), dateTime.minusMonths(i).getYear(),
                        demandResponseCount, dataCollectionCount, systemCount);
                queryStatsList.add(queryStats);
            }
        }
        for(EcobeeQueryStats stats : queryStatsList){
            log.debug("statsMonth: " + stats.statsMonth + " statsYear: " + stats.statsYear + " demandResponseCount: " +
                stats.demandResponseCount +
                " dataCollectionCount: " + stats.dataCollectionCount + " systemCount: " + stats.systemCount +
                " monthYearStr: " + stats.monthYearStr);
        }
        // end unit test, debug logging
        model.addAttribute("ecobeeStatsList", queryStatsList);

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
    
    public class EcobeeQueryStats {
        private int statsMonth;
        private int statsYear;
        private int demandResponseCount;
        private int dataCollectionCount;
        private int systemCount;
        private String monthYearStr;
        
        public EcobeeQueryStats(int month, int year, int demandCount, int dataCount, int sysCount) {
            statsMonth = month;
            statsYear = year;
            DateTime date = new DateTime(statsYear, statsMonth, 1, 0, 0);
            monthYearStr = date.toString("MMMM (YYYY)");
            demandResponseCount = demandCount;
            dataCollectionCount = dataCount;
            systemCount = sysCount;
        }
        public int getStatsMonth() {
            return statsMonth;
        }
        public void setStatsMonth(int month ) {
            statsMonth = month;
        }
        public int getStatsYear() {
            return statsYear;
        }
        public void setStatsYear(int year) {
            statsYear = year;
        }
        public int getDemandResponseCount() {
            return demandResponseCount;
        }
        public void setDemandResponseCount(int count) {
            demandResponseCount = count;
        }
        public int getDataCollectionCount() {
            return dataCollectionCount;
        }
        public void setDataCollectionCount(int count) {
            dataCollectionCount = count;
        }
        public int getSystemCount() {
            return systemCount;
        }
        public void setSystemCount(int count) {
            systemCount = count;
        }
        public String getMonthYearStr() {
            return monthYearStr;
        }
        public void setMonthYearStr(String monthYear) {
            monthYearStr = monthYear;
        }
    }
}
