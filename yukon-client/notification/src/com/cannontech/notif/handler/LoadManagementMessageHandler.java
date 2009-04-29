package com.cannontech.notif.handler;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

import org.apache.log4j.Logger;
import org.apache.log4j.helpers.ISO8601DateFormat;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.database.data.lite.*;
import com.cannontech.database.data.notification.NotifType;
import com.cannontech.message.notif.NotifLMControlMsg;
import com.cannontech.message.util.Message;
import com.cannontech.notif.outputs.*;
import com.cannontech.notif.server.NotifServerConnection;

/**
 * 
 */
public class LoadManagementMessageHandler extends NotifHandler {
    private Logger log = YukonLogManager.getLogger(LoadManagementMessageHandler.class);

    private static final DateFormat _dateFormatter = new SimpleDateFormat("EEEE, MMMM d"); // e.g. "Tuesday, May 31"
    private static final DateFormat _timeFormatter = new SimpleDateFormat("h:mm a"); // e.g. "3:45 PM"
    private static final ISO8601DateFormat _dateTimeFormatter = new ISO8601DateFormat();
    
    public LoadManagementMessageHandler(OutputHandlerHelper helper) {
        super(helper);
    }

    public boolean handleMessage(NotifServerConnection connection,  Message msg_) {
        if (!(msg_ instanceof NotifLMControlMsg)) {
            return false;
        }
        final NotifLMControlMsg msg = (NotifLMControlMsg) msg_;
        
        long durationMillis = msg.stopTime.getTime() - msg.startTime.getTime();
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
        switch(msg.notifType) {
        case NotifLMControlMsg.STARTING_CONTROL_NOTIFICATION:
            actionString = "starting";
            break;
        case NotifLMControlMsg.ADJUSTING_CONTROL_NOTIFICATION:
            actionString = "adjusting";
            break;
        case NotifLMControlMsg.FINISHING_CONTROL_NOTIFICATION:
            actionString = "finishing";
            break;
        default:
            actionString = "unknown";
        }
       NotificationBuilder notifFormatter = new NotificationBuilder() {
            public Notification buildNotification(Contactable contact) {
                Notification notif = new Notification("loadmanagement");
                
                LiteYukonPAObject liteYukonPAO = DaoFactory.getPaoDao().getLiteYukonPAO(msg.programId);
                notif.addData("programname", liteYukonPAO.getPaoName());
                notif.addData("customername", contact.getCustomerName());

                TimeZone timeZone = contact.getTimeZone();
                
                synchronized (_dateFormatter) {
                    _timeFormatter.setTimeZone(timeZone);
                    _dateFormatter.setTimeZone(timeZone);
                    _dateTimeFormatter.setTimeZone(timeZone);
                    
                    notif.addData("timezone", timeZone.getDisplayName());
                    
                    notif.addData("starttime", _timeFormatter.format(msg.startTime));
                    notif.addData("startdate", _dateFormatter.format(msg.startTime));
                    notif.addData("startdatetime", _dateTimeFormatter.format(msg.startTime));
                    notif.addData("stoptime", _timeFormatter.format(msg.stopTime));
                    notif.addData("stopdate", _dateFormatter.format(msg.stopTime));
                    notif.addData("stopdatetime", _dateTimeFormatter.format(msg.stopTime));
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

        for(int i = 0; i < msg.notifGroupIds.length; i++) {
            LiteNotificationGroup notificationGroup = 
                DaoFactory.getNotificationGroupDao().getLiteNotificationGroup(msg.notifGroupIds[i]);
            
            outputNotification(notifFormatter, notificationGroup);
        }
        return true;
    }

}
