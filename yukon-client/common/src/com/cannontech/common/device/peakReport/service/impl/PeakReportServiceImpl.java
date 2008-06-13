package com.cannontech.common.device.peakReport.service.impl;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Required;

import com.cannontech.amr.errors.model.DeviceErrorDescription;
import com.cannontech.amr.meter.dao.MeterDao;
import com.cannontech.amr.meter.model.Meter;
import com.cannontech.common.device.commands.CommandRequestDeviceExecutor;
import com.cannontech.common.device.commands.CommandResultHolder;
import com.cannontech.common.device.peakReport.dao.PeakReportDao;
import com.cannontech.common.device.peakReport.model.PeakReportPeakType;
import com.cannontech.common.device.peakReport.model.PeakReportResult;
import com.cannontech.common.device.peakReport.model.PeakReportRunType;
import com.cannontech.common.device.peakReport.service.PeakReportService;
import com.cannontech.common.exception.PeakSummaryReportRequestException;
import com.cannontech.common.util.TimeUtil;
import com.cannontech.core.dao.DBPersistentDao;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.core.service.SystemDateFormattingService;
import com.cannontech.core.service.SystemDateFormattingService.DateFormatEnum;
import com.cannontech.database.data.device.MCTBase;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.pao.YukonPAObject;
import com.cannontech.database.db.device.DeviceLoadProfile;
import com.cannontech.user.YukonUserContext;

public class PeakReportServiceImpl implements PeakReportService {

    private DateFormattingService dateFormattingService = null;
    private SystemDateFormattingService systemDateFormattingService = null;
    private DBPersistentDao dbPersistentDao = null;
    private PaoDao paoDao = null;
    private CommandRequestDeviceExecutor commandRequestExecutor = null;
    private MeterDao meterDao = null;
    private PeakReportDao peakReportDao = null;
    
    
    public PeakReportResult requestPeakReport(int deviceId, PeakReportPeakType peakType, PeakReportRunType runType, int channel, Date startDate, Date stopDate, boolean persist, YukonUserContext userContext) {
        
        // interval
        int interval = getChannelIntervalForDevice(deviceId, channel);
        
        // build command to be executed
        StringBuffer commandBuffer = new StringBuffer();
        commandBuffer.append("getvalue lp peak");
        commandBuffer.append(" " + peakType.toString().toLowerCase());
        commandBuffer.append(" channel " + channel);
        DateFormat cmdFormatter = systemDateFormattingService.getSystemDateFormat(DateFormatEnum.PeakReport);
        commandBuffer.append(" " + cmdFormatter.format(stopDate));
        
        Calendar startDateCal = dateFormattingService.getCalendar(userContext);
        startDateCal.setTime(startDate);
        
        Calendar stopDateCal = dateFormattingService.getCalendar(userContext);
        stopDateCal.setTime(stopDate);
        
        int commandDays = TimeUtil.differenceInDays(startDateCal, stopDateCal) + 1;
        commandBuffer.append(" " + commandDays);
        
        // execute command to get profile peak summary results for user request
        Meter meter = meterDao.getForId(deviceId);
        
        // setup basics of PeakReportResult
        PeakReportResult peakResult = new PeakReportResult();
        peakResult.setDeviceId(deviceId);
        peakResult.setChannel(channel);
        peakResult.setPeakType(peakType);
        peakResult.setRunType(runType);
        peakResult.setRunDate(new Date());
        
        
        // run command
        CommandResultHolder commandResultHolder;
        try{
            commandResultHolder = commandRequestExecutor.execute(meter, commandBuffer.toString(), userContext.getYukonUser());
        }
        catch(Exception e){
            throw new PeakSummaryReportRequestException();
        }
        // get result string from command, set result string
        String resultString = commandResultHolder.getLastResultString();
        peakResult.setResultString(resultString);
        
        // device errors!
        if (commandResultHolder.isErrorsExist()) {
            
            peakResult.setErrors(commandResultHolder.getErrors());
            
            StringBuffer sb = new StringBuffer();
            List<DeviceErrorDescription> errors = commandResultHolder.getErrors();
            for (DeviceErrorDescription ded : errors) {
                sb.append(ded.toString() + "\n");
            }
            peakResult.setDeviceError(sb.toString());
            peakResult.setNoData(true);
            
        // results exist, parse result string into peakResult
        } else {
            parseResultString(peakResult, resultString, interval);
        }
       
        
        // persist the results
        if(!peakResult.isNoData() && persist){
            peakReportDao.saveResult(peakResult);
        }
        
        return peakResult;
    }
    
    
    public PeakReportResult retrieveArchivedPeakReport(int deviceId, PeakReportRunType runType, LiteYukonUser user){
        
        PeakReportResult peakResult = peakReportDao.getResult(deviceId, runType);
        
        if(peakResult != null){
            
            // channel
            int channel = peakResult.getChannel();
            
            // interval
            int interval = getChannelIntervalForDevice(deviceId, channel);
            
            // result string
            String resultString = peakResult.getResultString();
            
            // parse result string to fill in rest of peak result obj
            parseResultString(peakResult, resultString, interval);
        }

        return peakResult;
    }
    
