package com.cannontech.notif.handler;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import com.cannontech.cc.dao.EconomicEventNotifDao;
import com.cannontech.cc.model.*;
import com.cannontech.cc.service.BaseEconomicStrategy;
import com.cannontech.cc.service.EconomicService;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.database.cache.functions.CustomerFuncs;
import com.cannontech.database.data.lite.LiteCICustomer;
import com.cannontech.enums.NotificationReason;
import com.cannontech.enums.NotificationState;
import com.cannontech.notif.outputs.*;

public class EconomicEventScheduler {
    private EconomicEventNotifDao economicEventNotifDao;
    private EconomicService economicService;

    private Timer timer = new Timer("Economic Notification Schedular", true);
    
    private static final DateFormat _dateFormatter = new SimpleDateFormat("EEEE, MMMM d"); // e.g. "Tuesday, May 31"
    private static final DateFormat _timeFormatter = new SimpleDateFormat("h:mm a"); // e.g. "3:45 PM"

    private final OutputHandlerHelper _helper;
    
    private class DoScheduledTask extends TimerTask {
        @Override
        public void run() {
            doScheduledNotifs();
        }
    };

    public EconomicEventScheduler(OutputHandlerHelper helper) {
        super();
        _helper = helper;
    }
    
    public void eventCreationNotification(EconomicEvent event) {
        List<EconomicEventParticipant> participants = economicService.getParticipants(event);
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
    
    public void eventRevisionNotification(EconomicEventPricing eventRevision) {
        EconomicEvent event = eventRevision.getEvent();
        BaseEconomicStrategy strategy = economicService.getEconomicStrategy(event);
        List<EconomicEventParticipant> participants = 
            strategy.getRevisionParticipantForNotif(eventRevision);
        Date now = new Date();
        createNotification(participants, 
                           eventRevision, 
                           NotificationReason.ADJUSTING, 
                           now);
    }
    
    public boolean deleteEventNotification(EconomicEvent event) {
        boolean startCancelled = attemptDeleteNotification(event, NotificationReason.STARTING);
        boolean stopCancelled = false;
        if (startCancelled) {
            stopCancelled = attemptDeleteNotification(event, NotificationReason.STOPPING);
        }
        boolean success = startCancelled && stopCancelled;
        if (!success) {
            CTILogger.error("Potential error while deleting eonomic notifications (startCancelled="
                            + startCancelled + ", stopCancelled=" + stopCancelled + ")");
        }
        return success;
    }
    
    public void eventExtensionNotification() {
        
    }
    
    public void eventCancellationNotification(EconomicEvent event) {
        List<EconomicEventParticipant> participants = economicService.getParticipants(event);
        EconomicEventPricing eventPricing = event.getRevisions().get(1);
        
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
            for (Integer type : particip.getNotifMap()) {
                EconomicEventNotif notif = new EconomicEventNotif();
                notif.setNotificationTime(notifTime);
                notif.setNotifTypeId(type);
                notif.setRevision(economicEventPricing);
                notif.setParticipant(particip);
                notif.setReason(reason);
                notif.setState(NotificationState.SCHEDULED);
                economicEventNotifDao.save(notif);
            }
        }
        timer.schedule(new DoScheduledTask(), notifTime);
    }
    
    private NotificationBuilder createBuilder(final EconomicEventNotif eventNotif) {
        final EconomicEvent event = eventNotif.getParticipant().getEvent();
        final EconomicEventPricing eventRevision = eventNotif.getRevision();
        NotificationReason reason = eventNotif.getReason();
        
        final Date stopTime = event.getStopTime();
        
        int durationMinutes = event.getDuration();
        long durationHours = durationMinutes / 60;
        long remainingMinutes = durationMinutes % 60;
        final String durationMinutesStr = Long.toString(durationMinutes);
        final String durationHoursStr = Long.toString(durationHours);
        final String remainingMinutesStr = Long.toString(remainingMinutes);

        final Notification notif = new Notification("economic");
        notif.addData("programname", event.getProgram().getName());
        notif.addData("programtype", event.getProgram().getProgramType().getName());
        notif.addData("eventname", "FILL ME IN"); //TODO
        notif.addData("action", reason.toString());
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
                economicEventNotifDao.save(eventNotif);
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

    private void doScheduledNotifs() {
        List<EconomicEventNotif> notifsPending;
        synchronized (this) {
            //TODO we might want to look for any notifs in the pending state 
            // and set them to some error state
            notifsPending = economicEventNotifDao.getScheduledNotifs();
            for (EconomicEventNotif notif : notifsPending) {
                notif.setState(NotificationState.PENDING);
                economicEventNotifDao.save(notif);
            }
        }
        
        for (EconomicEventNotif notif : notifsPending) {
            Integer customerId = notif.getParticipant().getCustomer().getId();
            LiteCICustomer liteCICustomer = CustomerFuncs.getLiteCICustomer(customerId);
            ContactableCustomer cc = new ContactableCustomer(liteCICustomer);
            Contactable contactable = new SingleNotifContactable(cc, notif.getNotifTypeId());

            NotificationBuilder builder = createBuilder(notif);
            _helper.handleNotification(builder, contactable);
        }
    }

    public void setEconomicEventNotifDao(EconomicEventNotifDao economicEventNotifDao) {
        this.economicEventNotifDao = economicEventNotifDao;
    }

    public void setEconomicService(EconomicService economicService) {
        this.economicService = economicService;
    }



}
