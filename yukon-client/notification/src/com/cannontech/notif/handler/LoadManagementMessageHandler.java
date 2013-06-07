package com.cannontech.notif.handler;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

import com.cannontech.common.util.Iso8601DateUtil;
import com.cannontech.core.dao.*;
import com.cannontech.database.data.lite.*;
import com.cannontech.database.data.notification.NotifType;
import com.cannontech.messaging.message.BaseMessage;
import com.cannontech.messaging.message.notif.ControlMessage;
import com.cannontech.notif.outputs.*;
import com.cannontech.notif.server.NotifServerConnection;
import com.cannontech.spring.YukonSpringHook;

/**
 * 
 */
public class LoadManagementMessageHandler extends NotifHandler implements MessageHandler<ControlMessage> {
    private static final DateFormat _dateFormatter = new SimpleDateFormat("EEEE, MMMM d"); // e.g. "Tuesday, May 31"
    private static final DateFormat _timeFormatter = new SimpleDateFormat("h:mm a"); // e.g. "3:45 PM"

    @Override
    public Class<ControlMessage> getSupportedMessageType() {
        return ControlMessage.class;
    }
    
    @Override
    public void handleMessage(NotifServerConnection connection,  BaseMessage msg_) {
        final ControlMessage msg = (ControlMessage) msg_;
        
        long durationMillis = msg.getStopTime().getTime() - msg.getStartTime().getTime();
        long durationMinutes = durationMillis / 1000 / 60;
        long durationHours = durationMinutes / 60;
        long remainingMinutes = durationMinutes % 60;
        final String durationMinutesStr = Long.toString(durationMinutes);
        final String durationHoursStr = Long.toString(durationHours);
        final String remainingMinutesStr = Long.toString(remainingMinutes);
        
        final boolean openEnded;
        // check if duration is greater than 1 year
        final int hoursInYear = (24 * 365);
        if (durationHours > hoursInYear) {
            openEnded = true;
        } else {
            openEnded = false;
        }

        final String actionString;
        switch(msg.getNotifType()) {
        case ControlMessage.STARTING_CONTROL_NOTIFICATION:
            actionString = "starting";
            break;
        case ControlMessage.ADJUSTING_CONTROL_NOTIFICATION:
            actionString = "adjusting";
            break;
        case ControlMessage.FINISHING_CONTROL_NOTIFICATION:
            actionString = "finishing";
            break;
        default:
            actionString = "unknown";
        }
       NotificationBuilder notifFormatter = new NotificationBuilder() {
            public Notification buildNotification(Contactable contact) {
                Notification notif = new Notification("loadmanagement");
                
                LiteYukonPAObject liteYukonPAO = YukonSpringHook.getBean(PaoDao.class).getLiteYukonPAO(msg.getProgramId());
                notif.addData("programname", liteYukonPAO.getPaoName());
                notif.addData("customername", contact.getCustomerName());

                TimeZone timeZone = contact.getTimeZone();
                
                synchronized (_dateFormatter) {
                    _timeFormatter.setTimeZone(timeZone);
                    _dateFormatter.setTimeZone(timeZone);
                    
                    notif.addData("timezone", timeZone.getDisplayName());
                    
                    notif.addData("starttime", _timeFormatter.format(msg.getStartTime()));
                    notif.addData("startdate", _dateFormatter.format(msg.getStartTime()));
                    notif.addData("startdatetime", Iso8601DateUtil.formatIso8601Date(msg.getStartTime(), timeZone));
                    notif.addData("stoptime", _timeFormatter.format(msg.getStopTime()));
                    notif.addData("stopdate", _dateFormatter.format(msg.getStopTime()));
                    notif.addData("stopdatetime", Iso8601DateUtil.formatIso8601Date(msg.getStopTime(), timeZone));
                }
                
                notif.addData("durationminutes", durationMinutesStr);
                notif.addData("durationhours", durationHoursStr);
                notif.addData("remainingminutes", remainingMinutesStr);
                
                notif.addData("openended", openEnded ? "yes" : "no");
                
                notif.addData("action", actionString);


                return notif;
            }
            
            public void notificationComplete(Contactable contactable, NotifType notifType, boolean success) {
                logNotificationStatus("LM NOTIF STATUS", success, contactable, notifType, this);
            }
            
            public void logIndividualNotification(LiteContactNotification destination, Contactable contactable, 
                    NotifType notifType, boolean success) {
                logNotificationActivity("LM NOTIF", success, destination, contactable, notifType, this);
            }
            public String toString() {
                return "LoadManagement " + actionString + " Notification";
            }
        };

        for(int i = 0; i < msg.getNotifGroupIds().length; i++) {
            LiteNotificationGroup notificationGroup = 
                YukonSpringHook.getBean(NotificationGroupDao.class).getLiteNotificationGroup(msg.getNotifGroupIds()[i]);
            
            outputNotification(notifFormatter, notificationGroup);
        }
    }

}
