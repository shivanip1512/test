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
            
            if (routeLocateResult.isComplete() || routeLocateResult.isCanceled())
                resolvableTemplate.addData("finished", true);
            else
                resolvableTemplate.addData("finished", false);
            
            return resolvableTemplate;
        }
    }),
    
    IS_COMPLETE(new ResultAccessor<RouteLocateResult>() {
        public Object getValue(RouteLocateResult routeLocateResult) {
            return routeLocateResult.isComplete();
        }
    }),
    
    IS_CANCELED(new ResultAccessor<RouteLocateResult>() {
        public Object getValue(RouteLocateResult routeLocateResult) {
            return routeLocateResult.isCanceled();
        }
    }),
    
    STATUS_CLASS(new ResultAccessor<RouteLocateResult>() {
        public Object getValue(RouteLocateResult routeLocateResult) {
            
            String className = "";
            if (routeLocateResult.isCanceled()) {
                className = "error";
            } else {
                className = "";
            }
            
            return className;
        }
    }),
    
    STATUS_TEXT(new ResultAccessor<RouteLocateResult>() {
        public Object getValue(RouteLocateResult routeLocateResult) {
            
        	ResolvableTemplate resolvableTemplate = null;
        	
        	if(routeLocateResult.isCanceled()){
        	    resolvableTemplate = new ResolvableTemplate("yukon.web.modules.amr.routeLocateHome.recentRouteLocateResults.IS_CANCELED_TEXT");
        	} else {
            	if (routeLocateResult.isComplete()) {
            		resolvableTemplate = new ResolvableTemplate("yukon.web.modules.amr.routeLocateHome.recentRouteLocateResults.IS_COMPLETE_TEXT");
            	} else {
        	        resolvableTemplate = new ResolvableTemplate("yukon.web.modules.amr.routeLocateHome.recentRouteLocateResults.IS_IN_PROGRESS_TEXT");
        	    }
        	}
        	
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
