package com.cannontech.cc.dao;

import java.util.List;

import com.cannontech.cc.model.AccountingEvent;
import com.cannontech.cc.model.Program;
import com.cannontech.core.dao.support.StandardDaoOperations;

public interface AccountingEventDao extends StandardDaoOperations<AccountingEvent>,
        CommonEventOperations {

    List<AccountingEvent> getAllForProgram(Program program);

}
