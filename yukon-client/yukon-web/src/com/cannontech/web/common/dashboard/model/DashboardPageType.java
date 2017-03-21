package com.cannontech.web.common.dashboard.model;

import static com.cannontech.web.common.dashboard.model.DashboardScope.*;

import java.util.Set;

import com.google.common.collect.ImmutableSet;

/**
 * Enumeration of pages that provide a user-customizable dashboard.
 */
public enum DashboardPageType {
    MAIN(GENERAL)
    //AMI(GENERAL),
    //DR(GENERAL),
    //METER_HOME(GENERAL, DEVICE_SPECIFIC),
    ;
    
    private Set<DashboardScope> scopes;
    
    private DashboardPageType(DashboardScope... scopes) {
        this.scopes = ImmutableSet.copyOf(scopes);
    }
    
    public Set<DashboardScope> getScopes() {
        return scopes;
    }
}
