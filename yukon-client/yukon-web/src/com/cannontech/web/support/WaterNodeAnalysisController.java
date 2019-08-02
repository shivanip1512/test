package com.cannontech.web.support;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.compress.utils.Lists;
import org.joda.time.DateTimeZone;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.core.service.DateFormattingService.DateFormatEnum;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.security.annotation.CheckRole;
import com.cannontech.web.support.waterNode.details.WaterNodeDetails;
import com.cannontech.web.support.waterNode.model.BatteryAnalysisModel;
import com.cannontech.web.support.waterNode.service.WaterNodeService;
import com.cannontech.web.support.waterNode.voltageDetails.VoltageDetails;
import com.cannontech.web.util.WebFileUtils;


@Controller
@RequestMapping("/waterNode/*")
@CheckRole(YukonRole.METERING)
public class WaterNodeAnalysisController {
        
        @Autowired private DateFormattingService dateFormattingService;
        @Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;
        @Autowired private WaterNodeService waterNodeService; 
        
        @GetMapping("generateReport")
        public void downloadWaterNodeReport(@RequestParam("analysisEnd") String analysisEnd, ModelMap model, HttpServletResponse response, YukonUserContext userContext) throws IOException {
            BatteryAnalysisModel batteryAnalysisModel = new BatteryAnalysisModel();
            DateTimeFormatter formatter = dateFormattingService.getDateTimeFormatter(DateFormatEnum.DATE, userContext);
            DateTimeZone timeZone = userContext.getJodaTimeZone();
            
            Instant intervalEnd = formatter.parseDateTime(analysisEnd).withTimeAtStartOfDay().withZone(timeZone).toInstant();
            intervalEnd = intervalEnd.plus(Duration.standardDays(1));
            batteryAnalysisModel.setAnalysisEnd(intervalEnd);
            
            Instant intervalStart =  intervalEnd.minus(Duration.standardDays(6)).plus(Duration.standardSeconds(1));//interval lasts six days and starts at 00:00:01
            String[] headerRow = getReportHeaderRow(userContext);
            List<String[]> dataRows = getReportDataRows(intervalStart,intervalEnd);
            
            writeToCSV(headerRow, dataRows, response, userContext, intervalEnd, "BatteryAnalysis");
            batteryAnalysisModel.setLastCreatedReport(intervalEnd);
            model.addAttribute("batteryModel", batteryAnalysisModel);
        }
        
        @GetMapping("generateVoltageReport")
        public void downloadVoltageReport(@RequestParam("lastCreatedReport") String lastReport, ModelMap model, HttpServletResponse response, YukonUserContext userContext) throws IOException {
            DateTimeFormatter formatter = dateFormattingService.getDateTimeFormatter(DateFormatEnum.DATE, userContext);
            DateTimeZone timeZone = userContext.getJodaTimeZone();
            Instant intervalEnd = formatter.parseDateTime(lastReport).withTimeAtStartOfDay().withZone(timeZone).toInstant();
            intervalEnd = intervalEnd.plus(Duration.standardDays(1));
            Instant intervalStart =  intervalEnd.minus(Duration.standardDays(6)).plus(Duration.standardSeconds(1));//interval lasts six days and starts at 00:00:01
            
            String[] headerRow = getVoltageHeaderRow(userContext);
            List<String[]> dataRows = getVoltageDataRows(intervalStart,intervalEnd, userContext);
            
            writeToCSV(headerRow, dataRows, response, userContext, intervalEnd, "BatteryVoltagesDetail");
        }
        
        @GetMapping("generateCSVReport")
        public void testCSVData(ModelMap model, HttpServletResponse response, YukonUserContext userContext) throws IOException {
            
        }
        
        @GetMapping("view")
        public String waterNodePage(ModelMap model)  {
            BatteryAnalysisModel batteryAnalysisModel = new BatteryAnalysisModel();
            batteryAnalysisModel.setAnalysisEnd(Instant.now().minus(Duration.standardDays(1)));
            batteryAnalysisModel.setLastCreatedReport(Instant.now().minus(Duration.standardDays(1)));
            model.addAttribute("batteryModel", batteryAnalysisModel);
            return "waterNode.jsp";
        }
        
