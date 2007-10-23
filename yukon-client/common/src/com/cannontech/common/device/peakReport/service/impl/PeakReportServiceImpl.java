package com.cannontech.common.device.peakReport.service.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Required;

import com.cannontech.common.device.peakReport.model.PeakReportPeakType;
import com.cannontech.common.device.peakReport.model.PeakReportResult;
import com.cannontech.common.device.peakReport.service.PeakReportService;
import com.cannontech.core.dao.DBPersistentDao;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.database.data.device.MCTBase;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.pao.YukonPAObject;
import com.cannontech.database.db.device.DeviceLoadProfile;

public class PeakReportServiceImpl implements PeakReportService {

    private DateFormattingService dateFormattingService = null;
    private DBPersistentDao dbPersistentDao = null;
    private PaoDao paoDao = null;
    
    public PeakReportResult parseResultString(PeakReportResult peakResult, String resultString, int interval, LiteYukonUser user){
        
        PeakReportPeakType peakType = peakResult.getPeakType();
        
        try{
            
            peakResult.setRunDateDisplay(dateFormattingService.formatDate(peakResult.getRunDate(), DateFormattingService.DateFormatEnum.DATEHM, user));
        
            String[] strings = resultString.split("\n");
            for (String s : strings) {
    
                // rangeStartDate, rangeStopDate
                if (s.startsWith("Report range: ")) {
              
                    String reportRangeStr = s.replaceFirst("Report range: ", "");
                    String[] rangeDateParts = reportRangeStr.split(" - ");
                    String rangeStartDateStr = rangeDateParts[0];
                    String rangeStopDateStr = rangeDateParts[1];
              
                    peakResult.setRangeStartDate(dateFormattingService.flexibleDateParser(rangeStartDateStr, user));
                    peakResult.setRangeStopDate(dateFormattingService.flexibleDateParser(rangeStopDateStr, user));
                    
                    peakResult.setPeriodStartDateDisplay(dateFormattingService.formatDate(peakResult.getRangeStartDate(), DateFormattingService.DateFormatEnum.DATE, user));
                    peakResult.setPeriodStopDateDisplay(dateFormattingService.formatDate(peakResult.getRangeStopDate(), DateFormattingService.DateFormatEnum.DATE, user));
              
                }
                
                // peakStartDate, peakStopDate, peakValue
                else if(s.startsWith("Peak day: ") && peakType == PeakReportPeakType.DAY) {
                    
                    String peakRangeEndStr = s.replaceFirst("Peak day: ", "");
                    Date peakRangeEnd = dateFormattingService.flexibleDateParser(peakRangeEndStr, user);
                    
                    peakResult.setPeakStopDate(peakRangeEnd);
                    
                    peakResult.setPeakStartDate(DateUtils.truncate(peakRangeEnd, Calendar.DATE));
                    
                    peakResult.setPeakValue(dateFormattingService.formatDate(peakRangeEnd, DateFormattingService.DateFormatEnum.DATE, user));
                    
                }
                else if (s.startsWith("Peak hour: ") && peakType == PeakReportPeakType.HOUR) {
                    
                    String peakRangeEndStr = s.replaceFirst("Peak hour: ", "");
                    Date peakRangeEnd = dateFormattingService.flexibleDateParser(peakRangeEndStr, user);
                    
                    peakResult.setPeakStopDate(peakRangeEnd);
                    
                    Date peakStartDate = DateUtils.truncate(peakRangeEnd, Calendar.HOUR_OF_DAY);
                    peakResult.setPeakStartDate(peakStartDate);
                    
                    String peakValueStr = new SimpleDateFormat("MM/dd/yy").format(peakRangeEnd);
                    peakValueStr += " ";
                    peakValueStr += new SimpleDateFormat("Ka").format(peakStartDate);
                    peakValueStr += " - ";
                    peakValueStr += new SimpleDateFormat("Ka").format(DateUtils.addMinutes(peakRangeEnd, 1));
                    peakResult.setPeakValue(peakValueStr);
                    
                 }
                 else if (s.startsWith("Peak interval: ") && peakType == PeakReportPeakType.INTERVAL) {
                    
                     String peakRangeEndStr = s.replaceFirst("Peak interval: ", "");
                     Date peakRangeEnd = dateFormattingService.flexibleDateParser(peakRangeEndStr, user);
                     
                     peakResult.setPeakStopDate(peakRangeEnd);
                    
                     Date peakStartDate = DateUtils.addSeconds(peakRangeEnd, 1);
                     peakStartDate = DateUtils.addMinutes(peakStartDate, -interval+1);
                     peakResult.setPeakStartDate(peakStartDate);
                    
                     String peakValueStr = new SimpleDateFormat("MM/dd/yy").format(peakRangeEnd);
                     peakValueStr += " ";
                     if(interval == 60){
                         peakValueStr += new SimpleDateFormat("Ha").format(peakStartDate);
                         peakValueStr += " - ";
                         peakValueStr += new SimpleDateFormat("Ha").format(DateUtils.addMinutes(peakRangeEnd, 1));
                     }
                     else{
                         peakValueStr += new SimpleDateFormat("K:mma").format(peakStartDate);
                         peakValueStr += " - ";
                         peakValueStr += new SimpleDateFormat("K:mma").format(DateUtils.addMinutes(peakRangeEnd, 1));
                     }
                     peakResult.setPeakValue(peakValueStr);
                    
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
        
        return peakResult;
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
    public void setDbPersistentDao(DBPersistentDao dbPersistentDao) {
        this.dbPersistentDao = dbPersistentDao;
    }

    @Required
    public void setPaoDao(PaoDao paoDao) {
        this.paoDao = paoDao;
    }
    
}
