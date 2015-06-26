package com.cannontech.web.widget;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.cannontech.amr.meter.dao.MeterDao;
import com.cannontech.amr.meter.model.PlcMeter;
import com.cannontech.common.device.peakReport.model.PeakReportPeakType;
import com.cannontech.common.device.peakReport.model.PeakReportResult;
import com.cannontech.common.device.peakReport.model.PeakReportRunType;
import com.cannontech.common.device.peakReport.service.PeakReportService;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.core.authorization.service.PaoCommandAuthorizationService;
import com.cannontech.core.authorization.service.RoleAndPropertyDescriptionService;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.servlet.YukonUserContextUtils;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.widget.support.SimpleWidgetInput;
import com.cannontech.web.widget.support.WidgetControllerBase;
import com.cannontech.web.widget.support.WidgetInput;
import com.cannontech.web.widget.support.WidgetParameterHelper;

/**
 * Widget used to display point data in a trend
 */
@Controller
@RequestMapping("/peakReportWidget/*")
public class PeakReportWidget extends WidgetControllerBase {

    @Autowired private PeakReportService peakReportService;
    @Autowired private MeterDao meterDao;
    @Autowired private DateFormattingService dateFormattingService;
    @Autowired private AttributeService attributeService;
    @Autowired private PaoCommandAuthorizationService commandAuthorizationService;
    @Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;
    
    
    @Autowired
    public PeakReportWidget(@Qualifier("widgetInput.deviceId") SimpleWidgetInput simpleWidgetInput,
            RoleAndPropertyDescriptionService roleAndPropertyDescriptionService) {
        
        Set<WidgetInput> simpleWidgetInputSet = new HashSet<WidgetInput>();
        simpleWidgetInputSet.add(simpleWidgetInput);
        
        this.setIdentityPath("common/deviceIdentity.jsp");
        this.setInputs(simpleWidgetInputSet);
        this.setRoleAndPropertiesChecker(roleAndPropertyDescriptionService.compile("METERING"));
    }
    
    @Override
    @RequestMapping("render")
    public ModelAndView render(HttpServletRequest request,
            HttpServletResponse response) throws Exception {

        ModelAndView mav = new ModelAndView("peakReportWidget/render.jsp");
        
        // user
        YukonUserContext userContext = YukonUserContextUtils.getYukonUserContext(request);

        // deviceId
        int deviceId = WidgetParameterHelper.getRequiredIntParameter(request, "deviceId");

        // get meter
        PlcMeter meter = meterDao.getPlcMeterForId(deviceId);

        // available channels
        Integer prevChannel = WidgetParameterHelper.getIntParameter(request, "channel", 1);
        
        Map<Integer, Attribute> channelAttributes = new HashMap<Integer, Attribute>();
        channelAttributes.put(1,BuiltInAttribute.LOAD_PROFILE);
        channelAttributes.put(4,BuiltInAttribute.VOLTAGE_PROFILE);
        
        Map<Integer, String> channelDisplayNames = new HashMap<Integer, String>();
        
        String channel1 = messageSourceResolver.getMessageSourceAccessor(userContext).getMessage("yukon.web.widgets.peakReportWidget.channel1");
        channelDisplayNames.put(1, channel1);
        
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
            String peakTypeDisplayName = messageSourceResolver.getMessageSourceAccessor(userContext).getMessage(peakType.getReportTypeDisplayNameKey()); 
            peakTypeInfo.put("peakTypeDisplayName",peakTypeDisplayName);
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
    
    @RequestMapping("requestReport")
    public ModelAndView requestReport(HttpServletRequest request,
            HttpServletResponse response) throws Exception {

        // user
        YukonUserContext userContext = YukonUserContextUtils.getYukonUserContext(request);

        // deviceId
        int deviceId = WidgetParameterHelper.getRequiredIntParameter(request, "deviceId");
        
        // get meter
        PlcMeter meter = meterDao.getPlcMeterForId(deviceId);
        
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

    @RequestMapping("setDefaultMavDateTime")
    private ModelAndView setDefaultMavDateTime(HttpServletRequest request, ModelAndView mav, YukonUserContext userContext){
        
        String startDateStr = WidgetParameterHelper.getStringParameter(request, "startDateStr", "");
        String stopDateStr = WidgetParameterHelper.getStringParameter(request, "stopDateStr", "");
        
        // start date
        if (StringUtils.isBlank(startDateStr)) {
        	Date startDate = DateUtils.addDays(new Date(), -5);
            mav.addObject("startDateStr",
                          dateFormattingService.format(startDate,
                                                       DateFormattingService.DateFormatEnum.DATE,
                                                       userContext));
            mav.addObject("startDate", startDate);
        }
        else {
            mav.addObject("startDateStr", startDateStr);
            try {
				mav.addObject("startDate", dateFormattingService.flexibleDateParser(startDateStr, userContext));
			} catch (ParseException e) {}
        }
        
        // stop date
        if (StringUtils.isBlank(stopDateStr)) {
        	Date stopDate = new Date();
            mav.addObject("stopDateStr",
                          dateFormattingService.format(stopDate,
                                                       DateFormattingService.DateFormatEnum.DATE,
                                                       userContext));
            mav.addObject("stopDate", stopDate);
        }
        else {
            mav.addObject("stopDateStr", stopDateStr);
            try {
				mav.addObject("startDate", dateFormattingService.flexibleDateParser(stopDateStr, userContext));
			} catch (ParseException e) {}
        }
            
        return mav;
    }
}
