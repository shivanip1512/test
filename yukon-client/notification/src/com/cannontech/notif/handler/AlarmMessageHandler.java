package com.cannontech.notif.handler;

import com.cannontech.database.cache.functions.PointFuncs;
import com.cannontech.database.data.lite.*;
import com.cannontech.message.notif.NotifAlarmMsg;
import com.cannontech.message.util.Message;
import com.cannontech.notif.outputs.*;
import com.cannontech.notif.server.NotifServerConnection;

public class AlarmMessageHandler extends NotifHandler {

    public AlarmMessageHandler(OutputHandlerHelper helper) {
        super(helper);
    }

    public boolean canHandle(Message msg) {
        return false;
    }

    public void handleMessage(NotifServerConnection connection,  Message msg_) {
        NotifAlarmMsg msg = (NotifAlarmMsg) msg_;
        
        // building the Notification object is the main work of 
        // this function
        final Notification notif = new Notification("alarm");
        
        LitePoint point = (LitePoint)PointFuncs.getLitePoint(msg.pointId);
        notif.addData("pointname", point.getPointName());
        notif.addData("value", Double.toString(msg.value));
        LitePointUnit uOfM = PointFuncs.getPointUnit(msg.pointId);
        notif.addData("unitofmeasure", uOfM.toString()); //This won't work!
        
        NotificationBuilder notifFormatter = new NotificationBuilder() {
            public Notification buildNotification(Contactable contact) {
                return notif;
            }
        };

        // there will be more than one of these, so this will be a loop
        
        
        
        LiteNotificationGroup lng = null;
        outputNotification(notifFormatter, lng);
    }

}
