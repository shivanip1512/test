package com.cannontech.web.loadprofile;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.cannontech.amr.errors.dao.DeviceErrorTranslatorDao;
import com.cannontech.amr.meter.dao.MeterDao;
import com.cannontech.common.util.TemplateProcessorFactory;
import com.cannontech.core.dao.ContactDao;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.YukonUserDao;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.core.service.LoadProfileService;
import com.cannontech.core.service.DateFormattingService.DateFormatEnum;
import com.cannontech.core.service.impl.LoadProfileServiceEmailCompletionCallbackImpl;
import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.database.data.lite.LiteDeviceMeterNumber;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.servlet.YukonUserContextUtils;
import com.cannontech.tools.email.EmailService;
import com.cannontech.user.YukonUserContext;
import com.cannontech.util.ServletUtil;
import com.cannontech.web.util.JsonView;

public class LoadProfileController extends MultiActionController {
    private LoadProfileService loadProfileService;
    private EmailService emailService;
    private PaoDao paoDao;
    private DeviceDao deviceDao;
    private YukonUserDao yukonUserDao;
    private ContactDao contactDao;
    private DateFormattingService dateFormattingService;
    private MeterDao meterDao;
    private DeviceErrorTranslatorDao deviceErrorTranslatorDao;
    private TemplateProcessorFactory templateProcessorFactory;
    /*
     * Long load profile email message format
     * NOTE: Outlook will sometimes strip extra line breaks of its own accord.  For some
     * reason putting extra spaces in front of the line breaks seems to prevent this.  
     * Not sure about other email clients.
     */
    
    final long MS_IN_A_DAY = 1000*60*60*24;
    /*    String template = "{name} is {age|####.000#}";
    ********************************************************************
    FULL("{default} {status||{unit}} {time|MM/dd/yyyy HH:mm:ss z}"),*/
        
