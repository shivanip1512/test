package com.cannontech.cc.dao;

import java.util.List;

import com.cannontech.cc.model.CurtailmentEvent;
import com.cannontech.cc.model.CurtailmentEventParticipant;
import com.cannontech.core.dao.support.IdAccessible;

public interface CurtailmentEventParticipantDao extends IdAccessible<CurtailmentEventParticipant> {
    public List<CurtailmentEventParticipant> getForEvent(CurtailmentEvent event);
    public void deleteForEvent(CurtailmentEvent event);
    public void save(CurtailmentEventParticipant object);
    public void delete(CurtailmentEventParticipant object);
    public CurtailmentEventParticipant getForId(Integer id);
}
