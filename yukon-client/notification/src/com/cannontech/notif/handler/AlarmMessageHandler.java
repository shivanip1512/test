package com.cannontech.notif.handler;

import java.text.*;
import java.util.Date;
import java.util.TimeZone;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.springframework.beans.factory.annotation.Required;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.clientutils.tags.AlarmUtils;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dynamic.PointValueHolder;
import com.cannontech.core.service.PointFormattingService;
import com.cannontech.core.service.PointFormattingService.Format;
import com.cannontech.database.data.lite.*;
import com.cannontech.database.data.notification.NotifType;
import com.cannontech.database.data.point.PointTypes;
import com.cannontech.message.notif.NotifAlarmMsg;
import com.cannontech.message.util.Message;
import com.cannontech.notif.outputs.*;
import com.cannontech.notif.server.NotifServerConnection;
import com.cannontech.user.YukonUserContext;

public class AlarmMessageHandler extends NotifHandler {
    private Logger log = YukonLogManager.getLogger(AlarmMessageHandler.class);
    private PointFormattingService pointFormattingService;
    
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
            
            NotificationBuilder notifFormatter = createNotificationBuilder(msg, liteNotifGroup);
            outputNotification(notifFormatter, liteNotifGroup);
        }
        return true;
    }

    private NotificationBuilder createNotificationBuilder(NotifAlarmMsg _msg, 
                                                          LiteNotificationGroup _liteNotifGroup) {
        
        final NotifAlarmMsg msg = _msg;
        final LiteNotificationGroup liteNotifGroup = _liteNotifGroup;
        
        NotificationBuilder notifFormatter = new NotificationBuilder() {
            public Notification buildNotification(Contactable contact) {
                
                Notification notif = new Notification("alarm");
                
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
                
                AlarmPointValue alarmPointValue = new AlarmPointValue(msg, point);
                YukonUserContext userContext = contact.getYukonUserContext();
                
                String shortValue = pointFormattingService.getValueString(alarmPointValue, Format.SHORT, userContext);
                notif.addData("shortvalue", shortValue);
                String fullValue = pointFormattingService.getValueString(alarmPointValue, Format.FULL, userContext);
                notif.addData("fullvalue", fullValue);
                String dateValue = pointFormattingService.getValueString(alarmPointValue, Format.DATE, userContext);
                notif.addData("datevalue", dateValue);
                
                notif.addData("abnormal", BooleanUtils.toStringTrueFalse(msg.abnormal));
                notif.addData("acknowledged", BooleanUtils.toStringTrueFalse(msg.acknowledged));
                
                
                String conditionText = AlarmUtils.getAlarmConditionText(msg.condition, point);
                notif.addData("condition", conditionText);
                
                String categoryText = DaoFactory.getAlarmCatDao().getAlarmCategoryName(msg.alarmCategoryId);
                notif.addData("category", categoryText);
                
                DateFormat dateFormatter = new SimpleDateFormat("EEEE, MMMM d"); // e.g. "Tuesday, May 31"
                DateFormat timeFormatter = new SimpleDateFormat("h:mm:ss a z"); // e.g. "3:45:13 CDT"
                
                TimeZone timeZone = contact.getTimeZone();
                timeFormatter.setTimeZone(timeZone);
                dateFormatter.setTimeZone(timeZone);
                DateTimeFormatter dateTimeFormatter = ISODateTimeFormat.dateTimeNoMillis().withZone(DateTimeZone.forTimeZone(timeZone));
                
                notif.addData("alarmtime", timeFormatter.format(msg.alarmTimestamp));
                notif.addData("alarmdate", dateFormatter.format(msg.alarmTimestamp));
                notif.addData("alarmdatetime", dateTimeFormatter.print(msg.alarmTimestamp.getTime()));
                
                String uofm = "";
                if (point.getPointType() == PointTypes.STATUS_POINT) {
                    // handle as status
                    LiteState liteState = DaoFactory.getStateDao().getLiteState(point.getStateGroupID(), (int)msg.value);
                    notif.addData("value", liteState.getStateText());
                } else {
                    // handle as analog
                    NumberFormat numberInstance = NumberFormat.getNumberInstance();
                    numberInstance.setMaximumFractionDigits(8); // seems to cut off most crazy floats
                    notif.addData("value", numberInstance.format(msg.value));
                    LiteUnitMeasure liteUnitMeasureByPointID = DaoFactory.getUnitMeasureDao().getLiteUnitMeasureByPointID(msg.pointId);
                    if (liteUnitMeasureByPointID != null) {
                        uofm = liteUnitMeasureByPointID.getUnitMeasureName();
                    }
                }
                notif.addData("unitofmeasure", uofm);
                
                log.debug("build notification for: " + contact);
                return notif;
            }
            public void notificationComplete(Contactable contactable, NotifType notifType, boolean success) {
                log.debug("notification complete for " + contactable + ", type=" + notifType + ", success=" + success);
                logNotificationStatus("ALARM NOTIF STATUS", success, contactable, notifType, this);
            }
            
            public void logIndividualNotification(LiteContactNotification destination, Contactable contactable, 
                    NotifType notifType, boolean success) {
                log.debug("individual notification complete for " + destination + ", contactable=" + contactable + ", notifType=" + notifType + ", success=" + success);
                logNotificationActivity("ALARM NOTIF", success, destination, contactable, notifType, this);
            }
            
            public String toString() {
                return "Alarm Notification";
            }
        };
        return notifFormatter;
    }
    
    private class AlarmPointValue implements PointValueHolder {
        
        private final NotifAlarmMsg msg;
        private final LitePoint litePoint;

        public AlarmPointValue(NotifAlarmMsg msg, LitePoint litePoint) {
            Validate.isTrue(msg.pointId == litePoint.getLiteID());
            this.msg = msg;
            this.litePoint = litePoint;
        }

        @Override
        public int getId() {
            return litePoint.getLiteID();
        }

        @Override
        public Date getPointDataTimeStamp() {
            return msg.alarmTimestamp;
        }

        @Override
        public int getType() {
            return litePoint.getPointType();
        }

        @Override
        public double getValue() {
            return msg.value;
        }
        
    }
    
    @Required
    public void setPointFormattingService(PointFormattingService pointFormattingService) {
        this.pointFormattingService = pointFormattingService;
    }


}
