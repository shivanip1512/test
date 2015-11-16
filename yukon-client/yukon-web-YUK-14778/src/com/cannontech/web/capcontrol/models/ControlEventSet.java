package com.cannontech.web.capcontrol.models;

import java.util.List;

import com.cannontech.database.db.capcontrol.CCEventLog;

public class ControlEventSet {
    
    private int paoId = -1;
    private String paoName = "";
    private List<CCEventLog> controlEvents = null;

    public ControlEventSet(int id, List<CCEventLog> events) {
        this.paoId = id;
        this.controlEvents = events;
    }

    public int getPaoId() {
        return paoId;
    }
    
    public void setPaoId(int paoId) {
        this.paoId = paoId;
    }
    
    public List<CCEventLog> getControlEvents() {
        return controlEvents;
    }

    public void setControlEvents(List<CCEventLog> controlEvents) {
        this.controlEvents = controlEvents;
    }
    
    public void setPaoName(String paoName) {
        this.paoName = paoName;
    }
    
    public String getPaoName() {
        return paoName;
    }
}
