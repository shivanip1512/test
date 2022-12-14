package com.cannontech.web.support;

import org.apache.commons.compress.utils.Lists;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Duration;
import org.joda.time.Instant;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.core.service.DateFormattingService.DateFormatEnum;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.security.annotation.CheckRole;
import com.cannontech.web.support.waterNode.details.WaterNodeDetails;
import com.cannontech.web.support.waterNode.fileUploadDao.impl.BatteryNodeBadIntervalEndException;
import com.cannontech.web.support.waterNode.fileUploadDao.impl.BatteryNodeFileParsingException;
import com.cannontech.web.support.waterNode.fileUploadDao.impl.BatteryNodeUnableToReadFileException;
import com.cannontech.web.support.waterNode.model.BatteryAnalysisModel;
import com.cannontech.web.support.waterNode.service.WaterNodeService;
import com.cannontech.web.util.WebFileUtils;

@Controller
@RequestMapping("/batteryNodeAnalysis/*")
@CheckRole(YukonRole.METERING)
public class BatteryNodeAnalysisController {
    private static final Logger log = YukonLogManager.getLogger(BatteryNodeAnalysisController.class);

    @Autowired private DateFormattingService dateFormattingService;
    @Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;
    @Autowired private WaterNodeService waterNodeService;

    @GetMapping("generateBatteryConditionReport")
    public void downloadBatteryConditionReport(@RequestParam("analysisEnd") String analysisEnd, ModelMap model,
            HttpServletResponse response,
            YukonUserContext userContext) throws IOException {
        DateTimeFormatter formatter = dateFormattingService.getDateTimeFormatter(DateFormatEnum.DATE, userContext);
        DateTimeZone timeZone = userContext.getJodaTimeZone();
        Instant timestampEnd = formatter.parseDateTime(analysisEnd).withTimeAtStartOfDay().withZone(timeZone).toInstant();
        Instant intervalEnd = timestampEnd.plus(Duration.standardDays(1));
        // interval lasts six days and starts at 00:00:01
        Instant intervalStart = timestampEnd.minus(Duration.standardDays(6)).plus(Duration.standardSeconds(1));

        BatteryAnalysisModel batteryAnalysisModel = new BatteryAnalysisModel();
        batteryAnalysisModel.setAnalysisEnd(intervalEnd);
        List<WaterNodeDetails> analyzedNodes = waterNodeService.getAnalyzedNodes(intervalStart, intervalEnd);
        String[] headerRow = getReportHeaderRow(userContext);
        List<String[]> dataRows = getReportDataRows(analyzedNodes, userContext);

        writeToCSV(headerRow, dataRows, response, userContext, timestampEnd, "BatteryAnalysis");
        batteryAnalysisModel.setLastCreatedReport(intervalEnd);
        model.addAttribute("batteryModel", batteryAnalysisModel);
    }

    @GetMapping("generateVoltageDataReport")
    public void downloadVoltageReport(@RequestParam("lastCreatedReport") String lastReport, ModelMap model,
            HttpServletResponse response,
            YukonUserContext userContext) throws IOException {
        DateTimeFormatter formatter = dateFormattingService.getDateTimeFormatter(DateFormatEnum.DATE, userContext);
        DateTimeZone timeZone = userContext.getJodaTimeZone();
        Instant timestampEnd = formatter.parseDateTime(lastReport).withTimeAtStartOfDay().withZone(timeZone).toInstant();
        Instant intervalEnd = timestampEnd.plus(Duration.standardDays(1));
        // interval lasts six days and starts at 00:00:01
        Instant intervalStart = timestampEnd.minus(Duration.standardDays(6)).plus(Duration.standardSeconds(1));

        List<WaterNodeDetails> voltageDetails = waterNodeService.getVoltageDetails(intervalStart, intervalEnd);
        String[] headerRow = getVoltageHeaderRow(userContext);
        List<String[]> dataRows = getVoltageDataRows(voltageDetails, userContext);

        writeToCSV(headerRow, dataRows, response, userContext, timestampEnd, "BatteryVoltagesDetail");
    }