    public void deleteArchivedPeakReport(int deviceId, PeakReportRunType runType) {
        
        peakReportDao.deleteReport(deviceId, runType);
    }
    
    private void parseResultString(PeakReportResult peakResult, String resultString, int interval){
        
        SimpleDateFormat dateFormater = new SimpleDateFormat("MM/dd/yy hh:mm:ss");
        
        try{
            
            String[] strings = resultString.split("\n");
            for (String s : strings) {
    
                // rangeStartDate, rangeStopDate
                if (s.startsWith("Report range: ")) {
              
                    String reportRangeStr = s.replaceFirst("Report range: ", "");
                    String[] rangeDateParts = reportRangeStr.split(" - ");
                    String rangeStartDateStr = rangeDateParts[0];
                    String rangeStopDateStr = rangeDateParts[1];
                    
                    peakResult.setRangeStartDate(dateFormater.parse(rangeStartDateStr));
                    peakResult.setRangeStopDate(dateFormater.parse(rangeStopDateStr));
                }
                
                // peakStartDate, peakStopDate, peakValue
                else if(s.startsWith("Peak day: ")) {
                    
                    String peakRangeEndStr = s.replaceFirst("Peak day: ", "");
                    Date peakRangeEnd = dateFormater.parse(peakRangeEndStr);
                    
                    peakResult.setPeakStopDate(peakRangeEnd);
                    
                    peakResult.setPeakStartDate(DateUtils.truncate(peakRangeEnd, Calendar.DATE));
                    
//                    peakResult.setPeakValue(dateFormattingService.formatDate(peakRangeEnd, DateFormattingService.DateFormatEnum.DATE, user));
                    
                }
                else if (s.startsWith("Peak hour: ")) {
                    
                    String peakRangeEndStr = s.replaceFirst("Peak hour: ", "");
                    Date peakRangeEnd = dateFormater.parse(peakRangeEndStr);
                    
                    peakResult.setPeakStopDate(peakRangeEnd);
                    
                    Date peakStartDate = DateUtils.truncate(peakRangeEnd, Calendar.HOUR_OF_DAY);
                    peakResult.setPeakStartDate(peakStartDate);
                    
//                    String peakValueStr = new SimpleDateFormat("MM/dd/yy").format(peakRangeEnd);
//                    peakValueStr += " ";
//                    peakValueStr += new SimpleDateFormat("Ka").format(peakStartDate);
//                    peakValueStr += " - ";
//                    peakValueStr += new SimpleDateFormat("Ka").format(DateUtils.addMinutes(peakRangeEnd, 1));
//                    peakResult.setPeakValue(peakValueStr);
                    
                 }
                 else if (s.startsWith("Peak interval: ")) {
                    
                     String peakRangeEndStr = s.replaceFirst("Peak interval: ", "");
                     Date peakRangeEnd = dateFormater.parse(peakRangeEndStr);
                     
                     peakResult.setPeakStopDate(peakRangeEnd);
                    
                     Date peakStartDate = DateUtils.addSeconds(peakRangeEnd, 1);
                     peakStartDate = DateUtils.addMinutes(peakStartDate, -interval+1);
                     peakResult.setPeakStartDate(peakStartDate);
                    
//                     String peakValueStr = new SimpleDateFormat("MM/dd/yy").format(peakRangeEnd);
//                     peakValueStr += " ";
//                     if(interval == 60){
//                         peakValueStr += new SimpleDateFormat("Ha").format(peakStartDate);
//                         peakValueStr += " - ";
//                         peakValueStr += new SimpleDateFormat("Ha").format(DateUtils.addMinutes(peakRangeEnd, 1));
//                     }
//                     else{
//                         peakValueStr += new SimpleDateFormat("K:mma").format(peakStartDate);
//                         peakValueStr += " - ";
//                         peakValueStr += new SimpleDateFormat("K:mma").format(DateUtils.addMinutes(peakRangeEnd, 1));
//                     }
//                     peakResult.setPeakValue(peakValueStr);
                    
                 }
                 else if (s.startsWith("Usage:  ")) {
                    
                    String usageStr = s.replaceFirst("Usage:  ", "");
                    double usageValue = new Double(usageStr.split(" ")[0]);
                    
                    peakResult.setUsage(usageValue);
                    
                 }
                 else if (s.startsWith("Demand: ")) {
                    
                    String demandStr = s.replaceFirst("Demand: ", "");
                    double demandValue = new Double(demandStr.split(" ")[0]);
                    
                    peakResult.setDemand(demandValue);
                    
                 } 
                 else if (s.startsWith("Average daily usage over range: ")) {
                    
                    String averageDailyUsageStr = s.replaceFirst("Average daily usage over range: ", "");
                    double averageDailyUsageValue = new Double(averageDailyUsageStr.split(" ")[0]);
                    
                    peakResult.setAverageDailyUsage(averageDailyUsageValue);
                    
                 } 
                 else if (s.startsWith("Total usage over range: ")) {
                    
                    String totalUsageStr = s.replaceFirst("Total usage over range: ", "");
                    double totalUsagValue = new Double(totalUsageStr.split(" ")[0]);
                    
                    peakResult.setTotalUsage(totalUsagValue);
                }
            }
            
            // finished parsing, set no data to false
            peakResult.setNoData(false);
          
        }
        catch (ParseException e) {
            peakResult.setDeviceError("Unable to parse: " + e.getMessage());
            peakResult.setNoData(true);
        }
        
    }

