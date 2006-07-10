package com.cannontech.cc.dao;

import java.util.List;

import com.cannontech.cc.model.CurtailmentEvent;
import com.cannontech.cc.model.CurtailmentEventParticipant;
import com.cannontech.database.cache.functions.StandardDaoOperations;

public interface CurtailmentEventParticipantDao extends StandardDaoOperations<CurtailmentEventParticipant> {
    public List<CurtailmentEventParticipant> getForEvent(CurtailmentEvent event);
    public void deleteForEvent(CurtailmentEvent event);
}
