package com.cannontech.cc.service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.springframework.transaction.annotation.Transactional;

import com.cannontech.cc.dao.CurtailmentEventDao;
import com.cannontech.cc.dao.CurtailmentEventNotifDao;
import com.cannontech.cc.dao.CurtailmentEventParticipantDao;
import com.cannontech.cc.model.BaseEvent;
import com.cannontech.cc.model.CurtailmentEvent;
import com.cannontech.cc.model.CurtailmentEventParticipant;
import com.cannontech.cc.model.GroupCustomerNotif;
import com.cannontech.cc.model.Program;
import com.cannontech.cc.service.builder.CurtailmentBuilder;
import com.cannontech.cc.service.enums.CurtailmentEventState;
import com.cannontech.cc.service.exception.EventCreationException;
import com.cannontech.common.util.TimeUtil;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.enums.CurtailmentEventAction;
import com.cannontech.yukon.INotifConnection;

public abstract class BaseNotificationStrategy extends StrategyBase {
    private INotifConnection notificationProxy;
    private CurtailmentEventDao curtailmentEventDao;
    private CurtailmentEventNotifDao curtailmentEventNotifDao;
    private CurtailmentEventParticipantDao curtailmentEventParticipantDao;
    
    @Override
    public String getMethodKey() {
        return "notification";
    }

    public CurtailmentBuilder createBuilder(Program program) {
        CurtailmentBuilder builder = new CurtailmentBuilder();
        CurtailmentEvent event = new CurtailmentEvent();
        event.setState(CurtailmentEventState.INITIAL);
        event.setProgram(program);
        builder.setEvent(event);
        
        TimeZone tz = getProgramService().getTimeZone(program);
        builder.setTimeZone(tz);
        
        Date now = new Date();
        Calendar calendar = Calendar.getInstance(tz);
        calendar.setTime(now);
        calendar.add(Calendar.MINUTE, getDefaultNotifTimeOffsetMinutes(program));
        TimeUtil.roundDateUp(calendar, 15);
        builder.setNotificationTime(calendar.getTime());
        
        calendar.add(Calendar.MINUTE, 
                     getDefaultStartTimeOffsetMinutes(program)
                     - getDefaultNotifTimeOffsetMinutes(program));
        builder.setStartTime(calendar.getTime());
        
        builder.setEventDuration(getDefaultDurationMinutes(program));
        
        return builder;
    }

    protected int getDefaultNotifTimeOffsetMinutes(Program program) {
        return getParameterValueInt(program, "DEFAULT_NOTIFICATION_OFFSET_MINUTES");
    }
    
    protected int getDefaultStartTimeOffsetMinutes(Program program) {
        return getParameterValueInt(program, "DEFAULT_EVENT_OFFSET_MINUTES");
    }
    
    protected int getDefaultDurationMinutes(Program program) {
        return getParameterValueInt(program, "DEFAULT_EVENT_DURATION_MINUTES");
    }
    
    public void verifyTimes(CurtailmentBuilder builder) throws EventCreationException {
        if (builder.getStartTime().before(builder.getNotificationTime())) {
            // start time is equal to or less than notification time
            throw new EventCreationException("Start time must be after notification time.");
        }
    }

    public CurtailmentEvent createEvent(CurtailmentBuilder builder) {
        // verify event

        CurtailmentEvent event = createDatabaseObjects(builder);
        
        getNotificationProxy().sendCurtailmentNotification(event.getId(), CurtailmentEventAction.STARTING);
        
        return event;
    }

    @Transactional
    protected CurtailmentEvent createDatabaseObjects(CurtailmentBuilder builder) {
        // create curtail event
        CurtailmentEvent event = builder.getEvent();
        
        getCurtailmentEventDao().save(event);
        
        List<GroupCustomerNotif> customerList = builder.getCustomerList();
        for (GroupCustomerNotif notif : customerList) {
            CurtailmentEventParticipant participant = new CurtailmentEventParticipant();
            participant.setCustomer(notif.getCustomer());
            participant.setEvent(event);
            participant.setNotifAttribs(notif.getAttribs());
            curtailmentEventParticipantDao.save(participant);
            
        }
        return event;
    }
    
