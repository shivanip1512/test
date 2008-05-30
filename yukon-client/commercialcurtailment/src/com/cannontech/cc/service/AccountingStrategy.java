package com.cannontech.cc.service;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import com.cannontech.cc.model.AccountingEvent;
import com.cannontech.cc.model.AccountingEventParticipant;
import com.cannontech.cc.model.Program;
import com.cannontech.cc.service.builder.AccountingBuilder;
import com.cannontech.cc.service.exception.EventCreationException;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.user.YukonUserContext;

public interface AccountingStrategy extends CICurtailmentStrategy {

    public AccountingBuilder createBuilder(Program program, YukonUserContext yukonUserContext);

    public AccountingEvent createEvent(final AccountingBuilder builder)
        throws EventCreationException;

    @Transactional
    public void deleteEvent(final AccountingEvent event, LiteYukonUser user);
    
    public List<AccountingEventParticipant> getParticipants(AccountingEvent event);
}