package com.cannontech.web.widget;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.servlet.ModelAndView;

import com.cannontech.amr.meter.dao.MeterDao;
import com.cannontech.amr.meter.model.Meter;
import com.cannontech.common.device.attribute.model.Attribute;
import com.cannontech.common.device.attribute.model.BuiltInAttribute;
import com.cannontech.common.device.attribute.service.AttributeService;
import com.cannontech.common.device.peakReport.model.PeakReportPeakType;
import com.cannontech.common.device.peakReport.model.PeakReportResult;
import com.cannontech.common.device.peakReport.model.PeakReportRunType;
import com.cannontech.common.device.peakReport.service.PeakReportService;
import com.cannontech.core.authorization.service.PaoCommandAuthorizationService;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.util.ServletUtil;
import com.cannontech.web.widget.support.WidgetControllerBase;
import com.cannontech.web.widget.support.WidgetParameterHelper;

/**
 * Widget used to display point data in a trend
 */
public class PeakReportWidget extends WidgetControllerBase {

    private PeakReportService peakReportService = null;
    private MeterDao meterDao = null;
    private DateFormattingService dateFormattingService = null;
    private AttributeService attributeService = null;
    private PaoCommandAuthorizationService commandAuthorizationService;
    
    private boolean readable = false;

    public ModelAndView render(HttpServletRequest request,
            HttpServletResponse response) throws Exception {

        ModelAndView mav = new ModelAndView("peakReportWidget/render.jsp");
        
        // user
        LiteYukonUser user = ServletUtil.getYukonUser(request);

        // deviceId
        int deviceId = WidgetParameterHelper.getRequiredIntParameter(request, "deviceId");

        // get meter
        Meter meter = meterDao.getForId(deviceId);

        // available channels
        Integer prevChannel = WidgetParameterHelper.getIntParameter(request, "channel", 1);
        
        Map<Integer, Attribute> channelAttributes = new HashMap<Integer, Attribute>();
        channelAttributes.put(1,BuiltInAttribute.LOAD_PROFILE);
        channelAttributes.put(4,BuiltInAttribute.VOLTAGE_PROFILE);
        
        Map<Integer, String> channelDisplayNames = new HashMap<Integer, String>();
        channelDisplayNames.put(1,"Channel 1 (Usage)");
        
        // no channel 4 (Voltage) support for now
        //channelDisplayNames.put(4,"Channel 4 (Voltage)");
        
        List<Integer> channelsNumbers = new ArrayList<Integer>(channelDisplayNames.keySet());
        Collections.sort(channelsNumbers);
        List<Map<String, String>> availableChannels = new ArrayList<Map<String, String>>();
        for(Integer channel : channelsNumbers){
            
            if(attributeService.isAttributeSupported(meter, channelAttributes.get(channel))){
                Map<String, String> channelInfo = new HashMap<String, String>();
                channelInfo.put("channelNumber",channel.toString());
                channelInfo.put("channelDescription",channelDisplayNames.get(channel));
                if (channel.equals(prevChannel)) {
                    channelInfo.put("selected","selected");
                }
                else {
                    channelInfo.put("selected","");
                }
                availableChannels.add(channelInfo);
            }
        }
        mav.addObject("availableChannels", availableChannels);
        
        // available peak type reports
        String prevPeakTypeStr = WidgetParameterHelper.getStringParameter(request, "peakType", null);
        PeakReportPeakType prevPeakType =null;
        if (prevPeakTypeStr != null){
            prevPeakType = PeakReportPeakType.valueOf(prevPeakTypeStr);
        }
        
        List<PeakReportPeakType> peakTypes = new ArrayList<PeakReportPeakType>();
        peakTypes.add(PeakReportPeakType.DAY);
        peakTypes.add(PeakReportPeakType.HOUR);
        peakTypes.add(PeakReportPeakType.INTERVAL);

        List<Map<String, String>> availablePeakTypes = new ArrayList<Map<String, String>>();
        for(PeakReportPeakType peakType : peakTypes){
            
            Map<String, String> peakTypeInfo = new HashMap<String, String>();
            peakTypeInfo.put("peakType",peakType.toString());
            peakTypeInfo.put("peakTypeDisplayName",peakType.getReportTypeDisplayName());
            if (peakType.equals(prevPeakType)) {
                peakTypeInfo.put("selected","selected");
            }
            else {
                peakTypeInfo.put("selected","");
            }
            availablePeakTypes.add(peakTypeInfo);
        }
        mav.addObject("availablePeakTypes", availablePeakTypes);
        
        // initialize dates/times
        mav = setDefaultMavDateTime(request, mav, user);
       
        // get previous peak report
        PeakReportResult peakResult = peakReportService.retrieveArchivedPeakReport(deviceId, PeakReportRunType.SINGLE, user);
        
        if(peakResult != null){
            mav.addObject("peakResult", peakResult);
            addParsedPeakResultValuesToMav(peakResult, mav, user, deviceId, prevChannel);
        }
        
        readable = commandAuthorizationService.isAuthorized(user, "getvalue lp peak", meter);
        mav.addObject("readable", readable);

        return mav;
    }
    
