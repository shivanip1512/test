package com.cannontech.notif.handler;

import com.cannontech.database.cache.functions.*;
import com.cannontech.database.data.lite.*;
import com.cannontech.database.data.notification.NotifType;
import com.cannontech.database.data.point.PointTypes;
import com.cannontech.message.notif.NotifAlarmMsg;
import com.cannontech.message.util.Message;
import com.cannontech.notif.outputs.*;
import com.cannontech.notif.server.NotifServerConnection;

public class AlarmMessageHandler extends NotifHandler {
    
    public AlarmMessageHandler(OutputHandlerHelper helper) {
        super(helper);
    }

    public boolean handleMessage(NotifServerConnection connection,  Message msg_) {
        if (!(msg_ instanceof NotifAlarmMsg)) {
            return false;
        }
        NotifAlarmMsg msg = (NotifAlarmMsg) msg_;
        
        // building the Notification object is the main work of 
        // this function
        final Notification notif = new Notification("alarm");
        
        LitePoint point = (LitePoint)PointFuncs.getLitePoint(msg.pointId);
        notif.addData("pointid", Integer.toString(point.getPointID()));
        notif.addData("pointname", point.getPointName());
        notif.addData("rawvalue", Double.toString(msg.value));
        notif.addData("pointtype", PointTypes.getType(point.getPointType()));
        int pAObjectId = point.getPaobjectID();
        notif.addData("paoname", PAOFuncs.getYukonPAOName(pAObjectId));
        
        String uofm = "";
        if (point.getPointType() == PointTypes.STATUS_POINT) {
            // handle as status
            LiteState liteState = StateFuncs.getLiteState(point.getStateGroupID(), (int)msg.value);
            notif.addData("value", liteState.getStateText());
        } else {
            // handle as analog
            notif.addData("value", Double.toString(msg.value));
            LiteUnitMeasure liteUnitMeasureByPointID = UnitMeasureFuncs.getLiteUnitMeasureByPointID(msg.pointId);
            if (liteUnitMeasureByPointID != null) {
                uofm = liteUnitMeasureByPointID.getUnitMeasureName();
            }
        }
        notif.addData("unitofmeasure", uofm);
        
        NotificationBuilder notifFormatter = new NotificationBuilder() {
            public Notification buildNotification(Contactable contact) {
                return notif;
            }
            public void notificationComplete(Contactable contact, NotifType notifType, boolean success) {
                // do nothing
            }
        };
        
        for (int i = 0; i < msg.notifGroupIds.length; i++) {
            int notifGroupId = msg.notifGroupIds[i];
            LiteNotificationGroup liteNotifGroup = NotificationGroupFuncs.getLiteNotificationGroup(notifGroupId);
            outputNotification(notifFormatter, liteNotifGroup);
        }
        
        return true;
    }

}
