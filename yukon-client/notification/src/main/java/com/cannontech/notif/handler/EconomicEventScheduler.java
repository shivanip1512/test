package com.cannontech.notif.handler;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.cc.dao.EconomicEventNotifDao;
import com.cannontech.cc.dao.EconomicEventParticipantDao;
import com.cannontech.cc.model.*;
import com.cannontech.cc.service.EconomicService;
import com.cannontech.cc.service.EconomicStrategy;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.database.data.lite.LiteContactNotification;
import com.cannontech.database.data.notification.NotifType;
import com.cannontech.cc.service.NotificationReason;
import com.cannontech.cc.service.NotificationState;
import com.cannontech.notif.outputs.*;

public class EconomicEventScheduler extends EventScheduler {
    private @Autowired EconomicEventNotifDao economicEventNotifDao;
    private @Autowired EconomicEventParticipantDao economicEventParticipantDao;
    private @Autowired EconomicService economicService;
    
    public void eventCreationNotification(EconomicEvent event) {
        List<EconomicEventParticipant> participants = economicEventParticipantDao.getForEvent(event);
        EconomicEventPricing eventPricing = event.getRevisions().get(1);
        
        createNotification(participants,
                           eventPricing,
                           NotificationReason.STARTING,
                           event.getNotificationTime());  
        createNotification(participants, 
                           eventPricing, 
                           NotificationReason.STOPPING, 
                           event.getStopTime());
    }
    
    public void eventExtensionNotification(EconomicEvent event) {
        List<EconomicEventParticipant> participants = economicEventParticipantDao.getForEvent(event);
        EconomicEventPricing eventPricing = event.getRevisions().get(1);
        
        EconomicEvent initialEvent = event.getInitialEvent();
        if (!attemptDeleteNotification(initialEvent, NotificationReason.STOPPING)) {
            CTILogger.warn("Stop message for initial event was not stopped (current event = " + event + ")");
        }
        
        createNotification(participants,
                           eventPricing,
                           NotificationReason.EXTENDING,
                           event.getNotificationTime());  
        createNotification(participants, 
                           eventPricing, 
                           NotificationReason.STOPPING, 
                           event.getStopTime());
    }

    public void eventRevisionNotification(EconomicEventPricing eventRevision) {
        EconomicEvent event = eventRevision.getEvent();
        EconomicStrategy strategy = economicService.getEconomicStrategy(event);
        List<EconomicEventParticipant> participants = 
            strategy.getRevisionParticipantForNotif(eventRevision);
        Date now = new Date();
        createNotification(participants, 
                           eventRevision, 
                           NotificationReason.ADJUSTING, 
                           now);
    }
    
    public boolean deleteEventNotification(EconomicEvent event, boolean deleteStart, boolean deleteStop) {
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
            CTILogger.error("Potential error while deleting economic notifications (startCancelled="
                            + startCancelled + ", stopCancelled=" + stopCancelled + ")");
        }
        return success;
    }
    
    public void eventExtensionNotification() {
        
    }
    
    public void eventCancellationNotification(EconomicEvent event) {
        List<EconomicEventParticipant> participants = economicEventParticipantDao.getForEvent(event);
        EconomicEventPricing eventPricing = event.getRevisions().get(1);
        
        attemptDeleteNotification(event, NotificationReason.STOPPING);
        
        createNotification(participants,
                           eventPricing,
                           NotificationReason.CANCELING,
                           new Date());  
    }
    
    public synchronized void createNotification(List<EconomicEventParticipant> customers,
                                    EconomicEventPricing economicEventPricing, 
                                    NotificationReason reason,
                                    Date notifTime) {
        
        for (EconomicEventParticipant particip : customers) {
            if (!particip.getEvent().equals(economicEventPricing.getEvent())) {
                throw new IllegalArgumentException("Participant event and pricing event do not match.");
            }
            for (NotifType type : particip.getNotifMap()) {
                EconomicEventNotif notif = new EconomicEventNotif();
                notif.setNotificationTime(notifTime);
                notif.setNotifType(type);
                notif.setRevision(economicEventPricing);
                notif.setParticipant(particip);
                notif.setReason(reason);
                notif.setState(NotificationState.SCHEDULED);
                economicEventNotifDao.save(notif);
            }
        }
        scheduleNotif(notifTime);
    }
    
    protected NotificationBuilder createBuilder(final EventNotif _eventNotif) {
        final EconomicEventNotif eventNotif = (EconomicEventNotif) _eventNotif;
        final EconomicEvent event = eventNotif.getParticipant().getEvent();
        final NotificationReason reason = eventNotif.getReason();
        
        
        final NotificationBuilder notifBuilder = new NotificationBuilder() {

            public Notification buildNotification(Contactable contact) {
                final Notification notif = new Notification("economic");
                fillInBaseAttribs(notif, event);
                EconomicEventPricing initialRevision = event.getInitialRevision();
                Integer numberOfWindows = initialRevision.getNumberOfWindows();
                notif.addData("initialRevisionPriceCount", Integer.toString(numberOfWindows));
                for (int windowIndex = 0; windowIndex < numberOfWindows; ++windowIndex) {
                    EconomicEventPricingWindow economicEventPricingWindow = initialRevision.getWindows().get(windowIndex);
                    String attributeName = "initialRevisionPriceHour" + (windowIndex + 1); // add one to make human friendly
                    notif.addData(attributeName, economicEventPricingWindow.getEnergyPrice().toPlainString());
                }
                notif.addData("action", reason.toString());
                TimeZone timeZone = contact.getTimeZone();
                fillInFormattedTimes(notif, event, timeZone);
                return notif;
            }

            public void notificationComplete(Contactable contact, NotifType notifType, boolean success) {
                // set status on eventNotif
                // probably shouldn't directly manipulate
                eventNotif.setState(success ? NotificationState.SUCCEEDED : NotificationState.FAILED);
                eventNotif.setNotificationTime(new Date());
                economicEventNotifDao.save(eventNotif);

                NotifHandler.logNotificationStatus("EE NOTIF STATUS", success, contact, notifType, this);
            }
            
            public void logIndividualNotification(LiteContactNotification destination, Contactable contactable, NotifType notifType, boolean success) {
                NotifHandler.logNotificationActivity("EE NOTIF", success, destination, contactable, notifType, this);
            }
            
            @Override
            public String toString() {
                return "Economic Event " + event.getDisplayName() + " " + reason + " Notification";
            }

        };
        return notifBuilder;
    }
    
    /**
     * Deletes all pending notifs for an event.
     * @param event
     * @param reason
     * @return true if notifs were prevented from running
     */
    public synchronized boolean attemptDeleteNotification(EconomicEvent event, 
                                                          NotificationReason reason) {
       // this should be serialized with any other method that updates the state
       // this could be optimized within the dao
       List<EconomicEventNotif> forEvent = economicEventNotifDao.getForEventAndReason(event, reason);
       if (forEvent.isEmpty()) {
           return false;
       }
       
       for (EconomicEventNotif notif : forEvent) {
           if (notif.getState().equals(NotificationState.SCHEDULED)) {
               economicEventNotifDao.delete(notif);
           } else {
               CTILogger.warn("Unable to delete event notification, state changed while deleting " 
                              + "(event=" + event + ", reason=" + reason + ", notif=" + notif + ")");
           }
       }
       return true;
    }
    
    protected List<? extends EventNotif> getScheduledNotifs() {
        return economicEventNotifDao.getScheduledNotifs();
    }
    
    protected void updateNotif(EventNotif notif) {
        economicEventNotifDao.save((EconomicEventNotif) notif);
    }
}
