package com.cannontech.cc.dao;

import java.util.List;

import com.cannontech.cc.model.CurtailmentEvent;
import com.cannontech.cc.model.CurtailmentEventParticipant;
import com.cannontech.core.dao.support.IdentifiableObjectProvider;

public interface CurtailmentEventParticipantDao extends IdentifiableObjectProvider<CurtailmentEventParticipant> {
    public List<CurtailmentEventParticipant> getForEvent(CurtailmentEvent event);
    public void deleteForEvent(CurtailmentEvent event);
    public void save(CurtailmentEventParticipant object);
    public void delete(CurtailmentEventParticipant object);
    public CurtailmentEventParticipant getForId(Integer id);
}
