package com.cannontech.notif.handler;

import java.text.*;
import java.util.Date;
import java.util.TimeZone;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.meter.dao.MeterDao;
import com.cannontech.amr.meter.model.YukonMeter;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.clientutils.tags.AlarmUtils;
import com.cannontech.common.util.Iso8601DateUtil;
import com.cannontech.core.dao.*;
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
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.user.YukonUserContext;

public class AlarmMessageHandler extends NotifHandler implements MessageHandler<NotifAlarmMsg> {
    private static Logger log = YukonLogManager.getLogger(AlarmMessageHandler.class);
    
    private @Autowired PointFormattingService pointFormattingService;
    private @Autowired MeterDao meterDao;
    
    @Override
    public Class<NotifAlarmMsg> getSupportedMessageType() {
        return NotifAlarmMsg.class;
    }
    
    @Override
    public void handleMessage(NotifServerConnection connection,  Message message) {
        NotifAlarmMsg msg = (NotifAlarmMsg) message;

        for (int i = 0; i < msg.notifGroupIds.length; i++) {
            int notifGroupId = msg.notifGroupIds[i];
            LiteNotificationGroup liteNotifGroup = YukonSpringHook.getBean(NotificationGroupDao.class).getLiteNotificationGroup(notifGroupId);
            
            NotificationBuilder notifFormatter = createNotificationBuilder(msg, liteNotifGroup);
            outputNotification(notifFormatter, liteNotifGroup);
        }
    }

    private NotificationBuilder createNotificationBuilder(NotifAlarmMsg _msg, 
                                                          LiteNotificationGroup _liteNotifGroup) {
        
        final NotifAlarmMsg msg = _msg;
        final LiteNotificationGroup liteNotifGroup = _liteNotifGroup;
        
        NotificationBuilder notifFormatter = new NotificationBuilder() {
            @Override
            public Notification buildNotification(Contactable contact) {
                
                Notification notif = new Notification("alarm");
                
                notif.addData("notificationgroup", liteNotifGroup.getNotificationGroupName());
                
                LitePoint point = YukonSpringHook.getBean(PointDao.class).getLitePoint(msg.pointId);
                notif.addData("pointid", Integer.toString(point.getPointID()));
                notif.addData("pointname", point.getPointName());
                notif.addData("rawvalue", Double.toString(msg.value));
                notif.addData("pointtype", PointTypes.getType(point.getPointType()));
                int pAObjectId = point.getPaobjectID();
                PaoDao paoDao = YukonSpringHook.getBean(PaoDao.class);
                LiteYukonPAObject liteYukonPAO = paoDao.getLiteYukonPAO(pAObjectId);
                notif.addData("paoname", liteYukonPAO.getPaoName());
                notif.addData("paodescription", liteYukonPAO.getPaoDescription());
                
                String meterNumber = "";
                if (liteYukonPAO.getPaoType().isMeter()) {
                    try {
                        YukonMeter meter = meterDao.getForId(liteYukonPAO.getYukonID());
                        meterNumber = meter.getMeterNumber();
                    } catch (NotFoundException nfe) {
                        // ignore if not found, will use default empty string.
                        // alarmed device is not a meter
                        log.debug("Alarmed device is not of type meter, skipping meternumber and using empty string.");
                    }
                }
                notif.addData("meternumber", meterNumber);
                
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
                
                LiteAlarmCategory liteAlarmCategory = YukonSpringHook.getBean(AlarmCatDao.class).getAlarmCategory(msg.alarmCategoryId);
                String categoryText = (liteAlarmCategory == null ? null : liteAlarmCategory.getCategoryName());
                notif.addData("category", categoryText);
                
                DateFormat dateFormatter = new SimpleDateFormat("EEEE, MMMM d"); // e.g. "Tuesday, May 31"
                DateFormat timeFormatter = new SimpleDateFormat("h:mm:ss a z"); // e.g. "3:45:13 CDT"
                
                TimeZone timeZone = contact.getTimeZone();
                timeFormatter.setTimeZone(timeZone);
                dateFormatter.setTimeZone(timeZone);
                
                notif.addData("alarmtime", timeFormatter.format(msg.alarmTimestamp));
                notif.addData("alarmdate", dateFormatter.format(msg.alarmTimestamp));
                notif.addData("alarmdatetime", Iso8601DateUtil.formatIso8601Date(msg.alarmTimestamp, timeZone));
                
                String uofm = "";
                if (point.getPointTypeEnum().isStatus()) {
                    // handle as status
                    LiteState liteState = YukonSpringHook.getBean(StateGroupDao.class).findLiteState(point.getStateGroupID(), (int)msg.value);
                    notif.addData("value", liteState.getStateText());
                } else {
                    // handle as analog
                    NumberFormat numberInstance = NumberFormat.getNumberInstance();
                    numberInstance.setMaximumFractionDigits(8); // seems to cut off most crazy floats
                    notif.addData("value", numberInstance.format(msg.value));
                    LiteUnitMeasure liteUnitMeasureByPointID = YukonSpringHook.getBean(UnitMeasureDao.class).getLiteUnitMeasureByPointID(msg.pointId);
                    if (liteUnitMeasureByPointID != null) {
                        uofm = liteUnitMeasureByPointID.getUnitMeasureName();
                    }
                }
                notif.addData("unitofmeasure", uofm);
                
                log.debug("build notification for: " + contact);
                return notif;
            }
            @Override
            public void notificationComplete(Contactable contactable, NotifType notifType, boolean success) {
                log.debug("notification complete for " + contactable + ", type=" + notifType + ", success=" + success);
                logNotificationStatus("ALARM NOTIF STATUS", success, contactable, notifType, this);
            }
            
            @Override
            public void logIndividualNotification(LiteContactNotification destination, Contactable contactable, 
                    NotifType notifType, boolean success) {
                log.debug("individual notification complete for " + destination + ", contactable=" + contactable + ", notifType=" + notifType + ", success=" + success);
                logNotificationActivity("ALARM NOTIF", success, destination, contactable, notifType, this);
            }
            
            @Override
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
}
