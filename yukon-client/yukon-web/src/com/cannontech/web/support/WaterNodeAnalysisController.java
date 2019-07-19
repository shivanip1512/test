package com.cannontech.web.support;

import org.apache.commons.compress.utils.Lists;
import org.joda.time.Duration;
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

import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.core.service.DateFormattingService.DateFormatEnum;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.security.annotation.CheckRole;
import com.cannontech.web.support.waterNode.details.WaterNodeDetails;
import com.cannontech.web.support.waterNode.service.WaterNodeService;
import com.cannontech.web.util.WebFileUtils;


@Controller
@RequestMapping("/waterNode/*")
@CheckRole(YukonRole.METERING)
public class WaterNodeAnalysisController {
        
        @Autowired private DateFormattingService dateFormattingService;
        @Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;
        @Autowired private WaterNodeService waterNodeService; 
        @GetMapping("generateReport")
        public void downloadWaterNodeReport(HttpServletResponse response, YukonUserContext userContext) throws IOException {
            
            Instant intervalEnd = Instant.now().minus(Duration.standardDays(1));//TODO: clarify with David if this is what he meant by 24 hrs previously
            Instant intervalStart =  intervalEnd.minus(Duration.standardDays(6));//interval lasts six days
            
            String[] headerRow = getReportHeaderRow(userContext);
            List<String[]> dataRows = getReportDataRows(intervalStart,intervalEnd);
            
            writeToCSV(headerRow, dataRows, response, userContext);
        }
        
        @GetMapping("view")
        public String waterNodePage()  {
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
        
        private void writeToCSV(String[] headerRow, List<String[]> dataRows, HttpServletResponse response,
                   YukonUserContext userContext) throws IOException {
            String dateStr = dateFormattingService.format(new LocalDateTime(userContext.getJodaTimeZone()),
                DateFormatEnum.FILE_TIMESTAMP, userContext);
            String fileName = "BatteryAnalysis_" + dateStr + ".csv";//TODO: Finalize report name
            WebFileUtils.writeToCSV(response, headerRow, dataRows, fileName);
        }
}
