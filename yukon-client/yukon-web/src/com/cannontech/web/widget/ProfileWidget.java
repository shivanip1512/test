package com.cannontech.web.widget;

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
import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.servlet.ModelAndView;

import com.cannontech.amr.errors.dao.DeviceErrorTranslatorDao;
import com.cannontech.amr.meter.dao.MeterDao;
import com.cannontech.core.dao.DBPersistentDao;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.core.service.LongLoadProfileService;
import com.cannontech.core.service.LongLoadProfileService.ProfileRequestInfo;
import com.cannontech.core.service.impl.LongLoadProfileServiceEmailCompletionCallbackImpl;
import com.cannontech.database.data.device.MCTBase;
import com.cannontech.database.data.lite.LiteDeviceMeterNumber;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.pao.YukonPAObject;
import com.cannontech.database.db.device.DeviceLoadProfile;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.tools.email.EmailService;
import com.cannontech.util.ServletUtil;
import com.cannontech.web.widget.support.WidgetControllerBase;
import com.cannontech.web.widget.support.WidgetParameterHelper;

/**
 * Widget used to display point data in a trend
 */
public class ProfileWidget extends WidgetControllerBase {

//    private LongLoadProfileService.EmailCompletionCallback emailCompletionCallback = null;
    private LongLoadProfileService loadProfileService = null;
    private EmailService emailService = null;
    private PaoDao paoDao = null;
    private DeviceDao deviceDao = null;
    private MeterDao meterDao = null;
    private DBPersistentDao dbPersistentDao = null;
    private DateFormattingService dateFormattingService = null;
    private DeviceErrorTranslatorDao deviceErrorTranslatorDao = null;
    private static DateFormat dateFormat = new SimpleDateFormat("MM/dd/yy hh:mma");
    /*
     * Long load profile email message format NOTE: Outlook will sometimes strip
     * extra line breaks of its own accord. For some reason putting extra spaces
     * in front of the line breaks seems to prevent this. Not sure about other
     * email clients.
     */

    final long MS_IN_A_DAY = 1000 * 60 * 60 * 24;


