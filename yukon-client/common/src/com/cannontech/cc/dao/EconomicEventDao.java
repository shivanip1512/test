package com.cannontech.cc.dao;

import java.util.List;

import com.cannontech.cc.model.EconomicEvent;
import com.cannontech.cc.model.EconomicEventParticipant;
import com.cannontech.cc.model.Program;
import com.cannontech.core.dao.support.IdentifiableObjectProvider;

public interface EconomicEventDao extends IdentifiableObjectProvider<EconomicEvent>, CommonEventOperations<EconomicEvent> {

    List<EconomicEvent> getAllForProgram(Program program);
    List<EconomicEvent> getAllForParticipants(List<EconomicEventParticipant> participantList);
    EconomicEvent getChildEvent(EconomicEvent event);
    public void save(EconomicEvent object);
    public void delete(EconomicEvent object);
    public EconomicEvent getForId(Integer id);
}
