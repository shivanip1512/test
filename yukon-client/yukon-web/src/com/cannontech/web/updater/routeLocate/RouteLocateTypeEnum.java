package com.cannontech.web.updater.routeLocate;

import com.cannontech.common.device.routeLocate.RouteLocateResult;
import com.cannontech.common.util.ResolvableTemplate;


public enum RouteLocateTypeEnum {

    COMPLETED_COUNT(new RouteLocateValueAccessor() {
        public Object getValue(RouteLocateResult routeLocateResult) {
            return routeLocateResult.getCompletedDeviceRouteLocations().size();
        }
    }),
    
    LOCATED_COUNT(new RouteLocateValueAccessor() {
        public Object getValue(RouteLocateResult routeLocateResult) {
            return routeLocateResult.getLocatedCount();
        }
    }),
    
    NOT_FOUND_COUNT(new RouteLocateValueAccessor() {
        public Object getValue(RouteLocateResult routeLocateResult) {
            return routeLocateResult.getNotFoundCount();
        }
    }),
    
    START_TIME(new RouteLocateValueAccessor() {
        public Object getValue(RouteLocateResult routeLocateResult) {
            return routeLocateResult.getStartTime();
        }
    }),
    
    STOP_TIME(new RouteLocateValueAccessor() {
        public Object getValue(RouteLocateResult routeLocateResult) {
            
            
            ResolvableTemplate resolvableTemplate = new ResolvableTemplate("yukon.common.device.routeLocate.home.recentRouteLocateResults.STOP_TIME");
            resolvableTemplate.addData("stopTime", routeLocateResult.getStopTime());
            
            boolean finished = false;
            if (routeLocateResult.isComplete()) {
                finished = true;
            }
            resolvableTemplate.addData("finished", finished);
            
            return resolvableTemplate;
        }
    }),
    
    IS_COMPLETE(new RouteLocateValueAccessor() {
        public Object getValue(RouteLocateResult routeLocateResult) {
            
            ResolvableTemplate resolvableTemplate = new ResolvableTemplate("yukon.common.device.routeLocate.home.recentRouteLocateResults.IS_COMPLETE");
            resolvableTemplate.addData("isComplete", routeLocateResult.isComplete());
            
            return resolvableTemplate;
        }
    }),
    
    ;
    
    private RouteLocateValueAccessor routeLocateValueAccessor;
    
    RouteLocateTypeEnum(RouteLocateValueAccessor routeLocateValueAccessor) {

        this.routeLocateValueAccessor = routeLocateValueAccessor;
    }
    
    public Object getValue(RouteLocateResult routeLocateResult) {
        return this.routeLocateValueAccessor.getValue(routeLocateResult);
    }
}
