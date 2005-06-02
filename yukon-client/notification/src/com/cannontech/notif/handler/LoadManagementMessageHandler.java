package com.cannontech.notif.handler;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import com.cannontech.database.cache.functions.NotificationGroupFuncs;
import com.cannontech.database.cache.functions.PAOFuncs;
import com.cannontech.database.data.lite.LiteNotificationGroup;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.message.util.Message;
import com.cannontech.notif.message.NotifLMControlMsg;
import com.cannontech.notif.outputs.*;

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

    public void handleMessage(Message msg_) {
        final NotifLMControlMsg msg = (NotifLMControlMsg) msg_;

        NotificationBuilder notifFormatter = new NotificationBuilder() {
            public Notification buildNotification(Contactable contact) {
                Notification notif = new Notification("loadmanagement");
                
                LiteYukonPAObject liteYukonPAO = PAOFuncs.getLiteYukonPAO(msg.programId);
                notif.put("programname", liteYukonPAO.getPaoName());

                _timeFormater.setTimeZone(contact.getTimeZone());
                _dateFormater.setTimeZone(contact.getTimeZone());
                
                notif.put("starttime", _timeFormater.format(msg.startTime));
                notif.put("startdate", _dateFormater.format(msg.startTime));
                notif.put("stoptime", _timeFormater.format(msg.stopTime));
                notif.put("stopdate", _dateFormater.format(msg.stopTime));
                
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
