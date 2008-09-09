package com.cannontech.web.updater.routeLocate;

import com.cannontech.common.device.routeLocate.RouteLocateResult;
import com.cannontech.common.util.ResolvableTemplate;
import com.cannontech.web.updater.ResultAccessor;


public enum RouteLocateTypeEnum {

    COMPLETED_COUNT(new ResultAccessor<RouteLocateResult>() {
        public Object getValue(RouteLocateResult routeLocateResult) {
            return routeLocateResult.getCompletedDeviceRouteLocations().size();
        }
    }),
    
    LOCATED_COUNT(new ResultAccessor<RouteLocateResult>() {
        public Object getValue(RouteLocateResult routeLocateResult) {
            return routeLocateResult.getLocatedCount();
        }
    }),
    
    NOT_FOUND_COUNT(new ResultAccessor<RouteLocateResult>() {
        public Object getValue(RouteLocateResult routeLocateResult) {
            return routeLocateResult.getNotFoundCount();
        }
    }),
    
    STOP_TIME(new ResultAccessor<RouteLocateResult>() {
        public Object getValue(RouteLocateResult routeLocateResult) {
            
            
            ResolvableTemplate resolvableTemplate = new ResolvableTemplate("yukon.web.modules.amr.routeLocateHome.recentRouteLocateResults.STOP_TIME");
            resolvableTemplate.addData("stopTime", routeLocateResult.getStopTime());
            
            boolean finished = false;
            if (routeLocateResult.isComplete()) {
                finished = true;
            }
            resolvableTemplate.addData("finished", finished);
            
            return resolvableTemplate;
        }
    }),
    
    IS_COMPLETE(new ResultAccessor<RouteLocateResult>() {
        public Object getValue(RouteLocateResult routeLocateResult) {
            return routeLocateResult.isComplete();
        }
    }),
    
    IS_COMPLETE_TEXT(new ResultAccessor<RouteLocateResult>() {
        public Object getValue(RouteLocateResult routeLocateResult) {
            
            ResolvableTemplate resolvableTemplate = new ResolvableTemplate("yukon.web.modules.amr.routeLocateHome.recentRouteLocateResults.IS_COMPLETE_TEXT");
            resolvableTemplate.addData("isComplete", routeLocateResult.isComplete());
            
            return resolvableTemplate;
        }
    }),
    
    ;
    
    private ResultAccessor<RouteLocateResult> routeLocateValueAccessor;
    
    RouteLocateTypeEnum(ResultAccessor<RouteLocateResult> routeLocateValueAccessor) {

        this.routeLocateValueAccessor = routeLocateValueAccessor;
    }
    
    public Object getValue(RouteLocateResult routeLocateResult) {
        return this.routeLocateValueAccessor.getValue(routeLocateResult);
    }
}
