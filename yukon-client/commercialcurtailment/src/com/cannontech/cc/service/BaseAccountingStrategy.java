package com.cannontech.cc.service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import com.cannontech.cc.dao.AccountingEventDao;
import com.cannontech.cc.dao.AccountingEventParticipantDao;
import com.cannontech.cc.model.AccountingEvent;
import com.cannontech.cc.model.AccountingEventParticipant;
import com.cannontech.cc.model.BaseEvent;
import com.cannontech.cc.model.GroupCustomerNotif;
import com.cannontech.cc.model.Program;
import com.cannontech.cc.model.ProgramParameterKey;
import com.cannontech.cc.service.builder.AccountingBuilder;
import com.cannontech.cc.service.builder.EventBuilderBase;
import com.cannontech.cc.service.builder.VerifiedCustomer;
import com.cannontech.cc.service.exception.EventCreationException;
import com.cannontech.common.util.TimeUtil;
import com.cannontech.database.data.lite.LiteYukonUser;

public class BaseAccountingStrategy extends StrategyBase implements AccountingStrategy {
    private TransactionTemplate transactionTemplate;
    private AccountingEventDao accountingEventDao;
    private AccountingEventParticipantDao accountingEventParticipantDao;
    
    @Transactional
    public AccountingBuilder createBuilder(Program program) {
        AccountingBuilder builder = new AccountingBuilder();
        AccountingEvent event = new AccountingEvent();
        builder.setEvent(event);
        Integer identifier = programDao.incrementAndReturnIdentifier(program);
        event.setIdentifier(identifier);
        event.setProgram(program);
        
        TimeZone tz = getProgramService().getTimeZone(program);
        builder.setTimeZone(tz);
        
        Date now = new Date();
        Calendar calendar = Calendar.getInstance(tz);
        calendar.setTime(now);
        TimeUtil.roundDateUp(calendar, 5);
        event.setStartTime(calendar.getTime());
        
        builder.setEventDuration(getDefaultDurationMinutes(program));
        
        return builder;
    }
    
    protected AccountingEvent createDatabaseObjects(AccountingBuilder builder) {
        // create curtail event
        AccountingEvent event = builder.getEvent();
        
        accountingEventDao.save(event);
        
        List<GroupCustomerNotif> customerList = builder.getCustomerList();
        for (GroupCustomerNotif notif : customerList) {
            AccountingEventParticipant participant = new AccountingEventParticipant();
            participant.setCustomer(notif.getCustomer());
            participant.setEvent(event);
            accountingEventParticipantDao.save(participant);
            
        }
        return event;
    }
    
    protected int getDefaultDurationMinutes(Program program) {
        return programParameterDao.getParameterValueInt(program, ProgramParameterKey.DEFAULT_EVENT_DURATION_MINUTES);
    }
    
    public AccountingEvent createEvent(final AccountingBuilder builder) throws EventCreationException {
        AccountingEvent event;
        event = (AccountingEvent) transactionTemplate.execute(new TransactionCallback() {
            public Object doInTransaction(TransactionStatus status) {
                AccountingEvent event = createDatabaseObjects(builder);
                return event;
            }
        });
        
        sendProgramNotifications(event, accountingEventParticipantDao.getForEvent(event), "started");
        return event;
    }
    
    @Transactional
    public void deleteEvent(AccountingEvent event, LiteYukonUser user) {
        sendProgramNotifications(event, accountingEventParticipantDao.getForEvent(event), "deleted");
        accountingEventDao.delete(event);
    }
    
    @Transactional
    public void forceDelete(BaseEvent event) {
        AccountingEvent economicEvent = (AccountingEvent) event;

        // notifications????
        accountingEventDao.delete(economicEvent);
    }

    @Override
    protected void verifyCustomer(EventBuilderBase builder, VerifiedCustomer vCustomer) {
        // the customers are always okay
    }
    
    @Override
    public boolean isConsideredActive(BaseEvent event) {
        return false;
    }

    @Override
    public List<? extends BaseEvent> getEventsForProgram(Program program) {
        return accountingEventDao.getAllForProgram(program);
    }
    
    public List<AccountingEventParticipant> getParticipants(AccountingEvent event) {
        return accountingEventParticipantDao.getForEvent(event);
    }

    @Required
    public void setAccountingEventDao(AccountingEventDao accountingEventDao) {
        this.accountingEventDao = accountingEventDao;
    }
    
    @Required
    public void setAccountingEventParticipantDao(AccountingEventParticipantDao accountingEventParticipantDao) {
        this.accountingEventParticipantDao = accountingEventParticipantDao;
    }

    @Required
    public void setTransactionTemplate(TransactionTemplate transactionTemplate) {
        this.transactionTemplate = transactionTemplate;
    }
}
