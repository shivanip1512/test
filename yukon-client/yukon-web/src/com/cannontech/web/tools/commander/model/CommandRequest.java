package com.cannontech.web.tools.commander.model;

import java.util.ArrayList;
import java.util.List;

import com.cannontech.common.util.Completable;

public class CommandRequest implements Completable {
    
    private boolean complete;
    private int id;
    private long timestamp;
    private CommandParams params;
    private List<CommandResponse> responses = new ArrayList<>();
    private String requestText;
    
    public long getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public long getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
    
    public CommandParams getParams() {
        return params;
    }
    
    public void setParams(CommandParams params) {
        this.params = params;
    }
    
    public List<CommandResponse> getResponses() {
        return responses;
    }
    
    public void setResponses(List<CommandResponse> responses) {
        this.responses = responses;
    }
    
    public String getRequestText() {
        return requestText;
    }
    
    public void setRequestText(String requestText) {
        this.requestText = requestText;
    }
    
    @Override
    public boolean isComplete() {
        return complete;
    }
    
    public void setComplete(boolean complete) {
        this.complete = complete;
    }
    
}