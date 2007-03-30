package com.cannontech.notif.handler;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import org.apache.commons.lang.BooleanUtils;
import org.apache.log4j.Logger;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.clientutils.tags.AlarmUtils;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.database.data.lite.*;
import com.cannontech.database.data.notification.NotifType;
import com.cannontech.database.data.point.PointTypes;
import com.cannontech.message.notif.NotifAlarmMsg;
import com.cannontech.message.util.Message;
import com.cannontech.notif.outputs.*;
import com.cannontech.notif.server.NotifServerConnection;

public class AlarmMessageHandler extends NotifHandler {
    private Logger log = YukonLogManager.getLogger(AlarmMessageHandler.class);
    
    public AlarmMessageHandler(OutputHandlerHelper helper) {
        super(helper);
    }

    public boolean handleMessage(NotifServerConnection connection,  Message msg_) {
        if (!(msg_ instanceof NotifAlarmMsg)) {
            return false;
        }
        NotifAlarmMsg msg = (NotifAlarmMsg) msg_;
        

        for (int i = 0; i < msg.notifGroupIds.length; i++) {
            int notifGroupId = msg.notifGroupIds[i];
            LiteNotificationGroup liteNotifGroup = DaoFactory.getNotificationGroupDao().getLiteNotificationGroup(notifGroupId);
            if (liteNotifGroup.isDisabled()) {
                log.warn("Ignoring notification request because notification group is disabled: group=" + liteNotifGroup);
                continue;
            }
            
            NotificationBuilder notifFormatter = createNotificationBuilder(msg, liteNotifGroup);
            outputNotification(notifFormatter, liteNotifGroup);
        }
        return true;
    }

    private NotificationBuilder createNotificationBuilder(NotifAlarmMsg msg, 
                                                          LiteNotificationGroup liteNotifGroup) {
        final Notification notif = new Notification("alarm");
        
        notif.addData("notificationgroup", liteNotifGroup.getNotificationGroupName());
        
        LitePoint point = (LitePoint)DaoFactory.getPointDao().getLitePoint(msg.pointId);
        notif.addData("pointid", Integer.toString(point.getPointID()));
        notif.addData("pointname", point.getPointName());
        notif.addData("rawvalue", Double.toString(msg.value));
        notif.addData("pointtype", PointTypes.getType(point.getPointType()));
        int pAObjectId = point.getPaobjectID();
        PaoDao paoDao = DaoFactory.getPaoDao();
        LiteYukonPAObject liteYukonPAO = paoDao.getLiteYukonPAO(pAObjectId);
        notif.addData("paoname", liteYukonPAO.getPaoName());
        notif.addData("paodescription", liteYukonPAO.getPaoDescription());
        
        notif.addData("abnormal", BooleanUtils.toStringTrueFalse(msg.abnormal));
        notif.addData("acknowledged", BooleanUtils.toStringTrueFalse(msg.acknowledged));
        
        
        String conditionText = AlarmUtils.getAlarmConditionText(msg.condition, point);
        notif.addData("condition", conditionText);
        
        String categoryText = DaoFactory.getAlarmCatDao().getAlarmCategoryName(msg.alarmCategoryId);
        notif.addData("category", categoryText);
        
        DateFormat dateFormatter = new SimpleDateFormat("EEEE, MMMM d"); // e.g. "Tuesday, May 31"
        DateFormat timeFormatter = new SimpleDateFormat("h:mm a"); // e.g. "3:45 PM"
        
        notif.addData("alarmtime", timeFormatter.format(msg.alarmTimestamp));
        notif.addData("alarmdate", dateFormatter.format(msg.alarmTimestamp));
        
        String uofm = "";
        if (point.getPointType() == PointTypes.STATUS_POINT) {
            // handle as status
            LiteState liteState = DaoFactory.getStateDao().getLiteState(point.getStateGroupID(), (int)msg.value);
            notif.addData("value", liteState.getStateText());
        } else {
            // handle as analog
            notif.addData("value", Double.toString(msg.value));
            LiteUnitMeasure liteUnitMeasureByPointID = DaoFactory.getUnitMeasureDao().getLiteUnitMeasureByPointID(msg.pointId);
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
        return notifFormatter;
    }

}