    public ModelAndView requestReport(HttpServletRequest request,
            HttpServletResponse response) throws Exception {

        // user
        LiteYukonUser user = ServletUtil.getYukonUser(request);

        // deviceId
        int deviceId = WidgetParameterHelper.getRequiredIntParameter(request, "deviceId");
        
        // channel
        int channel = WidgetParameterHelper.getRequiredIntParameter(request, "channel");
        
        // peakType
        PeakReportPeakType peakType = PeakReportPeakType.valueOf(WidgetParameterHelper.getRequiredStringParameter(request, "peakType"));
        
        // validate dates/times
        String startDateStr = WidgetParameterHelper.getStringParameter(request, "startDateStr", "");
        String stopDateStr = WidgetParameterHelper.getStringParameter(request, "stopDateStr", "");
        
        boolean datesOk = false;
        Date startDate = null;
        Date stopDate = null;
        String dateErrorMessage = "Unknown Error";
        try{
            
            if (StringUtils.isBlank(startDateStr)) {
                datesOk = false;
                dateErrorMessage = "Start Date Required";
            } else if (StringUtils.isBlank(stopDateStr)) {
                datesOk = false;
                dateErrorMessage = "Stop Date Required";
            }
            else{
            
                startDate = dateFormattingService.flexibleDateParser(startDateStr, user);
                stopDate = dateFormattingService.flexibleDateParser(stopDateStr, user);
            
                String todayStr = dateFormattingService.formatDate(new Date(), DateFormattingService.DateFormatEnum.BOTH, user);
                Date today = dateFormattingService.flexibleDateParser(todayStr, DateFormattingService.DateOnlyMode.END_OF_DAY, user);
                datesOk = true;

                if(datesOk){
                    if (startDate.after(stopDate)) {
                        datesOk = false;
                        dateErrorMessage = "Start Date Must Be Before Stop Date";
                    } else if (stopDate.after(today)) {
                        datesOk = false;
                        dateErrorMessage = "Stop Date/Time Must Be On Or Before The Current Date/Time";
                    } else {
                        datesOk = true;
                    }
                }
            }
        }
        catch (ParseException e) {
            datesOk = false;
            dateErrorMessage = "Unable to parse date: " + e.getMessage();
        }
        
        // init mav
        ModelAndView mav = new ModelAndView("peakReportWidget/peakSummaryReportResult.jsp");
        
        mav.addObject("readable", readable);
        
        // bad date, return mav with redisplay dates and error msg
        if(!datesOk){
            
            mav.addObject("dateErrorMessage", dateErrorMessage);
            
            mav.addObject("startDateStr", startDateStr);
            mav.addObject("stopDateStr", stopDateStr);
            
            return mav;
        }
            
        // request report from service
        PeakReportResult peakResult = peakReportService.requestPeakReport(deviceId, peakType, PeakReportRunType.SINGLE, channel, startDate, stopDate, true, user);
        
        mav.addObject("peakResult", peakResult);
        
        // special formatting of peakResult dates for display purposes
        if(!peakResult.isNoData()) {
            addParsedPeakResultValuesToMav(peakResult, mav, user, deviceId, channel);
        }
        
        return mav;
    }

    
   private void addParsedPeakResultValuesToMav(PeakReportResult peakResult, ModelAndView mav, LiteYukonUser user, int deviceId, int channel) {
       
       // special formatting of peakResult dates for display purposes
       String runDateDisplay = dateFormattingService.formatDate(peakResult.getRunDate(), DateFormattingService.DateFormatEnum.DATEHM, user);
       mav.addObject("runDateDisplay", runDateDisplay);
       
       String periodStartDateDisplay = dateFormattingService.formatDate(peakResult.getRangeStartDate(), DateFormattingService.DateFormatEnum.DATE, user);
       mav.addObject("periodStartDateDisplay", periodStartDateDisplay);
       
       String periodStopDateDisplay = dateFormattingService.formatDate(peakResult.getRangeStopDate(), DateFormattingService.DateFormatEnum.DATE, user);
       mav.addObject("periodStopDateDisplay", periodStopDateDisplay);
       
       mav.addObject("displayName", peakResult.getPeakType().getDisplayName());
       mav.addObject("reportTypeDisplayName",peakResult.getPeakType().getReportTypeDisplayName());
       
       String peakValueStr = "";
       if(peakResult.getPeakType() == PeakReportPeakType.DAY) {
           peakValueStr = dateFormattingService.formatDate(peakResult.getPeakStopDate(), DateFormattingService.DateFormatEnum.DATE, user);
       }
       else if(peakResult.getPeakType() == PeakReportPeakType.HOUR) {
           peakValueStr = new SimpleDateFormat("MM/dd/yy").format(peakResult.getPeakStopDate());
           peakValueStr += " ";
           peakValueStr += new SimpleDateFormat("Ka").format(peakResult.getPeakStartDate());
           peakValueStr += " - ";
           peakValueStr += new SimpleDateFormat("Ka").format(DateUtils.addMinutes(peakResult.getPeakStopDate(), 1));
       }
       else if(peakResult.getPeakType() == PeakReportPeakType.INTERVAL) {
           int interval = peakReportService.getChannelIntervalForDevice(deviceId,channel);
           peakValueStr = new SimpleDateFormat("MM/dd/yy").format(peakResult.getPeakStopDate());
           peakValueStr += " ";
           if(interval == 60){
               peakValueStr += new SimpleDateFormat("Ha").format(peakResult.getPeakStartDate());
               peakValueStr += " - ";
               peakValueStr += new SimpleDateFormat("Ha").format(DateUtils.addMinutes(peakResult.getPeakStopDate(), 1));
           }
           else{
               peakValueStr += new SimpleDateFormat("K:mma").format(peakResult.getPeakStartDate());
               peakValueStr += " - ";
               peakValueStr += new SimpleDateFormat("K:mma").format(DateUtils.addMinutes(peakResult.getPeakStopDate(), 1));
           }
       }
       mav.addObject("peakValueStr", peakValueStr);
   }
 
