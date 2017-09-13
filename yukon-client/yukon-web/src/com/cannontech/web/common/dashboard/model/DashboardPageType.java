package com.cannontech.web.common.dashboard.model;

import static com.cannontech.web.common.dashboard.model.DashboardScope.GENERAL;

import java.util.Set;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.collect.ImmutableSet;

/**
 * Enumeration of pages that provide a user-customizable dashboard.
 */
public enum DashboardPageType {
    MAIN(-1, GENERAL), 
    AMI(-2, GENERAL),
    //DR(GENERAL),
    //METER_HOME(GENERAL, DEVICE_SPECIFIC),
    ;
    private static final ImmutableMap<Integer, DashboardPageType> dashboardTypeLookup;
    
    static {
        Builder<Integer, DashboardPageType> builder = ImmutableMap.builder();
        for (DashboardPageType type : values()) {
            builder.put(type.getDefaultDashboardId(), type);
        }
        dashboardTypeLookup = builder.build();
    }
    
    private int defaultDashboardId;
    private Set<DashboardScope> scopes;
    
    private DashboardPageType(int defaultDashboardId, DashboardScope... scopes) {
        this.defaultDashboardId = defaultDashboardId;
        this.scopes = ImmutableSet.copyOf(scopes);
    }
    
    public Set<DashboardScope> getScopes() {
        return scopes;
    }

    public int getDefaultDashboardId() {
        return defaultDashboardId;
    }
    
    public static DashboardPageType getDashboardTypeById(int id) {
        return dashboardTypeLookup.get(id);
    }

}
