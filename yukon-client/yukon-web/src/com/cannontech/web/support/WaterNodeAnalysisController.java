package com.cannontech.web.support;

import org.apache.commons.compress.utils.Lists;
import org.joda.time.Instant;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.joda.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.cannontech.core.service.DateFormattingService;
import com.cannontech.core.service.DateFormattingService.DateFormatEnum;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.cannontech.web.support.waterNode.details.WaterNodeDetails;
import com.cannontech.web.support.waterNode.service.WaterNodeService;
import com.cannontech.web.util.WebFileUtils;


@Controller
@RequestMapping("/waterNode/*")
//@CheckRoleProperty(YukonRoleProperty.) //TODO: finalize permissions for this page
public class WaterNodeAnalysisController {
        
        @Autowired private DateFormattingService dateFormattingService;
        @Autowired private WaterNodeService waterNodeService; 
        @GetMapping("generateReport")
        public void downloadWaterNodeReport(HttpServletResponse response, YukonUserContext userContext) throws IOException {
            long oneDayInMillis = 24*60*60*1000;
            Instant intervalEnd = Instant.now().minus(oneDayInMillis);//TODO: clarify with David if this is what he meant by 24 hrs previously
            Instant intervalStart =  intervalEnd.minus(6*oneDayInMillis);//interval lasts six days
            
            String[] headerRow = getReportHeaderRow();
            List<String[]> dataRows = getReportDataRows(intervalStart,intervalEnd);
            
            writeToCSV(headerRow, dataRows, response, userContext);
        }
        
        @RequestMapping(value="view", method=RequestMethod.GET)
        public String waterNodePage()  {
            return "waterNode.jsp";
        }
        
        private String[] getReportHeaderRow() {
            String[] CSVHeader = {"Serial Number", 
                "Device Name", 
                "Meter Number", 
                "Device Type", 
                "Device Category",
                "High Sleeping Current Indicator",
                "Most Recent Reading"};
            return CSVHeader;
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
        
        private void writeToCSV(String[] headerRow, List<String[]> dataRows, HttpServletResponse response,
                   YukonUserContext userContext) throws IOException {
            String dateStr = dateFormattingService.format(new LocalDateTime(userContext.getJodaTimeZone()),
                DateFormatEnum.DATE, userContext);
            String fileName = "Battery_Depletion_" + dateStr + ".csv";//TODO: Finalize report name
            WebFileUtils.writeToCSV(response, headerRow, dataRows, fileName);
        }
}