        private String[] getReportHeaderRow(YukonUserContext userContext) {
            MessageSourceAccessor messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(userContext);
            String[] headerRow = new String[7];
            headerRow[0] = messageSourceAccessor.getMessage("yukon.web.modules.suppport.waterNode.serialNumber");
            headerRow[1] = messageSourceAccessor.getMessage("yukon.web.modules.suppport.waterNode.deviceName"); 
            headerRow[2] = messageSourceAccessor.getMessage("yukon.web.modules.suppport.waterNode.meterNumber");
            headerRow[3] = messageSourceAccessor.getMessage("yukon.web.modules.suppport.waterNode.deviceType");
            headerRow[4] = messageSourceAccessor.getMessage("yukon.web.modules.suppport.waterNode.deviceCategory");
            headerRow[5] = messageSourceAccessor.getMessage("yukon.web.modules.suppport.waterNode.currentIndicator");
            headerRow[6] = messageSourceAccessor.getMessage("yukon.web.modules.suppport.waterNode.recentReading");
            return headerRow;
         }
        
        
        private List<String[]> getReportDataRows(Instant intervalStart, Instant intervalEnd) {
            List<WaterNodeDetails> analyzedNodes = waterNodeService.getAnalyzedNodes(intervalStart, intervalEnd);
            List<String[]> dataRows = Lists.newArrayList();
            
            analyzedNodes.forEach(details -> {
                String[] dataRow = new String[7];
                ArrayList<Double> voltages = details.getVoltages();
                dataRow[0] = details.getSerialNumber();
                dataRow[1] = details.getName();
                dataRow[2] = details.getMeterNumber().toString();
                dataRow[3] = details.getType();
                dataRow[4] = details.getBatteryLevel().toString();
                dataRow[5] = String.valueOf(details.getHighSleepingCurrentIndicator());
                dataRow[6] = voltages.get(voltages.size()-1).toString();
                dataRows.add(dataRow);
            });
            return dataRows; 
        }
        
        private String[] getVoltageHeaderRow(YukonUserContext userContext) {
            MessageSourceAccessor messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(userContext);
            String[] headerRow = new String[4];
            headerRow[0] = messageSourceAccessor.getMessage("yukon.web.modules.suppport.waterNode.serialNumber");
            headerRow[1] = messageSourceAccessor.getMessage("yukon.web.modules.suppport.waterNode.date"); 
            headerRow[2] = messageSourceAccessor.getMessage("yukon.web.modules.suppport.waterNode.time");
            headerRow[3] = messageSourceAccessor.getMessage("yukon.web.modules.suppport.waterNode.voltage");
            return headerRow;
         }
        
        private List<String[]> getVoltageDataRows(Instant intervalStart, Instant intervalEnd, YukonUserContext userContext) {
            List<VoltageDetails> voltageDetails =  waterNodeService.getVoltageDetails(intervalStart, intervalEnd);
            List<String[]> dataRows = Lists.newArrayList();
    
            voltageDetails.forEach(details -> {
                    ArrayList<Double> voltages = details.getVoltages();
                    ArrayList<Instant> timestamps = details.getTimestamps();
                    String serialNumber = details.getSerialNumber();
                    int numValues = voltages.size();
                for (int currentIndex = 0; currentIndex < numValues; currentIndex++) {
                    String[] dataRow = new String[4];
                    dataRow[0] = serialNumber;
                    dataRow[1] = dateFormattingService.format(timestamps.get(currentIndex), DateFormatEnum.DATE, userContext);
                    dataRow[2] = dateFormattingService.format(timestamps.get(currentIndex), DateFormatEnum.TIME24H, userContext);
                    dataRow[3] = voltages.get(currentIndex).toString();
                    dataRows.add(dataRow);
                }
            });
            return dataRows;
        }
        
        private void writeToCSV(String[] headerRow, List<String[]> dataRows, HttpServletResponse response,
                   YukonUserContext userContext, Instant reportDate, String reportName) throws IOException {
            String dateStr = dateFormattingService.format(Instant.now(), DateFormatEnum.FILE_TIMESTAMP, userContext);
            String fileName = reportName + "_" + dateStr + ".csv";
            WebFileUtils.writeToCSV(response, headerRow, dataRows, fileName);
        }
}
