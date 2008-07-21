package com.cannontech.web.updater.routeLocate;

import org.springframework.beans.factory.annotation.Required;

import com.cannontech.common.device.routeLocate.RouteLocateResult;
import com.cannontech.common.util.RecentResultsCache;
import com.cannontech.web.updater.RecentResultUpdateBackingService;

public class RouteLocateBackingService extends RecentResultUpdateBackingService {

    private RecentResultsCache<RouteLocateResult> resultsCache = null;
    
    @Override
    public Object getResultValue(String resultId, String resultTypeStr) {

       RouteLocateResult routeLocateResult = resultsCache.getResult(resultId);
       
       if (routeLocateResult == null) {
           return "";
       }
       RouteLocateTypeEnum routeLocateTypeEnum = RouteLocateTypeEnum.valueOf(resultTypeStr);
       return routeLocateTypeEnum.getValue(routeLocateResult);
    }

    @Required
    public void setResultsCache(RecentResultsCache<RouteLocateResult> resultsCache) {
        this.resultsCache = resultsCache;
    }
}