    @RequestMapping(value = "generatePreExistingVoltageDataAnalysisReport", method = RequestMethod.POST)
    public String fileUploadAndAnalyze(@RequestParam("csvEndDate") String csvEndDate, ModelMap model,
            HttpServletResponse response,
            HttpServletRequest request, YukonUserContext userContext, FlashScope flash) throws IOException {
        DateTimeFormatter formatter = dateFormattingService.getDateTimeFormatter(DateFormatEnum.DATE, userContext);
        DateTimeZone timeZone = userContext.getJodaTimeZone();
        Instant timestampEnd = formatter.parseDateTime(csvEndDate).withTimeAtStartOfDay().withZone(timeZone).toInstant();
        Instant intervalEnd = timestampEnd.plus(Duration.standardDays(1));
        // interval lasts six days and starts at 00:00:01
        Instant intervalStart = timestampEnd.minus(Duration.standardDays(6)).plus(Duration.standardSeconds(1));

        if (ServletFileUpload.isMultipartContent(request)) {
            MultipartHttpServletRequest mRequest = (MultipartHttpServletRequest) request;
            // Create MultiPartFile from the uploadedFile
            MultipartFile uploadedFile = mRequest.getFile("uploadedFile");

            if (!uploadedFile.isEmpty()) {
                // Create temp local file for storing the data to be processed
                File temp = File.createTempFile("tempDataFile", ".csv");
                try (OutputStream os = Files.newOutputStream(temp.toPath())) {
                    os.write(uploadedFile.getBytes());
                    List<WaterNodeDetails> analyzedNodes = waterNodeService.getCsvAnalyzedNodes(intervalStart, intervalEnd, temp);
                    String[] headerRow = getReportHeaderRow(userContext);
                    List<String[]> dataRows = getReportDataRows(analyzedNodes, userContext);
                    writeToCSV(headerRow, dataRows, response, userContext, timestampEnd, "BatteryAnalysisFromCSV");
                } catch (BatteryNodeBadIntervalEndException e) {
                    flash.setWarning(new YukonMessageSourceResolvable(
                            "yukon.web.modules.support.batteryNodeAnalysisController.badEndIntervalSelected", e.getMessage()));
                    log.warn("Bad Interval End date, file has a date range of " + e.getMessage());
                    return "redirect:view";
                } catch (BatteryNodeFileParsingException e) {
                    flash.setWarning(new YukonMessageSourceResolvable(
                            "yukon.web.modules.support.batteryNodeAnalysisController.fileParsingError", uploadedFile.getOriginalFilename()));
                    log.warn("The file " + uploadedFile.getOriginalFilename() + " was unable to be processed due to an incorrect number of columns being present");
                    return "redirect:view";
                } catch (BatteryNodeUnableToReadFileException e) {
                    flash.setError(new YukonMessageSourceResolvable(
                            "yukon.web.modules.support.batteryNodeAnalysisController.unableToReadFile",
                            uploadedFile.getOriginalFilename()));
                    log.warn("Unable to read file ", uploadedFile.getOriginalFilename());
                    return "redirect:view";
                }
            } else {
                flash.setError(new YukonMessageSourceResolvable(
                        "yukon.web.modules.support.batteryNodeAnalysisController.noFileUploaded"));
                return "redirect:view";
            }
        }
        return null;
    }

    @GetMapping("view")
    public String batteryNodeAnalysisPage(ModelMap model) {
        // Setting max date to the previous day. Same as what the date picker defaults to currently.
        Instant maxDate = Instant.now().minus(Duration.standardDays(1));
        // Setting min date to 20 years in the past to prevent choosing date outside acceptable range.
        Instant minDate = maxDate.minus(Duration.standardDays(7300));

        BatteryAnalysisModel batteryAnalysisModel = new BatteryAnalysisModel();
        batteryAnalysisModel.setAnalysisEnd(maxDate);
        batteryAnalysisModel.setLastCreatedReport(maxDate);
        batteryAnalysisModel.setCsvEndDate(maxDate);
        model.addAttribute("batteryModel", batteryAnalysisModel);
        model.addAttribute("maxDate", maxDate);
        model.addAttribute("minDate", minDate);

        return "batteryNodeAnalysis.jsp";
    }

    private String[] getReportHeaderRow(YukonUserContext userContext) {
        MessageSourceAccessor messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        String[] headerRow = new String[10];
        headerRow[0] = messageSourceAccessor.getMessage("yukon.web.modules.support.batteryNodeAnalysis.fileHeader.deviceName");
        headerRow[1] = messageSourceAccessor.getMessage("yukon.web.modules.support.batteryNodeAnalysis.fileHeader.meterNumber");
        headerRow[2] = messageSourceAccessor.getMessage("yukon.web.modules.support.batteryNodeAnalysis.fileHeader.serialNumber");
        headerRow[3] = messageSourceAccessor.getMessage("yukon.web.modules.support.batteryNodeAnalysis.fileHeader.deviceType");
        headerRow[4] = messageSourceAccessor
                .getMessage("yukon.web.modules.support.batteryNodeAnalysis.fileHeader.depletionCategory");
        headerRow[5] = messageSourceAccessor
                .getMessage("yukon.web.modules.support.batteryNodeAnalysis.fileHeader.highSleepingCurrentIndicator");
        headerRow[6] = messageSourceAccessor
                .getMessage("yukon.web.modules.support.batteryNodeAnalysis.fileHeader.mostRecentReading");
        headerRow[7] = messageSourceAccessor.getMessage("yukon.web.modules.support.batteryNodeAnalysis.fileHeader.uom");
        headerRow[8] = messageSourceAccessor.getMessage("yukon.web.modules.support.batteryNodeAnalysis.fileHeader.date");
        headerRow[9] = messageSourceAccessor.getMessage("yukon.web.modules.support.batteryNodeAnalysis.fileHeader.time");
        return headerRow;
    }

