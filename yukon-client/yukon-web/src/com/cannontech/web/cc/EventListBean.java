package com.cannontech.web.cc;

import java.util.Collections;
import java.util.List;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.model.ListDataModel;

import com.cannontech.cc.model.BaseEvent;
import com.cannontech.cc.model.Program;
import com.cannontech.cc.service.ProgramService;
import com.cannontech.cc.service.StrategyBase;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.web.cc.methods.BaseDetailBean;

public class EventListBean {
    private Program program;
    private ProgramService programService;
    private LiteYukonUser yukonUser;
    private ListDataModel eventListModel;
    private EventDetailHelper eventDetailHelper;
    
    public Program getProgram() {
        return program;
    }

    public String showProgram() {
        ExternalContext externalContext = 
            FacesContext.getCurrentInstance().getExternalContext();
        String programIdStr = 
            (String) externalContext.getRequestParameterMap().get("programId");
        int programId = Integer.parseInt(programIdStr);
        program = programService.getProgram(programId);
        StrategyBase strategy = 
            eventDetailHelper.getStrategyFactory().getStrategy(getProgram());
        
        List<? extends BaseEvent> eventList = strategy.getEventsForProgram(getProgram());
        Collections.reverse(eventList);
        eventListModel = new ListDataModel(eventList);
        
        return "eventList";
    }
    
    public String showDetail() {
        // get selected row
        BaseEvent event = (BaseEvent) eventListModel.getRowData();
        BaseDetailBean detailBean = eventDetailHelper.getEventDetailBean(event);
        return detailBean.showDetail(event);
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

    public ListDataModel getEventListModel() {
        return eventListModel;
    }

    public void setEventListModel(ListDataModel eventListModel) {
        this.eventListModel = eventListModel;
    }

    public EventDetailHelper getEventDetailHelper() {
        return eventDetailHelper;
    }

    public void setEventDetailHelper(EventDetailHelper eventDetailHelper) {
        this.eventDetailHelper = eventDetailHelper;
    }
    
}
