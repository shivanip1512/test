package com.cannontech.web.tools.commander.model;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.cannontech.common.util.Completable;

public class CommandRequest implements Completable {
    
    private boolean complete;
    private int id;
    private long timestamp;
    private CommandParams params;
    private Map<Integer, CommandResponse> responses = new ConcurrentHashMap<>();
    
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
    
    public Map<Integer, CommandResponse> getResponses() {
        return responses;
    }
    
    public void setResponses(Map<Integer, CommandResponse> responses) {
        this.responses = responses;
    }
    
    @Override
    public boolean isComplete() {
        return complete;
    }
    
    public void setComplete(boolean complete) {
        this.complete = complete;
    }
    
}