package com.cannontech.web.widget;

import java.text.DecimalFormat;
import java.text.ParseException;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.servlet.ModelAndView;

import com.cannontech.amr.meter.dao.MeterDao;
import com.cannontech.amr.meter.model.Meter;
import com.cannontech.common.device.peakReport.model.PeakReportPeakType;
import com.cannontech.common.device.peakReport.model.PeakReportResult;
import com.cannontech.common.device.peakReport.model.PeakReportRunType;
import com.cannontech.common.device.peakReport.service.PeakReportService;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.core.authorization.service.PaoCommandAuthorizationService;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.servlet.YukonUserContextUtils;
import com.cannontech.user.YukonUserContext;
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
    private YukonUserContextMessageSourceResolver messageSourceResolver = null;
    
    public ModelAndView render(HttpServletRequest request,
            HttpServletResponse response) throws Exception {

        ModelAndView mav = new ModelAndView("peakReportWidget/render.jsp");
        
        // user
        YukonUserContext userContext = YukonUserContextUtils.getYukonUserContext(request);

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
        mav = setDefaultMavDateTime(request, mav, userContext);
       
        // get previous peak report
        PeakReportResult peakResult = peakReportService.retrieveArchivedPeakReport(deviceId, PeakReportRunType.SINGLE, userContext);
        
        if(peakResult != null){
            MessageSourceAccessor messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(userContext);
            DecimalFormat formatter = new DecimalFormat("#0.#");
            mav.addObject("avgVsTotal", 
                          messageSourceAccessor.getMessage("yukon.web.widgets.peakReportWidget.kwhVsKwh", 
                                                           formatter.format(peakResult.getAverageDailyUsage()),
                                                           formatter.format(peakResult.getTotalUsage())));
            mav.addObject("peakResult", peakResult);
        }
        
        boolean readable = commandAuthorizationService.isAuthorized(userContext.getYukonUser(), "getvalue lp peak", meter);
        mav.addObject("readable", readable);

        return mav;
    }
    
    public ModelAndView requestReport(HttpServletRequest request,
            HttpServletResponse response) throws Exception {

        // user
        YukonUserContext userContext = YukonUserContextUtils.getYukonUserContext(request);

        // deviceId
        int deviceId = WidgetParameterHelper.getRequiredIntParameter(request, "deviceId");
        
        // get meter
        Meter meter = meterDao.getForId(deviceId);
        
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
            
                startDate = dateFormattingService.flexibleDateParser(startDateStr, userContext);
                stopDate = dateFormattingService.flexibleDateParser(stopDateStr, userContext);
            
                String todayStr = dateFormattingService.format(new Date(), DateFormattingService.DateFormatEnum.BOTH, userContext);
                Date today = dateFormattingService.flexibleDateParser(todayStr, DateFormattingService.DateOnlyMode.END_OF_DAY, userContext);
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
        
        boolean readable = commandAuthorizationService.isAuthorized(userContext.getYukonUser(), "getvalue lp peak", meter);
        mav.addObject("readable", readable);
        
        // bad date, return mav with redisplay dates and error msg
        if(!datesOk){
            
            mav.addObject("dateErrorMessage", dateErrorMessage);
            
            mav.addObject("startDateStr", startDateStr);
            mav.addObject("stopDateStr", stopDateStr);
            
            return mav;
        }
            
        // request report from service
        PeakReportResult peakResult = peakReportService.requestPeakReport(deviceId, peakType, PeakReportRunType.SINGLE, channel, startDate, stopDate, true, userContext);
        
        mav.addObject("peakResult", peakResult);
        
        return mav;
    }

    private ModelAndView setDefaultMavDateTime(HttpServletRequest request, ModelAndView mav, YukonUserContext userContext){
        
        String startDateStr = WidgetParameterHelper.getStringParameter(request, "startDateStr", "");
        String stopDateStr = WidgetParameterHelper.getStringParameter(request, "stopDateStr", "");
        
        // start date
        if (StringUtils.isBlank(startDateStr)) {
            mav.addObject("startDateStr",
                          dateFormattingService.format(DateUtils.addDays(new Date(),
                                                                             -5),
                                                           DateFormattingService.DateFormatEnum.DATE,
                                                           userContext));
        }
        else {
            mav.addObject("startDateStr", startDateStr);
        }
        
        // stop date
        if (StringUtils.isBlank(stopDateStr)) {
            mav.addObject("stopDateStr",
                          dateFormattingService.format(new Date(),
                                                           DateFormattingService.DateFormatEnum.DATE,
                                                           userContext));
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
    
    @Autowired
    public void setMessageSourceResolver(YukonUserContextMessageSourceResolver messageSourceResolver) {
        this.messageSourceResolver = messageSourceResolver;
    }
}
