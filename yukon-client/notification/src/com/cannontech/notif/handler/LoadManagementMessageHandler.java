package com.cannontech.notif.handler;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Iterator;

import com.cannontech.database.cache.functions.NotificationGroupFuncs;
import com.cannontech.database.cache.functions.PAOFuncs;
import com.cannontech.database.data.lite.LiteNotificationGroup;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.message.util.Message;
import com.cannontech.notif.message.NotifLMControlMsg;
import com.cannontech.notif.outputs.Notification;
import com.cannontech.notif.outputs.OutputHandlerHelper;

/**
 * 
 */
public class LoadManagementMessageHandler extends NotifHandler {

    private static final DateFormat _dateFormater = new SimpleDateFormat("EEEE, MMMM d"); // e.g. "Tuesday May 31"
    private static final DateFormat _timeFormater = new SimpleDateFormat("h:m a"); // e.g. "3:45 PM"

    public LoadManagementMessageHandler(OutputHandlerHelper helper) {
        super(helper);
    }

    public boolean canHandle(Message msg) {
        return msg instanceof NotifLMControlMsg;
    }

    public void handleMessage(Message msg_) {
        NotifLMControlMsg msg = (NotifLMControlMsg) msg_;

        // building the Notification object is the main work of
        // this function
        Notification notif = new Notification("loadmanagement");
        
        notif.put("starttime", _timeFormater.format(msg.startTime));
        notif.put("startdate", _dateFormater.format(msg.startTime));
        notif.put("stoptime", _timeFormater.format(msg.stopTime));
        notif.put("stopdate", _dateFormater.format(msg.stopTime));
        LiteYukonPAObject liteYukonPAO = PAOFuncs.getLiteYukonPAO(msg.programId);
        notif.put("programname", liteYukonPAO.getPaoName());

        for (Iterator iter = msg.notifGroupIds.iterator(); iter.hasNext();) {
            Integer notifGroupId = (Integer) iter.next();
            LiteNotificationGroup notificationGroup = 
                NotificationGroupFuncs.getLiteNotificationGroup(notifGroupId.intValue());
            
            outputNotification(notif, notificationGroup);
        }
    }

}
