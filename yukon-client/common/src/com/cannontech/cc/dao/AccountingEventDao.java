package com.cannontech.cc.dao;

import java.util.List;

import com.cannontech.cc.model.AccountingEvent;
import com.cannontech.cc.model.Program;
import com.cannontech.core.dao.support.IdentifiableObjectProvider;

public interface AccountingEventDao extends IdentifiableObjectProvider<AccountingEvent>, CommonEventOperations<AccountingEvent> {

    List<AccountingEvent> getAllForProgram(Program program);
    public void save(AccountingEvent object);
    public void delete(AccountingEvent object);
    public AccountingEvent getForId(Integer id);
}