    public ModelAndView initiateLoadProfile(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView mav = new ModelAndView(new JsonView());
        
        try {
            
            YukonUserContext userContext = YukonUserContextUtils.getYukonUserContext(request);
            
            String email = ServletRequestUtils.getRequiredStringParameter(request, "email");
            int deviceId = ServletRequestUtils.getRequiredIntParameter(request, "deviceId");
            String profileRequestOrigin = ServletRequestUtils.getRequiredStringParameter(request, "profileRequestOrigin");
            
            Date startDate = null;
            Date stopDate = null;
            Date maxStopDate = new Date();
            maxStopDate = DateUtils.truncate(maxStopDate, Calendar.SECOND);
            
            String peakDateStr = ServletRequestUtils.getStringParameter(request, "peakDate", null);
            String startDateStr = ServletRequestUtils.getStringParameter(request, "startDate", "");
            String stopDateStr = ServletRequestUtils.getStringParameter(request, "stopDate", "");
            
            if (peakDateStr != null) {
                
                String beforeDaysStr = ServletRequestUtils.getRequiredStringParameter(request, "beforeDays");
                String afterDaysStr = ServletRequestUtils.getRequiredStringParameter(request, "afterDays");
                
                // before peak
                if (beforeDaysStr.equalsIgnoreCase("ALL")) {
                    startDate = dateFormattingService.flexibleDateParser(startDateStr, userContext);
                    startDate = DateUtils.truncate(startDate, Calendar.DATE);
                }
                else {
                    startDate = dateFormattingService.flexibleDateParser(peakDateStr, userContext);
                    startDate = DateUtils.truncate(startDate, Calendar.DATE);
                    startDate = DateUtils.addDays(startDate, -Integer.parseInt(beforeDaysStr));
                }
                
                // after peak
                if (afterDaysStr.equalsIgnoreCase("ALL")) {
                    stopDate = dateFormattingService.flexibleDateParser(stopDateStr, userContext);
                    stopDate = DateUtils.truncate(stopDate, Calendar.DATE);
                    stopDate = DateUtils.addDays(stopDate, 1);
                }
                else {
                    stopDate = dateFormattingService.flexibleDateParser(peakDateStr, userContext);
                    stopDate = DateUtils.truncate(stopDate, Calendar.DATE);
                    stopDate = DateUtils.addDays(stopDate, Integer.parseInt(afterDaysStr));
                    stopDate = DateUtils.addDays(stopDate, 1);
                }
                
            }
            else {
                
                startDate = dateFormattingService.flexibleDateParser(startDateStr, userContext);
                startDate = DateUtils.truncate(startDate, Calendar.DATE);
                
                stopDate = dateFormattingService.flexibleDateParser(stopDateStr, userContext);
                stopDate = DateUtils.truncate(stopDate, Calendar.DATE);
                stopDate = DateUtils.addDays(stopDate, 1);
            }
            
            if (stopDate.after(maxStopDate)) {
                stopDate = maxStopDate;
            }
            
            Validate.isTrue(startDate == null || stopDate == null || startDate.before(stopDate), 
                            "Start Date must be before Stop Date");

            LiteYukonPAObject device = paoDao.getLiteYukonPAO(deviceId);
            LiteDeviceMeterNumber meterNum = deviceDao.getLiteDeviceMeterNumber(deviceId);
            
            String baseSubject = "";
            if(profileRequestOrigin.equalsIgnoreCase("HBC")){
                baseSubject += "High Bill profile ";
            }
            else{
                baseSubject += "Profile ";
            }
            baseSubject += "data collection for " + meterDao.getFormattedDeviceName(meterDao.getForId(device.getLiteID())) 
                                + " from " + startDateStr + " - " + stopDateStr;
            
            // general body
            Map<String, Object> msgData = new HashMap<String, Object>();
            msgData.put("formattedDeviceName", meterDao.getFormattedDeviceName(meterDao.getForId(device.getLiteID())));
            msgData.put("deviceName", device.getPaoName());
            msgData.put("meterNumber", meterNum.getMeterNumber());
            msgData.put("physAddress", device.getAddress());
            msgData.put("startDate", startDate);
            msgData.put("stopDate", stopDate);
            long numDays = (stopDate.getTime() - startDate.getTime()) / MS_IN_A_DAY;
            msgData.put("totalDays", Long.toString(numDays));
            
            LoadProfileServiceEmailCompletionCallbackImpl callback = 
                new LoadProfileServiceEmailCompletionCallbackImpl(emailService, dateFormattingService, templateProcessorFactory, deviceErrorTranslatorDao);
            
            callback.setUserContext(userContext);
            callback.setEmail(email);
            callback.setMessageData(msgData);
            
            loadProfileService.initiateLoadProfile(device, 1, startDate, stopDate, callback, userContext);
            
            mav.addObject("success", true);
            
        } catch (ServletRequestBindingException e) {
            mav.addObject("success", false);
            mav.addObject("errString", "Missing parameter: " + e.getMessage());
        } catch (ParseException e) {
            mav.addObject("success", false);
            mav.addObject("errString", "Unable to parse: " + e.getMessage());
        }
        
        return mav;
    }
    
