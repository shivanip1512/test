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
import java.util.TimeZone;

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

import com.cannontech.common.util.SimpleTemplateProcessor;
import com.cannontech.common.util.TemplateProcessor;
import com.cannontech.common.util.TimeUtil;
import com.cannontech.core.dao.ContactDao;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.PointDao;
import com.cannontech.core.dao.YukonUserDao;
import com.cannontech.core.service.LongLoadProfileService;
import com.cannontech.core.service.LongLoadProfileService.ProfileRequestInfo;
import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.database.data.lite.LiteDeviceMeterNumber;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.tools.email.DefaultEmailMessage;
import com.cannontech.tools.email.EmailCompletionCallback;
import com.cannontech.tools.email.EmailService;
import com.cannontech.util.ServletUtil;

public class LoadProfileController extends MultiActionController {
    private LongLoadProfileService loadProfileService;
    private EmailService emailService;
    private PaoDao paoDao;
    private DeviceDao deviceDao;
    private YukonUserDao yukonUserDao;
    private ContactDao contactDao;
    private static DateFormat dateFormat = new SimpleDateFormat("MM/dd/yy hh:mma");
    /*
     * Long load profile email message format
     * NOTE: Outlook will sometimes strip extra line breaks of its own accord.  For some
     * reason putting extra spaces in front of the line breaks seems to prevent this.  
     * Not sure about other email clients.
     */
    private final String lpNotificationFormat = "{statusMsg}\n\n" +
            "Device Summary\n" +
            "Device Name: {deviceName}    \n" +
            "Meter Number: {meterNumber}    \n" +
            "Physical Address: {physAddress}\n\n" +
            "Request Range: {startDate} to {stopDate}    \n" +
            "Total Requested Days: {totalDays}\n\n" +
            "Data is now available online." ;
    
    final long MS_IN_A_DAY = 1000*60*60*24;
    /*    String template = "{name} is {age|####.000#}";
    ********************************************************************
    FULL("{default} {status||{unit}} {time|MM/dd/yyyy HH:mm:ss z}"),*/
        
    public ModelAndView initiateLoadProfile(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView mav = new ModelAndView("json");
        
        try {
            
            LiteYukonUser user = ServletUtil.getYukonUser(request);
            TimeZone timeZone = yukonUserDao.getUserTimeZone(user);
            TemplateProcessor tp = new SimpleTemplateProcessor();
            
            String email = ServletRequestUtils.getRequiredStringParameter(request, "email");
            int deviceId = ServletRequestUtils.getRequiredIntParameter(request, "deviceId");
            String startDateStr = ServletRequestUtils.getStringParameter(request, "startDate", "");
            Date startDate = TimeUtil.flexibleDateParser(startDateStr, TimeUtil.NO_TIME_MODE.START_OF_DAY, timeZone);
            String stopDateStr = ServletRequestUtils.getStringParameter(request, "stopDate", "");
            Date stopDate = TimeUtil.flexibleDateParser(stopDateStr, TimeUtil.NO_TIME_MODE.END_OF_DAY, timeZone);
            
            Validate.isTrue(startDate == null || stopDate == null || startDate.before(stopDate), 
                            "Start Date must be before Stop Date");

            LiteYukonPAObject device = paoDao.getLiteYukonPAO(deviceId);
            LiteDeviceMeterNumber meterNum = deviceDao.getLiteDeviceMeterNumber(deviceId);
            
            DefaultEmailMessage successEmailer = new DefaultEmailMessage();
            successEmailer.setRecipient(email);
            String subject = "Data collection for " + device.getPaoName() + " completed";
            successEmailer.setSubject(subject);
            
            Map<String, Object> msgData = new HashMap<String, Object>();
            msgData.put("statusMsg", "Your long load profile request has completed.");
            msgData.put("deviceName", device.getPaoName());
            msgData.put("meterNumber", meterNum.getMeterNumber());
            msgData.put("physAddress", device.getAddress());
            msgData.put("startDate", dateFormat.format(startDate));
            msgData.put("stopDate", dateFormat.format(stopDate));
            long numDays = (stopDate.getTime() - startDate.getTime())/MS_IN_A_DAY;
            msgData.put("totalDays", Long.toString(numDays));
            successEmailer.setBody(tp.process(lpNotificationFormat, msgData));
            
            DefaultEmailMessage failureEmailer = new DefaultEmailMessage();
            failureEmailer.setRecipient(email);
            subject = "Data collection for " + device.getPaoName() + " failed";
            failureEmailer.setSubject(subject);
            msgData.put("statusMsg", "Your long load profile request has encountered an unknown error.");
            failureEmailer.setBody(tp.process(lpNotificationFormat, msgData));
            
            EmailCompletionCallback callback = new EmailCompletionCallback(emailService);
            callback.setSuccessMessage(successEmailer);
            callback.setFailureMessage(failureEmailer);
            
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
        ModelAndView mav = new ModelAndView("json");

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
            LiteYukonUser user = ServletUtil.getYukonUser(request);
            TimeZone timeZone = yukonUserDao.getUserTimeZone(user);
            
            try {
                Date startDate = TimeUtil.flexibleDateParser(startDateStr, TimeUtil.NO_TIME_MODE.START_OF_DAY, timeZone);
                Date stopDate = TimeUtil.flexibleDateParser(stopDateStr, TimeUtil.NO_TIME_MODE.START_OF_DAY, timeZone);
            
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
        List<Map<String,String>> requestData = new ArrayList<Map<String, String>>();
        Collection<ProfileRequestInfo> loadProfileRequests = loadProfileService.getPendingLongLoadProfileRequests(device);
        for (ProfileRequestInfo info : loadProfileRequests) {
            HashMap<String, String> data = new HashMap<String, String>();
            data.put("email", info.runner.toString());
            data.put("from", dateFormat.format(info.from));
            data.put("to", dateFormat.format(info.to));
            data.put("command", info.request.getCommandString());
            data.put("requestId", Long.toString(info.request.getUserMessageID()));
            requestData.add(data);
        }
        mav.addObject("pendingRequests", requestData);
        return mav;
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

}