    public ModelAndView render(HttpServletRequest request,
            HttpServletResponse response) throws Exception {

        ModelAndView mav = new ModelAndView("profileWidget/render.jsp");

        LiteYukonUser user = ServletUtil.getYukonUser(request);

        int deviceId = WidgetParameterHelper.getRequiredIntParameter(request,
                                                                     "deviceId");
        String startDateStr = WidgetParameterHelper.getStringParameter(request,
                                                                       "pastProfile_start",
                                                                       "");
        String stopDateStr = WidgetParameterHelper.getStringParameter(request,
                                                                    "pastProfile_stop",
                                                                    "");

        
       
        // get lite device, set name
        LiteYukonPAObject device = paoDao.getLiteYukonPAO(deviceId);
        
        // get heavy device
        YukonPAObject yukonPaobject = (YukonPAObject)dbPersistentDao.retrieveDBPersistent(device);
        
        
        // get load profile
        DeviceLoadProfile deviceLoadProfile = ((MCTBase)yukonPaobject).getDeviceLoadProfile();
        
        // set profile intervals
        int loadProfileDemandRate = deviceLoadProfile.getLoadProfileDemandRate();
        int vaoltageDemandRate = deviceLoadProfile.getVoltageDmdRate();
        
        mav.addObject("chan1Interval", loadProfileDemandRate / 60);
        mav.addObject("chan4Interval", vaoltageDemandRate / 60);

        // set collection on flags
        boolean chan1CollectionOn = deviceLoadProfile.loadProfileIsOnForChannel(1);
        boolean chan4CollectionOn = deviceLoadProfile.loadProfileIsOnForChannel(4);
        
        mav.addObject("chan1CollectionOn", chan1CollectionOn);
        mav.addObject("chan4CollectionOn", chan4CollectionOn);
        
        // initialize past profile dates
        if (StringUtils.isBlank(startDateStr) && StringUtils.isBlank(stopDateStr)) {
            mav.addObject("startDateStr",
                          dateFormattingService.formatDate(DateUtils.addDays(new Date(),
                                                                             -5),
                                                           DateFormattingService.DateFormatEnum.DATE,
                                                           user));
            mav.addObject("stopDateStr",
                          dateFormattingService.formatDate(new Date(),
                                                           DateFormattingService.DateFormatEnum.DATE,
                                                           user));
        }

        
        
        
        List<Map<String, String>> pendingRequests = getPendingRequests(device, user);
        mav.addObject("pendingRequests", pendingRequests);

        return mav;
    }

    
    
    
    public ModelAndView cancelLoadProfile(HttpServletRequest request,
            HttpServletResponse response) throws Exception {


        LiteYukonUser user = ServletUtil.getYukonUser(request);
        
        int deviceId = WidgetParameterHelper.getRequiredIntParameter(request,
                                                                     "deviceId");
        int requestId = WidgetParameterHelper.getRequiredIntParameter(request,
                                                                      "requestId");
        
        LiteYukonPAObject device = paoDao.getLiteYukonPAO(deviceId);

        loadProfileService.removePendingLongLoadProfileRequest(device, requestId, user);
        List<Map<String, String>> pendingRequests = getPendingRequests(device, user);
        
        ModelAndView mav = render(request, response);
        mav.addObject("requestId", requestId);

        // reload pending request
        mav.addObject("pendingRequests", pendingRequests);
        
        return mav;

    }

    
    
    
    public ModelAndView initiateLoadProfile(HttpServletRequest request,
            HttpServletResponse response) throws Exception {

        ModelAndView mav = render(request, response);

        boolean success = false;
        String initiateMessage = "Unknown Error";

        String email = WidgetParameterHelper.getRequiredStringParameter(request,
        "email");
        int deviceId = WidgetParameterHelper.getRequiredIntParameter(request,
        "deviceId");
        int channel = WidgetParameterHelper.getRequiredIntParameter(request,
        "channel");
        String startDateStr = WidgetParameterHelper.getStringParameter(request,
                                                                       "startDateStr",
        "");
        String stopDateStr = WidgetParameterHelper.getStringParameter(request,
                                                                      "stopDateStr",
        "");
        try {

            LiteYukonUser user = ServletUtil.getYukonUser(request);


            mav.addObject("startDateStr", startDateStr);
            mav.addObject("stopDateStr", stopDateStr);

            Date startDate = dateFormattingService.flexibleDateParser(startDateStr,
                                                                      DateFormattingService.DateOnlyMode.START_OF_DAY,
                                                                      user);

            Date stopDate = dateFormattingService.flexibleDateParser(stopDateStr,
                                                                     DateFormattingService.DateOnlyMode.END_OF_DAY,
                                                                     user);

            boolean datesOk = false;

            if (startDate == null) {
                success = false;
                datesOk = false;
                initiateMessage = "Start Date Required";
            } else if (stopDate == null) {
                success = false;
                datesOk = false;
                initiateMessage = "Stop Date Required";
            } else {

                String todayStr = dateFormattingService.formatDate(new Date(),
                                                                   DateFormattingService.DateFormatEnum.DATE,
                                                                   user);
                Date today = dateFormattingService.flexibleDateParser(todayStr,
                                                                      DateFormattingService.DateOnlyMode.END_OF_DAY,
                                                                      user);

                if (startDate.after(stopDate)) {
                    success = false;
                    datesOk = false;
                    initiateMessage = "Start Date Must Be Before Stop Date";
                } else if (stopDate.after(today)) {
                    success = false;
                    datesOk = false;
                    initiateMessage = "Stop Date Must Be On Or Before Today";
                } else {
                    datesOk = true;
                }

            }

            if (datesOk) {

                LiteYukonPAObject device = paoDao.getLiteYukonPAO(deviceId);
                LiteDeviceMeterNumber meterNum = deviceDao.getLiteDeviceMeterNumber(deviceId);

                // map of email elements
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

                // completion callbacks
                LongLoadProfileServiceEmailCompletionCallbackImpl callback = 
                    new LongLoadProfileServiceEmailCompletionCallbackImpl(emailService, dateFormattingService, deviceErrorTranslatorDao);
                
                callback.setSuccessMessage(msgData);
                callback.setFailureMessage(msgData);
                callback.setCancelMessage(msgData);

                loadProfileService.initiateLongLoadProfile(device,
                                                           channel,
                                                           startDate,
                                                           stopDate,
                                                           callback);

                success = true;
                initiateMessage = "";
                mav.addObject("channel", channel);
                
                // reload pending request
                List<Map<String, String>> pendingRequests = getPendingRequests(device, user);
                mav.addObject("pendingRequests", pendingRequests);
            }

        } catch (ParseException e) {
            success = false;
            initiateMessage = "Unable to parse: " + e.getMessage();
        }

        mav.addObject("success", success);
        mav.addObject("initiateMessage", initiateMessage);
        
        return mav;
    }

