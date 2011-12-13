package com.cannontech.notif.handler;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.cc.dao.*;
import com.cannontech.cc.model.*;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.database.data.lite.LiteContactNotification;
import com.cannontech.database.data.notification.NotifType;
import com.cannontech.enums.NotificationReason;
import com.cannontech.enums.NotificationState;
import com.cannontech.message.notif.CurtailmentEventMsg;
import com.cannontech.notif.outputs.*;

public class CurtailmentEventScheduler extends EventScheduler {
    private @Autowired CurtailmentEventDao curtailmentEventDao;
    private @Autowired CurtailmentEventParticipantDao curtailmentEventParticipantDao;
    private @Autowired CurtailmentEventNotifDao curtailmentEventNotifDao;
        
    public boolean deleteEventNotification(CurtailmentEvent event, boolean deleteStart, boolean deleteStop) {
        boolean startCancelled = true;
        
        if (deleteStart) {
            startCancelled = attemptDeleteNotification(event, NotificationReason.STARTING);
        }
        
        boolean stopCancelled = true;
        
        if (deleteStop && startCancelled) {
            stopCancelled = attemptDeleteNotification(event, NotificationReason.STOPPING);
        }
        
        boolean success = startCancelled && stopCancelled;
        
        if (!success) {
            CTILogger.error("Potential error while deleting eonomic notifications (startCancelled="
                            + startCancelled + ", stopCancelled=" + stopCancelled + ")");
        }
        
        return success;
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
       //TODO this loop should be changed to check if it could delete all and then
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

    public void handleCurtailmentMessage(CurtailmentEventMsg msg) {
        // should I be talking to the services layer instead???
        final CurtailmentEvent event = curtailmentEventDao.getForId(msg.curtailmentEventId);
        List<CurtailmentEventParticipant> participants = curtailmentEventParticipantDao.getForEvent(event);
        
        Date now = new Date();
        switch(msg.action) {
        case STARTING:
            createNotification(participants,
                               NotificationReason.STARTING,
                               event.getNotificationTime());
            
            createNotification(participants,
                               NotificationReason.STOPPING,
                               event.getStopTime()); 
            break;
        case CANCELING:
            createNotification(participants, NotificationReason.CANCELING, now);
            break;
            
        case ADJUSTING:
            if (!attemptDeleteNotification(event, NotificationReason.STOPPING)) {
                CTILogger.warn("Stop message for initial event was not stopped on adjust: event=" + event);
            }
            createNotification(participants, NotificationReason.ADJUSTING, now);
            createNotification(participants, NotificationReason.STOPPING, event.getStopTime());
            break;
        }

    }

    
    private void createNotification(List<CurtailmentEventParticipant> customers,
                                    NotificationReason reason,
                                    Date notifTime) {
        
        for (CurtailmentEventParticipant particip : customers) {
            for (NotifType type : particip.getNotifMap()) {
                CurtailmentEventNotif notif = new CurtailmentEventNotif();
                notif.setNotificationTime(notifTime);
                notif.setNotifType(type);
                notif.setParticipant(particip);
                notif.setReason(reason);
                notif.setState(NotificationState.SCHEDULED);
                curtailmentEventNotifDao.save(notif);
            }
        }
        scheduleNotif(notifTime);
    }
    


    @Override
    protected void updateNotif(EventNotif notif) {
        curtailmentEventNotifDao.save((CurtailmentEventNotif) notif);
    }

    @Override
    protected List<? extends EventNotif> getScheduledNotifs() {
        return curtailmentEventNotifDao.getScheduledNotifs();
    }

    @Override
    protected NotificationBuilder createBuilder(EventNotif _eventNotif) {
        final CurtailmentEventNotif eventNotif = (CurtailmentEventNotif) _eventNotif;
        final CurtailmentEvent event = eventNotif.getParticipant().getEvent();
        
        
        final NotificationBuilder notifBuilder = new NotificationBuilder() {

            public Notification buildNotification(Contactable contact) {
                final Notification notif = new Notification("curtailment");
                
                fillInBaseAttribs(notif, event);
                NotificationReason reason = eventNotif.getReason();
                notif.addData("action", reason.toString());
                notif.addData("message", event.getMessage());
                TimeZone timeZone = contact.getTimeZone();
                fillInFormattedTimes(notif, event, timeZone);
                return notif;
            }

            public void notificationComplete(Contactable contact, NotifType notifType, boolean success) {
                // set status on eventNotif
                // probably shouldn't directly manipulate
                eventNotif.setState(success ? NotificationState.SUCCEEDED : NotificationState.FAILED);
                eventNotif.setNotificationTime(new Date());
                curtailmentEventNotifDao.save(eventNotif);

                NotifHandler.logNotificationStatus("CE NOTIF STATUS", success, contact, notifType, this);
            }
            
            public void logIndividualNotification(LiteContactNotification destination, Contactable contactable, NotifType notifType, boolean success) {
                NotifHandler.logNotificationActivity("CE NOTIF", success, destination, contactable, notifType, this);
            }
            
            @Override
            public String toString() {
                return "Curtailment Event " + event.getDisplayName() + " " + eventNotif.getReason() + " Notification";
            }
        };
        
        return notifBuilder;
    }
}
