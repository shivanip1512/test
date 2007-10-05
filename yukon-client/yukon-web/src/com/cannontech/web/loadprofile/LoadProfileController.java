package com.cannontech.web.loadprofile;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.cannontech.amr.errors.dao.DeviceErrorTranslatorDao;
import com.cannontech.amr.meter.dao.MeterDao;
import com.cannontech.core.dao.ContactDao;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.YukonUserDao;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.core.service.LongLoadProfileService;
import com.cannontech.core.service.LongLoadProfileService.ProfileRequestInfo;
import com.cannontech.core.service.impl.LongLoadProfileServiceEmailCompletionCallbackImpl;
import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.database.data.lite.LiteDeviceMeterNumber;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.tools.email.EmailService;
import com.cannontech.util.ServletUtil;
import com.cannontech.web.util.JsonView;

public class LoadProfileController extends MultiActionController {
    private LongLoadProfileService loadProfileService;
    private EmailService emailService;
    private PaoDao paoDao;
    private DeviceDao deviceDao;
    private YukonUserDao yukonUserDao;
    private ContactDao contactDao;
    private DateFormattingService dateFormattingService;
    private MeterDao meterDao;
    private DeviceErrorTranslatorDao deviceErrorTranslatorDao;
    private static DateFormat dateFormat = new SimpleDateFormat("MM/dd/yy hh:mma");
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
            
            LiteYukonUser user = ServletUtil.getYukonUser(request);
            
            String email = ServletRequestUtils.getRequiredStringParameter(request, "email");
            int deviceId = ServletRequestUtils.getRequiredIntParameter(request, "deviceId");
            String startDateStr = ServletRequestUtils.getStringParameter(request, "startDate", "");
            Date startDate = dateFormattingService.flexibleDateParser(startDateStr, DateFormattingService.DateOnlyMode.START_OF_DAY, user);
            String stopDateStr = ServletRequestUtils.getStringParameter(request, "stopDate", "");
            Date stopDate = dateFormattingService.flexibleDateParser(stopDateStr, DateFormattingService.DateOnlyMode.END_OF_DAY, user);
            String profileRequestOrigin = ServletRequestUtils.getRequiredStringParameter(request, "profileRequestOrigin");
            
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
            msgData.put("email", email);
            msgData.put("formattedDeviceName", meterDao.getFormattedDeviceName(meterDao.getForId(device.getLiteID())));
            msgData.put("deviceName", device.getPaoName());
            msgData.put("meterNumber", meterNum.getMeterNumber());
            msgData.put("physAddress", device.getAddress());
            msgData.put("startDate", dateFormat.format(startDate));
            msgData.put("stopDate", dateFormat.format(stopDate));
            long numDays = (stopDate.getTime() - startDate.getTime()) / MS_IN_A_DAY;
            msgData.put("totalDays", Long.toString(numDays));
            
            LongLoadProfileServiceEmailCompletionCallbackImpl callback = 
                new LongLoadProfileServiceEmailCompletionCallbackImpl(emailService, dateFormattingService, deviceErrorTranslatorDao);
            
            callback.setSuccessMessage(msgData);
            callback.setFailureMessage(msgData);
            callback.setCancelMessage(msgData);
            
            loadProfileService.initiateLongLoadProfile(device, 1, startDate, stopDate, callback);
            
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
                Date startDate = dateFormattingService.flexibleDateParser(startDateStr, DateFormattingService.DateOnlyMode.START_OF_DAY, yukonUser);
                Date stopDate = dateFormattingService.flexibleDateParser(stopDateStr, DateFormattingService.DateOnlyMode.START_OF_DAY, yukonUser);
            
                SimpleDateFormat format = new SimpleDateFormat("MM/dd/yy");
                
                mav.addObject("startDate", format.format(startDate));
                mav.addObject("stopDate", format.format(stopDate));
            
            } catch (ParseException e) {
                mav.addObject("success", false);
                mav.addObject("errString", "Unable to parse: " + e.getMessage());
            }
            
        } else {
            Date now = new Date();
            String stopDate = dateFormat.format(now);
            mav.addObject("stopDate", stopDate);
            
            int startOffset = ServletRequestUtils.getIntParameter(request, "startOffset", 7);
            Date weekAgo = DateUtils.addDays(now, -startOffset);
            String startDate = dateFormat.format(weekAgo);
            mav.addObject("startDate", startDate);
        }
        
        LiteYukonPAObject device = paoDao.getLiteYukonPAO(deviceId);
        // re-get pending
        List<Map<String, String>> pendingRequests = getPendingRequests(device, yukonUser);
        mav.addObject("pendingRequests", pendingRequests);
        
        return mav;
    }
    
    public ModelAndView cancelLoadProfile(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView mav = new ModelAndView(new JsonView());
        
        try{
            

            LiteYukonUser user = ServletUtil.getYukonUser(request);

            long requestId = ServletRequestUtils.getRequiredLongParameter(request, "requestId");
            int deviceId = ServletRequestUtils.getRequiredIntParameter(request, "deviceId");
            LiteYukonPAObject device = paoDao.getLiteYukonPAO(deviceId);

            // remove
            loadProfileService.removePendingLongLoadProfileRequest(device, requestId, user);
            
            // re-get pending
            List<Map<String, String>> pendingRequests = getPendingRequests(device, user);
            mav.addObject("pendingRequests", pendingRequests);
            
            
        } catch (ServletRequestBindingException e) {
            mav.addObject("success", false);
            mav.addObject("errString", "Missing parameter: " + e.getMessage());
        }
        
        return mav;
    }
    
    private List<Map<String, String>> getPendingRequests(LiteYukonPAObject device,  LiteYukonUser user){
        
        List<Map<String,String>> pendingRequests = new ArrayList<Map<String, String>>();
        Collection<ProfileRequestInfo> loadProfileRequests = loadProfileService.getPendingLongLoadProfileRequests(device);
        for (ProfileRequestInfo info : loadProfileRequests) {
            HashMap<String, String> data = new HashMap<String, String>();
            
            data.put("email", info.runner.toString());
            data.put("from", dateFormat.format(info.from));
            data.put("to", dateFormat.format(info.to));
            data.put("command", info.request.getCommandString());
            data.put("requestId", Long.toString(info.request.getUserMessageID()));
            pendingRequests.add(data);
        }
        
        return pendingRequests;
        
    }
        
        
    @Required
    public void setEmailService(EmailService emailService) {
        this.emailService = emailService;
    }

    @Required
    public void setLoadProfileService(LongLoadProfileService loadProfileService) {
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

}
