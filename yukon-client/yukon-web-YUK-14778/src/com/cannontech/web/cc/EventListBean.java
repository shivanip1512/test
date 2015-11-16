package com.cannontech.web.cc;

import java.util.Collections;
import java.util.List;

import com.cannontech.cc.model.BaseEvent;
import com.cannontech.cc.model.Program;
import com.cannontech.cc.service.CICurtailmentStrategy;
import com.cannontech.cc.service.ProgramService;
import com.cannontech.database.data.lite.LiteYukonUser;

public class EventListBean {
    private Program program;
    private ProgramService programService;
    private LiteYukonUser yukonUser;
    private EventDetailHelper eventDetailHelper;
    
    public Program getProgram() {
        return program;
    }
    
    public void setProgram(Program program) {
        this.program = program;
    }

    public String showProgram() {
        return "eventList";
    }
    
    public List<? extends BaseEvent> getEventList() {
        CICurtailmentStrategy strategy = 
            eventDetailHelper.getStrategyFactory().getStrategy(getProgram());
        
        List<? extends BaseEvent> eventList = strategy.getEventsForProgram(getProgram());
        Collections.reverse(eventList);
        return eventList;
    }
    
    public LiteYukonUser getYukonUser() {
        return yukonUser;
    }

    public void setYukonUser(LiteYukonUser yukonUser) {
        this.yukonUser = yukonUser;
    }

    public ProgramService getProgramService() {
        return programService;
    }

    public void setProgramService(ProgramService programService) {
        this.programService = programService;
    }

    public EventDetailHelper getEventDetailHelper() {
        return eventDetailHelper;
    }

    public void setEventDetailHelper(EventDetailHelper eventDetailHelper) {
        this.eventDetailHelper = eventDetailHelper;
    }
    
}
