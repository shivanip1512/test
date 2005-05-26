package com.cannontech.notif.handler;

import com.cannontech.database.cache.functions.PointFuncs;
import com.cannontech.database.data.lite.*;
import com.cannontech.message.util.Message;
import com.cannontech.notif.message.NotifAlarmMsg;
import com.cannontech.notif.outputs.Notification;
import com.cannontech.notif.outputs.OutputHandlerHelper;

public class AlarmMessageHandler extends NotifHandler {

    public AlarmMessageHandler(OutputHandlerHelper helper) {
        super(helper);
    }

    public boolean canHandle(Message msg) {
        return false;
    }

    public void handleMessage(Message msg_) {
        NotifAlarmMsg msg = (NotifAlarmMsg) msg_;
        
        // building the Notification object is the main work of 
        // this function
        Notification notif = new Notification("alarm");
        
        LitePoint point = (LitePoint)PointFuncs.getLitePoint(msg.pointId);
        notif.put("pointname", point.getPointName());
        notif.put("value", Double.toString(msg.value));
        LitePointUnit uOfM = PointFuncs.getPointUnit(msg.pointId);
        notif.put("unitofmeasure", uOfM.toString()); //This won't work!
        
        // there will be more than one of these, so this will be a loop
        LiteNotificationGroup lng = null;
        outputNotification(notif, lng);
    }

}
