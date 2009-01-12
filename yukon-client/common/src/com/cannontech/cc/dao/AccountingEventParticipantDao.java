package com.cannontech.cc.dao;

import java.util.List;

import com.cannontech.cc.model.AccountingEvent;
import com.cannontech.cc.model.AccountingEventParticipant;
import com.cannontech.core.dao.support.StandardDaoOperations;

public interface AccountingEventParticipantDao extends StandardDaoOperations<AccountingEventParticipant> {
    public List<AccountingEventParticipant> getForEvent(AccountingEvent event);
    public void deleteForEvent(AccountingEvent event);
}
