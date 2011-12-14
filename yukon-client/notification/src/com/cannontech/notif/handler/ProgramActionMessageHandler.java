package com.cannontech.notif.handler;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.cc.dao.ProgramDao;
import com.cannontech.cc.dao.ProgramNotificationGroupDao;
import com.cannontech.cc.model.Program;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.util.Iso8601DateUtil;
import com.cannontech.core.dao.CustomerDao;
import com.cannontech.database.data.lite.*;
import com.cannontech.database.data.notification.NotifType;
import com.cannontech.message.notif.ProgramActionMsg;
import com.cannontech.message.util.Message;
import com.cannontech.notif.outputs.*;
import com.cannontech.notif.server.NotifServerConnection;

/**
 * 
 */
public class ProgramActionMessageHandler extends NotifHandler implements MessageHandler<ProgramActionMsg> {
    
    private static final Logger log = YukonLogManager.getLogger(NotifEmailMessageHandler.class);
    
    private @Autowired ProgramNotificationGroupDao programNotificationGroupDao;
    private @Autowired ProgramDao programDao;
    private @Autowired CustomerDao customerDao;

    private static final DateFormat _dateFormatter = new SimpleDateFormat("EEEE, MMMM d"); // e.g. "Tuesday, May 31"
    private static final DateFormat _timeFormatter = new SimpleDateFormat("h:mm a"); // e.g. "3:45 PM"

    @Override
    public Class<ProgramActionMsg> getSupportedMessageType() {
        return ProgramActionMsg.class;
    }

    @Override
    public void handleMessage(NotifServerConnection connection,  Message message) {
        final ProgramActionMsg msg = (ProgramActionMsg) message;
        
        log.info("Got into the ProgramActionMessageHandler");
        
        long durationMillis = msg.stopTime.getTime() - msg.startTime.getTime();
        long durationMinutes = durationMillis / 1000 / 60;
        long durationHours = durationMinutes / 60;
        long remainingMinutes = durationMinutes % 60;
        final String durationMinutesStr = Long.toString(durationMinutes);
        final String durationHoursStr = Long.toString(durationHours);
        final String remainingMinutesStr = Long.toString(remainingMinutes);
        
        final Program program = programDao.getForId(msg.programId);
        
        final List<String> customerNames = new ArrayList<String>();
        for (int i = 0; i < msg.customerIds.length; i++) {
            int customerId = msg.customerIds[i];
            LiteCICustomer liteCICustomer = customerDao.getLiteCICustomer(customerId);
            customerNames.add(liteCICustomer.getCompanyName());
        }
        final String customersString = StringUtils.join(customerNames.iterator(), ", ");
        
       NotificationBuilder notifFormatter = new NotificationBuilder() {
            public Notification buildNotification(Contactable contact) {
                Notification notif = new Notification("programaction");
                
                notif.addData("programname", program.getName());
                notif.addData("programtype", program.getProgramType().getName());
                notif.addData("eventname", msg.eventDisplayName);

                TimeZone timeZone = contact.getTimeZone();
                
                synchronized (_dateFormatter) {
                    _timeFormatter.setTimeZone(timeZone);
                    _dateFormatter.setTimeZone(timeZone);
                    
                    notif.addData("timezone", timeZone.getDisplayName());
                    
                    notif.addData("starttime", _timeFormatter.format(msg.startTime));
                    notif.addData("startdate", _dateFormatter.format(msg.startTime));
                    notif.addData("startdatetime", Iso8601DateUtil.formatIso8601Date(msg.startTime, timeZone));
                    notif.addData("stoptime", _timeFormatter.format(msg.stopTime));
                    notif.addData("stopdate", _dateFormatter.format(msg.stopTime));
                    notif.addData("stopdatetime", Iso8601DateUtil.formatIso8601Date(msg.stopTime, timeZone));
                    notif.addData("notiftime", _timeFormatter.format(msg.notificationTime));
                    notif.addData("notifdate", _dateFormatter.format(msg.notificationTime));
                    notif.addData("notifdatetime", Iso8601DateUtil.formatIso8601Date(msg.notificationTime, timeZone));
                }
                
                notif.addData("durationminutes", durationMinutesStr);
                notif.addData("durationhours", durationHoursStr);
                notif.addData("remainingminutes", remainingMinutesStr);
                
                notif.addData("action", msg.action);

                notif.addData("customers", customersString);
                notif.addCollection("customerlist", customerNames);

                return notif;
            }
            public void notificationComplete(Contactable contactable, NotifType notifType, boolean success) {
                logNotificationStatus("PA NOTIF STATUS", success, contactable, notifType, this);
            }
            
            public void logIndividualNotification(LiteContactNotification destination, Contactable contactable, 
                    NotifType notifType, boolean success) {
                logNotificationActivity("PA NOTIF", success, destination, contactable, notifType, this);
            }
            public String toString() {
                return "Commercial Curtailment action Notification";
            }
        };
        Set<LiteNotificationGroup> notificationGroupsForProgram = 
            programNotificationGroupDao.getNotificationGroupsForProgram(program);
        
        for(LiteNotificationGroup lng : notificationGroupsForProgram) {
            outputNotification(notifFormatter, lng);
        }
    }
}
