package com.cannontech.cc.dao;

import java.util.List;

import com.cannontech.cc.model.EconomicEvent;
import com.cannontech.cc.model.EconomicEventParticipant;
import com.cannontech.cc.model.Program;
import com.cannontech.core.dao.support.StandardDaoOperations;

public interface EconomicEventDao extends StandardDaoOperations<EconomicEvent>, CommonEventOperations<EconomicEvent> {

    List<EconomicEvent> getAllForProgram(Program program);
    List<EconomicEvent> getAllForParticipants(List<EconomicEventParticipant> participantList);
    /**
     * @return the child even or null if one doesn't exist
     */
    EconomicEvent getChildEvent(EconomicEvent event);

}
