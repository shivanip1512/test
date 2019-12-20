package com.cannontech.web.dev.service;

import com.cannontech.web.dev.error.RelayStateInjectionException;
import com.cannontech.web.dev.model.RelayStateInjectionParams;
import com.cannontech.web.dev.model.RelayStateInjectionStatus;

public interface RelayStateInjectionService {
    
    //start injection
    //return device count
    int startInjection(RelayStateInjectionParams params) throws RelayStateInjectionException;
    
    //stop injection
    void stopInjection() throws RelayStateInjectionException;
    
    //get injection status
    RelayStateInjectionStatus getStatus();
    
    //get injection params
    RelayStateInjectionParams getParams();
    
}
