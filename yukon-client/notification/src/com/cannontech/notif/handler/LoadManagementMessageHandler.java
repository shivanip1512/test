package com.cannontech.notif.handler;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import com.cannontech.database.cache.functions.NotificationGroupFuncs;
import com.cannontech.database.cache.functions.PAOFuncs;
import com.cannontech.database.data.lite.LiteNotificationGroup;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.message.notif.NotifLMControlMsg;
import com.cannontech.message.util.Message;
import com.cannontech.notif.outputs.*;
import com.cannontech.notif.server.NotifServerConnection;

/**
 * 
 */
public class LoadManagementMessageHandler extends NotifHandler {

    private static final DateFormat _dateFormater = new SimpleDateFormat("EEEE, MMMM d"); // e.g. "Tuesday, May 31"
    private static final DateFormat _timeFormater = new SimpleDateFormat("h:mm a"); // e.g. "3:45 PM"

    public LoadManagementMessageHandler(OutputHandlerHelper helper) {
        super(helper);
    }

    public boolean canHandle(Message msg) {
        return msg instanceof NotifLMControlMsg;
    }

    public void handleMessage(NotifServerConnection connection,  Message msg_) {
        final NotifLMControlMsg msg = (NotifLMControlMsg) msg_;
        
        long durationMillis = msg.stopTime.getTime() - msg.startTime.getTime();
        long durationMinutes = durationMillis / 1000 / 60;
        long durationHours = durationMinutes / 60;
        long remainingMinutes = durationMinutes % 60;
        final String durationMinutesStr = Long.toString(durationMinutes);
        final String durationHoursStr = Long.toString(durationHours);
        final String remainingMinutesStr = Long.toString(remainingMinutes);

        NotificationBuilder notifFormatter = new NotificationBuilder() {
            public Notification buildNotification(Contactable contact) {
                Notification notif = new Notification("loadmanagement");
                
                LiteYukonPAObject liteYukonPAO = PAOFuncs.getLiteYukonPAO(msg.programId);
                notif.addAttribute("programname", liteYukonPAO.getPaoName());

                _timeFormater.setTimeZone(contact.getTimeZone());
                _dateFormater.setTimeZone(contact.getTimeZone());
                
                notif.addData("starttime", _timeFormater.format(msg.startTime));
                notif.addData("startdate", _dateFormater.format(msg.startTime));
                notif.addData("stoptime", _timeFormater.format(msg.stopTime));
                notif.addData("stopdate", _dateFormater.format(msg.stopTime));
                
                notif.addData("durationminutes", durationMinutesStr);
                notif.addData("durationhours", durationHoursStr);
                notif.addData("remainingminutes", remainingMinutesStr);

                return notif;
            }
        };

        for(int i = 0; i < msg.notifGroupIds.length; i++) {
            LiteNotificationGroup notificationGroup = 
                NotificationGroupFuncs.getLiteNotificationGroup(msg.notifGroupIds[i]);
            
            outputNotification(notifFormatter, notificationGroup);
        }
    }

}