    private List<String[]> getReportDataRows(List<WaterNodeDetails> analyzedNodes, YukonUserContext userContext) {
        List<String[]> dataRows = Lists.newArrayList();

        analyzedNodes.forEach(details -> {
            String[] dataRow = new String[10];
            ArrayList<Double> voltages = details.getVoltages();
            dataRow[0] = details.getName();
            dataRow[1] = details.getMeterNumber().toString();
            dataRow[2] = details.getSerialNumber();
            dataRow[3] = details.getType();
            dataRow[4] = details.getBatteryLevel().getOutputName();
            dataRow[5] = String.valueOf(details.getHighSleepingCurrentIndicator());
            dataRow[6] = voltages.get(voltages.size() - 1).toString();
            dataRow[7] = "volts";//
            dataRow[8] = dateFormattingService.format(details.getLastTimestamp(), DateFormatEnum.DATE, userContext);
            dataRow[9] = dateFormattingService.format(details.getLastTimestamp(), DateFormatEnum.TIME24H, userContext);
            dataRows.add(dataRow);
        });
        return dataRows;
    }

    private String[] getVoltageHeaderRow(YukonUserContext userContext) {
        MessageSourceAccessor messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        String[] headerRow = new String[8];
        headerRow[0] = messageSourceAccessor.getMessage("yukon.web.modules.support.batteryNodeAnalysis.fileHeader.deviceName");
        headerRow[1] = messageSourceAccessor.getMessage("yukon.web.modules.support.batteryNodeAnalysis.fileHeader.meterNumber");
        headerRow[2] = messageSourceAccessor.getMessage("yukon.web.modules.support.batteryNodeAnalysis.fileHeader.serialNumber");
        headerRow[3] = messageSourceAccessor.getMessage("yukon.web.modules.support.batteryNodeAnalysis.fileHeader.deviceType");
        headerRow[4] = messageSourceAccessor
                .getMessage("yukon.web.modules.support.batteryNodeAnalysis.fileHeader.mostRecentReading");
        headerRow[5] = messageSourceAccessor.getMessage("yukon.web.modules.support.batteryNodeAnalysis.fileHeader.uom");
        headerRow[6] = messageSourceAccessor.getMessage("yukon.web.modules.support.batteryNodeAnalysis.fileHeader.date");
        headerRow[7] = messageSourceAccessor.getMessage("yukon.web.modules.support.batteryNodeAnalysis.fileHeader.time");
        return headerRow;
    }

    private List<String[]> getVoltageDataRows(List<WaterNodeDetails> voltageDetails, YukonUserContext userContext) {
        List<String[]> dataRows = Lists.newArrayList();

        voltageDetails.forEach(details -> {
            ArrayList<Double> voltages = details.getVoltages();
            ArrayList<Instant> timestamps = details.getTimestamps();
            String deviceName = details.getName();
            String meterNumber = details.getMeterNumber();
            String serialNumber = details.getSerialNumber();
            String deviceType = details.getType();
            int numValues = voltages.size();
            for (int currentIndex = 0; currentIndex < numValues; currentIndex++) {
                String[] dataRow = new String[8];
                dataRow[0] = deviceName;
                dataRow[1] = meterNumber;
                dataRow[2] = serialNumber;
                dataRow[3] = deviceType;
                dataRow[4] = voltages.get(currentIndex).toString();
                dataRow[5] = "volts";
                dataRow[6] = dateFormattingService.format(timestamps.get(currentIndex), DateFormatEnum.DATE, userContext);
                dataRow[7] = dateFormattingService.format(timestamps.get(currentIndex), DateFormatEnum.TIME24H, userContext);
                dataRows.add(dataRow);
            }
        });
        return dataRows;
    }

    private void writeToCSV(String[] headerRow, List<String[]> dataRows, HttpServletResponse response,
            YukonUserContext userContext, Instant reportDate, String reportName) throws IOException {
        String dateStr = dateFormattingService.format(new DateTime(reportDate).toLocalDateTime(),
                DateFormatEnum.FILE_TIMESTAMP, userContext);
        String fileName = reportName + "_" + dateStr + ".csv";
        WebFileUtils.writeToCSV(response, headerRow, dataRows, fileName);
    }
}
