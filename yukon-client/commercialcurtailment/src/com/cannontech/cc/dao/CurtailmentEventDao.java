package com.cannontech.cc.dao;

import java.util.List;

import com.cannontech.cc.model.CurtailmentEvent;
import com.cannontech.cc.model.Program;
import com.cannontech.database.cache.functions.StandardDaoOperations;

public interface CurtailmentEventDao extends StandardDaoOperations<CurtailmentEvent>,
        CommonEventOperations {

    List<CurtailmentEvent> getAllForProgram(Program program);

}