    private ModelAndView setDefaultMavDateTime(HttpServletRequest request, ModelAndView mav, LiteYukonUser user){
        
        String startDateStr = WidgetParameterHelper.getStringParameter(request, "startDateStr", "");
        String stopDateStr = WidgetParameterHelper.getStringParameter(request, "stopDateStr", "");
        
        // start date
        if (StringUtils.isBlank(startDateStr)) {
            mav.addObject("startDateStr",
                          dateFormattingService.formatDate(DateUtils.addDays(new Date(),
                                                                             -5),
                                                           DateFormattingService.DateFormatEnum.DATE,
                                                           user));
        }
        else {
            mav.addObject("startDateStr", startDateStr);
        }
        
        // stop date
        if (StringUtils.isBlank(stopDateStr)) {
            mav.addObject("stopDateStr",
                          dateFormattingService.formatDate(new Date(),
                                                           DateFormattingService.DateFormatEnum.DATE,
                                                           user));
        }
        else {
            mav.addObject("stopDateStr", stopDateStr);
        }
            
        return mav;
    }


    @Required
    public void setMeterDao(MeterDao meterDao) {
        this.meterDao = meterDao;
    }
    
    @Required
    public void setDateFormattingService(DateFormattingService dateFormattingService) {
        this.dateFormattingService = dateFormattingService;
    }
  
    @Required
    public void setPeakReportService(PeakReportService peakReportService) {
        this.peakReportService = peakReportService;
    }

    @Required
    public void setAttributeService(AttributeService attributeService) {
        this.attributeService = attributeService;
    }
    
    @Required
    public void setCommandAuthorizationService(
			PaoCommandAuthorizationService commandAuthorizationService) {
		this.commandAuthorizationService = commandAuthorizationService;
	}
}
