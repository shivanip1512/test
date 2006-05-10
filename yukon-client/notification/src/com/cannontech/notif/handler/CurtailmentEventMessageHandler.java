package com.cannontech.notif.handler;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import com.cannontech.cc.dao.*;
import com.cannontech.cc.model.*;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.database.cache.functions.CustomerFuncs;
import com.cannontech.database.data.lite.LiteCICustomer;
import com.cannontech.enums.NotificationReason;
import com.cannontech.enums.NotificationState;
import com.cannontech.message.notif.CurtailmentEventDeleteMsg;
import com.cannontech.message.notif.CurtailmentEventMsg;
import com.cannontech.message.server.ServerRequestMsg;
import com.cannontech.message.server.ServerResponseMsg;
import com.cannontech.message.util.*;
import com.cannontech.notif.outputs.*;
import com.cannontech.notif.server.NotifServerConnection;

public class CurtailmentEventMessageHandler extends MessageHandler {
    //private final OutputHandlerHelper _helper;
    private final OutputHandlerHelper _helper;
    private CurtailmentEventDao curtailmentEventDao;
    private CurtailmentEventParticipantDao curtailmentEventParticipantDao;
    private CurtailmentEventNotifDao curtailmentEventNotifDao;
    
    private Timer timer = new Timer("Notification Schedular", true);
    
    private static final DateFormat _dateFormatter = new SimpleDateFormat("EEEE, MMMM d"); // e.g. "Tuesday, May 31"
    private static final DateFormat _timeFormatter = new SimpleDateFormat("h:mm a"); // e.g. "3:45 PM"
    
    private class DoScheduledTask extends TimerTask {
        @Override
        public void run() {
            doScheduledNotifs();
        }
    };

    public CurtailmentEventMessageHandler(OutputHandlerHelper helper) {
        _helper = helper;
    }

    public boolean handleMessage(NotifServerConnection connection, Message msg_) {
        if (msg_ instanceof CurtailmentEventMsg) {
            CurtailmentEventMsg msg = (CurtailmentEventMsg) msg_;
            handleCurtailmentMessage(msg);
            return true;
        } else if (ServerRequestHelper.isPayloadInstanceOf(msg_, CurtailmentEventDeleteMsg.class)) {
            ServerRequestMsg reqMsg = (ServerRequestMsg) msg_;
            CurtailmentEventDeleteMsg reqPayload = (CurtailmentEventDeleteMsg) reqMsg.getPayload();
            Integer curtailmentEventId = reqPayload.curtailmentEventId;
            CurtailmentEvent event = curtailmentEventDao.getForId(curtailmentEventId);
            boolean success = deleteEventNotification(event);
            CollectableBoolean respPayload = new CollectableBoolean(success);
            ServerResponseMsg responseMsg = reqMsg.createResponseMsg();
            responseMsg.setPayload(respPayload);
            responseMsg.setStatus(ServerResponseMsg.STATUS_OK);
            connection.write(responseMsg);
            return true;
        } else {
            return false;
        }
    }
    
    public boolean deleteEventNotification(CurtailmentEvent event) {
        boolean startCancelled = attemptDeleteNotification(event, NotificationReason.STARTING);
        boolean stopCancelled = false;
        if (startCancelled) {
            stopCancelled = attemptDeleteNotification(event, NotificationReason.STOPPING);
        }
        boolean success = startCancelled && stopCancelled;
        if (!success) {
            CTILogger.error("Potential error while deleting curtailment notifications (startCancelled="
                            + startCancelled + ", stopCancelled=" + stopCancelled + ")");
        }
        return success;
    }


    private void handleCurtailmentMessage(CurtailmentEventMsg msg) {
        // should I be talking to the services layer instead???
        final CurtailmentEvent event = curtailmentEventDao.getForId(msg.curtailmentEventId);
        List<CurtailmentEventParticipant> participants = curtailmentEventParticipantDao.getForEvent(event);
        
        if (msg.action == CurtailmentEventMsg.STARTING) {
            createNotification(participants,
                               NotificationReason.STARTING,
                               event.getNotificationTime());
    
            createNotification(participants,
                               NotificationReason.STOPPING,
                               event.getStopTime()); 
        } else if (msg.action == CurtailmentEventMsg.CANCELLING) {
            Date now = new Date();
            createNotification(participants, NotificationReason.CANCELING, now);
        }

    }
    
    private void createNotification(List<CurtailmentEventParticipant> customers,
                                    NotificationReason reason,
                                    Date notifTime) {
        
        for (CurtailmentEventParticipant particip : customers) {
            for (Integer type : particip.getNotifMap()) {
                CurtailmentEventNotif notif = new CurtailmentEventNotif();
                notif.setNotificationTime(notifTime);
                notif.setNotifTypeId(type);
                notif.setParticipant(particip);
                notif.setReason(reason);
                notif.setState(NotificationState.SCHEDULED);
                curtailmentEventNotifDao.save(notif);
            }
        }
        timer.schedule(new DoScheduledTask(), notifTime);
    }
    
