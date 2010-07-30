package com.cannontech.cc.dao;

import java.util.List;

import com.cannontech.cc.model.CurtailmentEvent;
import com.cannontech.cc.model.Program;
import com.cannontech.core.dao.support.IdentifiableObjectProvider;

public interface CurtailmentEventDao extends IdentifiableObjectProvider<CurtailmentEvent>, CommonEventOperations<CurtailmentEvent> {

    List<CurtailmentEvent> getAllForProgram(Program program);
    public void save(CurtailmentEvent object);
    public void delete(CurtailmentEvent object);
    public CurtailmentEvent getForId(Integer id);
}
