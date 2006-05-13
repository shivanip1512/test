package com.cannontech.cc.dao;

import java.util.List;

import com.cannontech.cc.model.EconomicEvent;
import com.cannontech.cc.model.Program;

public interface EconomicEventDao extends StandardDaoOperations<EconomicEvent>, CommonEventOperations {

    List<EconomicEvent> getAllForProgram(Program program);

    /**
     * @return the child even or null if one doesn't exist
     */
    EconomicEvent getChildEvent(EconomicEvent event);

}