    private void doScheduledNotifs() {
        List<CurtailmentEventNotif> notifsPending;
        synchronized (this) {
            //TODO we might want to look for any notifs in the pending state 
            // and set them to some error state
            notifsPending = curtailmentEventNotifDao.getScheduledNotifs();
            for (CurtailmentEventNotif notif : notifsPending) {
                notif.setState(NotificationState.PENDING);
                curtailmentEventNotifDao.save(notif);
            }
        }
        
        for (CurtailmentEventNotif notif : notifsPending) {
            Integer customerId = notif.getParticipant().getCustomer().getId();
            LiteCICustomer liteCICustomer = CustomerFuncs.getLiteCICustomer(customerId);
            ContactableCustomer cc = new ContactableCustomer(liteCICustomer);
            Contactable contactable = new SingleNotifContactable(cc, notif.getNotifTypeId());

            NotificationBuilder builder = createBuilder(notif);
            _helper.handleNotification(builder, Collections.singletonList(contactable));
        }
    }

    private NotificationBuilder createBuilder(final CurtailmentEventNotif eventNotif) {
        final CurtailmentEvent event = eventNotif.getParticipant().getEvent();
        NotificationReason reason = eventNotif.getReason();
        
        final Calendar stopTimeCal = Calendar.getInstance();
        stopTimeCal.setTime(event.getStartTime());
        stopTimeCal.add(Calendar.MINUTE, event.getDuration());
        final Date stopTime = stopTimeCal.getTime();
        
        final long durationHours = event.getDuration() / 60;
        final long remainingMinutes = event.getDuration() % 60;
        final String durationMinutesStr = Long.toString(event.getDuration());
        final String durationHoursStr = Long.toString(durationHours);
        final String remainingMinutesStr = Long.toString(remainingMinutes);

        final Notification notif = new Notification("curtailment");
        notif.addData("programname", event.getProgram().getName());
        notif.addData("programtype", event.getProgram().getProgramType().getName());
        notif.addData("action", reason.toString());
        notif.addData("message", event.getMessage());
        notif.addData("durationminutes", durationMinutesStr);
        notif.addData("durationhours", durationHoursStr);
        notif.addData("remainingminutes", remainingMinutesStr);
        
        final NotificationBuilder notifBuilder = new NotificationBuilder() {

            public Notification buildNotification(Contactable contact) {
                TimeZone timeZone = contact.getTimeZone();
                synchronized (_timeFormatter) {
                    // we will use _dateFormatter as if we'd explicitly synched on it too
                    _timeFormatter.setTimeZone(timeZone);
                    _dateFormatter.setTimeZone(timeZone);
                    
                    notif.addData("timezone", timeZone.getDisplayName());
                    notif.addData("starttime", _timeFormatter.format(event.getStartTime()));
                    notif.addData("startdate", _dateFormatter.format(event.getStartTime()));
                    notif.addData("stoptime", _timeFormatter.format(stopTime));
                    notif.addData("stopdate", _dateFormatter.format(stopTime));
                    return notif;
                }
            }

            public void notificationComplete(Contactable contact, int notifType, boolean success) {
                // set status on eventNotif
                // probably shouldn't directly manipulate
                eventNotif.setState(success ? NotificationState.SUCCEEDED : NotificationState.FAILED);
                eventNotif.setNotificationTime(new Date());
                curtailmentEventNotifDao.save(eventNotif);
            }
        };
        return notifBuilder;
    }
    
    /**
     * Deletes all pending notifs for an event.
     * @param msg
     * @return true if all notifs were prevented from running
     */
    private synchronized boolean attemptDeleteNotification(CurtailmentEvent event, 
                                                           NotificationReason reason) {
       // this should be serialized with any other method that updates the state
       // this should be optimized within the dao
       List<CurtailmentEventNotif> forEvent = curtailmentEventNotifDao.getForEventAndReason(event, reason);
       boolean missedOne = false;
       //TODO this loop should be changed to check if it could delete all and the
       // go through and delete, it probably doesn't make a big difference, though
       for (CurtailmentEventNotif notif : forEvent) {
           if (notif.getState().equals(NotificationState.SCHEDULED)) {
               curtailmentEventNotifDao.delete(notif);
           } else {
               missedOne = true;
           }
       }
       return !missedOne;
    }

    public void setCurtailmentEventDao(CurtailmentEventDao curtailmentEventDao) {
        this.curtailmentEventDao = curtailmentEventDao;
    }

    public void setCurtailmentEventNotifDao(CurtailmentEventNotifDao curtailmentEventNotifDao) {
        this.curtailmentEventNotifDao = curtailmentEventNotifDao;
    }

    public CurtailmentEventParticipantDao getCurtailmentEventParticipantDao() {
        return curtailmentEventParticipantDao;
    }

    public void setCurtailmentEventParticipantDao(
                                                  CurtailmentEventParticipantDao curtailmentEventParticipantDao) {
        this.curtailmentEventParticipantDao = curtailmentEventParticipantDao;
    }
    
    
}
