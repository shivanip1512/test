package com.cannontech.cc.dao;

import java.util.List;

import com.cannontech.cc.model.AccountingEvent;
import com.cannontech.cc.model.AccountingEventParticipant;

public interface AccountingEventParticipantDao {
    public List<AccountingEventParticipant> getForEvent(AccountingEvent event);
    public void deleteForEvent(AccountingEvent event);
    public void save(AccountingEventParticipant object);
}