    public ModelAndView defaults(HttpServletRequest request, HttpServletResponse response) throws ServletRequestBindingException {
        ModelAndView mav = new ModelAndView(new JsonView());

        int deviceId = ServletRequestUtils.getRequiredIntParameter(request, "deviceId");
        
        YukonUserContext userContext = YukonUserContextUtils.getYukonUserContext(request);
        LiteYukonUser yukonUser = ServletUtil.getYukonUser(request);
        
        LiteContact contact = yukonUserDao.getLiteContact(yukonUser.getUserID());
        String email = "";
        if (contact != null) {
            String[] allEmailAddresses = contactDao.getAllEmailAddresses(contact.getContactID());
            if (allEmailAddresses.length > 0) {
                email = allEmailAddresses[0];
            }
        }
        mav.addObject("email", email);
        
        String startDateStr = ServletRequestUtils.getStringParameter(request, "startDate", "");
        String stopDateStr = ServletRequestUtils.getStringParameter(request, "stopDate", "");
        
        if(StringUtils.isNotBlank(startDateStr) && StringUtils.isNotBlank(stopDateStr)){
            
            try {
                Date startDate = dateFormattingService.flexibleDateParser(startDateStr, DateFormattingService.DateOnlyMode.START_OF_DAY, userContext);
                Date stopDate = dateFormattingService.flexibleDateParser(stopDateStr, DateFormattingService.DateOnlyMode.START_OF_DAY, userContext);
            
                mav.addObject("startDate", dateFormattingService.formatDate(startDate, DateFormatEnum.DATE, userContext));
                mav.addObject("stopDate", dateFormattingService.formatDate(stopDate, DateFormatEnum.DATE, userContext));
            
            } catch (ParseException e) {
                mav.addObject("success", false);
                mav.addObject("errString", "Unable to parse: " + e.getMessage());
            }
            
        } else {
            Date now = new Date();
            String stopDate = dateFormattingService.formatDate(now, DateFormatEnum.DATE, userContext);
            mav.addObject("stopDate", stopDate);
            
            int startOffset = ServletRequestUtils.getIntParameter(request, "startOffset", 7);
            Date weekAgo = DateUtils.addDays(now, -startOffset);
            String startDate = dateFormattingService.formatDate(weekAgo, DateFormatEnum.DATE, userContext);
            mav.addObject("startDate", startDate);
        }
        
        LiteYukonPAObject device = paoDao.getLiteYukonPAO(deviceId);
        // re-get pending
        List<Map<String, String>> pendingRequests = loadProfileService.getPendingRequests(device, userContext);
        mav.addObject("pendingRequests", pendingRequests);
        
        return mav;
    }
    
    public ModelAndView cancelLoadProfile(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView mav = new ModelAndView(new JsonView());
        
        try{
            

            YukonUserContext userContext = YukonUserContextUtils.getYukonUserContext(request);

            long requestId = ServletRequestUtils.getRequiredLongParameter(request, "requestId");
            int deviceId = ServletRequestUtils.getRequiredIntParameter(request, "deviceId");
            LiteYukonPAObject device = paoDao.getLiteYukonPAO(deviceId);

            // remove
            loadProfileService.removePendingLoadProfileRequest(device, requestId, userContext);
            
            // re-get pending
            List<Map<String, String>> pendingRequests = loadProfileService.getPendingRequests(device, userContext);
            mav.addObject("pendingRequests", pendingRequests);
            
            
        } catch (ServletRequestBindingException e) {
            mav.addObject("success", false);
            mav.addObject("errString", "Missing parameter: " + e.getMessage());
        }
        
        return mav;
    }
    
    @Required
    public void setEmailService(EmailService emailService) {
        this.emailService = emailService;
    }

    @Required
    public void setLoadProfileService(LoadProfileService loadProfileService) {
        this.loadProfileService = loadProfileService;
    }

    @Required
    public void setPaoDao(PaoDao paoDao) {
        this.paoDao = paoDao;
    }

    @Required
    public void setYukonUserDao(YukonUserDao yukonUserDao) {
        this.yukonUserDao = yukonUserDao;
    }

    @Required
    public void setContactDao(ContactDao contactDao) {
        this.contactDao = contactDao;
    }

    @Required
    public DeviceDao getDeviceDao() {
        return deviceDao;
    }

    @Required
    public void setDeviceDao(DeviceDao deviceDao) {
        this.deviceDao = deviceDao;
    }
    
    @Required
    public void setDateFormattingService(
            DateFormattingService dateFormattingService) {
        this.dateFormattingService = dateFormattingService;
    }

    @Required
    public void setMeterDao(MeterDao meterDao) {
        this.meterDao = meterDao;
    }

    @Required
    public void setDeviceErrorTranslatorDao(
            DeviceErrorTranslatorDao deviceErrorTranslatorDao) {
        this.deviceErrorTranslatorDao = deviceErrorTranslatorDao;
    }
    
    @Autowired
    public void setTemplateProcessorFactory(TemplateProcessorFactory templateProcessorFactory) {
        this.templateProcessorFactory = templateProcessorFactory;
    }

}