    public ModelAndView toggleProfiling(HttpServletRequest request,
            HttpServletResponse response) throws Exception {

        ModelAndView mav = render(request, response);

        // get toggle to channel, value
        String toggleChannel1ProfilingOn = WidgetParameterHelper.getStringParameter(request,
                                                                                    "toggleChannel1ProfilingOn", null);
        String toggleChannel4ProfilingOn = WidgetParameterHelper.getStringParameter(request,
                                                                                    "toggleChannel4ProfilingOn", null);
        
        // get device
        int deviceId = WidgetParameterHelper.getRequiredIntParameter(request, "deviceId");
        
        LiteYukonPAObject device = paoDao.getLiteYukonPAO(deviceId);
        YukonPAObject yukonPaobject = (YukonPAObject)dbPersistentDao.retrieveDBPersistent(device);
        
        // get load profile collection of device
        DeviceLoadProfile deviceLoadProfile = ((MCTBase)yukonPaobject).getDeviceLoadProfile();
        
        
        // only toggle to channel whose button was pushed
        if(toggleChannel1ProfilingOn != null){
            deviceLoadProfile.setLoadProfileIsOnForChannel(1, (new Boolean(toggleChannel1ProfilingOn)).booleanValue());
        }

        if(toggleChannel4ProfilingOn != null){
            deviceLoadProfile.setLoadProfileIsOnForChannel(4, (new Boolean(toggleChannel4ProfilingOn)).booleanValue());
        }
        
        // persist change
        dbPersistentDao.performDBChange(yukonPaobject, DBChangeMsg.CHANGE_TYPE_UPDATE);
        
        // re-set collection on flags
        boolean chan1CollectionOn = deviceLoadProfile.loadProfileIsOnForChannel(1);
        boolean chan4CollectionOn = deviceLoadProfile.loadProfileIsOnForChannel(4);
        
        mav.addObject("chan1CollectionOn", chan1CollectionOn);
        mav.addObject("chan4CollectionOn", chan4CollectionOn);
        
        return mav;
     
    }
    
    
    private List<Map<String, String>> getPendingRequests(LiteYukonPAObject device,  LiteYukonUser user){
    
        //  pending past profiles
        List<Map<String, String>> pendingRequests = new ArrayList<Map<String, String>>();
        Collection<ProfileRequestInfo> loadProfileRequests = loadProfileService.getPendingLongLoadProfileRequests(device);
        for (ProfileRequestInfo info : loadProfileRequests) {
            HashMap<String, String> data = new HashMap<String, String>();
                
            data.put("email", info.runner.toString());
            data.put("from",
                     dateFormattingService.formatDate(info.from,
                                                      DateFormattingService.DateFormatEnum.DATE,
                                                      user));
            data.put("to",
                     dateFormattingService.formatDate(DateUtils.addDays(info.to,
                                                                        -1),
                                                                        DateFormattingService.DateFormatEnum.DATE,
                                                                        user));
            data.put("command", info.request.getCommandString());
            data.put("requestId", Long.toString(info.request.getUserMessageID()));
            data.put("channel", ((Integer) info.channel).toString());
            pendingRequests.add(data);
        }

        // loadProfileService.printSizeOfCollections(device.getLiteID());
        
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
    public void setDbPersistentDao(DBPersistentDao dbPersistentDao) {
        this.dbPersistentDao = dbPersistentDao;
    }

    @Required
    public DeviceDao getDeviceDao() {
        return deviceDao;
    }

    @Required
    public void setMeterDao(MeterDao meterDao) {
        this.meterDao = meterDao;
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
    public void setDeviceErrorTranslatorDao(
            DeviceErrorTranslatorDao deviceErrorTranslatorDao) {
        this.deviceErrorTranslatorDao = deviceErrorTranslatorDao;
    }
}
