package com.cannontech.notif.handler;

import com.cannontech.database.cache.functions.*;
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
        return msg instanceof NotifAlarmMsg;
    }

    public void handleMessage(NotifServerConnection connection,  Message msg_) {
        NotifAlarmMsg msg = (NotifAlarmMsg) msg_;
        
        // building the Notification object is the main work of 
        // this function
        final Notification notif = new Notification("alarm");
        
        LitePoint point = (LitePoint)PointFuncs.getLitePoint(msg.pointId);
        notif.addData("pointname", point.getPointName());
        notif.addData("value", Double.toString(msg.value));
        String uofmName = UnitMeasureFuncs.getLiteUnitMeasureByPointID(msg.pointId).getUnitMeasureName();
        notif.addData("unitofmeasure", uofmName);
        
        NotificationBuilder notifFormatter = new NotificationBuilder() {
            public Notification buildNotification(Contactable contact) {
                return notif;
            }
        };

        for (int i = 0; i < msg.notifGroupIds.length; i++) {
            int notifGroupId = msg.notifGroupIds[i];
            LiteNotificationGroup liteNotifGroup = NotificationGroupFuncs.getLiteNotificationGroup(notifGroupId);
            outputNotification(notifFormatter, liteNotifGroup);
        }
    }

}
