package com.cannontech.web.dr;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

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
import com.cannontech.common.util.Range;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.dr.assetavailability.dao.DRGroupDeviceMappingDao;
import com.cannontech.dr.ecobee.model.EcobeeDeviceReading;
import com.cannontech.dr.ecobee.model.EcobeeDeviceReadings;
import com.cannontech.dr.ecobee.service.EcobeeCommunicationService;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.stars.core.dao.EnergyCompanyDao;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.dr.model.EcobeeSettings;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.google.common.collect.Lists;

@Controller
@CheckRoleProperty(YukonRoleProperty.DEMAND_RESPONSE)
public class EcobeeController {

    private static final Logger log = YukonLogManager.getLogger(EcobeeController.class);
    private static final String homeKey = "yukon.web.modules.dr.home.ecobee.configure.";

    @Autowired private EcobeeCommunicationService ecobeeCommunicationService;
    @Autowired private EnergyCompanyDao ecDao;
    @Autowired private DRGroupDeviceMappingDao drGroupDeviceMappingDao;
    
    @RequestMapping(value="/ecobee/settings", method=RequestMethod.POST)
    public String saveSettings(@ModelAttribute("ecobeeSettings") EcobeeSettings settings, FlashScope flash) {
        log.info(settings);
        //flash.setConfirm(new YukonMessageSourceResolvable(homeKey + "successful"));
        flash.setError(new YukonMessageSourceResolvable(homeKey + "failure"));
        return "redirect:/dr/home";
    }
    
    // TODO: set requestMapping values
    //@RequestMapping(value="/ecobeeCsv", method=RequestMethod.GET)
    public void ecobeeDataReportCsv(HttpServletResponse response, LiteYukonUser user) throws Exception {
        response.setContentType("text/csv");
        // TODO: figure out good name for file
        response.setHeader("Content-Disposition","filename=\"ecobee_data_report.csv\"");

        int ecId = ecDao.getEnergyCompany(user).getId();

        // TODO: get date range from request
        Range<Instant> dateRange = Range.inclusive(Instant.now().minus(Duration.standardDays(7)), Instant.now());
        // TODO: get loadGroupIds from request
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
    public String details(ModelMap model) {
        
        List<String> months = new ArrayList<>();
        DateTime date = new DateTime();
        String month = date.toString("MMMM (YYYY)");
        months.add(month);
        // need to add properties for each month as per spec
        for (int i = 1; i <= 12; i++) {
            months.add(date.minusMonths(i).toString("MMMM (YYYY)")); 
        }
        
        model.addAttribute("months", months);
        
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
        
        return "dr/ecobee/details.jsp";
    }
    
    public class EcobeeSyncIssue {
        
        private EcobeeSyncIssueType type;
        private String serialNumber;
        private String loadGroupName;
        
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
            // TODO 
            return null;
        }
    }
}
