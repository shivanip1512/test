package com.cannontech.cc.service;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import com.cannontech.cc.dao.CurtailmentEventDao;
import com.cannontech.cc.dao.CurtailmentEventNotifDao;
import com.cannontech.cc.dao.CurtailmentEventParticipantDao;
import com.cannontech.cc.model.BaseEvent;
import com.cannontech.cc.model.CurtailmentEvent;
import com.cannontech.cc.model.CurtailmentEventParticipant;
import com.cannontech.cc.model.GroupCustomerNotif;
import com.cannontech.cc.model.Program;
import com.cannontech.cc.model.ProgramParameterKey;
import com.cannontech.cc.service.builder.CurtailmentBuilder;
import com.cannontech.cc.service.builder.CurtailmentChangeBuilder;
import com.cannontech.cc.service.enums.CurtailmentEventState;
import com.cannontech.cc.service.exception.EventCreationException;
import com.cannontech.cc.service.exception.EventModificationException;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.TimeUtil;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.enums.CurtailmentEventAction;

public abstract class BaseNotificationStrategy extends StrategyBase implements NotificationStrategy {
    private CurtailmentEventDao curtailmentEventDao;
    private CurtailmentEventNotifDao curtailmentEventNotifDao;
    private CurtailmentEventParticipantDao curtailmentEventParticipantDao;
    private TransactionTemplate transactionTemplate;
    
    public void setTransactionTemplate(TransactionTemplate transactionTemplate) {
        this.transactionTemplate = transactionTemplate;
    }

    @Transactional
    public CurtailmentBuilder createBuilder(Program program) {
        CurtailmentBuilder builder = new CurtailmentBuilder();
        CurtailmentEvent event = new CurtailmentEvent();
        Integer identifier = programDao.incrementAndReturnIdentifier(program);
        event.setIdentifier(identifier);
        event.setState(CurtailmentEventState.INITIAL);
        event.setProgram(program);
        builder.setEvent(event);
        
        TimeZone tz = getProgramService().getTimeZone(program);
        builder.setTimeZone(tz);
        
        Date now = new Date();
        Calendar calendar = Calendar.getInstance(tz);
        calendar.setTime(now);
        int startTimeOffsetMinutes = getDefaultStartTimeOffsetMinutes(program);
        calendar.add(Calendar.MINUTE, startTimeOffsetMinutes);
        if (startTimeOffsetMinutes < 60) {
            TimeUtil.roundDateUp(calendar, 5);
        } else {
            TimeUtil.roundDateUp(calendar, 60);
        }
        builder.getEvent().setStartTime(calendar.getTime());
        
        calendar.add(Calendar.MINUTE, 
                     -getDefaultNotifTimeBacksetMinutes(program));
        builder.getEvent().setNotificationTime(calendar.getTime());

        builder.setEventDuration(getDefaultDurationMinutes(program));
        
        return builder;
    }

    public CurtailmentChangeBuilder createChangeBuilder(CurtailmentEvent event) {
        CurtailmentChangeBuilder builder = new CurtailmentChangeBuilder(event);
        builder.setNewLength(event.getDuration());
        builder.setNewMessage("");
        builder.setNewStartTime(event.getStartTime());
        
        return builder;
    }

    protected int getDefaultNotifTimeBacksetMinutes(Program program) {
        return programParameterDao.getParameterValueInt(program, ProgramParameterKey.DEFAULT_NOTIFICATION_OFFSET_MINUTES);
    }
    
    protected int getDefaultStartTimeOffsetMinutes(Program program) {
        return programParameterDao.getParameterValueInt(program, ProgramParameterKey.DEFAULT_EVENT_OFFSET_MINUTES);
    }
    
    protected int getDefaultDurationMinutes(Program program) {
        return programParameterDao.getParameterValueInt(program, ProgramParameterKey.DEFAULT_EVENT_DURATION_MINUTES);
    }
    
    protected int getMinimumDurationMinutes(Program program) {
        return programParameterDao.getParameterValueInt(program, ProgramParameterKey.MINIMUM_EVENT_DURATION_MINUTES);
    }
    
    protected int getMinimumNotificationMinutes(Program program) {
        return programParameterDao.getParameterValueInt(program, ProgramParameterKey.MINIMUM_NOTIFICATION_MINUTES);
    }
    
    public void verifyTimes(CurtailmentBuilder builder) throws EventCreationException {
        if (builder.getStartTime().before(builder.getNotificationTime())) {
            // start time is equal to or less than notification time
            throw new EventCreationException("Start time must be after notification time.");
        }
        int minDuration = getMinimumDurationMinutes(builder.getProgram());
        if (builder.getEventDuration() < minDuration) {
            throw new EventCreationException("Duration must be greater than " + minDuration + " minutes.");
        }
        int notifMinutes = TimeUtil.differenceMinutes(builder.getNotificationTime(), builder.getStartTime());
        int minNotification = getMinimumNotificationMinutes(builder.getProgram());
        if (notifMinutes < minNotification) {
            throw new EventCreationException("Notification time must be greater than " + minNotification + " minutes.");
        }
    }
    
