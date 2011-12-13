package com.cannontech.notif.handler;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.cc.model.BaseEvent;
import com.cannontech.cc.model.EventNotif;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.util.Iso8601DateUtil;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.database.data.lite.LiteCICustomer;
import com.cannontech.enums.NotificationState;
import com.cannontech.notif.outputs.*;

public abstract class EventScheduler {

    private static final Logger log = YukonLogManager.getLogger(EventScheduler.class);
    
    static final DateFormat _dateFormatter = new SimpleDateFormat("EEEE, MMMM d"); // e.g. "Tuesday, May 31"
    static final DateFormat _timeFormatter = new SimpleDateFormat("h:mm a"); // e.g. "3:45 PM"
    
    protected Timer timer = new Timer(getClass().getSimpleName(), true);

    private @Autowired OutputHandlerHelper outputHandlerHelper;

    private class DoScheduledTask extends TimerTask {
        @Override
        public void run() {
            try {
                doScheduledNotifs();
            } catch (Exception e) {
                log.error("There was an uncaught exception while running timer task", e);
            }
        }
    };

    public EventScheduler() {
        // this is the just-in-case timer, it will start about 30 seconds after
        // startup and check every five minutes after that
        double extraPeriod = Math.random() * 2;
        int period = (int) ((4 + extraPeriod) * 60 * 1000);
        int extraDelay = (int) (Math.random() * 30);
        int delay = (30 + extraDelay) * 1000;
        timer.schedule(new DoScheduledTask(), delay, period);
    }

    protected void doScheduledNotifs() {
        List<? extends EventNotif> notifsPending;
        synchronized (this) {
            log.debug("Starting doScheduledNotifs for " + getClass().getSimpleName());
            notifsPending = getScheduledNotifs();
            
            log.debug("Found " + notifsPending.size() + " for " + getClass().getSimpleName());
        
            for (EventNotif notif : notifsPending) {
                log.debug("Starting " + notif + " processing for " + notif.getCustomer().getCompanyName());
                try {
                    notif.setState(NotificationState.PENDING);
                    updateNotif(notif);
                    Integer customerId = notif.getCustomer().getId();
                    LiteCICustomer liteCICustomer = DaoFactory.getCustomerDao().getLiteCICustomer(customerId);
                    ContactableCustomer cc = new ContactableCustomer(liteCICustomer);
                    Contactable contactable = new SingleNotifContactable(cc, notif.getNotifType());
                    
                    NotificationBuilder builder = createBuilder(notif);
                    outputHandlerHelper.handleNotification(builder, contactable);
                } catch (Exception e) {
                    log.error("Unable to process notification", e);
                    notif.setState(NotificationState.FAILED);
                    updateNotif(notif);
                }
                log.debug("Finished " + notif + " processing for " + notif.getCustomer().getCompanyName());
            }
        }
    }
    
    protected abstract NotificationBuilder createBuilder(EventNotif notif);

    protected void scheduleNotif(Date time) {
        timer.schedule(new DoScheduledTask(), time);
    }

    protected abstract void updateNotif(EventNotif notif);

    abstract protected List<? extends EventNotif> getScheduledNotifs();

    protected void fillInBaseAttribs(Notification notif, BaseEvent event) {
        int durationMinutes = event.getDuration();
        long durationHours = durationMinutes / 60;
        long remainingMinutes = durationMinutes % 60;
        final String durationMinutesStr = Long.toString(durationMinutes);
        final String durationHoursStr = Long.toString(durationHours);
        final String remainingMinutesStr = Long.toString(remainingMinutes);
    
        notif.addData("programname", event.getProgram().getName());
        notif.addData("programtype", event.getProgram().getProgramType().getName());
        notif.addData("eventname", event.getDisplayName());
        notif.addData("durationminutes", durationMinutesStr);
        notif.addData("durationhours", durationHoursStr);
        notif.addData("remainingminutes", remainingMinutesStr);
    }

    protected void fillInFormattedTimes(final Notification notif, final BaseEvent event, TimeZone timeZone) {
        synchronized (_timeFormatter) {
            // we will use _dateFormatter as if we'd explicitly synched on it too
            _timeFormatter.setTimeZone(timeZone);
            _dateFormatter.setTimeZone(timeZone);
            
            notif.addData("timezone", timeZone.getDisplayName());
            notif.addData("starttime", _timeFormatter.format(event.getStartTime()));
            notif.addData("startdate", _dateFormatter.format(event.getStartTime()));
            notif.addData("startdatetime", Iso8601DateUtil.formatIso8601Date(event.getStartTime(), timeZone));
            notif.addData("stoptime", _timeFormatter.format(event.getStopTime()));
            notif.addData("stopdate", _dateFormatter.format(event.getStopTime()));
            notif.addData("stopdatetime", Iso8601DateUtil.formatIso8601Date(event.getStopTime(), timeZone));
        }
    }

}
