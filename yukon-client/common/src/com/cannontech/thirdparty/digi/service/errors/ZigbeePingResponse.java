package com.cannontech.thirdparty.digi.service.errors;

import org.springframework.context.MessageSourceResolvable;

import com.cannontech.database.db.point.stategroup.Commissioned;

public class ZigbeePingResponse {
    private boolean success;
    private Commissioned state;
    private MessageSourceResolvable pingResultResolvable;
    
    
    public ZigbeePingResponse( boolean success, 
                                          Commissioned state, 
                                          MessageSourceResolvable pingResultResolvable) {
        this.success = success;
        this.state = state;
        this.pingResultResolvable = pingResultResolvable;
    }

    public boolean isSuccess() {
        return success;
    }

    public Commissioned getState() {
        return state;
    }
    
    public MessageSourceResolvable getPingResultResolvable() {
        return pingResultResolvable;
    }
}
