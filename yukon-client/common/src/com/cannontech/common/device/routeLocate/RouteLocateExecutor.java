package com.cannontech.common.device.routeLocate;

import java.util.List;

import com.cannontech.common.bulk.collection.device.model.DeviceCollection;
import com.cannontech.common.util.SimpleCallback;
import com.cannontech.database.data.lite.LiteYukonUser;

public interface RouteLocateExecutor {

    public String execute(DeviceCollection deviceCollection, List<Integer> routeIds, boolean autoUpdateRoute, SimpleCallback<RouteLocateResult> callback, LiteYukonUser user);
    
    public void cancelExecution(String resultId, LiteYukonUser user);
    
    public List<RouteLocateResult> getCompleted();
    
    public List<RouteLocateResult> getPending();
    
    public RouteLocateResult getResult(String id);
}