    public int getChannelIntervalForDevice(int deviceId, int channel){
        
        // interval
        LiteYukonPAObject device = paoDao.getLiteYukonPAO(deviceId);
        YukonPAObject yukonPaobject = (YukonPAObject)dbPersistentDao.retrieveDBPersistent(device);
        DeviceLoadProfile deviceLoadProfile = ((MCTBase)yukonPaobject).getDeviceLoadProfile();
        int loadProfileDemandRate = deviceLoadProfile.getLoadProfileDemandRate();
        int voltageDemandRate = deviceLoadProfile.getVoltageDmdRate();
        
        int interval = 0;
        if(channel == 1){
            interval = loadProfileDemandRate / 60;
        }
        else if(channel == 4){
            interval = voltageDemandRate / 60;
        }
        
        return interval;
        
    }
    
    @Required
    public void setDateFormattingService(DateFormattingService dateFormattingService) {
        this.dateFormattingService = dateFormattingService;
    }
    
    @Required
    public void setSystemDateFormattingService(SystemDateFormattingService systemDateFormattingService) {
        this.systemDateFormattingService = systemDateFormattingService;
    }
    
    @Required
    public void setDbPersistentDao(DBPersistentDao dbPersistentDao) {
        this.dbPersistentDao = dbPersistentDao;
    }

    @Required
    public void setPaoDao(PaoDao paoDao) {
        this.paoDao = paoDao;
    }

    @Required
    public void setCommandRequestExecutor(
            CommandRequestDeviceExecutor commandRequestExecutor) {
        this.commandRequestExecutor = commandRequestExecutor;
    }

    @Required
    public void setMeterDao(MeterDao meterDao) {
        this.meterDao = meterDao;
    }

    @Required
    public void setPeakReportDao(PeakReportDao peakReportDao) {
        this.peakReportDao = peakReportDao;
    }
    
}
