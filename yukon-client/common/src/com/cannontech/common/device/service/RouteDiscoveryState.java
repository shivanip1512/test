package com.cannontech.common.device.service;

import java.util.List;

import com.cannontech.common.util.SimpleCallback;
import com.cannontech.database.data.lite.LiteYukonUser;

public class RouteDiscoveryState {

    private List<Integer> routeIds;
    private int routeIdx = 0;
    private int attemptCount;
    private LiteYukonUser user;
    private SimpleCallback<Integer> routeFoundCallback;
    
    public List<Integer> getRouteIds() {
        return routeIds;
    }
    public void setRouteIds(List<Integer> routeIds) {
        this.routeIds = routeIds;
    }
    public int getRouteIdx() {
        return routeIdx;
    }
    public void incrementRoute() {
        routeIdx++;
    }
    public boolean isRoutesRemaining() {
        return getRouteIdx() < getRouteIds().size();
    }
    public int getAttemptCount() {
        return attemptCount;
    }
    public void setAttemptCount(int attemptCount) {
        this.attemptCount = attemptCount;
    }
    public LiteYukonUser getUser() {
        return user;
    }
    public void setUser(LiteYukonUser user) {
        this.user = user;
    }
    public SimpleCallback<Integer> getRouteFoundCallback() {
        return routeFoundCallback;
    }
    public void setRouteFoundCallback(
            SimpleCallback<Integer> routeFoundCallback) {
        this.routeFoundCallback = routeFoundCallback;
    }
    
}