    public CurtailmentEvent createEvent(final CurtailmentBuilder builder) throws EventCreationException {
        CurtailmentEvent event;
        event = (CurtailmentEvent) transactionTemplate.execute(new TransactionCallback() {
            public Object doInTransaction(TransactionStatus status) {
                verifyTimes(builder);
                verifyCustomers(builder);
                
                CurtailmentEvent event = createDatabaseObjects(builder);
                return event;
            }
        });
        
        getNotificationProxy().sendCurtailmentNotification(event.getId(), CurtailmentEventAction.STARTING);
        sendProgramNotifications(event, curtailmentEventParticipantDao.getForEvent(event), "started");
        return event;
    }

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
        if (event.getState().equals(CurtailmentEventState.MODIFIED)) {
            return false;
        }
        final int UNSTOPPABLE_WINDOW_MINUTES = 2;
        Date now = new Date();
        Date paddedNotif = event.getNotificationTime();
        Date paddedStart = TimeUtil.addMinutes(event.getStartTime(), -UNSTOPPABLE_WINDOW_MINUTES);
        return now.before(paddedStart) && now.after(paddedNotif);
    }
    
    public Boolean canEventBeAdjusted(CurtailmentEvent event, LiteYukonUser user) {
        if (event.getState().equals(CurtailmentEventState.CANCELLED)) {
            return false;
        }
        final int UNSTOPPABLE_WINDOW_MINUTES = 2;
        Date now = new Date();
        Date paddedStart = TimeUtil.addMinutes(event.getNotificationTime(), 0);
        Date paddedStop = TimeUtil.addMinutes(event.getStopTime(), -UNSTOPPABLE_WINDOW_MINUTES);
        return now.after(paddedStart) && now.before(paddedStop);
    }
    
    public Boolean canEventBeChanged(CurtailmentEvent event, LiteYukonUser user) {
        return false;
    }
    
    public Boolean canEventBeDeleted(CurtailmentEvent event, LiteYukonUser user) {
        final int UNSTOPPABLE_WINDOW_MINUTES = 2;
        Date now = new Date();
        Date paddedNotif = TimeUtil.addMinutes(event.getNotificationTime(), -UNSTOPPABLE_WINDOW_MINUTES);
        return now.before(paddedNotif);
    }
    
    /**
     * Future use for now < notif time
     * @param event
     * @param user
     */
    @Transactional
    public void changeEvent(CurtailmentChangeBuilder builder, LiteYukonUser user) {
    }
    
    public CurtailmentEvent adjustEvent(final CurtailmentChangeBuilder builder, final LiteYukonUser user) 
    throws EventModificationException {
        final CurtailmentEvent event = builder.getOriginalEvent();
        transactionTemplate.execute(new TransactionCallback(){
            public Object doInTransaction(TransactionStatus status) {
                if (!canEventBeAdjusted(event, user)) {
                    throw new EventModificationException("Event cannot be modified at this time by this user.");
                }
                event.setDuration(builder.getNewLength());
                event.setMessage(builder.getNewMessage());
                event.setState(CurtailmentEventState.MODIFIED);
                curtailmentEventDao.save(event);
                
                return event;
            } 
        });
        CTILogger.info(event + " modified by " + user + " new durration " + builder.getNewLength());
        
        getNotificationProxy().sendCurtailmentNotification(event.getId(), CurtailmentEventAction.ADJUSTING);
        sendProgramNotifications(event, curtailmentEventParticipantDao.getForEvent(event), "adjusted");
        return event;
    }
    
    @Transactional
    public void deleteEvent(final CurtailmentEvent event, LiteYukonUser user) {
        if (!canEventBeDeleted(event, user)) {
            throw new RuntimeException("Event can't be deleted right now by this user");
        }
        boolean success = getNotificationProxy()
            .attemptDeleteCurtailmentNotification(event.getId(), true);

        sendProgramNotifications(event, curtailmentEventParticipantDao.getForEvent(event), success ? "deleted" : "deleted (failed)");
        if (success) {
            doBeforeDeleteEvent(event, user);
            curtailmentEventDao.delete(event);
        } else {
            // this is crude, but it will work for the time being
            throw new RuntimeException("DB State might have been lost while deleting event.");
        }
    }
    
    protected void doBeforeDeleteEvent(final CurtailmentEvent event, LiteYukonUser user) {
    }
    
    @Transactional
    public void forceDelete(BaseEvent event) {
        CurtailmentEvent economicEvent = (CurtailmentEvent) event;

        // notifications????
        curtailmentEventDao.delete(economicEvent);
    }
    
    public void cancelEvent(CurtailmentEvent event, LiteYukonUser user) {
        if (!canEventBeCancelled(event, user)) {
            throw new RuntimeException("Event can't be cancelled right now by this user");
        }
        getNotificationProxy().attemptDeleteCurtailmentNotification(event.getId(), false);
        getNotificationProxy().sendCurtailmentNotification(event.getId(), CurtailmentEventAction.CANCELING);

        event.setState(CurtailmentEventState.CANCELLED);
        curtailmentEventDao.save(event);
        sendProgramNotifications(event, curtailmentEventParticipantDao.getForEvent(event), "cancelled");
    }

    @Override
    public boolean isConsideredActive(BaseEvent event) {
        CurtailmentEventState[] activeStates = 
            new CurtailmentEventState[] {CurtailmentEventState.INITIAL,
                                         CurtailmentEventState.MODIFIED};
        CurtailmentEvent curtailmentEvent = (CurtailmentEvent)event;
        return Arrays.asList(activeStates).contains(curtailmentEvent.getState());
    }

    @Override
    public List<? extends BaseEvent> getEventsForProgram(Program program) {
        return curtailmentEventDao.getAllForProgram(program);
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

    public void setCurtailmentEventParticipantDao(CurtailmentEventParticipantDao curtailmentEventParticipantDao) {
        this.curtailmentEventParticipantDao = curtailmentEventParticipantDao;
    }
    
}