    public Boolean canEventBeCancelled(CurtailmentEvent event, LiteYukonUser user) {
        if (event.getState().equals(CurtailmentEventState.CANCELLED)) {
            return false;
        }
        final int UNSTOPPABLE_WINDOW_MINUTES = 2;
        Date now = new Date();
        Date paddedNotif = TimeUtil.addMinutes(event.getNotificationTime(), UNSTOPPABLE_WINDOW_MINUTES);
        Date paddedStart = TimeUtil.addMinutes(event.getStartTime(), -UNSTOPPABLE_WINDOW_MINUTES);
        return now.before(paddedStart) && now.after(paddedNotif);
    }
    
    public Boolean canEventBeAdjusted(CurtailmentEvent event, LiteYukonUser user) {
        if (event.getState().equals(CurtailmentEventState.CANCELLED)) {
            return false;
        }
        final int UNSTOPPABLE_WINDOW_MINUTES = 2;
        Date now = new Date();
        Date paddedStart = TimeUtil.addMinutes(event.getStartTime(), UNSTOPPABLE_WINDOW_MINUTES);
        Date paddedStop = TimeUtil.addMinutes(event.getStopTime(), -UNSTOPPABLE_WINDOW_MINUTES);
        return now.after(paddedStart) && now.before(paddedStop);
    }
    
    public Boolean canEventBeChanged(CurtailmentEvent event, LiteYukonUser user) {
        return canEventBeDeleted(event, user);
    }
    
    public Boolean canEventBeDeleted(CurtailmentEvent event, LiteYukonUser user) {
        final int UNSTOPPABLE_WINDOW_MINUTES = 2;
        Date now = new Date();
        Date paddedNotif = TimeUtil.addMinutes(event.getNotificationTime(), -UNSTOPPABLE_WINDOW_MINUTES);
        return now.before(paddedNotif);
    }
    
    @Transactional
    public void deleteEvent(CurtailmentEvent event, LiteYukonUser user) {
        if (!canEventBeDeleted(event, user)) {
            throw new RuntimeException("Event can't be deleted right now by this user");
        }
        boolean success = getNotificationProxy()
            .attemptDeleteCurtailmentNotification(event.getId());

        if (success) {
            // no notifications have been sent, simply delete the event
            curtailmentEventParticipantDao.deleteForEvent(event);
            curtailmentEventDao.delete(event);
        } else {
            // this is crude, but it will work for the time being
            throw new RuntimeException("DB State might have been lost while deleting event.");
        }
    }
    
    public void cancelEvent(CurtailmentEvent event, LiteYukonUser user) {
        if (!canEventBeCancelled(event, user)) {
            throw new RuntimeException("Event can't be cancelled right now by this user");
        }
        getNotificationProxy().sendCurtailmentNotification(event.getId(), CurtailmentEventAction.CANCELING);

        event.setState(CurtailmentEventState.CANCELLED);
        curtailmentEventDao.save(event);
    }

    @Override
    public List<? extends BaseEvent> getEventsForProgram(Program program) {
        return curtailmentEventDao.getAllForProgram(program);
    }

    public INotifConnection getNotificationProxy() {
        return notificationProxy;
    }

    public void setNotificationProxy(INotifConnection notificationProxy) {
        this.notificationProxy = notificationProxy;
    }

    public CurtailmentEventDao getCurtailmentEventDao() {
        return curtailmentEventDao;
    }

    public void setCurtailmentEventDao(CurtailmentEventDao curtailmentEventDao) {
        this.curtailmentEventDao = curtailmentEventDao;
    }

    public CurtailmentEventNotifDao getCurtailmentEventNotifDao() {
        return curtailmentEventNotifDao;
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
